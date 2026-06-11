package com.transithub.auth.service;
import com.transithub.auth.model.User;
import com.transithub.auth.model.RefreshToken;
import com.transithub.auth.model.OutboxEvent;
import com.transithub.auth.repository.UserRepository;
import com.transithub.auth.repository.RefreshTokenRepository;
import com.transithub.auth.repository.OutboxEventRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {
    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final OutboxEventRepository outboxRepo;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SecretKey jwtSecret;
    private final long accessExpiry;
    private final long refreshExpiry;

    public AuthService(UserRepository userRepo, RefreshTokenRepository refreshTokenRepo,
                       OutboxEventRepository outboxRepo,
                       @Value("${jwt.secret:TransitHub_JWT_Min32Char_SecretKey_2024}") String secret,
                       @Value("${jwt.access-token-expiry:900}") long accessExpiry,
                       @Value("${jwt.refresh-token-expiry:604800}") long refreshExpiry) {
        this.userRepo = userRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.outboxRepo = outboxRepo;
        this.passwordEncoder = new BCryptPasswordEncoder(12);
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiry = accessExpiry;
        this.refreshExpiry = refreshExpiry;
    }

    public record AuthPayload(String accessToken, String refreshToken, int expiresIn, UserProfile user) {}
    public record UserProfile(UUID id, String email, String phone, String fullName, boolean isActive, String role) {}
    public record LoginInput(String emailOrPhone, String password) {}
    public record RegisterInput(String email, String phone, String fullName, String password) {}

    private String generateAccessToken(User user) {
        return Jwts.builder()
            .subject(user.getId().toString())
            .claim("email", user.getEmail())
            .claim("role", user.getRole())
            .claim("fullName", user.getFullName())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + accessExpiry * 1000))
            .signWith(jwtSecret)
            .compact();
    }

    private String generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            String hash = HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
            refreshTokenRepo.save(new RefreshToken(user.getId(), hash, LocalDateTime.now().plusSeconds(refreshExpiry)));
        } catch (Exception e) { throw new RuntimeException("Failed to hash refresh token", e); }
        return token;
    }

    @Transactional
    public AuthPayload register(String email, String phone, String fullName, String password) {
        if (userRepo.findByEmail(email).isPresent()) throw new RuntimeException("Email already registered");
        if (userRepo.findByPhone(phone).isPresent()) throw new RuntimeException("Phone already registered");
        var user = userRepo.save(new User(email, phone, passwordEncoder.encode(password), fullName));
        var profile = new UserProfile(user.getId(), user.getEmail(), user.getPhone(), user.getFullName(), user.isActive(), user.getRole());
        outboxRepo.save(new OutboxEvent("USER", user.getId(), "USER_REGISTERED", 
            "{\"userId\":\"" + user.getId() + "\",\"email\":\"" + email + "\"}"));
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);
        return new AuthPayload(accessToken, refreshToken, (int) accessExpiry, profile);
    }

    @Transactional
    public AuthPayload login(String emailOrPhone, String password) {
        var userOpt = emailOrPhone.contains("@") ? userRepo.findByEmail(emailOrPhone) : userRepo.findByPhone(emailOrPhone);
        var user = userOpt.orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) throw new RuntimeException("Invalid credentials");
        if (!user.isActive()) throw new RuntimeException("Account is deactivated");
        user.setLastLoginAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepo.save(user);
        var profile = new UserProfile(user.getId(), user.getEmail(), user.getPhone(), user.getFullName(), user.isActive(), user.getRole());
        return new AuthPayload(generateAccessToken(user), generateRefreshToken(user), (int) accessExpiry, profile);
    }

    @Transactional
    public AuthPayload refreshToken(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            String hash = HexFormat.of().formatHex(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
            var rtOpt = refreshTokenRepo.findByTokenHash(hash);
            var rt = rtOpt.orElseThrow(() -> new RuntimeException("Invalid refresh token"));
            if (rt.isRevoked()) throw new RuntimeException("Refresh token revoked");
            if (rt.getExpiresAt().isBefore(LocalDateTime.now())) throw new RuntimeException("Refresh token expired");
            rt.setRevoked(true);
            refreshTokenRepo.save(rt);
            var user = userRepo.findById(rt.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
            var profile = new UserProfile(user.getId(), user.getEmail(), user.getPhone(), user.getFullName(), user.isActive(), user.getRole());
            return new AuthPayload(generateAccessToken(user), generateRefreshToken(user), (int) accessExpiry, profile);
        } catch (RuntimeException e) { throw e; } catch (Exception e) { throw new RuntimeException("Token processing failed", e); }
    }

    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepo.deleteByUserId(userId);
    }

    public UserProfile getUserProfile(UUID userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return new UserProfile(user.getId(), user.getEmail(), user.getPhone(), user.getFullName(), user.isActive(), user.getRole());
    }

    public boolean verifyOtp(String phone, String otp) {
        return "1234".equals(

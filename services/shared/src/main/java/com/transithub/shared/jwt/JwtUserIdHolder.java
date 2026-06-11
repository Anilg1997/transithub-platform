package com.transithub.shared.jwt;

import java.util.UUID;

public final class JwtUserIdHolder {
    private static final ThreadLocal<UUID> HOLDER = new ThreadLocal<>();

    private JwtUserIdHolder() {}

    public static void set(UUID userId) { HOLDER.set(userId); }
    public static UUID get() { return HOLDER.get(); }
    public static void clear() { HOLDER.remove(); }
}

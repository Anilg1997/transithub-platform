package com.transithub.refund.repository;
import com.transithub.refund.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface RefundRepository extends JpaRepository<Refund, UUID> {}

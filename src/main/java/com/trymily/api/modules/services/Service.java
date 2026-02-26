package com.trymily.api.modules.services;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotNull
    @Column(name = "tenant_id", nullable = false, updatable = false)
    private UUID tenantId;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @NotNull
    @Positive
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull
    @Positive
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @NotBlank
    @Builder.Default
    @Column(nullable = false)
    private String status = "ACTIVE";

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;
}

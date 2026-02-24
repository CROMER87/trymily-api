package com.trymily.api.modules.tenants;

import com.trymily.api.modules.tenants.settings.TenantSettings;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.envers.Audited;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Audited
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(columnDefinition = "text")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "logo_url", columnDefinition = "text")
    private String logoUrl;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private TenantSettings settings;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private ZonedDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;
}

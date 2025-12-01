package com.laresencanto.laresencantorestapi.domain.product;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "colors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"products"})
@ToString(exclude = {"products"})
@EntityListeners(AuditingEntityListener.class)
public class Color {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "hex_code", nullable = false, length = 7, unique = true)
    private String hexCode;

    @Column(length = 100)
    private String name;

    @ManyToMany(mappedBy = "colors")
    private Set<Product> products = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}



package com.laresencanto.laresencantorestapi.domain.exchange;

import com.laresencanto.laresencantorestapi.domain.order.Order;
import com.laresencanto.laresencantorestapi.domain.order.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "exchanges")
@EqualsAndHashCode(of = "id")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Exchange {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @OneToMany(mappedBy = "exchange", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ExchangeItem> items; // Lista de itens da troca/devolução

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "status_id", nullable = false)
  private OrderStatus status; // TROCA SOLICITADA, TROCA ACEITA, TROCA CONCLUÍDA, etc.

  @Column(name = "return_to_stock", nullable = false)
  private Boolean returnToStock; // Se os itens devem retornar ao estoque

  @Column(name = "coupon_generated")
  private Boolean couponGenerated; // Se o cupom já foi gerado

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}

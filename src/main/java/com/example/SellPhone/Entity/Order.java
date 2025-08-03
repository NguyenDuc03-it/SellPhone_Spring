package com.example.SellPhone.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

//    @Column(name = "user_id")
//    private Long userId;

    @Column(name = "delivery_adress")
    private String deliveryAdress;

    @Column(name = "order_time")
    private String orderTime;

    @Column(name = "delivery_time_end")
    private String deliveryTimeEnd;

    @Column(name = "order_status")
    private String orderStatus;

    @Column(name = "total_price")
    private Long totalPrice;

    @Column(name = "payment_method")
    private String paymentMethod;

    // Mối quan hệ OneToMany với OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

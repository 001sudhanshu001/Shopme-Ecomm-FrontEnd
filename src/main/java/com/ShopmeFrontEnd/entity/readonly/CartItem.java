package com.ShopmeFrontEnd.entity.readonly;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "cart_items")
@Getter @Setter
@NoArgsConstructor
public class CartItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    public float getShippingCost() {
        return shippingCost;
    }

    @Transient
    public void setShippingCost(float shippingCost) {
        this.shippingCost = shippingCost;
    }

    @Transient
    private float shippingCost;


    @Transient
    public float getSubTotal() {
        return product.getDiscountPrice() * quantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", customer=" + customer.getFullName() +
                ", product=" + product.getName() +
                ", quantity=" + quantity +
                ", shippingCost=" + shippingCost +
                '}';
    }
}

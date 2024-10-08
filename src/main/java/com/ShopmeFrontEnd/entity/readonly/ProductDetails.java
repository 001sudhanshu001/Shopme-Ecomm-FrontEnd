package com.ShopmeFrontEnd.entity.readonly;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Product_details")
@NoArgsConstructor
@AllArgsConstructor
public class  ProductDetails implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    public String name;

    @Column(nullable = false, length = 255)
    public String value;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetails(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }
}

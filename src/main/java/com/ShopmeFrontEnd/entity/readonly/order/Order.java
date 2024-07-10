package com.ShopmeFrontEnd.entity.readonly.order;


import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "first_name", nullable = false, length = 45)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 45)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 15)
    private String phoneNumber;

    @Column(name = "address_line_1", nullable = false, length = 64)
    private String addressLine1;

    @Column(name = "address_line_2", length = 64)
    private String addressLine2;

    @Column(nullable = false, length = 45)
    private String city;

    @Column(nullable = false, length = 45)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 45)
    private String country;

    private Date orderTime;
    private float shippingCost;
    private float productCost;
    private float subtotal;
    private float tax;
    private float total;

    private int deliveryDays;
    private Date deliveryDate;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne // one customer can place one or more order
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // TODO -> Initially Fetch type was lazy and then suddenly the code broke, now after making it Eager it is working
    // Strange behaviour
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER) // one order can be in many details
    private Set<OrderDetail> orderDetails = new HashSet<>();

    // TODO -> Initially Fetch type was lazy and then suddenly the code broke, now after making it Eager it is working
    // Strange behaviour            , fetch = FetchType.EAGER
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderTrack> orderTracks = new ArrayList<>();

    @Transient
    public String getDestination() {
        String destination =  city + ", ";
        if (state != null && !state.isEmpty()) destination += state + ", ";
        destination += country;

        return destination;
    }

    public void copyAddressFromCustomer() {
        this.setFirstName(customer.getFirstName());
        this.setLastName(customer.getLastName());
        this.setPhoneNumber(customer.getPhoneNumber());
        this.setAddressLine1(customer.getAddressLine1());
        this.setAddressLine2(customer.getAddressLine2());
        this.setCity(customer.getCity());
        this.setCountry(customer.getCountry().getName());
        this.setPostalCode(customer.getPostalCode());
        this.setState(customer.getState());
    }

    public void copyShippingAddressFromShipping(Address address) {
        this.setFirstName(address.getFirstName());
        this.setLastName(address.getLastName());
        this.setPhoneNumber(address.getPhoneNumber());
        this.setAddressLine1(address.getAddressLine1());
        this.setAddressLine2(address.getAddressLine2());
        this.setCity(address.getCity());
        this.setCountry(address.getCountry().getName());
        this.setPostalCode(address.getPostalCode());
        this.setState(address.getState());
    }


    @Transient
    public String getShippingAddress() {
        String address = firstName;

        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;

        if (!addressLine1.isEmpty()) address += ", " + addressLine1;

        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if (!city.isEmpty()) address += ", " + city;

        if (state != null && !state.isEmpty()) address += ", " + state;

        address += ", " + country;

        if (!postalCode.isEmpty()) address += ". Postal Code: " + postalCode;
        if (!phoneNumber.isEmpty()) address += ". Phone Number: " + phoneNumber;

        return address;
    }

    @Transient
    public String getRecipientName() {
        String name = firstName;
        if (lastName != null && !lastName.isEmpty()) name += " " + lastName;
        return name;
    }

    @Transient
    public String getRecipientAddress() {
        String address = addressLine1;
        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
        if (!city.isEmpty()) address += ", " + city;

        if (state != null && !state.isEmpty()) address += ", " + state;
        address += ", " + country;
        if (!postalCode.isEmpty()) address += ". " + postalCode;

        return address;
    }

    @Transient
    public boolean isCOD() {
        return paymentMethod.equals(PaymentMethod.COD);
    }

    @Transient
    public boolean isPicked() {
        return hasStatus(OrderStatus.PICKED);
    }

    @Transient
    public boolean isShipping() {
        return hasStatus(OrderStatus.SHIPPING);
    }

    @Transient
    public boolean isDelivered() {
        return hasStatus(OrderStatus.DELIVERED);
    }

    @Transient
    public boolean isReturned() {
        return hasStatus(OrderStatus.RETURNED);
    }

    @Transient
    public boolean isReturnRequested() {
        return hasStatus(OrderStatus.RETURN_REQUESTED);
    }

    @Transient
    public boolean isProcessing() {
        return hasStatus(OrderStatus.PROCESSING);
    }

    public boolean hasStatus(OrderStatus status) {
        for (OrderTrack aTrack : orderTracks) {
            if (aTrack.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    @Transient
    public String getProductNames() {
        String productNames = "";
        productNames = "<ul>";

        for (OrderDetail detail : orderDetails) {
            productNames += "<li>" + detail.getProduct().getShortName() + "</li>";
        }
        productNames += "</ul>";

        return productNames;
    }

    @Override
    public String toString() {
        return this.orderTracks + " ...... " + this.orderDetails;
    }
}

package com.ShopmeFrontEnd;

import com.ShopmeFrontEnd.dao.OrderDetailRepository;
import com.ShopmeFrontEnd.entity.readonly.order.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderDetailRepositoryTest {

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    public void testCountByProductAndCustomerAndOrderStatus() {
        Integer productId = 1;
        Integer customerId = 6;

        Long count = orderDetailRepository.countByProductAndCustomerAndOrderStatus(productId, customerId, OrderStatus.DELIVERED);
        assertThat(count).isGreaterThan(0);
    }

}

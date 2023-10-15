package com.ShopmeFrontEnd;

import com.ShopmeFrontEnd.dao.CartItemRepo;
import com.ShopmeFrontEnd.entity.readonly.CartItem;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CartItemRepoTest {
    @Autowired
    private CartItemRepo repo;
    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void  testSaveItem(){
        Integer customerId = 9;
        Integer productId = 23;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem newItem = new CartItem();
        newItem.setCustomer(customer);
        newItem.setProduct(product);
        newItem.setQuantity(2);

        CartItem savedItem = repo.save(newItem);
        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void  testSave2Item(){
        Integer customerId = 9;
        Integer productId = 14;

        Customer customer = entityManager.find(Customer.class, customerId);
        Product product = entityManager.find(Product.class, productId);

        CartItem item1 = new CartItem();
        item1.setCustomer(customer);
        item1.setProduct(product);
        item1.setQuantity(2);

        CartItem item2 = new CartItem();
        item2.setCustomer(customer);
        item2.setProduct(product);
        item2.setQuantity(1);

        CartItem savedItem = repo.save(item1);
        assertThat(savedItem.getId()).isGreaterThan(0);
    }

    @Test
    public void testFindByCustomerAndProduct() {
        Integer customerId = 9;
        Integer productId = 14;

        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNotNull();

        System.out.println(item);

    }

    @Test
    public void testUpdateQuantity() {
        Integer customerId = 9;
        Integer productId = 14;
        Integer quantity = 4;

        repo.updateQuantity(quantity, customerId, productId);

        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item.getQuantity()).isEqualTo(4);
    }

    @Test
    public void testDeleteByCustomerAndProduct() {
        Integer customerId = 9;
        Integer productId = 14;

        repo.deleteByCustomerAndProduct(customerId, productId);

        CartItem item = repo.findByCustomerAndProduct(new Customer(customerId), new Product(productId));

        assertThat(item).isNull();

    }


}

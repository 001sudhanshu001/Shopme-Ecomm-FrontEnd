package com.ShopmeFrontEnd;

import com.ShopmeFrontEnd.dao.ProductRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepoTest {
    @Autowired
    ProductRepoFrontEnd repo;

    @Test
    public void testFindByAlias() {
        String alias = "Aus";
        Product product = repo.findByAlias(alias);

        assertThat(product).isNotNull();
    }
}

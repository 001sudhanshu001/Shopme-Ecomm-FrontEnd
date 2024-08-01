package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.ExceptionHandler.ProductNotFoundException;
import com.ShopmeFrontEnd.dao.ProductRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Product;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductServiceFrontEnd {
    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULT_PER_PAGE = 10;

    private final ProductRepoFrontEnd productRepo;

    public Page<Product> listProductByCategory(int pageNum, Integer categoryId){
        String categoryIdMatch = "-" + categoryId + "-";

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);

        return productRepo.listProductByCategory(categoryId, categoryIdMatch, pageable);
    }

    // TODO -> Causes LAZY Initialization Exception
//    @Cacheable(value = "product", key = "#alias")
    public Product getProductByAlias(String alias) {
        return productRepo.findByAlias(alias);
    }

    public Product getProduct(Integer id) throws ProductNotFoundException {
        return productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find any product with ID " + id));
    }


    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
        return productRepo.search(keyword, pageable);
    }
}

package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.ExceptionHandler.ProductNotFoundException;
import com.ShopmeFrontEnd.Util.AmazonS3Util;
import com.ShopmeFrontEnd.dao.ProductRepoFrontEnd;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.ProductImage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceFrontEnd {
    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int SEARCH_RESULT_PER_PAGE = 10;

    private final ProductRepoFrontEnd productRepo;

    private final AmazonS3Util amazonS3Util;

    public Page<Product> listProductByCategory(int pageNum, Integer categoryId){
        String categoryIdMatch = "-" + categoryId + "-";

        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);

        Page<Product> productPage = productRepo.listProductByCategory(categoryId, categoryIdMatch, pageable);

        addPresignedURL(productPage);
        return productPage;
    }

    // TODO -> Causes LAZY Initialization Exception
//    @Cacheable(value = "product", key = "#alias")
    public Product getProductByAlias(String alias) {
        Product product = productRepo.findByAlias(alias);
        addPresignedURL(product);
        return product;
    }

    public Product getProduct(Integer id) throws ProductNotFoundException {
        return productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Could not find any product with ID " + id));
    }

    public Page<Product> search(String keyword, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);
        Page<Product> productPage = productRepo.search(keyword, pageable);
        addPresignedURL(productPage);
        return productPage;
    }

    public void addPresignedURL(Product product) {
        product.setPreSignedURL(amazonS3Util.generatePreSignedUrl("product-images/"
                + product.getId() + "/" + product.getMainImage()));

        // TODO : Create Static Field in a separate class for Object keys
        for (ProductImage productImage : product.getImages()) {
            productImage.setPreSignedURL(amazonS3Util.generatePreSignedUrl("product-images/"
                    + product.getId() + "/extras/" + productImage.getName()));
        }
    }

    private void addPresignedURL(Page<Product> page) {
        List<Product> content = page.getContent();

        for(Product product : content) {
            product.setPreSignedURL(amazonS3Util.generatePreSignedUrl("product-images/"
                    + product.getId() + "/" + product.getMainImage()));
        }
        // No need to generate URL for extra images as in list only main image will be displayed
//
//        // TODO : Create Static Field in a separate class for Object keys
//        for (ProductImage productImage : product.getImages()) {
//            productImage.setPreSignedURL(amazonS3Util.generatePreSignedUrl("product-images/"
//                    + product.getId() + "/extras/" + productImage.getName()));
//        }
    }
}

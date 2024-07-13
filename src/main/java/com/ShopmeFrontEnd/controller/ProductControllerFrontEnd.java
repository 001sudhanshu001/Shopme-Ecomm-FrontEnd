package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.entity.readonly.Category;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.Review;
import com.ShopmeFrontEnd.service.CategoryServiceFrontEnd;
import com.ShopmeFrontEnd.service.ProductServiceFrontEnd;
import com.ShopmeFrontEnd.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductControllerFrontEnd {
    private final CategoryServiceFrontEnd categoryService;
    private final ProductServiceFrontEnd productService;
    private final ReviewService reviewService;


    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,  Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias,
                               @PathVariable("pageNum") Integer pageNum,Model model) {
        Category category = categoryService.getCategory(alias);

        if(category == null){ // We can also throw exception in getCategory(alias) method and then handle it and show error page
            return "error/404";
        }
        List<Category> listCategoryParents = categoryService.getCategoryParents(category); // For breadcrumb

        Page<Product> pageProducts = productService.listProductByCategory(pageNum, category.getId());
        List<Product> listProducts = pageProducts.getContent();

        long startCount = (long) (pageNum - 1) * ProductServiceFrontEnd.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount +  ProductServiceFrontEnd.PRODUCTS_PER_PAGE - 1;

        if(endCount > pageProducts.getTotalElements()){
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("listProducts", listProducts);

        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("pageTitle",category.getName());
        model.addAttribute("category", category);

        return "product/product_by_category";
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model){
        Product product = productService.getProductByAlias(alias);
        if(product == null){
            return "error/404";
        }

        // For breadcrumb
        List<Category> listCategoryParents = categoryService.getCategoryParents(product.getCategory());

        Page<Review> listReviews = reviewService.list3MostVotedReviewsByProduct(product);

        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("product", product);
        model.addAttribute("pageTitle", product.getShortName());
        model.addAttribute("listReviews", listReviews);
        return "product/product_details";
    }

    @GetMapping("/search")
    public String seachFirstResult(@Param("keyword") String keyword, Model model){
        return searchByPage(keyword, 1, model);
    }

    @GetMapping("/search/page/{pageNum}")
    public String searchByPage(@Param("keyword") String keyword,
                         @PathVariable("pageNum") int pageNum, Model model){

        Page<Product> pageProducts = productService.search(keyword, pageNum);
        List<Product> listProducts = pageProducts.getContent();

        long startCount = (long) (pageNum - 1) * ProductServiceFrontEnd.SEARCH_RESULT_PER_PAGE + 1;
        long endCount = startCount +  ProductServiceFrontEnd.SEARCH_RESULT_PER_PAGE - 1;

        if(endCount > pageProducts.getTotalElements()){
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("pageTitle", keyword + " -Search Result");

        model.addAttribute("listProducts", listProducts);
        model.addAttribute("keyword", keyword);
        return "product/search_result";
    }
}

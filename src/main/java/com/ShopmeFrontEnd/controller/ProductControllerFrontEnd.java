package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.entity.readonly.Category;
import com.ShopmeFrontEnd.service.CategoryServiceFrontEnd;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductControllerFrontEnd {
    private final CategoryServiceFrontEnd categoryService;

    @GetMapping("/c/{category_alias}")
    public String viewCategory(@PathVariable("category_alias") String alias, Model model) {
        Category category = categoryService.getCategory(alias);
        if(category == null){
            return "error/404";
        }

        List<Category> listCategoryParents = categoryService.getCategoryParents(category);

        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("pageTitle",category.getName());
        return "product/product_by_category";
    }

}

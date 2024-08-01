package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.Util.AmazonS3Util;
import com.ShopmeFrontEnd.entity.readonly.Category;
import com.ShopmeFrontEnd.service.CategoryServiceFrontEnd;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainControllerFrontEnd {

    private final CategoryServiceFrontEnd categoryService;
    private final AmazonS3Util amazonS3Util;

    @GetMapping("")
    public String viewHomePage(Model model){
        List<Category> listCategories = categoryService.listNoChildrenCategory();
        addPreResignedURL(listCategories);
        model.addAttribute("listCategories",listCategories);

        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";
    }

    private void addPreResignedURL(List<Category> categoryPage) {
        for(Category category : categoryPage) {
            if(category.getImage() != null && !category.getImage().isEmpty()) {
                String resignedUrl =
                        amazonS3Util.generatePreSignedUrl("category-images/"
                                + category.getId() + "/" + category.getImage());
                category.setPreSignedURL(resignedUrl);
            }
        }
    }
}

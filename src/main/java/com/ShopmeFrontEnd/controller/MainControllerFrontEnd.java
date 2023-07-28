package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.entity.readonly.Category;
import com.ShopmeFrontEnd.service.CategoryServiceFrontEnd;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainControllerFrontEnd {

    private final CategoryServiceFrontEnd categoryService;
    @GetMapping("")
    public String viewHomePage(Model model){
        List<Category> listCategories = categoryService.listNoChildrenCategory();
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
}

package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.entity.readonly.Category;
import com.ShopmeFrontEnd.service.CategoryServiceFrontEnd;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {

    private final CategoryServiceFrontEnd categoryService;
    @GetMapping("")
    public String viewHomePage(Model model){
        List<Category> listCategories = categoryService.listNoChildrenCategory();
        model.addAttribute("listCategories",listCategories);

        return "index";
    }
}

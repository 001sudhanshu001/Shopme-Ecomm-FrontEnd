package com.ShopmeFrontEnd.controller;

import com.ShopmeFrontEnd.ExceptionHandler.CustomerNotFoundException;
import com.ShopmeFrontEnd.ExceptionHandler.ProductNotFoundException;
import com.ShopmeFrontEnd.ExceptionHandler.ReviewNotFoundException;
import com.ShopmeFrontEnd.Util.GetEmailOfAuthenticatedCustomer;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.Review;
import com.ShopmeFrontEnd.service.CustomerServiceFrontEnd;
import com.ShopmeFrontEnd.service.ProductServiceFrontEnd;
import com.ShopmeFrontEnd.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private static final String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";

    private final ReviewService reviewService;

    private final CustomerServiceFrontEnd customerService;

    private final ProductServiceFrontEnd productService;

    @GetMapping("/reviews")
    public String listFirstPage(Model model) {
        return defaultRedirectURL;
    }

    @GetMapping("/reviews/page/{pageNum}")
    public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
                                              @PathVariable(name = "pageNum") int pageNum,
                                              String keyword, String sortField, String sortDir)  {

        Customer customer = getAuthenticatedCustomer(request);
        System.out.println("Customer is " + customer);

        Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("moduleURL", "/reviews");

        model.addAttribute("listReviews", listReviews);

        long startCount = (long) (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;

        model.addAttribute("startCount", startCount);

        long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("endCount", endCount);
        return "reviews/reviews_customer";
    }

    @GetMapping("/reviews/detail/{id}")
    public String viewReview(@PathVariable("id") Integer id, Model model,
                             RedirectAttributes ra, HttpServletRequest request) throws CustomerNotFoundException {

        Customer customer = getAuthenticatedCustomer(request);
        try {

            Review review = reviewService.getByCustomerAndId(customer, id);

            model.addAttribute("review", review);

            return "reviews/review_detail_modal";

        } catch (ReviewNotFoundException ex) {
            ra.addFlashAttribute("messageError", ex.getMessage());

            return defaultRedirectURL;
        }
    }

    @GetMapping("/ratings/{productAlias}")
    public String listByProductFirstPage(@PathVariable(name = "productAlias") String productAlias, Model model,
                                         HttpServletRequest request) {

        return listByProductByPage(model, productAlias, 1, "reviewTime", "desc", request);
    }

    @GetMapping("/ratings/{productAlias}/page/{pageNum}")
    public String listByProductByPage(Model model,
                                      @PathVariable(name = "productAlias") String productAlias,
                                      @PathVariable(name = "pageNum") int pageNum,
                                      String sortField, String sortDir, HttpServletRequest request) {

        Product product = productService.getProductByAlias(productAlias);
        if(product == null) {
            return "error/404";
        }

        Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
        List<Review> listReviews = page.getContent();

        long startCount = (long) (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;

        model.addAttribute("startCount", startCount);

        long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;

        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("listReviews", listReviews);
        model.addAttribute("product", product);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);

        return "reviews/reviews_product";
    }



    private Customer getAuthenticatedCustomer(HttpServletRequest request) {
        String email = GetEmailOfAuthenticatedCustomer.getEmail(request);
        // Since only Logged-in customer can call "/cart" url(we configure in SecurityConfig ) So the email is always present

        return customerService.getCustomerByEmail(email);
    }
}


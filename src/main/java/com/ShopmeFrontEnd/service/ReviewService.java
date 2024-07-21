package com.ShopmeFrontEnd.service;

import com.ShopmeFrontEnd.ExceptionHandler.ReviewNotFoundException;
import com.ShopmeFrontEnd.dao.OrderDetailRepository;
import com.ShopmeFrontEnd.dao.ProductRepoFrontEnd;
import com.ShopmeFrontEnd.dao.ReviewRepository;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.Product;
import com.ShopmeFrontEnd.entity.readonly.Review;
import com.ShopmeFrontEnd.entity.readonly.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    public static final int REVIEWS_PER_PAGE = 5;

    private final ReviewRepository reviewRepo;

    private final OrderDetailRepository orderDetailRepository;

    private final ProductRepoFrontEnd productRepository;


    public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, String sortField,
                                             String sortDir) {

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        if (keyword != null) {
            return reviewRepo.findByCustomer(customer.getId(), keyword, pageable);
        }

        return reviewRepo.findByCustomer(customer.getId(), pageable);
    }

    public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
        Review review = reviewRepo.findByCustomerAndId(customer.getId(), reviewId);
        if (review == null)
            throw new ReviewNotFoundException("Customer doesn not have any reviews with ID " + reviewId);

        return review;
    }

    // TODO : Implement Redis
    @Cacheable(value = "reviews", key = "#product.getId()")
    public Page<Review> list3MostVotedReviewsByProduct(Product product) {
        System.out.println("method invoked ");
        Sort sort = Sort.by("votes").descending();
        Pageable pageable = PageRequest.of(0, 3, sort);

        return reviewRepo.findByProduct(product, pageable);
    }

    public Page<Review> listByProduct(Product product, int pageNum, String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);

        return reviewRepo.findByProduct(product, pageable);
    }

    public Review save(Review review) {
        review.setReviewTime(new Date());
        Review savedReview = reviewRepo.save(review);

        Integer productId = savedReview.getProduct().getId();
        productRepository.updateReviewCountAndAverageRating(productId);
        return savedReview;
    }


    public boolean hasCustomerAlreadyReviewedProduct(Customer customer, Integer productId) {
        Long count = reviewRepo.countByCustomerAndProduct(customer.getId(), productId);
        return count > 0;
    }

    public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
        // A customer can only review the product if he had bought it and has been delivered to him
        Long count = orderDetailRepository
                .countByProductAndCustomerAndOrderStatus(productId, customer.getId(), OrderStatus.DELIVERED);

        return count > 0;
    }

}

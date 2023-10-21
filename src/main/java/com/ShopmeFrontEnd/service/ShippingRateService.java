package com.ShopmeFrontEnd.service;


import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import com.ShopmeFrontEnd.dao.CountryRepoFrontEnd;
import com.ShopmeFrontEnd.dao.ShippingRateRepo;
import com.ShopmeFrontEnd.entity.readonly.Address;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.Customer;
import com.ShopmeFrontEnd.entity.readonly.ShippingRate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class ShippingRateService {
    public static final int RATES_PER_PAGE = 10;
    private static final int DIM_DIVISOR = 139;

    private final ShippingRateRepo shipRepo;

    private final CountryRepoFrontEnd countryRepo;
//
//    private ProductRepository productRepo;


//    public void listByPage(int pageNum, PagingAndSortingHelper helper) {
//        // TODO Auto-generated method stub
//        helper.listEntities(pageNum, RATES_PER_PAGE, shipRepo);
//    }


    public List<Country> listAllCountries() {
        // TODO Auto-generated method stub
        return countryRepo.findAllByOrderByNameAsc();
    }

    public ShippingRate getShippingRateForCustomer(Customer customer) {
        String state = customer.getState();
        if(state == null || state.isEmpty()) {
            state = customer.getCity();
        }
        return shipRepo.findByCountryAndState(customer.getCountry(), state);
    }
    public ShippingRate getShippingRateForAddress(Address address) {
        String state = address.getState();
        if(state == null || state.isEmpty()) {
            state = address.getCity();
        }

        return shipRepo.findByCountryAndState(address.getCountry(), state);
    }


//    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {
//        // TODO Auto-generated method stub
//        ShippingRate rateInDB = shipRepo.findByCountryAndState(
//                rateInForm.getCountry().getId(), rateInForm.getState());
//        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
//        boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);
//
//        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
//            throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
//                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
//        }
//        shipRepo.save(rateInForm);
//    }

//    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
//        // TODO Auto-generated method stub
//        try {
//            return shipRepo.findById(id).get();
//        } catch (NoSuchElementException ex) {
//            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
//        }
//    }

//    public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
//        // TODO Auto-generated method stub
//        Long count = shipRepo.countById(id);
//        if (count == null || count == 0) {
//            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
//        }
//
//        shipRepo.updateCODSupport(id, codSupported);
//    }

//    public void delete(Integer id) throws ShippingRateNotFoundException {
//        // TODO Auto-generated method stub
//        Long count = shipRepo.countById(id);
//        if (count == null || count == 0) {
//            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
//
//        }
//        shipRepo.deleteById(id);
//    }

//    public float calculateShippingCost(Integer productId, Integer countryId, String state)
//            throws ShippingRateNotFoundException {
//        ShippingRate shippingRate = shipRepo.findByCountryAndState(countryId, state);
//
//        if (shippingRate == null) {
//            throw new ShippingRateNotFoundException("No shipping rate found for the given "
//                    + "destination. You have to enter shipping cost manually.");
//        }
//
//        Product product = productRepo.findById(productId).get();
//
//        float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
//        float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
//
//        return finalWeight * shippingRate.getRate();
//    }

}

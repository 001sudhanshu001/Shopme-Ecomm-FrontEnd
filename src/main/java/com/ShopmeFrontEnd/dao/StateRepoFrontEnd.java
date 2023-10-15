package com.ShopmeFrontEnd.dao;


import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepoFrontEnd extends JpaRepository<State, Integer> {
    List<State> findByCountryOrderByNameAsc(Country country);
}

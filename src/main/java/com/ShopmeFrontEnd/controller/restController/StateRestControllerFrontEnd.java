package com.ShopmeFrontEnd.controller.restController;

import com.ShopmeFrontEnd.dao.StateRepoFrontEnd;
import com.ShopmeFrontEnd.dto.StateDto;
import com.ShopmeFrontEnd.entity.readonly.Country;
import com.ShopmeFrontEnd.entity.readonly.State;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StateRestControllerFrontEnd {
    private final StateRepoFrontEnd stateRepo;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDto> listByCountry(@PathVariable("id") Integer countryId){
        List<State> listStates = stateRepo.findByCountryOrderByNameAsc(new Country(countryId));
        List<StateDto> result = new ArrayList<>();

        for(State state : listStates){
            result.add(new StateDto(state.getId(), state.getName()));
        }
        return result;
    }

    @PostMapping("/states/save")
    public String save(@RequestBody State state){
        State savedState = stateRepo.save(state);

        return String.valueOf(savedState.getId());
    }

//    @DeleteMapping("/sates/delete/{id}")
//    public void delete(@PathVariable("id") Integer id) {
//        stateRepo.deleteById(id);
//    }

}

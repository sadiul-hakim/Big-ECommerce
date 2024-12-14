package org.shopme.admin.country;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.admin.state.StateRepository;
import org.shopme.common.entity.Country;
import org.shopme.common.entity.State;
import org.shopme.common.exception.NotFoundException;
import org.shopme.common.util.JpaResult;
import org.shopme.common.util.JpaResultType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository repository;
    private final StateRepository stateRepository;

    public JpaResult save(String name, String code) {
        Optional<Country> existingCountry = repository.findByNameAndCode(name, code);
        if (existingCountry.isPresent()) {
            return new JpaResult(JpaResultType.NOT_UNIQUE, "Country " + name + " already exists!");
        }

        repository.save(new Country(0, name, code));
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully saved country " + name);
    }

    public JpaResult delete(String name) {

        List<State> allState = findAllState(name);
        if (!allState.isEmpty()) {
            log.warn("{} States are associated with country {}", allState.size(), name);
            return new JpaResult(JpaResultType.FAILED, allState.size() + " States are associated with country " + name);
        }

        Country country = findByName(name);
        repository.delete(country);
        return new JpaResult(JpaResultType.SUCCESSFUL, "Successfully deleted country " + name);
    }

    public Country findById(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Country not found with id " + id));
    }

    public Country findByName(String name) {
        return repository.findByName(name).orElseThrow(() -> new NotFoundException("Country not found with id " + name));
    }

    public List<State> findAllState(String name) {
        return stateRepository.findAllByCountryOrderByNameAsc(name);
    }

    public List<Country> findAll() {
        return repository.findAll();
    }
}

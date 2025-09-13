package org.shopme.site.country;

import lombok.RequiredArgsConstructor;
import org.shopme.common.entity.Country;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;

    public List<Country> findAll() {
        return countryRepository.findAll();
    }
}

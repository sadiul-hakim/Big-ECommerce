package org.shopme.admin.country;

import org.shopme.common.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    Optional<Country> findByNameAndCode(String name, String code);
    Optional<Country> findByName(String name);

    Optional<Country> findByCode(String code);

    List<Country> findAllByNameContainingOrCodeContaining(String name, String code);
}

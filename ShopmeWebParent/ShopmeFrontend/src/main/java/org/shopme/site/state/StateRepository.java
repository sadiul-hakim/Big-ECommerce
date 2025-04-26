package org.shopme.site.state;

import org.shopme.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Integer> {
    Optional<State> findByName(String name);

    List<State> findAllByNameContaining(String name);

    List<State> findAllByCountryOrderByNameAsc(String country);
}

package org.shopme.admin.shippingRate;

import org.shopme.common.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    Page<ShippingRate> findAllByCountryContainingIgnoreCaseAndStateContainingIgnoreCase(String country, String state, Pageable pageable);
    Optional<ShippingRate> findByCountryAndState(String country, String state);
}

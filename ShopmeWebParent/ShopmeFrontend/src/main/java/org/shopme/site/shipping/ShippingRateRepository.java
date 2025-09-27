package org.shopme.site.shipping;

import org.shopme.common.entity.ShippingRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {

    Optional<ShippingRate> findByCountryAndState(String country, String state);
}

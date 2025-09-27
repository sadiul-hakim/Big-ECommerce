package org.shopme.site.shipping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shopme.common.entity.Address;
import org.shopme.common.entity.ShippingRate;
import org.shopme.site.address.AddressService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShippingRateService {

    private final AddressService addressService;
    private final ShippingRateRepository repository;

    public boolean shippingAvailable() {
        Address address = addressService.currentCustomerActiveAddress();
        if (address == null) {
            return false;
        }

        Optional<ShippingRate> shipping = repository.findByCountryAndState(address.getCountry().getName(),
                address.getState().getName());
        return shipping.isPresent();
    }
}

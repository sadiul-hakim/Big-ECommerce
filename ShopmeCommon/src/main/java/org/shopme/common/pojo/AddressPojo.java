package org.shopme.common.pojo;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.shopme.common.entity.Country;
import org.shopme.common.entity.State;

@Data
public class AddressPojo {

    private long id;

    @NotNull
    @NotEmpty
    @Size(min = 11, max = 15)
    private String phoneNumber;

    @NotNull
    @NotEmpty
    @Size(min = 11, max = 15)
    private String alternativePhoneNumber;

    @NotNull
    @NotEmpty
    @Size(max = 150)
    private String address;

    @NotNull(message = "Must be selected")
    private Country country;

    @NotNull(message = "Must be selected")
    private State state;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    private String postalCode;
    private boolean selected;
}

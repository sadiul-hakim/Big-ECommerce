package org.shopme.common.pojo;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.shopme.common.entity.Country;
import org.shopme.common.entity.State;

@Data
public class CustomerRegistrationPojo {

    @NotNull
    @NotEmpty
    @Size(min = 2, max = 45)
    private String firstname;

    @NotNull
    @NotEmpty
    @Size(min = 2, max = 45)
    private String lastname;

    @Email
    private String email;

    @NotNull
    @NotEmpty
    @Size(min = 11, max = 15)
    private String phoneNumber;

    @NotNull
    @NotBlank
    @NotEmpty
    @Size(min = 8, max = 100)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "Password must be at least 8 characters long and include: \" +\r\n"
            + "              \"1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character (@$!%*?&).")
    private String password;

    @NotNull
    private Country country;

    @NotNull
    private State state;

    @NotNull
    @Size(max = 45)
    private String city;

    @NotNull
    @NotEmpty
    @Size(max = 10)
    private String postalCode;

    @NotNull
    @NotEmpty
    @Size(max = 150)
    private String address;
}

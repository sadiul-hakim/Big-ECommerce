package org.shopme.common.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Email
	@Column(length = 128, nullable = false, unique = true)
	private String email;

	@NotNull
	@NotBlank
	@NotEmpty
	@Size(min = 8, max = 100)
	@Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}$", message = "Password must be at least 8 characters long and include: \" +\r\n"
			+ "              \"1 uppercase letter, 1 lowercase letter, 1 number, and 1 special character (@$!%*?&).")
	@Column(length = 100, nullable = false)
	private String password;

	@NotNull
	@NotEmpty
	@Size(min = 2, max = 45)
	@Column(name = "first_name", length = 45, nullable = false)
	private String firstname;

	@NotNull
	@NotEmpty
	@Size(min = 2, max = 45)
	@Column(name = "last_name", length = 45, nullable = false)
	private String lastname;
	@Column(length = 64)
	private String photo;
	private boolean enabled;

	@Temporal(TemporalType.TIMESTAMP)
	private Timestamp joined = new Timestamp(System.currentTimeMillis());

	@NotNull
	@NotEmpty
	@Size(min = 11, max = 15)
	@Column(length = 15, nullable = false, unique = true)
	private String phoneNumber;

	@NotNull
	@NotEmpty
	@Size(max = 84)
	@Column(length = 84, nullable = false)
	private String address;

	@NotNull
	@ManyToOne
	private Country country;

	@NotNull
	@ManyToOne
	private State state;

	@NotNull
	@NotEmpty
	@Size(max = 10)
	@Column(length = 10, nullable = false)
	private String postalCode;

	@Column(length = 64)
	private String verificationCode;

	public Customer(String email, String password, String firstname, String lastname, String photo, boolean enabled,
					String phoneNumber, String address, Country country, State state, String postalCode) {
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.photo = photo;
		this.enabled = enabled;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.country = country;
		this.state = state;
		this.postalCode = postalCode;
	}
}

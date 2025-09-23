package org.shopme.common.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

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

	public Customer(String email, String password, String firstname, String lastname, boolean enabled) {
		this.email = email;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.enabled = enabled;
	}


	public String getFullName(){
		return getFirstname() + " " + getLastname();
	}
}

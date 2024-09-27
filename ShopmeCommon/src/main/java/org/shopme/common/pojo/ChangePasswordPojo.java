package org.shopme.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordPojo {
	private String currentPassword;
	private String newPassword;
	private String confirmPassword;	
}

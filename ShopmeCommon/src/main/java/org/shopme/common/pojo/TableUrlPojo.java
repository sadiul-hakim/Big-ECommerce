package org.shopme.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TableUrlPojo {
	private String searchUrl;
	private String rootUrl;
	private String csvUrl;
	private String createPageUrl;
}

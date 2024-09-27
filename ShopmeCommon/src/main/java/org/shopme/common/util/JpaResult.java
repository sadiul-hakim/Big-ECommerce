package org.shopme.common.util;

public record JpaResult(
			JpaResultType type,
			String message
		) {

}

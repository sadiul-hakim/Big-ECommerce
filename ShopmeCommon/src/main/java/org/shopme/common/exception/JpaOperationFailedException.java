package org.shopme.common.exception;

public class JpaOperationFailedException extends RuntimeException {
	private static final long serialVersionUID = 176675665643657346L;

	public JpaOperationFailedException(String msg) {
		super(msg);
	}
}

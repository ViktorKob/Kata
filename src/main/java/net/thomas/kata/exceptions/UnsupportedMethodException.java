package net.thomas.kata.exceptions;

public class UnsupportedMethodException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnsupportedMethodException(String message) {
		super(message);
	}
}
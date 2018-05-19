package com.brunomb.processadorplanilhas.exception;

public class FileStorageException extends RuntimeException {
	private static final long serialVersionUID = -8214068896014552964L;

	public FileStorageException(String message) {
		super(message);
	}

	public FileStorageException(String message, Throwable cause) {
		super(message, cause);
	}
}
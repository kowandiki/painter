package com.example.fileio;

public class InvalidFileNumberException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidFileNumberException(String errorMessage)
	{
		super(errorMessage);
	}
}
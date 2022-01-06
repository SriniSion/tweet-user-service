package com.tweetapp.exception;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ApiError {

	private int id;
	private String errorMessage;
	private String errorCode;
	@JsonInclude(Include.NON_NULL)
	private List<String> errors;
	private String timeStamp;
	private String exceptionMessage;

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	public ApiError(int id, String errorMessage, String errorCode, List<String> errors, String timeStamp) {
		super();
		this.id = id;
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.errors = errors;
		this.timeStamp = timeStamp;
	}

	public ApiError(int id, String errorMessage, String errorCode, String timeStamp, String exceptionMessage) {
		super();
		this.id = id;
		this.errorMessage = errorMessage;
		this.errorCode = errorCode;
		this.timeStamp = timeStamp;
		this.exceptionMessage = exceptionMessage;
	}
}

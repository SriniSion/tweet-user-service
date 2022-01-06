package com.tweetapp.exception;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.security.core.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private boolean success = false;
	private Integer httpStatus;
	private String message = "";
	private Integer errorId;
	private String timestamp;

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public Integer getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(Integer httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getErrorId() {
		return errorId;
	}

	public void setErrorId(Integer errorId) {
		this.errorId = errorId;
	}

	@Autowired
	MessageSourceUtil messageSourceUtil;

	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<Object> handleApplicationException(WebRequest request, Exception ex) {
		BaseException e = (BaseException) ex;
		logger.error("ApplicationException Occured:: URL= " + request.getDescription(true));
		logger.error("ApplicationException Occured:: " + ex);
		return getCustomExceptionResponse(request, e);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Object> handleBusinessException(WebRequest request, BusinessException ex) {
		BaseException e = (BaseException) ex;
		logger.error("BusinessException Occured:: URL= " + request.getDescription(true));
		logger.error("BusinessException Occured:: " + ex);
		logger.error("BusinessException Occured:: " + e.getExceptionMessage());
		return getCustomExceptionResponse(request, e);
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Object> handleRuntimeException(WebRequest request, RuntimeException re) {
		BaseException e = new BaseException(HttpStatus.INTERNAL_SERVER_ERROR,
				ExceptionConstants.RUNTIME_EXCEPTION_ERROR_CODE, re.getMessage());
		logger.error("RuntimeException Occured:: URL= " + request.getDescription(true));
		logger.error("RuntimeException Occured:: ", re);
		logger.error("RuntimeException Occured:: " + re.getMessage());
		return getCustomExceptionResponse(request, e);
	}
	
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<Object> handleNullPointerException(WebRequest request, RuntimeException re) {
		BaseException e = new BaseException(HttpStatus.INTERNAL_SERVER_ERROR,
				ExceptionConstants.NULL_POINTER_EXCEPTION_ERROR_CODE, re.getMessage());
		logger.error("RuntimeException Occured:: URL= " + request.getDescription(true));
		logger.error("RuntimeException Occured:: ", re);
		logger.error("RuntimeException Occured:: " + re.getMessage());
		return getCustomExceptionResponse(request, e);
	}
	
	@ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Object> unAuthorizedException(WebRequest request, RuntimeException re) {
		BaseException e = new BaseException(HttpStatus.INTERNAL_SERVER_ERROR,
				ExceptionConstants.UNAUTHORIZED, re.getMessage());
		logger.error("RuntimeException Occured:: URL= " + request.getDescription(true));
		logger.error("RuntimeException Occured:: ", re);
		logger.error("RuntimeException Occured:: " + re.getMessage());
		return getCustomExceptionResponse(request, e);
	}


	@SuppressWarnings("unused")
	private ResponseEntity<Object> getCustomExceptionResponse(WebRequest request, BaseException ex) {
		String errorCode = ex.getErrorCode();
		String exceptionMessage = ex.getExceptionMessage();
		String errorModule = ex.getErrorModule();
		String errorMessage = "";
		// Integer id = RandomUtils.nextInt(10000, 50000);
		try {
			errorMessage = messageSourceUtil.getLocalisedText(errorCode, ex.getErrorModule());
		} catch (Exception e) {
			logger.error("GlobalControllerExceptionHandler :: getCustomExceptionResponse() :: " + e.getMessage());
			errorMessage = messageSourceUtil.getLocalisedText(ExceptionConstants.GENERAL_ERROR_CODE,
					ExceptionConstants.GENERAL_MODULE);
			exceptionMessage = "The message for errorCode:" + errorCode + " module:" + errorModule
					+ " is not found in prop file";
		}
		HttpStatus status = ex.getHttpStatus();
		GlobalExceptionHandler response = new GlobalExceptionHandler();
		response.setHttpStatus(status.value());
		response.setMessage(errorMessage);
		response.setSuccess(false);
		response.setErrorId(2);
		response.setTimestamp(ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));
		return new ResponseEntity<Object>(response, status);
	}

}

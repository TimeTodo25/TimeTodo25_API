package com.pape.timetodo.global.exception;

import java.util.Locale;

import org.springframework.context.MessageSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppException extends RuntimeException {
    
    
    private static final long serialVersionUID = 1L;

	private final ExceptionCode errorCode;
	
	private final Object[] paramArray;
	
	private String sourceErrorCode;
	
	private String sourceErrorMessage;

	public AppException(Throwable th, ExceptionCode errorCode, Object... paramArray) {
		super(th != null ? th.getMessage() : errorCode.message(), th);
		this.errorCode = errorCode;
		this.paramArray = paramArray;
		this.sourceErrorCode = errorCode.code();
		this.sourceErrorMessage = errorCode.message();
	}

	public AppException(ExceptionCode errorCode, Object... paramArray) {
		this(null, errorCode, paramArray);
	}

	public AppException(Object... paramArray) {
		this(null, ExceptionCode.INTERNAL_SERVER_ERROR, paramArray);
	}

	public String getCode() {
		return errorCode.code();
	}

    public ExceptionCode getErrorCode(){
        return errorCode;
    }

	public Object[] getParamArray() {
		return paramArray;
	}

	public String getErrorMessage(MessageSource ms) {
		if (ms == null) {
			return ExceptionCode.INTERNAL_SERVER_ERROR.message();
		}
		log.debug("############ getErrorMessage     : {}",ms);
		log.debug("############ getErrorCode        : {}",getCode());
		log.debug("############ getParamArray       : {}",getParamArray());
		log.debug("############ ErrorCode.NOT_REGIST_ERROR_CODE.message() : {}",ExceptionCode.NOT_REGIST_ERROR_CODE.message());
		log.debug("############ Locale.getDefault() : {}",Locale.getDefault());
		return ms.getMessage(getCode(), getParamArray(), ExceptionCode.NOT_REGIST_ERROR_CODE.message(), Locale.getDefault());
	}

	public String getSourceErrorCode() {
		return sourceErrorCode;
	}

	public void setSourceErrorCode(String sourceErrorCode) {
		this.sourceErrorCode = sourceErrorCode;
	}

	public String getSourceErrorMessage() {
		return sourceErrorMessage;
	}

	public void setSourceErrorMessage(String sourceErrorMessage) {
		this.sourceErrorMessage = sourceErrorMessage;
	}

}

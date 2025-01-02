package me.findthepeach.common.response.exception;

import lombok.Getter;
import me.findthepeach.common.response.constant.ReturnCode;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
    private final String traceId;

    public BaseException(ReturnCode returnCode) {
        super(returnCode.getMessage());
        this.code = returnCode.getCode();
        this.message = returnCode.getMessage();
        this.httpStatus = returnCode.getHttpStatus();
        this.traceId = MDC.get("traceId");
    }
}


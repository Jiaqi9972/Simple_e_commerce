package me.findthepeach.common.response.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.findthepeach.common.response.constant.ReturnCode;
import org.slf4j.MDC;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;
    private String traceId;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(ReturnCode.SUCCESS.getCode())
                .message(ReturnCode.SUCCESS.getMessage())
                .data(data)
                .traceId(MDC.get("traceId"))
                .build();
    }

    public static <T> ApiResponse<T> error(ReturnCode returnCode) {
        return error(returnCode.getCode(), returnCode.getMessage());
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .traceId(MDC.get("traceId"))
                .build();
    }
}
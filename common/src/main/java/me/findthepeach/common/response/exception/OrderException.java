package me.findthepeach.common.response.exception;

import me.findthepeach.common.response.constant.ReturnCode;

public class OrderException extends BaseException {
    public OrderException(ReturnCode returnCode) {
        super(returnCode);
    }
}

package me.findthepeach.common.response.exception;

import me.findthepeach.common.response.constant.ReturnCode;

public class ShopException extends BaseException {
    public ShopException(ReturnCode returnCode) {
        super(returnCode);
    }
}

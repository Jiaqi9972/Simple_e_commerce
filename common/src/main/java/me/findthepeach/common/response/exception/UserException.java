package me.findthepeach.common.response.exception;

import me.findthepeach.common.response.constant.ReturnCode;

public class UserException extends BaseException {
    public UserException(ReturnCode returnCode) {
        super(returnCode);
    }
}

package me.findthepeach.common.response.exception;

import me.findthepeach.common.response.constant.ReturnCode;

public class SystemException extends BaseException {
    public SystemException(ReturnCode returnCode) {
        super(returnCode);
    }
}

package me.findthepeach.common.response.exception;

import me.findthepeach.common.response.constant.ReturnCode;

public class InventoryException extends BaseException {
    public InventoryException(ReturnCode returnCode) {
        super(returnCode);
    }
}
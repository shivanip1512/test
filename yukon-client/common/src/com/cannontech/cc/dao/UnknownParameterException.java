package com.cannontech.cc.dao;

import com.cannontech.cc.model.ProgramParameterKey;

public class UnknownParameterException extends Exception {

    public UnknownParameterException(ProgramParameterKey message) {
        super(message.toString());
    }

}

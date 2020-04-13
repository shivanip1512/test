package com.cannontech.web.api.commChannel;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import com.cannontech.common.device.port.TcpSharedPortDetail;
import com.cannontech.common.device.port.TerminalServerPortDetailBase;

@Service
public class TcpSharedPortValidator extends TerminalServerPortValidator <TcpSharedPortDetail> {

    public TcpSharedPortValidator() {
        super(TcpSharedPortDetail.class);
    }
    
    @Override
    protected void doValidation(TerminalServerPortDetailBase<?> target, Errors errors) {
        super.doValidation(target, errors);
    }
}

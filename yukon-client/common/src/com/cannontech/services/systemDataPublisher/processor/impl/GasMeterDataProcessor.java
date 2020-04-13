package com.cannontech.services.systemDataPublisher.processor.impl;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.cannontech.common.pao.PaoType;

@Service
public class GasMeterDataProcessor extends PaoCountDataProcessor {

    @Override
    public Set<PaoType> getSupportedPaoTypes() {
        return PaoType.getGasMeterTypes();
    }

    @Override
    public boolean supportsField(String field) {
        return "gmcount".equals(field);
    }
}

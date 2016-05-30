package com.cannontech.common.pao.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jdom2.Element;
import org.w3c.dom.Node;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.service.PaoSelectionService;

public class MockPaoSelectionServiceImpl implements PaoSelectionService {
    @Override
    public Map<PaoIdentifier, PaoData> selectPaoIdentifiersAndGetData(Node paoCollectionNode,
            Set<OptionalField> responseFields) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PaoSelectionData selectPaoIdentifiersByType(Node paoCollectionNode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T extends YukonPao> Map<T, PaoData> lookupPaoData(Iterable<T> paos, Set<OptionalField> requestedFields) {
        Map<T, PaoData> paoDataByPao = new HashMap<>();
        for (T pao : paos) {
            PaoData paoData = new PaoData(OptionalField.SET_OF_ALL, pao.getPaoIdentifier());
            if (pao instanceof YukonMeter) { // ExporterReportGeneratorServiceImplTest uses these
                YukonMeter meter = (YukonMeter) pao;
                paoData.setName(meter.getName());
                paoData.setEnabled(!meter.isDisabled());
                paoData.setMeterNumber(meter.getMeterNumber());
                // paoData.setCarrierAddress(carrierAddress);
                paoData.setRouteName(meter.getRoute());
                paoData.setAddressOrSerialNumber(meter.getSerialOrAddress());
            }
            paoDataByPao.put(pao, paoData);
        }
        return paoDataByPao;
    }

    @Override
    public void addLookupErrorsNode(PaoSelectionData paoData, Element parent) {
        throw new UnsupportedOperationException();
    }
}

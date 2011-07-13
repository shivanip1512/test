package com.cannontech.yukon.api.amr.endpoint.helper;

import java.util.Map;
import java.util.Set;

import org.jdom.Element;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.database.data.lite.LiteState;
import com.google.common.collect.Maps;

public class ArchivedValuesResponseData {
    private Set<ResponseDescriptor> responseFields;
    private boolean flatten;
    private Map<PaoIdentifier, Element> paoElements = Maps.newHashMap();
    private Map<PaoIdentifier, PaoData> paoDataByPaoIdentifier;

    private Map<Integer, Map<Integer, LiteState>> statesByGroupIdAndRawState = Maps.newHashMap();

    public Set<PaoIdentifier> getPaoIdentifiers() {
        return paoDataByPaoIdentifier.keySet();
    }

    public Set<ResponseDescriptor> getResponseFields() {
        return responseFields;
    }

    public void setResponseFields(Set<ResponseDescriptor> responseFields) {
        this.responseFields = responseFields;
    }

    public boolean isFlatten() {
        return flatten;
    }

    public void setFlatten(boolean flatten) {
        this.flatten = flatten;
    }

    public Element getPaoElement(PaoIdentifier paoIdentifier) {
        return paoElements.get(paoIdentifier);
    }

    public void addPaoElement(PaoIdentifier paoIdentifier, Element element) {
        paoElements.put(paoIdentifier, element);
    }


    public PaoData getPaoData(PaoIdentifier paoIdentifier) {
        return paoDataByPaoIdentifier.get(paoIdentifier);
    }

    public Iterable<PaoData> getAllPaoData() {
        return paoDataByPaoIdentifier.values();
    }

    public void setPaoDataByPaoIdentifier(Map<PaoIdentifier, PaoData> paoDataByPaoIdentifier) {
        this.paoDataByPaoIdentifier = paoDataByPaoIdentifier;
    }

    public Map<Integer, Map<Integer, LiteState>> getStatesByGroupIdAndRawState() {
        return statesByGroupIdAndRawState;
    }
}

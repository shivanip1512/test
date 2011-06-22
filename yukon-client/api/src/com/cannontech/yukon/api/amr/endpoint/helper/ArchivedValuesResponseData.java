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
    private Map<PaoIdentifier, PaoData> paoDataByPaoId;

    private Map<Integer, Map<Integer, LiteState>> statesByGroupIdAndRawState = Maps.newHashMap();

    public Set<PaoIdentifier> getPaoIds() {
        return paoDataByPaoId.keySet();
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

    public Element getPaoElement(PaoIdentifier paoId) {
        return paoElements.get(paoId);
    }

    public void addPaoElement(PaoIdentifier paoId, Element element) {
        paoElements.put(paoId, element);
    }


    public PaoData getPaoData(PaoIdentifier paoId) {
        return paoDataByPaoId.get(paoId);
    }

    public Iterable<PaoData> getAllPaoData() {
        return paoDataByPaoId.values();
    }

    public void setPaoDataByPaoId(Map<PaoIdentifier, PaoData> paoDataByPaoId) {
        this.paoDataByPaoId = paoDataByPaoId;
    }

    public Map<Integer, Map<Integer, LiteState>> getStatesByGroupIdAndRawState() {
        return statesByGroupIdAndRawState;
    }
}

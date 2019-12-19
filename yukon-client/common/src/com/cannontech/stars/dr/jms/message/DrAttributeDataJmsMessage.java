package com.cannontech.stars.dr.jms.message;

import java.util.List;

import com.cannontech.common.pao.definition.model.PaoPointIdentifier;

public class DrAttributeDataJmsMessage extends DrJmsMessage {

    private static final long serialVersionUID = 1L;

    private PaoPointIdentifier paoPointIdentifier;
    private List<DrAttributeData> attributeDataList;

    public PaoPointIdentifier getPaoPointIdentifier() {
        return paoPointIdentifier;
    }

    public void setPaoPointIdentifier(PaoPointIdentifier paoPointIdentifier) {
        this.paoPointIdentifier = paoPointIdentifier;
    }

    public List<DrAttributeData> getAttributeDataList() {
        return attributeDataList;
    }

    public void setAttributeDataList(List<DrAttributeData> attributeDataList) {
        this.attributeDataList = attributeDataList;
    }

}

package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;

import com.cannontech.common.pao.PaoClass;

public class SignalTransmitterPicker extends YukonObjectCriteriaHelper {

    public SignalTransmitterPicker() {
        super();
        addCriteria("paoclass", PaoClass.TRANSMITTER.getDbString(), BooleanClause.Occur.SHOULD);
    }
}

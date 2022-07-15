package com.cannontech.web.search.lucene.criteria;

import org.apache.lucene.search.BooleanClause;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;

public class SignalTransmitterCriteria extends YukonObjectCriteriaHelper {

    public SignalTransmitterCriteria() {
        super();
        
        addCriteria("paoclass", PaoClass.TRANSMITTER.getDbString(), BooleanClause.Occur.SHOULD);
        addCriteria("type", PaoType.REPEATER.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.REPEATER_800.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.REPEATER_801.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.REPEATER_850.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.REPEATER_902.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.REPEATER_921.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.CCU710A.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.CCU711.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.CCU721.getDbString(), BooleanClause.Occur.MUST_NOT);
        addCriteria("type", PaoType.DIGIGATEWAY.getDbString(), BooleanClause.Occur.MUST_NOT);
    }
}

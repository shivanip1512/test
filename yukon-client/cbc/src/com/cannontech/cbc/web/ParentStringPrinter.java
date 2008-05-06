package com.cannontech.cbc.web;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;

public class ParentStringPrinter {
    private static final String ORPH_STRING = "---";
    private CapbankDao capbankDao = YukonSpringHook.getBean("capbankDao",CapbankDao.class);
    private PointDao pointDao;
    private PaoDao paoDao;

    public ParentStringPrinter(final HttpServletRequest request) {
    }

    public String printPAO(final Integer paoId) {
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(paoId);
        int type = lite.getType();
        if(type == CapControlTypes.CAP_CONTROL_AREA || type == CapControlTypes.CAP_CONTROL_SPECIAL_AREA) {
            return ORPH_STRING;
        }else {
            switch(type) {
            case CapControlTypes.CAP_CONTROL_SUBSTATION :
                Integer subParentId = CCSubAreaAssignment.getAreaIDForSubStation(paoId);
                if(subParentId > -1) {
                    return paoDao.getYukonPAOName(subParentId);
                } else {
                    return ORPH_STRING;
                }
                
            case CapControlTypes.CAP_CONTROL_SUBBUS :
                Integer subBusParentId = CCSubstationSubBusList.getSubStationForSubBus(paoId);
                if(subBusParentId > -1) {
                    return paoDao.getYukonPAOName(subBusParentId);
                } else {
                    return ORPH_STRING;
                }
                
            case CapControlTypes.CAP_CONTROL_FEEDER :
                Integer feederParentId = CCFeederSubAssignment.getSubBusIdForFeeder(paoId);
                if(feederParentId > -1) {
                    return paoDao.getYukonPAOName(feederParentId);
                } else {
                    return ORPH_STRING;
                }
                
            case CapControlTypes.CAP_CONTROL_CAPBANK :
                Integer capBankParentId = CCFeederBankList.getFeederIdForCapBank(paoId);
                if(capBankParentId > -1) {
                    return paoDao.getYukonPAOName(capBankParentId);
                } else {
                    return ORPH_STRING;
                }
                
            default :
                Integer cbcParentId = capbankDao.getCapBankIdByCBC(paoId);
                if(cbcParentId > -1) {
                    return paoDao.getYukonPAOName(cbcParentId);
                } else {
                    return ORPH_STRING;
                }
            }
        }
    }

    public String printPoint(Integer id) {
        LitePoint point = pointDao.getLitePoint(id);
        return paoDao.getYukonPAOName(point.getPaobjectID());
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
}

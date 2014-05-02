package com.cannontech.web.capcontrol;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.cannontech.capcontrol.dao.CapbankDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.util.ServletUtil;

public class CapControlUrlService {
    
    public static final String ORPH_STRING = "---";
    private static final String FEEDER_URL = "/capcontrol/tier/feeders";
    private static final String AREA_URL = "/capcontrol/tier/areas";
    private static final String ZONE_DETAIL_URL = "/capcontrol/ivvc/zone/detail";
    
    @Autowired private CapbankDao capbankDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDao paoDao;
    @Autowired private ZoneDao zoneDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao; 
    
    public String printPAO(HttpServletRequest request, final Integer paoId) {
        
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(paoId);
        PaoType paoType = lite.getPaoType();
        
        if (paoDefinitionDao.isTagSupported(paoType, PaoTag.VOLTAGE_REGULATOR)) {
            Zone zone = null;
            
            try {
                zone = zoneDao.getZoneByRegulatorId(paoId);
            } catch (OrphanedRegulatorException e) {
                return ORPH_STRING;
            }
            
            String url = ZONE_DETAIL_URL + "?zoneId=" + zone.getId();

            final StringBuilder sb = new StringBuilder();
            sb.append("<a href=\"");
            sb.append(url);
            sb.append("\">");
            sb.append( HtmlUtils.htmlEscape(zone.getName()));
            sb.append("</a>");
            
            String result = sb.toString();
            
            return result;
            
        } else if (paoType == PaoType.CAP_CONTROL_AREA || paoType == PaoType.CAP_CONTROL_SPECIAL_AREA) {
            
            return ORPH_STRING;
            
        } else if (paoType == PaoType.CAP_CONTROL_SUBSTATION) {
            
            Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(paoId);
            if (areaId > -1) {
                String areaName = paoDao.getYukonPAOName(areaId);
                return buildLink(request, areaName, null, null, AREA_URL);
            } else {
                return ORPH_STRING;
            }
            
        } else if (paoType == PaoType.CAP_CONTROL_SUBBUS) {
            
            Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(paoId);
            if (substationId > -1) {
                Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                if (areaId > -1) {
                    String parentString = paoDao.getYukonPAOName(substationId) + ", " + paoDao.getYukonPAOName(areaId);
                    return buildLink(request, parentString, substationId, areaId, FEEDER_URL);
                } else {
                    return paoDao.getYukonPAOName(substationId);
                }
            } else {
                return ORPH_STRING;
            }
            
        } else if (paoType == PaoType.CAP_CONTROL_FEEDER) {
            
            Integer subBusId = CCFeederSubAssignment.getSubBusIdForFeeder(paoId);
            if (subBusId > -1) {
                Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
                if (substationId > -1) {
                    Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                    if (areaId > -1) {
                        String parentString = paoDao.getYukonPAOName(subBusId) + ", " 
                            + paoDao.getYukonPAOName(substationId) + ", " 
                            + paoDao.getYukonPAOName(areaId);
                        return buildLink(request, parentString, substationId, areaId, FEEDER_URL);
                    } else {
                        return paoDao.getYukonPAOName(subBusId) + ", " + paoDao.getYukonPAOName(substationId);
                    }
                } else {
                    return paoDao.getYukonPAOName(subBusId);
                }
            } else {
                return ORPH_STRING;
            }
            
        } else if (paoType == PaoType.CAPBANK) {
            
            Integer feederId = CCFeederBankList.getFeederIdForCapBank(paoId);
            if (feederId > -1) {
                Integer subBusId = CCFeederSubAssignment.getSubBusIdForFeeder(feederId);
                if (subBusId > -1) {
                    Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
                    if (substationId > -1) {
                        Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                        if (areaId > -1) {
                            String parentString = paoDao.getYukonPAOName(feederId) + ", " 
                                + paoDao.getYukonPAOName(subBusId)+ ", " 
                                + paoDao.getYukonPAOName(substationId) + ", " 
                                + paoDao.getYukonPAOName(areaId);
                            return buildLink(request, parentString, substationId, areaId, FEEDER_URL);
                        } else {
                            return paoDao.getYukonPAOName(feederId) + ", " 
                                + paoDao.getYukonPAOName(subBusId) + ", " 
                                + paoDao.getYukonPAOName(substationId);
                        }
                    } else {
                        return paoDao.getYukonPAOName(feederId) + ", " + paoDao.getYukonPAOName(subBusId);
                    }
                } else {
                    return paoDao.getYukonPAOName(feederId);
                }
            } else {
                return ORPH_STRING;
            }
            
        } else {
            
            PaoIdentifier capbankIdentifier = capbankDao.findCapBankByCbc(paoId);
            if (capbankIdentifier != null) {
                int capbankId = capbankIdentifier.getPaoId();
                Integer feederId = CCFeederBankList.getFeederIdForCapBank(capbankId);
                if (feederId > -1) {
                    Integer subBusId = CCFeederSubAssignment.getSubBusIdForFeeder(feederId);
                    if (subBusId > -1) {
                        Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
                        if (substationId > -1) {
                            Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                            if (areaId > -1) {
                                String parentString = paoDao.getYukonPAOName(capbankId) + ", " 
                                    + paoDao.getYukonPAOName(feederId) + ", " 
                                    + paoDao.getYukonPAOName(subBusId) + ", " 
                                    + paoDao.getYukonPAOName(substationId) + ", " 
                                    + paoDao.getYukonPAOName(areaId);
                                return buildLink(request, parentString, substationId, areaId, FEEDER_URL);
                            } else {
                                return paoDao.getYukonPAOName(capbankId) + ", " 
                                    + paoDao.getYukonPAOName(feederId) + ", " 
                                    + paoDao.getYukonPAOName(subBusId) + ", " 
                                    + paoDao.getYukonPAOName(substationId);
                            }
                        } else {
                            return paoDao.getYukonPAOName(capbankId) + ", " 
                                + paoDao.getYukonPAOName(feederId) + ", " + paoDao.getYukonPAOName(subBusId);
                        }
                    } else {
                        return paoDao.getYukonPAOName(capbankId) + ", " + paoDao.getYukonPAOName(feederId);
                    }
                } else {
                    return paoDao.getYukonPAOName(capbankId);
                }
            } else {
                return ORPH_STRING;
            }
        }
    }
    
    public String buildLink(final HttpServletRequest request, 
            final String paoName, 
            final Integer paoId, 
            final Integer areaId, 
            String url) {
        
        String safeUrl = "";
        final StringBuilder sb = new StringBuilder();
        if (url.equalsIgnoreCase(FEEDER_URL)) {
            safeUrl = url + "?substationId=" + paoId + "&areaId=" + areaId;
        }else {
            safeUrl = ServletUtil.createSafeUrl(request, url);
        }
        sb.append("<a href=\"");
        sb.append(safeUrl);
        sb.append("\">");
        sb.append(HtmlUtils.htmlEscape(paoName));
        sb.append("</a>");
        String result = sb.toString();
        return result;
    }

    public String printPoint(Integer id) {
        LitePoint point = pointDao.getLitePoint(id);
        return paoDao.getYukonPAOName(point.getPaobjectID());
    }

}

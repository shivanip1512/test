package com.cannontech.cbc.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.cbc.dao.CapbankDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.database.db.capcontrol.CCFeederBankList;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.database.db.capcontrol.CCFeederSubAssignment;
import com.cannontech.util.ServletUtil;

public class ParentStringPrinter {
    private HttpServletRequest request;
    public static final String ORPH_STRING = "---";
    private static final String FEEDER_URL = "/spring/capcontrol/tier/feeders";
    private static final String AREA_URL = "/spring/capcontrol/tier/areas";
    private static final String SPECIAL_AREA_URL = "/spring/capcontrol/tier/areas?isSpecialArea=true";
    private static final String ZONE_DETAIL_URL = "/spring/capcontrol/ivvc/zone/detail";
    
    private CapbankDao capbankDao = YukonSpringHook.getBean("capbankDao",CapbankDao.class);
    private PointDao pointDao;
    private PaoDao paoDao;
    private ZoneDao zoneDao;
    
    public ParentStringPrinter(final HttpServletRequest request) {
        this.request = request;
    }

    public String printPAO(final Integer paoId) {
        LiteYukonPAObject lite = paoDao.getLiteYukonPAO(paoId);
        int type = lite.getType();

        if (type == PaoType.LOAD_TAP_CHANGER.getDeviceTypeId() || 
            type == PaoType.PHASE_OPERATED.getDeviceTypeId() ||
            type == PaoType.GANG_OPERATED.getDeviceTypeId()) {
            
            Zone zone = null;
            
            try {
                zone = zoneDao.getZoneByRegulatorId(paoId);
            } catch (EmptyResultDataAccessException e) {
                return ORPH_STRING;
            }
            
            //This is not cool, but ok...
            String url = ZONE_DETAIL_URL + "?isSpecialArea=false&zoneId=" + zone.getId();

            final StringBuilder sb = new StringBuilder();
            sb.append("<a href=\"");
            sb.append(url);
            sb.append("\">");
            sb.append(zone.getName());
            sb.append("</a>");
            
            String result = sb.toString();
            return result;
        }
        else if (type == CapControlTypes.CAP_CONTROL_AREA || 
            type == CapControlTypes.CAP_CONTROL_SPECIAL_AREA) {
            return ORPH_STRING;
        } else if (type == CapControlTypes.CAP_CONTROL_SUBSTATION) {
            Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(paoId);
            List<Integer> specialAreaIdList = CCSubSpecialAreaAssignment.getAreaIdsForSubstation(paoId);
            if (areaId > -1) {
                String areaName = paoDao.getYukonPAOName(areaId);
                return buildLink(request, areaName, null, null, false, AREA_URL);
            } else if (specialAreaIdList != null && specialAreaIdList.size() > 0){
                String areaName = paoDao.getYukonPAOName(specialAreaIdList.get(0));
                return buildLink(request, areaName, null, null, true, SPECIAL_AREA_URL);
            } else {
                return ORPH_STRING;
            }
        } else if (type == CapControlTypes.CAP_CONTROL_SUBBUS) {
            Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(paoId);
            if (substationId > -1) {
                Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                List<Integer> specialAreaIdList = CCSubSpecialAreaAssignment.getAreaIdsForSubstation(substationId);
                if (areaId > -1) {
                    String parentString = paoDao.getYukonPAOName(substationId) +", "+ paoDao.getYukonPAOName(areaId);
                    return buildLink(request, parentString, substationId, areaId, false, FEEDER_URL);
                } else if (specialAreaIdList != null && specialAreaIdList.size() > 0) {
                    String areaName = paoDao.getYukonPAOName(specialAreaIdList.get(0));
                    String parentString = paoDao.getYukonPAOName(substationId) +", "+ areaName;
                    return buildLink(request, parentString, substationId, specialAreaIdList.get(0), true, FEEDER_URL);
                } else {
                    return paoDao.getYukonPAOName(substationId);
                }
            } else {
                return ORPH_STRING;
            }
        } else if (type == CapControlTypes.CAP_CONTROL_FEEDER) {
            Integer subBusId = CCFeederSubAssignment.getSubBusIdForFeeder(paoId);
            if (subBusId > -1) {
                Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
                if (substationId > -1) {
                    Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                    List<Integer> specialAreaIdList = CCSubSpecialAreaAssignment.getAreaIdsForSubstation(substationId);
                    if (areaId > -1) {
                        String parentString = paoDao.getYukonPAOName(subBusId)+", "+ paoDao.getYukonPAOName(substationId) +", "+ paoDao.getYukonPAOName(areaId);
                        return buildLink(request, parentString, substationId, areaId, false, FEEDER_URL);
                    } else if (specialAreaIdList != null && specialAreaIdList.size() > 0) {
                        String areaName = paoDao.getYukonPAOName(specialAreaIdList.get(0));
                        String parentString = paoDao.getYukonPAOName(subBusId)+", "+ paoDao.getYukonPAOName(substationId) +", "+ areaName;
                        return buildLink(request, parentString, substationId, specialAreaIdList.get(0), true, FEEDER_URL);
                    } else {
                        return paoDao.getYukonPAOName(subBusId) +", "+ paoDao.getYukonPAOName(substationId);
                    }
                } else {
                    return paoDao.getYukonPAOName(subBusId);
                }
            } else {
                return ORPH_STRING;
            }
        } else if (type == CapControlTypes.CAP_CONTROL_CAPBANK) {
            Integer feederId = CCFeederBankList.getFeederIdForCapBank(paoId);
            if (feederId > -1) {
                Integer subBusId = CCFeederSubAssignment.getSubBusIdForFeeder(feederId);
                if (subBusId > -1) {
                    Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
                    if (substationId > -1) {
                        Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                        List<Integer> specialAreaIdList = CCSubSpecialAreaAssignment.getAreaIdsForSubstation(substationId);
                        if (areaId > -1) {
                            String parentString = paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId)+", "+ paoDao.getYukonPAOName(substationId) +", "+ paoDao.getYukonPAOName(areaId);
                            return buildLink(request, parentString, substationId, areaId, false, FEEDER_URL);
                        } else if (specialAreaIdList != null && specialAreaIdList.size() > 0) {
                            String areaName = paoDao.getYukonPAOName(specialAreaIdList.get(0));
                            String parentString = paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId)+", "+ paoDao.getYukonPAOName(substationId) +", "+ areaName;
                            return buildLink(request, parentString, substationId, specialAreaIdList.get(0), true, FEEDER_URL);
                        } else {
                            return paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId) +", "+ paoDao.getYukonPAOName(substationId);
                        }
                    } else {
                        return paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId);
                    }
                } else {
                    return paoDao.getYukonPAOName(feederId);
                }
            } else {
                return ORPH_STRING;
            }
        } else {
            Integer capbankId = capbankDao.getCapBankIdByCBC(paoId);
            if (capbankId > -1) {
                Integer feederId = CCFeederBankList.getFeederIdForCapBank(capbankId);
                if (feederId > -1) {
                    Integer subBusId = CCFeederSubAssignment.getSubBusIdForFeeder(feederId);
                    if (subBusId > -1) {
                        Integer substationId = CCSubstationSubBusList.getSubStationForSubBus(subBusId);
                        if (substationId > -1) {
                            Integer areaId = CCSubAreaAssignment.getAreaIDForSubStation(substationId);
                            List<Integer> specialAreaIdList = CCSubSpecialAreaAssignment.getAreaIdsForSubstation(substationId);
                            if (areaId > -1) {
                                String parentString = paoDao.getYukonPAOName(capbankId) +", "+ paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId)+", "+ paoDao.getYukonPAOName(substationId) +", "+ paoDao.getYukonPAOName(areaId);
                                return buildLink(request, parentString, substationId, areaId, false, FEEDER_URL);
                            } else if (specialAreaIdList != null && specialAreaIdList.size() > 0) {
                                String areaName = paoDao.getYukonPAOName(specialAreaIdList.get(0));
                                String parentString = paoDao.getYukonPAOName(capbankId) +", "+ paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId)+", "+ paoDao.getYukonPAOName(substationId) +", "+ areaName;
                                return buildLink(request, parentString, substationId, specialAreaIdList.get(0), true, FEEDER_URL);
                            } else {
                                return paoDao.getYukonPAOName(capbankId) +", "+ paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId) +", "+ paoDao.getYukonPAOName(substationId);
                            }
                        } else {
                            return paoDao.getYukonPAOName(capbankId) +", "+ paoDao.getYukonPAOName(feederId) +", "+ paoDao.getYukonPAOName(subBusId);
                        }
                    } else {
                        return paoDao.getYukonPAOName(capbankId) +", "+ paoDao.getYukonPAOName(feederId);
                    }
                } else {
                    return paoDao.getYukonPAOName(capbankId);
                }
            } else {
                return ORPH_STRING;
            }
        }
    }
    
    private String buildLink(final HttpServletRequest request, final String paoName, final Integer paoId, final Integer areaId, boolean special, String url) {
        String safeUrl = "";
        final StringBuilder sb = new StringBuilder();
        if(url.equalsIgnoreCase(FEEDER_URL)) {
            safeUrl = url + "?subStationId=" + paoId + "&areaId=" + areaId + "&isSpecialArea=" + special;
        }else {
            safeUrl = ServletUtil.createSafeUrl(request, url);
        }
        sb.append("<a href=\"");
        sb.append(safeUrl);
        sb.append("\">");
        sb.append(paoName);
        sb.append("</a>");
        String result = sb.toString();
        return result;
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
    
    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }
}

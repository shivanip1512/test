package com.cannontech.cbc.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.Validate;

import com.cannontech.cbc.cache.CapControlCache;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.CapControlDao;
import com.cannontech.util.ServletUtil;
import com.cannontech.yukon.cbc.CCArea;
import com.cannontech.yukon.cbc.CCSpecialArea;
import com.cannontech.yukon.cbc.CapBankDevice;
import com.cannontech.yukon.cbc.Feeder;
import com.cannontech.yukon.cbc.StreamableCapObject;
import com.cannontech.yukon.cbc.SubBus;
import com.cannontech.yukon.cbc.SubStation;

public class ParentStringPrinter {
    private static final String AREA_URL = "/capcontrol/subareas.jsp";
    private static final String SPECIAL_AREA_URL = "/capcontrol/specialSubAreas.jsp";
    private static final String FEEDER_URL = "/capcontrol/feeders.jsp";
    private static final String ORPH_STRING = "---";
    private final HttpServletRequest request;
    private CapControlCache capControlCache;
    private CapControlDao cbcDao;

    public ParentStringPrinter(final HttpServletRequest request) {
        this.request = request;
    }

    public String printPAO(final Integer paoId) {
        StreamableCapObject obj = getStreamable(paoId);
        if (obj instanceof CCArea || obj instanceof CCSpecialArea) return ORPH_STRING;
        
        Integer parentId = (obj != null) ? obj.getParentID() : getCapBankForController(paoId);
        if (parentId < 1) return ORPH_STRING;
        
        String result = printHierarchy(parentId); 
        return result;
    }

    private StreamableCapObject getStreamable(Integer paoId) {
    	Validate.notNull(paoId, "id cannot be null");
    	return capControlCache.getCapControlPAO(paoId);
    }

    private String printHierarchy(int id) {
        /*
         * If a cc object does not live under an area it can't be
         * displayed in feeders.jsp.  If this is the case the parent link
         * will be set to the editor for it's parent instead of feeders.jsp.
         */
        
        boolean isCBCId = isController(id);
        if (isCBCId) {
            Integer capBankId = getCapBankForController(id);
            String controllerResult = printHierarchy(capBankId);
            return controllerResult;
        }

        String result = ORPH_STRING;
        final StreamableCapObject capObject = getStreamable(id);

        if (capObject == null) return result;

        final int parentId = capObject.getParentID();
        final String paoName = capObject.getCcName();
        final Integer paoId = capObject.getCcId();

        if (capObject instanceof CCArea) {
            result = buildLink(request, paoName, null, AREA_URL);
        }

        if (capObject instanceof CCSpecialArea) {
            result = buildLink(request, paoName, null, SPECIAL_AREA_URL);
        }

        if (capObject instanceof SubStation) {
            if (((SubStation)capObject).getParentID() < 1) {
                String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                result = appendHierarchy(editorLink, parentId);
            }else {
                String feederLink = buildLink(request, paoName, paoId, FEEDER_URL);
                result = appendHierarchy(feederLink, parentId);
            }
        }

        if (capObject instanceof SubBus) {
            SubBus bus = (SubBus)capObject;
            Integer stationId = bus.getParentID();
            if(stationId < 1) {
                String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                result = appendHierarchy(editorLink, parentId);
            }else {
                SubStation station = capControlCache.getSubstation(stationId);
                Integer areaId = station.getParentID();
                if(areaId < 1) {
                    String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                    result = appendHierarchy(editorLink, parentId);
                }else {
                    String feederLink = buildLink(request, paoName, parentId, FEEDER_URL);
                    result = appendHierarchy(feederLink, parentId);
                }
            }
        }

        if (capObject instanceof Feeder) {
            Feeder fdr = (Feeder)capObject;
            Integer busId = fdr.getParentID();
            if(busId < 1) {
                String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                result = appendHierarchy(editorLink, parentId);
            }else {
                SubBus bus = capControlCache.getSubBus(busId);
                Integer stationId = bus.getParentID();
                if(stationId < 1) {
                    String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                    result = appendHierarchy(editorLink, parentId);
                }else {
                    SubStation station = capControlCache.getSubstation(stationId);
                    Integer areaId = station.getParentID();
                    if(areaId < 1) {
                        String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                        result = appendHierarchy(editorLink, parentId);
                    }else {
                        Integer substationId = getSubstationId(parentId);
                        String feederLink = buildLink(request, paoName, substationId, FEEDER_URL);
                        result = appendHierarchy(feederLink, parentId);
                    }
                }
            }
        }    

        if (capObject instanceof CapBankDevice) {
            
            CapBankDevice cb = (CapBankDevice) capObject;
            Integer fdrId = cb.getParentID();
            if(fdrId < 1) {
                String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                result = appendHierarchy(editorLink, parentId);
            }else {
                
                Feeder fdr = capControlCache.getFeeder(fdrId);
                Integer busId = fdr.getParentID();
                if(busId < 1) {
                    String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                    result = appendHierarchy(editorLink, parentId);
                }else {
                    SubBus bus = capControlCache.getSubBus(busId);
                    Integer stationId = bus.getParentID();
                    if(stationId < 1) {
                        String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                        result = appendHierarchy(editorLink, parentId);
                    }else {
                        SubStation station = capControlCache.getSubstation(stationId);
                        Integer areaId = station.getParentID();
                        if(areaId < 1) {
                            String editorLink = buildEditorLink(request, paoName, "/editor/cbcBase.jsf?type=2&itemid=" +paoId+"&ignoreBookmark=true");
                            result = appendHierarchy(editorLink, parentId);
                        }else {
                            Integer substationId = getSubstationId(parentId);
                            String feederLink = buildLink(request, paoName, substationId, FEEDER_URL);
                            result = appendHierarchy(feederLink, parentId);
                        }
                    }
                }
            }
        }

        return result;
    }
    
    private Integer getSubstationId(Integer paoId) {
        final StreamableCapObject capObject = capControlCache.getCapControlPAO(paoId);
        
        if (capObject instanceof CCArea || capObject instanceof CCSpecialArea)
            throw new UnsupportedOperationException("Area not supported");
        
        if (capObject instanceof SubStation) {
            return capObject.getCcId();
        }
        if(capObject == null) {
            return 0;
        }
        return getSubstationId(capObject.getParentID());
    }
    
    private String appendHierarchy(String link, int parentId) {
        String hierarchy = printHierarchy(parentId);
        
        final StringBuilder sb = new StringBuilder();
        sb.append(hierarchy);
        sb.append(":");
        sb.append(link);
        
        String result = sb.toString();
        return result;
    }
    
    private String buildLink(final HttpServletRequest request, final String paoName, final Integer paoId, final String url) {
        String safeUrl = ServletUtil.createSafeUrl(request, url);
        
        final StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"");
        sb.append(safeUrl);
        if (paoId != null) sb.append("?id=" + paoId);
        sb.append("\">");
        sb.append(paoName);
        sb.append("</a>");
        String result = sb.toString();
        return result;
    }
    
    private String buildEditorLink(final HttpServletRequest request, final String paoName, final String url) {
        String safeUrl = ServletUtil.createSafeUrl(request, url);
        
        final StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"");
        sb.append(safeUrl);
        sb.append("\">");
        sb.append(paoName);
        sb.append("</a>");
        String result = sb.toString();
        return result;
    }
    
    private Integer getCapBankForController(int id) {
        Integer parentID = cbcDao.getParentForController(id);
        return parentID;
    }

    private boolean isController(int id) {
        return CBCUtils.isController(id);
    }

    public String printPoint(Integer id) {
        Integer parentID = cbcDao.getParentForPoint(id);
        if (parentID > 0) return printHierarchy(parentID);
        return ORPH_STRING;
    }

    public void setCapControlCache(CapControlCache capControlCache) {
        this.capControlCache = capControlCache;
    }

    public void setCbcDao(CapControlDao cbcDao) {
        this.cbcDao = cbcDao;
    }

}

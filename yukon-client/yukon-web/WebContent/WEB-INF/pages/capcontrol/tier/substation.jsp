<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="substation">
<%@ include file="/capcontrol/capcontrolHeader.jspf"%>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<cti:url var="onelineCBCServlet" value="/capcontrol/oneline/OnelineCBCServlet"/>
<cti:url var="stateMenuUrl" value="/spring/capcontrol/menu/capBankState"/>

<cti:msg2 var="moveBank" key="yukon.web.modules.capcontrol.substation.moveBank"/>
<c:set var="substationId" value="${substation.id}"/>

<c:if test="${hasSubstationControl}">
    <script type="text/javascript">
        addCommandMenuBehavior('a[id^="substationState"]');
    </script>
</c:if>

<script type="text/javascript">
Event.observe(window, 'load', checkPageExpire);

// Filters
function applySubBusFilter(select) {
    var rows = $$("tr[id^='tr_sub_']");
    var subBusIds = new Array();
    if (select.selectedIndex == 0) {
        rows.each(function (row) {
            row.show();
            subBusIds.push(row.id.split('_')[2]);
        });
        $('feederFilter').selectedIndex = 0;
        applyFeederFilter(subBusIds);
    } else {
        var selectedSubBusId = select.options[select.selectedIndex].value;
        rows.each(function (row) {
            var subBusId =  row.id.split('_')[2];
            if (subBusId == selectedSubBusId) {
                row.show();
                subBusIds.push(subBusId);
            } else {
                row.hide();
            }
        });
        applyFeederFilter(subBusIds);
    }
}

function applyFeederSelectFilter(select) {
    var rows = $$("tr[id^='tr_feeder_']");
    var feederIds = new Array();
    if (select.selectedIndex == 0) {
        rows.each(function (row) {
            row.show();
            feederIds.push(row.id.split('_')[2]);
        });
        applyCapBankFilter(feederIds);
    } else {
        var selectedFeederId = select.options[select.selectedIndex].value;
        rows.each(function (row) {
            var feederId =  row.id.split('_')[2];
            
            if (feederId == selectedFeederId) {
                row.show();
                feederIds.push(feederId);
            } else {
                row.hide();
            }
        });
        applyCapBankFilter(feederIds);
    }
}

function applyFeederFilter(subBusIds) {
    var rows = $$("tr[id^='tr_feeder_']");
    var feederIds = new Array();
    rows.each(function(row) {
        var parentId = row.id.split('_')[4];
        if (subBusIds.indexOf(parentId) != -1) {
            row.show();
            feederIds.push(row.id.split('_')[2]);
        } else {
            row.hide();
        }
    });
    applyCapBankFilter(feederIds);
}

function applyCapBankFilter(feederIds) {
    var rows = $$("tr[id^='tr_cap_']");
    rows.each(function(row) {
        var parentId = row.id.split('_')[4];
        if (feederIds.indexOf(parentId) != -1) {
            row.show();
        } else {
            row.hide();
        }
    });
}

// BANK MOVE JS
function toggleLi(event) {
    var li = event.findElement('li');
    var childUl = li.down('ul');
    if (childUl) {
        if (childUl.toggle().visible()) {
            li.addClassName('minus');
            li.removeClassName('plus');
        } else {
            li.addClassName('plus');
            li.removeClassName('minus');
        }
    }
}
YEvent.observeSelectorClick('li.toggle', toggleLi);
</script>

<i:simplePopup titleKey=".recentEvents" id="recentEventsOptions" on="#recentEventsButton" styleClass="mediumSimplePopup">
    <%@ include file="recentEventsOptions.jspf" %>
</i:simplePopup>

<c:if test="${showAnalysis}">
    <i:simplePopup titleKey=".analysisTrends" id="analysisTrendsOptions" on="#analysisTrendsButton" styleClass="mediumSimplePopup">
        <%@ include file="analysisTrendsOptions.jspf" %>
    </i:simplePopup>
</c:if>
    
    <input type="hidden" id="paoId_${substationId}" value="${substationId}">
    
    <c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editKey" value="edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="editKey" value="info"/>
        </c:otherwise>
    </c:choose>
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout" cellStyle="width: 50%;" tableStyle="width:100%;">
        
        <cti:dataGridCell>
        
            <tags:boxContainer2 nameKey="infoContainer" styleClass="padBottom" hideEnabled="true" showInitially="true">
                <div style="float:left;width:50%;">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".name">
                            <span><spring:escapeBody>${substation.name}</spring:escapeBody></span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".geoName">
                            <span><spring:escapeBody>${substation.description}</spring:escapeBody></span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".area">
                            <span>
                                <c:choose>
                                    <c:when test="${areaId > 0}">
                                        <cti:deviceName deviceId="${areaId}"/>
                                    </c:when>
                                    <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
                                </c:choose>
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    
                </div>
                <div style="float:right;width:50%;">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".state" rowClass="nw">
                            <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                                <a id="substationState_${substationId}">
                                    <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE"/>
                                </a>
                                <cti:dataUpdaterCallback function="updateStateColorGenerator('substationState_${substationId}')" initialize="true" value="SUBSTATION/${substationId}/STATE"/>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".mapLocationId">
                            <span>${substation.mapLocationId}</span>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".specialArea">
                            <span>
                                <c:choose>
                                    <c:when test="${specialAreaId > 0}">
                                        <cti:classUpdater type="SUBSTATION" identifier="${substationId}/SA_ENABLED">
                                            <cti:deviceName deviceId="${specialAreaId}"/>: <cti:capControlValue paoId="${specialAreaId}" type="CBCSPECIALAREA" format="STATE"/>
                                        </cti:classUpdater>
                                    </c:when>
                                    <c:otherwise><i:inline key="yukon.web.defaults.none"/></c:otherwise>
                                </c:choose>
                            </span>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="clear actionArea fl">
                    <cti:button nameKey="location" href="/spring/capcontrol/capbank/capBankLocations?value=${substationId}&amp;specialArea=${isSpecialArea}"/>
                </div>
                
                <div class="actionArea fr">
                    <cti:button nameKey="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${substationId}"/>
                    <c:if test="${hasEditingRole}">
                        <cti:button nameKey="remove" href="/editor/deleteBasePAO.jsf?value=${substationId}"/>
                    </c:if>
                </div>
        
            </tags:boxContainer2>
            
        </cti:dataGridCell>
        
        <cti:dataGridCell>
        
            <tags:boxContainer2 nameKey="statsContainer" styleClass="padBottom" hideEnabled="true" showInitially="true">
                <div style="float:left;width:50%;">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".availableKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE" />
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".unavailableKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" />
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                </div>
                <div style="float:right;width:50%;">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".closedKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED" />
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".trippedKvars">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED" />
                        </tags:nameValue2>
                        
                    </tags:nameValueContainer2>
                </div>
                <div class="clear">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".pfactorEstimated" rowClass="powerFactor">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" />
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="clear actionArea">
                    <c:if test="${showAnalysis}">
                        <cti:button nameKey="analysis" id="analysisTrendsButton"/>
                    </c:if>
                    <cti:button nameKey="recentEvents" id="recentEventsButton"/>
                </div>
            </tags:boxContainer2>
        
        </cti:dataGridCell>
        
    </cti:dataGrid>
    
    <%@ include file="busTier.jspf" %>
    <%@ include file="feederTier.jspf" %>
    <%@ include file="bankTier.jspf" %>
    
</cti:standardPage>
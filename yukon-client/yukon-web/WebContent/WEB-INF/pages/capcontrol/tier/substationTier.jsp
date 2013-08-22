<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %> <%-- Used in Trends --%>

<cti:standardPage module="capcontrol" page="area">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    <flot:defaultIncludes/>

    <c:if test="${hasSubstationControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="substationState_"]');
        </script>
    </c:if>
    
    <c:if test="${hasAreaControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="areaState_"]');
        </script>
    </c:if>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

    <c:if test="${hasEditingRole}">
        <c:set var="editKey" value="edit"/>
    </c:if>
    <c:if test="${not hasEditingRole}">
        <c:set var="editKey" value="info"/>
    </c:if>
    <c:if test="${isSpecialArea}">
        <c:set var="updaterType" value="CBCSPECIALAREA" />
    </c:if>
    <c:if test="${not isSpecialArea}">
        <c:set var="updaterType" value="CBCAREA" />
    </c:if>
    
    
    <div class="column_12_12 clearfix">
        <div class="column one">
            <tags:boxContainer2 nameKey="infoContainer" styleClass="padBottom" hideEnabled="true" showInitially="true">
                <div class="column_12_12 clearfix">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="infoContainer">
                            <tags:nameValue2 nameKey=".name">
                                <span>${fn:escapeXml(bc_areaName)}</span>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".geoName">
                                <span>${fn:escapeXml(description)}</span>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:nameValueContainer2 tableClass="infoContainer">
                            <tags:nameValue2 nameKey=".state" rowClass="wsnw">
                                <capTags:warningImg paoId="${bc_areaId}" type="${updaterType}"/>
                                <a id="areaState_${bc_areaId}" href="javascript:void(0);">
                                    <cti:capControlValue paoId="${bc_areaId}" type="${updaterType}" format="STATE" />
                                </a>
                                <cti:dataUpdaterCallback function="updateStateColorGenerator('areaState_${bc_areaId}')" initialize="true" value="${updaterType}/${bc_areaId}/STATE"/>
                             </tags:nameValue2>
                             <tags:nameValue2 nameKey=".substations">
                                ${fn:length(subStations)}
                             </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
                <div class="actionArea">
                    <cti:button nameKey="${editKey}" href="/editor/cbcBase.jsf?type=2&amp;itemid=${bc_areaId}" icon="icon-pencil"/>
                </div>
            </tags:boxContainer2>
        </div>
        
        <div class="column two nogutter">
            <tags:boxContainer2 nameKey="statsContainer" styleClass="padBottom" hideEnabled="true" showInitially="true">
                <div class="column_12_12">
                    <div class="column one">
                        <tags:nameValueContainer2 tableClass="infoContainer">
                            <tags:nameValue2 nameKey=".availableKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${updaterType}" format="KVARS_AVAILABLE"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".unavailableKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${updaterType}" format="KVARS_UNAVAILABLE"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                    <div class="column two nogutter">
                        <tags:nameValueContainer2 tableClass="infoContainer">
                            <tags:nameValue2 nameKey=".closedKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${updaterType}" format="KVARS_CLOSED"/>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".trippedKvars">
                                <cti:capControlValue paoId="${bc_areaId}" type="${updaterType}" format="KVARS_TRIPPED"/>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </div>
                </div>
                <div class="clear">
                    <tags:nameValueContainer2 tableClass="infoContainer">
                        <tags:nameValue2 nameKey=".pfactorEstimated" rowClass="powerFactor">
                            <cti:capControlValue paoId="${bc_areaId}" type="${updaterType}" format="PFACTOR"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </div>
                <div class="clear actionArea">
                    <c:if test="${showAnalysis}">
                        <i:simplePopup titleKey=".analysisTrends" id="analysisTrendsOptions" on="#analysisTrendsButton">
                             <%@ include file="analysisTrendsOptions.jspf" %>
                        </i:simplePopup>
                        <cti:button nameKey="analysis" id="analysisTrendsButton" icon="icon-chart-line"/>
                    </c:if>
                
                    <i:simplePopup titleKey=".recentEvents" id="recentEventsOptions" on="#recentEventsButton">
                         <%@ include file="recentEventsOptions.jspf" %>
                    </i:simplePopup>
                    <cti:button nameKey="recentEvents" id="recentEventsButton" icon="icon-application-view-columns"/>
                </div>
            </tags:boxContainer2>
        </div>
    </div>
    
    <tags:boxContainer2 hideEnabled="false" nameKey="substationsContainer" arguments="${bc_areaName}">
        
        <table id="subTable" class="compactResultsTable">
            <thead>
                <tr>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".actions"/></th>
                    <th><i:inline key=".state"/></th>
                    <th><i:inline key=".availableKvars"/></th>
                    <th><i:inline key=".unavailableKvars"/></th>
                    <th><i:inline key=".closedKvars"/></th>
                    <th><i:inline key=".trippedKvars"/></th>
                    <th><i:inline key=".pfactorEstimated"/></th>
                </tr>
            </thead>
    
            <c:forEach var="subStation" items="${subStations}">
            
                <c:set var="substationId" value="${subStation.ccId}"/>
                <cti:url var="editUrl" value="/editor/cbcBase.jsf">
                    <cti:param name="itemid" value="${substationId}"/>
                    <cti:param name="type" value="2"/>
                </cti:url>
                <cti:url var="deleteUrl" value="/editor/deleteBasePAO.jsf">
                    <cti:param name="value" value="${substationId}"/>
                </cti:url>
                <cti:url value="/capcontrol/tier/feeders" var="feederLink">
                    <cti:param name="substationId" value="${substationId}"/>
                    <cti:param name="isSpecialArea" value="${isSpecialArea}"/>
                </cti:url>
            
    	        <tr>
                
    				<td>
                        <input type="hidden" id="paoId_${substationId}" value="${substationId}">
    				    <a href="${feederLink}" id="anc_${substationId}">
                            <spring:escapeBody>${subStation.ccName}</spring:escapeBody>
                        </a>
    				    <span class="error textFieldLabel">
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="SA_ENABLED_MSG" />
                        </span>
    				</td>
                    
                    <td class="wsnw">
                        <cti:icon nameKey="${editKey}" href="${editUrl}" icon="icon-pencil"/>
                        <c:if test="${hasEditingRole}">
                            <cti:icon nameKey="remove" href="${deleteUrl}"  icon="icon-cross"/>
                        </c:if>
                    </td>
                    
    				<td class="wsnw">
                        <capTags:warningImg paoId="${substationId}" type="SUBSTATION"/>
                        <c:if test="${hasSubstationControl}"><a id="substationState_${substationId}" href="javascript:void(0);"></c:if>
                        <c:if test="${!hasSubstationControl}"><span id="substationState_${substationId}"></c:if>
                            <cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="STATE" />
                        <c:if test="${hasSubstationControl}"></a></c:if>
                        <c:if test="${!hasSubstationControl}"></span></c:if>
                        <cti:dataUpdaterCallback function="updateStateColorGenerator('substationState_${substationId}')" initialize="true" value="SUBSTATION/${substationId}/STATE"/>
                	</td>
                    
    				<td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_AVAILABLE" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_UNAVAILABLE" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_CLOSED" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="KVARS_TRIPPED" /></td>
                    <td><cti:capControlValue paoId="${substationId}" type="SUBSTATION" format="PFACTOR" /></td>
                </tr>
            </c:forEach>
    		
        </table>
    
    </tags:boxContainer2>
    
</cti:standardPage>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="capTags" tagdir="/WEB-INF/tags/capcontrol"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="area">
    <%@include file="/capcontrol/capcontrolHeader.jspf"%>

    <c:if test="${hasSubstationControl}">
        <script type="text/javascript">
            addCommandMenuBehavior('a[id^="substationState_"]');
        </script>
    </c:if>

    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

    <c:choose>
        <c:when test="${hasEditingRole}">
            <c:set var="editKey" value="edit"/>
        </c:when>
        <c:otherwise>
            <c:set var="editKey" value="info"/>
        </c:otherwise>
    </c:choose>
    
    <tags:boxContainer2 hideEnabled="false" nameKey="substationsContainer" arguments="${bc_areaName}" styleClass="padBottom">
        
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
            
    	        <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                
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
                        <cti:img nameKey="${editKey}" href="${editUrl}" styleClass="tierIconLink"/>
                        <c:if test="${hasEditingRole}">
                            <cti:img nameKey="remove" href="${deleteUrl}" styleClass="tierIconLink"/>
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
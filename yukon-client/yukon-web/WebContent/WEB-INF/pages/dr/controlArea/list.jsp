<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="controlAreaList">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.controlAreaList.breadcrumb.controlAreas"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlAreaList.controlAreas"/></h2>

	<table id="controlAreaList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.name"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.state"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.valueThreshold"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.peakProjection"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.atku"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.priority"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.startStop"/></th>
            <th><cti:msg key="yukon.web.modules.dr.controlAreaList.heading.loadCapacity"/></th>
		</tr>
		<c:forEach var="controlArea" items="${controlAreas}">
            <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
            <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
                <c:param name="controlAreaId" value="${controlAreaId}"/>
            </c:url>
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>
                    <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${controlArea.name}</spring:escapeBody></a>
                </td>
                <td>
                    <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STATE"/>
                </td>

                <td>
                    <c:if test="${empty controlArea.triggers}">
                        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers"/>              
                    </c:if>
                    <c:forEach var="trigger" items="${controlArea.triggers}">
                           <cti:dataUpdaterValue type="DR_CONTROLAREA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/VALUE"/>
                           /
                           <cti:dataUpdaterValue type="DR_CONTROLAREA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/THRESHOLD"/>
                           <br/>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach var="trigger" items="${controlArea.triggers}">
                        <c:if test="${trigger.thresholdType}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/PEAK"/>
                            /
                            <cti:dataUpdaterValue type="DR_CONTROLAREA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/PROJECTION"/>
                        </c:if>
                        <br/>
                    </c:forEach>
                    
                </td>
                <td>
                    <c:forEach var="trigger" items="${controlArea.triggers}">
                       <c:if test="${trigger.thresholdType}">
                           <cti:dataUpdaterValue type="DR_CONTROLAREA_TRIGGER" identifier="${controlAreaId}/${trigger.triggerNumber}/ATKU"/>
                        </c:if>
                        <br/>
                    </c:forEach>
                </td>

                <td>
                    <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                </td>               
                <td>
                    <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                    -
                    <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                </td>               
                <td>
                    <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/LOAD_CAPACITY"/>
                </td>				
			</tr>
		</c:forEach>
	</table>

</cti:standardPage>

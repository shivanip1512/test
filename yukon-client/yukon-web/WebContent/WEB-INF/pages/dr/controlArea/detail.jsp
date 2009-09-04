<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.controlAreaDetail.pageTitle" argument="${controlArea.name}"/>
<cti:standardPage module="dr" page="controlAreaDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|controlareas"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/controlArea/list">
        	<cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.controlAreas"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.controlAreaDetail.breadcrumb.controlArea"
                htmlEscape="true" argument="${controlArea.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.controlAreaDetail.controlArea"
        htmlEscape="true" argument="${controlArea.name}"/></h2>
    <br>

    <c:set var="controlAreaId" value="${controlArea.paoIdentifier.paoId}"/>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.state"/>
                        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STATE"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.priority"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/PRIORITY"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.startStop"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/START"/>
                            -
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/STOP"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.loadCapacity"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue type="DR_CONTROLAREA" identifier="${controlAreaId}/LOAD_CAPACITY"/>
                        </tags:nameValue>

	                    <c:if test="${!empty controlArea.triggers}">
	                        <cti:msg var="fieldName" key="yukon.web.modules.dr.controlAreaDetail.info.triggers"/>
	                        <tags:nameValue name="${fieldName}">
                            <table id="controlAreaList" class="resultsTable activeResultsTable">
                                <tr>
                                    <th><cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.valueThreshold"/></th>
                                    <th><cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.peakProjection"/></th>
                                    <th><cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.atku"/></th>
                                </tr>
                                <c:forEach var="trigger" items="${controlArea.triggers}">
                                    <c:set var="triggerNumber" value="${trigger.triggerNumber}"/>                               
                                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                        <td>
                                           <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/VALUE"/>
                                           /
                                           <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/THRESHOLD"/>
                                        </td>
                                        <td>
                                            <c:if test="${trigger.thresholdType}">
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/PEAK"/>
                                                /
                                                <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/PROJECTION"/>
                                            </c:if>
                                        </td>
                                        <td>
                                            <c:if test="${trigger.thresholdType}">
                                               <cti:dataUpdaterValue type="DR_CA_TRIGGER" identifier="${controlAreaId}/${triggerNumber}/ATKU"/>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </table>
	                        </tags:nameValue>
	                    </c:if>
                    </tags:nameValueContainer>
                    <c:if test="${empty controlArea.triggers}">
                        <br/>
                        <cti:msg key="yukon.web.modules.dr.controlAreaDetail.info.noTriggers"/>
                    </c:if>
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.start"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.stop"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.triggersChange"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.dailyTimeChange"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.disable"/><br>
                    <cti:msg key="yukon.web.modules.dr.controlAreaDetail.actions.enable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.controlAreaDetail.heading.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
        <c:set var="baseUrl" value="/spring/dr/controlArea/detail"/>
        <%@ include file="../program/programList.jspf" %>
    </tags:abstractContainer>

</cti:standardPage>

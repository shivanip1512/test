<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.programDetail.pageTitle" argument="${program.name}"/>
<cti:standardPage module="dr" page="programDetail" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|programs"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/program/list">
        	<cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.programs"/>
        </cti:crumbLink>
        <cti:crumbLink>
            <cti:msg key="yukon.web.modules.dr.programDetail.breadcrumb.program"
                htmlEscape="true" argument="${program.name}"/>
        </cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.programDetail.program"
        htmlEscape="true" argument="${program.name}"/></h2>

    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.info"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <tags:nameValueContainer>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.state"/>
                        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
                            <cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.start"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/START" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.stop"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/STOP" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.currentGear"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/CURRENT_GEAR" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.priority"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/PRIORITY" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.reduction"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/REDUCTION" type="DR_PROGRAM"/>
                        </tags:nameValue>
                        <cti:msg var="fieldName" key="yukon.web.modules.dr.programDetail.info.loadCapacity"/>
                        <tags:nameValue name="${fieldName}">
                            <cti:dataUpdaterValue identifier="${programId}/LOAD_CAPACITY" type="DR_PROGRAM"/>
                        </tags:nameValue>
                    </tags:nameValueContainer>
                </tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.modules.dr.programDetail.actions.start"/><br>
            		<cti:msg key="yukon.web.modules.dr.programDetail.actions.stop"/><br>
                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.changeGears"/><br>
                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.enable"/><br>
                    <cti:msg key="yukon.web.modules.dr.programDetail.actions.disable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.loadGroups"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
	    <c:if test="${empty loadGroups}">
	        <cti:msg key="yukon.web.modules.dr.programDetail.loadGroups.notFound"/>
	    </c:if>
        <c:if test="${!empty loadGroups}">
            <%@ include file="../loadGroup/loadGroupList.jspf" %>
        </c:if>
    </tags:abstractContainer>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.programDetail.heading.parents"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

        <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.controlArea"/></p>
        <c:if test="${empty parentControlArea}">
            <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.noControlArea"/></p>
        </c:if>
        <c:if test="${!empty parentControlArea}">
            <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
                <c:param name="controlAreaId" value="${parentControlArea.paoIdentifier.paoId}"/>
            </c:url>
            <a href="${controlAreaURL}"><spring:escapeBody htmlEscape="true">${parentControlArea.name}</spring:escapeBody></a><br>
        </c:if>
        <br>

        <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.scenarios"/></p>
        <c:if test="${empty parentScenarios}">
            <p><cti:msg key="yukon.web.modules.dr.programDetail.parents.noScenarios"/></p>
        </c:if>
        <c:if test="${!empty parentScenarios}">
	        <c:forEach var="parentScenario" items="${parentScenarios}">
                <c:url var="scenarioURL" value="/spring/dr/scenario/detail">
                    <c:param name="scenarioId" value="${parentScenario.paoIdentifier.paoId}"/>
                </c:url>
                <a href="${scenarioURL}"><spring:escapeBody htmlEscape="true">${parentScenario.name}</spring:escapeBody></a><br>
	        </c:forEach>
        </c:if>

    </tags:abstractContainer>

</cti:standardPage>

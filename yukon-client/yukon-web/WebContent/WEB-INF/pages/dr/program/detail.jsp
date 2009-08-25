<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.dr.program.detail.pageTitle" argument="${program.name}"/>
<cti:standardPage module="dr" title="${pageTitle}">
    <cti:standardMenu menuSelection="details|programs"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
        	<cti:msg key="yukon.web.dr.program.detail.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/program/list">
        	<cti:msg key="yukon.web.dr.program.detail.breadcrumb.programs"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.dr.program.detail.breadcrumb.program" argument="${program.name}"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.dr.program.detail.program" argument="${program.name}"/></h2>

    <table cellspacing="0" cellpadding="0" width="100%">
        <tr>
            <td width="50%" valign="top">
                <cti:msg var="boxTitle" key="yukon.web.dr.program.detail.heading.info"/>
	            <tags:abstractContainer type="box" title="${boxTitle}">
            	</tags:abstractContainer>
            </td>
            <td width="10">&nbsp;</td>
            <td width="50%" valign="top">
                <cti:msg var="infoTitle" key="yukon.web.dr.program.detail.heading.actions"/>
            	<tags:abstractContainer type="box" title="${boxTitle}">
            		<cti:msg key="yukon.web.dr.program.detail.actions.start"/><br>
            		<cti:msg key="yukon.web.dr.program.detail.actions.stop"/><br>
                    <cti:msg key="yukon.web.dr.program.detail.actions.changeGears"/><br>
                    <cti:msg key="yukon.web.dr.program.detail.actions.enable"/><br>
                    <cti:msg key="yukon.web.dr.program.detail.actions.disable"/><br>
    	        </tags:abstractContainer>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.dr.program.detail.heading.loadGroups"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

	<table id="loadGroupList" class="compactMiniResultsTable">
		<tr>
			<th><cti:msg key="yukon.web.dr.program.detail.loadGroups.heading.name"/></th>
		</tr>
		<c:forEach var="loadGroup" items="${loadGroups}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				<td>
				<c:url var="loadGroupURL" value="/spring/dr/loadGroup/detail">
					<c:param name="loadGroupId" value="${loadGroup.paoIdentifier.paoId}"/>
				</c:url>
				<a href="${loadGroupURL}">${loadGroup.name}</a>
				</td>
			</tr>
		</c:forEach>
	</table>

    </tags:abstractContainer>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.dr.program.detail.heading.parents"/>
    <tags:abstractContainer type="box" title="${boxTitle}">

        <p><cti:msg key="yukon.web.dr.program.detail.parents.controlArea"/></p>
        <c:if test="${empty parentControlArea}">
            <p><cti:msg key="yukon.web.dr.program.detail.parents.noControlArea"/></p>
        </c:if>
        <c:if test="${!empty parentControlArea}">
            <c:url var="controlAreaURL" value="/spring/dr/controlArea/detail">
                <c:param name="controlAreaId" value="${parentControlArea.paoIdentifier.paoId}"/>
            </c:url>
            <a href="${controlAreaURL}">${parentControlArea.name}</a><br>
        </c:if>
        <br>

        <p><cti:msg key="yukon.web.dr.program.detail.parents.scenarios"/></p>
        <c:if test="${empty parentScenarios}">
            <p><cti:msg key="yukon.web.dr.program.detail.parents.noScenarios"/></p>
        </c:if>
        <c:if test="${!empty parentScenarios}">
	        <c:forEach var="parentScenario" items="${parentScenarios}">
                <c:url var="scenarioURL" value="/spring/dr/scenario/detail">
                    <c:param name="scenarioId" value="${parentScenario.paoIdentifier.paoId}"/>
                </c:url>
                <a href="${scenarioURL}">${parentScenario.name}</a><br>
	        </c:forEach>
        </c:if>

    </tags:abstractContainer>

</cti:standardPage>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="ext" tagdir="/WEB-INF/tags/ext"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

	<cti:standardMenu menuSelection="meters" />

	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/meter/start" title="Metering" />
		<cti:crumbLink><i:inline key=".title" /></cti:crumbLink>
	</cti:breadCrumbs>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#errorHelp">
        <div class="mediumDialogScrollArea">
        <table id="errorCodes" class="resultsTable">
            <tr>
                <th><i:inline key=".errorCodesPopup.header.code" /></th>
                <th><i:inline key=".errorCodesPopup.header.porter" /></th>
            </tr>
            <c:forEach items="${allErrors}" var="error">
                <tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
                    <td nowrap="nowrap">${error.errorCode}</td>
                    <td>${error.description}</td>
                </tr>
            </c:forEach>
        </table><br>
        <span class="footerLink">
            <i:inline key=".errorCodesVerboseMsg"/>
            <a href="${fullErrorCodesURL}"><i:inline key=".errorCodesSupportLink"/></a>
        </span>
        </div>
    </i:simplePopup>

    <h2><i:inline key=".title" /></h2>
	<br>

	<%-- MAIN DETAILS --%>
	<tags:formElementContainer nameKey="sectionHeader">
		<tags:nameValueContainer2>

			<%-- monitor name --%>
			<tags:nameValue2 nameKey=".name">
				<spring:escapeBody htmlEscape="true">${monitorDto.name}</spring:escapeBody>
			</tags:nameValue2>

            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url var="deviceGroupUrl" value="/spring/group/editor/home">
                    <cti:param name="groupName">${monitorDto.groupName}</cti:param>
                </cti:url>
                <a href="${deviceGroupUrl}">${monitorDto.groupName}</a>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".attribute">
                <spring:escapeBody htmlEscape="true">${monitorDto.attribute.description}</spring:escapeBody>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".stateGroup">
                <spring:escapeBody htmlEscape="true">${monitorDto.stateGroup.stateGroupName}</spring:escapeBody>
            </tags:nameValue2>

			<%-- enable/disable monitoring --%>
			<tags:nameValue2 nameKey=".monitoring">
				<i:inline key="${monitorDto.evaluatorStatus}" />
			</tags:nameValue2>
		</tags:nameValueContainer2>

		<c:choose>
			<c:when test="${not empty monitorDto.rules}">
				<tags:boxContainer2 nameKey="rulesTable" id="rulesTable" styleClass="mediumContainer">
					<table class="compactResultsTable">
						<tr>
							<th><i:inline key=".rulesTable.header.ruleOrder" /></th>
							<th><i:inline key=".rulesTable.header.success" /></th>
							<th><i:inline key=".rulesTable.header.errors" />
                                <cti:img id="errorHelp" key="help" styleClass="pointer hoverableImage"/>
                            </th>
							<th><i:inline key=".rulesTable.header.matchStyle" /></th>
							<th><i:inline key=".rulesTable.header.state" /></th>
						</tr>

						<c:forEach items="${monitorDto.rules}" var="rule" varStatus="status">
							<tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
								<td nowrap="nowrap">${rule.value.ruleOrder}</td>
								<td nowrap="nowrap">${rule.value.success}</td>
								<td nowrap="nowrap">${rule.value.errorCodes}</td>
								<td nowrap="nowrap">${rule.value.matchStyle}</td>
								<td nowrap="nowrap">${states[rule.value.state].stateText}</td>
							</tr>
						</c:forEach>
					</table>
				</tags:boxContainer2>
			</c:when>
		</c:choose>

		<div class="pageActionArea">
			<form id="editMonitorForm" action="/spring/amr/porterResponseMonitor/editPage" method="get">
				<input type="hidden" name="monitorId" value="${monitorDto.monitorId}">
				<tags:slowInput2 formId="editMonitorForm" key="edit" />
			</form>
		</div>

	</tags:formElementContainer>
</cti:standardPage>
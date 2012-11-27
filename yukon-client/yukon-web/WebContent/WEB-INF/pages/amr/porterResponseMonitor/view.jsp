<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

    <script type="text/javascript">
        Event.observe(window, "load", function() {
            var totalGroupCountSpan = $('totalGroupCount');
            var supportedDevicesMsgSpan = $('supportedDevicesMsg');

            totalGroupCountSpan.addClassName('icon loading');
            supportedDevicesMsgSpan.addClassName('icon loading');
            $('supportedDevicesHelpIcon').hide();
    
            new Ajax.Request("getCounts",{
                parameters: {'monitorId': ${monitorDto.monitorId}},
                onComplete: function(transport) {
                    var json = transport.responseJSON;

                    var totalGroupCount = json.totalGroupCount;
                    var supportedDevicesMsg = json.supportedDevicesMessage;
                    var missingPointCount = json.missingPointCount;

                    totalGroupCountSpan.innerHTML = totalGroupCount;
                    supportedDevicesMsgSpan.innerHTML = supportedDevicesMsg;
                    
                    if (missingPointCount > 0) {
                        supportedDevicesMsgSpan.show();

                        if (${showAddRemovePoints}) {
                            $('addPointsSpan').show();
                        }
                    }

                    totalGroupCountSpan.removeClassName('icon loading');
                    supportedDevicesMsgSpan.removeClassName('icon loading');
                    $('supportedDevicesHelpIcon').show();
                }
            });
        });

        YEvent.observeSelectorClick('#supportedDevicesHelpIcon', function(event) {
            $('supportedDevicesHelpPopup').toggle();
        });
    </script>

    <cti:url var="fullErrorCodesURL" value="/support/errorCodes/view" />

    <i:simplePopup titleKey=".supportedDevicesHelpPopup" id="supportedDevicesHelpPopup">
        <spring:escapeBody htmlEscape="true">${supportedDevicesHelpText}</spring:escapeBody>
    </i:simplePopup>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#errorHelp">
        <div class="largeDialogScrollArea">
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

	<%-- MAIN DETAILS --%>
	<tags:formElementContainer nameKey="sectionHeader">
		<tags:nameValueContainer2>

			<%-- monitor name --%>
			<tags:nameValue2 nameKey=".name">
				<spring:escapeBody htmlEscape="true">${monitorDto.name}</spring:escapeBody>
			</tags:nameValue2>

            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url var="deviceGroupUrl" value="/group/editor/home">
                    <cti:param name="groupName">${monitorDto.groupName}</cti:param>
                </cti:url>
                <a href="${deviceGroupUrl}">${monitorDto.groupName}</a>
            </tags:nameValue2>

            <%-- Device Count --%>
            <tags:nameValue2 nameKey=".deviceCount">
                <span id="totalGroupCount"></span>&nbsp
            </tags:nameValue2>

            <%-- Supported Devices --%>
            <tags:nameValue2 nameKey=".supportedDevices">
                <span id="supportedDevicesMsg"></span>
                <span id="addPointsSpan" style="display: none;">
                <c:if test="${deviceCollection != null}">
                    <cti:url value="/bulk/addPoints/home" var="addPointsLink">
                        <cti:mapParam value="${deviceCollection.collectionParameters}"/>
                    </cti:url>
                    <span> - </span>
                    <a href="${addPointsLink}"><i:inline key=".addPoints"/></a>
                </c:if>
                </span>
                <cti:img id="supportedDevicesHelpIcon" nameKey="help" styleClass="pointer hoverableImage nameValueLogoImage" />&nbsp
            </tags:nameValue2>

			<%-- enable/disable monitoring --%>
			<tags:nameValue2 nameKey=".monitoring">
				<i:inline key="${monitorDto.evaluatorStatus}" />
			</tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:formElementContainer>
	<c:choose>
		<c:when test="${not empty monitorDto.rules}">
			<tags:boxContainer2 nameKey="rulesTable" id="rulesTable" styleClass="prmFixedViewTable">
				<table class="compactResultsTable">
					<tr>
						<th><i:inline key=".rulesTable.header.ruleOrder" /></th>
						<th><i:inline key=".rulesTable.header.outcome" /></th>
						<th><i:inline key=".rulesTable.header.errors" />
                            <cti:img id="errorHelp" nameKey="help" styleClass="pointer hoverableImage"/>
                        </th>
						<th><i:inline key=".rulesTable.header.matchStyle" /></th>
						<th><i:inline key=".rulesTable.header.state" /></th>
					</tr>

					<c:forEach items="${monitorDto.rules}" var="rule">
						<tr class="<tags:alternateRow odd="altTableCell" even="tableCell"/>">
							<td nowrap="nowrap">${rule.value.ruleOrder}</td>
							<td nowrap="nowrap">
                                <c:choose>
                                    <c:when test="${rule.value.success}">
                                        <span class="successMessage"><i:inline key=".rule.success"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="errorMessage"><i:inline key=".rule.failure"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
							<td nowrap="nowrap">${rule.value.errorCodes}</td>
							<td nowrap="nowrap"><i:inline key="${rule.value.matchStyle.formatKey}"/></td>
							<td nowrap="nowrap">${states[rule.value.state].stateText}</td>
						</tr>
					</c:forEach>
				</table>
			</tags:boxContainer2>
		</c:when>
	</c:choose>

	<div class="pageActionArea">
		<form id="editMonitorForm" action="/amr/porterResponseMonitor/editPage" method="get">
			<input type="hidden" name="monitorId" value="${monitorDto.monitorId}">
            <cti:button nameKey="edit" type="submit" styleClass="f_blocker"/>
		</form>
	</div>

</cti:standardPage>
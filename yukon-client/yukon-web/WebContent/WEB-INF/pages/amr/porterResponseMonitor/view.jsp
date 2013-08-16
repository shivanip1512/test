<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

    <script type="text/javascript">
        jQuery(function () {
            var totalGroupCountSpan = jQuery('#totalGroupCount');
            var supportedDevicesMsgSpan = jQuery('#supportedDevicesMsg');

            totalGroupCountSpan.addClass('icon icon-spinner');
            supportedDevicesMsgSpan.addClass('icon icon-spinner');
            jQuery('#supportedDevicesHelpIcon').hide();
    
            jQuery.get(
                'counts', 
                {'monitorId': '${monitorDto.monitorId}'}
            ).done(function (json) {
                var totalGroupCount = json.totalGroupCount;
                var supportedDevicesMsg = json.supportedDevicesMessage;
                var missingPointCount = json.missingPointCount;
                
                totalGroupCountSpan.html(totalGroupCount);
                supportedDevicesMsgSpan.html(supportedDevicesMsg);
                
                if (missingPointCount > 0) {
                  supportedDevicesMsgSpan.show();
                
                  if (${showAddRemovePoints}) {
                      $('addPointsSpan').show();
                  }
                }
                
                totalGroupCountSpan.removeClass('icon icon-spinner');
                supportedDevicesMsgSpan.removeClass('icon icon-spinner');
                jQuery('#supportedDevicesHelpIcon').show();
            });
            
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

                    totalGroupCountSpan.removeClassName('icon icon-spinner');
                    supportedDevicesMsgSpan.removeClassName('icon icon-spinner');
                    $('supportedDevicesHelpIcon').show();
                }
            });
        });

    </script>

    <i:simplePopup titleKey=".supportedDevicesHelpPopup" id="supportedDevicesHelpPopup" on="#supportedDevicesHelpIcon" options="{width:600}">
        <p>${fn:escapeXml(supportedDevicesHelpText)}</p>
    </i:simplePopup>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#errorHelp">
        <div class="scrollingContainer_large">
            <table id="errorCodes" class="compactResultsTable stacked">
                <thead>
                    <tr>
                        <th><i:inline key=".errorCodesPopup.header.code" /></th>
                        <th><i:inline key=".errorCodesPopup.header.porter" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${allErrors}" var="error">
                        <tr>
                            <td>${error.errorCode}</td>
                            <td>${error.description}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <span class="footerLink">
                <i:inline key=".errorCodesVerboseMsg"/>
                <a href="<cti:url value="/support/errorCodes/view"/>"><i:inline key=".errorCodesSupportLink"/></a>
            </span>
        </div>
    </i:simplePopup>

	<%-- MAIN DETAILS --%>
	<tags:formElementContainer nameKey="sectionHeader">
		<tags:nameValueContainer2>

			<%-- monitor name --%>
			<tags:nameValue2 nameKey=".name">${fn:escapeXml(monitorDto.name)}</tags:nameValue2>

            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url var="deviceGroupUrl" value="/group/editor/home">
                    <cti:param name="groupName">${monitorDto.groupName}</cti:param>
                </cti:url>
                <a href="${deviceGroupUrl}">${monitorDto.groupName}</a>
            </tags:nameValue2>

            <%-- Device Count --%>
            <tags:nameValue2 nameKey=".deviceCount">
                <span id="totalGroupCount"></span>
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
                <cti:icon id="supportedDevicesHelpIcon" nameKey="help" icon="icon-help" classes="fn cp"/>
            </tags:nameValue2>

			<%-- enable/disable monitoring --%>
			<tags:nameValue2 nameKey=".monitoring">
				<i:inline key="${monitorDto.evaluatorStatus}"/>
			</tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:formElementContainer>
	<c:choose>
		<c:when test="${not empty monitorDto.rules}">
			<tags:boxContainer2 nameKey="rulesTable" id="rulesTable">
				<table class="compactResultsTable">
                    <thead>
    					<tr>
    						<th><i:inline key=".rulesTable.header.ruleOrder" /></th>
    						<th><i:inline key=".rulesTable.header.outcome" /></th>
    						<th><i:inline key=".rulesTable.header.errors" />
                                <cti:icon id="errorHelp" nameKey="help" icon="icon-help" classes="fn cp"/>
                            </th>
    						<th><i:inline key=".rulesTable.header.matchStyle" /></th>
    						<th><i:inline key=".rulesTable.header.state" /></th>
    					</tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
    					<c:forEach items="${monitorDto.rules}" var="rule">
    						<tr>
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
                    </tbody>
				</table>
			</tags:boxContainer2>
		</c:when>
	</c:choose>

	<div class="pageActionArea">
		<form id="editMonitorForm" action="/amr/porterResponseMonitor/editPage" method="get">
			<input type="hidden" name="monitorId" value="${monitorDto.monitorId}">
            <cti:button nameKey="edit" icon="icon-pencil" type="submit" classes="f-blocker"/>
		</form>
	</div>

</cti:standardPage>
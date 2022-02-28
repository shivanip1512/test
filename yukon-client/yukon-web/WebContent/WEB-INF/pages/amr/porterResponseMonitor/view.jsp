<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="porterResponseMonitor.${mode}">

<script type="text/javascript">
$(function () {
    var totalGroupCountSpan = $('#totalGroupCount');
    var supportedDevicesMsgSpan = $('#supportedDevicesMsg');

    totalGroupCountSpan.addClass('icon icon-spinner');
    supportedDevicesMsgSpan.addClass('icon icon-spinner');
    $('#supportedDevicesHelpIcon').hide();

    $.get('counts', {'monitorId': '${monitorDto.monitorId}'}).done(function(json) {
        var totalGroupCount = json.totalGroupCount;
        var supportedDevicesMsg = json.supportedDevicesMessage;
        var missingPointCount = json.missingPointCount;
        
        totalGroupCountSpan.html(totalGroupCount);
        supportedDevicesMsgSpan.html(supportedDevicesMsg);
        
        if (missingPointCount > 0) {
          supportedDevicesMsgSpan.show();
        
          if (${showAddRemovePoints}) {
              $('#addPointsSpan').show();
          }
        }
        
        totalGroupCountSpan.removeClass('icon icon-spinner');
        supportedDevicesMsgSpan.removeClass('icon icon-spinner');
        $('#supportedDevicesHelpIcon').show();
    });
    
});
</script>

    <i:simplePopup titleKey=".supportedDevicesHelpPopup" id="supportedDevicesHelpPopup" on="#supportedDevicesHelpIcon" options="{width:600}">
        <p>${fn:escapeXml(supportedDevicesHelpText)}</p>
    </i:simplePopup>

    <i:simplePopup titleKey=".errorCodesPopup" id="errorCodesHelpPopup" on="#errorHelp">
        <div class="scroll-lg">
            <table id="errorCodes" class="compact-results-table stacked">
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
    
    <tags:sectionContainer2 nameKey="sectionHeader" styleClass="stacked">
        <tags:nameValueContainer2 tableClass="has-actions">
        
            <%-- monitor name --%>
            <tags:nameValue2 nameKey=".name">${fn:escapeXml(monitorDto.name)}</tags:nameValue2>
            
            <%-- Device Count --%>
            <tags:nameValue2 nameKey=".monitoring">
                <span id="totalGroupCount"></span>
            </tags:nameValue2>
            
            <tags:nameValueGap2 gapHeight="20px"/>
            
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
                <cti:icon id="supportedDevicesHelpIcon" nameKey="help" icon="icon-help" classes="fn cp vatt show-on-hover"/>
            </tags:nameValue2>
            
            <tags:nameValueGap2 gapHeight="20px"/>
            
            <tags:nameValue2 nameKey=".deviceGroup">
                <cti:url var="deviceGroupUrl" value="/group/editor/home">
                    <cti:param name="groupName">${monitorDto.groupName}</cti:param>
                </cti:url>
                <a href="${deviceGroupUrl}" >${fn:escapeXml(monitorDto.groupName)}</a>
                <cm:dropdown triggerClasses="vv">
                    <cti:url var="groupReportUrl" value="/amr/reports/groupDevicesReport">
                        <cti:param name="groupName" value="${monitorDto.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-folder-explore" key="yukon.web.components.button.view.label" href="${groupReportUrl}"/>
                    <cti:url var="mapUrl" value="/tools/map/dynamic">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${monitorDto.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-map" key="yukon.web.components.button.map.label" href="${mapUrl}"/>
                    <cti:url var="collectionActionUrl" value="/bulk/collectionActions" htmlEscape="true">
                        <cti:param name="collectionType" value="group"/>
                        <cti:param name="group.name" value="${monitorDto.groupName}"/>
                    </cti:url>
                    <cm:dropdownOption icon="icon-cog-go" key="yukon.web.components.button.collectionAction.label" href="${collectionActionUrl}"/>
                </cm:dropdown>
            </tags:nameValue2>
            
            <%-- enable/disable monitoring --%>
            <c:if test="${monitorDto.enabled}"><c:set var="clazz" value="success"/></c:if>
            <c:if test="${!monitorDto.enabled}"><c:set var="clazz" value="error"/></c:if>
            <tags:nameValue2 nameKey=".status" valueClass="${clazz}">
                <i:inline key="${monitorDto.evaluatorStatus}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </tags:sectionContainer2>
    
    <c:if test="${not empty monitorDto.rules}">
        <tags:sectionContainer2 nameKey="rulesTable" id="rulesTable">
            <table class="compact-results-table dashed">
                <thead>
                    <tr>
                        <th><i:inline key=".rulesTable.header.ruleOrder" /></th>
                        <th><i:inline key=".rulesTable.header.outcome" /></th>
                        <th>
                            <i:inline key=".rulesTable.header.errors" />
                            <cti:icon id="errorHelp" nameKey="help" icon="icon-help" classes="fn cp vatt show-on-hover"/>
                        </th>
                        <th><i:inline key=".rulesTable.header.matchStyle" /></th>
                        <th><i:inline key=".rulesTable.header.state" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach items="${monitorDto.rules}" var="rule">
                        <tr>
                            <td>${rule.ruleOrder}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${rule.success}">
                                        <span class="success"><i:inline key=".rule.success"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="error"><i:inline key=".rule.failure"/></span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>${rule.errorCodes}</td>
                            <td><i:inline key="${rule.matchStyle.formatKey}"/></td>
                            <td>${states[rule.state].stateText}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </tags:sectionContainer2>
    </c:if>
    
    <div class="page-action-area">
        <cti:url var="editPageUrl" value="/amr/porterResponseMonitor/editPage"/>
        <form id="editMonitorForm" action="${editPageUrl}" method="get">
            <input type="hidden" name="monitorId" value="${monitorDto.monitorId}">
            <cti:button nameKey="edit" icon="icon-pencil" type="submit" classes="js-blocker"/>
        </form>
    </div>

</cti:standardPage>
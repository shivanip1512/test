<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msgScope paths="modules.amr.meterProgramming.summary,modules.amr.meterProgramming">

    <td><cti:paoDetailUrl yukonPao="${result.device}">${fn:escapeXml(result.device.name)}</cti:paoDetailUrl></td>
    <td>${fn:escapeXml(result.meterNumber)}</td>
    <td>${result.device.paoIdentifier.paoType.paoTypeName}</td>
    <td>${fn:escapeXml(result.programInfo.name)}
        <c:if test="${result.programInfo.source.isOldFirmware()}">
            <cti:icon icon="icon-help" data-popup="#firmware-help" classes="fn cp ML0 vam"/>
            <cti:msg2 var="helpTitle" key=".oldFirmware.helpTitle"/>
            <div id="firmware-help" class="dn" data-dialog data-cancel-omit="true" data-title="${helpTitle}"><cti:msg2 key=".oldFirmware.helpText"/></div>
        </c:if>
    </td>
    <td>
        <c:set var="programName" value="${!empty result.assignedProgramName ? result.assignedProgramName : ''}"/>
        <cti:msg2 key=".statusMsg.${result.status}" argument="${programName}"/>
        <c:if test="${result.displayProgressBar()}">
            <tags:updateableProgressBar totalCount="100" countKey="METER_PROGRAMMING/${result.device.id}/PROGRESS" hideCount="true"/>
            <cti:dataUpdaterCallback function="yukon.ami.meterProgramming.summary.refreshCheck(${result.device.id})" 
                isInProgress="METER_PROGRAMMING/${result.device.id}/IS_IN_PROGRESS"/>
        </c:if>
        <c:if test="${result.confirming}">
            <cti:dataUpdaterCallback function="yukon.ami.meterProgramming.summary.refreshCheck(${result.device.id})" 
                isConfirming="METER_PROGRAMMING/${result.device.id}/IS_CONFIRMING"/>
        </c:if>
    </td>
    <td>
        <c:choose>
            <c:when test="${result.displayProgressBar()}">
                <cti:dataUpdaterValue type="METER_PROGRAMMING" identifier="${result.device.id}/LAST_UPDATED"/>
            </c:when>
            <c:otherwise>
                <cti:formatDate type="BOTH" value="${result.lastUpdate}"/>
            </c:otherwise>
        </c:choose>
    </td>
    <td>
        <c:if test="${result.displayCancel() || result.displayRead() || result.displaySend() || result.displayAccept()}">
            <cm:dropdown icon="icon-cog">
                <c:if test="${result.displayCancel()}">
                    <cm:dropdownOption icon="icon-cross" key=".cancel" classes="js-cancel" data-id="${result.device.id}" data-guid="${result.assignedGuid}"/>
                </c:if>
                <c:if test="${result.displayRead()}">
                    <cm:dropdownOption icon="icon-read" key=".read" classes="js-read" data-id="${result.device.id}"/>
                </c:if>
                <c:if test="${result.displaySend()}">
                    <cm:dropdownOption icon="icon-control-repeat-blue" key=".resend" classes="js-resend" data-id="${result.device.id}" data-guid="${result.assignedGuid}"/>
                </c:if>
                <c:if test="${result.displayAccept()}">
                    <cm:dropdownOption icon="icon-accept" key=".accept" classes="js-accept" data-id="${result.device.id}" data-guid="${result.programInfo.guid}"/>
                </c:if>
            </cm:dropdown>
        </c:if>
    </td>

</cti:msgScope>

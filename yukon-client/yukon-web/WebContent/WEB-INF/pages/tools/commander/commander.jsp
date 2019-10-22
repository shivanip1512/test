<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="commander">
<cti:includeScript link="/resources/js/pages/yukon.tools.commander.js"/>
<style>
.console { min-height: 200px; }
.console .title-bar {
    border-bottom: 1px dashed #ccc;
    margin-bottom: 4px;
    padding-bottom: 4px;
    line-height: 26px;
}
.console .title-bar .title {
    display: inline-block;
    margin: 0;
}
.console .title-bar .button {
    float: right;
}
.cmd-req-resp { margin: 10px 0; }
.cmd-req { color: #222; }
.cmd-resp-success { color: #1f4cff; }
.cmd-resp-fail { color: #d14836; }
.cmd-resp-warn { color: #fb8521; }
.cmd-pending { margin-top: 4px; }
</style>

<%-- Commander Settings POPUP --%>
<div class="dn js-settings-popup"
    data-dialog
    data-title="Settings"
    data-url="<cti:url value="editSettingsPopup"/>"
    data-event="yukon:tools:commander:popup"></div>
    
<tags:nameValueContainer2 tableClass="with-form-controls stacked">
    
    <tags:nameValue2 nameKey=".target" rowId="target-row">
        <div class="button-group button-group-toggle">
            <c:set var="clazz" value="${target == 'DEVICE' ? 'on' : ''}"/>
            <cti:button id="target-device-btn" icon="icon-database-add" 
                    nameKey="device" data-show="#device-row" classes="${clazz} M0"/>
            <c:set var="clazz" value="${target == 'LOAD_GROUP' ? 'on' : ''}"/>
            <cti:button id="target-lm-group-btn" icon="icon-database-add" 
                    nameKey="loadGroup" data-show="#load-group-row" classes="${clazz}"/>
            <c:set var="clazz" value="${target == 'EXPRESSCOM' ? 'on' : ''}"/>
            <cti:button id="target-expresscom-btn" icon="icon-textfield" nameKey="expressCom" 
                    data-show="#serial-number-row, #route-row" data-type="EXPRESSCOM_SERIAL" classes="${clazz}"/>
            <c:set var="clazz" value="${target == 'VERSACOM' ? 'on' : ''}"/>
            <cti:button id="target-versacom-btn" icon="icon-textfield" nameKey="versaCom" 
                    data-show="#serial-number-row, #route-row" data-type="VERSACOM_SERIAL" classes="${clazz}"/>
        </div>
    </tags:nameValue2>
    
    <c:set var="clazz" value="${target != 'DEVICE' ? 'dn' : ''}"/>
    <c:set var="deviceChosen" value="${target == 'DEVICE' and not empty paoId ? true : false}"/>
    
    <tags:nameValue2 nameKey=".device" rowId="device-row" rowClass="${clazz}">
    
        <input id="pao-id" type="hidden" name="paoId" value="${deviceChosen ? paoId : ''}">
        
        <tags:pickerDialog type="commanderDevicePicker" id="commanderDevicePicker"
            buttonStyleClass="js-device-picker"
            linkType="selection"
            selectionProperty="paoName"
            destinationFieldId="pao-id"
            immediateSelectMode="true"
            endAction="yukon.tools.commander.deviceChosen"/>
            
        <c:set var="clazz" value="${empty route ? ' dn' : ''}"/>
        <span class="js-on-route${clazz}" <c:if test="${not empty route}">data-route-id="${route.liteID}"</c:if>>
            <span class="name"><i:inline key=".onRoute"/></span>
            <span class="value">${fn:escapeXml(route.paoName)}</span>
            <div data-dialog id="change-route-dialog" title="<cti:msg2 key=".changeRoute"/>" data-width="500" 
                    data-event="yukon.tools.commander.routeChange" class="dn"></div>
        </span>
        
        <cm:dropdown type="button" id="device-actions-menu" triggerClasses="js-device-actions-btn vab" showLabel="false">
            <c:set var="clazz" value="${!routable ? 'dn' : ''}"/>
            <c:if test="${changeRoute}">
                <cm:dropdownOption id="change-route-btn" key=".changeRoute" icon="icon-pencil" classes="${clazz}"/>
            </c:if>
            <c:if test="${deviceChosen}">
                <cti:url var="viewMeterUrl" value="/meter/home">
                    <cti:param name="deviceId" value="${paoId}"/>
                </cti:url>
                <cti:url var="otherActionsUrl" value="/bulk/collectionActions">
                    <cti:param name="collectionType" value="idList"/>
                    <cti:param name="idList.ids" value="${paoId}"/>
                </cti:url>
            </c:if>
            <cm:dropdownOption id="readings-btn" key=".readings" icon="icon-table"/>
            <c:set var="clazz" value="${!meter ? 'dn' : ''}"/>
            <cm:dropdownOption id="view-meter-detail-btn" key=".viewMeter" icon="icon-layout-go" 
                    href="${viewMeterUrl}" classes="${clazz}" newTab="true"/>
            <cm:dropdownOption id="other-actions-btn" key=".otherActions" icon="icon-cog-go" 
                    href="${otherActionsUrl}"  newTab="true"/>
        </cm:dropdown>
        
    </tags:nameValue2>
    
    <c:set var="clazz" value="${!target.serialNumber ? 'dn' : ''}"/>
    <tags:nameValue2 nameKey=".serialNumber" rowId="serial-number-row" rowClass="${clazz}">
        <input id="serial-number" type="text" name="serialNumber" value="${serialNumber}">
    </tags:nameValue2>
    
    <c:set var="clazz" value="${!target.route ? 'dn' : ''}"/>
    <tags:nameValue2 nameKey=".route" rowId="route-row" rowClass="${clazz}">
        <select id="route-id" name="routeId">
            <c:forEach var="route" items="${routes}">
                <c:set var="selected" value="${routeId == route.paoIdentifier.paoId ? ' selected' : ''}"/>
                <option value="${route.paoIdentifier.paoId}"${selected}>${fn:escapeXml(route.paoName)}</option>
            </c:forEach>
        </select>
    </tags:nameValue2>
    
    <c:set var="clazz" value="${target != 'LOAD_GROUP' ? 'dn' : ''}"/>
    <tags:nameValue2 nameKey=".loadGroup" rowId="load-group-row" rowClass="${clazz}">
        <input id="lm-group-id" type="hidden" name="lmGroupId" value="${target == 'LOAD_GROUP' and not empty paoId ? paoId : ''}">
        <tags:pickerDialog type="lmGroupPaoPermissionCheckingPicker" id="lmGroupPicker"
            buttonStyleClass="js-lm-group-picker"
            linkType="selection"
            selectionProperty="paoName"
            destinationFieldId="lm-group-id"
            immediateSelectMode="true"
            initialId="${paoId}"
            endAction="yukon.tools.commander.lmGroupChosen"/>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".availableCommands">
        <select id="common-commands" data-placeholder="<cti:msg2 key=".availableCommands.select"/>">
            <option value=""><cti:msg2 key=".availableCommands.select"/></option>
        </select>
        <cti:button id="custom-commands" renderMode="buttonImage" icon="icon-script-edit" classes="fn vam"/>
        <cti:msg2 var="customCommandsTitle" key=".customCommands.title"/>
        <div class="dn" id="custom-commands-popup" data-dialog data-title="${customCommandsTitle}"></div>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".command">
        <c:if test="${!executeManualCommand}">
            <input id="command-text" type="text" size="60" tabindex="0" readOnly>
        </c:if>
        <c:if test="${executeManualCommand}">
            <input id="command-text" type="text" size="60" tabindex="0">
        </c:if>  
        <cti:button id="cmd-execute-btn" nameKey="execute" classes="primary action fn vat"/>
        <cti:button id="settings-btn" nameKey="settings" classes="fn" data-popup=".js-settings-popup" data-popup-toggle=""/>
    </tags:nameValue2>
    
</tags:nameValueContainer2>

	<div id="commander-console" class="console lite-container stacked code">
    <div class="title-bar clearfix">
        <h2 class="title"><i:inline key="yukon.common.console"/></h2>
        <cti:button id="scroll-lock-btn" icon="icon-lock" renderMode="buttonImage" classes="M0"/>
        <cti:button id="clear-console-btn" nameKey="clear" classes="right"/>
        <cti:button id="select-console-btn" nameKey="selectAll" classes="left"/>
        <cti:button id="refresh-console-btn" nameKey="refresh" classes="left"/>
    </div>
    <div id="commander-results" class="content scroll-lg">
        <c:forEach var="req" items="${requests}">
            <div class="cmd-req-resp" data-request-id="${req.id}">
                <div class="cmd-req">${fn:escapeXml(req.requestText)}</div>
                <c:forEach var="resp" items="${req.responses}">
                    <c:if test="${resp.type == 'SUCCESS'}"><c:set var="clazz" value="cmd-resp-success"/></c:if>
                    <c:if test="${resp.type == 'ERROR'}"><c:set var="clazz" value="cmd-resp-fail"/></c:if>
                    <c:if test="${resp.type == 'INHIBITED'}"><c:set var="clazz" value="cmd-resp-warn"/></c:if>
                    <div class="${clazz}" data-response-id="${resp.id}">
                        <c:forEach var="result" items="${resp.results}">
                            <div>${fn:escapeXml(result)}</div>
                        </c:forEach>
                    </div>
                </c:forEach>
                <c:if test="${!req.complete}">
                    <div class="cmd-pending clearfix"><cti:icon icon="icon-loading-bars"/></div>
                </c:if>
            </div>
        </c:forEach>
    </div>
</div>

<cti:toJson id="intial-requests" object="${requests}"/>

<!-- Template elements for js cloning. -->
<div id="cmdr-templates" class="dn">
    <div class="cmd-pending clearfix"><cti:icon icon="icon-loading-bars"/></div>
    <cm:dropdownOption icon="icon-database-add">placeholder</cm:dropdownOption>
</div>

<div id="device-readings-popup" class="dn" data-height="400"></div>

<div data-dialog id="prompt-dialog" class="dn"
        data-title="<cti:msg2 key=".prompt"/>"
        data-event="yukon.tools.commander.user.input"
        data-position-my="left top+2"
        data-position-at="left bottom"
        data-position-of="#command-text">
    <table class="name-value-table">
        <thead></thead>
        <tfoot></tfoot>
        <tbody>
            <tr>
                <td class="name js-prompt-text"></td>
                <td class="value">
                    <input class="js-prompt-input" type="text">
                </td>
            </tr>
        </tbody>
    </table>
</div>

<div id="page-buttons" class="dn">
<c:set var="clazz" value="${empty recentTargets ? 'js-recent-btn dn' : 'js-recent-btn'}"/>
<cm:dropdown key=".recent" type="button" triggerClasses="vab ${clazz}" icon="icon-time" menuClasses="js-recent-menu">
    <c:if test="${not empty recentTargets}">
        <c:forEach var="target" items="${recentTargets}">
            <c:set var="label" value="${fn:escapeXml(target.label)}"/>
            <c:set var="type" value="${target.target.target}"/>
            <c:set var="icon" value="${type == 'DEVICE' || type == 'LOAD_GROUP' ? 'icon-database-add' : 'icon-textfield'}"/>
            <cm:dropdownOption icon="${icon}"
                data-type="${type}"
                data-pao-id="${target.target.paoId}"
                data-route-id="${target.target.routeId}"
                data-serial-number="${target.target.serialNumber}">${label}</cm:dropdownOption>
        </c:forEach>
    </c:if>
</cm:dropdown>
</div>

</cti:standardPage>
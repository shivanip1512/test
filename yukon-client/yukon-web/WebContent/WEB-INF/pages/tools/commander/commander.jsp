<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="commander">
<cti:includeScript link="/JavaScript/yukon.tools.commander.js"/>

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
    margin: 0;
}
.cmd-req-resp { margin: 10px 0; }
.cmd-req { color: #222; }
.cmd-resp-success { color: #1f4cff; }
.cmd-resp-fail { color: #d14836; }
.cmd-resp-warn { color: #fb8521; }
.cmd-pending { margin-top: 4px; }
</style>

<tags:nameValueContainer2 tableClass="with-form-controls stacked">
    
    <tags:nameValue2 nameKey=".target" rowId="target-row">
        <div class="button-group button-group-toggle">
            <cti:button id="target-device-btn" icon="icon-database-add" 
                    nameKey="device" data-show="#device-row" classes="on M0"/>
            <cti:button id="target-lm-group-btn" icon="icon-database-add" 
                    nameKey="loadGroup" data-show="#load-group-row"/>
            <cti:button id="target-expresscom-btn" icon="icon-textfield" nameKey="expressCom" 
                    data-show="#serial-number-row, #route-row" data-type="EXPRESSCOM_SERIAL"/>
            <cti:button id="target-versacom-btn" icon="icon-textfield" nameKey="versaCom" 
                    data-show="#serial-number-row, #route-row" data-type="VERSACOM_SERIAL"/>
        </div>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".device" rowId="device-row">
        <input id="pao-id" type="hidden" name="paoId">
        <tags:pickerDialog type="commanderDevicePicker" id="commanderDevicePicker"
            buttonStyleClass="js-device-picker"
            linkType="selection"
            selectionProperty="paoName"
            destinationFieldId="pao-id"
            immediateSelectMode="true"
            endAction="yukon.tools.commander.deviceChosen"/>
        <span class="js-on-route dn">
            <span class="name"><i:inline key=".onRoute"/></span>
            <span class="value"></span>
            <cti:button id="change-route-btn" nameKey="change" icon="icon-pencil" classes="fn vat"/>
            <div data-dialog id="change-route-dialog" title="<cti:msg2 key=".changeRoute"/>" data-width="500" 
                    data-event="yukon.tools.commander.routeChange" class="dn"></div>
        </span>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".serialNumber" rowId="serial-number-row" rowClass="dn">
        <input id="serial-number" type="text" name="serialNumber">
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".route" rowId="route-row" rowClass="dn">
        <select id="route-id" name="routeId">
            <c:forEach var="route" items="${routes}">
                <option value="${route.paoIdentifier.paoId}">${fn:escapeXml(route.paoName)}</option>
            </c:forEach>
        </select>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".loadGroup" rowId="load-group-row" rowClass="dn">
        <input id="lm-group-id" type="hidden" name="lmGroupId">
        <tags:pickerDialog type="lmGroupPaoPermissionCheckingPicker" id="lmGroupPicker"
            buttonStyleClass="js-lm-group-picker"
            linkType="selection"
            selectionProperty="paoName"
            destinationFieldId="lm-group-id"
            immediateSelectMode="true"
            endAction="yukon.tools.commander.lmGroupChosen"/>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".availableCommands">
        <select id="common-commands" data-placeholder="<cti:msg2 key=".availableCommands.select"/>">
            <option value=""><cti:msg2 key=".availableCommands.select"/></option>
        </select>
    </tags:nameValue2>
    
    <tags:nameValue2 nameKey=".command">
        <input id="command-text" type="text" size="60" tabindex="0">
        <cti:button id="cmd-execute-btn" nameKey="execute" classes="primary action fn vat"/>
    </tags:nameValue2>
    
</tags:nameValueContainer2>

<div id="commander-console" class="console lite-container stacked code">
    <div class="title-bar clearfix">
        <h2 class="title"><i:inline key="yukon.common.console"/></h2>
        <cti:button id="clear-console-btn" nameKey="clear" classes="right"/>
        <cti:button id="select-console-btn" nameKey="selectAll" classes="left"/>
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
<div id="cmd-templates" class="dn">
    <div class="cmd-pending clearfix"><cti:icon icon="icon-loading-bars"/></div>
</div>

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

</cti:standardPage>
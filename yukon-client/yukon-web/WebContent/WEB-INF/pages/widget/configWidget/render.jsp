<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<style type="text/css">
.padBottom {
    padding-bottom:20px;
}
</style>

<div class="js-config-widget" data-device-id="${deviceId}">

    <c:if test="${not empty userMessage}">
        <tags:alertBox type="success" includeCloseButton="true">${userMessage}</tags:alertBox>
    </c:if>
    <c:set var="errorClass" value="${not empty errorMessage ? '' : 'dn'}"/>
    <tags:alertBox classes="js-error-msg ${errorClass}" includeCloseButton="true">${errorMessage}</tags:alertBox>
    
    <!-- Device Configuration -->
    <c:if test="${configurableDevice}">
        <tags:nameValueContainer2 tableClass="spaced-form-controls">
            <tags:nameValue2 nameKey=".currentConfiguration" nameColumnWidth="160px">
                ${fn:escapeXml(currentConfigName)}
                <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                    <c:if test="${not empty currentConfigId}">
                        <tags:widgetActionRefresh method="removeConfig" nameKey="remove" renderMode="image" classes="fn vam js-config-action-btns" icon="icon-cross"/>
                    </c:if>
                </cti:checkRolesAndProperties>
            </tags:nameValue2>
    
            <c:if test="${not empty currentConfigId}">
                <tags:nameValue2 nameKey=".status" rowClass="js-status-row">
                    <span class="js-status"></span>
                    <cti:msg2 var="actionsDisabledMessage" key=".actionsDisabledMessage"/>
                    <input id="actionsDisabledMessage" type="hidden" value="${actionsDisabledMessage}"/>
                    <div class="dn js-out-of-sync-popup" data-dialog data-cancel-omit="true" data-title="<cti:msg2 key="yukon.web.modules.tools.configs.summary.outOfSync"/>" 
                        data-width="600"></div>
                </tags:nameValue2>
                
                <tags:nameValue2 nameKey=".action">
                    <div class="button-group" style="vertical-align:unset;">
                        <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                            <tags:widgetActionRefresh method="uploadConfig" nameKey="upload" container="${widgetParameters.widgetId}_config_results" classes="M0 js-config-action-btns"/>
                            <tags:widgetActionRefresh method="validateConfig" nameKey="validate" container="${widgetParameters.widgetId}_config_results" classes="M0 js-config-action-btns"/>
                        </cti:checkRolesAndProperties>
                    </div>
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2>
    
        <tags:nameValueContainer2 tableClass="spaced-form-controls">
            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                <tags:nameValue2 nameKey=".changeConfiguration" nameColumnWidth="160px">
                    <select id="configuration" name="configuration" style="max-width: 200px;">
                        <c:forEach var="config" items="${existingConfigs}">
                            <option value="${config.configurationId}" <c:if test="${config.configurationId == currentConfigId}">selected</c:if>>${fn:escapeXml(config.name)}</option>
                        </c:forEach>
                    </select>
                    <cti:button nameKey="change" classes="js-change-config js-config-action-btns fn vam" data-device-id="${deviceId}"/>
                    <cti:msg2 var="uploadTitle" key=".uploadPopup.title"/>
                    <cti:msg2 var="uploadButton" key=".upload.label"/>
                    <div id="uploadPopup" data-dialog class="dn" data-title="${uploadTitle}" data-upload-btn="${uploadButton}">
                        <span class=js-upload-msg></span>
                    </div>
                </tags:nameValue2>
            </cti:checkRolesAndProperties>
        </tags:nameValueContainer2>
        <c:if test="${displayUploadPopup}">
            <div data-dialog></div>
        </c:if>
    </c:if>
    
    <!-- Data Streaming Configuration -->
    <cti:checkLicenseKey keyName="RF_DATA_STREAMING_ENABLED">
        <c:if test="${streamableDevice}">
            <cti:msg var="dataStreamingTitle" key="yukon.web.widgets.configWidget.dataStreaming"/>
            <tags:sectionContainer title="${dataStreamingTitle}">
                <tags:nameValueContainer2 tableClass="spaced-form-controls">
                    <tags:nameValue2 nameKey=".currentDataStreaming" valueClass="full-width dib" nameClass="wsnw">
                        <c:choose>
                            <c:when test="${dataStreamingConfig != null}">
                                <a href="javascript:void(0);" data-popup="#data-streaming-popup">
                                    ${fn:escapeXml(dataStreamingConfig.name)}
                                    <cti:url var="discrepancyUrl" value="/tools/dataStreaming/discrepancies"/>
                                    <cti:msg2 var="viewDiscrepancy" key="yukon.web.modules.tools.dataStreaming.discrepancies.viewDiscrepancy"/>
                                    <cti:icon icon="icon-error fn ML0" href="${discrepancyUrl}" title="${viewDiscrepancy}"/>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <i:inline key="yukon.common.none.choice"/>
                            </c:otherwise>
                        </c:choose>
                    </tags:nameValue2>
                    <cti:checkRolesAndProperties value="RF_DATA_STREAMING">
                        <tags:nameValue2 nameKey=".action">
                            <cti:url var="configureUrl" value="/bulk/dataStreaming/configure">
                                <cti:param name="collectionType" value="idList"/>
                                <cti:param name="idList.ids" value="${deviceId}"/>
                            </cti:url>
                            <cti:url var="removeUrl" value="/bulk/dataStreaming/remove">
                                <cti:param name="collectionType" value="idList"/>
                                <cti:param name="idList.ids" value="${deviceId}"/>
                            </cti:url>
                            <c:if test="${dataStreamingConfig != null}">
                                <div class="button-group">
                                    <cti:button nameKey="configure" href="${configureUrl}" classes="M0"/>
                                    <cti:button nameKey="remove" href="${removeUrl}" classes="M0"/>
                                </div>
                            </c:if>
                            <c:if test="${dataStreamingConfig == null}">
                                <cti:button nameKey="configure" href="${configureUrl}" classes="M0"/>
                            </c:if>
                        </tags:nameValue2>
                    </cti:checkRolesAndProperties>
                </tags:nameValueContainer2>
            </tags:sectionContainer>
        </c:if>
    
        <div id="data-streaming-popup" data-width="400" data-title="<cti:msg2 key=".dataStreamingConfiguration"/>" class="dn">
            <c:set var="config" value="${dataStreamingConfig}"/>
            <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
        </div>
    </cti:checkLicenseKey>
    
    <!-- Meter Programming -->
    <c:if test="${showMeterProgramming}">
        <cti:msg var="meterProgrammingTitle" key="yukon.web.widgets.configWidget.meterProgramming"/>
        <tags:sectionContainer title="${meterProgrammingTitle}">
            <tags:nameValueContainer2 tableClass="spaced-form-controls">
                <c:choose>
                    <c:when test="${!empty meterProgram}">
                        <tags:nameValue2 nameKey=".currentMeterProgram">
                            <cti:url var="summaryUrl" value="/amr/meterProgramming/summary">
                                <cti:param name="programs[0].guid" value="${meterProgram.programInfo.guid}"/>
                                <cti:param name="programs[0].name" value="${meterProgram.programInfo.name}"/>
                                <cti:param name="programs[0].source" value="${meterProgram.programInfo.source}"/>
                            </cti:url>
                            <a href="${summaryUrl}">${fn:escapeXml(meterProgram.programInfo.name)}</a>
                        </tags:nameValue2>
                    </c:when>
                    <c:otherwise>
                        <tags:nameValue2 nameKey=".currentMeterProgram">
                            <i:inline key="yukon.common.none.choice"/>
                            <cti:url var="programUrl" value="/bulk/meterProgramming/program">
                                <cti:param name="collectionType" value="idList"/>
                                <cti:param name="idList.ids" value="${deviceId}"/>
                            </cti:url>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".action">
                            <cti:button nameKey="program" href="${programUrl}" classes="fr M0"/>
                        </tags:nameValue2>
                    </c:otherwise>
                </c:choose>
            </tags:nameValueContainer2>
        </tags:sectionContainer>
    </c:if>

</div>

<cti:includeScript link="/resources/js/widgets/yukon.widget.config.js"/>


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

<tags:nameValueContainer2 naturalWidth="true">
    <tags:nameValue2 nameKey=".currentConfigurations">${fn:escapeXml(currentConfigName)}</tags:nameValue2>
</tags:nameValueContainer2>

<c:if test="${not empty currentConfigId}">
    <div class="stacked-md clearfix">
        <div class="button-group fr">
            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                <tags:widgetActionRefresh method="unassignConfig" nameKey="unassign" classes="M0"/>
            </cti:checkRolesAndProperties>
            <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                    <tags:widgetActionUpdate method="sendConfig" nameKey="send" container="${widgetParameters.widgetId}_config_results" classes="M0"/>
                    <tags:widgetActionUpdate method="readConfig" nameKey="read" container="${widgetParameters.widgetId}_config_results" classes="M0"/>
            </cti:checkRolesAndProperties>
            <tags:widgetActionUpdate method="verifyConfig" nameKey="verify" container="${widgetParameters.widgetId}_config_results" classes="M0"/>
        </div>
    </div>
</c:if>

<tags:nameValueContainer2 naturalWidth="true">

    <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
        <tags:nameValue2 nameKey=".deviceConfigurations" valueClass="full-width" nameClass="wsnw padBottom">
            <select id="configuration" name="configuration" class="fl left M0" style="max-width: 200px;">
                <c:forEach var="config" items="${existingConfigs}">
                    <option value="${config.configurationId}" <c:if test="${config.configurationId == currentConfigId}">selected</c:if>>${fn:escapeXml(config.name)}</option>
                </c:forEach>
            </select>
            <tags:widgetActionRefresh method="assignConfig" nameKey="assign" classes="right M0"/>
        </tags:nameValue2>
    </cti:checkRolesAndProperties>
    
    <div id="${widgetParameters.widgetId}_config_results"></div>

    <!-- Data Streaming Configuration -->
    <tags:nameValue2 nameKey=".dataStreaming" valueClass="full-width" nameClass="wsnw">
        <span class="fl">
        <c:choose>
            <c:when test="${dataStreamingConfig != null}">
                <a href="javascript:void(0);" data-popup="#data-streaming-popup">${fn:escapeXml(dataStreamingConfig.name)}</a>
            </c:when>
            <c:otherwise>
                <i:inline key="yukon.common.none.choice"/>
            </c:otherwise>
        </c:choose>
        </span>
        <cti:url var="configureUrl" value="/bulk/dataStreaming/configure">
            <cti:param name="collectionType" value="idList"/>
            <cti:param name="idList.ids" value="${deviceId}"/>
        </cti:url>
        <cti:url var="removeUrl" value="/bulk/dataStreaming/remove">
            <cti:param name="collectionType" value="idList"/>
            <cti:param name="idList.ids" value="${deviceId}"/>
        </cti:url>
        <c:set var="buttonClass" value=""/>
        <c:if test="${dataStreamingConfig != null}">
            <c:set var="buttonClass" value="button-group fr"/>
        </c:if>
        <span class="${buttonClass}">
            <cti:button nameKey="configure" href="${configureUrl}" />
            <c:if test="${dataStreamingConfig != null}">
                <cti:button nameKey="remove" href="${removeUrl}"/>
            </c:if>
        </span>
    </tags:nameValue2>
        
</tags:nameValueContainer2>

<div id="data-streaming-popup" data-width="400" data-title="<cti:msg2 key=".dataStreamingConfiguration"/>" class="dn">
    <c:set var="config" value="${dataStreamingConfig}"/>
    <%@ include file="/WEB-INF/pages/dataStreaming/configurationTable.jspf" %>
</div>
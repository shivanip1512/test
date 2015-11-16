<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
<cti:checkRolesAndProperties value="ASSIGN_CONFIG">
    <tags:nameValueContainer2 naturalWidth="false" tableClass="with-form-controls">
        <tags:nameValue2 nameKey=".deviceConfigurations" valueClass="full-width" nameClass="wsnw">
            <select id="configuration" name="configuration" class="fl left M0" style="max-width: 200px;">
                <c:forEach var="config" items="${existingConfigs}">
                    <option value="${config.configurationId}" <c:if test="${config.configurationId == currentConfigId}">selected</c:if>>${fn:escapeXml(config.name)}</option>
                </c:forEach>
            </select>
            <tags:widgetActionRefresh method="assignConfig" nameKey="assign" classes="right M0"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</cti:checkRolesAndProperties>
<div id="${widgetParameters.widgetId}_config_results"></div>
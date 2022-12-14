<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:msgScope paths="modules.smartNotifications">
    
    <input type="hidden" class="js-event-type-ddm" value="${eventTypeDDM}"/>
    <input type="hidden" class="js-event-type-asset-import" value="${eventTypeAssetImport}"/>

    <cti:url var="action" value="/notifications/subscription/saveDetails"/>
    <form:form id="notification-details" modelAttribute="subscription" action="${action}" method="POST">
        <cti:csrfToken/>
        <form:hidden path="id"/>

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".type">
                <c:choose>
                    <c:when test="${!empty subscription.type}">
                        <cti:msg2 key="${subscription.type.formatKey}"/>
                        <form:hidden path="type" cssClass="js-type"/>
                    </c:when>
                    <c:otherwise>
                        <tags:selectWithItems path="type" items="${eventTypes}" inputClass="js-type"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            
            <c:set var="monitorClass" value="${subscription.type != eventTypeDDM ? 'dn' : ''}"/>
            <tags:nameValue2 nameKey=".monitor" rowClass="js-monitor ${monitorClass}">
                <c:choose>
                    <c:when test="${!empty subscription.parameters['monitorId']}">
                        ${fn:escapeXml(monitorName)}
                        <form:hidden path="parameters['monitorId']" id="device-data-monitor"/>
                    </c:when>
                    <c:otherwise>
                        <tags:selectWithItems path="parameters['monitorId']" items="${deviceDataMonitors}" itemLabel="name" 
                            itemValue="id" id="device-data-monitor"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            <c:set var="displayAssetImportResultTypes" value="${subscription.type != eventTypeAssetImport ? 'dn' : ''}"/>
            <tags:nameValue2 nameKey=".importResult" rowClass="js-import-result ${displayAssetImportResultTypes}">
                <c:choose>
                    <c:when test="${!empty subscription.parameters['assetImportResultType']}">
                        <i:inline key="yukon.web.modules.operator.assetImportResultType.${subscription.parameters['assetImportResultType']}"/> 
                        <form:hidden path="parameters['assetImportResultType']" id="js-asset-import-result-type"/>
                    </c:when>
                    <c:otherwise>
                        <tags:selectWithItems path="parameters['assetImportResultType']" items="${assetImportResultTypes}"
                                              id="js-asset-import-result-type"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".frequency">
                <tags:selectWithItems path="frequency" items="${frequencies}" inputClass="js-frequency"/>
                <input type="hidden" name="frequency" value="IMMEDIATE" class="js-frequency-hidden" disabled="disabled"/>
            </tags:nameValue2>
            <c:set var="disableDaily" value="${subscription.frequency != 'DAILY_DIGEST'}"/>
            <c:set var="dailyClass" value="${disableDaily ? 'dn' : ''}"/>
            <tags:nameValue2 nameKey=".sendTime" rowClass="js-daily ${dailyClass}">
                <tags:timeSlider startPath="parameters['sendTime']" displayTimeToLeft="true" stepValue="60" timeFormat="HHMM" maxValue="1380"/>
                <div class="dn warning js-single-notification-warning"><i:inline key=".singleNotificationWarning"/></div>
                <input type="hidden" id="userSendTime" value="${fn:escapeXml(sendTime)}"/>
            </tags:nameValue2>            
            <tags:nameValue2 nameKey=".media">
                <tags:selectWithItems path="media" items="${mediaTypes}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".email">
                <tags:input path="recipient" size="30" maxlength="100"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".detail">
                <tags:selectWithItems path="verbosity" items="${detailTypes}"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>

    </form:form>


</cti:msgScope>

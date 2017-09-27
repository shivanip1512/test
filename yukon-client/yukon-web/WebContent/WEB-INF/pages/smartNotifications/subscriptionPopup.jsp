<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.smartNotifications">

    <cti:url var="action" value="/notifications/subscription/saveDetails"/>
    <form:form id="notification-details" commandName="subscription" action="${action}" method="POST">       
        <cti:csrfToken/>
        <form:hidden path="id"/>
        <span class="fr">
            <cti:link href="/user/profile#notifications-section" key="yukon.web.modules.smartNotifications.settingsLink"/>
            <cti:button renderMode="image" icon="icon-help" classes="fr" onclick="$('#user-message').removeClass('dn');"/>
        </span><br/>
        <tags:alertBox type="info" key=".popup.helpText" classes="dn" includeCloseButton="true"/>

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
            <c:set var="disableMonitor" value="${!empty subscription.type}"/>
            <c:set var="monitorClass" value="${subscription.type != 'DEVICE_DATA_MONITOR' ? 'dn' : ''}"/>
            <tags:nameValue2 nameKey=".monitor" rowClass="js-monitor ${monitorClass}">
                <c:choose>
                    <c:when test="${!empty subscription.parameters['monitorId']}">
                        ${monitorName}
                        <form:hidden path="parameters['monitorId']" id="device-data-monitor"/>
                    </c:when>
                    <c:otherwise>
                        <tags:selectWithItems path="parameters['monitorId']" items="${deviceDataMonitors}" itemLabel="name" 
                            itemValue="id" disabled="${disableMonitor}" id="device-data-monitor"/>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".frequency">
                <tags:selectWithItems path="frequency" items="${frequencies}" inputClass="js-frequency"/>
            </tags:nameValue2>
            <c:set var="disableDaily" value="${subscription.frequency != 'DAILY_DIGEST'}"/>
            <c:set var="dailyClass" value="${disableDaily ? 'dn' : ''}"/>
            <tags:nameValue2 nameKey=".sendTime" rowClass="js-daily ${dailyClass}">
                <div class="column-8-16 clearfix stacked">
                    <div class="column one">
                        <span class="js-time-label fwb">10:00 AM</span>
                        <form:hidden path="parameters['sendTime']" id="notifications-send-time" disabled="${disableDaily}"/>
                        <input type="hidden" id="userSettingSendTime" value="${sendTime}"/>
                    </div>
                    <div class="column two nogutter">
                        <div class="js-time-slider" style="margin-top: 7px;"></div>
                    </div>
                    <div class="dn warning js-single-notification-warning"><i:inline key=".singleNotificationWarning"/></div>
                </div>
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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:msgScope paths="modules.dr.cc.init">
    <cti:flashScopeMessages />
    <cti:url var="actionUrl" value="/dr/cc/program/${event.programId}/createEvent" />
    <form:form id="confirm-popup-form" action="${actionUrl}" modelAttribute="event">
        <cti:csrfToken />
        <cti:list var="arguments">
            <cti:item value="${program.name}"/>
            <cti:item value="${program.programType.name}"/>
        </cti:list>
        <cti:msg2 var="argument" arguments="${arguments}"/>
        <h3><i:inline key=".messagedetail" arguments="${argument}"/></h3>
        <b><i:inline key=".confirmation.parameters"/></b>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".notificationTime">
                <cti:formatDate type="FULL" value="${event.notificationTime}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".startTime">
                <cti:formatDate type="FULL" value="${event.startTime}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".stopTime">
                <cti:formatDate type="FULL" value="${event.stopTime}"/>
            </tags:nameValue2>
            <c:if test="${event.eventType.accounting or event.eventType.notification}">
            <tags:nameValue2 nameKey=".duration">
            <cti:formatDuration type="DHMS_REDUCED" value="${event.duration * 60 * 1000}"/>
        </tags:nameValue2>
        </c:if>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
<cti:includeScript link="/resources/js/pages/yukon.assets.rtu.js" />
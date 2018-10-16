<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="yukon.common.point">
    <cti:flashScopeMessages/>
    <cti:url var="saveUrl" value="/meter/historicalReadings/edit"/>
    <form:form id="manual-entry-form" modelAttribute="backingBean" action="${saveUrl}" method="post">
        <cti:csrfToken/>
        <form:hidden path="pointId" />
        <cti:formatDate var="dateTime" type="BOTH" value="${backingBean.timestamp}"/>
        <input type="hidden" name="editTimestamp" value="${dateTime}"/>
        <c:choose>
            <c:when test="${stateList == null}">
                <input type="hidden" name="oldValue" value="${oldValue}"/>
            </c:when>
            <c:otherwise>
                <input type="hidden" name="oldValue" value="${backingBean.stateId}"/>
            </c:otherwise>
        </c:choose>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.common.device">${fn:escapeXml(deviceName)}</tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.point">${fn:escapeXml(pointName)}</tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.timestamp">
                <cti:formatDate type="BOTH" value="${backingBean.timestamp}" />
            </tags:nameValue2>
            <c:if test="${stateList == null}">
                <tags:inputNameValue nameKey=".manualEntry.value" path="value" />
            </c:if>
            <c:if test="${stateList != null}">
                <tags:selectNameValue nameKey=".manualEntry.state" path="stateId" items="${stateList}" itemValue="liteID" itemLabel="stateText" />
            </c:if>
        </tags:nameValueContainer2>
    </form:form>
</cti:msgScope>
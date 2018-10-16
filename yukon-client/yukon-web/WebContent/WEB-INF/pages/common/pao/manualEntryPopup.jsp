<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="yukon.common.point">
    <cti:flashScopeMessages/>
    <cti:url var="saveUrl" value="/tools/points/manualEntrySend"/>
    <form:form id="manual-entry-form" cssClass="js-no-submit-on-enter" modelAttribute="backingBean" action="${saveUrl}">
        <cti:csrfToken/>
        <form:hidden path="pointId" />
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.common.device">${fn:escapeXml(deviceName)}</tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.point">${fn:escapeXml(pointName)}</tags:nameValue2>
            <c:if test="${stateList == null}">
                <tags:inputNameValue nameKey=".manualEntry.value" path="value" />
            </c:if>
            <c:if test="${stateList != null}">
                <tags:selectNameValue nameKey=".manualEntry.state" path="stateId" items="${stateList}" itemValue="liteID" itemLabel="stateText" />
            </c:if>
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:button nameKey="ok" classes="primary js-manual-entry-send" />
            <cti:button nameKey="close" onclick="$('#manual-entry-popup').dialog('close');" />
        </div>
    </form:form>
</cti:msgScope>
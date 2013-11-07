<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:msgScope paths="modules.tools.tdc">
    <cti:flashScopeMessages/>

    <form:form id="manualEntryForm" commandName="backingBean" action="/tdc/manualEntrySend">
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
            <cti:button nameKey="ok" classes="primary f-manualEntry-send" />
            <cti:button nameKey="close" onclick="jQuery('#tdc-popup').dialog('close');" />
        </div>
    </form:form>
</cti:msgScope>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:includeScript link="/resources/js/pages/yukon.dialog.ajax.js"/>
<cti:msgScope paths="modules.tools.tdc">
    <cti:flashScopeMessages/>
    <cti:url var="saveUrl" value="/tools/data-viewer/manualControlSend"/>
    <form:form id="tdc-manual-control-form" cssClass="js-no-submit-on-enter" modelAttribute="backingBean" action="${saveUrl}">
        <cti:csrfToken/>
        <form:hidden path="deviceId" />
        <form:hidden path="pointId" />
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.common.device">${fn:escapeXml(deviceName)}</tags:nameValue2>
            <tags:nameValue2 nameKey="yukon.common.point">${fn:escapeXml(pointName)}</tags:nameValue2>
            <c:if test="${stateList == null}">
                <tags:inputNameValue nameKey=".manualControl.value" path="value" />
            </c:if>
            <c:if test="${stateList != null}">
                <tags:nameValue2 nameKey=".manualControl.state">
                    <cti:pointValue pointId="${backingBean.pointId}" format="{value}" />
                </tags:nameValue2>
                <tags:selectNameValue emptyValueKey="true" nameKey=".manualControl.control" path="stateId" items="${stateList}" itemValue="liteID" itemLabel="stateText" />
            </c:if>            
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:button nameKey="ok" classes="primary js-tdc-manual-control-send"/>
            <cti:button nameKey="close" onclick="$('#tdc-popup').dialog('close');" />
        </div>
    </form:form>
</cti:msgScope>


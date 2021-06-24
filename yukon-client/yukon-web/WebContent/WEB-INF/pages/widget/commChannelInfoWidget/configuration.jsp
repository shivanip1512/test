<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:if test="${isIpAddressSupported}">
    <tags:nameValue2 nameKey=".ipaddress">
        <tags:input path="ipAddress"/>
    </tags:nameValue2>
</c:if>
<c:if test="${isPortNumberSupported}">
    <tags:nameValue2 nameKey=".portNumber">
        <tags:input path="portNumber"/>
    </tags:nameValue2>
</c:if>
<c:if test="${isPhysicalPortSupported}">
    <tags:nameValue2 nameKey=".physicalPort" rowClass="js-physical-port-row">
        <cti:displayForPageEditModes modes="VIEW">
            ${fn:escapeXml(commChannel.physicalPort)}
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <input type="hidden" id="otherPhysicalPortEnumValue" value="${otherPhysicalPort}">
            <c:set var="physicalPortError">
                <form:errors path="physicalPort"/>
            </c:set>
            <c:if test="${not empty physicalPortError}">
                <input type="hidden" id="physicalPortErrors" value="true">
            </c:if>
            <c:if test="${isPhysicalPortUserDefined || physicalPortError}">
                <input type="hidden" id="isOtherSelected" value="true">
            </c:if>
            <tags:selectWithItems path="physicalPort" items="${physicalPortList}" inputClass="js-physical-port"/>
            <tags:input path="physicalPort" maxlength="8" size="12" inputClass="js-user-physical-port-value dn"/>
        </cti:displayForPageEditModes>
    </tags:nameValue2>
</c:if>
<tags:nameValue2 nameKey=".baudRate">
    <tags:selectWithItems items="${baudRateList}" path="baudRate"/>
</tags:nameValue2>
<tags:nameValue2 nameKey=".status">
    <tags:switchButton path="enable" offNameKey=".disabled.label" onNameKey=".enabled.label"/>
</tags:nameValue2>
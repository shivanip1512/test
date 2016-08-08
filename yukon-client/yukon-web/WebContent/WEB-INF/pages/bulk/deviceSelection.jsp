<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.deviceSelection">
    <c:if test="${not empty errorMsg}">
        <div class="error">${errorMsg}</div>
    </c:if>
    <cti:msg var="headerTitle" key="yukon.common.device.bulk.deviceSelection.header" />
    <tags:sectionContainer title="${headerTitle}" id="updateConfirmContainer">
        <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
        <cti:url var="action" value="/bulk/deviceSelectionGetDevices">
            <c:if test="${redirectUrl != null}">
                <cti:param name="redirectUrl" value="${redirectUrl}"/>
            </c:if>
        </cti:url>
        <tags:deviceSelection action="${action}" groupDataJson="${groupDataJson}" pickerType="devicePicker" />
    </tags:sectionContainer>
</cti:standardPage>
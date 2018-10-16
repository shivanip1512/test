<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tags:standardPopup pageName="ivvc" module="capcontrol" popupName="zoneWizard">

<cti:url var="selectedParentUrl" value="/capcontrol/ivvc/wizard/wizardParentSelected" />
<form:form id="zoneParentForm" modelAttribute="zoneDto" action="${selectedParentUrl}">
    <cti:csrfToken/>
    <input type="hidden" name="zoneType" value="${zoneDto.zoneType}"/>
    <form:hidden path="substationBusId"/>

    <cti:msg2 var="createAsRootLabel" key=".label.createAsRootZone"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".label.parentZone">
            <form:select path="parentId">
                <c:if test="${fn:length(parentZones) == 0}">
                    <form:option value="">
                        <spring:escapeBody>${createAsRootLabel}</spring:escapeBody>
                    </form:option>
                </c:if>
                <c:forEach var="parentZone" items="${parentZones}">
                    <form:option value="${parentZone.id}">
                        <spring:escapeBody>${parentZone.name}</spring:escapeBody>
                    </form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
    </tags:nameValueContainer2>

</form:form>

</tags:standardPopup>
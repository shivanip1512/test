<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tags:standardPopup pageName="ivvc" module="capcontrol" popupName="zoneWizard">

<script type="text/javascript">
    submitParentSelect = function() {
        submitFormViaAjax('zoneWizardPopup', 'zoneParentForm');
    }
    
    cancelZoneWizard = function() {
        jQuery('#zoneWizardPopup').dialog('close');
    }
</script>

<form:form id="zoneParentForm" commandName="zoneDto" action="/capcontrol/ivvc/wizard/wizardParentSelected">
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
    <div class="action-area">
        <cti:button nameKey="next" onclick="submitParentSelect()" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="cancelZoneWizard()"/>
    </div>

</form:form>

</tags:standardPopup>
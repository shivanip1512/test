<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tags:standardPageFragment module="capcontrol" pageName="ivvc" fragmentName="selectZoneType">

<script type="text/javascript">
    GANG_OPERATED_option = function() {
        $('phaseSelector').addClassName('dn');
    }
    THREE_PHASE_option = function() {
        $('phaseSelector').addClassName('dn');
    }
    SINGLE_PHASE_option = function() {
        $('phaseSelector').removeClassName('dn');
    }

    backToParentSelect = function() {
        submitFormViaAjaxWithSkipShow('zoneWizardPopup', 'zoneTypeForm', '/spring/capcontrol/ivvc/wizard/wizardSelectParent', null, false);
    }
    submitTypeSelect = function() {
        submitFormViaAjaxWithSkipShow('zoneWizardPopup', 'zoneTypeForm', null, null, false);
    }
</script>

<form:form id="zoneTypeForm" commandName="zone" action="/spring/capcontrol/ivvc/wizard/wizardTypeSelected" >
    <form:hidden path="substationBusId"/>
    <form:hidden path="parentId"/>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".label.zoneType">
            <form:select path="zoneType">
                <c:forEach var="availableZoneType" items="${availableZoneTypes}">
                    <form:option value="${availableZoneType}" onclick="${availableZoneType}_option()">
                        <i:inline key="${availableZoneType}" />
                    </form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        
        <c:set var="phaseRowStyle" value="dn"/>
        <c:if test="${fn:length(availableZonePhases) == 1 ||
                    zone.zoneType == singlePhase}">
            <c:set var="phaseRowStyle" value=""/>
        </c:if>

        <tags:nameValue2 rowId="phaseSelector" nameKey=".label.zonePhase" rowClass="${phaseRowStyle}">
            <form:select path="regulators[0].phase">
                <c:forEach var="phase" items="${availableZonePhases}">
                    <form:option value="${phase}">
                        <spring:escapeBody htmlEscape="true">
                        	<i:inline key="${phase}" />
                        </spring:escapeBody>
                    </form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".label.parentZone">
            <span id="selectedParentZoneIdInType" class="disabledRow">
                <spring:escapeBody htmlEscape="true">${parentZoneName}</spring:escapeBody>
            </span>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div class="actionArea">
        <cti:button key="back" onclick="backToParentSelect()"/>
        <cti:button key="next" onclick="submitTypeSelect()"/>
    </div>
</form:form>

</tags:standardPageFragment>
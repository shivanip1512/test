<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tags:standardPageFragment module="capcontrol" pageName="ivvc" fragmentName="selectZoneType">

<script type="text/javascript">
    zoneTypeChange = function(zoneType) {
        if (zoneType == 'SINGLE_PHASE') {
            $('phaseSelector').removeClassName('dn');
        } else {
            $('phaseSelector').addClassName('dn');
        }
    };
    backToParentSelect = function() {
        submitFormViaAjax('zoneWizardPopup', 'zoneTypeForm', '/spring/capcontrol/ivvc/wizard/wizardSelectParent', false);
    };
    submitTypeSelect = function() {
        submitFormViaAjax('zoneWizardPopup', 'zoneTypeForm', null, false);
    };
</script>

<form:form id="zoneTypeForm" commandName="zoneDto" action="/spring/capcontrol/ivvc/wizard/wizardTypeSelected" >
    <form:hidden path="substationBusId"/>
    <form:hidden path="parentId"/>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".label.zoneType">
            <form:select path="zoneType" onchange="zoneTypeChange(this.options[this.selectedIndex].value)">
                <c:forEach var="availableZoneType" items="${availableZoneTypes}">
                    <form:option value="${availableZoneType}">
                        <i:inline key="${availableZoneType}" />
                    </form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        
        <c:set var="phaseRowStyle" value="dn"/>
        <c:if test="${fn:length(availableZonePhases) == 1 ||
                    zoneDto.zoneType == singlePhase}">
            <c:set var="phaseRowStyle" value=""/>
        </c:if>

        <tags:nameValue2 rowId="phaseSelector" nameKey=".label.zonePhase" rowClass="${phaseRowStyle}">
            <form:select path="regulatorsList[0].phase">
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
        <cti:button nameKey="back" onclick="backToParentSelect()"/>
        <cti:button nameKey="next" onclick="submitTypeSelect()"/>
    </div>
</form:form>

</tags:standardPageFragment>
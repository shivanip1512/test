<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<tags:standardPageFragment module="capcontrol" pageName="ivvc" fragmentName="selectZoneType">

<cti:url var="selectedTypeUrl" value="/capcontrol/ivvc/wizard/wizardTypeSelected"/>
<form:form id="zoneTypeForm" modelAttribute="zoneDto" action="${selectedTypeUrl}" >
    <cti:csrfToken/>
    <form:hidden path="substationBusId"/>
    <form:hidden path="parentId"/>

    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".label.zoneType">
            <form:select path="zoneType" onchange="yukon.da.zone.wizard.zoneTypeChange(this.options[this.selectedIndex].value)">
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
            <span id="selectedParentZoneIdInType" class="disabledRow">${fn:escapeXml(parentZoneName)}</span>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    
</form:form>

</tags:standardPageFragment>
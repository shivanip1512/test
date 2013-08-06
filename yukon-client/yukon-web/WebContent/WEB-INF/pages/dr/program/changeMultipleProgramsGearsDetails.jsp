<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.changeMultipleGears">

<script type="text/javascript">

jQuery(function() {
   
    submitForm = function() {
        return submitFormViaAjax('drDialog', 'changeGearForm', '<cti:url value="/dr/program/changeMultipleGears"/>');
    }
    
    singleProgramChecked = function(event) {
        if (jQuery(event.target).is(':checked')) {
            jQuery(event.target).parent().next().find(".f-useStopGearCheckedTarget").removeAttr("disabled");
        } else {
            jQuery(event.target).parent().next().find(".f-useStopGearCheckedTarget").attr("disabled","disabled");
        }
    }
    
    jQuery(".f-singleProgramChecked").click(singleProgramChecked); 

});
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked">
    <c:if test="${!empty controlArea}">
        <cti:msg2 key=".confirmChange.controlArea" htmlEscape="true" argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg2 key=".confirmChange.scenario" htmlEscape="true" argument="${scenario.name}"/>
    </c:if>
</h4>

<cti:url var="submitUrl" value="/dr/program/changeMultipleGears"/>
<form:form id="changeGearForm" commandName="backingBean" action="${submitUrl}" onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" value="true" name=fromBack"/>
    <cti:msg2 var="boxTitle" key=".programs"/>
    <tags:boxContainer title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable">
        <thead>
	        <tr>
	            <th><cti:msg2 key=".programLbl"/></th>
	            <th><cti:msg2 key=".gearLbl"/></th>
	            <th><cti:msg2 key=".currentState"/></th>
	        </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
	        <c:forEach var="program" varStatus="status" items="${programs}">
	            <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
	            <c:set var="gears" value="${gearsByProgramId[programId]}"/>
	            <c:set var="currentGear" value="${currentGearByProgramId[programId]}"/>
	                <tr>
	                    <c:if test="${fn:length(gears) > 1}">
	                        <td>
	                            <form:hidden path="programGearChangeInfo[${status.index}].programId" />
	                            <form:checkbox path="programGearChangeInfo[${status.index}].changeGear" id="changeGearCheckbox${status.index}" cssClass="f-singleProgramChecked"/>
	                            <c:if test="${fn:length(gears) < 2}">
	                                <form:hidden path="programGearChangeInfo[${status.index}].changeGear"/>
	                                <input type="checkbox" disabled="disabled"/>
	                            </c:if>
	                            <label for="changeGearCheckbox${status.index}"><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></label>
	                        </td>
	                        <td>
	                            <form:select path="programGearChangeInfo[${status.index}].gearNumber" id="programGear${status.index}" cssClass="f-useStopGearCheckedTarget">
	                                <c:forEach var="gear" varStatus="gearStatus" items="${gears}">
	                                    <c:if test="${currentGear.gearNumber != gear.gearNumber}">
	                                        <form:option value="${gearStatus.index + 1}"><spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody></form:option>
	                                    </c:if>
	                                </c:forEach>
	                            </form:select>
	                        </td>
	                        <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/></td>
	                    </c:if>
	                    <c:if test="${fn:length(gears) < 2}">
	                        <td>
	                            <input type="checkbox" id="changeGearCheckboxUnavailable${status.index}" disabled="disabled"/>
	                            <label for="changeGearCheckboxUnavailable${status.index}"><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></label>
	                        </td>
	                        <td>
	                            <cti:msg2 key="yukon.web.modules.dr.program.stopMultiplePrograms.gearChangeUnavailable"/>
	                        </td>
	                        <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/></td>
	                    </c:if>
	                </tr>
	        </c:forEach>
        </tbody>
    </table>
    </div>
    </tags:boxContainer>

    <div class="actionArea">
        <cti:button id="okButton" nameKey="ok" classes="primary action" type="submit"/>
        <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
    </div>
</form:form>
</cti:msgScope>
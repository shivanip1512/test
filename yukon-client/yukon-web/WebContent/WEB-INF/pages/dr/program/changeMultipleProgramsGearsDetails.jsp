<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">

jQuery(function() {
   
    submitForm = function() {
        return submitFormViaAjax('drDialog', 'changeGearForm', '<cti:url value="/spring/dr/program/changeMultipleGears"/>');
    }
    
    singleProgramChecked = function(event) {
        if (jQuery(event.target).is(':checked')) {
            jQuery(event.target).parent().next().find(".f_useStopGearCheckedTarget").removeAttr("disabled");
        } else {
            jQuery(event.target).parent().next().find(".f_useStopGearCheckedTarget").attr("disabled","disabled");
        }
    }
    
    hideDrDialog = function() {
        jQuery("#drDialog").hide();
    }
    
    jQuery("#cancelBtn").click(hideDrDialog);
    jQuery(".f_singleProgramChecked").click(singleProgramChecked); 

});
</script>

<cti:flashScopeMessages/>

<h1 class="dialogQuestion">

    <c:if test="${!empty controlArea}">
        <cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.confirmChange.controlArea"
            htmlEscape="true" argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.confirmChange.scenario"
            htmlEscape="true" argument="${scenario.name}"/>
    </c:if>
</h1>

<cti:url var="submitUrl" value="/spring/dr/program/changeMultipleGears"/>
<form:form id="changeGearForm" commandName="backingBean" action="${submitUrl}" onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" value="true" name=fromBack"/>
    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.changeMultipleGears.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <th><cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.programLbl"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.gearLbl"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.currentState"/></th>
        </tr>
        <c:forEach var="program" varStatus="status" items="${programs}">
            <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
            <c:set var="gears" value="${gearsByProgramId[programId]}"/>
            <c:set var="currentGear" value="${currentGearByProgramId[programId]}"/>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <c:if test="${fn:length(gears) > 1}">
                        <td>
                            <form:hidden path="programGearChangeInfo[${status.index}].programId" />
                            <form:checkbox path="programGearChangeInfo[${status.index}].changeGear" id="changeGearCheckbox${status.index}" cssClass="f_singleProgramChecked"/>
                            <c:if test="${fn:length(gears) < 2}">
                                <form:hidden path="programGearChangeInfo[${status.index}].changeGear"/>
                                <input type="checkbox" disabled="disabled"/>
                            </c:if>
                            <label for="changeGearCheckbox${status.index}"><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></label>
                        </td>
                        <td>
                            <form:select path="programGearChangeInfo[${status.index}].gearNumber" id="programGear${status.index}" cssClass="f_useStopGearCheckedTarget">
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
                            <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.gearChangeUnavailable"/>
                        </td>
                        <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/></td>
                    </c:if>
                </tr>
        </c:forEach>
    </table>
    </div>
    </tags:abstractContainer>
    <br>

    <div class="actionArea">
        <input id="okButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.okButton"/>"/>
        <input type="button" id="cancelBtn" value="<cti:msg key="yukon.web.modules.dr.program.changeMultipleGears.cancelButton"/>"/>
    </div>
</form:form>

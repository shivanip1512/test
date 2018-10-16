<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.stopMultiplePrograms">

<script type="text/javascript">

$(function(){
    
    submitForm = function() {
        var url;

        if ($("#autoObserveConstraints").is(":checked") 
                || $("#autoObserveConstraints").is(":disabled")
                || ${!checkConstraintsAllowed}) {
            url = '<cti:url value="/dr/program/stop/stopMultiple"/>';
        } else {
            url = '<cti:url value="/dr/program/stop/stopMultipleConstraints"/>';
        }
        return submitFormViaAjax('drDialog', 'stopProgramForm',url);
    }
    
    stopProgramChecked = function(event) {
        if (!$(event.target).is(':checked')) {
            $("#allProgramsCheckbox").removeAttr("checked");
        }
        updateComponents();
    }
    
    // Check-All type checkbox for Stop Program checkboxes
    updateStopAllProgramCheckbox = function() {
        var stopAll = true,
            allDisabled = true;
        
        $(".js-singleProgramChecked").each(function(index,element) {
            // Stopall should be checked if all the valid checkboxes are checked
            stopAll = stopAll && ($(element).is(":disabled") || $(element).is(":checked"));
            allDisabled = (allDisabled && $(element).is(":disabled"));
        });
        
        
        if (allDisabled) {
            $("#allProgramsCheckbox").removeAttr("checked");
            $("#allProgramsCheckbox").attr("disabled","disabled");
        } else if (stopAll) {
            $("#allProgramsCheckbox").removeAttr("disabled");
            $("#allProgramsCheckbox").attr("checked","checked");
        } else {
            $("#allProgramsCheckbox").removeAttr("disabled");
            $("#allProgramsCheckbox").removeAttr("checked");
        }
        
    }
    
    useStopGearChecked = function(event) {
        if ($(event.target).is(':checked')) {
            $(event.target).siblings(".js-useStopGearCheckedTarget").removeAttr("disabled");
        } else {
            $(event.target).siblings(".js-useStopGearCheckedTarget").attr("disabled","disabled");
        }
        updateComponents();
    }
    
    // Check-All type checkbox for Use Stop Gear checkboxes
    updateUseStopGearsAllCheckbox = function() {
        var stopAll = true,
            allDisabled = true;
        
        if ($("#stopNowCheckbox").is(':checked')) {
            $("#allProgramsUseStopGearsCheckbox").removeAttr("checked");
            $("#allProgramsUseStopGearsCheckbox").attr("disabled","disabled");
        } else {
            $("#allProgramsUseStopGearsCheckbox").removeAttr("disabled");

            $(".js-useStopGearChecked").each(function(index,element) {
                stopAll = stopAll && ($(element).is(":disabled") || $(element).is(":checked"));
                allDisabled = (allDisabled && $(element).is(":disabled"));
            });
            
            if (allDisabled) {
                $("#allProgramsUseStopGearsCheckbox").removeAttr("checked");
                $("#allProgramsUseStopGearsCheckbox").attr("disabled","disabled");
            } else if (stopAll) {
                $("#allProgramsUseStopGearsCheckbox").removeAttr("disabled");
                $("#allProgramsUseStopGearsCheckbox").attr("checked","checked");
            } else {
                $("#allProgramsUseStopGearsCheckbox").removeAttr("disabled");
                $("#allProgramsUseStopGearsCheckbox").removeAttr("checked");
            }
        }
    }
    
    allStopProgramChecked = function() {
        var index,
        allChecked = $("#allProgramsCheckbox").is(":checked");
        for (index = 0; index < ${fn:length(programs)}; index++) {
            if (allChecked && !$("#stopProgramCheckbox"+index).is(":disabled")) {
                $("#stopProgramCheckbox"+index).attr("checked","checked");
            } else {
                $("#stopProgramCheckbox"+index).removeAttr("checked");
            }
        }
        updateComponents();
    }
    
    allUseStopGearChecked = function() {
        var index,
        allChecked = $("#allProgramsUseStopGearsCheckbox").is(":checked");
        for (index = 0; index < ${fn:length(programs)}; index++) {
            if (allChecked) {
                $("#useStopGear" + index).attr("checked","checked");
            } else {
                $("#useStopGear" + index).removeAttr("checked");
            }
        }
        updateComponents();
    }
    
    stopNowChecked = function () {
        updateComponents();
    }
    
    updateComponents = function() {
        var stopNow = $("#stopNowCheckbox").is(':checked'),
            index;
        $('#stopDate').prop('disabled', stopNow);
        
        for (index = 0; index < ${fn:length(programs)}; index++) {

            if (!stopNow && $("#stopProgramCheckbox" + index).is(":checked")) {
                $('#useStopGear'+index).removeAttr("disabled");
                if($('#useStopGear'+index).is(":checked")) {
                    $('#programGear'+index).removeAttr("disabled");
                } else {
                    $('#programGear'+index).attr("disabled","disabled");
                }
            } else {
                $('#useStopGear'+index).removeAttr("checked");
                $('#useStopGear'+index).attr("disabled","disabled");
                $('#programGear'+index).attr("disabled","disabled");
            }
        }

        updateStopAllProgramCheckbox();
        updateUseStopGearsAllCheckbox();
        updateActionButtons();
    }
    
    
    updateActionButtons = function() {
        var atLeastOneGearChange = false;

        $(".js-useStopGearChecked").each(function(index,element) {
            atLeastOneGearChange = (atLeastOneGearChange || 
                    (!$(element).is(":disabled") && $(element).is(":checked")));
        });
        
        if (atLeastOneGearChange && ${checkConstraintsAllowed}) {
            $("#autoObserveConstraints").removeAttr("disabled");
            if ($("#autoObserveConstraints").is(":checked")) {
                $('#okButton').removeAttr("disabled");
                $('#nextButton').attr("disabled","disabled");
            } else {
                $('#okButton').attr("disabled","disabled");
                $('#nextButton').removeAttr("disabled");
            }
        } else {
            $("#autoObserveConstraints").removeAttr("checked");
            $("#autoObserveConstraints").attr("disabled","disabled");
            $('#okButton').removeAttr("disabled");
            $('#nextButton').attr("disabled","disabled");
        }
    }
    
    updateComponents();
    
    $(".js-singleProgramChecked").click(stopProgramChecked);
    $(".js-useStopGearChecked").click(useStopGearChecked);
    $("#allProgramsUseStopGearsCheckbox").click(allUseStopGearChecked);
    $("#allProgramsCheckbox").click(allStopProgramChecked);
    $("#stopNowCheckbox").click(stopNowChecked);
    $("#autoObserveConstraints").click(updateActionButtons);

    updateProgramState = function(index) {
        return function(data) {
            var programState = JSON.parse(data.state);
            if (programState.running || programState.scheduled) {
                jQuery('#stopProgramCheckbox' + index).prop("disabled", false);
            } else {
                jQuery('#stopProgramCheckbox' + index).prop("disabled", true);
                document.getElementById('stopProgramCheckbox' + index).checked = false;
            }
            updateComponents();
        }
    }

});
$( function () {
    // init dateTime fields dynamically brought onto page after initial page load
    yukon.ui.initDateTimePickers();
});
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked">
    <c:if test="${!empty controlArea}">
        <cti:msg2 key=".confirmQuestion.controlArea" argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg2 key=".confirmQuestion.scenario" argument="${scenario.name}"/>
    </c:if>
</h4>

<cti:url var="submitUrl" value="/dr/program/stop/stopMultiple"/>
<form:form id="stopProgramForm" modelAttribute="backingBean" onsubmit="return submitForm();">
    <cti:csrfToken/>
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>

    <table class="compact-results-table stacked">
        <tr>
            <th><cti:msg2 key=".stopTime"/></th>
        </tr>
        <tr><td>
            <table>
                <tr><td>
                    <form:checkbox path="stopNow" id="stopNowCheckbox"/>
                    <label for="stopNowCheckbox">
                        <cti:msg2 key=".stopNow"/>
                    </label>
                </td></tr>
                <tr><td>
                    <dt:dateTime id="stopDate" path="stopDate" value="${backingBean.stopDate}"/>
                </td></tr>
            </table>
        </td></tr>
    </table>

    <cti:msg2 var="boxTitle" key=".programs"/>
    <tags:sectionContainer title="${boxTitle}">
        <div class="scroll-md">
            <table class="compact-results-table dashed">
                <tr>
                    <th><cti:msg2 key=".stopProgramName"/></th>
                    
                     <c:if test="${stopGearAllowed}">
                        <th><cti:msg2 key="yukon.web.modules.dr.program.changeMultipleGears.stopGearLbl"/></th>
                     </c:if>
                    <th><cti:msg2 key=".currentState"/></th>
                    
                    <c:if test="${!empty scenarioPrograms}">
                        <th><cti:msg2 key=".stopOffset"/></th>
                    </c:if>
                </tr>
                <c:forEach var="program" varStatus="status" items="${programs}">
                    <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
                    <c:if test="${stopGearAllowed}">
                        <c:set var="gears" value="${gearsByProgramId[programId]}"/>
                    </c:if>
                    <c:set var="currentGear" value="${currentGearByProgramId[programId]}"/>
                    <tr>
                        <td>
                            <form:hidden path="programStopInfo[${status.index}].programId"/>
                            <form:checkbox path="programStopInfo[${status.index}].stopProgram" id="stopProgramCheckbox${status.index}" cssClass="js-singleProgramChecked"/>
                            <label for="stopProgramCheckbox${status.index}">${fn:escapeXml(program.name)}</label>
                        </td>
                        <c:if test="${stopGearAllowed}">
                            <td>
                                <c:if test="${fn:length(gears) > 1}">
                                    <form:checkbox path="programStopInfo[${status.index}].useStopGear" id="useStopGear${status.index}" cssClass="js-useStopGearChecked"/>
                                    <form:select path="programStopInfo[${status.index}].gearNumber" id="programGear${status.index}" cssClass="js-useStopGearCheckedTarget">
                                        <c:forEach var="gear" varStatus="gearStatus" items="${gears}">
                                            <c:if test="${currentGear.gearNumber != gear.gearNumber}">
                                                <form:option value="${gearStatus.index + 1}">
                                                    <spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody>
                                                </form:option>
                                            </c:if>
                                        </c:forEach>
                                    </form:select>
                                </c:if>
                                <c:if test="${fn:length(gears) < 2}">
                                   <cti:msg2 key=".gearChangeUnavailable"/>
                                </c:if>
                            </td>
                        </c:if>
                        <td>
                            <cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/>
                            <cti:dataUpdaterCallback function="updateProgramState(${status.index})" initialize="true" state="DR_PROGRAM/${programId}/SHOW_ACTION"/>
                        </td>
                        <c:if test="${!empty scenarioPrograms}">
                            <c:set var="scenarioProgram" value="${scenarioPrograms[programId]}"/>
                            <td><cti:formatPeriod type="HM_SHORT" value="${scenarioProgram.stopOffset}"/></td>
                        </c:if>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </tags:sectionContainer>

    <input type="checkbox" id="allProgramsCheckbox"/>
    <label for="allProgramsCheckbox">
        <cti:msg2 key=".stopAllPrograms"/>
    </label><br>
    
    <c:if test="${stopGearAllowed}">
        <input type="checkbox" id="allProgramsUseStopGearsCheckbox"/>
        <label for="allProgramsUseStopGearsCheckbox">
            <cti:msg2 key=".allProgramsUseStopGears"/>
        </label><br>
    </c:if>
    
    <c:if test="${autoObserveConstraintsAllowed}">
        <c:if test="${checkConstraintsAllowed}">
            <form:checkbox path="autoObserveConstraints" id="autoObserveConstraints" onclick="updateSubmitButtons();"/>
            <label for="autoObserveConstraints">
                <cti:msg2 key=".autoObserveConstraints"/>
            </label>
        </c:if>
        <c:if test="${!checkConstraintsAllowed}">
            <%-- They have to automatically observe constraints. --%>
            <cti:msg2 key=".constraintsWillBeObserved"/>
        </c:if>
    </c:if>

    <div class="action-area">
        <cti:button nameKey="ok" classes="primary action" type="submit"/>
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <cti:button nameKey="next" id="nextButton" classes="primary action" type="submit"/>
        </c:if>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
    </div>
</form:form>
</cti:msgScope>
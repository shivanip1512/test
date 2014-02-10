<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:msgScope paths="modules.dr.program.stopMultiplePrograms">

<script type="text/javascript">

jQuery(function(){
    
    submitForm = function() {
        var url;

        if (jQuery("#autoObserveConstraints").is(":checked") 
                || jQuery("#autoObserveConstraints").is(":disabled")
                || ${!checkConstraintsAllowed}) {
            url = '<cti:url value="/dr/program/stop/stopMultiple"/>';
        } else {
            url = '<cti:url value="/dr/program/stop/stopMultipleConstraints"/>';
        }
        return submitFormViaAjax('drDialog', 'stopProgramForm',url);
    }
    
    stopProgramChecked = function(event) {
        if (!jQuery(event.target).is(':checked')) {
            jQuery("#allProgramsCheckbox").removeAttr("checked");
        }
        updateComponents();
    }
    
    // Check-All type checkbox for Stop Program checkboxes
    updateStopAllProgramCheckbox = function() {
        var stopAll = true,
            allDisabled = true;
        
        jQuery(".f-singleProgramChecked").each(function(index,element) {
            // Stopall should be checked if all the valid checkboxes are checked
            stopAll = stopAll && (jQuery(element).is(":disabled") || jQuery(element).is(":checked"));
            allDisabled = (allDisabled && jQuery(element).is(":disabled"));
        });
        
        
        if (allDisabled) {
            jQuery("#allProgramsCheckbox").removeAttr("checked");
            jQuery("#allProgramsCheckbox").attr("disabled","disabled");
        } else if (stopAll) {
            jQuery("#allProgramsCheckbox").removeAttr("disabled");
            jQuery("#allProgramsCheckbox").attr("checked","checked");
        } else {
            jQuery("#allProgramsCheckbox").removeAttr("disabled");
            jQuery("#allProgramsCheckbox").removeAttr("checked");
        }
        
    }
    
    useStopGearChecked = function(event) {
        if (jQuery(event.target).is(':checked')) {
            jQuery(event.target).siblings(".f-useStopGearCheckedTarget").removeAttr("disabled");
        } else {
            jQuery(event.target).siblings(".f-useStopGearCheckedTarget").attr("disabled","disabled");
        }
        updateComponents();
    }
    
    // Check-All type checkbox for Use Stop Gear checkboxes
    updateUseStopGearsAllCheckbox = function() {
        var stopAll = true,
            allDisabled = true;
        
        if (jQuery("#stopNowCheckbox").is(':checked')) {
            jQuery("#allProgramsUseStopGearsCheckbox").removeAttr("checked");
            jQuery("#allProgramsUseStopGearsCheckbox").attr("disabled","disabled");
        } else {
            jQuery("#allProgramsUseStopGearsCheckbox").removeAttr("disabled");

            jQuery(".f-useStopGearChecked").each(function(index,element) {
                stopAll = stopAll && (jQuery(element).is(":disabled") || jQuery(element).is(":checked"));
                allDisabled = (allDisabled && jQuery(element).is(":disabled"));
            });
            
            if (allDisabled) {
                jQuery("#allProgramsUseStopGearsCheckbox").removeAttr("checked");
                jQuery("#allProgramsUseStopGearsCheckbox").attr("disabled","disabled");
            } else if (stopAll) {
                jQuery("#allProgramsUseStopGearsCheckbox").removeAttr("disabled");
                jQuery("#allProgramsUseStopGearsCheckbox").attr("checked","checked");
            } else {
                jQuery("#allProgramsUseStopGearsCheckbox").removeAttr("disabled");
                jQuery("#allProgramsUseStopGearsCheckbox").removeAttr("checked");
            }
        }
    }
    
    allStopProgramChecked = function() {
        var index,
        allChecked = jQuery("#allProgramsCheckbox").is(":checked");
        for (index = 0; index < ${fn:length(programs)}; index++) {
            if (allChecked && !jQuery("#stopProgramCheckbox"+index).is(":disabled")) {
                jQuery("#stopProgramCheckbox"+index).attr("checked","checked");
            } else {
                jQuery("#stopProgramCheckbox"+index).removeAttr("checked");
            }
        }
        updateComponents();
    }
    
    allUseStopGearChecked = function() {
        var index,
        allChecked = jQuery("#allProgramsUseStopGearsCheckbox").is(":checked");
        for (index = 0; index < ${fn:length(programs)}; index++) {
            if (allChecked) {
                jQuery("#useStopGear" + index).attr("checked","checked");
            } else {
                jQuery("#useStopGear" + index).removeAttr("checked");
            }
        }
        updateComponents();
    }
    
    stopNowChecked = function () {
        updateComponents();
    }
    
    updateComponents = function() {
        var stopNow = jQuery("#stopNowCheckbox").is(':checked'),
            index;
        jQuery('#stopDate').prop('disabled', stopNow);
        
        for (index = 0; index < ${fn:length(programs)}; index++) {

            if (!stopNow && jQuery("#stopProgramCheckbox" + index).is(":checked")) {
                jQuery('#useStopGear'+index).removeAttr("disabled");
                if(jQuery('#useStopGear'+index).is(":checked")) {
                    jQuery('#programGear'+index).removeAttr("disabled");
                } else {
                    jQuery('#programGear'+index).attr("disabled","disabled");
                }
            } else {
                jQuery('#useStopGear'+index).removeAttr("checked");
                jQuery('#useStopGear'+index).attr("disabled","disabled");
                jQuery('#programGear'+index).attr("disabled","disabled");
            }
        }

        updateStopAllProgramCheckbox();
        updateUseStopGearsAllCheckbox();
        updateActionButtons();
    }
    
    
    updateActionButtons = function() {
        var atLeastOneGearChange = false;

        jQuery(".f-useStopGearChecked").each(function(index,element) {
            atLeastOneGearChange = (atLeastOneGearChange || 
                    (!jQuery(element).is(":disabled") && jQuery(element).is(":checked")));
        });
        
        if (atLeastOneGearChange && ${checkConstraintsAllowed}) {
            jQuery("#autoObserveConstraints").removeAttr("disabled");
            if (jQuery("#autoObserveConstraints").is(":checked")) {
                jQuery('#okButton').removeAttr("disabled");
                jQuery('#nextButton').attr("disabled","disabled");
            } else {
                jQuery('#okButton').attr("disabled","disabled");
                jQuery('#nextButton').removeAttr("disabled");
            }
        } else {
            jQuery("#autoObserveConstraints").removeAttr("checked");
            jQuery("#autoObserveConstraints").attr("disabled","disabled");
            jQuery('#okButton').removeAttr("disabled");
            jQuery('#nextButton').attr("disabled","disabled");
        }
    }
    
    updateComponents();
    
    jQuery(".f-singleProgramChecked").click(stopProgramChecked);
    jQuery(".f-useStopGearChecked").click(useStopGearChecked);
    jQuery("#allProgramsUseStopGearsCheckbox").click(allUseStopGearChecked);
    jQuery("#allProgramsCheckbox").click(allStopProgramChecked);
    jQuery("#stopNowCheckbox").click(stopNowChecked);
    jQuery("#autoObserveConstraints").click(updateActionButtons);
    

    updateProgramState = function(index) {
        return function(data) {
            if (0 === data.state.indexOf('running') || 0 === data.state.indexOf('scheduled')) {
                jQuery('#stopProgramCheckbox' + index).removeAttr("disabled");
            } else {
                jQuery('#stopProgramCheckbox' + index).attr("disabled", "disabled");
                document.getElementById('stopProgramCheckbox' + index).checked = false;
            }
            updateComponents();
        }
    }

});
jQuery( function () {
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
<form:form id="stopProgramForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>

    <table class="compact-results-table">
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
    <br>

    <cti:msg2 var="boxTitle" key=".programs"/>
    <tags:boxContainer title="${boxTitle}">
    <div class="scroll-medium">
    <table class="compact-results-table">
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
                    <form:checkbox path="programStopInfo[${status.index}].stopProgram" id="stopProgramCheckbox${status.index}" cssClass="f-singleProgramChecked"/>
                    <label for="stopProgramCheckbox${status.index}">${program.name}</label>
                </td>
                <c:if test="${stopGearAllowed}">
                    <td>
                        <c:if test="${fn:length(gears) > 1}">
                            <form:checkbox path="programStopInfo[${status.index}].useStopGear" id="useStopGear${status.index}" cssClass="f-useStopGearChecked"/>
                            <form:select path="programStopInfo[${status.index}].gearNumber" id="programGear${status.index}" cssClass="f-useStopGearCheckedTarget">
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
    </tags:boxContainer>

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
    <br>

    <div class="action-area">
        <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <cti:button nameKey="next" id="nextButton" classes="primary action" type="submit"/>
        </c:if>
        <cti:button nameKey="ok" classes="primary action" type="submit"/>
    </div>
</form:form>
</cti:msgScope>
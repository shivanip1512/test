<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript">

jQuery(function () {

    overideAllChecked = function() {
        var allChecked = jQuery("#overrideAllConstraints").is(":checked");
        for (var index = 0; index < ${numProgramsToStop}; index++) {
            var checkbox = jQuery("#programStopInfoOverride" + index);
            if (checkbox.length) {// implies *not* zero
                if (allChecked) {
                    checkbox.attr("checked","checked");
                } else {
                    checkbox.removeAttr("checked");
                }
            }
        }
        updateAllOverridesChecked();
    }

    updateAllOverridesChecked = function() {
        var allChecked = true;
        for (var index = 0; index < ${numProgramsToStop}; index++) {
            var checkbox = jQuery("#programStopInfoOverride" + index);
            if (checkbox.length) {// implies *not* zero
                allChecked = (allChecked && checkbox.is(":checked"));
                if (!allChecked) break;
            }
        }

        if (allChecked) {
            jQuery("#okButton").removeAttr("disabled");
            jQuery("#overrideAllConstraints").attr("checked","checked");
        } else {
            jQuery("#okButton").attr("disabled","disabled");
            jQuery("#overrideAllConstraints").removeAttr("checked");
        }
        
    }

    singleOverrideChecked = function(event) {
        updateAllOverridesChecked();
    }
    
    jQuery("#overrideAllConstraints").click(overideAllChecked);
    jQuery(".f_singleOverrideChecked").click(singleOverrideChecked);
    updateAllOverridesChecked();
});
</script>

<cti:url var="submitUrl" value="/spring/dr/program/stop/stopMultiple"/>
<form:form id="stopMultipleProgramsForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'stopMultipleProgramsForm')">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" name="from" value="constraints"/>
    <form:hidden path="stopNow"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="autoObserveConstraints"/>

    <h1 class="dialogQuestion">
    <c:if test="${!empty controlArea}">
        <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.confirmQuestion.controlArea"
            htmlEscape="true" argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.confirmQuestion.scenario"
            htmlEscape="true" argument="${scenario.name}"/>
    </c:if>
    </h1>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.stopMultiplePrograms.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable" id="stopMultipleProgramsOverridePrograms">
        <tr>
            <c:if test="${constraintsViolated}">
                <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.overrideProgramName"/></th>
                <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.violations"/></th>
            </c:if>
            <c:if test="${!constraintsViolated}">
                <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.programName"/></th>
            </c:if>
        </tr>
        <c:set var="index" value="0"/>
        <c:forEach var="programStopInfo" varStatus="status" items="${backingBean.programStopInfo}">
            <c:if test="${backingBean.programStopInfo[status.index].stopProgram}">
                <c:set var="program" value="${programsByProgramId[backingBean.programStopInfo[status.index].programId]}"/>
                <c:set var="violationsForProgram" value="${violationsByProgramId[backingBean.programStopInfo[status.index].programId]}"/>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <c:if test="${constraintsViolated}">
                        <td class="override">
                            <c:if test="${!empty violationsForProgram && overrideAllowed}">
                                <form:checkbox
                                   id="programStopInfoOverride${index}"
                                   path="programStopInfo[${status.index}].overrideConstraints"
                                   cssClass="f_singleOverrideChecked" />
                            </c:if>
                            <label for="programStopInfoOverride${index}">
                                <spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody>
                            </label>
                        </td>
                        <td class="violations">
                            <c:if test="${empty violationsForProgram}">
                                <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.noConstraintsViolatedForProgram"/>
                            </c:if>
                            <c:if test="${!empty violationsForProgram}">
                                    <ul>
                                        <c:forEach var="violation" items="${violationsForProgram.constraintContainers}">
                                            <li><spring:escapeBody htmlEscape="true"><cti:msg2 key="${violation.constraintTemplate}"/></spring:escapeBody></li>
                                        </c:forEach>
                                    </ul>
                            </c:if>
                        </td>
                    </c:if>
                    <c:if test="${!constraintsViolated}">
                        <td><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></td>
                    </c:if>
                </tr>
                <c:set var="index" value="${index + 1}"/>
            </c:if>
        </c:forEach>
    </table>
    </div>
    </tags:abstractContainer>

    <c:forEach var="programStopInfo" varStatus="status" items="${backingBean.programStopInfo}">
        <form:hidden path="programStopInfo[${status.index}].useStopGear"/>
        <form:hidden path="programStopInfo[${status.index}].programId"/>
        <form:hidden path="programStopInfo[${status.index}].stopProgram"/>
        <form:hidden path="programStopInfo[${status.index}].gearNumber"/>
    </c:forEach>

    <br>
    <c:if test="${!constraintsViolated}">
        <p><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${constraintsViolated}">
        <c:if test="${overrideAllowed}">
            <p><input type="checkbox" id="overrideAllConstraints" name="overrideAllConstraints">
            <label for="overrideAllConstraints">
                <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.overrideAllConstraints"/>
            </label></p>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <cti:url var="backUrl" value="/spring/dr/program/stop/multipleDetails">
        </cti:url>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.backButton"/>"
            onclick="submitFormViaAjax('drDialog', 'stopMultipleProgramsForm', '${backUrl}')"/>
        <c:if test="${!constraintsViolated || overrideAllowed}">
            <input type="submit" id="okButton" value="<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.okButton"/>"/>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.cancelButton"/>"
            onclick="jQuery('#drDialog').hide()"/>
    </div>
</form:form>
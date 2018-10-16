<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.stopMultiplePrograms">

<script type="text/javascript">

$(function () {

    overideAllChecked = function() {
        var allChecked = $("#overrideAllConstraints").is(":checked");
        for (var index = 0; index < ${numProgramsToStop}; index++) {
            var checkbox = $("#programStopInfoOverride" + index);
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
            var checkbox = $("#programStopInfoOverride" + index);
            if (checkbox.length) {// implies *not* zero
                allChecked = (allChecked && checkbox.is(":checked"));
                if (!allChecked) break;
            }
        }

        if (allChecked) {
            $("#okButton").removeAttr("disabled");
            $("#overrideAllConstraints").attr("checked","checked");
        } else {
            $("#okButton").attr("disabled","disabled");
            $("#overrideAllConstraints").removeAttr("checked");
        }
        
    }

    singleOverrideChecked = function(event) {
        updateAllOverridesChecked();
    }
    
    $("#overrideAllConstraints").click(overideAllChecked);
    $(".js-singleOverrideChecked").click(singleOverrideChecked);
    updateAllOverridesChecked();
});
</script>

<cti:url var="submitUrl" value="/dr/program/stop/stopMultiple"/>
<form:form id="stopMultipleProgramsForm" modelAttribute="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'stopMultipleProgramsForm')">
    <cti:csrfToken/>
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" name="from" value="constraints"/>
    <form:hidden path="stopNow"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="autoObserveConstraints"/>

    <h4 class="dialogQuestion stacked">
        <c:if test="${!empty controlArea}">
            <cti:msg2 key=".confirmQuestion.controlArea"
                htmlEscape="true" argument="${controlArea.name}"/>
        </c:if>
        <c:if test="${!empty scenario}">
            <cti:msg2 key=".confirmQuestion.scenario"
                htmlEscape="true" argument="${scenario.name}"/>
        </c:if>
    </h4>

    <cti:msg2 var="boxTitle" key=".programs"/>
    <tags:sectionContainer title="${boxTitle}">
        <div class="scroll-md">
            <table class="compact-results-table dashed" id="stopMultipleProgramsOverridePrograms">
                <thead>
                    <tr>
                        <c:if test="${constraintsViolated}">
                            <th><cti:msg2 key=".overrideProgramName"/></th>
                            <th><cti:msg2 key=".violations"/></th>
                        </c:if>
                        <c:if test="${!constraintsViolated}">
                            <th><cti:msg2 key=".programName"/></th>
                        </c:if>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:set var="index" value="0"/>
                    <c:forEach var="programStopInfo" varStatus="status" items="${backingBean.programStopInfo}">
                        <c:if test="${backingBean.programStopInfo[status.index].stopProgram}">
                            <c:set var="program" value="${programsByProgramId[backingBean.programStopInfo[status.index].programId]}"/>
                            <c:set var="violationsForProgram" value="${violationsByProgramId[backingBean.programStopInfo[status.index].programId]}"/>
                            <tr>
                                <c:if test="${constraintsViolated}">
                                    <td class="override">
                                        <c:if test="${!empty violationsForProgram && overrideAllowed}">
                                            <form:checkbox
                                               id="programStopInfoOverride${index}"
                                               path="programStopInfo[${status.index}].overrideConstraints"
                                               cssClass="js-singleOverrideChecked" />
                                        </c:if>
                                        <label for="programStopInfoOverride${index}">${fn:escapeXml(program.name)}</label>
                                    </td>
                                    <td class="violations">
                                        <c:if test="${empty violationsForProgram}">
                                            <cti:msg2 key=".noConstraintsViolatedForProgram"/>
                                        </c:if>
                                        <c:if test="${!empty violationsForProgram}">
                                                <ul>
                                                    <c:forEach var="violation" items="${violationsForProgram.constraintContainers}">
                                                        <li><cti:msg2 key="${violation.constraintTemplate}" htmlEscape="true"/></li>
                                                    </c:forEach>
                                                </ul>
                                        </c:if>
                                    </td>
                                </c:if>
                                <c:if test="${!constraintsViolated}">
                                    <td>${fn:escapeXml(program.name)}</td>
                                </c:if>
                            </tr>
                            <c:set var="index" value="${index + 1}"/>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </tags:sectionContainer>

    <c:forEach var="programStopInfo" varStatus="status" items="${backingBean.programStopInfo}">
        <form:hidden path="programStopInfo[${status.index}].useStopGear"/>
        <form:hidden path="programStopInfo[${status.index}].programId"/>
        <form:hidden path="programStopInfo[${status.index}].stopProgram"/>
        <form:hidden path="programStopInfo[${status.index}].gearNumber"/>
    </c:forEach>

    <br>
    <c:if test="${!constraintsViolated}">
        <p><cti:msg2 key=".noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${constraintsViolated}">
        <c:if test="${overrideAllowed}">
            <p><input type="checkbox" id="overrideAllConstraints" name="overrideAllConstraints">
            <label for="overrideAllConstraints">
                <cti:msg2 key=".overrideAllConstraints"/>
            </label></p>
        </c:if>
    </c:if>

    <div class="action-area">
        <c:if test="${!constraintsViolated || overrideAllowed}">
            <cti:button id="okButton" nameKey="ok" classes="primary action" type="submit"/>
        </c:if>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        <cti:url var="backUrl" value="/dr/program/stop/multipleDetails"/>
        <cti:button nameKey="back" onclick="submitFormViaAjax('drDialog', 'stopMultipleProgramsForm', '${backUrl}')"/>
    </div>
</form:form>
</cti:msgScope>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<script type="text/javascript">
overrideConstraintsChecked = function() {
    <%-- never called if there are no violations so we don't have to check that --%>
    $('okButton').disabled = !$('overrideConstraints').checked;
}
</script>

<cti:url var="submitUrl" value="/spring/dr/program/start/start"/>
<form:form id="startProgramForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'startProgramForm');">
    <form:hidden path="programId"/>
    <form:hidden path="gearNumber"/>
    <input type="hidden" name="from" value="constraints"/>
    <form:hidden path="startNow"/>
    <form:hidden path="now"/>
    <form:hidden path="startDate"/>
    <form:hidden path="scheduleStop"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="autoObserveConstraints"/>
    <form:hidden path="addAdjustments"/>
    <c:if test="${backingBean.addAdjustments}">
        <c:forEach var="index" begin="0" end="${fn:length(backingBean.gearAdjustments) - 1}">
            <form:hidden path="gearAdjustments[${index}].beginTime"/>
            <form:hidden path="gearAdjustments[${index}].endTime"/>
            <form:hidden path="gearAdjustments[${index}].adjustmentValue"/>
        </c:forEach>
    </c:if>

    <h1 class="dialogQuestion"><cti:msg key="yukon.web.modules.dr.program.startProgram.confirmQuestion"
        argument="${program.name}"/></h1>

    <dr:programStartInfo page="startProgram"/>

    <c:if test="${empty violations.violations}">
        <p><cti:msg key="yukon.web.modules.dr.program.startProgram.noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${!empty violations.violations}">
        <p><cti:msg key="yukon.web.modules.dr.program.startProgram.constraintsViolated"/></p>
        <ul>
            <c:forEach var="violation" items="${violations.violations}">
                <li>${violation}</li>
            </c:forEach>
        </ul>
        <br>

        <c:if test="${overrideAllowed}">
            <p><input type="checkbox" id="overrideConstraints"
                name="overrideConstraints" onclick="overrideConstraintsChecked();">
            <label for="overrideConstraints">
                <cti:msg key="yukon.web.modules.dr.program.startProgram.overrideConstraints"/>
            </label></p>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <cti:url var="backUrl" value="/spring/dr/program/start/details">
            <cti:param name="fromBack" value="true"/>
        </cti:url>
        <c:if test="${backingBean.addAdjustments}">
            <cti:url var="backUrl" value="/spring/dr/program/start/gearAdjustments"/>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.backButton"/>"
            onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
        <c:if test="${empty violations.violations || overrideAllowed}">
            <input type="submit" id="okButton" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.okButton"/>"/>
            <c:if test="${!empty violations.violations}">
                <script type="text/javascript">$('okButton').disabled = true</script>
                <!-- TODO:  need to disable submit via text field too -->
            </c:if>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>

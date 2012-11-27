<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<script type="text/javascript">
overrideConstraintsChecked = function() {
    <%-- never called if there are no violations so we don't have to check that --%>
    $('okButton').disabled = !$('overrideConstraints').checked;
}
</script>

<cti:url var="submitUrl" value="/dr/program/stop/stop"/>
<form:form id="stopProgramForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'stopProgramForm');">
    <form:hidden path="programId"/>
    <form:hidden path="stopNow"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="useStopGear"/>
    <form:hidden path="gearNumber"/>

    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.program.stopProgram.confirmQuestion"
        	htmlEscape="true" argument="${program.name}"/>
    </h1>

    <p>
    <c:if test="${backingBean.stopNow}">
        <cti:msg key="yukon.web.modules.dr.program.stopProgram.stoppingNow"/>
    </c:if>
    <c:if test="${!backingBean.stopNow}">
        <cti:formatDate var="formattedStopDate" type="BOTH" value="${backingBean.stopDate}"/>
        <cti:msg key="yukon.web.modules.dr.program.stopProgram.stoppingAt"
            argument="${formattedStopDate}"/>
    </c:if>
    </p>

    <c:if test="${empty violations.constraintContainers}">
        <p><cti:msg key="yukon.web.modules.dr.program.stopProgram.noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${!empty violations.constraintContainers}">
        <p><cti:msg key="yukon.web.modules.dr.program.stopProgram.constraintsViolated"/></p>
        <ul>
            <c:forEach var="violation" items="${violations.constraintContainers}">
                <li><i:inline key="${violation.constraintTemplate}"/></li>
            </c:forEach>
        </ul>
        <br>

        <c:if test="${overrideAllowed}">
            <p><input type="checkbox" id="overrideConstraints"
                name="overrideConstraints" onclick="overrideConstraintsChecked();">
            <label for="overrideConstraints">
                <cti:msg key="yukon.web.modules.dr.program.stopProgram.overrideConstraints"/>
            </label></p>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <cti:url var="backUrl" value="/dr/program/stop/details"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.stopProgram.backButton"/>"
            onclick="submitFormViaAjax('drDialog', 'stopProgramForm', '${backUrl}')"/>
        <c:if test="${empty violations.constraintContainers || overrideAllowed}">
            <input type="submit" id="okButton" value="<cti:msg key="yukon.web.modules.dr.program.stopProgram.okButton"/>"/>
            <c:if test="${!empty violations.constraintContainers}">
                <script type="text/javascript">$('okButton').disabled = true</script>
                <!-- TODO:  need to disable submit via text field too -->
            </c:if>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.stopProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>

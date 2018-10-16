<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.stopProgram">

<script type="text/javascript">
overrideConstraintsChecked = function() {
    <%-- never called if there are no violations so we don't have to check that --%>
    $('#okButton').prop('disabled', !$('#overrideConstraints').prop('checked'));
}
</script>

<cti:url var="submitUrl" value="/dr/program/stop/stop"/>
<form:form id="stopProgramForm" modelAttribute="backingBean" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'stopProgramForm');">
    <cti:csrfToken/>
    <form:hidden path="programId"/>
    <form:hidden path="stopNow"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="useStopGear"/>
    <form:hidden path="gearNumber"/>

    <h4 class="dialogQuestion stacked">
        <cti:msg2 key=".confirmQuestion" argument="${program.name}"/>
    </h4>

    <p>
    <c:if test="${backingBean.stopNow}">
        <cti:msg2 key=".stoppingNow"/>
    </c:if>
    <c:if test="${!backingBean.stopNow}">
        <cti:formatDate var="formattedStopDate" type="BOTH" value="${backingBean.stopDate}"/>
        <cti:msg2 key=".stoppingAt" argument="${formattedStopDate}"/>
    </c:if>
    </p>

    <c:if test="${empty violations.constraintContainers}">
        <p><cti:msg2 key=".noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${!empty violations.constraintContainers}">
        <p><cti:msg2 key=".constraintsViolated"/></p>
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
                <cti:msg2 key=".overrideConstraints"/>
            </label></p>
        </c:if>
    </c:if>

    <div class="action-area">
        <c:if test="${empty violations.constraintContainers || overrideAllowed}">
            <cti:button nameKey="ok" id="okButton" classes="primary action" type="submit"/>
            <c:if test="${!empty violations.constraintContainers}">
                <script type="text/javascript">document.getElementById('okButton').disabled = true</script>
                <!-- TODO:  need to disable submit via text field too -->
            </c:if>
        </c:if>
        <cti:url var="backUrl" value="/dr/program/stop/details"/>
        <cti:button nameKey="back" onclick="submitFormViaAjax('drDialog', 'stopProgramForm', '${backUrl}')"/>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
    </div>
</form:form>
</cti:msgScope>
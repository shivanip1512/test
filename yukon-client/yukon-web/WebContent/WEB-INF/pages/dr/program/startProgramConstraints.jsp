<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.startProgram">

<cti:url var="submitUrl" value="/dr/program/start/start"/>
<form:form id="startProgramForm" modelAttribute="backingBean" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'startProgramForm');">
    <cti:csrfToken/>
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

    <h4 class="dialogQuestion">
    	<cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${program.name}"/>
    </h4>

    <dr:programStartInfo page="startProgram"/>

    <c:if test="${empty violations.constraintContainers}">
        <p><cti:msg2 key=".noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${!empty violations.constraintContainers}">
        <p><cti:msg2 key=".constraintsViolated"/></p>
        <ul>
            <c:forEach var="violation" items="${violations.constraintContainers}">
                <li><cti:msg2 key="${violation.constraintTemplate}"/></li>
            </c:forEach>
        </ul>
        <br>

        <c:if test="${overrideAllowed}">
            <p><input type="checkbox" id="overrideConstraints" name="overrideConstraints">
            <label for="overrideConstraints">
                <cti:msg2 key=".overrideConstraints"/>
            </label></p>
        </c:if>
    </c:if>

    <div class="action-area">
        <c:if test="${empty violations.constraintContainers || overrideAllowed}">
            <cti:button nameKey="ok" id="okButton" classes="primary action" type="submit" busy="true"/>
        </c:if>
        <cti:url var="backUrl" value="/dr/program/start/details">
            <cti:param name="fromBack" value="true"/>
        </cti:url>
        <c:if test="${backingBean.addAdjustments}">
            <cti:url var="backUrl" value="/dr/program/start/gearAdjustments"/>
        </c:if>
        <cti:button nameKey="back" onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
    </div>
</form:form>
</cti:msgScope>
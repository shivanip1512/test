<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
</script>

<h1 class="dialogQuestion">
    <cti:msg key="yukon.web.modules.dr.program.startProgram.enterdjustments"
        argument="${program.name}"/>
</h1>

<cti:url var="submitUrl" value="/spring/dr/program/startProgramConstraints"/>
<form:form id="startProgramForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'startProgramForm');">
    <form:hidden path="programId"/>
    <form:hidden path="gearNumber"/>
    <form:hidden path="startNow"/>
    <form:hidden path="startDate"/>
    <form:hidden path="scheduleStop"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="autoObserveConstraints"/>
    <form:hidden path="addAdjustments"/>
    <form:hidden path="numAdjustments"/>

    <c:forEach var="timeSlot" varStatus="status" items="${timeSlots}">
        <cti:formatDate type="TIME24H" value="${timeSlot.startTime}"/> -
        <cti:formatDate type="TIME24H" value="${timeSlot.endTime}"/>
        <form:input path="gearAdjustments[${status.count-1}]"/><br>
    </c:forEach>

    <br>
    <br>

    <cti:msg var="submitButtonText" key="yukon.web.modules.dr.program.startProgram.nextButton"/>
    <c:if test="${backingBean.autoObserveConstraints}">
        <cti:msg var="submitButtonText" key="yukon.web.modules.dr.program.startProgram.okButton"/>
    </c:if>
    <div class="actionArea">
        <cti:url var="backUrl" value="/spring/dr/program/startProgramDetails"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.backButton"/>"
            onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
        <input type="submit" value="${submitButtonText}"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>

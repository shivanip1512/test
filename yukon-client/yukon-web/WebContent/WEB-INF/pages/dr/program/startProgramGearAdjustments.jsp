<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
</script>


<c:choose>
	<c:when test="${not empty program}">
		<h1 class="dialogQuestion">
		    <cti:msg key="yukon.web.modules.dr.program.startProgram.enterAdjustments"
		        argument="${program.name}"/>
		</h1>
		<cti:url var="submitUrl" value="/spring/dr/program/startProgramConstraints"/>
	</c:when>
	<c:otherwise>
		<h1 class="dialogQuestion">
		    <cti:msg key="yukon.web.modules.dr.program.startProgram.enterMultipleAdjustments"
		        argument="${program.name}"/>
		</h1>
		<cti:url var="submitUrl" value="/spring/dr/program/startMultipleProgramsConstraints"/>
	</c:otherwise>
</c:choose>

<form:form id="startProgramForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'startProgramForm');">

<c:choose>
	<c:when test="${not empty program}">
	    <form:hidden path="programId"/>
	    <form:hidden path="gearNumber"/>
    </c:when>
	<c:otherwise>
		<form:hidden path="controlAreaId"/>
	    <form:hidden path="scenarioId"/>
    	<c:forEach var="programStartInfo" varStatus="status" items="${backingBean.programStartInfo}">
	        <form:hidden path="programStartInfo[${status.index}].programId"/>
	        <form:hidden path="programStartInfo[${status.index}].startProgram"/>
	        <form:hidden path="programStartInfo[${status.index}].gearNumber"/>
	    </c:forEach>
	</c:otherwise>
</c:choose>
    
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
	    <c:choose>
			<c:when test="${not empty program}">
		        <cti:url var="backUrl" value="/spring/dr/program/startProgramDetails"/>
		        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.backButton"/>"
		            onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
           	</c:when>
			<c:otherwise>
		        <cti:url var="backUrl" value="/spring/dr/program/startMultipleProgramsDetails"/>
		        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.backButton"/>"
		            onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
			</c:otherwise>
		</c:choose>
            
        <input type="submit" value="${submitButtonText}"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>

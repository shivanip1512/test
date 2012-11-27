<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:flashScopeMessages/>

<c:if test="${!empty program}">
    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.program.startProgram.enterAdjustments"
			htmlEscape="true" argument="${program.name}"/>
    </h1>
    <cti:url var="submitUrl" value="/dr/program/start/constraints"/>
    <dr:programStartInfo page="startProgram"/>
</c:if>
<c:if test="${empty program}">
    <h1 class="dialogQuestion">
        <cti:msg key="yukon.web.modules.dr.program.startProgram.enterMultipleAdjustments"/>
    </h1>
    <cti:url var="submitUrl" value="/dr/program/start/multipleConstraints"/>
    <dr:programStartInfo page="startMultiplePrograms"/>
</c:if>


<form:form id="startProgramForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'startProgramForm');">

    <input type="hidden" name="from" value="gear_adjustments"/>
    <c:if test="${!empty program}">
        <form:hidden path="programId"/>
        <form:hidden path="gearNumber"/>
    </c:if>
    <c:if test="${empty program}">
        <form:hidden path="controlAreaId"/>
        <form:hidden path="scenarioId"/>
        <c:forEach var="programStartInfo" varStatus="status" items="${backingBean.programStartInfo}">
            <form:hidden path="programStartInfo[${status.index}].programId"/>
            <form:hidden path="programStartInfo[${status.index}].startProgram"/>
            <form:hidden path="programStartInfo[${status.index}].gearNumber"/>
        </c:forEach>
    </c:if>

    <form:hidden path="startNow"/>
    <form:hidden path="now"/>
    <form:hidden path="startDate"/>
    <form:hidden path="scheduleStop"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="autoObserveConstraints"/>
    <form:hidden path="addAdjustments"/>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.startProgram.gearAdjustments"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable">
        <tr>
            <th><cti:msg key="yukon.web.modules.dr.program.startProgram.timeFrame"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.startProgram.adjustmentValue"/></th>
        </tr>
	    <c:forEach var="gearAdjustment" varStatus="status" items="${backingBean.gearAdjustments}">
	        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>
		            <form:hidden path="gearAdjustments[${status.count-1}].beginTime"/>
		            <form:hidden path="gearAdjustments[${status.count-1}].endTime"/>
		            <cti:formatDate type="TIME24H" value="${gearAdjustment.beginTime}"/> -
		            <cti:formatDate type="TIME24H" value="${gearAdjustment.endTime}"/>
                </td>
                <td>
                    <tags:input path="gearAdjustments[${status.count-1}].adjustmentValue"
		                maxlength="3" size="5"/><br>
                </td>
	        </tr>
	    </c:forEach>
    </table>
    </div>
    </tags:abstractContainer>

    <br>
    <br>

    <cti:msg var="submitButtonText" key="yukon.web.modules.dr.program.startProgram.nextButton"/>
    <c:if test="${backingBean.autoObserveConstraints}">
        <cti:msg var="submitButtonText" key="yukon.web.modules.dr.program.startProgram.okButton"/>
    </c:if>
    <div class="actionArea">
        <c:if test="${empty errors}">
            <cti:url var="backUrl" value="/dr/program/start/details">
                <cti:param name="fromBack" value="true"/>
            </cti:url>
            <c:if test="${empty program}">
                <cti:url var="backUrl" value="/dr/program/start/multipleDetails">
                    <cti:param name="fromBack" value="true"/>
                </cti:url>
            </c:if>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.backButton"/>"
                onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
        </c:if>

        <input type="submit" value="${submitButtonText}"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>

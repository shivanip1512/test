<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.startProgram">

<cti:flashScopeMessages/>

<c:if test="${!empty program}">
    <h4 class="dialogQuestion stacked">
        <cti:msg2 key=".enterAdjustments" htmlEscape="true" argument="${program.name}"/>
    </h4>
    <cti:url var="submitUrl" value="/dr/program/start/constraints"/>
    <dr:programStartInfo page="startProgram"/>
</c:if>
<c:if test="${empty program}">
    <h4 class="dialogQuestion stacked">
        <cti:msg2 key=".enterMultipleAdjustments"/>
    </h4>
    <cti:url var="submitUrl" value="/dr/program/start/multipleConstraints"/>
    <dr:programStartInfo page="startMultiplePrograms"/>
</c:if>


<form:form id="startProgramForm" modelAttribute="backingBean" action="${submitUrl}" onsubmit="return submitFormViaAjax('drDialog', 'startProgramForm');">
    <cti:csrfToken/>
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

    <cti:msg2 var="boxTitle" key=".gearAdjustments"/>
    <tags:sectionContainer title="${boxTitle}">
        <div class="scroll-md">
            <table class="compact-results-table dashed">
                <thead>
                    <tr>
                        <th><cti:msg2 key=".timeFrame"/></th>
                        <th><cti:msg2 key=".adjustmentValue"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="gearAdjustment" varStatus="status" items="${backingBean.gearAdjustments}">
                        <tr>
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
                </tbody>
            </table>
        </div>
    </tags:sectionContainer>

    <div class="action-area">
        <c:if test="${empty errors}">
            <cti:url var="backUrl" value="/dr/program/start/details">
                <cti:param name="fromBack" value="true"/>
            </cti:url>
            <c:if test="${empty program}">
                <cti:url var="backUrl" value="/dr/program/start/multipleDetails">
                    <cti:param name="fromBack" value="true"/>
                </cti:url>
            </c:if>    
            <cti:button nameKey="back" onclick="submitFormViaAjax('drDialog', 'startProgramForm', '${backUrl}')"/>
        </c:if>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
        <c:if test="${backingBean.autoObserveConstraints}">
            <cti:button nameKey="ok" id="okButton" classes="primary action" type="submit"/>
        </c:if>
        <c:if test="${!backingBean.autoObserveConstraints}">
            <cti:button nameKey="next" id="okButton" classes="primary action" type="submit"/>
        </c:if>
    </div>
</form:form>
</cti:msgScope>
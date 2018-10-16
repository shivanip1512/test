<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="adminSetup" page="survey.report">

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".startDate">
        <c:if test="${empty reportConfig.startDate}">
            <i:inline key=".noStartDate"/>
        </c:if>
        <c:if test="${!empty reportConfig.startDate}">
            <cti:formatDate type="DATEHM" value="${reportConfig.startDate}"/>
        </c:if>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".endDate">
        <c:if test="${empty reportConfig.stopDate}">
            <i:inline key=".noEndDate"/>
        </c:if>
        <c:if test="${!empty reportConfig.stopDate}">
            <cti:formatDate type="DATEHM" value="${reportConfig.stopDate}"/>
        </c:if>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".question">
        <i:inline key="yukon.web.surveys.${survey.surveyKey}.${question.questionKey}"/>
    </tags:nameValue2>
</tags:nameValueContainer2>


<cti:simpleReportUrlFromNameTag var="csvUrlBase" viewType="csvView"
    definitionName="${definitionName}" htmlOutput="true"/>
<cti:url var="csvUrl" value="${csvUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <cti:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</cti:url>

<cti:simpleReportUrlFromNameTag var="pdfUrlBase" viewType="pdfView"
    definitionName="${definitionName}" htmlOutput="true"/>
<cti:url var="pdfUrl" value="${pdfUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <cti:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</cti:url>

<div class="reportHeader">
    <strong><i:inline key=".export"/>&nbsp;</strong>
    <cti:button renderMode="labeledImage" nameKey="csvReport" href="${csvUrl}" icon="icon-csv"/> |
    <cti:button renderMode="labeledImage" nameKey="pdfReport" href="${pdfUrl}" icon="icon-csv"/> |
</div>

<table class="results-table">
    <tr>
        <c:forEach var="ci" items="${columnInfo}">
            <th style="text-align:${ci.align};width:${ci.columnWidthPercentage}%"><i:inline key=".${ci.name}"/></th>
        </c:forEach>
    </tr>
    <c:forEach var="rowData" items="${data}">
        <tr>
            <c:forEach var="colData" items="${rowData}" varStatus="colCounter">
                <td>${colData}</td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>

<cti:url var="submitUrl" value="/stars/surveyReport/config"/>
<form:form action="${submitUrl}" modelAttribute="reportConfig">
    <cti:csrfToken/>
    <form:hidden path="accountNumber"/>
    <c:forEach var="answer" items="${reportConfig.answerIds}">
        <input type="hidden" value="${answer}" name="answerId"/>
    </c:forEach>
    <form:hidden path="deviceSerialNumber"/>
    <form:hidden path="includeOtherAnswers"/>
    <form:hidden path="includeUnanswered"/>
    <c:forEach var="program" items="${reportConfig.programIds}">
        <input type="hidden" value="${program}" name="programIds"/>
    </c:forEach>
    <form:hidden path="questionId"/>
    <form:hidden path="reportType"/>
    <form:hidden path="startDate"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="surveyId"/>

    <div class="page-action-area">
        <cti:button type="submit" nameKey="configureReport"/>
    </div>
</form:form>
</cti:standardPage>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="survey" page="report">

<cti:includeCss link="/WebConfig/yukon/styles/operator/survey.css"/>

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
        <c:if test="${empty reportConfig.endDate}">
            <i:inline key=".noEndDate"/>
        </c:if>
        <c:if test="${!empty reportConfig.endDate}">
            <cti:formatDate type="DATEHM" value="${reportConfig.endDate}"/>
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
    <cti:labeledImg key="csvReport" href="${csvUrl}"/> |
    <cti:labeledImg key="pdfReport" href="${pdfUrl}"/>
</div>

<table class="resultsTable">
    <tr>
        <c:forEach var="ci" items="${columnInfo}">
            <th style="text-align:${ci.columnAlignment};width:${ci.columnWidthPercentage}%">${ci.columnName}</th>
        </c:forEach>
    </tr>
    <c:forEach var="rowData" items="${data}">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <c:forEach var="colData" items="${rowData}" varStatus="colCounter">
                <td>${colData}</td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>

</cti:standardPage>

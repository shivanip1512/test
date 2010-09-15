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

<c:if test="${reportConfig.reportType == 'summary'}">
	<cti:simpleReportUrlFromNameTag var="reportUrl" viewType="htmlView"
	    definitionName="surveyResultsSummaryDefinition" htmlOutput="true"
	    viewJsp="BODY"
	    startDate="${reportConfig.start}" endDate="${reportConfig.end}"/>
        <%--
	<cti:simpleReportUrlFromNameTag var="reportUrl" viewType="htmlView"
	    definitionName="surveyResultsSummaryDefinition" htmlOutput="true"
	    viewJsp="BODY" surveyId="${reportConfig.surveyId}"
	    startDate="${reportConfig.start}" endDate="${reportConfig.end}"
	    questionId="${reportConfig.questionId}"
	    answerIds="${reportConfig.answerIdList}"
	    includeOtherAnswers="${reportConfig.includeOtherAnswers}"
        includeUnanswered="${reportConfig.includeUnanswered}"
        programIds="${reportConfig.programIdList}"/>
        --%>
</c:if>
<c:if test="${reportConfig.reportType == 'detail'}">
	<cti:simpleReportUrlFromNameTag var="reportUrl" viewType="htmlView"
	    definitionName="surveyResultsDetailDefinition" htmlOutput="true"
        viewJsp="BODY" surveyId="${reportConfig.surveyId}"
        startDate="${reportConfig.start}" endDate="${reportConfig.end}"
        questionId="${reportConfig.questionId}"
        answerIds="${reportConfig.answerIdList}"
        includeOtherAnswers="${reportConfig.includeOtherAnswers}"
        includeUnanswered="${reportConfig.includeUnanswered}"
        programIds="${reportConfig.programIdList}"
        accountNumber="${reportConfig.accountNumber}"
        deviceSerialNumber="${reportConfig.deviceSerialNumber}"/>
</c:if>

<jsp:include page="${reportUrl}"/>

</cti:standardPage>

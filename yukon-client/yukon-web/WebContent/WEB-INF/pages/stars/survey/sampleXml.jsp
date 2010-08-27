<?xml version="1.0" encoding="UTF-8" ?>
<%@ page contentType="text/xml" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<!-- The .title key is optional although highly recommended. -->
<!-- The .description is also optional -->
<!-- The .pleaseChoose keys are optional.  Excluding this key will make the first answer the default. -->
<properties>

<c:forEach var="survey" items="${surveys}"><c:set var="baseSurveyKey" value="yukon.web.surveys.${survey.surveyKey}"/>
<entry key="${baseSurveyKey}.title">TITLE_HERE</entry>
<entry key="${baseSurveyKey}.description">DESCRIPTION_HERE</entry><c:forEach var="question" items="${questions[survey.surveyId]}"><c:set var="baseQuestionKey" value="${baseSurveyKey}.${question.questionKey}"/>
<entry key="${baseQuestionKey}">QUESTION_HERE</entry><c:if test="${question.questionType == 'DROP_DOWN'}">
<entry key="${baseQuestionKey}.pleaseChoose">Please Choose</entry><c:forEach var="answer" items="${question.answers}">
<entry key="${baseQuestionKey}.${answer.answerKey}">ANSWER_HERE</entry></c:forEach>
<c:if test="${question.textAnswerAllowed}"><entry key="${baseQuestionKey}.other">Other</entry></c:if>
</c:if></c:forEach></c:forEach>

</properties>

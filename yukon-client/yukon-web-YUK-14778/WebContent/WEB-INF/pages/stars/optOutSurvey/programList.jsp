<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.operator.surveyList">

    <tags:nameValueContainer2 tableClass="stacked">
        <tags:nameValue2 nameKey=".surveyName">
            <spring:escapeBody htmlEscape="true">${survey.surveyName}</spring:escapeBody>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".startDate">
            <cti:formatDate type="BOTH" value="${optOutSurvey.startDate}"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".stopDate">
            <cti:msg2 var="noStopDate" key=".noStopDate"/>
            <cti:formatDate type="BOTH" value="${optOutSurvey.stopDate}"
                nullText="${noStopDate}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <tags:sectionContainer2 nameKey="programs">
        <ul class="striped-list simple-list">
            <c:forEach var="programId" items="${optOutSurvey.programIds}">
                <li>
                    ${fn:escapeXml(programNamesById[programId])}
               </li>
            </c:forEach>
        </ul>
    </tags:sectionContainer2>
</cti:msgScope>

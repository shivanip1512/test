<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.dr.surveyList">

    <tags:nameValueContainer2>
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

    <tags:boxContainer2 nameKey="programs" id="programListBox">
    <div class="dialogScrollArea">
        <table class="compactResultsTable">
            <tr>
                <th><i:inline key=".programName"/></th>
            </tr>
	        <c:forEach var="programId" items="${optOutSurvey.programIds}">
	            <tr class="<tags:alternateRow odd="" even="altRow"/>">
	               <td>
	                   <spring:escapeBody htmlEscape="true">${programNamesById[programId]}</spring:escapeBody>
	               </td>
	           </tr>
	        </c:forEach>
        </table>
    </div>
    </tags:boxContainer2>

    <div class="actionArea">
        <cti:button nameKey="ok" onclick="parent.$('ajaxDialog').hide()"/>
    </div>

</cti:msgScope>

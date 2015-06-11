<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="surveyList">

<dt:pickerIncludes/>
<cti:includeScript link="/resources/js/pages/yukon.surveys.optOut.js"/>

<cti:msg2 var="addTitle" key=".addSurveyTitle"/>
<cti:msg2 var="editTitle" key=".edit.title"/>
<cti:msg2 var="deleteTitle" key=".delete.title"/>
<cti:msg2 var="programsTitle" key=".programListTitle"/>
<div id="survey-popup" class="dn" data-add-title="${addTitle}" data-edit-title="${editTitle}" data-delete-title="${deleteTitle}"></div>
<div id="more-programs-popup" title="${programsTitle}" class="dn"></div>
    
<c:if test="${optOutSurveys.hitCount == 0}">
    <span class="empty-list"><i:inline key=".noResults"/></span>
</c:if>
<div data-url="<cti:url value="/stars/optOutSurvey/list"/>" data-static>
    <c:if test="${optOutSurveys.hitCount > 0}">
        <table class="compact-results-table has-actions">
            <thead>
                <tr>
                    <th><i:inline key=".surveyName"/></th>
                    <th><i:inline key=".programs"/></th>
                    <th><i:inline key=".startDate"/></th>
                    <th><i:inline key=".stopDate"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            
                <cti:msg2 var="noStopDate" key=".noStopDate"/>
                <cti:checkRolesAndProperties value="OPERATOR_SURVEY_EDIT">
                    <c:set var="surveyEditPermission" value="true" />
                </cti:checkRolesAndProperties>
    
                <c:forEach var="optOutSurvey" items="${optOutSurveys.resultList}">
                    <c:set var="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>
                    <tr>
                        <td>
                            <c:choose>
                                <c:when test="${not empty surveyEditPermission}">
                                    <cti:url var="surveyUrl" value="/stars/survey/edit">
                                        <cti:param name="surveyId" value="${optOutSurvey.surveyId}"/>
                                    </cti:url>
                                    <a href="${surveyUrl}">${fn:escapeXml(optOutSurvey.surveyName)}</a>
                                </c:when>
                                <c:otherwise>${fn:escapeXml(optOutSurvey.surveyName)}</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <c:forEach var="programId" items="${optOutSurvey.programIds}" end="2">
                                ${fn:escapeXml(programNamesById[programId])}<br>
                            </c:forEach>
                            <c:if test="${fn:length(optOutSurvey.programIds) > 3}">
                                <a class="js-more-programs" data-survey-id="${optOutSurveyId}" href="javascript:void(0)">
                                    <span><i:inline key=".morePrograms"/></span>
                                </a>
                            </c:if>
                        </td>
                        <td><cti:formatDate type="BOTH" value="${optOutSurvey.startDate}"/></td>
                        <td>
                            <cti:formatDate type="BOTH" value="${optOutSurvey.stopDate}" nullText="${noStopDate}"/>
                            <cm:dropdown triggerClasses="fr">
                                <cm:dropdownOption classes="js-edit-survey" icon="icon-pencil" 
                                    data-survey-id="${optOutSurveyId}"
                                    key="components.button.edit.label"/>
                                <cm:dropdownOption classes="js-delete-survey" icon="icon-cross" 
                                    data-survey-id="${optOutSurveyId}"
                                    key="components.button.delete.label"/>
                            </cm:dropdown>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
    <tags:pagingResultsControls result="${optOutSurveys}" adjustPageCount="true"/>
</div>

<div class="action-area">
    <cti:button icon="icon-plus-green" classes="js-add-survey" nameKey="add"/>
</div>

</cti:standardPage>
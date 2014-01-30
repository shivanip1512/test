<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="operator" page="surveyList">
    <dt:pickerIncludes/>

    <tags:simpleDialog id="ajaxDialog"/>
    <cti:includeScript link="/JavaScript/picker.js"/>
    <cti:includeScript link="/JavaScript/simpleDialog.js"/>
    <cti:includeScript link="/JavaScript/tableCreation.js"/>
    <cti:includeScript link="/JavaScript/yukon.surveys.optOut.js"/>
    <cti:msg2 var="addSurveyTitle" key=".addSurveyTitle" javaScriptEscape="true"/>

    <cti:url var="baseUrl" value="/stars/optOutSurvey/list"/>

    <cti:msg2 var="boxTitle" key=".optOutSurveys"/>
    <c:if test="${optOutSurveys.hitCount == 0}">
        <i:inline key=".noResults"/>
    </c:if>
    <c:if test="${optOutSurveys.hitCount > 0}">
    <cti:msg2 var="programListTitle" key=".programListTitle" javaScriptEscape="true"/>
    <cti:msg2 var="okText" key="yukon.common.okButton" javaScriptEscape="true"/>
        <table id="optOutSurveyList" class="compact-results-table row-highlighting" data-ok-text="${okText}" data-dialog-title="${programListTitle}">
            <thead>
            <tr>
                <th><i:inline key=".surveyName"/></th>
                <th><i:inline key=".programs"/></th>
                <th><i:inline key=".startDate"/></th>
                <th><i:inline key=".stopDate"/></th>
                <th><i:inline key=".actions"/></th>
            </tr>
            </thead>
            <tfoot></tfoot>
            <cti:msg2 var="noStopDate" key=".noStopDate"/>
            <tbody>
            
            <cti:checkRolesAndProperties value="OPERATOR_SURVEY_EDIT">
                <c:set var="surveyEditPermission" value="true" />
            </cti:checkRolesAndProperties>

            <c:forEach var="optOutSurvey" items="${optOutSurveys.resultList}">
                <c:set var="optOutSurveyId" value="${optOutSurvey.optOutSurveyId}"/>
                <tr>
                    <td>
                        <c:if test="${not empty surveyEditPermission}">
                            <cti:url var="surveyUrl" value="/stars/survey/edit">
                                <cti:param name="surveyId" value="${optOutSurvey.surveyId}"/>
                            </cti:url>
                            <a href="${surveyUrl}">
                        </c:if>
                            ${fn:escapeXml(optOutSurvey.surveyName)}
                        <c:if test="${not empty surveyEditPermission}">
                            </a>
                        </c:if>
                    </td>
                    <td>
					    <c:forEach var="programId" items="${optOutSurvey.programIds}" end="2">
                            ${fn:escapeXml(programNamesById[programId])}
                            <br />
					    </c:forEach>
			            <c:if test="${fn:length(optOutSurvey.programIds) > 3}">
			                <cti:url var="programListUrl" value="/stars/optOutSurvey/programList">
			                    <cti:param name="optOutSurveyId" value="${optOutSurveyId}"/>
			                </cti:url>
                            <a class="more-programs" data-list-url="${programListUrl}" href="javascript:void(0)"><i:inline key=".morePrograms"/></a>
                        </c:if>
                    </td>
                    <td>
                        <cti:formatDate type="BOTH" value="${optOutSurvey.startDate}"/>
                    </td>
                    <td>
                        <cti:formatDate type="BOTH" value="${optOutSurvey.stopDate}"
                            nullText="${noStopDate}"/>
                    </td>
                    <td>
                        <cti:url var="editUrl" value="/stars/optOutSurvey/edit">
                            <cti:param name="optOutSurveyId"
                                value="${optOutSurvey.optOutSurveyId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="ajaxDialog"
                            nameKey="edit" skipLabel="true" 
                            actionUrl="${editUrl}" icon="icon-pencil"/>
                        <cti:url var="deleteUrl" value="/stars/optOutSurvey/confirmDelete">
                            <cti:param name="optOutSurveyId" value="${optOutSurveyId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="ajaxDialog"
                            nameKey="delete" actionUrl="${deleteUrl}"
                            skipLabel="true" icon="icon-cross"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
    <tags:pagingResultsControls baseUrl="${baseUrl}"  result="${optOutSurveys}" adjustPageCount="true"/>

    <cti:url var="addUrl" value="/stars/optOutSurvey/edit"/>
    <form id="addForm" action="${addUrl}">
        <div class="action-area">
            <cti:url var="addUrl" value="/stars/optOutSurvey/edit"/>
            <cti:button type="button" icon="icon-plus-green" classes="add-survey" nameKey="add" data-dialog-title="${addSurveyTitle}" data-add-url="${addUrl}"/>
        </div>
    </form>
</cti:standardPage>

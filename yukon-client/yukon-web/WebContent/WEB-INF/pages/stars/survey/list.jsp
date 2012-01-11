<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="survey" page="list">
    <cti:includeScript link="/JavaScript/ajaxDialog.js"/>

    <div id="ajaxDialog"></div>

    <cti:url var="addUrl" value="editDetails"/>
    <script type="text/javascript">
    jQuery(document).ready(function() {
        jQuery('#addSurveyBtn').click(function() {
            jQuery('#ajaxDialog').load('${addUrl}');
        });

        jQuery(document).bind('yukonDetailsUpdated', function(event, newUrl) {
            jQuery('#ajaxDialog').dialog('close');
            window.location = newUrl;
        });
    });
    </script>

    <c:set var="baseUrl" value="list"/>
    <tags:pagedBox2 nameKey="surveys" searchResult="${surveys}" baseUrl="${baseUrl}">
        <c:if test="${surveys.hitCount == 0}">
            <cti:msg2 key=".noResults"/>
        </c:if>
        <c:if test="${surveys.hitCount > 0}">
            <table id="surveyList" class="compactResultsTable rowHighlighting">
                <tr>
                    <th><cti:msg2 key=".name"/></th>
                    <th><cti:msg2 key=".key"/></th>
                    <th><cti:msg2 key=".actions"/></th>
                </tr>
                <c:forEach var="survey" items="${surveys.resultList}">
                    <c:set var="surveyId" value="${survey.surveyId}"/>
                    <c:url var="surveyUrl" value="edit">
                        <c:param name="surveyId" value="${surveyId}"/>
                    </c:url>
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><a href="${surveyUrl}">${fn:escapeXml(survey.surveyName)}</a></td>
                        <td>${survey.surveyKey}</td>
                        <td>
                            <cti:url var="deleteUrl" value="delete">
                                <cti:param name="surveyId" value="${surveyId}"/>
                            </cti:url>
                            <dialog:confirm on="#deleteBtn${surveyId}" nameKey="confirmDelete"
                                argument="${survey.surveyName}"/>
                            <cti:button id="deleteBtn${surveyId}" nameKey="delete" renderMode="image" href="${deleteUrl}" />
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <div class="actionArea">
            <cti:url var="sampleXmlUrl" value="sampleXml"/>
            <cti:button nameKey="sampleXml" href="${sampleXmlUrl}"/>
            <cti:button id="addSurveyBtn" nameKey="add" styleClass="f_blocker"/>
        </div>
    </tags:pagedBox2>
</cti:standardPage>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>

<cti:standardPage module="survey" page="list">
    <tags:simpleDialog id="ajaxDialog"/>

    <c:set var="baseUrl" value="/spring/stars/survey/list"/>
    <cti:url var="submitUrl" value="${baseUrl}"/>

    <cti:msg2 var="boxTitle" key=".surveys"/>
    <tags:pagedBox title="${boxTitle}" searchResult="${surveys}"
        baseUrl="${baseUrl}">
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
                    <c:url var="surveyUrl" value="/spring/stars/survey/edit">
                        <c:param name="surveyId" value="${surveyId}"/>
                    </c:url>    
                    <tr class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><a href="${surveyUrl}"><spring:escapeBody
                            htmlEscape="true">${survey.surveyName}</spring:escapeBody></a></td>
                        <td>${survey.surveyKey}</td>
                        <td>
                            <cti:url var="deleteUrl" value="/spring/stars/survey/confirmDelete">
                                <cti:param name="surveyId" value="${surveyId}"/>
                            </cti:url>
                            <tags:simpleDialogLink2 dialogId="ajaxDialog"
                                nameKey="delete" actionUrl="${deleteUrl}"
                                skipLabel="true"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <div class="actionArea">
            <cti:url var="sampleXmlUrl" value="/spring/stars/survey/sampleXml"/>
            <input type="button" value="<cti:msg2 key=".sampleXml"/>"
                onclick="window.location='${sampleXmlUrl}'">

            <cti:url var="addUrl" value="/spring/stars/survey/editDetails"/>
            <input type="button" value="<cti:msg2 key=".add"/>"
                onclick="openSimpleDialog('ajaxDialog', '${addUrl}', '<cti:msg2 key=".addTitle"/>')">
        </div>
    </tags:pagedBox>
</cti:standardPage>

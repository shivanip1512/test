<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="adminSetup" page="operatorLogin.home">

    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
            <tags:boxContainer2 nameKey="pageName" styleClass="operatorLogins">
                <table class="compactResultsTable rowHighlighting">
                    <th><i:inline key=".username" /></th>
                    <th style="width: 6em;"><i:inline key=".loginEnabled" /></th>
                    <c:forEach items="${operatorLogins}" var="login">
                        <tr class="<tags:alternateRow odd="" even="altRow"/>">
                            <td>
                                <b><a href="${baseUrl}/view?operatorLoginId=${login.userID}&ecId=${ecId}">
                                   <spring:escapeBody htmlEscape="true">${login}</spring:escapeBody></a></b>
                            </td>
                            <td >
                                <span class="icon ${login.loginStatus}"/>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </tags:boxContainer2>

            <cti:url var="operatorLoginCreateUrl" value="${baseUrl}/new">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
            <div class="pageActionArea">
                <cti:button key="create" href="${operatorLoginCreateUrl}"/>
            </div>
        </cti:dataGridCell>
    </cti:dataGrid>
    
</cti:standardPage>
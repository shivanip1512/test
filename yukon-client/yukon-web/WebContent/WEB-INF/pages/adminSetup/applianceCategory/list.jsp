<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="adminSetup" page="applianceCategory.list">

<tags:boxContainer2 nameKey="applianceCategories" hideEnabled="false">
    <c:if test="${empty applianceCategories}">
        <i:inline key=".noApplianceCategories"/>
    </c:if>
    <c:if test="${!empty applianceCategories}">
        <table class="compactResultsTable rowHighlighting">
            <c:forEach var="applianceCategory" items="${applianceCategories}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td valign="top">
                    <c:if test="${!empty applianceCategory.icon}">
                        <img src="<cti:url value="/WebConfig/${applianceCategory.icon}"/>"/>
                    </c:if>
                    </td>
                    <td valign="top">
                        <cti:url var="viewUrl" value="view">
                            <cti:param name="ecId" value="${ecId}"/>
                            <cti:param name="applianceCategoryId" value="${applianceCategory.applianceCategoryId}"/>
                        </cti:url>
                        <a href="${viewUrl}">
                            <spring:escapeBody htmlEscape="true">
                                ${applianceCategory.name}
                            </spring:escapeBody>
                        </a>
                    </td>
                    <td>
                        <c:if test="${applianceCategory.energyCompanyId != param.ecId}">
                            <i:inline key=".inherited"/><br>
                        </c:if>
                        <c:if test="${!applianceCategory.consumerSelectable}">
                            <i:inline key=".notConsumerSelectable"/><br>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>

    <cti:url var="createUrl" value="create">
        <cti:param name="ecId" value="${ecId}"/>
    </cti:url>
    <div class="actionArea">
        <cti:button key="create" href="${createUrl}"/>
    </div>
</tags:boxContainer2>

</cti:standardPage>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="applianceCategory.list">

<c:if test="${empty applianceCategories}">
    <div class="empty-list"><i:inline key=".noApplianceCategories"/></div>
</c:if>
<c:if test="${!empty applianceCategories}">
    <table class="compact-results-table row-highlighting">
        <c:forEach var="applianceCategory" items="${applianceCategories}">
            <tr>
                <td>
                    <c:if test="${!empty applianceCategory.icon}">
                        <img src="<cti:url value="/WebConfig/${applianceCategory.icon}"/>"/>
                    </c:if>
                </td>
                <td>
                    <cti:url var="viewUrl" value="view">
                        <cti:param name="ecId" value="${ecId}"/>
                        <cti:param name="applianceCategoryId" value="${applianceCategory.applianceCategoryId}"/>
                    </cti:url>
                    <a href="${viewUrl}">${fn:escapeXml(applianceCategory.name)}</a>
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
<div class="action-area">
    <cti:button nameKey="create" icon="icon-plus-green" href="${createUrl}"/>
</div>

</cti:standardPage>
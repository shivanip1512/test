<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="adminSetup" page="applianceCategory.PROGRAMS">

<c:choose>
    <c:when test="${empty assignedPrograms.resultList}"><i:inline key=".noAssignedPrograms"/></c:when>
    <c:otherwise>
        <cti:url var="programsUrl" value="programs/list">
            <cti:param name="ecId" value="${ecId}"/>
            <cti:param name="filterBy" value="${filterBy}"/>
        </cti:url>
        <div id="program-list" data-url="${programsUrl}">
            <%@ include file="programs.list.jsp" %>
        </div>
    </c:otherwise>
</c:choose>

</cti:standardPage>
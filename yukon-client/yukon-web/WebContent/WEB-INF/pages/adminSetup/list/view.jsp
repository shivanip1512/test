<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<c:set var="useType" value="${!empty listDefinitions}"/>
<c:if test="${!empty param.useType}">
    <c:set var="useType" value="true"/>
</c:if>

<cti:standardPage module="adminSetup" page="list.VIEW">

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".where">
        <spring:escapeBody htmlEscape="true">${list.whereIsList}</spring:escapeBody>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".label">
        <spring:escapeBody htmlEscape="true">${list.selectionLabel}</spring:escapeBody>
    </tags:nameValue2>
</tags:nameValueContainer2>

<tags:boxContainer2 nameKey="entries" id="selectionListEntries">
    <c:if test="${empty list.yukonListEntries}">
        <i:inline key=".entries.noItems"/>
    </c:if>
    <c:if test="${!empty list.yukonListEntries}">
        <table class="compactResultsTable rowHighlighting">
            <c:forEach var="entry" varStatus="status" items="${list.yukonListEntries}">
                <tr>
                    <td>
                        <c:if test="${empty entry.entryText}">
                            <i:inline key=".noEntryText"/>
                        </c:if>
                        <c:if test="${!empty entry.entryText}">
                            <spring:escapeBody htmlEscape="true">${entry.entryText}</spring:escapeBody>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</tags:boxContainer2>

<c:if test="${!isInherited}">
    <div class="pageActionArea">
        <cti:url var="editUrl" value="edit">
            <cti:param name="ecId" value="${param.ecId}"/>
            <cti:param name="listId" value="${param.listId}"/>
        </cti:url>
        <cti:button key="edit" href="${editUrl}"/>
    </div>
</c:if>

</cti:standardPage>

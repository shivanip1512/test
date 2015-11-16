<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="adminSetup" page="list.list">

<c:if test="${!empty lists}">
    <c:set var="totalNumLists" value="0"/>
    <c:forEach var="listEntry" items="${lists}">
        <c:set var="totalNumLists" value="${totalNumLists + fn:length(listEntry.value)}"/>
    </c:forEach>
    <c:set var="totalNumListsAndHeaders" value="${totalNumLists + fn:length(lists)}"/>
    
    <c:set var="numDisplayedLists" value="0"/>
    <c:set var="lastIndexOnLeft" value="-1"/>
    <c:forEach var="listEntry" varStatus="status" items="${lists}">
        <c:set var="numDisplayedLists" value="${numDisplayedLists + fn:length(listEntry.value) + 1}"/>
        <c:if test="${lastIndexOnLeft == -1 && numDisplayedLists >= totalNumListsAndHeaders / 2}">
            <c:set var="lastIndexOnLeft" value="${status.index}"/>
        </c:if>
    </c:forEach>
</c:if>

<tags:boxContainer2 nameKey="lists">
    <c:if test="${empty lists}">
        <i:inline key=".noLists"/>
    </c:if>
    <c:if test="${!empty lists}">
        <table id="selectionListList">
            <tr>
                <c:set var="listBegin" value="0"/>
                <c:set var="listEnd" value="${lastIndexOnLeft}"/>
                <%@ include file="partialList.jspf" %>
                <c:set var="listBegin" value="${lastIndexOnLeft + 1}"/>
                <c:set var="listEnd" value="${totalNumLists}"/>
                <%@ include file="partialList.jspf" %>
            </tr>
        </table>
    </c:if>
</tags:boxContainer2>

</cti:standardPage>

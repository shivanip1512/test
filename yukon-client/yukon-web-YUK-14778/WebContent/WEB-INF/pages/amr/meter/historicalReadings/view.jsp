<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.amr.widgetClasses.MeterReadingsWidget.historicalReadings">

<input class="js-popup-title" type="hidden" value="${fn:escapeXml(title)}"> 

<div class="form-control clearfix">
    <c:if test="${points.size() > 0}">
        <span class="detail">${resultLimit}</span>
    </c:if>
    <cti:url var="oneMonthUrl" value="${oneMonthUrl}"/>
    <cti:url var="allUrl" value="${allUrl}"/>
    <cti:button label="${oneMonth}" href="${oneMonthUrl}" classes="fr right" icon="icon-page-white-excel"/>
    <cti:button label="${all}" href="${allUrl}" classes="fr left" icon="icon-page-white-excel"/>
</div>

<c:choose>
    <c:when test="${empty points}">
        <div class="empty-list"><i:inline key=".notFound"/></div>
    </c:when>
    <c:otherwise>
        <cti:url value="/meter/historicalReadings/values" var="viewUrl">
            <cti:param name="pointId" value="${pointId}"/>
        </cti:url>
        <div data-url="${viewUrl}"><%@ include file="values.jsp" %></div>
    </c:otherwise>
</c:choose>
</cti:msgScope>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ attribute name="fileParam" %>
<%@ attribute name="sortByParam" %>
<%@ attribute name="showParam" %>
<%@ attribute name="customStartParam" %>
<%@ attribute name="customEndParam" %>

<cti:default var="fileParam" value="${file}"/>
<cti:default var="sortByParam" value="${sortBy}"/>
<cti:default var="showParam" value="${show}"/>
<cti:default var="customStartParam" value="${customStart}"/>
<cti:default var="customEndParam" value="${customEnd}"/>

<cti:url value="menu">
	<cti:param name="file" value="${fileParam}"/>
	<cti:param name="sortBy" value="${sortByParam}"/>
	<cti:param name="show" value="${showParam}"/>
	<c:if test="${showParam eq 'custom'}">
		<cti:formatDate var="customStartDateParam" type="DATE" value="${customStartParam}"/>
		<cti:formatDate var="customEndDateParam" type="DATE" value="${customEndParam}"/>
		<cti:param name="customStart" value="${customStartDateParam}"/>
		<cti:param name="customEnd" value="${customEndDateParam}"/>
	</c:if>
</cti:url>

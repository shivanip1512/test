<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Event Log" module="blank">
<cti:standardMenu menuSelection="" />

<form method="get" action="view">
<select name="categories" multiple="multiple" size="4">
<c:forEach items="${eventCategoryList}" var="eventCategory">
<c:set var="selected" value="${selectedCategories[eventCategory] ? 'selected' : ''}"/>
<option ${selected}>${eventCategory.fullName}</option>
</c:forEach>
</select>
<input value="Filter" type="submit">
</form>
<br>    
<table class="resultsTable">
<tr><th>Event</th><th>Date & Time</th><th>Message</th></tr>
<c:forEach items="${events}" var="event">
<tr>
<td>${event.eventType}</td>
<td><cti:formatDate type="BOTH" value="${event.dateTime}"/></td>
<td><cti:msg key="${event.messageSourceResolvable}"/></td>
</tr>
</c:forEach>
</table>


</cti:standardPage>
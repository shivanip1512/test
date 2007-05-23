<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<!-- Trend -->
<tags:trend title="${title}" pointIds="${pointIds}" startDate="${startDate}" period="${period}" unitOfMeasure="${unitOfMeasure}"></tags:trend>

<!-- Available attribute graphs -->
<span class="widgetText"><b>Available Graphs: </b></span>
<c:set var="notFirst" value="false" scope="page"></c:set>
<c:forEach var="attribute" items="${allAtributes}">
	
	<c:if test="${notFirst}">
		<span class="widgetText">|</span>
	</c:if>
	<tags:widgetLink method="render" title="${attribute.key} data" labelBusy="${attribute.key}" selected="${attributeType == attribute.key}" attributeType="${attribute.key}">${attribute.key}</tags:widgetLink>
	
	<c:set var="notFirst" value="true" scope="page"></c:set>
</c:forEach>

<br/>

<!-- Time periods -->
<span class="widgetText"><b>Available Time Periods: </b></span>
<tags:widgetLink method="render" title="Previous 24 hour's data" labelBusy="1D" selected="${period == 'DAY'}" period="DAY">1D</tags:widgetLink>
<span class="widgetText">|</span>

<tags:widgetLink method="render" title="Previous weeks's data" labelBusy="1W" selected="${period == 'WEEK'}" period="WEEK">1W</tags:widgetLink>
<span class="widgetText">|</span>

<tags:widgetLink method="render" title="Previous month's data" labelBusy="1M" selected="${period == 'MONTH'}" period="MONTH">1M</tags:widgetLink>
<span class="widgetText">|</span>

<tags:widgetLink method="render" title="Previous year's data" labelBusy="1Y" selected="${period == 'YEAR'}" period="YEAR">1Y</tags:widgetLink>


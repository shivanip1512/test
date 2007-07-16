<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript"> 
	
	function toggleWhatsThis(){
		$('whatsThisText').toggle();
	}

</script>

<c:choose>
	<c:when test="${selectedAttributeGraph != null}">

		<c:if test="${selectedAttributeGraph.description != null}">
			<div style="font-size: 10px; text-align: right;">
				<span onmouseover="javascript:toggleWhatsThis()" onmouseout="javascript:toggleWhatsThis()">What's this?</span>
			</div>
			<div id="whatsThisText" class="widgetPopup" style="display:none;">${selectedAttributeGraph.description}</div>
		</c:if>
		
		<!-- Trend -->
		<div style="height: 250px">
			<tags:trend title="${title}" pointIds="${pointIds}" startDate="${startDate}" period="${period}" graphType="${selectedAttributeGraph.graphType}"></tags:trend>
		</div>
		
		<!-- Available attribute graphs -->
		<b>Available Graphs: </b>
		<c:set var="notFirst" value="false" scope="page"></c:set>
		<c:forEach var="attributeGraph" items="${availableAttributeGraphs}">
			
			<c:if test="${notFirst}">
				|
			</c:if>
			<tags:widgetLink method="render" title="${attributeGraph.label} data" labelBusy="${attributeGraph.label}" selected="${selectedAttributeGraph == attributeGraph}" selectedAttributeGraph="${attributeGraph.label}">${attributeGraph.label}</tags:widgetLink>
			
			<c:set var="notFirst" value="true" scope="page"></c:set>
		</c:forEach>
		
		<br/>
		
		<!-- Time periods -->
		<b>Available Time Periods: </b>
		<tags:widgetLink method="render" title="Previous 24 hour's data" labelBusy="1D" selected="${period == 'DAY'}" period="DAY">1D</tags:widgetLink>
		|
		
		<tags:widgetLink method="render" title="Previous weeks's data" labelBusy="1W" selected="${period == 'WEEK'}" period="WEEK">1W</tags:widgetLink>
		|
		
		<tags:widgetLink method="render" title="Previous month's data" labelBusy="1M" selected="${period == 'MONTH'}" period="MONTH">1M</tags:widgetLink>
		|
		
		<tags:widgetLink method="render" title="Previous 3 month's data" labelBusy="3M" selected="${period == 'THREEMONTH'}" period="THREEMONTH">3M</tags:widgetLink>
		|
		
		<tags:widgetLink method="render" title="Previous year's data" labelBusy="1Y" selected="${period == 'YEAR'}" period="YEAR">1Y</tags:widgetLink>
	
	</c:when>
	<c:otherwise>
		No trends available for device
	</c:otherwise>
</c:choose>
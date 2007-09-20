<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/calendarControl.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

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
			<tags:trend title="${title}" pointIds="${pointIds}" startDate="${startDate}" endDate="${endDate}" period="${period}" converterType="${selectedAttributeGraph.converterType}" graphType="${graphType}"></tags:trend>
		</div>
		
		<table cellpadding="1" cellpadding="1">
		
		<!-- Available attribute graphs -->
		<tr>
		<td><b>Graph Type</b></td>
		<td><b>:</b>&nbsp;</td>
		
		<td>
		<c:set var="notFirst" value="false" scope="page"></c:set>
		<c:forEach var="attributeGraph" items="${availableAttributeGraphs}">
			
			<c:if test="${notFirst}">
				|
			</c:if>
			<tags:widgetLink method="render" title="${attributeGraph.label} data" labelBusy="${attributeGraph.label}" selected="${selectedAttributeGraph == attributeGraph}" selectedAttributeLabel="${attributeGraph.label}">${attributeGraph.label}</tags:widgetLink>
			
			<c:set var="notFirst" value="true" scope="page"></c:set>
		</c:forEach>
		</td>
		</tr>
		
		<!-- Time periods -->
		<tr >
		<td ><b>Time Period</b></td>
		<td><b>:</b>&nbsp;</td>
		
		<td>
		<tags:widgetLink method="render" title="Previous 24 hour's data" labelBusy="1D" selected="${period == 'DAY'}" period="DAY" >1D</tags:widgetLink>
		|
		<tags:widgetLink method="render" title="Previous weeks's data" labelBusy="1W" selected="${period == 'WEEK'}" period="WEEK" >1W</tags:widgetLink>
		|
		<tags:widgetLink method="render" title="Previous month's data" labelBusy="1M" selected="${period == 'MONTH'}" period="MONTH" >1M</tags:widgetLink>
		|
		<tags:widgetLink method="render" title="Previous 3 month's data" labelBusy="3M" selected="${period == 'THREEMONTH'}" period="THREEMONTH" >3M</tags:widgetLink>
		|
		<tags:widgetLink method="render" title="Previous year's data" labelBusy="1Y" selected="${period == 'YEAR'}" period="YEAR" >1Y</tags:widgetLink>
		|
		<tags:widgetLink method="render" title="Custom Date Range" labelBusy="Custom" selected="${period == 'NOPERIOD'}" period="NOPERIOD" >Custom</tags:widgetLink>

		</td>
		</tr>
		
		<!-- Optional Data Fields -->
		
		<c:choose>
		<c:when test="${period == 'NOPERIOD'}">
		
			<tr id="optionalDateFields" name="optionalDateFields" >
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			
			<td>

			<tags:dateInputCalendar fieldName="startDateParam" fieldValue="${startDateParam}" />&nbsp;
			
			<tags:dateInputCalendar fieldName="endDateParam" fieldValue="${endDateParam}" />&nbsp;

			<tags:widgetActionRefreshImage method="render" title="Reload Graph Using Custom Dates" imgSrc="/WebConfig/yukon/Icons/arrow_refresh_small.gif" imgSrcHover="/WebConfig/yukon/Icons/arrow_refresh.gif" />
			
			</td>
			</tr>
			
			
		</c:when>
		<c:otherwise>
		
			<input type="hidden" name="startDateParam" value="${startDateParam}">
			<input type="hidden" name="endDateParam" value="${endDateParam}"> 
		
		</c:otherwise>
		</c:choose>
		
		
		<!-- Charting Style (line/bar) -->

		<tr>
		<td><b>Chart Style</b></td>
		<td><b>:</b>&nbsp;</td>
		
		<td>
		<tags:widgetLink method="render" title="Line Graph" labelBusy="Line" selected="${graphType == 'LINE'}" graphType="LINE">Line</tags:widgetLink>
		|
		<tags:widgetLink method="render" title="Column Graph" labelBusy="Column" selected="${graphType == 'COLUMN'}" graphType="COLUMN">Column</tags:widgetLink>
		</td>
		</tr>
		
		
		
		
		</table>
		
		</c:when>
		
		<c:otherwise>
			No trends available for device
		</c:otherwise>

</c:choose>
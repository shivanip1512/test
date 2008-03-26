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
    
    <c:when test="${attributeGraphType != null}">
    
        <%-- DESCRIPTION POPUP --%>
		<c:if test="${attributeGraphType.description != null}">
			<div style="font-size: 10px; text-align: right; position: relative"  onmouseover="toggleWhatsThis()" onmouseout="toggleWhatsThis()">
				What's this?
			<div id="whatsThisText" class="widgetPopup" style="display:none;text-align: left">${attributeGraphType.description}</div>
			</div>
		</c:if>
		
		<%-- THE CHART --%>
		<div style="height: 250px">
			<tags:trend title="${title}" pointIds="${pointId}" startDate="${startDateMillis}" endDate="${stopDateMillis}" period="${period}" converterType="${attributeGraphType.converterType}" graphType="${graphType}"></tags:trend>
		</div>
		
		<table class="compactResultsTable">
		
    		<%-- ATTRIBUTES GRAPH TYPES --%>
    		<tr>
        		<td class="label">Graph Type:</td>
        		
        		<td>
            		<c:set var="notFirst" value="false" scope="page"></c:set>
            		<c:forEach var="agt" items="${availableAttributeGraphs}">
            			
            			<c:if test="${notFirst}">
            				|
            			</c:if>
                        
            			<tags:widgetLink method="render" title="${agt.label} data" labelBusy="${agt.label}" selected="${agt == attributeGraphType}" attribute="${agt.attribute.key}">${agt.label}</tags:widgetLink>
            			
            			<c:set var="notFirst" value="true" scope="page"></c:set>
                        
            		</c:forEach>
        		</td>
    		</tr>
		
    		<%-- PERIOD --%>
    		<tr>
    			<td class="label">Time Period:</td>
    		
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
		
    		<%-- CUSTOM DATES --%>
    		<c:choose>
            
        		<c:when test="${period == 'NOPERIOD'}">
        			<tr id="optionalDateFields" name="optionalDateFields">
        				<td>&nbsp;</td>
        				<td>
            				<tags:dateInputCalendar fieldName="startDateParam" fieldValue="${startDate}" />&nbsp;
            				<tags:dateInputCalendar fieldName="stopDateParam" fieldValue="${stopDate}" />&nbsp;
            				<tags:widgetActionRefreshImage method="render" title="Reload Graph Using Custom Dates" imgSrc="/WebConfig/yukon/Icons/arrow_refresh_small.gif" imgSrcHover="/WebConfig/yukon/Icons/arrow_refresh.gif" />
        				</td>
        			</tr>
        		</c:when>
            
        		<c:otherwise>
        			<input type="hidden" name="startDateParam" value="${startDate}">
        			<input type="hidden" name="stopDateParam" value="${stopDate}"> 
        		</c:otherwise>
                
    		</c:choose>
		
		
    		<%-- CHART STYLE --%>
    		<tr>
    			<td class="label">Chart Style:</td>
    		
    			<td class="last">
    				<tags:widgetLink method="render" title="Line Graph" labelBusy="Line" selected="${graphType == 'LINE'}" graphType="LINE">Line</tags:widgetLink>
    				|
    				<tags:widgetLink method="render" title="Column Graph" labelBusy="Column" selected="${graphType == 'COLUMN'}" graphType="COLUMN">Column</tags:widgetLink>
    			</td>
    		</tr>
        
            <%-- TABULAR DATA REPROTS --%>
            <c:choose>
            	<c:when test="${attributeGraphType.attribute == 'USAGE'}">
            		<c:set var="rawTabularDataLabel" value="Archived Usage Data" />
            	</c:when>
            	<c:otherwise>
            		<c:set var="rawTabularDataLabel" value="Tabular Data" />
            	</c:otherwise>
            </c:choose>
            
    		<tr>
                <td class="label"><b>${rawTabularDataLabel}:</b></td>
                
                <td>
                    <a href="<c:url value="/spring/amr/reports/${tabularDataViewer}?def=rawPointHistoryDefinition&pointId=${pointId}&startDate=${startDateMillis}&stopDate=${stopDateMillis}" />">HTML</a>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}">CSV</cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}">PDF</cti:simpleReportLinkFromNameTag>
                </td>
            </tr>
            
            <c:if test="${attributeGraphType.attribute == 'USAGE'}">
    	        <tr>
    	            <td class="label"><b>Normalized Usage Data:</b></td>
    	            
    	            <td>
    	                <a href="<c:url value="/spring/amr/reports/${tabularDataViewer}?def=normalizedUsageDefinition&pointId=${pointId}&startDate=${startDateMillis}&stopDate=${stopDateMillis}" />">HTML</a>
    	                |
    	                <cti:simpleReportLinkFromNameTag definitionName="normalizedUsageDefinition" viewType="csvView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}">CSV</cti:simpleReportLinkFromNameTag>
    	                |
    	                <cti:simpleReportLinkFromNameTag definitionName="normalizedUsageDefinition" viewType="pdfView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}">PDF</cti:simpleReportLinkFromNameTag>
    	            </td>
    	        </tr>
    	    </c:if>
        
		</table>
		
	</c:when>
		
	<c:otherwise>
		No trends available for device
	</c:otherwise>

</c:choose>
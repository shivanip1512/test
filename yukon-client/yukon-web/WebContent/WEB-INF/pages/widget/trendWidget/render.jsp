<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>

<script type="text/javascript"> 
jQuery(function () {
  jQuery(".trend-settings-anchor").click(function(e) {
      jQuery(".trend-settings").toggle();
      e.stopPropagation();
      return true;
  });
});
</script>

<c:choose>
    
    <c:when test="${attributeGraphType != null}">
    
        <%-- DESCRIPTION POPUP --%>
		<c:if test="${attributeGraphType.description != null}">
            <div class="pr">
				<div class="f-tooltip dn">
	                <cti:msg2 key="${attributeGraphType.description}"/>
	            </div>
				<div class="f-has-tooltip detail">
					<cti:msg2 key=".whatsThis"/>
				</div>
            </div>
		</c:if>
		
		<%-- THE CHART --%>
		<flot:trend title="${title}" pointIds="${pointId}"
			startDate="${startDateMillis}" endDate="${stopDateMillis}"
			interval="${interval}"
			converterType="${attributeGraphType.converterType}"
			graphType="${graphType}"/>

        <div class="fr"><a href="javascript:void(0);" class="trend-settings-anchor"><i class="icon icon-cog"></i></a></div>
		<table class="compactResultsTable trend-settings ${keepSettingsOpen ? '' : 'dn'}">
		
    		<%-- ATTRIBUTES GRAPH TYPES --%>
    		<tr>
        		<td class="label"><i:inline key=".graphType"/></td>
        		
        		<td>
            		<c:set var="notFirst" value="false" scope="page"></c:set>
            		<c:forEach var="agt" items="${availableAttributeGraphs}">
            			
            			<c:if test="${notFirst}">
            				|
            			</c:if>
                        <cti:msg2 var="graphTypeLabel" key="${agt.label}"/>
            			<tags:widgetLink method="render" title="${graphTypeLabel}" labelBusy="${agt.label}" selected="${agt == attributeGraphType}" attribute="${agt.attribute.key}"><i:inline key="${agt.label}"/></tags:widgetLink>
            			
            			<c:set var="notFirst" value="true" scope="page"/>
                        
            		</c:forEach>
        		</td>
    		</tr>
		
    		<%-- PERIOD --%>
    		<tr>
    			<td class="label"><i:inline key=".timePeriod"/></td>
                <cti:msg2 var="prev24Hours" key=".prev24HourData"/>
                <cti:msg2 var="prevWeeksData" key=".prevWeeksData"/>
                <cti:msg2 var="prevMonthsData" key=".prevMonthsData"/>
                <cti:msg2 var="prev3MonthsData" key=".prev3MonthsData"/>
                <cti:msg2 var="prevYearsData" key=".prevYearsData"/>
                <cti:msg2 var="customDateRange" key=".customDateRange"/>
                <cti:msg2 var="oneDay" key=".oneDay"/>
                <cti:msg2 var="oneWeek" key=".oneWeek"/>
                <cti:msg2 var="oneMonth" key=".oneMonth"/>
                <cti:msg2 var="threeMonths" key=".threeMonths"/>
                <cti:msg2 var="oneYear" key=".oneYear"/>
                <cti:msg2 var="custom" key=".custom"/>
    		
    			<td>
        			<tags:widgetLink method="render" title="${prev24Hours}" labelBusy="${oneDay}" selected="${period == 'DAY'}" period="DAY" >${oneDay}</tags:widgetLink>
        			|
        			<tags:widgetLink method="render" title="${prevWeeksData}" labelBusy="${oneWeek}" selected="${period == 'WEEK'}" period="WEEK" >${oneWeek}</tags:widgetLink>
        			|
        			<tags:widgetLink method="render" title="${prevMonthsData}" labelBusy="${oneMonth}" selected="${period == 'MONTH'}" period="MONTH" >${oneMonth}</tags:widgetLink>
        			|
        			<tags:widgetLink method="render" title="${prev3MonthsData}" labelBusy="${threeMonths}" selected="${period == 'THREEMONTH'}" period="THREEMONTH" >${threeMonths}</tags:widgetLink>
        			|
        			<tags:widgetLink method="render" title="${prevYearsData}" labelBusy="${oneYear}" selected="${period == 'YEAR'}" period="YEAR" >${oneYear}</tags:widgetLink>
        			|
        			<tags:widgetLink method="render" title="${customDateRange}" labelBusy="${custom}" selected="${period == 'NOPERIOD'}" period="NOPERIOD" >${custom}</tags:widgetLink>
    			</td>
                
    		</tr>
		
    		<%-- CUSTOM DATES --%>
    		<c:choose>
            
        		<c:when test="${period == 'NOPERIOD'}">
        			<tr id="optionalDateFields" name="optionalDateFields">
        				<td>&nbsp;</td>
        				<td>
							<div class="fl">
								<dt:dateRange startName="startDateParam" startValue="${startDate}" endName="stopDateParam" endValue="${stopDate}" forceIncludes="true" />
							</div>
                            <tags:widgetActionRefreshImage nameKey="reloadUsingCustomDates" method="render" icon="icon-arrow-refresh"/>
        				</td>
        			</tr>
        		</c:when>

        		<c:otherwise>
        		    <span class="dn">
	        		    <dt:date name="startDateParam" value="${startDate}"/>
	        		    <dt:date name="stopDateParam" value="${stopDate}"/>
        		    </span>
        		</c:otherwise>
                
    		</c:choose>
		
		
    		<%-- CHART STYLE --%>
    		<tr>
    			<td class="label"><i:inline key=".chartStyle"/></td>
    		
    			<td class="last">
                    <cti:msg2 var="lineGraph" key=".lineGraph"/>
                    <cti:msg2 var="line" key=".line"/>
                    <cti:msg2 var="barGraph" key=".barGraph"/>
                    <cti:msg2 var="bar" key=".bar"/>
    				<tags:widgetLink method="render" title="${lineGraph}" labelBusy="${line}" selected="${graphType == 'LINE'}" graphType="LINE"><i:inline key=".line"/></tags:widgetLink>
    				|
    				<tags:widgetLink method="render" title="${barGraph}" labelBusy="${bar}" selected="${graphType == 'COLUMN'}" graphType="COLUMN"><i:inline key=".bar"/></tags:widgetLink>
    			</td>
    		</tr>
        
            <%-- TABULAR DATA REPROTS --%>
    		<tr>
                <td class="label">
                    <c:choose>
                        <c:when test="${attributeGraphType.attribute == 'USAGE' || attributeGraphType.attribute == 'USAGE_WATER'}">
                            <i:inline key=".archivedUsageData"/>
                        </c:when>
                        <c:otherwise>
                            <i:inline key=".tabularUsageData"/>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="<cti:url value="/amr/reports/${tabularDataViewer}?def=rawPointHistoryDefinition&pointId=${pointId}&startDate=${startDateMillis}&stopDate=${stopDateMillis}" />"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></a>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="csvView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
                    |
                    <cti:simpleReportLinkFromNameTag definitionName="rawPointHistoryDefinition" viewType="pdfView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
                </td>
            </tr>
            
            <c:if test="${attributeGraphType.attribute == 'USAGE' || attributeGraphType.attribute == 'USAGE_WATER'}">
    	        <tr>
    	            <td class="label"><b><i:inline key=".normalizedUsageData"/></b></td>
    	            
    	            <td>
    	                <a href="<cti:url value="/amr/reports/${tabularDataViewer}?def=normalizedUsageDefinition&pointId=${pointId}&startDate=${startDateMillis}&stopDate=${stopDateMillis}&attribute=${attributeGraphType.attribute}" />"><i:inline key="yukon.web.modules.amr.fileFormatHtml"/></a>
    	                |
    	                <cti:simpleReportLinkFromNameTag definitionName="normalizedUsageDefinition" viewType="csvView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}" attribute="${attributeGraphType.attribute}"><i:inline key="yukon.web.modules.amr.fileFormatCsv"/></cti:simpleReportLinkFromNameTag>
    	                |
    	                <cti:simpleReportLinkFromNameTag definitionName="normalizedUsageDefinition" viewType="pdfView" pointId="${pointId}" startDate="${startDateMillis}" stopDate="${stopDateMillis}" attribute="${attributeGraphType.attribute}"><i:inline key="yukon.web.modules.amr.fileFormatPdf"/></cti:simpleReportLinkFromNameTag>
    	            </td>
    	        </tr>
    	    </c:if>
        
		</table>
		
	</c:when>
		
	<c:otherwise>
		<i:inline key=".noTrendsAvailable"/>
	</c:otherwise>

</c:choose>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="grid" tagdir="/WEB-INF/tags/jqGrid" %>

<%-- INPUTS --%>
<c:if test="${not empty metaInfo}">
    <br/>
    <tags:nameValueContainer style="width:40%;">
        <c:forEach var="metaInfo" items="${metaInfo}">
            <tags:nameValue name="${metaInfo.key}">${fn:escapeXml(metaInfo.value)}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</c:if>
<br>

<%-- CSV URL --%>
<cti:simpleReportUrlFromNameTag var="csvUrlBase" viewType="csvView" definitionName="${definitionName}" htmlOutput="true" />
<cti:url var="csvUrl" value="${csvUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <cti:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</cti:url>

<%-- PDF URL --%>
<cti:simpleReportUrlFromNameTag var="pdfUrlBase" viewType="pdfView" definitionName="${definitionName}" htmlOutput="true" />
<cti:url var="pdfUrl" value="${pdfUrlBase}">
    <c:forEach var="input" items="${inputMap}">
        <cti:param name="${input.key}" value="${input.value}"/>
    </c:forEach>
</cti:url>

<%-- PURE HTML EXPORT OPTIONS --%>
<c:if test="${pureHtml}">
	<br>
	<div style="text-align:right;width:95%;padding-bottom:5px;">
		<b>Export:&nbsp;</b>
        <a href="${csvUrl}" style="text-decoration:none;color:#000;">
        	<img src="/WebConfig/yukon/Icons/excel.gif">
        	CSV
        </a>
        |
        <a href="${pdfUrl}" style="text-decoration:none;color:#000;">
        	<img src="/WebConfig/yukon/Icons/pdf.gif">
        	PDF
        </a>
	</div>
</c:if>

<c:choose>

	<%-- jqGrid REPORT TABLE --%>
	<c:when test="${!pureHtml}">
	
		<c:set var="gridWidth" value=""/>
		<c:if test="${not empty pageScope.width}">
			<c:set var="gridWidth" value="${pageScope.width}" />
		</c:if>
		
		
		<grid:report title="${reportTitle}" 
            height="${height}" 
            width="${gridWidth}" 
            columnInfo="${columnInfo}" 
            dataUrl="${dataUrl}" 
            csvUrl="${csvUrl}" 
            pdfUrl="${pdfUrl}" 
            showLoadMask="${showLoadMask}" 
            refreshRate="${refreshRate}" />
	</c:when>
	
	<%-- PURE HTML REPORT TABLE --%>
	<c:otherwise>
		
		<table class="resultsTable">
		
		    <!-- header -->
		    <tr>
		        <c:forEach var="ci" items="${columnInfo}">
		            <th style="text-align:${ci.align};width:${ci.columnWidthPercentage}%">${ci.label}</th>
		        </c:forEach>
		    </tr>
		    
		    <!-- data -->
		    <c:forEach var="rowData" items="${data}">
		        <tr class="<tags:alternateRow odd="" even="altRow"/>">
		            <c:forEach var="colData" items="${rowData}" varStatus="colCounter">
		                <td>${colData}</td>
		            </c:forEach>
		        </tr>
		    </c:forEach>
		
		</table>

	</c:otherwise>

</c:choose>
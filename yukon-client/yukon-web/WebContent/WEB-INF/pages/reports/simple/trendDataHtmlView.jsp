<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="${module}">
<c:if test="${showMenu}">

    <cti:standardMenu menuSelection="${menuSelection}" />
    
    <cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink url="/spring/csr/search" title="Device Selection" />
		<cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}"><cti:deviceName deviceId="${deviceId}"></cti:deviceName></cti:crumbLink>
		<cti:crumbLink>${reportTitle}</cti:crumbLink>
	</cti:breadCrumbs>

</c:if>


<%-- TITLE --%>
<div class="simpleReportHeader">${reportTitle}</div>

<%-- INPUTS --%>
<c:if test="${not empty metaInfo}">
    <br/>
    <tags:nameValueContainer style="width:40%;">
        <c:forEach var="metaInfo" items="${metaInfo}">
            <tags:nameValue name="${metaInfo.key}">${fn:escapeXml(metaInfo.value)}</tags:nameValue>
        </c:forEach>
    </tags:nameValueContainer>
</c:if>



<%-- EXPORT OPTIONS --%>
<br/>
<tags:nameValueContainer style="width:30%;">
    <tags:nameValue name="Export Data">
        <cti:simpleReportLinkFromModelTag reportModel="${reportModel}" definitionName="${definitionName}" viewType="csvView">CSV</cti:simpleReportLinkFromModelTag>
        |
        <cti:simpleReportLinkFromModelTag reportModel="${reportModel}" definitionName="${definitionName}" viewType="pdfView">PDF</cti:simpleReportLinkFromModelTag>
    </tags:nameValue>
</tags:nameValueContainer>
<br/>


<%-- REPORT TABLE --%>
<table class="resultsTable">

    <!-- COLUMN HEADER -->
    <tr>
        <c:forEach var="ci" items="${columnInfo}">
            <th style="text-align:${ci.columnAlignment};width:${ci.columnWidthPercentage}%">${ci.columnName}</th>
        </c:forEach>
    </tr>
    
    <!-- DATA -->
    <c:forEach var="rowData" items="${data}">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <c:forEach var="colData" items="${rowData}" varStatus="colCounter">
                <td>${colData}</td>
            </c:forEach>
        </tr>
    </c:forEach>

</table>




</cti:standardPage>
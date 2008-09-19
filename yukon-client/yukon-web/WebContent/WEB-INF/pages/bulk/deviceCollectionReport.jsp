<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.bulk.collectionActions.deviceCollectionReport.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- device collection report --%>
        <cti:crumbLink title="${pageTitle}"/>
        
    </cti:breadCrumbs>
    
    <h2>${pageTitle}</h2>
    <br>

    <%-- EXT REPORT GRID (not working in IE yet)
    <c:url var="dataUrl" value="/spring/bulk/deviceCollectionReportXmlData">
        <c:forEach var="p" items="${deviceCollection.collectionParameters}">
            <c:param name="${p.key}" value="${p.value}"/>
        </c:forEach>
    </c:url>
    <tags:extGrid columnInfo="${columnInfo}" dataUrl="${dataUrl}" />
    --%> 
    
    <table class="resultsTable">
        
        <tr>
            <c:forEach var="columnHeader" items="${columnInfo}">
                <th>${columnHeader.columnName}</th>
            </c:forEach>
        </tr>
        
        <c:forEach var="meter" items="${meterList}">
            <tr>
                <td>${meter.name}</td>
                <td>${meter.meterNumber}</td>
                <td>${meter.typeStr}</td>
                <td>${meter.address}</td>
                <td>${meter.route}</td>
            </tr>
        </c:forEach>
    
    </table>
    
</cti:standardPage>
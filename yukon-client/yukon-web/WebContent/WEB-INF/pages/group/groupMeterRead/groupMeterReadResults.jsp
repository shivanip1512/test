<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.groupMeterRead.results.pageTitle"/>
<cti:msg var="groupMeterReadHomePageTitle" key="yukon.common.device.groupMeterRead.home.pageTitle"/>
<cti:msg var="recentResultsTitle" key="yukon.common.device.groupMeterRead.resultDetail.recentResultsTitle" />
<cti:msg var="attributesHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.attributes"/>
<cti:msg var="devicesHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.devices"/>
<cti:msg var="successCountHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.successCount"/>
<cti:msg var="failureCountHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.failureCount"/>
<cti:msg var="unsupportedCountHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.unsupportedCount"/>
<cti:msg var="detailHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.detail"/>
<cti:msg var="statusHeader" key="yukon.common.device.groupMeterRead.results.tableHeader.status"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="devicegroups|groupMeterRead"/>
    
       	<cti:breadCrumbs>
    	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
   	        <cti:crumbLink url="/spring/group/commander/groupProcessing" title="${groupMeterReadHomePageTitle}" />
            <cti:crumbLink title="${pageTitle}"/>
    	</cti:breadCrumbs>

        <h2>${pageTitle}</h2>
        <br>
        
        <tags:boxContainer title="${recentResultsTitle}" id="groupMeterReadResultsContainer" hideEnabled="false">
        
        <table class="compactResultsTable">
            <tr>
                <th>${attributesHeader}</th>
                <th>${devicesHeader}</th>
                <th style="text-align:right;">${successCountHeader}</th>
                <th style="text-align:right;">${failureCountHeader}</th>
                <th style="text-align:right;">${unsupportedCountHeader}</th>
                <th></th>
                <th>${detailHeader}</th>
                <th>${statusHeader}</th>
            </tr>
            
            <c:forEach var="resultWrapper" items="${resultWrappers}">
            
            	<c:set var="resultKey" value="${resultWrapper.key.result.key}"/>
            
                <cti:url var="resultDetailUrl" value="/spring/group/groupMeterRead/resultDetail">
                    <cti:param name="resultKey" value="${resultKey}" />
                </cti:url>
            
                <tr>
                    <td>${resultWrapper.value}</td>
                    <td><cti:msg key="${resultWrapper.key.result.deviceCollection.description}"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/SUCCESS_COUNT"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/FAILURE_COUNT"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/UNSUPPORTED_COUNT"/></td>
                    <td><div style="width:20px;"></div></td>
                    <td><a href="${resultDetailUrl}">View</a></td>
                    <td>
                    	<div id="statusDiv_${resultKey}">
                    		<cti:classUpdater type="GROUP_METER_READ" identifier="${resultKey}/STATUS_CLASS">
	                        	<cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/STATUS_TEXT"/>
	                        </cti:classUpdater>
                        </div>
                    </td>
                </tr>
            </c:forEach>
        </table>
        
    </tags:boxContainer>
    
</cti:standardPage>
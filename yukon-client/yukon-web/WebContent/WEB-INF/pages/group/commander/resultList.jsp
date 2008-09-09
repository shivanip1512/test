<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Commander Results" module="amr">

    <cti:standardMenu menuSelection="devicegroups|commander"/>
    
       	<cti:breadCrumbs>
    	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
            
   	        <cti:crumbLink url="/spring/group/commander/groupProcessing" title="Group Processing" />
            
            &gt; Command Executing
    	</cti:breadCrumbs>

        <%-- TITLE --%>
        <h2>Command Processing Results</h2>
        <br>
        
        <tags:boxContainer title="Command Processing Results" id="commandProcessingContainer" hideEnabled="false">
        
        <table class="compactResultsTable">
            <tr>
                <th>Command</th>
                <th>Devices</th>
                <th style="text-align:right;">Success Count</th>
                <th style="text-align:right;">Failure Count</th>
                <th></th>
                <th>Detail</th>
                <th>Status</th>
            </tr>
            
            <c:forEach items="${resultList}" var="result">
            
                <c:url var="resultDetailUrl" value="/spring/group/commander/resultDetail">
                    <c:param name="resultKey" value="${result.key}" />
                </c:url>
            
                <tr>
                    <td>${result.command}</td>
                    <td><cti:msg key="${result.deviceCollection.description}"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></td>
                    <td style="text-align:right;"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></td>
                    <td><div style="width:20px;"></div></td>
                    <td><a href="${resultDetailUrl}">View</a></td>
                    <td>
                        <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/IS_COMPLETE_TEXT"/>
                        <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/IS_CANCELED_TEXT"/>
                    </td>
                </tr>
            </c:forEach>
            
        </table>
        
    </tags:boxContainer>
    
</cti:standardPage>
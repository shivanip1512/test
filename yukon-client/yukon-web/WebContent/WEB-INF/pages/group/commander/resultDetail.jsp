<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage title="Commander Results" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
   	    <cti:crumbLink url="/spring/group/commander/groupProcessing" title="Group Processing" />
	    &gt; Command Executing
	</cti:breadCrumbs>
  
  <a href="resultList">View All Results</a>
  <br>
  
	Your group request has been started:<br><br>
	Executing <b>${result.command}</b> on <b><cti:msg key="${result.deviceCollection.description}"/></b>.


<hr>         

<script type="text/javascript">
    function submitForm(id) {
        $(id).submit();
    }
    
    function hideShowErrorList(id) {
        $(id).toggle();
    }
</script>

<%-- SUCCESS --%>
<br>
<div class="largeBoldLabel">Succeeded: <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/> </div>

<div style="padding:10px;">

    <c:choose>
    <c:when test="${result.successCollection.deviceCount > 0}">
    
        <form id="successForm" method="get" action="/spring/bulk/collectionActions">
            <cti:deviceCollection deviceCollection="${result.successCollection}" />
        </form>
             
        <a href="javascript:submitForm('successForm');" class="small">Do stuff</a> <ct:selectedDevicesPopup deviceCollection="${result.successCollection}" />
        
   <ct:hideReveal title="Show Results">     
<table class="compactResultsTable">
<tr><th>Device</th><th>Result</th><th>First Point Value</th></tr>
<c:forEach items="${result.resultHolder.successfulDevices}" var="device">
<tr>
<td><cti:deviceName device="${device}"/></td>
<td>${result.resultHolder.resultStrings[device]}</td>
<td>${result.resultHolder.values[device][0]}</td>
</tr>
</c:forEach>
</table>
      </ct:hideReveal>  
        
    </c:when>
    <c:otherwise>
        <div class="smallBoldLabel">No devices successfully processed.</div>
    </c:otherwise>
    </c:choose>
    
</div> 


<%-- PROCESSING EXCEPTION --%>
<br>
<div class="largeBoldLabel">Failed: <cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></div>

<div style="padding:10px;">
    
    <form id="processingExceptionForm" method="get" action="/spring/bulk/collectionActions">
        <cti:deviceCollection deviceCollection="${result.failureCollection}" />
    </form>
         
    <a href="javascript:submitForm('processingExceptionForm');" class="small">Do stuff</a> <ct:selectedDevicesPopup deviceCollection="${result.failureCollection}" />
    
    <ct:hideReveal title="Show Errors">
<table class="compactResultsTable">
<tr><th>Device</th><th>Result</th></tr>
<c:forEach items="${result.resultHolder.failedDevices}" var="device">
<tr>
<td><cti:deviceName device="${device}"/></td>
<c:set value="${result.resultHolder.errors[device]}" var="error"/>
  <td>${error.description} (${error.errorCode})</td>
</tr>
</c:forEach>
</table>
</ct:hideReveal>
    
</div>  
    


	
</cti:standardPage>
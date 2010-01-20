<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.deviceReconfig.batchDetail.pageTitle"/>
<cti:url var="deleteImg" value="/WebConfig/yukon/Icons/delete.gif"/>

<cti:standardPage title="${pageTitle}" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Device Reconfiguration" />
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    
    <script type="text/javascript">
    </script>
    
   <tags:sectionContainer title="Batches">
    
    	<table class="resultsTable">
    	
    		<tr>
    			<th>Batch Number</th>
    			<th>Device Count</th>
    			<th>Start Time</th>
    			<th>Status</th>
    			<th>Cancel</th>
    		</tr>
    		
    		<tr>
    			<td>1</td>
    			<td>9</td>
    			<td>01/15/2010 13:10</td>
    			<td><span class="okGreen">Complete</span></td>
    			<td>
    				N/A
    			</td>
    		</tr>
    		
    		<tr>
    			<td>2</td>
    			<td>9</td>
    			<td>01/15/2010 13:40</td>
    			<td><span class="okGreen">Complete</span></td>
    			<td>
    				N/A
    			</td>
    		</tr>
    		
    		<tr>
    			<td>3</td>
    			<td>9</td>
    			<td>01/15/2010 14:10</td>
    			<td><span class="okGreen">Complete</span></td>
    			<td>
    				N/A
    			</td>
    		</tr>
    		
    		<tr>
    			<td>5</td>
    			<td>9</td>
    			<td>01/15/2010 14:40</td>
    			<td><span class="okGreen">Complete</span></td>
    			<td>
    				N/A
    			</td>
    		</tr>
    		
    		<tr>
    			<td>6</td>
    			<td>9</td>
    			<td>01/15/2010 15:10</td>
    			<td><span class="okGreen">Complete</span></td>
    			<td>
    				N/A
    			</td>
    		</tr>
    		
    		<tr>
    			<td>7</td>
    			<td>9</td>
    			<td>01/15/2010 15:10</td>
    			<td>In Progress</td>
    			<td>
    				<img src="${deleteImg}">
    			</td>
    		</tr>
    		
    		<c:forEach var="batchNumber" begin="8" end="22" step="1">
    		
    			<tr>
	    			<td>${batchNumber}</td>
	    			<td>9</td>
	    			<td>N/A</td>
	    			<td><span class="subtleGray">Waiting</span></td>
	    			<td>
	    				<img src="${deleteImg}">
	    			</td>
	    		</tr>
    		
    		</c:forEach>
    		
    	
    	</table>
    
    </tags:sectionContainer>
    
</cti:standardPage>
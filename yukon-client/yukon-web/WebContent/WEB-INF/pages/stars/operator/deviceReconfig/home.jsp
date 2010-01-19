<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.dr.deviceReconfig.home.pageTitle"/>
<cti:msg var="createNewTask" key="yukon.web.modules.dr.deviceReconfig.home.createNewTask"/>
<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>

<cti:standardPage title="${pageTitle}" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink>${pageTitle}</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>${pageTitle}</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    
    <script type="text/javascript">
    </script>
    
    <tags:sectionContainer title="Recent Tasks">
    
    	<table class="resultsTable">
    	
    		<tr>
    			<th>Name</th>
    			<th>Start Time</th>
    			<th>Status</th>
    			<th>Total Device Count</th>
    			<th>Batch Count</th>
    			<th>Batch Detail</th>
    		</tr>
    		
    		<tr>
    			<td>Golay Group 1</td>
    			<td>01/15/2010 13:10</td>
    			<td>In Progress</td>
    			<td>195</td>
    			<td>22</td>
    			<td>
    				<a href="/spring/stars/operator/deviceReconfig/batchDetail?batchId=XXXYZ">View</a>
    			</td>
    		</tr>
    		
    		<tr>
    			<td>Bethany LCR-4500s</td>
    			<td>01/15/2010 17:45</td>
    			<td>In Progress</td>
    			<td>89</td>
    			<td>10</td>
    			<td>
    				<a href="/spring/stars/operator/deviceReconfig/batchDetail?batchId=XXXYZ">View</a>
    			</td>
    		</tr>
    		
    		<tr>
    			<td>Residential LCR Cooling 75% (213000 - 215500)</td>
    			<td>01/15/2010 09:00</td>
    			<td>
    				<span class="okGreen">Complete</span>
    			</td>
    			<td>650</td>
    			<td>50</td>
    			<td>
    				<a href="/spring/stars/operator/deviceReconfig/batchDetail?batchId=XXXYZ">View</a>
    			</td>
    		</tr>
    	
    		<%-- ADD RULE --%>
			<tr style="background-color:#EEE;">
				<td colspan="6">
					<form id="createNewTaskForm" action="/spring/stars/operator/deviceReconfig/setupSelection" method="get">
						<input type="image" src="${add}" onmouseover="javascript:this.src='${addOver}'" onmouseout="javascript:this.src='${add}'">
						${createNewTask}
					</form>
				</td>
			</tr>
    	
    	</table>
    
    </tags:sectionContainer>
    
</cti:standardPage>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="createNewTask" key="yukon.web.modules.dr.deviceReconfig.home.createNewTask"/>
<cti:url var="add" value="/WebConfig/yukon/Icons/add.gif"/>
<cti:url var="addOver" value="/WebConfig/yukon/Icons/add_over.gif"/>

<cti:standardPage title="Inventory Operations" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink>Inventory Operations</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>Inventory Operations</h2>
    <br>
    
    <cti:includeScript link="/JavaScript/deviceReconfig.js"/>
    
    <script type="text/javascript">
    </script>
    
    
    
    <table class="widgetColumns">
    
    	<tr>
    		<td class="widgetColumnCell" valign="top">
    			<tags:boxContainer title="Choose Device Selection Type"  hideEnabled="false">
				    <table cellspacing="10">
				    
					    <%-- DEVICES --%>
					    <tr>
					        <td>
					            <form action="/spring/stars/operator/deviceReconfig/setupSelection" method="get">
					            	<input type="submit" value="Select Inventory"  style="width:140px;">
					            </form>
					        </td>
					        
					        <td>
					            Set various inventory parameters to select devices.
					        </td>
					    </tr>
					    
					    <%-- FILE UPLOAD --%>
					    <tr>
					        <td>
					            <input type="button" id="fileButton" value="Select File" style="width:140px;">
					        </td>
					        
					        <td>
					            Upload a CSV formatted file containing serial number, device type, and account number.
					        </td>
					    </tr>
				 	</table>
			 	
			 	</tags:boxContainer>
			 	<br>
    		</td>
    	</tr>
    
		<tr>
			<td class="widgetColumnCell" valign="top">
    
			    <tags:boxContainer title="Device Reconfiguration" hideEnabled="false">
			    
			    	<table class="compactResultsTable">
			    	
			    		<tr>
			    			<th>Name</th>
			    			<th>Run Schedule</th>
			    			<th>Duration</th>
			    			<th>Status</th>
			    			<th>Device Count</th>
			    		</tr>
			    		
			    		<tr>
			    			<td>
			    				<a href="/spring/stars/operator/deviceReconfig/batchDetail?batchId=XXXYZ">
			    					Golay Group 1
			    				</a>
			    			</td>
			    			<td>Weekdays, at 09:55 PM</td>
			    			<td>6 Hours</td>
			    			<td>In Progress - 90%</td>
			    			<td>1650</td>
			    		</tr>
			    		
			    		<tr>
			    			<td>
			    				<a href="/spring/stars/operator/deviceReconfig/batchDetail?batchId=XXXYZ">
			    					Bethany LCR-4500s
			    				</a>
			    			</td>
			    			<td>Daily, at 1:00 AM</td>
			    			<td>4 Hours 30 Minutes</td>
			    			<td>In Progress - 78%</td>
			    			<td>89</td>
			    		</tr>
			    		
			    		<tr>
			    			<td>
			    				<a href="/spring/stars/operator/deviceReconfig/batchDetail?batchId=XXXYZ">
			    					Residential LCR Cooling 75% (213000 - 215500)
			    				</a>
			    			</td>
			    			<td>Daily, at 12:00 AM</td>
			    			<td>8 Hours</td>
			    			<td>
			    				<span class="okGreen">Complete</span>
			    			</td>
			    			<td>650</td>
			    		</tr>
			    	
			    	
			    	</table>
			   	
			   	</tags:boxContainer>
			</td>
			
		</tr>
	</table>
    
</cti:standardPage>
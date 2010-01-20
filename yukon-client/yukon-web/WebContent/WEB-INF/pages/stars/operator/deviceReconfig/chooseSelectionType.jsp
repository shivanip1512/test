<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Selection Type" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Device Reconfiguration" />
	    <cti:crumbLink>Device Selection Type</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>Device Selection Type</h2>
    <br>
    
    <script type="text/javascript">
    </script>
    
    <tags:boxContainer title="Choose Device Selection Type"  hideEnabled="false">
    
	    <table cellspacing="10">
	    
		    <%-- DEVICES --%>
		    <tr>
		        <td>
		            <form action="/spring/stars/operator/deviceReconfig/setupSelection" method="get">
		            	<input type="submit" value="Select Devices"  style="width:140px;">
		            </form>
		        </td>
		        
		        <td>
		            Set various device parameters. Devices that meet <u>all</u> set parameters will be included.
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
    	
    
</cti:standardPage>
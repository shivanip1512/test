<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<cti:standardPage title="Inventory Actions" module="dr">

    <cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/home" title="Inventory Operations" />
	    <cti:crumbLink url="/spring/stars/operator/deviceReconfig/setupSelection" title="Inventory Selection" />
	    <cti:crumbLink>Inventory Actions</cti:crumbLink>
	</cti:breadCrumbs>
	
	<cti:standardMenu menuSelection="blank"/>

    <h2>Inventory Actions</h2>
    <br>
    
    <script type="text/javascript">
    </script>
    
    <tags:boxContainer title="Select An Action To Perform On Selected Inventory">
    
    	<div style="font-size:11px;">
    				

    
    <table cellpadding="2">
    
        
        <tr>
            <td valign="top" colspan="2" class="smallBoldLabel">
				Device Count: 1650 <img onclick="javascript:showSelectedDevices(this, 'selectedDevicesPopup_70', 'selectedDevicesPopup_70InnerDiv', '/spring/bulk/selectedDevicesTableForDeviceCollection?collectionType=idList&idList.ids=2324%2c2330', '/WebConfig/yukon/Icons/magnifier.gif', '/WebConfig/yukon/Icons/indicator_arrows.gif');" title="View names of selected devices." src="/WebConfig/yukon/Icons/magnifier.gif" onmouseover="javascript:this.src='/WebConfig/yukon/Icons/magnifier_zoom_in.gif'" onmouseout="javascript:this.src='/WebConfig/yukon/Icons/magnifier.gif'">
            </td>
        </tr>
        
        
        
        
        
        
        
            <tr>
                <td valign="top" class="smallBoldLabel" >Instruction:</td>
                <td style="font-size:11px;">
                    
        Selecting any of these options will not immediately make any changes.<br>You will be directed to a page with additional options and/or opportunity to confirm your action.
    
                </td>

            </tr>
        
    </table>
    <br>

    	</div>
    
	    <table cellspacing="10">
	    
		
		    <%-- RECONFIG --%>
		    <tr>
		    
		        <td>
		            
		            <form action="/spring/stars/operator/deviceReconfig/setupSchedule" method="post">
		            	<input type="submit" value="Device Reconfiguration"  style="width:180px;">
		            </form>
		            
		        </td>
		        
		        <td>
		            Setup device reconfiguration options and scheduling
		        </td>
		    
		    </tr>
		    
		    <%-- DELETE --%>
		    <tr>
		    
		        <td>
		            
		            <form action="/spring/stars/operator/deviceReconfig/setupSchedule" method="post">
		            	<input type="submit" value="Delete Inventory"  style="width:180px;">
		            </form>
		            
		        </td>
		        
		        <td>
		            Remove selected inventory
		        </td>
		    
		    </tr>
	 
	 	</table>
 	
 	</tags:boxContainer>
    	
    
</cti:standardPage>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Profile Page" module="amr">
	<cti:standardMenu menuSelection="deviceselection" />
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/csr/search" title="Device Selection"  />
	    <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail"  />
	    &gt; Profile
	</cti:breadCrumbs>
	
	<table class="widgetColumns">
        <tr>
            <td>
                <h2 style="display: inline;">
                    Profile
                </h2>
            </td>
            <td align="right">
                &nbsp; <!-- quick search? -->
            </td>
        </tr>
    </table>
    
    <br>
	
	<div style="width: 50%;">
		<ct:widgetContainer deviceId="${deviceId}">
        
            <div class="widgetColumns">
        
    			<ct:widget bean="meterInformationWidget" />
    			<br>
    			<ct:widget bean="profileWidget" />
    			<br>
    			<ct:widget bean="peakReportWidget" />
                
            </div>
                
		</ct:widgetContainer>
	</div>

</cti:standardPage>

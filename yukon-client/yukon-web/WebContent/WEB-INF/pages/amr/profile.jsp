<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Profile Page" module="amr">
	<cti:standardMenu menuSelection="meters" />
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/meter/search" title="Meters"  />
	    <cti:crumbLink url="/spring/meter/home?deviceId=${deviceId}">
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
        </cti:crumbLink>
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
    
    <ct:widgetContainer deviceId="${deviceId}">
    <table class="widgetColumns">
        
        <tr>
            <td class="widgetColumnCell" valign="top">
            
                <c:if test="${lpSupported && (profileCollection || profileCollectionScanning)}">
                    <ct:widget bean="profileWidget" />
                </c:if>
                
                <c:if test="${peakReportSupported}">
                    <ct:widget bean="peakReportWidget" />
                </c:if>
                
            </td>
            
            <td class="widgetColumnCell" valign="top">
            
                <ct:widget bean="meterInformationWidget" />
                
                <c:if test="${lpSupported && profileCollection}">
                    <ct:widget bean="pendingProfilesWidget" />
                </c:if>
            
            </td>
        </tr>
        
    </table>
    </ct:widgetContainer>
	
	

</cti:standardPage>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterDetailProfile">

    <ct:widgetContainer deviceId="${deviceId}">
    <table class="widgetColumns">
        
        <tr>
            <td class="widgetColumnCell" valign="top">
            
                <c:if test="${lpSupported}">
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

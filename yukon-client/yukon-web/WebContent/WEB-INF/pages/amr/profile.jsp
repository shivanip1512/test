<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterDetailProfile">

    <tags:widgetContainer deviceId="${deviceId}">
    <table class="widgetColumns">
        
        <tr>
            <td class="widgetColumnCell" valign="top">
                <c:if test="${lpSupported}">
                    <tags:widget bean="profile" />
                </c:if>
                
                <c:if test="${peakReportSupported}">
                    <tags:widget bean="peakReport" />
                </c:if>
                
            </td>
            
            <td class="widgetColumnCell" valign="top">
            
                <tags:widget bean="meterInformation" />
                
                <c:if test="${lpSupported && profileCollection}">
                    <tags:widget bean="pendingProfiles" />
                </c:if>
            
            </td>
        </tr>
        
    </table>
    </tags:widgetContainer>
	
</cti:standardPage>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="High Bill Complaint Device Selection" module="amr">
    <cti:standardMenu menuSelection="deviceselection" />
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        <cti:crumbLink url="/spring/csr/search" title="Device Selection" />
        <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail" />
    &gt; High Bill Complaint
</cti:breadCrumbs>

<script type="text/javascript">
	function createLPPoint(url){
		window.location = url;
	}
</script>

    <table class="widgetColumns">
        <tr>
            <td>
                <h2 style="display: inline;">
                    <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
                </h2>
            </td>
            <td align="right">
                &nbsp; <!-- quick search? -->
            </td>
        </tr>
    </table>
    
    <br />

    <ct:widgetContainer deviceId="${deviceId}" identify="false">
    
        <div class="widgetColumns">
            <div class="left" style="width:95%">
                <c:choose>
                
                    <c:when test="${lmPointExists}">
                        <div style="width: 400px">
                            <ct:widget bean="meterInformationWidget" identify="false" deviceId="${deviceId}" hideEnabled="false" />
                        </div>
                        <div style="width: 700px">
                            <ct:widget bean="profilePeakWidget" identify="false" deviceId="${deviceId}" collectLPVisible="true" highlight="usage,averageUsage" loadProfileRequestOrigin="HBC"  hideEnabled="false"/>
                        </div>
                    </c:when>
                    
                    <c:otherwise>
                        <c:url var="highBillUrl" value="/spring/csr/highBill">
                        <c:param name="deviceId" value="${deviceId}" />
                        <c:param name="createLPPoint" value="true" />
                        </c:url>
                        <cti:deviceName deviceId="${deviceId}"></cti:deviceName> is not configured for 
                        High Bill Processing. <input type="button" value="Configure Now" onclick="javascript:createLPPoint('${highBillUrl}')"></input>
                    </c:otherwise>
                    
                </c:choose>
            </div>
        </div>
    
    </ct:widgetContainer>
    <div style="clear: both"></div>

    <ct:dataUpdateEnabler />
    
</cti:standardPage>
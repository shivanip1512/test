<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">

    function toggleConnectButons() {
    
        return function(data) {
        
            var rawValue = data['rawValue'];
            
            // connected states
            if (rawValue == 0 || rawValue == 2) {
                $('connectSpan').show();
                $('disconnectSpan').hide();
            }
            else {
                $('connectSpan').hide();
                $('disconnectSpan').show();
            }
           
        };
    }

</script>

<ct:nameValueContainer altRowOn="true">
  <ct:nameValue name="${attribute.description}">
  <c:if test="${isConfigured}">
    <ct:attributeValue device="${device}" attribute="${attribute}" />
  </c:if>
  <c:if test="${not isConfigured}">
    Disconnect Status Point is not configured.
  </c:if>
  </ct:nameValue>
</ct:nameValueContainer>
<BR>

<div style="text-align: right">
	<ct:widgetActionRefresh hide="${!readable}" method="read" label="Read Status" labelBusy="Reading"/>
    
    <%-- INIT VISIBILITY OF BUTTONS --%>
    <c:set var="connectStyle" value="" />
    <c:if test="${state == 'CONNECTED'}">
        <c:set var="connectStyle" value="display:none;" />
    </c:if>
    
    <c:set var="disconnectStyle" value="" />
    <c:if test="${state == 'DISCONNECTED'}">
        <c:set var="disconnectStyle" value="display:none;" />
    </c:if>
    
    <%-- CONNECT/DISCONNECT BUTTONS --%>
    <span id="connectSpan" style="${connectStyle}">
		<ct:widgetActionRefresh hide="${!controllable}" method="connect" label="Connect" labelBusy="Connecting"/>
    </span>
    
    <span id="disconnectSpan" style="${disconnectStyle}">
		<ct:widgetActionRefresh hide="${!controllable}" method="disconnect" label="Disconnect" labelBusy="Disconnecting"/>
    </span>
    
</div>
<BR>
<c:if test="${configString != ''}">
<div style="max-height: 240px; overflow: auto">
    <ct:hideReveal title="Disconnect Configuration Settings" showInitially="false">
		${configString}
    </ct:hideReveal><br>
</div>
</c:if>
<br>
<c:if test="${isRead}">
	<c:import url="/WEB-INF/pages/widget/common/meterReadingsResult.jsp"/>
</c:if>

<%-- UPDATER WILL TOGGLE WHICH BUTTON IS DISPLAYED IF REMOTELY CONTROLED --%>
<cti:dataUpdaterCallback function="toggleConnectButons()" initialize="true" rawValue="POINT/${pointId}/RAWVALUE" />

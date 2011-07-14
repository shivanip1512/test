<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script type="text/javascript">
    function toggleConnectButons() {
      //assumes data is of type Hash
        return function(data) {
            var rawValue = data.get('rawValue');
            
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

<div align="right" id="popupDiv">
	<ct:widgetActionPopup2 method="helpInfo" container="helpInfo" labelBusy=".title" label=".title" deviceId="${device.deviceId}">
        <i:inline key=".infoLink"/>
	</ct:widgetActionPopup2>
</div>

<ct:simpleDialog id="disconnectInfo"/>

<ct:nameValueContainer altRowOn="true">
  <ct:nameValue name="${attribute.description}">
  <c:if test="${isConfigured}">
    <ct:attributeValue device="${device}" attribute="${attribute}" />
  </c:if>
  <c:if test="${not isConfigured}">
    <cti:msg2 key=".notConfigured" />
  </c:if>
  </ct:nameValue>
</ct:nameValueContainer>
<br>
<div style="text-align: right">
	<ct:widgetActionRefresh2 hide="${!readable}" method="read" label=".readStatus" labelBusy=".reading"/>
    
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
		<ct:widgetActionRefresh2 hide="${!controllable}" method="connect" label=".connect" labelBusy=".connecting" confirmText=".confirmConnect"/>
    </span>
    
    <span id="disconnectSpan" style="${disconnectStyle}">
		<ct:widgetActionRefresh2 hide="${!controllable}" method="disconnect" label=".disconnect" labelBusy=".disconnecting" confirmText=".confirmDisconnect"/>
    </span>
</div>
<br>
<c:if test="${configString != ''}">
<div style="max-height: 240px; overflow: auto">
    <cti:msg2 var="disconnectConfigSettings" key=".disconnectConfigSettings"/>
    <ct:hideReveal title="${disconnectConfigSettings}" showInitially="false">
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

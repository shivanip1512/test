<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<script type="text/javascript">
    function toggleConnectButtons() {
      //assumes data is of type Hash
        return function(data) {
            var rawValue = data.rawValue;
            // connected states
            if (rawValue == 0 || rawValue == 2) {
                $('#connectSpan').show();
                $('#disconnectSpan').hide();
            }
            else {
                $('#connectSpan').hide();
                $('#disconnectSpan').show();
            }
        };
    }

    $(function() {
        $('.f-showDisconnectInfo').click(function(event) {
            var params = {'deviceId': ${device.deviceId},
                          'shortName': '${shortName}'};
            $('#disconnectInfo').load('/widget/disconnectMeterWidget/helpInfo', params, function() {
                $('#disconnectInfo').dialog({width: 600});
            });
        });
    });
</script>
<tags:nameValueContainer2>
    <tags:nameValue2 label="${attribute}">
        <c:if test="${isConfigured}">
            <tags:attributeValue device="${device}" attribute="${attribute}" />
        </c:if>
        <c:if test="${not isConfigured}">
            <cti:msg2 key=".notConfigured" />
        </c:if>
    </tags:nameValue2>
</tags:nameValueContainer2>

<div id="disconnectInfo" class="dn" title="<cti:msg2 key=".infoLink"/>"></div>

<div class="action-area">

    <a href="javascript:void(0);" class="f-showDisconnectInfo fl"><i:inline key=".infoLink"/></a>

    <tags:widgetActionRefresh hide="${!readable}" method="read" nameKey="read" icon="icon-read"/>
    
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
        <tags:widgetActionRefresh hide="${!controllable}" method="connect" nameKey="connect" showConfirm="true"/>
    </span>
    
    <span id="disconnectSpan" style="${disconnectStyle}">
        <tags:widgetActionRefresh hide="${!controllable}" method="disconnect" nameKey="disconnect" showConfirm="true"/>
    </span>
</div>
<c:if test="${configString != ''}">
<div style="max-height: 240px; overflow: auto">
    <cti:msg2 var="disconnectConfigSettings" key=".disconnectConfigSettings"/>
    <tags:hideReveal title="${disconnectConfigSettings}" showInitially="false">
        ${configString}
    </tags:hideReveal><br>
</div>
</c:if>
<c:if test="${isRead}">
    <c:import url="/WEB-INF/pages/widget/common/meterReadingsResult.jsp"/>
</c:if>

<%-- UPDATER WILL TOGGLE WHICH BUTTON IS DISPLAYED IF REMOTELY CONTROLED --%>
<cti:dataUpdaterCallback function="toggleConnectButtons()" initialize="true" rawValue="POINT/${pointId}/RAWVALUE" />

<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<c:choose>
    <c:when test="${useArming}">
        <cti:msg var="connectLabel" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.armLabel"/>
        <cti:msg var="connectLabelBusy" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.armLabelBusy"/>
        <cti:msg var="confirmConnect" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.confirmArm"/>
    </c:when>
    <c:otherwise>
        <cti:msg var="connectLabel" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.connectLabel"/>
        <cti:msg var="connectLabelBusy" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.connectLabelBusy"/>
        <cti:msg var="confirmConnect" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.confirmConnect"/>
    </c:otherwise>
</c:choose>

<cti:msg var="disconnectLabel" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.disconnectLabel"/>
<cti:msg var="disconnectLabelBusy" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.disconnectLabelBusy"/>
<cti:msg var="confirmDisconnect" key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.confirmDisconnect"/>

<c:choose>
    <c:when test="pointNotConfigured">
        <i:inline key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.notConfigured"/>
    </c:when>
    <c:otherwise>

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey="yukon.web.modules.widgets.rfnMeterDisconnectWidget.disconnectStatus"><cti:pointValue pointId="${pointId}"/></tags:nameValue2>
        </tags:nameValueContainer2>
        
        <br>
        
        <c:if test="${not empty responseStatus}">
            <c:choose>
                <c:when test="${responseStatus eq 'SUCCESS'}">
                    <div class="successMessage">
                        <i:inline key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.sendCommand.success" arguments="${command}"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="errorMessage">
                        <i:inline key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.sendCommand.error" arguments="${command}"/>
                        <span>${responseStatus}</span>
                    </div>
                    <c:if test="${not responseStatus eq 'FAILURE'}">
                        <br><br>
                        <i:inline key="yukon.web.modules.widgets.rfnMeterDisconnectWidget.sendCommand.error.lessSevere" arguments="${command}"/>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <br>
        
        <div style="text-align: right">
            <tags:widgetActionRefresh method="connect" label="${connectLabel}" labelBusy="${connectLabelBusy}" confirmText="${confirmConnect}"/>
            <tags:widgetActionRefresh method="disconnect" label="${disconnectLabel}" labelBusy="${disconnectLabelBusy}" confirmText="${confirmDisconnect}"/>
        </div>


    </c:otherwise>
</c:choose>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<cti:includeScript link="/resources/js/widgets/yukon.widget.meter.disconnect.info.js"/>

<input type="hidden" id="deviceIdentifier" value="${device.deviceId}">
<tags:nameValueContainer2 naturalWidth="false">
        <tags:nameValue2 label="${attribute}" valueClass="full-width">
            <cti:pointStatus pointId="${pointId}" classes="vatt"/>
            <cti:pointValue pointId="${pointId}" format="VALUE"/>&nbsp;
            <tags:historicalValue pointId="${pointId}" pao="${device}" classes="wsnw"/>
        </tags:nameValue2>
        <c:if test="${isDisconnectCollarSupported}">
	        <c:choose>
	            <c:when test="${deviceMCT400Series.disconnectAddress != null}">
		            <tags:nameValue2 nameKey=".disconnectAddress">${fn:escapeXml(deviceMCT400Series.disconnectAddress)}</tags:nameValue2>
	            </c:when>
	            <c:otherwise>
	                 <tags:nameValue2 label="${attribute}" valueClass="full-width">
		                <cti:msg2 key=".notConfigured" />
	                 </tags:nameValue2>
	            </c:otherwise>
	        </c:choose>
        </c:if>
</tags:nameValueContainer2>

<c:if test="${configString != null}">
    <tags:hideReveal2 titleKey=".disconnectConfigSettings" showInitially="false">
        <div class="scroll-md monospace">${configString}</div>
    </tags:hideReveal2>
</c:if>
<c:if test="${fn:length(errors) > 0}">
	<div class="scroll-md">
		<c:forEach items="${errors}" var="error">
			<tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
				<c:if test="${not empty error.detail}">
					<i:inline key="${error.detail}" />
				</c:if>
				<div>${error.porter}</div>
				<div>${error.troubleshooting}</div>
			</tags:hideReveal>
		</c:forEach>
		<c:if test="${exceptionReason != null}">
			<span class="error">${exceptionReason}</span>
		</c:if>
	</div>
</c:if>



<cti:url var="editUrl" value="/widget/disconnectMeterWidget/edit">
	<cti:param name="deviceId" value="${device.deviceId}"/>
	<cti:param name="shortName" value="disconnectMeterWidget"/>
</cti:url>
<%-- Edit Popup --%>
<div id="disconnect-meter-popup" 
	data-event="yukon:widget:meter:disconnectInfo:edit"
	data-width="500"
	data-title="<cti:msg2 key=".configure" />"
	data-ok-text="<cti:msg2 key="yukon.common.save"/>"
	data-url="${editUrl}">
</div>
<cti:msgScope paths="yukon.web.widgets.disconnectMeterWidget">
<div class="action-area">
	<c:if test="${success}">
		<c:if test="${command != null}">
		  <span class="success fl"><i:inline key=".${command}.success" /></span>
		</c:if>
		<c:if test="${isRead}">
		  <span class="success fl"><i:inline key=".read.success" /></span>
		</c:if>
		<c:if test="${isQuery}">
		  <span class="success fl"><i:inline key=".query.success" /></span>
		</c:if>
	</c:if>
	<c:if test="${(isDisconnectCollarSupported && deviceMCT400Series.disconnectAddress != null ) || (!isDisconnectCollarSupported && showDisconnect)}">
	<c:if test="${supportsRead}">
	   <tags:widgetActionRefresh method="read" nameKey="read" icon="icon-read" classes="right M0"/>
	</c:if>
	<c:if test="${supportsQuery}">
	   <tags:widgetActionRefresh method="query" nameKey="query" icon="icon-read" classes="right M0"/>
	</c:if>
	<cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
		<tags:widgetActionRefresh method="connect" nameKey="connect" showConfirm="true" classes="middle"/>
		<c:if test="${supportsArm}">
		  <tags:widgetActionRefresh method="arm" nameKey="arm" showConfirm="true" classes="middle"/>
		</c:if>
		<tags:widgetActionRefresh method="disconnect" nameKey="disconnect" showConfirm="true" classes="left"/>
	</cti:checkRolesAndProperties>
	</c:if>
	<cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="UPDATE">
	    <c:if test="${isDisconnectCollarSupported}">
		  <cti:button nameKey="edit" icon="icon-pencil" data-popup="#disconnect-meter-popup"/>
	    </c:if>
	</cti:checkRolesAndProperties>
</div>
</cti:msgScope>
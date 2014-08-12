<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<tags:nameValueContainer2>
    <tags:nameValue2 label="${attribute}">
        <c:choose>
            <c:when test="${isConfigured}">
                <tags:attributeValue pao="${device}" attribute="${attribute}"
                    showHistoricalReadings="${device.getPaoType().isMct()}" />
            </c:when>
            <c:otherwise>
                <cti:msg2 key=".notConfigured" />
            </c:otherwise>
        </c:choose>
    </tags:nameValue2>
</tags:nameValueContainer2>

<c:if test="${pointId != null}">        
    <div id="disconnectInfo" class="dn" title="<cti:msg2 key=".infoLink"/>"></div>
</c:if>   

<div class="action-area">
    <a href="javascript:void(0);" class="js-show-disconnect-info fl"
       data-device-id="${device.deviceId}" data-name="${shortName}"><i:inline key=".infoLink"/></a>
    <tags:widgetActionRefresh method="read" nameKey="read" icon="icon-read"/>
    
    <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
        <tags:widgetActionRefresh method="connect" nameKey="connect" showConfirm="true" classes="connect-btn"/>
        <tags:widgetActionRefresh method="disconnect" nameKey="disconnect" showConfirm="true" classes="disconnect-btn"/>
        <c:if test="${supportsArm}">
            <tags:widgetActionRefresh method="arm" nameKey="arm" showConfirm="true" classes="connect-btn"/>
        </c:if>
    </cti:checkRolesAndProperties>
</div>

<c:if test="${configString != null}">
    <div class="scroll-md">
        <cti:msg2 var="disconnectConfigSettings" key=".disconnectConfigSettings" />
        <tags:hideReveal title="${disconnectConfigSettings}" showInitially="false">
            <pre>${configString}</pre>
        </tags:hideReveal>
    </div>
</c:if>

<c:if test="${errors != null || exceptionReason != null}">
    <div class="scroll-md">
        <c:forEach items="${errors}" var="error">
            <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                <div>${error.porter}</div>
                <div>${error.troubleshooting}</div>
            </tags:hideReveal>
        </c:forEach>
        <c:if test="${exceptionReason != null}">
            <span class="error">${exceptionReason}</span>
        </c:if>
    </div>
</c:if>
<c:if test="${success}">
    <c:choose>
        <c:when test="${command != null}">
            <span class="success"><i:inline key=".${command}.success" /></span>
        </c:when>
        <c:otherwise>
            <span class="success"><i:inline key=".read.success" /></span>
        </c:otherwise>
    </c:choose>
</c:if>

<cti:includeScript link="/JavaScript/yukon.tools.disconnect.js" />
<%-- UPDATER WILL TOGGLE WHICH BUTTON IS DISPLAYED IF REMOTELY CONTROLED --%>
<c:if test="${pointId != null}">
    <cti:dataUpdaterCallback function="yukon.tools.disconnect.toggleButtons" initialize="true" rawValue="POINT/${pointId}/RAWVALUE" />
</c:if>

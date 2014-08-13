<%@ page trimDirectiveWhitespaces="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/yukon.widget.disconnect.js" />

<tags:nameValueContainer2 naturalWidth="false">
    <tags:nameValue2 label="${attribute}" valueClass="full-width">
        <c:choose>
            <c:when test="${isConfigured}">
                <cti:pointStatus pointId="${pointId}" classes="vatt"/>
                <cti:pointValue pointId="${pointId}" format="VALUE"/>&nbsp;
                <tags:historicalValue pointId="${pointId}" pao="${device}" classes="wsnw"/>
            </c:when>
            <c:otherwise>
                <cti:msg2 key=".notConfigured" />
            </c:otherwise>
        </c:choose>
    </tags:nameValue2>
</tags:nameValueContainer2>

<c:if test="${configString != null}">
    <tags:hideReveal2 titleKey=".disconnectConfigSettings" showInitially="false">
        <div class="scroll-md monospace">${configString}</div>
    </tags:hideReveal2>
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

<div class="action-area">
    <c:if test="${success}">
        <c:choose>
            <c:when test="${command != null}">
                <span class="success fl"><i:inline key=".${command}.success" /></span>
            </c:when>
            <c:otherwise>
                <span class="success fl"><i:inline key=".read.success" /></span>
            </c:otherwise>
        </c:choose>
    </c:if>
    
    <tags:widgetActionRefresh method="read" nameKey="read" icon="icon-read" classes="right M0"/>
    <cti:checkRolesAndProperties value="ALLOW_DISCONNECT_CONTROL">
        <tags:widgetActionRefresh method="connect" nameKey="connect" showConfirm="true" classes="connect-btn middle"/>
        <c:if test="${supportsArm}">
            <tags:widgetActionRefresh method="arm" nameKey="arm" showConfirm="true" classes="connect-btn middle"/>
        </c:if>
        <tags:widgetActionRefresh method="disconnect" nameKey="disconnect" showConfirm="true" classes="disconnect-btn left"/>
    </cti:checkRolesAndProperties>
</div>

<%-- UPDATER WILL TOGGLE WHICH BUTTON IS DISPLAYED IF REMOTELY CONTROLED --%>
<c:if test="${pointId != null}">
    <cti:dataUpdaterCallback function="yukon.widget.disconnect.toggleButtons" initialize="true" rawValue="POINT/${pointId}/RAWVALUE" />
</c:if>

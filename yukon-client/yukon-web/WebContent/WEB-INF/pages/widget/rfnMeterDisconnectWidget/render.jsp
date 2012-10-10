<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:choose>
    <c:when test="pointNotConfigured">
        <i:inline key=".notConfigured"/>
    </c:when>
    <c:otherwise>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".disconnectStatus"><cti:pointValue pointId="${pointId}"/></tags:nameValue2>
        </tags:nameValueContainer2>
        <br>
        
        <div id="${widgetParameters.widgetId}_results"></div>
        <br>
        <div style="text-align: right">
            <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="query" nameKey="query" showConfirm="false"/>
            <c:if test="${controllable}">
                <c:if test="${arming || both}">
                    <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="arm" nameKey="arm" showConfirm="true"/>
                </c:if>
                <c:if test="${!arming || both}">
                    <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="connect" nameKey="connect" showConfirm="true"/>
                </c:if>
                <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="disconnect" nameKey="disconnect" showConfirm="true"/>
            </c:if>
        </div>
    </c:otherwise>
</c:choose>
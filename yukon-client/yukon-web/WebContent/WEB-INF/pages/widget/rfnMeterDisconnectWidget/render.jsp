<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<c:choose>
    <c:when test="pointNotConfigured"><i:inline key=".notConfigured"/></c:when>
    <c:otherwise>
        <div id="${widgetParameters.widgetId}_results" class="stacked"></div>
        
        <tags:nameValueContainer2 tableClass="stacked">
            <tags:nameValue2 nameKey=".disconnectStatus"><cti:pointValue pointId="${pointId}"/></tags:nameValue2>
        </tags:nameValueContainer2>

        <div class="action-area">
            <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="query" nameKey="query"/>
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
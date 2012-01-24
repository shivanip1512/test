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
            <c:choose>
                <c:when test="${useArming}">
                    <c:set var="connectOrArm" value="arm"/>
                </c:when>
                <c:otherwise>
                    <c:set var="connectOrArm" value="connect"/>
                </c:otherwise>
            </c:choose>
            
            <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="query" nameKey="query" showConfirm="false"/>
            
            <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="connect" nameKey="${connectOrArm}" showConfirm="true"/>

            <tags:widgetActionUpdate container="${widgetParameters.widgetId}_results" method="disconnect" nameKey="disconnect" showConfirm="true"/>

        </div>
    </c:otherwise>
</c:choose>
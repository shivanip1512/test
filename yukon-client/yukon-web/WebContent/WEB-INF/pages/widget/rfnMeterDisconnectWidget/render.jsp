<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<c:choose>
    <c:when test="pointNotConfigured">
        <i:inline key=".notConfigured"/>
    </c:when>
    <c:otherwise>
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".disconnectStatus"><cti:pointValue pointId="${pointId}"/></tags:nameValue2>
        </tags:nameValueContainer2>
        <br>
        <c:if test="${not empty responseStatus}">
            <c:choose>
                <c:when test="${responseStatus eq 'SUCCESS'}">
                    <div class="successMessage">
                        <i:inline key=".sendCommand.success" arguments="${command}"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="errorMessage">
                        <i:inline key=".sendCommand.error" arguments="${command}"/>
                        <span>${responseStatus}</span>
                    </div>
                    <c:if test="${not responseStatus eq 'FAILURE'}">
                        <br><br>
                        <i:inline key=".sendCommand.error.lessSevere" arguments="${command}"/>
                    </c:if>
                </c:otherwise>
            </c:choose>
        </c:if>
        <br>
        <div style="text-align: right">
            <c:choose>
                <c:when test="${useArming}">
                    <tags:widgetActionRefresh method="connect" nameKey="arm" showConfirm="true"/>
                </c:when>
                <c:otherwise>
                    <tags:widgetActionRefresh method="connect" nameKey="connect" showConfirm="true"/>
                </c:otherwise>
            </c:choose>
            <tags:widgetActionRefresh method="disconnect" nameKey="disconnect" showConfirm="true"/>
        </div>
    </c:otherwise>
</c:choose>
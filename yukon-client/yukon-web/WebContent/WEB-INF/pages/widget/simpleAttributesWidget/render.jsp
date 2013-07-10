<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<tags:nameValueContainer>
    <c:forEach var="attributeInfo" items="${attributeInfos}">
    	<cti:msg2 var="attributeName" key="${attributeInfo.attribute}"/>
        <c:choose>
            <c:when test="${not attributeInfo.supported}">
                <tags:nameValue name="${attributeName}">
                    <i:inline key=".unsupported"/>
                </tags:nameValue>
            </c:when>
            
            <c:when test="${not attributeInfo.exists}">
                <tags:nameValue name="${attributeName}">
                    <i:inline key=".notConfigured"/>
                </tags:nameValue>
            </c:when>
            
            <c:when test="${attributeInfo.supported}">
                <tags:nameValue name="${attributeName}">
                    <tags:attributeValue device="${device}" attribute="${attributeInfo.attribute}" />
                </tags:nameValue>
            </c:when>
        </c:choose>
    </c:forEach>
</tags:nameValueContainer>

<c:if test="${isReadable}">
    <div class="widgetInternalSection" id="${widgetParameters.widgetId}_results"></div>
    <div class="actionArea">
        <tags:widgetActionUpdate method="read" nameKey="read" container="${widgetParameters.widgetId}_results" />
    </div>
</c:if>
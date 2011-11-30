<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<tags:nameValueContainer>
                
    <c:forEach var="attributeInfo" items="${attributeInfos}">
    
        <c:choose>
        
            <c:when test="${not attributeInfo.supported}">
                <tags:nameValue name="${attributeInfo.description}">
                    <i:inline key=".unsupported"/>
                </tags:nameValue>
            </c:when>
            
            <c:when test="${not attributeInfo.exists}">
                <tags:nameValue name="${attributeInfo.description}">
                    <i:inline key=".notConfigured"/>
                </tags:nameValue>
            </c:when>
            
            <c:when test="${attributeInfo.supported}">
                <tags:nameValue name="${attributeInfo.description}">
                    <tags:attributeValue device="${device}" attribute="${attributeInfo.attribute}" />
                </tags:nameValue>
            </c:when>
            
        </c:choose>
    
    </c:forEach>
                
    <c:if test="${isReadable}">
        <tr>
            <td colspan="2">
                <div class="widgetInternalSection" id="${widgetParameters.widgetId}_results"></div>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <div class="fr">
                    <tags:widgetActionUpdate method="read" nameKey="read" container="${widgetParameters.widgetId}_results" />
                </div>
            </td>
        </tr>
    </c:if>

</tags:nameValueContainer>


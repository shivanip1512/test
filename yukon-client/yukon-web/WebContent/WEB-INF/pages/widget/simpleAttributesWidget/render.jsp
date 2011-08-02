<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<tags:nameValueContainer>
                
    <c:forEach var="attributeInfo" items="${attributeInfos}">
    
        <c:choose>
        
            <c:when test="${not attributeInfo.supported}">
                <tags:nameValue name="${attributeInfo.description}">
                    Unsupported
                </tags:nameValue>
            </c:when>
            
            <c:when test="${not attributeInfo.exists}">
                <tags:nameValue name="${attributeInfo.description}">
                    Not Configured
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
        <tr><td><div style="height:8px;"></div></td></tr>
        <tr>
            <td>
                <tags:widgetActionUpdate method="read" nameKey="read" container="${widgetParameters.widgetId}_results" />
            </td>
            <td>
                <div id="${widgetParameters.widgetId}_results">
                </div>
            </td>
        </tr>
    </c:if>

</tags:nameValueContainer>


<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="startIndex" required="true" type="java.lang.Integer" %>
<%@ attribute name="endIndex" required="true" type="java.lang.Integer" %>
<%@ attribute name="addressContainerClass" required="false" type="java.lang.String" %>

<div class="button-group ${addressContainerClass}">
    <c:forEach begin="${startIndex}" end="${endIndex}" varStatus="status">
        <c:choose>
            <c:when test="${status.index < 10}">
                <tags:check value="0" label="0${status.index}" classes="${status.first ? 'ML0' : ''}"/>
            </c:when>
            <c:otherwise>
                <tags:check value="0" key="${status.index}" classes="${status.first ? 'ML0' : ''}"/>
            </c:otherwise>
        </c:choose>
    </c:forEach>
</div>

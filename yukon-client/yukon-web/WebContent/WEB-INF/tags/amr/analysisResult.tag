<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="data" required="true" type="com.cannontech.common.bulk.model.DeviceArchiveData"%>
<%@ attribute name="width" required="true" description="The width of the bar in pixels"%>

<cti:uniqueIdentifier var="uniqueId" prefix="resultBar_${data.id.paoId}"/>

<c:set var="colorWidth" value="1"/>

<div id="${uniqueId}" style="width: ${width}px;" class="resultBar">
    
    <c:set var="start" value="${data.timeline[0].start}"/>
    <c:forEach var="pixel" items="${data.timeline}" varStatus="status">
    
        <c:set var="nextPixel" value="${data.timeline[status.index + 1]}"/>
        
        <c:choose>
            
            <c:when test="${empty nextPixel or nextPixel.readType != pixel.readType}">

                <c:set var="intervalClass" value="${pixel.readType}"/>
                
                <cti:formatInterval var="title" start="${start}" end="${pixel.end}" type="DATEHM"/>
                
                <div class="resultBarInterval ${intervalClass}" style="width:${colorWidth}px;" title="${title}"></div>
                
                <c:if test="${not empty nextPixel}">
                    <c:set var="start" value="${nextPixel.start}"/>
                    <c:set var="colorWidth" value="1"/>
                </c:if>
            </c:when>
            
            <c:otherwise>
            
                <c:set var="colorWidth" value="${colorWidth + 1}"/>
                
            </c:otherwise>
        
        </c:choose>
        
    </c:forEach>
    
</div>
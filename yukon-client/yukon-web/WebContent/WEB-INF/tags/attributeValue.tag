<%@ attribute name="device" required="true" type="com.cannontech.common.pao.YukonDevice"%>
<%@ attribute name="attribute" required="true" type="com.cannontech.common.pao.attribute.model.Attribute"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:attributeResolver device="${device}" attribute="${attribute}" var="pointId"/>
<c:choose>
    <c:when test="${pointId == 0}">
        <span class="errorMessage">
            <cti:msg2 key="yukon.common.attributes.pointNotFound" arguments="${attribute.description}"/>
        </span>
    </c:when>
    <c:otherwise>
        <cti:pointValue pointId="${pointId}"/>
    </c:otherwise>
</c:choose>
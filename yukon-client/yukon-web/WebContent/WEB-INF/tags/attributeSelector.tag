<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="id" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="attributes" required="true" type="java.lang.Object" %>
<%@ attribute name="selectedAttributes" type="java.util.Set" %>
<%@ attribute name="multipleSize" type="java.lang.Integer" %>
<%@ attribute name="groupItems" type="java.lang.Boolean" %>

<c:choose>
    <c:when test="${not empty pageScope.multipleSize}">
        <cti:msg var="selectOneLabel" key="yukon.common.attribute.select.multi"/>
    </c:when>
    <c:otherwise>
        <cti:msg var="selectOneLabel" key="yukon.common.attribute.select"/>
    </c:otherwise>
</c:choose>

<select data-placeholder="${selectOneLabel}" class="js-init-chosen"
    <c:if test="${not empty pageScope.id}">id="${id}"</c:if>
    <c:if test="${not empty pageScope.multipleSize}">multiple size="${pageScope.multipleSize}"</c:if>
    name="${name}">
    
    <c:if test="${empty pageScope.multipleSize}"><%-- Only use dummy option for single select. --%>
        <option value="">${selectOneLabel}</option>
    </c:if>
    
    <c:choose>
        <c:when test="${pageScope.groupItems}">
            <c:forEach var="group" items="${attributes}">
                <optgroup label="<cti:msg2 key="${group.key}"/>">
                    <c:forEach items="${group.value}" var="item">
                        <c:set var="selected" value="${selectedAttributes.contains(item) ? 'selected' : ''}"/>
                        <option value="${item.key}" ${selected}><cti:formatObject value="${item}"/></option>
                    </c:forEach>
                </optgroup>
            </c:forEach>
        </c:when>
        
        <c:otherwise>
            <c:forEach var="attr" items="${attributes}">
                <c:set var="selected" value=""/>
                <c:set var="found" value="false"/>
                <c:forEach var="selectedAttribute" items="${pageScope.selectedAttributes}">
                    <c:if test="${!found && selectedAttribute == attr}">
                        <c:set var="selected" value="selected"/>
                        <c:set var="found" value="true"/>
                    </c:if>
                </c:forEach>
                <option value="${attr}" ${selected}><cti:msg2 key="${attr}"/></option>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</select>
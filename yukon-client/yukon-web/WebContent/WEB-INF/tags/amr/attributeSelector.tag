<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="attributes" required="true" type="java.lang.Object"%>
<%@ attribute name="selectedAttributes" required="false" type="java.util.Set"%>
<%@ attribute name="includeDummyOption" required="false" type="java.lang.Boolean"%>
<%@ attribute name="multipleSize" required="false" type="java.lang.Integer"%>
<%@ attribute name="groupItems" required="false" type="java.lang.Boolean"%>

<c:if test="${empty pageScope.includeDummyOption}">
	<c:set var="includeDummyOption" value="false"/>
</c:if>

<c:if test="${not empty pageScope.multipleSize}">
	<c:set var="includeDummyOption" value="false"/>
</c:if>


<cti:uniqueIdentifier var="uniqueId" prefix="attributeSelector_"/>

<select id="${uniqueId}" name="${fieldName}" <c:if test="${not empty pageScope.multipleSize}">multiple size="${pageScope.multipleSize}"</c:if>>

	<c:if test="${includeDummyOption}">
		<cti:msg var="selectOneLabel" key="yukon.common.device.commander.selector.selectOne"/>
		<option value="">${selectOneLabel}</option>
	</c:if>

    <c:choose>
        <c:when test="${pageScope.groupItems}">
            <c:forEach var="group" items="${attributes}">
                <optgroup label="<cti:msg2 key="${group.key}"/>">
                    <c:forEach items="${group.value}" var="item">
                        <option value="${item.key}">
                            <cti:formatObject value="${item}"/>
                        </option>
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
                <option value="${attr}" ${selected}><cti:msg2 key="${attr.formatKey}"/></option>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</select>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ attribute name="fieldName" required="true" type="java.lang.String"%>
<%@ attribute name="attributes" required="true" type="java.util.Collection"%>
<%@ attribute name="selectedAttributes" required="false" type="java.util.Set"%>
<%@ attribute name="includeDummyOption" required="false" type="java.lang.Boolean"%>
<%@ attribute name="multipleSize" required="false" type="java.lang.Integer"%>

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
	
	<c:forEach var="attr" items="${attributes}">
	
		<c:set var="selected" value=""/>
		<c:set var="found" value="false"/>
		<c:forEach var="selectedAttribute" items="${pageScope.selectedAttributes}">
			<c:if test="${!found && selectedAttribute == attr}">
				<c:set var="selected" value="selected"/>
				<c:set var="found" value="true"/>
			</c:if>
		</c:forEach>
		
		<option value="${attr}" ${selected}>${attr.description}</option>
	</c:forEach>
</select>
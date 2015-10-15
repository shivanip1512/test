<%@ tag dynamic-attributes="attrs" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="name" %>
<%@ attribute name="mode" %>
<%@ attribute name="classes" %>
<%@ attribute name="hideControls" type="java.lang.Boolean" %>

<cti:default var="hideControls" value="${false}"/>


<span data-edit-group="${name}" class="single-field ${classes}" <c:forEach items="${pageScope.attrs}" var="attr">${attr.key}="${attr.value}"</c:forEach>>
    <span class="js-view">
        <tags:setFormEditMode mode="VIEW"/>
        <jsp:doBody />
        <c:if test="${not hideControls}">
            <cti:button icon="icon-pencil" renderMode="image" data-edit-toggle="${name}" classes="fn form-control"/>
        </c:if>
    </span>
    <span class="js-edit dn">
        <tags:setFormEditMode mode="EDIT"/>
        <jsp:doBody />
        <c:if test="${not hideControls}">
            <cti:button icon="icon-cross" renderMode="image" data-edit-toggle="${name}" classes="fn form-control"/>
        </c:if>
    </span>
</span>
<tags:setFormEditMode mode="${mode}"/>

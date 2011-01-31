<%@ tag body-content="empty" %>
<%@ attribute name="id" required="true"%>
<%@ attribute name="title"%>
<%@ attribute name="onClose"%>
<%@ attribute name="styleClass" required="false"%>
<%@ attribute name="on" description="registers click event on the element with this CSS selector"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:includeScript link="/JavaScript/simpleDialog.js"/>

<%-- if the title is going to be specified when popping up the dialog,
     it doesn't matter what it is for now --%>
<c:if test="${empty pageScope.title}">
    <c:set var="title" value="title not specified"/>
</c:if>

<tags:simplePopup id="${id}" title="${pageScope.title}" onClose="${pageScope.onClose}"
    styleClass="${pageScope.styleClass}" on="${pageScope.on}"/>
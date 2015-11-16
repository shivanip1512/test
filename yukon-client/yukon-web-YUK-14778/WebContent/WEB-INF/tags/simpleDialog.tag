<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" required="true" %>
<%@ attribute name="on" description="Registers click event on the element to open this popup." %>
<%@ attribute name="onClose" %>
<%@ attribute name="styleClass" %>
<%@ attribute name="title" %>

<%-- if the title is going to be specified when popping up the dialog,
     it doesn't matter what it is for now --%>
<c:if test="${empty pageScope.title}">
    <c:set var="title" value="title not specified"/>
</c:if>

<tags:simplePopup id="${id}" title="${pageScope.title}" onClose="${pageScope.onClose}" styleClass="${pageScope.styleClass}" on="${pageScope.on}"/>
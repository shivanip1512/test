<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="mode" required="true" type="com.cannontech.web.PageEditMode" %>

<c:set var="com.cannontech.web.taglib.StandardPageTag.pageEditMode" scope="request" value="${mode}"/>

<input type="hidden" id="pageEditMode" value="${mode == 'EDIT'}"/>
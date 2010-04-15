<%@ tag body-content="empty" %>
<%@ attribute name="mode" required="true" type="com.cannontech.web.PageEditMode"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="com.cannontech.web.taglib.StandardPageTag.pageEditMode" scope="request" value="${mode}"/>

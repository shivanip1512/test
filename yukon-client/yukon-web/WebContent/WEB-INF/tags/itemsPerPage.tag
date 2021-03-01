<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="count" required="true" type="java.lang.Integer" description="The selected number of items per page." %>
<%@ attribute name="itemsPerPage" required="true" type="java.lang.Integer" description="The number of items per page value to display on the page."%>

<c:if test="${itemsPerPage == count}">
    <span data-page-size="${itemsPerPage}">${itemsPerPage}</span>
</c:if>
<c:if test="${itemsPerPage != count}">
    <a data-page-size="${itemsPerPage}" href="javascript:void(0);">${itemsPerPage}</a>
</c:if>
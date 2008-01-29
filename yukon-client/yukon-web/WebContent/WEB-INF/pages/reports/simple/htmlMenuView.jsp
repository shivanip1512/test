<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- MODULE, MENU --%>
<cti:standardPage title="${reportTitle}" module="${module}">
<c:if test="${showMenu}">
    <cti:standardMenu menuSelection="${menuSelection}" />
</c:if>

<jsp:include page="htmlBodyView.jsp" />

</cti:standardPage>
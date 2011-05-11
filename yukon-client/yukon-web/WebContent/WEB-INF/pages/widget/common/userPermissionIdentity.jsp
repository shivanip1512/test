<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<c:if test="${empty widgetParameters.permissionDescription}">
    ${permission.description}
</c:if>
<c:if test="${!empty widgetParameters.permissionDescription}">
    ${widgetParameters.permissionDescription}
</c:if>
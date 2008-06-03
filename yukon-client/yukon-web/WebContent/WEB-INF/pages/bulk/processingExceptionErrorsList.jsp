<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<ul>
<c:forEach var="e" items="${exceptionRowNumberMap}">
   <li><div style="font-size:11px;">Line ${e.key + 1} - ${e.value.message}"</div></li>
</c:forEach>
</ul>
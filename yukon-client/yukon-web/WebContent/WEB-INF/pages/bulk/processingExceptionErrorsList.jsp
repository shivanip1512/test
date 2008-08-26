<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%-- line number is key plus 2. (adjust for zero-indexed map key and header row) --%>
<ul>
<c:forEach var="e" items="${exceptionRowNumberMap}">
   <li><div style="font-size:11px;">Line ${e.key + 2} - ${e.value.message}</div></li>
</c:forEach>
</ul>
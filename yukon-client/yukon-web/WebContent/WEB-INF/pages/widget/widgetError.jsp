<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="fwb error">Error processing this widget:</div>
${fn:escapeXml(errorMessage)}

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<d:ajaxPage nameKey="accountInfo" module="amr" page="waterLeakReport.report" okEvent="none" id="accountInfoAjaxDialog">
	<c:choose>
		<c:when test="${!hasVendorId}">
			<i:inline key=".noVendor" />
		</c:when>
		<c:otherwise>
            <%@ include file="../../widget/accountInformationWidget/accountInfoPartial.jspf" %>
		</c:otherwise>
	</c:choose>
</d:ajaxPage>

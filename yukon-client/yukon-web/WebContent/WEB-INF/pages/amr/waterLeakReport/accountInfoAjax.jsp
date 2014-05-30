<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="widgets.accountInformationWidget">
    <c:choose>
        <c:when test="${!hasVendorId}">
            <i:inline key=".noVendor" />
        </c:when>
        <c:otherwise>
            <%@ include file="../../widget/accountInformationWidget/accountInfoPartial.jspf" %>
        </c:otherwise>
    </c:choose>
</cti:msgScope>

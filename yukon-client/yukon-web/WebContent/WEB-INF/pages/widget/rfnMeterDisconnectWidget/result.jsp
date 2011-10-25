<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<c:choose>
    <c:when test="${responseSuccess}">
        <div class="successMessage">
            <i:inline key="yukon.web.widgets.rfnMeterDisconnectWidget.sendCommand.success" arguments="${command}"/>
        </div>
    </c:when>
    <c:otherwise>
        <div class="errorMessage">
            <i:inline key="${responseStatus}"/>
        </div>
    </c:otherwise>
</c:choose>
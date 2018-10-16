<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:if test="${not empty errorMsg}">
    <tags:alertBox>${errorMsg}</tags:alertBox>
</c:if>

<cti:msgScope paths="yukon.web.widgets.disconnectMeterWidget">
<form:form id="meter-disconnect-addr-form" method="post" modelAttribute="addressEditorBean" onsubmit="return false;">
    <form:hidden cssClass="js-meter-info-id" path="deviceId" />
    <input type="hidden" name="shortName" value="disconnectAddressWidget" />
    <input type="hidden" name="identity" value="false">
    <cti:csrfToken/>
    <tags:nameValueContainer2 naturalWidth="false"
        tableClass="with-form-controls">
        <tags:inputNameValue nameKey=".disconnectAddress" path="disconnectAddress" valueClass="full-width" maxlength="7"
            size="7" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE" />
    </tags:nameValueContainer2>
</form:form>
</cti:msgScope>
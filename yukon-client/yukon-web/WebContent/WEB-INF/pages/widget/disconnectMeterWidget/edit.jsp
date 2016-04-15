<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${not empty errorMsg}"><tags:alertBox>${errorMsg}</tags:alertBox></c:if>

<form:form id="meter-disconnect-addr-form" method="post" commandName="deviceMCT400Series" onsubmit="return false;">
	<form:hidden cssClass="js-meter-info-id" path="deviceID"/>
	<input type="hidden" name="shortName" value="disconnectAddressWidget"/>
	 <input type="hidden" name="identity" value="false">
	<cti:csrfToken/>
	<tags:nameValueContainer2 naturalWidth="false" tableClass="with-form-controls">
	    <tags:inputNameValue nameKey=".disconnectAddress" path="disconnectAddress" valueClass="full-width" maxlength="7" size="18" property="ENDPOINT_PERMISSION" minPermissionLevel="UPDATE"/>
	</tags:nameValueContainer2>
<div class="page-action-area">
	<c:set var="isCreate" value="${null== deviceMCT400Series.disconnectAddress || deviceMCT400Series.disconnectAddress == 0}"/>
	<input type="hidden" name="isCreate" value="${isCreate}"/>
	<cti:button nameKey="save" id="edit-btn" classes="primary action" busy="true"/>
	<c:if test="${!isCreate}">
		<!-- Remove Collar Address if exists-->
		<cti:button nameKey="delete" id="delete-btn"  classes="delete" data-ok-event="yukon:widget:meter:disconnect:delete:addr"/>
	</c:if>
	<cti:url value="/meter/home?deviceId=${deviceMCT400Series.deviceID}" var="backUrl"/>
	<cti:button nameKey="cancel" href="${backUrl}"/>
</div>
<d:confirm on="#delete-btn" nameKey="confirmAddrDelete" argument="${deviceMCT400Series.disconnectAddress}"/>
</form:form>
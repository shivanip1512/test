<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="myFormId" required="true" type="java.lang.String" %>
<%@ attribute name="key" required="true" type="java.lang.String" description="Base i18n key. Available settings: .label (required), .labelBusy (optional), .description (optional)"%>
<%@ attribute name="width" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="disableOtherButtons" required="false" type="java.lang.Boolean"%>
<%@ tag body-content="empty" %>

<cti:msgScope paths=".${key},components.slowInput.${key}">
	<cti:msg2 var="label" key=".label"/>
	<cti:msg2 var="labelBusy" key=".labelBusy" blankIfMissing="true"/>
	<cti:msg2 var="description" key=".description" blankIfMissing="true"/>
</cti:msgScope>

<tags:slowInput myFormId="${pageScope.myFormId}" 
				label="${pageScope.label}" 
				labelBusy="${pageScope.labelBusy}" 
				description="${pageScope.desciption}" 
				width="${pageScope.width}" 
				id="${pageScope.id}" 
				disableOtherButtons="${pageScope.disableOtherButtons}"/>

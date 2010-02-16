<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="myFormId" required="true" type="java.lang.String" %>
<%@ attribute name="labelKey" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusyKey" required="false" type="java.lang.String"%>
<%@ attribute name="descriptionKey" required="false" type="java.lang.String"%>
<%@ attribute name="width" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="disableOtherButtons" required="false" type="java.lang.Boolean"%>

<cti:msg2 var="label" key="${pageScope.labelKey}"/>
<cti:msg2 var="labelBusy" key="${pageScope.labelBusyKey}" blankIfMissing="true"/>
<cti:msg2 var="description" key="${pageScope.descriptionKey}" blankIfMissing="true"/>

<tags:slowInput myFormId="${pageScope.myFormId}" 
				label="${pageScope.label}" 
				labelBusy="${pageScope.labelBusy}" 
				description="${pageScope.desciption}" 
				width="${pageScope.width}" id="${pageScope.id}" 
				disableOtherButtons="${pageScope.disableOtherButtons}"/>

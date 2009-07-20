<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:uniqueIdentifier var="uniqueId" prefix="slowSubmit_"/>

<span style="white-space:nowrap;">
<input type="submit" value="${label}" onclick="this.value='${labelBusy}...';$('slowsubmitImg${uniqueId}').show();this.disable();"> 

<img id="slowsubmitImg${uniqueId}" style="display:none;" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting"> 
</span>
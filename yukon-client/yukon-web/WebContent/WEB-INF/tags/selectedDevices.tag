<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="id" %>
<%@ attribute name="deviceCollection" required="true" type="java.lang.Object"%>

<c:set var="count" value="${deviceCollection.deviceCount}"/>

<cti:url var="downloadResultUrl" value="/bulk/downloadResult"/>
<cti:uniqueIdentifier prefix="form_" var="thisId"/>
<form method="post" action="${downloadResultUrl}" id="${thisId}">
<cti:csrfToken/>
<div <c:if test="${not empty pageScope.id}">id="${id}"</c:if>>
    <strong><cti:msg2 key="yukon.common.device.bulk.selectedDevicesPopup.linkLabel"/>:</strong>&nbsp;
    <span class="badge js-count">${count}</span>&nbsp;
    <cti:msg2 key="${deviceCollection.description}"/>
    <c:if test="${count > 0}"><tags:selectedDevicesPopup deviceCollection="${deviceCollection}"/></c:if>
    <c:if test="${deviceErrorCount > 0}">
    	<span class="badge badge-error">${deviceErrorCount}</span>
    	<strong>
    		<cti:msg2 key="yukon.common.device.bulk.selectedDevicesPopup.errorCount"/>
    	</strong>
        <input type="hidden" name=uploadFileName value="${deviceCollection.uploadFileName}" />
            <c:if test="${not empty deviceCollection.header}">
            	<input type="hidden" name=header value="${deviceCollection.header}" />
            </c:if>
            <c:forEach var="deviceError" items="${deviceErrors}">
                <input type="hidden" name="deviceErrors" value="${deviceError}" />
            </c:forEach>
            <input type="hidden" name="collectionType" value="fileUpload" />
            <a href="javascript:$('#${thisId}').submit();" class="wsnw">
            <cti:icon icon="icon-page-excel" classes="cp fn pull-icon-down"/></a>
    </c:if>
</div>
 </form>
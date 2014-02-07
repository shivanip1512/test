<%@ attribute name="key" required="true" %>
<%@ attribute name="noteTextArguments" %>
<%@ attribute name="deviceCollection" type="com.cannontech.common.bulk.collection.device.DeviceCollection" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg2 var="headerTitle" key="${key}.header"/>
<tags:sectionContainer title="${headerTitle}" id="collectionActionsContainer">
    <div class="stacked">
        <%-- SELECTED DEVICES POPUP, NOTE TO USER --%>
        <c:if test="${not empty pageScope.deviceCollection}">
            <div class="fwb vat">
                <tags:selectedDevices deviceCollection="${pageScope.deviceCollection}" id="selectedDevices"/>
            </div>
        </c:if>
        
        <%-- NOTE --%>
        <cti:msg2 var="noteLabel" key="${key}.noteLabel"/>
        <cti:msg2 var="noteText" key="${key}.noteText" arguments="${pageScope.noteTextArguments}"/>
        
        <c:if test="${not empty noteLabel && not empty noteText}">
            <span class="vat">${noteLabel}</span>
            <span class="notes">${noteText}</span>
        </c:if>
    </div>
    
    <jsp:doBody/>
</tags:sectionContainer>
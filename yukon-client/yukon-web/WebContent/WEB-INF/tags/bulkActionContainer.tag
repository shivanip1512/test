<%@ attribute name="key" required="true" type="java.lang.String"%>
<%@ attribute name="noteLabelStyle" required="false" type="java.lang.String"%>
<%@ attribute name="noteTextArguments" required="false" type="java.lang.String"%>
<%@ attribute name="deviceCollection" required="false" type="com.cannontech.common.bulk.collection.device.DeviceCollection"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="headerTitle" key="${key}.header"/>
<tags:sectionContainer title="${headerTitle}" id="collectionActionsContainer">
    <div class="stacked">
        <%-- SELECTED DEVICES POPUP, NOTE TO USER --%>
        <c:if test="${not empty pageScope.deviceCollection}">
            <div class="smallBoldLabel vat">
                <tags:selectedDevices deviceCollection="${pageScope.deviceCollection}" id="selectedDevices"/>
            </div>
        </c:if>
        
        <%-- NOTE --%>
        <cti:msg var="noteLabel" key="${key}.noteLabel"/>
        <cti:msg var="noteText" key="${key}.noteText" arguments="${pageScope.noteTextArguments}" />
        
        <c:if test="${not empty noteLabel && not empty noteText}">
                <span class="smallBoldLabel vat" <c:if test="${not empty pageScope.noteLabelStyle}">style="${pageScope.noteLabelStyle}"</c:if>>${noteLabel}</span>
                <span class="notes">${noteText}</span>
        </c:if>
    </div>
    
<jsp:doBody/>
</tags:sectionContainer>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:forEach items="${validationErrors}" var="error">
    <div class="error stacked">${error}</div>
</c:forEach>

<form action="<cti:url value="/meter/moveOutRequest?deviceId=${meter.deviceId}"/>" id="moveOutForm" method="post">
    <cti:csrfToken/>
    <input name="deviceId" type="hidden" value="${meter.deviceId}">

    <tags:sectionContainer2 nameKey="moveOutForm" >
             
        <tags:nameValueContainer2 tableClass="with-form-controls">
        
            <tags:nameValue2 nameKey=".moveOutDate">
                <dt:date name="moveOutDate" value="${currentDate}"/>
            </tags:nameValue2>

            <c:if test="${!isSmtpConfigured}">
                <tags:nameValue2 nameKey="yukon.common.email.send">
                    <tags:switchButton name="sendEmail" toggleGroup="email-address" offClasses="M0" 
                        offNameKey=".no.label" onNameKey=".yes.label"/>
                    <input name="emailAddress" type="text" disabled="disabled" value="${email}" 
                        data-toggle-group="email-address">
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2>
        <div class="action-area">
            <cti:msg2 var="moveOut" key=".moveOut"/>
            <cti:button label="${moveOut}" busy="true" type="submit" classes="primary action"/>
        </div>

    </tags:sectionContainer2>
</form>
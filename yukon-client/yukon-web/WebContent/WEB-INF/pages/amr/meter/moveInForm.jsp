<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:forEach items="${validationErrors}" var="error">
    <div class="error" style>${error}</div>
</c:forEach>

<form action="<cti:url value="/meter/moveInRequest?deviceId=${meter.deviceId}"/>" id="moveInForm" method="post">
    <cti:csrfToken/>
    <tags:sectionContainer2 nameKey="moveInForm" >

        <tags:nameValueContainer2 tableClass="with-form-controls">
        
            <tags:nameValue2 nameKey=".meterNumber">
                <input name="meterNumber" size="10" type="text" value="${meter.meterNumber}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".deviceName">
                <input name="deviceName" size="30" type="text" value="${meter.name}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".moveInDate">
                <dt:date name="moveInDate" value="${currentDate}" />
            </tags:nameValue2>
            
            <c:if test="${!isSmtpConfigured}">
                <tags:nameValue2 nameKey="yukon.common.email.address">
                    <tags:switchButton name="sendEmail" toggleGroup="email-address" offClasses="M0"/>
                    <input name="emailAddress" type="text" disabled="disabled" value="${email}" data-toggle-group="email-address"/>
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2>

        <div class="action-area">
            <cti:msg2 var="moveIn" key=".moveIn"/>
            <cti:button label="${moveIn}" type="submit" busy="true" classes="primary action"/>
        </div>
    </tags:sectionContainer2>
</form>

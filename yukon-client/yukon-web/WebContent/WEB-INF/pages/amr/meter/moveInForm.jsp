<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<c:forEach items="${validationErrors}" var="error">
    <div class="error" style>${error}</div>
</c:forEach>

<form action="<cti:url value="/meter/moveInRequest?deviceId=${meter.deviceId}"/>" id="moveInForm" method="post">
    <cti:csrfToken/>
    <tags:boxContainer2 nameKey="moveInForm" hideEnabled="false">

        <tags:nameValueContainer2>
        
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
                <tags:nameValue2 nameKey="yukon.common.email.send">
                    <label><input type="checkbox" name="sendEmail" data-toggle="email-address">
                        <i:inline key="yukon.web.modules.amr.moveIn.sendEmailNotification" /></label>
                </tags:nameValue2>
    
                <tags:nameValue2 nameKey="yukon.common.email.address">
                    <input name="emailAddress" type="text" disabled="disabled" value="${email}" data-toggle-group="email-address"/>
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2>

        <div class="page-action-area">
            <cti:msg2 var="moveIn" key=".moveIn"/>
            <cti:button label="${moveIn}" type="submit" busy="true" classes="primary action"/>
        </div>
    </tags:boxContainer2>
</form>

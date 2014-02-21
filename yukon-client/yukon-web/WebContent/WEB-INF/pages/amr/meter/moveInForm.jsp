<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<c:forEach items="${validationErrors}" var="error">
    <span class="internalSectionHeader" style>${error}</span>
    <br />
</c:forEach>

<form action="/meter/moveInRequest?deviceId=${meter.deviceId}" id="moveInForm" method="post">
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
            <tags:nameValue2 nameKey=".emailNotification">
                <input name="emailAddress" type="text" />
            </tags:nameValue2>

        </tags:nameValueContainer2>

        <div class="page-action-area">
            <cti:msg2 var="moveIn" key=".moveIn"/>
            <cti:button label="${moveIn}" type="submit" busy="true" classes="primary action"/>
        </div>
    </tags:boxContainer2>
</form>

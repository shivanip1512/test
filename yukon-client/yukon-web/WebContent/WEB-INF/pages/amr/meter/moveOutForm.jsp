<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:forEach items="${validationErrors}" var="error">
    <div class="error stacked">${error}</div>
</c:forEach>

<form action="/meter/moveOutRequest?deviceId=${meter.deviceId}" id="moveOutForm" method="post">
    <cti:csrfToken/>
    <input name="deviceId" type="hidden" value="${meter.deviceId}">

    <tags:boxContainer2 nameKey="moveOutForm" hideEnabled="false">
    
        <tags:nameValueContainer2>
        
            <tags:nameValue2 nameKey=".moveOutDate">
                <dt:date name="moveOutDate" value="${currentDate}" />
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".emailNotification">
                <input name="emailAddress" type="text" />
            </tags:nameValue2>
        
        </tags:nameValueContainer2>
    
        <cti:msg2 var="moveOut" key=".moveOut"/>
        <cti:button label="${moveOut}" busy="true" type="submit" classes="primary action"/>

    </tags:boxContainer2>
</form>

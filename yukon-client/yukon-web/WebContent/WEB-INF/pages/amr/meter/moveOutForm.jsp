<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
    function toggleEmailNotificaionField() {
        yukon.ui.util.toggleEmailNotificationAddressField(
                'input[name =\'sendEmail\']', 'input[name =\'emailAddress\']');
    }
</script>
<c:forEach items="${validationErrors}" var="error">
    <div class="error stacked">${error}</div>
</c:forEach>

<form action="<cti:url value="/meter/moveOutRequest?deviceId=${meter.deviceId}"/>" id="moveOutForm" method="post">
    <cti:csrfToken/>
    <input name="deviceId" type="hidden" value="${meter.deviceId}">

    <tags:boxContainer2 nameKey="moveOutForm" hideEnabled="false">
             
        <tags:nameValueContainer2>
        
            <tags:nameValue2 nameKey=".moveOutDate">
                <dt:date name="moveOutDate" value="${currentDate}"/>
            </tags:nameValue2>

            <c:if test="${isSMTPConfigured}">
                <tags:nameValue2 nameKey=".sendEmailNotification">
                    <label><input type="checkbox" name="sendEmail" onclick="toggleEmailNotificaionField();">
                        <i:inline key="yukon.web.modules.amr.moveOut.sendemailNotification" /></label>
                </tags:nameValue2>

                <tags:nameValue2 nameKey=".emailNotification">
                    <input name="emailAddress" type="text" disabled="disabled" value="${email}">
                </tags:nameValue2>
            </c:if>
        </tags:nameValueContainer2>

        <cti:msg2 var="moveOut" key=".moveOut"/>
        <cti:button label="${moveOut}" busy="true" type="submit" classes="primary action"/>

    </tags:boxContainer2>
</form>
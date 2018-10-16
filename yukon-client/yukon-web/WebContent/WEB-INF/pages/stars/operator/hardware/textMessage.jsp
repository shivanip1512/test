<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:msgScope paths="modules.operator.hardware">

<script type="text/javascript">
submitForm = function() {
    return submitFormViaAjax('textMsgDialog', 'myform');
}
$( function () {
    // init dateTime fields dynamically brought onto page after initial page load
    yukon.ui.initDateTimePickers();
});
</script>

<cti:flashScopeMessages/>

<!-- Send Text Message Popup -->
<tags:setFormEditMode mode="${mode}"/>

<form:form modelAttribute="textMessage" id="myform" onsubmit="return submitForm();" action="/stars/operator/hardware/zb/sendTextMessage">
    <cti:csrfToken/>
    <form:hidden path="accountId"/>
    <form:hidden path="inventoryId"/>
    <form:hidden path="gatewayId"/>
    <tags:nameValueContainer2>
        <tags:textareaNameValue nameKey=".message" rows="1" cols="21" path="message" />
        <tags:checkboxNameValue nameKey=".confirmationRequired" path="confirmationRequired"/>
        <tags:nameValue2 nameKey=".displayDuration">
            <form:select path="displayDuration">
                <c:forEach var="duration" items="${durations}">
                    <cti:formatDuration type="DHMS_REDUCED" value="${duration.millis}" var="durationLabel"/>
                    <form:option value="${duration}" label="${durationLabel}"/>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".startTime">
            <dt:dateTime id="startTime" path="startTime" value="${textMessage.startTime}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="action-area">
        <cti:button nameKey="send" type="submit" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="$('#textMsgDialog').dialog('close');"/>
    </div>
</form:form>

</cti:msgScope>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:msgScope paths="modules.operator.hardware">

<script type="text/javascript">
submitForm = function() {
    combineDateAndTimeFields('startTime');
    return submitFormViaAjax('ajaxDialog', 'myform', '/stars/operator/hardware/zb/sendTextMessage');
}
</script>

<cti:flashScopeMessages/>

<!-- Send Text Message Popup -->
<tags:setFormEditMode mode="${mode}"/>

<form:form commandName="textMessage" id="myform" onsubmit="return submitForm();">
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
            <tags:dateTimeInput path="startTime" inline="true" fieldValue="${textMessage.startTime}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="actionArea">
        <cti:button nameKey="send" type="submit" styleClass="f_blocker"/>
        <cti:button nameKey="cancel" onclick="$('ajaxDialog').hide()"/>
    </div>
</form:form>

</cti:msgScope>
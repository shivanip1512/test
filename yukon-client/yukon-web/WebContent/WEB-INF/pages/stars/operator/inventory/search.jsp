<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<tags:boxContainer2 nameKey="inventorySearch">

    <form:form commandName="inventorySearch" action="search" method="post">
        <tags:nameValueContainer2 id="inventorySearch" tableClass="nonwrapping">
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.SERIAL_NUMBER">
                <form:input path="serialNumber" size="40" class="f_focus"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.METER_NUMBER">
                <form:input path="meterNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.ACCOUNT_NUMBER">
                <form:input path="accountNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.PHONE_NUMBER">
                <form:input path="phoneNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.LAST_NAME">
                <form:input path="lastName" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.WORK_ORDER_NUMBER">
                <form:input path="workOrderNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".operatorInventorySearchByEnum.ALT_TRACKING_NUMBER">
                <form:input path="altTrackingNumber" size="40"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="actionArea"><cti:button type="submit" nameKey="search"/></div>
    </form:form>
    
</tags:boxContainer2>
<br>
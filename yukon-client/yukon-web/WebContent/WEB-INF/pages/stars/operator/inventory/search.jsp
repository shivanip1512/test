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
            <tags:nameValue2 nameKey=".serialNumber">
                <form:input path="serialNumber" size="40" class="f_focus"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".meterNumber">
                <form:input path="meterNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".accountNumber">
                <form:input path="accountNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phoneNumber">
                <form:input path="phoneNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lastName">
                <form:input path="lastName" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".workOrderNumber">
                <form:input path="workOrderNumber" size="40"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".altTrackingNumber">
                <form:input path="altTrackingNumber" size="40"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="actionArea"><cti:button type="submit" nameKey="search"/></div>
    </form:form>
    
</tags:boxContainer2>
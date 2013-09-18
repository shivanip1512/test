<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:sectionContainer2 nameKey="inventorySearch">

    <form:form commandName="inventorySearch" action="search" method="post">
        <tags:nameValueContainer2 id="inventorySearch" naturalWidth="false">
            <tags:nameValue2 nameKey=".serialNumber">
                <form:input path="serialNumber" class="full_width f-focus"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".meterNumber">
                <form:input path="meterNumber" class="full_width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".accountNumber">
                <form:input path="accountNumber" class="full_width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phoneNumber">
                <form:input path="phoneNumber" class="full_width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lastName">
                <form:input path="lastName" class="full_width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".workOrderNumber">
                <form:input path="workOrderNumber" class="full_width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".altTrackingNumber">
                <form:input path="altTrackingNumber" class="full_width"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="actionArea"><cti:button type="submit" nameKey="search"/></div>
    </form:form>
    
</tags:sectionContainer2>
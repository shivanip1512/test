<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:sectionContainer2 nameKey="inventorySearch">

    <form:form modelAttribute="inventorySearch" action="search" method="get" >
        <tags:nameValueContainer2 id="inventorySearch" >
            <tags:nameValue2 nameKey=".serialNumber">
                <form:input path="serialNumber" class="full-width js-focus"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".meterNumber">
                <form:input path="meterNumber" class="full-width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".accountNumber">
                <form:input path="accountNumber" class="full-width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".phoneNumber">
                <form:input path="phoneNumber" class="full-width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".lastName">
                <form:input path="lastName" class="full-width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".workOrderNumber">
                <form:input path="workOrderNumber" class="full-width"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".altTrackingNumber">
                <form:input path="altTrackingNumber" class="full-width"/>
            </tags:nameValue2>
        </tags:nameValueContainer2>
        
        <div class="action-area"><cti:button type="submit" nameKey="search"/></div>
    </form:form>
    
</tags:sectionContainer2>
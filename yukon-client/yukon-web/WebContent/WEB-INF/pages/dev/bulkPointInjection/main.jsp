<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="date" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dev" page="bulkPointInjection">
<cti:includeScript link="/resources/js/common/yukon.field.helper.js"/>

<script type="text/javascript">
$(document).on('click', '.bulkInject', function(event) {
    $('#populateDbForm').submit();
});
</script>

<form:form modelAttribute="bulkInjection" action="sendBulkData" method="post" id="populateDbForm">
    <cti:csrfToken/>
    <tags:nameValueContainer2>
        <%-- device group --%>
        <tags:nameValue2 nameKey=".deviceGroup">
            <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
            <tags:deviceGroupNameSelector fieldName="groupName"
                fieldValue="${bulkInjection.groupName}" 
                dataJson="${groupDataJson}"
                linkGroupName="true" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".attribute">
            <span class="focusableFieldHolder">
                <tags:selectWithItems path="attribute" items="${groupedAttributes}" itemLabel="message" itemValue="key"
                    groupItems="true"/>
            </span>
            <span class="focused-field-description">We will go through each device in the
                device group selected above and find the point for the selected attribute. This
                is the point we will push at dispatch to save to the db.</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".startTime">
            <date:dateTime path="start" value="${bulkInjection.start}" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".stopTime">
            <date:dateTime path="stop" value="${bulkInjection.stop}" />
            
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".period">
            <span class="focusableFieldHolder">
                <form:input path="period"/>
            </span>
            <span class="focused-field-description">The interval that should be used when
                iterating between our range. Input format for this is a shorthand notation
                defined in SimplePeriodFormat.java. For example: "1w", or "1h15m"</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".periodWindow">
            <span class="focusableFieldHolder">
                <form:input path="periodWindow"/>
            </span>
            <span class="focused-field-description">The maximum random interval period that
                should be added onto the timestamp we will send to dispatch. Same shorthand
                notation as a Period above. This is calculated by converting the Period Window
                to milliseconds, then calculating a random number between 0 and that number.
                This is done per point injection.</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".quality">
            <span class="focusableFieldHolder">
                <form:select path="pointQualities" multiple="multiple" size="4">
                    <c:forEach items="${qualities}" var="quality">
                        <form:option value="${quality}">${quality}</form:option>
                    </c:forEach>
                </form:select>
            </span>
            <span class="focused-field-description">The point quality. If more than 1 is
                selected than a random quality between those selected is used per point
                injection.</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".distributionAlg">
            <span class="focusableFieldHolder">
                <form:select path="algorithm">
                    <form:option value="normal">Normal (Gaussian)</form:option>
                    <form:option value="uniform">Uniform</form:option>
                </form:select>
            </span>
            <span class="focused-field-description">Normal
                (http://en.wikipedia.org/wiki/Normal_distribution), which better represents
                reads coming in from the field or Uniform Distribution.</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".incremental">
            <span class="focusableFieldHolder">
                <form:checkbox path="incremental" />
            </span>
            <span class="focused-field-description">If checked, then each value generated
                per point will be treated as a delta of the total. Main reason for this is for
                the attribute "Usage" (kWh) </span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".valueRangeLow">
            <span class="focusableFieldHolder">
                <form:input path="valueLow"/>
            </span>
            <span class="focused-field-description">Values generated will never be less
                than this. If Normal Distribution is used then this value is treated as the -3rd
                standard deviation from the mean.</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".valueRangeHigh">
            <span class="focusableFieldHolder">
                <form:input path="valueHigh"/>
            </span>
            <span class="focused-field-description">Values generated will never be greater
                than this. If Normal Distribution is used then this value is treated as the 3rd
                standard deviation from the mean.</span>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".decimalPlaces">
            <span class="focusableFieldHolder">
                <form:input path="decimalPlaces"/>
            </span>
            <span class="focused-field-description">The number of decimal places to round
                the calculated value to</span>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".archive">
            <span class="focusableFieldHolder">
                <form:checkbox path="archive" />
            </span>
            <span class="focused-field-description">Whether or not the value should be forced to be
                archived.</span>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="page-action-area">
        <cti:button nameKey="send" type="submit" classes="bulkInject js-blocker" />
    </div>
</form:form>
</cti:standardPage>

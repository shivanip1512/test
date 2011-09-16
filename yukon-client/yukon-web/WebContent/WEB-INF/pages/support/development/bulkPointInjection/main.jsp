<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="support" page="bulkPointInjection">

<script type="text/javascript">
YEvent.observeSelectorClick('.bulkInject', function(event) {
    combineDateAndTimeFields('start');
    combineDateAndTimeFields('stop');
    $('populateDbForm').submit();
});
</script>

<form:form commandName="bulkInjection" action="sendBulkData" method="post" id="populateDbForm">
    <tags:nameValueContainer2>
        <%-- device group --%>
        <tags:nameValue2 nameKey=".deviceGroup">
            <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="groupDataJson" />
            <tags:deviceGroupNameSelector fieldName="groupName"
                fieldValue="${bulkInjection.groupName}" dataJson="${groupDataJson}"
                linkGroupName="true" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".attribute">
            <form:select path="attribute">
                <c:forEach items="${allAttributes}" var="attribute">
                    <form:option value="${attribute}">${attribute.description}</form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".startTime">
            <tags:dateTimeInput path="start" fieldValue="${bulkInjection.start}" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".stopTime">
            <tags:dateTimeInput path="stop" fieldValue="${bulkInjection.stop}" />
        </tags:nameValue2>
        <tags:inputNameValue nameKey=".period" path="period" />
        <tags:inputNameValue nameKey=".periodWindow" path="periodWindow" />
        <tags:nameValue2 nameKey=".quality">
            <form:select path="pointQualities" multiple="multiple" size="4">
                <c:forEach items="${qualities}" var="quality">
                    <form:option value="${quality}">${quality}</form:option>
                </c:forEach>
            </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".distributionAlg">
            <form:select path="algorithm">
                <form:option value="normal">Normal (Gaussian)</form:option>
                <form:option value="uniform">Uniform</form:option>
            </form:select>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".incremental">
            <form:checkbox path="incremental" />
        </tags:nameValue2>
        <tags:inputNameValue nameKey=".valueRangeLow" path="valueLow" />
        <tags:inputNameValue nameKey=".valueRangeHigh" path="valueHigh" />
        <tags:nameValue2 nameKey=".archive">
            <form:checkbox path="archive" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="pageActionArea">
        <cti:button nameKey="send" type="submit" styleClass="bulkInject f_blocker" />
    </div>
</form:form>
</cti:standardPage>

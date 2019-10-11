<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="dev" page="rfnTest.statusArchiveRequest">

<tags:sectionContainer2 nameKey="demandReset">
    <form action="sendDemandResetStatusArchiveRequest" method="post">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            <tags:nameValue name="Demand Reset Status Code">
                <select name="statusCode">
                    <c:forEach items="${dRStatusCodes}" var="dRStatusCode">
                        <option value="${dRStatusCode.ordinal()}">
                             ${dRStatusCode.name()}
                        </option>
                    </c:forEach>
                </select>
            </tags:nameValue>
            <tags:nameValue name="Number of Messages to send"><input name="messageCount" type="text" value="10"></tags:nameValue>
        </tags:nameValueContainer>
        <div class="page-action-area">
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form>
</tags:sectionContainer2>
<tags:sectionContainer2 nameKey="meterInfo">
    <form:form action="sendMeterInfoStatusArchiveRequest" method="post" modelAttribute="meterInfoStatus">
        <cti:csrfToken/>
        <tags:nameValueContainer>
            <%@include file="rfnManufacturerModel.jspf" %>
            <%@include file="rfnSerialNumberRange.jspf" %>
            <tags:nameValue name="Meter Configuration ID">
                <tags:input path="meterConfigurationId" placeholder="optional"/>
            </tags:nameValue>
            <tags:nameValue name="Meter Disconnect Meter Mode">
                <form:select path="meterMode">
                    <form:option value="">(none)</form:option>
                    <form:options items="${meterDisconnectMeterModes}"/>
                </form:select>
            </tags:nameValue>
            <tags:nameValue name="Meter Disconnect State Type">
                <form:select path="relayStatus">
                    <form:option value="">(none)</form:option>
                    <form:options items="${meterDisconnectStateTypes}"/>
                </form:select>
            </tags:nameValue>
        </tags:nameValueContainer>
        <div class="page-action-area">
            <cti:button nameKey="send" type="submit" classes="js-blocker"/>
        </div>
    </form:form>
</tags:sectionContainer2>
</cti:standardPage>
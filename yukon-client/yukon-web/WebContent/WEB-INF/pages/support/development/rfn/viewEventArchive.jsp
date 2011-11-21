<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<cti:standardPage module="support" page="rfnTest">

    <tags:sectionContainer2 nameKey="${formAction}">
        <form:form action="${formAction}" method="post" commandName="event">
            <tags:nameValueContainer>
                <tags:nameValue name="Serial Number">
                    <form:input path="serialFrom" /> to <form:input path="serialTo"/>
                </tags:nameValue>
                
                <tags:nameValue name="Manufacturer">
                    <form:select path="manufacturer">
                        <form:option value="LGYR">LGYR</form:option>
                        <form:option value="Eka">Eka</form:option>
                        <form:option value="EE">EE</form:option>
                        <form:option value="GE">GE</form:option>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Model">
                    <form:select path="model">
                        <form:option value="FocuskWh">FocuskWh</form:option>
                        <form:option value="water_sensor">water_sensor</form:option>
                        <form:option value="water_sensor">water_node</form:option>
                        <form:option value="A3R">A3R</form:option>
                        <form:option value="Centron">Centron</form:option>
                        <form:option value="kV2">kV2</form:option>
                        <form:option value="FocusAXD">FocusAXD</form:option>
                        <form:option value="FocusAXR">FocusAXR</form:option>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValue name="Event Type">
                    <form:select path="rfnConditionType" styleClass="rfnConditionTypes">
                        <c:forEach items="${rfnConditionTypes}" var="type">
                            <form:option value="${type}">${type}</form:option>
                        </c:forEach>
                    </form:select>
                </tags:nameValue>
                
                <tags:nameValueContainer2>
                    <br>
                    <strong><u>Condition Data Types (meta data)</u></strong>
                    <tags:inputNameValue nameKey=".dataType.CLEARED" path="cleared"/>
                    <tags:inputNameValue nameKey=".dataType.COUNT" path="count"/>
                    <tags:inputNameValue nameKey=".dataType.DIRECTION" path="direction"/>
                    <tags:inputNameValue nameKey=".dataType.MEASURED_VALUE" path="measuredValue"/>
                    <tags:inputNameValue nameKey=".dataType.OUTAGE_START_TIME" path="outageStartTime"/>
                    <tags:inputNameValue nameKey=".dataType.THRESHOLD_VALUE" path="thresholdValue"/>
                    <tags:inputNameValue nameKey=".dataType.UOM" path="uom"/>
                    <tags:inputNameValue nameKey=".dataType.UOM_MODIFIERS" path="uomModifiers"/>
                </tags:nameValueContainer2>
                
            </tags:nameValueContainer>
            <div class="pageActionArea">
                <cti:button nameKey="send" type="submit" styleClass="f_blocker"/>
            </div>
        </form:form>
    </tags:sectionContainer2>
    
</cti:standardPage>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<tags:nameValueContainer>
                
    <tags:nameValue name="Last Interval">
        <tags:attributeValue device="${device}" attribute="${voltageAttribute}" />
    </tags:nameValue>
    
    <tags:nameValue name="Minimum">
        <tags:attributeValue device="${device}" attribute="${minimumVoltageAttribute}" />
    </tags:nameValue>
    
    <tags:nameValue name="Maximum">
        <tags:attributeValue device="${device}" attribute="${maximumVoltageAttribute}" />
    </tags:nameValue>
    
    <c:if test="${isReadable}">
        <tr><td><div style="height:8px;"></div></td></tr>
        <tr>
            <td>
                <tags:widgetActionUpdate method="readVoltage" label="Read" labelBusy="Reading" container="${widgetParameters.widgetId}_results" /></td>
            <td>
                <div id="${widgetParameters.widgetId}_results">
                </div>
            </td>
        </tr>
    </c:if>

</tags:nameValueContainer>


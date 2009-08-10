<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <div>
        <ct:nameValueContainer>
            
		    <ct:nameValue name="Current Configuration">
                <c:out value="${currentConfigName}"/>
		    </ct:nameValue>
		    <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
			    <ct:nameValue name="Device Configurations">
	                <select id="configuration" name="configuration">
	                    <option value="-1">(none)</option>
	                    <c:forEach var="config" items="${existingConfigs}">
	                        <option value="${config.id}" <c:if test="${config.id == currentConfigId}">selected</c:if>>${config.name}</option>
	                    </c:forEach>
	                </select>
	                <ct:widgetActionRefresh method="assignConfig" label="Assign" labelBusy="Assigning"/>
	            </ct:nameValue>
            </cti:checkRolesAndProperties>
		</ct:nameValueContainer>
                        
        <%-- ASSIGN BUTTON --%>
        <c:choose>
            <c:when test="${fn:length(existingConfigs) > 0}">
                <div style="text-align: right">
                    <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                        <ct:widgetActionUpdate method="sendConfig" label="Send" labelBusy="Sending" container="${widgetParameters.widgetId}_config_results"/>
                        <ct:widgetActionUpdate method="readConfig" label="Read" labelBusy="Reading" container="${widgetParameters.widgetId}_config_results"/>
                    </cti:checkRolesAndProperties>
                    <ct:widgetActionUpdate method="verifyConfig" label="Verify" labelBusy="Verifying" container="${widgetParameters.widgetId}_config_results"/>
				</div>
            </c:when>
            <c:otherwise>
                There are no existing configurations.<br><br>
            </c:otherwise>
        </c:choose>
        
        <div id="${widgetParameters.widgetId}_config_results"></div>
    </div>

<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:includeCss link="/WebConfig/yukon/styles/YukonGeneralStyles.css"/>
    <div>
        <ct:nameValueContainer2>
            
		    <ct:nameValue2 nameKey=".currentConfigurations">
                <c:out value="${currentConfigName}"/>
		    </ct:nameValue2>
		    <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
			    <ct:nameValue2 nameKey=".deviceConfigurations">
	                <select id="configuration" name="configuration">
	                    <option value="-1">(none)</option>
	                    <c:forEach var="config" items="${existingConfigs}">
	                        <option value="${config.id}" <c:if test="${config.id == currentConfigId}">selected</c:if>>${config.name}</option>
	                    </c:forEach>
	                </select>
	                <ct:widgetActionRefresh method="assignConfig" nameKey="assign"/>
	            </ct:nameValue2>
            </cti:checkRolesAndProperties>
		</ct:nameValueContainer2>
                        
        <%-- ASSIGN BUTTON --%>
        <c:choose>
            <c:when test="${fn:length(existingConfigs) > 0}">
				<c:if test="${currentConfigId >= 0}">
	                <div style="text-align: right">
	                    <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
	                        <ct:widgetActionUpdate method="sendConfig" nameKey="send" container="${widgetParameters.widgetId}_config_results"/>
	                        <ct:widgetActionUpdate method="readConfig" nameKey="read" container="${widgetParameters.widgetId}_config_results"/>
	                    </cti:checkRolesAndProperties>
	                    <ct:widgetActionUpdate method="verifyConfig" nameKey="verify" container="${widgetParameters.widgetId}_config_results"/>
					</div>
				</c:if>
            </c:when>
            <c:otherwise>
                <i:inline key=".noConfigurations"/><br><br>
            </c:otherwise>
        </c:choose>
        
        <div id="${widgetParameters.widgetId}_config_results"></div>
    </div>

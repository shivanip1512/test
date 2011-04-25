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
                    <cti:msg2 var="assign" key=".assign"/>
                    <cti:msg2 var="assigning" key=".assigning"/>
	                <ct:widgetActionRefresh method="assignConfig" label="${assign}" labelBusy="${assigning}"/>
	            </ct:nameValue2>
            </cti:checkRolesAndProperties>
		</ct:nameValueContainer2>
                        
        <%-- ASSIGN BUTTON --%>
        <c:choose>
            <c:when test="${fn:length(existingConfigs) > 0}">
                <div style="text-align: right">
                    <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                    <cti:msg2 var="send" key=".send"/>
                    <cti:msg2 var="sending" key=".sending"/>
                    <cti:msg2 var="read" key=".read"/>
                    <cti:msg2 var="reading" key=".reading"/>
                        <ct:widgetActionUpdate method="sendConfig" label="${send}" labelBusy="${sending}" container="${widgetParameters.widgetId}_config_results"/>
                        s<ct:widgetActionUpdate method="readConfig" label="${read}" labelBusy="${reading}" container="${widgetParameters.widgetId}_config_results"/>
                    </cti:checkRolesAndProperties>
                    <cti:msg2 var="verify" key=".verify"/>
                    <cti:msg2 var="verifying" key=".verifying"/>
                    <ct:widgetActionUpdate method="verifyConfig" label="${verify}" labelBusy="${verifying}" container="${widgetParameters.widgetId}_config_results"/>
				</div>
            </c:when>
            <c:otherwise>
                <i:inline key=".noConfigurations"/><br><br>
            </c:otherwise>
        </c:choose>
        
        <div id="${widgetParameters.widgetId}_config_results"></div>
    </div>

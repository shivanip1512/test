<%@ taglib tagdir="/WEB-INF/tags" prefix="ct" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:includeCss link="/WebConfig/yukon/styles/yukon.css"/>
    <div>
        <table>
            <tr>
                <td>
                    <ct:nameValueContainer2>
                        <ct:nameValue2 nameKey=".currentConfigurations">
                            <c:out value="${currentConfigName}"/>
                        </ct:nameValue2>
                    </ct:nameValueContainer2>
                </td>
                <td>
                    <c:if test="${not empty currentConfigId}">
						<cm:dropdownActions type="button">
							<cti:checkRolesAndProperties value="ASSIGN_CONFIG">
								<li>
                                    <ct:widgetActionRefresh method="unassignConfig"
										nameKey="unassign" type="link"/>
                                </li>
							</cti:checkRolesAndProperties>
							<cti:checkRolesAndProperties value="SEND_READ_CONFIG">
								<li>
								    <ct:widgetActionUpdate method="sendConfig"
										nameKey="send"
										container="${widgetParameters.widgetId}_config_results"
										type="link"/>
                                </li>
								<li>
								    <ct:widgetActionUpdate method="readConfig"
										nameKey="read"
										container="${widgetParameters.widgetId}_config_results"
										type="link"/>
                                </li>
							</cti:checkRolesAndProperties>
							<li>
                                <ct:widgetActionUpdate method="verifyConfig"
									nameKey="verify"
									container="${widgetParameters.widgetId}_config_results"
									type="link"/>
                            </li>
						</cm:dropdownActions>
                    </c:if>
                </td>
            </tr>
            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                <tr>
                    <td>
                        <ct:nameValueContainer2>
                            <ct:nameValue2 nameKey=".deviceConfigurations">
                                <select id="configuration" name="configuration">
                                    <c:forEach var="config" items="${existingConfigs}">
                                        <option value="${config.id}" <c:if test="${config.id == currentConfigId}">selected</c:if>>${config.name}</option>
                                    </c:forEach>
                                </select>
                            </ct:nameValue2>
                        </ct:nameValueContainer2>
                    </td>
                    <td>
                        <ct:widgetActionRefresh method="assignConfig" nameKey="assign"/>
                    </td>
                </tr>
            </cti:checkRolesAndProperties>
        </table>
        
        <div id="${widgetParameters.widgetId}_config_results"></div>
    </div>
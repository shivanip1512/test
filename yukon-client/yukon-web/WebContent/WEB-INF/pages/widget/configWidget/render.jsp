<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>

<cti:includeScript link="/JavaScript/dropdown_actions.js"/>
<cti:includeCss link="/WebConfig/yukon/styles/yukon.css"/>

    <div>
        <table>
            <tr>
                <td colspan="2">                  
                    <div class="fr">
                        <c:if test="${not empty currentConfigId}">
                            <cm:dropdown type="button">
                                <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                                    <li>
                                        <tags:widgetActionRefresh method="unassignConfig"
                                            nameKey="unassign" type="link"/>
                                    </li>
                                </cti:checkRolesAndProperties>
                                <cti:checkRolesAndProperties value="SEND_READ_CONFIG">
                                    <li>
                                        <tags:widgetActionUpdate method="sendConfig"
                                            nameKey="send"
                                            container="${widgetParameters.widgetId}_config_results"
                                            type="link"
                                            waitingTextLocation="#${widgetParameters.widgetId}_busy" />
                                    </li>
                                    <li>
                                        <tags:widgetActionUpdate method="readConfig"
                                            nameKey="read"
                                            container="${widgetParameters.widgetId}_config_results"
                                            type="link"
                                            waitingTextLocation="#${widgetParameters.widgetId}_busy" />
                                    </li>
                                </cti:checkRolesAndProperties>
                                <li>
                                    <tags:widgetActionUpdate method="verifyConfig"
                                        nameKey="verify"
                                        container="${widgetParameters.widgetId}_config_results"
                                        type="link" 
                                        waitingTextLocation="#${widgetParameters.widgetId}_busy" />
                                </li>
                            </cm:dropdown>
                        </c:if>
                    </div>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".currentConfigurations">
                            <c:out value="${currentConfigName}"/>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                </td>
            </tr>
            <cti:checkRolesAndProperties value="ASSIGN_CONFIG">
                <tr>
                    <td>
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".deviceConfigurations">
                                <select id="configuration" name="configuration" class="full-width">
                                    <c:forEach var="config" items="${existingConfigs}">
                                        <option value="${config.configurationId}" <c:if test="${config.configurationId == currentConfigId}">selected</c:if>>${fn:escapeXml(config.name)}</option>
                                    </c:forEach>
                                </select>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                    </td>
                    <td>
                        <tags:widgetActionRefresh method="assignConfig" nameKey="assign"/>
                    </td>
                </tr>
            </cti:checkRolesAndProperties>
        </table>
        <div id="${widgetParameters.widgetId}_busy" class="stacked"></div>
        <div id="${widgetParameters.widgetId}_config_results"></div>
    </div>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="appliance.list">

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING" >
        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE">
    
            <script type="text/javascript">
    
                function showCreateAppliancePopup() {
                    $('createAppliancePopup').show();
                }
         
            </script>

            <%-- CREATE APPLIANCE POPUP --%>
            <i:simplePopup titleKey=".createAppliancePopup.title" id="createAppliancePopup" 
                           styleClass="smallSimplePopup" 
                           showImmediately="${param.showSwitchCheckingPopup}">
                <form action="/spring/stars/operator/appliances/applianceNew">
                    <input type="hidden" name="accountId" value="${accountId}"/>
                    <input type="hidden" name="new" />

                    <ct:nameValueContainer2>
                        <ct:nameValue2 nameKey=".applianceName">
                            <select name="applianceCategoryId">
                                <c:forEach var="applianceCategory" items="${applianceCategories}">
                                    <option value="${applianceCategory.applianceCategoryId}">
                                        <spring:htmlEscape defaultHtmlEscape="true">
                                            ${applianceCategory.name}
                                        </spring:htmlEscape>
                                    </option>
                                </c:forEach>
                            </select>
                        </ct:nameValue2>
                    </ct:nameValueContainer2>
                    <div class="actionArea">
                        <cti:button nameKey="ok" type="submit"/>
                        <cti:button nameKey="cancel" onclick="$('createAppliancePopup').hide();"/>
                    </div>
                </form>
            </i:simplePopup>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>

    <ct:boxContainer2 nameKey=".appliances">
        <table class="compactResultsTable">
            <c:choose>
                <c:when test="${fn:length(displayableApplianceListEntries) > 0}">
                     <tr>
                         <th><i:inline key=".applianceName" /></th>
                         <th><i:inline key=".deviceName" /></th>
                         <th><i:inline key=".programName" /></th>
                         <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING" >
                             <th class="removeColumn"><i:inline key=".delete" /></th>
                         </cti:checkRolesAndProperties>
                     </tr>

                     <c:forEach var="displayableApplianceListEntry" items="${displayableApplianceListEntries}">
                         <tr class="<ct:alternateRow odd="" even="altRow"/>">
                             <td>
                                 <cti:url var="editApplianceUrl" value="/spring/stars/operator/appliances/view">
                                     <cti:param name="accountId" value="${accountId}"/>
                                     <cti:param name="applianceId" value="${displayableApplianceListEntry.applianceId}" />
                                 </cti:url>
                                 <a href="${editApplianceUrl}">
                                     <spring:escapeBody htmlEscape="true">
                                         ${displayableApplianceListEntry.applianceName}
                                     </spring:escapeBody>
                                 </a>
                             </td>
                             <td>
                                 <c:choose>
                                     <c:when test="${displayableApplianceListEntry.deviceLabel eq '(none)'}">
                                         <i:inline key=".notApplicable"/>
                                     </c:when>
                                     <c:otherwise>
                                         <spring:escapeBody htmlEscape="true">
                                             ${displayableApplianceListEntry.deviceLabel}
                                         </spring:escapeBody>
                                     </c:otherwise>
                                 </c:choose>
                             </td>
                             <td>
                                 <c:choose>
                                     <c:when test="${displayableApplianceListEntry.assignedProgramName eq '(none)'}">
                                         <i:inline key=".notApplicable"/>
                                     </c:when>
                                     <c:otherwise>
                                         <spring:escapeBody htmlEscape="true">
                                             ${displayableApplianceListEntry.assignedProgramName}
                                         </spring:escapeBody>
                                     </c:otherwise>
                                 </c:choose>
                             </td>
                             <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING" >
                                 <td class="removeColumn">
                                    <cti:url var="deleteApplianceUrl" value="/spring/stars/operator/appliances/applianceDelete">
                                        <cti:param name="accountId" value="${accountId}" />
                                        <cti:param name="applianceId" value="${displayableApplianceListEntry.applianceId}" />
                                        <cti:param name="delete" />
                                    </cti:url>
                                    <cti:img href="${deleteApplianceUrl}" nameKey="deleteAppliance"/>

                                 </td>
                             </cti:checkRolesAndProperties>
                         </tr>
                     </c:forEach>
                 </c:when>
                 <c:otherwise>
                     <i:inline key=".noAppliances"/>
                 </c:otherwise>
            </c:choose>

            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING" >
                <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE">
                    <tr align="right">
                        <td colspan="4">
                            <br>
                            <cti:button nameKey="create" type="submit" onclick="showCreateAppliancePopup();"/>
                        </td>
                    </tr>
                </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
        </table>
    </ct:boxContainer2>
</cti:standardPage>
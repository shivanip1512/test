<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="appliance.list">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING" >
        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE">
            <%-- CREATE APPLIANCE POPUP --%>
            <i:simplePopup on="#createAppliance" titleKey=".createAppliancePopup.title" id="createAppliancePopup" showImmediately="${param.showSwitchCheckingPopup}">
                <cti:url var="applianceNewUrl" value="/stars/operator/appliances/applianceNew"/>
                <form action="${applianceNewUrl}">
                    <input type="hidden" name="accountId" value="${accountId}"/>
                    <input type="hidden" name="new" />

                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".applianceName">
                            <select name="applianceCategoryId">
                                <c:forEach var="applianceCategory" items="${applianceCategories}">
                                    <option value="${applianceCategory.applianceCategoryId}">${fn:escapeXml(applianceCategory.name)}</option>
                                </c:forEach>
                            </select>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <div class="action-area">
                        <cti:button nameKey="ok" type="submit" classes="action primary"/>
                        <cti:button nameKey="cancel" onclick="$('#createAppliancePopup').dialog('close');"/>
                    </div>
                </form>
            </i:simplePopup>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>

    <c:choose>
        <c:when test="${fn:length(displayableApplianceListEntries) > 0}">
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".applianceName" /></th>
                        <th><i:inline key=".deviceName" /></th>
                        <th><i:inline key=".programName" /></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                 <c:forEach var="displayableApplianceListEntry" items="${displayableApplianceListEntries}">
                     <tr>
                         <td>
                             <cti:url var="editApplianceUrl" value="/stars/operator/appliances/view">
                                 <cti:param name="accountId" value="${accountId}"/>
                                 <cti:param name="applianceId" value="${displayableApplianceListEntry.applianceId}" />
                             </cti:url>
                             <a href="${editApplianceUrl}">${fn:escapeXml(displayableApplianceListEntry.applianceName)}</a>
                         </td>
                         <td>
                             <c:choose>
                                 <c:when test="${displayableApplianceListEntry.deviceLabel eq '(none)'}">
                                     <i:inline key=".notApplicable"/>
                                 </c:when>
                                 <c:otherwise>${fn:escapeXml(displayableApplianceListEntry.deviceLabel)}</c:otherwise>
                             </c:choose>
                         </td>
                         <td>
                             <c:choose>
                                 <c:when test="${displayableApplianceListEntry.assignedProgramName eq '(none)'}">
                                     <i:inline key=".notApplicable"/>
                                 </c:when>
                                 <c:otherwise>${fn:escapeXml(displayableApplianceListEntry.assignedProgramName)}</c:otherwise>
                             </c:choose>
                         </td>
                     </tr>
                 </c:forEach>
                 
            </table>
         </c:when>
         <c:otherwise>
             <span class="empty-list"><i:inline key=".noAppliances"/></span>
         </c:otherwise>
    </c:choose>

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING" >
        <cti:checkRolesAndProperties value="OPERATOR_CONSUMER_INFO_APPLIANCES_CREATE">
            <div class="page-action-area">
                <cti:button id="createAppliance" nameKey="create" icon="icon-plus-green" />
            </div>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>
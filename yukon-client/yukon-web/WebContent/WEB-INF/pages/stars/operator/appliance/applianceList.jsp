<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="appliance.list">

    <c:choose>
         <c:when test="${fn:length(displayableApplianceListEntries) > 1}">
             <ct:boxContainer2 key=".appliances">
                 <table class="compactResultsTable">
                     <tr>
                         <th><i:inline key=".applianceName" /></th>
                         <th><i:inline key=".inventoryName" /></th>
                         <th><i:inline key=".programName" /></th>
                         <th><i:inline key=".actions" /></th>
                     </tr>

                     <c:forEach var="displayableApplianceListEntry" items="${displayableApplianceListEntries}">
                         <tr class="<ct:alternateRow odd="" even="altRow"/>">
                             <td>
                                 <cti:url var="editApplianceUrl" value="/spring/stars/operator/appliances/applianceEdit">
                                     <cti:param name="accountId" value="${accountId}"/>
                                     <cti:param name="energyCompanyId" value="${energyCompanyId}" />
                                     <cti:param name="applianceId" value="${displayableApplianceListEntry.applianceId}" />
                                 </cti:url>
                                 <a href="${editApplianceUrl}">
                                     <spring:htmlEscape defaultHtmlEscape="true">
                                         ${displayableApplianceListEntry.applianceName}
                                     </spring:htmlEscape>
                                 </a>
                             </td>
                             <c:choose>
                                <c:when test="${displayableApplianceListEntry.assignedProgramName eq '(none)' or
                                         displayableApplianceListEntry.serialNumber eq '(none)'}">
                                    <td colspan="2"><i:inline key=".applianceNotAssigned"/></td>
                                </c:when>
                                <c:otherwise>
                                    <td><spring:htmlEscape defaultHtmlEscape="true">
                                        ${displayableApplianceListEntry.serialNumber}
                                    </spring:htmlEscape></td>
                                    <td><spring:htmlEscape defaultHtmlEscape="true">
                                        ${displayableApplianceListEntry.assignedProgramName}
                                    </spring:htmlEscape></td>
                                </c:otherwise>
                             </c:choose>
                             <td>
                                 <cti:url var="deleteApplianceUrl" value="/spring/stars/operator/appliances/applianceDelete">
                                     <cti:param name="accountId" value="${accountId}"/>
                                     <cti:param name="energyCompanyId" value="${energyCompanyId}" />
                                     <cti:param name="applianceId" value="${displayableApplianceListEntry.applianceId}" />
                                 </cti:url>
                                 <i:simpleLink actionUrl="${deleteApplianceUrl}" logoKey="deleteAppliance"/>

                             </td>
                         </tr>
                     </c:forEach>
                     
                     
                     <tr align="right">
                         <td colspan="4">
                             <br>
                             <form action="/spring/stars/operator/appliances/applianceNew">
                                 <input type="hidden" name="accountId" value="${accountId}"/>
                                 <input type="hidden" name="energyCompanyId" value="${energyCompanyId}" />
                                 
                                 <table>
                                     <tr align="right"><td>
                                         <select name="applianceCategoryId">
                                             <c:forEach var="applianceCategory" items="${applianceCategories}">
                                                 <option value="${applianceCategory.applianceCategoryId}">
                                                    <spring:htmlEscape defaultHtmlEscape="true">
                                                        ${applianceCategory.name}
                                                    </spring:htmlEscape>
                                                </option>
                                             </c:forEach>
                                         </select>
                                     </td></tr>
                                     <tr align="right"><td>
                                         <input type="submit" value="<cti:msg2 key=".create"/>" />
                                     </td></tr>
                                 </table>
                             </form>
                         </td>
                     </tr>
                 </table>
             </ct:boxContainer2>
         </c:when>
         <c:otherwise>
             <i:inline key=".noAppliances"/>
         </c:otherwise>
	</c:choose>

</cti:standardPage>
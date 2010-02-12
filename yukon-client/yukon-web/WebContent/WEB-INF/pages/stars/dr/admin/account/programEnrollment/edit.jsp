<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="accountAdmin" page="programsEnrollment">

    <cti:standardMenu menuSelection="programs|enrollment"/>

    <tags:simpleDialog id="peDialog"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.breadcrumb.editProgramEnrollment"/></cti:crumbLink>
    </cti:breadCrumbs>

    <form>
        <script type="text/javascript">
        function selectDevices(devices) {
            alert('second page of wizard here');
        }
        </script>
        <input type="hidden" id="programsToAssign" name="programsToAssign"/>
    </form>

    <%-- criteria needs to include only programs which assigned to an appliance category and are in a control area --%>
    <%--
    <cti:drPicker pickerId="programPicker"
        constraint="com.cannontech.common.search.criteria.LMProgramOrScenarioCriteria"
        paoIdField="programsToAssign" finalTriggerAction="selectDevices"
        selectionLinkName="Next"/>
    --%>

    <table width="100%" style="margin-bottom: 20px;">
        <tr valign="top">
            <td>
                <div style="font-size: 10px;">
                    Acct #1234567890<br>
                    Jon Doe, (612) 555-1111(H), (763) 555-2222(W)<br>
                    2092 E 3RD AVE APT 2103, Minneapolis, MN 55401<br>
                </div>
            </td>
            <td align="right">
                Search <input type="text"/>
            </td>
        </tr>
    </table>

    <c:set var="baseUrl" value="/spring/stars/dr/admin/account/programEnrollment/edit"/>
    <c:set var="editEnrollmentUrl" value="/spring/stars/dr/admin/account/programEnrollment/editEnrollment"/>

    <cti:msg var="boxTitle" key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.enrolledPrograms"/>
    <tags:pagedBox title="${boxTitle}" searchResult="${programEnrollments}"
        baseUrl="${baseUrl}">
        <table id="programList" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.name"
                        baseUrl="${baseUrl}" fieldName="PROGRAM_NAME" isDefault="true"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.applianceCategory"
                        baseUrl="${baseUrl}" fieldName="APPLIANCE_CATEGORY" isDefault="false"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.group"
                        baseUrl="${baseUrl}" fieldName="GROUP" isDefault="false"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.hardwareAndRelay"
                        baseUrl="${baseUrl}" fieldName="HARDWARE" isDefault="false"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.status"
                        baseUrl="${baseUrl}" fieldName="STATUS" isDefault="false"/>
                </th>
                <th>
                    <cti:msg key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.actions"/>
                </th>
            </tr>
            <c:forEach var="programEnrollment" items="${programEnrollments.resultList}">
                <c:set var="programId" value="${programEnrollment.paoIdentifier.paoId}"/>
                <c:url var="programUrl" value="/spring/dr/program/detail">
                    <c:param name="programId" value="${programId}"/>
                </c:url>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <a href="${programUrl}"><spring:escapeBody htmlEscape="true">${programEnrollment.name}</spring:escapeBody></a>
                    </td>
                    <td><spring:escapeBody htmlEscape="true">${programEnrollment.applianceCategory}</spring:escapeBody></td>
                    <td><spring:escapeBody htmlEscape="true">${programEnrollment.group}</spring:escapeBody></td>
                    <td><spring:escapeBody htmlEscape="true">hardware here</spring:escapeBody></td>
                    <td><spring:escapeBody htmlEscape="true">${programEnrollment.status}</spring:escapeBody></td>
                    <td>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.editDialogTitle" 
                            dialogId="peDialog" 
                            actionUrl="${editEnrollmentUrl}" 
                            logoKey="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.edit"/>
                        <tags:simpleDialogLink titleKey="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.removeDialogTitle" 
                            dialogId="peDialog" 
                            actionUrl="${editEnrollmentUrl}" 
                            logoKey="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.remove"/>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <div class="actionArea">
            <cti:msg var="addEnrollmentMsg" key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.addEnrollment"/>
            <%-- criteria needs to include only programs which are not already assigned and are in a control area --%>
            <input type="button" value="${addEnrollmentMsg}"
                onclick="javascript:programPicker.showPicker();"/>
        </div>
    </tags:pagedBox>

    <br><br>
    <cti:msg var="boxTitle" key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.programHistory"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
        <table class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th>
                    <cti:msg key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.date"/>
                </th>
                <th>
                    <cti:msg key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.type"/>
                </th>
                <th>
                    <cti:msg key="yukon.web.modules.stars.dr.admin.account.programEnrollment.edit.program"/>
                </th>
            </tr>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>12/02/2009 15:54:10</td>
                <td>Signup</td>
                <td>WH</td>
            </tr>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>12/02/2009 15:43:04</td>
                <td>Signup</td>
                <td>AC</td>
            </tr>
        </table>
    </tags:abstractContainer>

</cti:standardPage>

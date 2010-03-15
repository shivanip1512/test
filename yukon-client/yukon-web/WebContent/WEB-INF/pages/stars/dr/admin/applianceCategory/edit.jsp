<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="energyCompanyAdmin" page="editApplianceCategory">

    <cti:includeScript link="/JavaScript/iconChooser.js"/>

    <tags:simpleDialog id="acDialog"/>

    <c:set var="applianceCategoryId" value="${param.applianceCategoryId}"/>
    <c:set var="baseUrl" value="/spring/stars/dr/admin/applianceCategory/edit"/>
    <cti:url var="clearFilterUrl" value="${baseUrl}">
        <c:if test="${!empty param.itemsPerPage}">
            <cti:param name="itemsPerPage" value="${param.itemsPerPage}"/>
        </c:if>
        <c:if test="${!empty param.sort}">
            <cti:param name="sort" value="${param.sort}"/>
        </c:if>
        <c:if test="${!empty param.descending}">
            <cti:param name="descending" value="${param.descending}"/>
        </c:if>
        <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
    </cti:url>

    <script type="text/javascript">
    function clearFilter() {
        window.location = '${clearFilterUrl}';
    }
    </script>

    <form>
        <script type="text/javascript">
        function assignPrograms(devices) {
            openSimpleDialog('acDialog', $('assignProgramForm').action,
                    '<cti:msg2 key=".assignProgramsDialogTitle"/>', $('assignProgramForm').serialize(true));
            return true;
        }
        </script>
        <input type="hidden" id="programsToAssign" name="programsToAssign"/>
    </form>

    <i:simplePopup id="filterDialog" titleKey=".filters">
        <cti:url var="submitUrl" value="/spring/stars/dr/admin/applianceCategory/edit"/>
        <form:form action="${submitUrl}" commandName="backingBean" method="get">
            <input type="hidden" name="applianceCategoryId" value="${applianceCategoryId}"/>
            <tags:sortFields backingBean="${backingBean}"/>

            <table cellspacing="10">
                <tr>
                    <td><i:inline key=".programName"/></td>
                    <td><form:input path="name" size="40"/></td>
                </tr>
            </table>
    
            <br>
            <div class="actionArea">
                <input type="submit" value="<cti:msg2 key=".filterButton"/>"/>
                <input type="button" value="<cti:msg2 key=".showAllButton"/>"
                    onclick="javascript:clearFilter()"/>
            </div>
        </form:form>
    </i:simplePopup>

    <script type="text/javascript">
        function simpleAJAXRequest(url) {
            var successCallback = function(transport, json) {
                if (json.action === 'reload') {
                    window.location = window.location;
                }
            };

            var errorCallback = function(transport) {
                alert('error making request');
            };

            var options = {
                    'evalScript': true,
                    'method': 'post',
                    'onSuccess': successCallback,
                    'onFailure': errorCallback
                    };
            // 'parameters'
            new Ajax.Request(url, options);
        }
    </script>

    <table class="widgetColumns">
        <tr>
            <td class="widgetColumnCell" valign="top">
                <div class="widgetContainer">
                    <tags:boxContainer2 key="info">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".type">
                                <i:inline key="${applianceCategory.applianceType.formatKey}"/>
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".icon">
                                <img src="<cti:url value="/WebConfig/${applianceCategory.icon}"/>"/>
                            </tags:nameValue2>

                            <tags:nameValue2 nameKey=".description">
                                <spring:escapeBody>${applianceCategory.description}</spring:escapeBody>
                            </tags:nameValue2>
                        </tags:nameValueContainer2>
                        <br>
                        <c:if test="${applianceCategory.consumerSelectable}">
                            <i:inline key=".isConsumerSelectable"/>
                        </c:if>
                        <c:if test="${!applianceCategory.consumerSelectable}">
                            <i:inline key=".isNotConsumerSelectable"/>
                        </c:if>
                    </tags:boxContainer2>
                </div>
            </td>
            <td class="widgetColumnCell" valign="top">
                <div class="widgetContainer">
                    <tags:boxContainer2 key="actions">
                        <ul>
                            <cti:url var="detailUrl" value="/spring/stars/dr/admin/applianceCategory/editDetails">
                                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                            </cti:url>
                            <li>
                                <tags:simpleDialogLink2 dialogId="acDialog"
                                    key="edit" actionUrl="${detailUrl}"/>
                            </li>
                            <li>
                                <cti:url var="assignProgramUrl"
                                    value="/spring/stars/dr/admin/applianceCategory/assignProgram"/>
                                <form id="assignProgramForm" action="${assignProgramUrl}">
                                    <input type="hidden" name="applianceCategoryId"
                                        value="${applianceCategoryId}"/>
                                    <tags:pickerDialog type="applianceProgramPicker"
                                        id="programPicker" multiSelectMode="true"
                                        memoryGroup="programPicker"
                                        destinationFieldName="programsToAssign"
                                        endAction="assignPrograms"
                                        styleClass="simpleLink">
                                        <cti:labeledImg key="assignPrograms"/>
                                    </tags:pickerDialog>
                                </form>
                            </li>
                            <li>
                                <cti:url var="createVirtualProgramUrl"
                                    value="/spring/stars/dr/admin/applianceCategory/createVirtualProgram">
                                    <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                                </cti:url>
                                <tags:simpleDialogLink2 dialogId="acDialog"
                                    key="createVirtualProgram"
                                    actionUrl="${createVirtualProgramUrl}"/>
                            </li>
                        </ul>
                    </tags:boxContainer2>
                </div>
            </td>
        </tr>
        <tr>
            <td class="widgetColumnCell" valign="top" colspan="2">
                <div class="widgetContainer">

    <cti:msg2 var="boxTitle" key=".assignedPrograms"/>
    <tags:pagedBox title="${boxTitle}" searchResult="${assignedPrograms}"
        baseUrl="${baseUrl}" filterDialog="filterDialog" isFiltered="${isFiltered}"
        showAllUrl="${clearFilterUrl}">
        <table id="programList" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th>
                    <tags:sortLink key=".programName" baseUrl="${baseUrl}"
                        fieldName="PROGRAM_NAME" isDefault="${!applianceCategory.consumerSelectable}"/>
                </th>
                <th>
                    <i:inline key=".actions"/>
                </th>
                <c:if test="${applianceCategory.consumerSelectable}">
                    <th>
                        <tags:sortLink key=".displayOrder" baseUrl="${baseUrl}"
                            fieldName="DISPLAY_ORDER" isDefault="true"/>
                    </th>
                </c:if>
            </tr>
            <c:forEach var="assignedProgram" items="${assignedPrograms.resultList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <c:if test="${assignedProgram.programId == 0}">
                            <i:inline key=".virtualProgramName"
                                arguments="${assignedProgram.displayName}"/>
                        </c:if>
                        <c:if test="${assignedProgram.programId != 0}">
                            <spring:escapeBody htmlEscape="true">${assignedProgram.programName}</spring:escapeBody>
                            <c:if test="${!empty assignedProgram.displayName && assignedProgram.displayName != assignedProgram.programName}">
                                (<spring:escapeBody htmlEscape="true">${assignedProgram.displayName}</spring:escapeBody>)
                            </c:if>
                        </c:if>
                    </td>
                    <td>
                        <cti:url var="editProgramUrl" value="/spring/stars/dr/admin/applianceCategory/editAssignedProgram">
                            <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                            <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="acDialog"
                            key="editAssignedProgram" skipLabel="true" 
                            actionUrl="${editProgramUrl}"/>

                        <cti:url var="unassignProgramUrl" value="/spring/stars/dr/admin/applianceCategory/confirmUnassignProgram">
                            <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                            <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="acDialog"
                            key="unassignProgram" skipLabel="true"
                            actionUrl="${unassignProgramUrl}"/>
                    </td>
                    <c:if test="${applianceCategory.consumerSelectable}">
                    <td>
                        <c:if test="${assignedProgram.first}">
                            <cti:img key="up.disabled"/>
                        </c:if>
                        <c:if test="${!assignedProgram.first}">
                            <cti:url var="moveProgramUpUrl" value="/spring/stars/dr/admin/applianceCategory/moveProgram">
                                <cti:param name="direction" value="up"/>
                                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                                <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                            </cti:url>
                            <cti:img key="up" href="javascript:simpleAJAXRequest('${moveProgramUpUrl}')"/>
                        </c:if>
                        <c:if test="${assignedProgram.last}">
                            <cti:img key="down.disabled"/>
                        </c:if>
                        <c:if test="${!assignedProgram.last}">
                            <cti:url var="moveProgramDownUrl" value="/spring/stars/dr/admin/applianceCategory/moveProgram">
                                <cti:param name="direction" value="down"/>
                                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                                <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                            </cti:url>
                            <cti:img key="down" href="javascript:simpleAJAXRequest('${moveProgramDownUrl}')"/>
                        </c:if>
                        ${assignedProgram.programOrder}
                    </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
    </tags:pagedBox>
                </div>
            </td>
        </tr>
    </table>

</cti:standardPage>

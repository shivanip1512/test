<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="adminSetup" page="applianceCategory.${mode}">

<cti:includeScript link="/JavaScript/iconChooser.js"/>
<tags:setFormEditMode mode="${mode}"/>

<tags:simpleDialog id="acDialog"/>

<cti:url var="baseUrl" value="edit"/>
<cti:displayForPageEditModes modes="VIEW">
    <cti:url var="baseUrl" value="view"/>
</cti:displayForPageEditModes>
<c:set var="applianceCategoryId" value="${param.applianceCategoryId}"/>
<cti:url var="clearFilterUrl" value="${baseUrl}">
    <cti:param name="ecId" value="${param.ecId}"/>
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

<cti:displayForPageEditModes modes="CREATE,EDIT">
    <script type="text/javascript">
    lastDisplayName = false;
    sameAsNameClicked = function() {
        if ($('sameAsName').checked) {
            lastDisplayName = $('displayNameInput').value;
            $('displayNameInput').value = $('nameInput').value;
            $('displayNameInput').disable();
        } else {
            if (lastDisplayName) {
                $('displayNameInput').value = lastDisplayName;
            }
            $('displayNameInput').enable(); 
        }
    }

    nameChanged = function() {
        if ($('sameAsName').checked) {
            $('displayNameInput').value = $('nameInput').value;
        }
    }

    submitForm = function() {
        nameChanged();
        $('displayNameInput').enable();
        return true;
    }

    jQuery(function() {
        sameAsNameClicked();
      });
    </script>
</cti:displayForPageEditModes>

<cti:displayForPageEditModes modes="VIEW">
    <script type="text/javascript">
    function clearFilter() {
        window.location = '${clearFilterUrl}';
    }
    </script>

    <c:if test="${isEditable}">
        <form>
            <script type="text/javascript">
            var assignProgramsDialogTitle = '<cti:msg2 key=".assignProgramsDialogTitle" javaScriptEscape="true"/>';
            function assignPrograms(devices) {
                openSimpleDialog('acDialog', $('assignProgramForm').action,
                        assignProgramsDialogTitle, $('assignProgramForm').serialize(true));
                return true;
            }
            </script>
            <input type="hidden" id="programsToAssign" name="programsToAssign"/>
        </form>
    </c:if>

    <i:simplePopup id="filterDialog" titleKey=".filters">
        <form:form action="${baseUrl}" commandName="backingBean" method="get">
            <input type="hidden" name="ecId" value="${param.ecId}">
            <input type="hidden" name="applianceCategoryId" value="${applianceCategoryId}"/>
            <tags:sortFields backingBean="${backingBean}"/>

            <table cellspacing="10">
                <tr>
                    <td><i:inline key=".programName"/></td>
                    <td><form:input path="name" size="40"/></td>
                </tr>
            </table>

            <div class="actionArea">
                <cti:button type="submit" nameKey="filterButton"/>
                <cti:button nameKey="showAllButton" onclick="clearFilter();"/>
            </div>
        </form:form>
    </i:simplePopup>

    <c:if test="${isEditable}">
        <cti:url var="assignProgramUrl" value="assignProgram"/>
        <form id="assignProgramForm" action="${assignProgramUrl}">
            <input type="hidden" name="applianceCategoryId" value="${applianceCategoryId}"/>
            <input type="hidden" name="ecId" value="${param.ecId}"/>
            <tags:pickerDialog type="applianceCategoryProgramPicker" id="programPicker"
                linkType="none" multiSelectMode="true" memoryGroup="programPicker"
                destinationFieldName="programsToAssign" endAction="assignPrograms">
            </tags:pickerDialog>
        </form>
    </c:if>
</cti:displayForPageEditModes>

<cti:url var="submitUrl" value="save"/>
<form:form id="inputForm" commandName="applianceCategory" action="${submitUrl}" onsubmit="return submitForm()">

<form:hidden path="energyCompanyId"/>
<form:hidden path="applianceCategoryId"/>
<form:hidden path="webConfiguration.configurationId"/>
<tags:nameValueContainer>
    <cti:displayForPageEditModes modes="CREATE,EDIT">
        <cti:msg2 var="fieldName" key=".name"/>
        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
            <tags:input id="nameInput" path="name" size="30" maxlength="40"
                onkeyup="nameChanged()" onblur="nameChanged()"/>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".displayName"/>
        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
            <tags:input id="displayNameInput" path="displayName" size="30" maxlength="100"/>
            <c:set var="selcted" value=""/>
            <c:if test="${applianceCategory.name == applianceCategory.displayName}">
                <c:set var="checked" value=" checked=\"true\""/>
            </c:if>
            <input id="sameAsName" type="checkbox"${checked}
                onclick="sameAsNameClicked()"/>
            <label for="sameAsName"><i:inline key=".sameAsName"/></label>
        </tags:nameValue>
    </cti:displayForPageEditModes>

    <cti:msg2 var="fieldName" key=".type"/>
    <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <form:select path="applianceType">
                <c:forEach var="applianceType" items="${applianceTypes}">
                    <cti:msg var="optionLabel" key="${applianceType}"/>
                    <form:option value="${applianceType}" label="${optionLabel}"/>
                </c:forEach>
            </form:select>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
            <i:inline key="${applianceCategory.applianceType.formatKey}"/>
        </cti:displayForPageEditModes>
    </tags:nameValue>

    <cti:msg2 var="fieldName" key=".icon"/>
    <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <dr:iconChooser id="iconChooser" path="icon" icons="${icons}"
                selectedIcon="${applianceCategory.iconEnum}" applianceCategoryIconMode="true"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
            <c:if test="${!empty applianceCategory.icon}">
                <img src="<cti:url value="/WebConfig/${applianceCategory.icon}"/>"/>
            </c:if>
        </cti:displayForPageEditModes>
    </tags:nameValue>

    <cti:msg2 var="fieldName" key=".description"/>
    <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <tags:textarea path="description" cols="40" rows="5"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
            <spring:escapeBody>${applianceCategory.description}</spring:escapeBody>
            <c:if test="${empty applianceCategory.description}">
                <cti:msg2 key=".noDescription"/>
            </c:if>
        </cti:displayForPageEditModes>
    </tags:nameValue>

    <c:set var="fieldName">
        <label for="consumerSelectableCheckbox"><i:inline key=".consumerSelectable"/></label>
    </c:set>
    <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <form:checkbox id="consumerSelectableCheckbox" path="consumerSelectable"/>
            <label for="consumerSelectableCheckbox"><i:inline key=".consumerSelectableDescription"/></label>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="VIEW">
            <c:if test="${applianceCategory.consumerSelectable}">
                <i:inline key=".isConsumerSelectable"/>
            </c:if>
            <c:if test="${!applianceCategory.consumerSelectable}">
                <i:inline key=".isNotConsumerSelectable"/>
            </c:if>
        </cti:displayForPageEditModes>
    </tags:nameValue>
</tags:nameValueContainer>

<cti:displayForPageEditModes modes="VIEW">
    <cti:msg2 var="boxTitle" key=".assignedPrograms"/>
    <tags:pagedBox id="applianceCategoryPrograms" title="${boxTitle}"
        searchResult="${assignedPrograms}" baseUrl="${baseUrl}" filterDialog="filterDialog"
        isFiltered="${isFiltered}" showAllUrl="${clearFilterUrl}">
        <c:if test="${empty assignedPrograms.resultList}">
            <i:inline key=".noAssignedPrograms"/>
        </c:if>
        <c:if test="${!empty assignedPrograms.resultList}">
        <table id="programList" class="compactResultsTable rowHighlighting">
            <tr>
                <th>
                    <tags:sortLink nameKey="programName" baseUrl="${baseUrl}"
                        fieldName="PROGRAM_NAME" isDefault="${!applianceCategory.consumerSelectable}"/>
                </th>
                <th>
                    <i:inline key=".actions"/>
                </th>
                <c:if test="${applianceCategory.consumerSelectable}">
                    <th>
                        <tags:sortLink nameKey="displayOrder" baseUrl="${baseUrl}"
                            fieldName="PROGRAM_ORDER" isDefault="true"/>
                    </th>
                </c:if>
            </tr>
            <c:forEach var="assignedProgram" items="${assignedPrograms.resultList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <dr:assignedProgramName assignedProgram="${assignedProgram}"/>
                    </td>
                    <td>
                        <cti:url var="editProgramUrl" value="editAssignedProgram">
                            <cti:param name="ecId" value="${ecId}"/>
                            <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                            <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="acDialog"
                            nameKey="editAssignedProgram" skipLabel="true" 
                            actionUrl="${editProgramUrl}"/>

                        <c:if test="${isEditable}">
                            <cti:url var="unassignProgramUrl" value="confirmUnassignProgram">
                                <cti:param name="ecId" value="${ecId}"/>
                                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                                <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                            </cti:url>
                            <tags:simpleDialogLink2 dialogId="acDialog"
                                nameKey="unassignProgram" skipLabel="true"
                                actionUrl="${unassignProgramUrl}"/>
                        </c:if>
                    </td>
                    <c:if test="${applianceCategory.consumerSelectable}">
                    <td>
                        <c:if test="${isEditable}">
                            <c:if test="${assignedProgram.first}">
                                <cti:img nameKey="up.disabled"/>
                            </c:if>
                            <c:if test="${!assignedProgram.first}">
                                <cti:url var="moveProgramUpUrl" value="moveProgram">
                                    <cti:param name="direction" value="up"/>
                                    <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                                    <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                                </cti:url>
                                <cti:img nameKey="up" href="javascript:simpleAJAXRequest('${moveProgramUpUrl}')"/>
                            </c:if>
                            <c:if test="${assignedProgram.last}">
                                <cti:img nameKey="down.disabled"/>
                            </c:if>
                            <c:if test="${!assignedProgram.last}">
                                <cti:url var="moveProgramDownUrl" value="moveProgram">
                                    <cti:param name="direction" value="down"/>
                                    <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                                    <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
                                </cti:url>
                                <cti:img nameKey="down" href="javascript:simpleAJAXRequest('${moveProgramDownUrl}')"/>
                            </c:if>
                        </c:if>
                        ${assignedProgram.programOrder}
                    </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
        </c:if>
        <c:if test="${isEditable}">
            <div class="actionArea">
                <cti:button nameKey="assignPrograms" onclick="programPicker.show()"/>

                <c:if test="${canAddVirtual}">
                    <cti:url var="createVirtualProgramUrl" value="createVirtualProgram">
                        <cti:param name="ecId" value="${param.ecId}"/>
                        <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                    </cti:url>
                    <tags:dialogButton dialogId="acDialog" nameKey="createVirtualProgram"
                        actionUrl="${createVirtualProgramUrl}"/>
                </c:if>
            </div>
        </c:if>
    </tags:pagedBox>
</cti:displayForPageEditModes>

<div class="pageActionArea">
    <c:set var="ecId" value="${applianceCategory.energyCompanyId}"/>
    <cti:displayForPageEditModes modes="VIEW">
        <c:if test="${isEditable}">
            <cti:url var="editUrl" value="edit">
                <cti:param name="ecId" value="${ecId}"/>
                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
            </cti:url>
            <cti:button nameKey="edit" href="${editUrl}"/>
        </c:if>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="CREATE,EDIT">
        <cti:button nameKey="save" name="save" type="submit"/>
        <c:set var="isCreate" value="${empty applianceCategoryId || applianceCategoryId == 0}"/>
        <c:if test="${isCreate}">
            <cti:url var="backUrl" value="list">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
        </c:if>
        <c:if test="${!isCreate}">
            <cti:button id="deleteButton" nameKey="delete"/>
            <tags:confirmDialog nameKey=".deleteConfirmation" argument="${applianceCategory.name}"
                on="#deleteButton" submitName="delete"/>
            <cti:url var="backUrl" value="view">
                <cti:param name="ecId" value="${ecId}"/>
                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
            </cti:url>
        </c:if>
        <cti:button nameKey="cancel" name="cancel" href="${backUrl}"/>
    </cti:displayForPageEditModes>
</div>

</form:form>

</cti:standardPage>

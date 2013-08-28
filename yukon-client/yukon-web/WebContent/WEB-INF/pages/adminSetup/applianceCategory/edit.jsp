<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="applianceCategory.${mode}">

<cti:includeScript link="/JavaScript/iconChooser.js"/>
<cti:includeScript link="/JavaScript/picker.js"/>
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
    if (document.getElementById('sameAsName').checked) {
        lastDisplayName = document.getElementById('displayNameInput').value;
        document.getElementById('displayNameInput').value = document.getElementById('nameInput').value;
        document.getElementById('displayNameInput').disabled = "disabled";
    } else {
        if (lastDisplayName) {
            document.getElementById('displayNameInput').value = lastDisplayName;
        }
        document.getElementById('displayNameInput').removeAttribute("disabled");
    }
};

nameChanged = function() {
    if (document.getElementById('sameAsName').checked) {
        document.getElementById('displayNameInput').value = document.getElementById('nameInput').value;
    }
};

submitForm = function() {
    nameChanged();
    document.getElementById('displayNameInput').removeAttribute("disabled");
    return true;
};

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
<script type="text/javascript">
var assignProgramsDialogTitle = '<cti:msg2 key=".assignProgramsDialogTitle" javaScriptEscape="true"/>';
function assignPrograms(devices) {
    submitFormViaAjax('acDialog', 'assignProgramForm', null, assignProgramsDialogTitle);
    return true;
}
</script>
<form id="assignProgramForm" action="assignProgram">
    <input type="hidden" name="applianceCategoryId" value="${applianceCategoryId}"/>
    <input type="hidden" name="ecId" value="${param.ecId}"/>

    <tags:pickerDialog destinationFieldName="programsToAssign" 
        endAction="assignPrograms"
        id="programPicker"
        linkType="none" 
        memoryGroup="programPicker"
        multiSelectMode="true" 
        type="applianceCategoryProgramPicker"/>
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
                <cti:button type="submit" nameKey="filterButton" classes="primary action"/>
                <cti:button nameKey="showAllButton" onclick="clearFilter();"/>
            </div>
        </form:form>
    </i:simplePopup>
</cti:displayForPageEditModes>

<form:form id="appCatForm" commandName="applianceCategory" action="save" onsubmit="return submitForm()">

<form:hidden path="energyCompanyId"/>
<form:hidden path="applianceCategoryId"/>
<form:hidden path="webConfiguration.configurationId"/>
<tags:nameValueContainer2>
    <cti:displayForPageEditModes modes="CREATE,EDIT">
        <tags:nameValue2 nameKey=".name" >
            <tags:input id="nameInput" path="name" size="30" maxlength="40"
                onkeyup="nameChanged()" onblur="nameChanged()"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".displayName" >
            <tags:input id="displayNameInput" path="displayName" size="30" maxlength="100"/>
            <c:set var="selcted" value=""/>
            <c:if test="${applianceCategory.name == applianceCategory.displayName}">
                <c:set var="checked" value=" checked=\"true\""/>
            </c:if>
            <input id="sameAsName" type="checkbox"${checked}
                onclick="sameAsNameClicked()"/>
            <label for="sameAsName"><i:inline key=".sameAsName"/></label>
        </tags:nameValue2>
    </cti:displayForPageEditModes>

    <tags:nameValue2 nameKey=".type" >
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
    </tags:nameValue2>
    
    <cti:checkRolesAndProperties value="!ENABLE_ESTIMATED_LOAD">
        <tags:hidden path="applianceLoad"/>
    </cti:checkRolesAndProperties>

	<cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
	    <tags:nameValue2 nameKey=".applianceLoad">
	        <tags:input path="applianceLoad"/>
	    </tags:nameValue2>
	</cti:checkRolesAndProperties>

    <tags:nameValue2 nameKey=".icon" >
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <dr:iconChooser id="iconChooser" path="icon" icons="${icons}"
                selectedIcon="${applianceCategory.iconEnum}" applianceCategoryIconMode="true"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
            <c:if test="${!empty applianceCategory.icon}">
                <img src="<cti:url value="/WebConfig/${applianceCategory.icon}"/>"/>
            </c:if>
        </cti:displayForPageEditModes>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".description" >
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <tags:textarea path="description" cols="40" rows="5"/>
        </cti:displayForPageEditModes>

        <cti:displayForPageEditModes modes="VIEW">
            ${fn:escapeXml(applianceCategory.description)}
            <c:if test="${empty applianceCategory.description}">
                <cti:msg2 key=".noDescription"/>
            </c:if>
        </cti:displayForPageEditModes>
    </tags:nameValue2>

    <tags:nameValue2 nameKey=".consumerSelectable" >
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
    </tags:nameValue2>
</tags:nameValueContainer2>

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
            <thead>
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
            </thead>
            <tfoot></tfoot>
            <tbody>
	            <c:forEach var="assignedProgram" items="${assignedPrograms.resultList}">
	                <tr>
	                    <td>
	                        <dr:assignedProgramName assignedProgram="${assignedProgram}"/>
	                    </td>
	                    <td>
	                        <cti:url var="editProgramUrl" value="editAssignedProgram">
	                            <cti:param name="ecId" value="${ecId}"/>
	                            <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
	                            <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
	                        </cti:url>
	                        <tags:simpleDialogLink2 dialogId="acDialog" icon="icon-pencil"
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
	                                actionUrl="${unassignProgramUrl}" icon="icon-cross"/>
	                        </c:if>
	                    </td>
	                    <c:if test="${applianceCategory.consumerSelectable}">
	                    <td>
	                        <c:if test="${isEditable}">
	                            <c:if test="${assignedProgram.first}">
	                                <cti:button renderMode="image" nameKey="up.disabled" disabled="true" icon="icon-bullet-go-up"/>
	                            </c:if>
	                            <c:if test="${!assignedProgram.first}">
	                                <cti:url var="moveProgramUpUrl" value="moveProgram">
	                                    <cti:param name="direction" value="up"/>
	                                    <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
	                                    <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
	                                </cti:url>
	                                <cti:button renderMode="image" nameKey="up" href="javascript:simpleAJAXRequest('${moveProgramUpUrl}')" icon="icon-bullet-go-up"/>
	                            </c:if>
	                            <c:if test="${assignedProgram.last}">
	                                <cti:button renderMode="image" nameKey="down.disabled" disabled="true" icon="icon-bullet-go-down"/>
	                            </c:if>
	                            <c:if test="${!assignedProgram.last}">
	                                <cti:url var="moveProgramDownUrl" value="moveProgram">
	                                    <cti:param name="direction" value="down"/>
	                                    <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
	                                    <cti:param name="assignedProgramId" value="${assignedProgram.assignedProgramId}"/>
	                                </cti:url>
	                                <cti:button nameKey="down" renderMode="image" icon="icon-bullet-go-down" href="javascript:simpleAJAXRequest('${moveProgramDownUrl}')"/>
	                            </c:if>
	                        </c:if>
	                        ${assignedProgram.programOrder}
	                    </td>
	                    </c:if>
	                </tr>
	            </c:forEach>
            </tbody>
        </table>
        </c:if>
        <c:if test="${isEditable}">
            <div class="actionArea">
                <cti:button nameKey="assignPrograms" onclick="programPicker.show.call(programPicker)" icon="icon-add"/>

                <c:if test="${canAddVirtual}">
                    <cti:url var="createVirtualProgramUrl" value="createVirtualProgram">
                        <cti:param name="ecId" value="${param.ecId}"/>
                        <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                    </cti:url>
                    <tags:dialogButton dialogId="acDialog" nameKey="createVirtualProgram"
                        actionUrl="${createVirtualProgramUrl}" icon="icon-plus-green"/>
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
            <cti:button nameKey="edit" icon="icon-pencil" href="${editUrl}"/>
        </c:if>
    </cti:displayForPageEditModes>

    <cti:displayForPageEditModes modes="CREATE,EDIT">
        <cti:button nameKey="save" name="save" type="submit" class="primary action"/>
        <c:set var="isCreate" value="${empty applianceCategoryId || applianceCategoryId == 0}"/>
        <c:if test="${isCreate}">
            <cti:url var="backUrl" value="list">
                <cti:param name="ecId" value="${ecId}"/>
            </cti:url>
        </c:if>
        <c:if test="${!isCreate}">
            <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${applianceCategory.name}"/>
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

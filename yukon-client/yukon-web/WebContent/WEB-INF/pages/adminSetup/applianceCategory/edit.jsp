<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>

<cti:standardPage module="adminSetup" page="applianceCategory.${mode}">

<tags:setFormEditMode mode="${mode}"/>
<c:set var="applianceCategoryId" value="${applianceCategory.applianceCategoryId}"/>

<cti:includeScript link="/resources/js/pages/yukon.dr.icon.chooser.js"/>
<cti:includeScript link="/resources/js/pages/yukon.admin.ec.appliance.categories.js"/>

<div id="program-edit" title="<cti:msg2 key=".editAssignedProgram.title"/>" class="dn"></div>
<div id="program-unassign" title="<cti:msg2 key=".unassignProgram.title"/>" class="dn"></div>


<form:form id="category-form" modelAttribute="applianceCategory" action="save" data-mode="${mode}">

    <cti:csrfToken/>
    <form:hidden path="energyCompanyId"/>
    <form:hidden path="applianceCategoryId"/>
    <form:hidden path="webConfiguration.configurationId"/>
    
    <tags:nameValueContainer2 tableClass="stacked">
    
        <cti:displayForPageEditModes modes="CREATE,EDIT">
        
            <tags:nameValue2 nameKey=".name" >
                <tags:input id="category-name" path="name" size="30" maxlength="40"/>
            </tags:nameValue2>
    
            <tags:nameValue2 nameKey=".displayName" rowId="display-name-row">
                <c:if test="${applianceCategory.name == applianceCategory.displayName}">
                    <c:set var="sameAsChecked" value=" checked"/>
                    <c:set var="displayNameDisabled" value="true"/>
                </c:if>
                <tags:input id="category-display-name" path="displayName" size="30" maxlength="100" disabled="${displayNameDisabled}"/>
                <label><input type="checkbox"${sameAsChecked}><i:inline key=".sameAsName"/></label>
            </tags:nameValue2>
            
        </cti:displayForPageEditModes>
    
        <tags:nameValue2 nameKey=".type">
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
            <tags:nameValue2 nameKey=".applianceLoad" nameClass="wsnw">
                <tags:input path="applianceLoad"/>
            </tags:nameValue2>
        </cti:checkRolesAndProperties>
    
        <tags:nameValue2 nameKey=".icon" >
            <cti:displayForPageEditModes modes="CREATE,EDIT">
                <dr:iconChooser id="iconChooser" 
                    path="icon" 
                    icons="${icons}"
                    selectedIcon="${applianceCategory.iconEnum}" 
                    applianceCategoryIconMode="true"/>
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
                <c:choose>
                    <c:when test="${empty applianceCategory.description}"><cti:msg2 key=".noDescription"/></c:when>
                    <c:otherwise>${fn:escapeXml(applianceCategory.description)}</c:otherwise>
                </c:choose>
            </cti:displayForPageEditModes>
        </tags:nameValue2>
    
        <tags:nameValue2 nameKey=".consumerSelectable" nameClass="wsnw">
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
    
    <div class="page-action-area stacked">
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
            <cti:button nameKey="save" name="save" type="submit" classes="primary action" busy="true"/>
            <c:set var="isCreate" value="${empty applianceCategoryId || applianceCategoryId == 0}"/>
            <c:if test="${isCreate}">
                <cti:url var="backUrl" value="list">
                    <cti:param name="ecId" value="${ecId}"/>
                </cti:url>
            </c:if>
            <c:if test="${!isCreate}">
                <cti:button id="deleteButton" nameKey="delete" name="delete" type="submit" classes="delete"/>
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
    
    <cti:displayForPageEditModes modes="VIEW">
        
        <tags:sectionContainer2 nameKey="assignedPrograms" controls="${filter}">
            <cti:url value="programs-list" var="programsUrl">
                <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                <cti:param name="ecId" value="${ecId}"/>
                <cti:param name="filterBy" value="${filterBy}"/>
            </cti:url>
            <div id="assigned-programs" class="scroll-md"
                     data-url="${programsUrl}" data-category="${applianceCategoryId}" data-ec="${ecId}">
                <c:choose>
                    <c:when test="${empty assignedPrograms.resultList}"><i:inline key=".noAssignedPrograms"/></c:when>
                    <c:otherwise><%@ include file="ac.programs.list.jsp" %></c:otherwise>
                </c:choose>
            </div>
            
            <div class="action-area">
                <c:if test="${isEditable}">
                    <form id="assign-program-form" action="assignProgram">
                        <input type="hidden" name="ecId" value="${ecId}">
                        <input type="hidden" name="applianceCategoryId" value="${applianceCategoryId}">
                        <tags:pickerDialog destinationFieldName="programsToAssign"
                            nameKey="assignPrograms" 
                            endAction="yukon.admin.ec.ac.addProgramsPopup"
                            id="programPicker"
                            linkType="button"
                            icon="icon-add" 
                            memoryGroup="programPicker"
                            multiSelectMode="true" 
                            type="applianceCategoryProgramPicker"/>
                    </form>
                    
                    <c:if test="${canAddVirtual}">
                        <cti:url var="createVirtualProgramUrl" value="createVirtualProgram">
                            <cti:param name="ecId" value="${param.ecId}"/>
                            <cti:param name="applianceCategoryId" value="${applianceCategoryId}"/>
                        </cti:url>
                        <cti:button id="create-virtual-program-btn" 
                                nameKey="createVirtualProgram" 
                                icon="icon-plus-green" 
                                data-url="${createVirtualProgramUrl}"/>
                    </c:if>
                </c:if>
                <c:if test="${!applianceCategory.consumerSelectable}">
                    <c:set var ="filtered" value="${empty filterBy ? false : true}"/>
                    <div class="button-group fr">
                        <cti:button nameKey="filter" icon="icon-filter" data-popup=".js-filter-popup" 
                            classes="js-filter fl ${filtered ? 'left' : ''}"/>
                        <cti:button renderMode="buttonImage" classes="js-clear-filter fr right ${filtered ? '' : 'dn'}" icon="icon-cross"/>
                    </div>
                    <div class="dn js-filter-popup" data-dialog data-title="<cti:msg2 key=".assignedPrograms.filterBy.title"/>" 
                            data-event="yukon.admin.ec.ac.program.filter">
                        <label>
                            <i:inline key=".assignedPrograms.filterBy"/>&nbsp;
                            <input id="program-filter" type="text" value="${filterBy}">
                        </label>
                    </div>
                    
                </c:if>
            </div>
        </tags:sectionContainer2>
        
    </cti:displayForPageEditModes>
    
</cti:standardPage>

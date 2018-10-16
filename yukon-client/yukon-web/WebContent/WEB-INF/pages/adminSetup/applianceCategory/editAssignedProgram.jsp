<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.adminSetup.applianceCategory.editAssignedProgram">

<tags:setFormEditMode mode="${mode}"/>

<cti:flashScopeMessages/>

<c:if test="${backingBean.multiple}">
<i:inline key=".editingMultiple"/>
</c:if>

<form:form id="program-form" 
    modelAttribute="backingBean" 
    action="saveAssignedProgram"
    data-multiple="${backingBean.multiple}"
    data-virtual="${backingBean.virtual}">
    
    <cti:csrfToken/>
    <input type="hidden" name="ecId" value="${param.ecId}"/>
    <form:hidden path="virtual"/>
    <form:hidden path="multiple"/>
    
    <c:if test="${backingBean.multiple}">
        <c:forEach var="programId" items="${backingBean.programIds}">
            <input type="hidden" name="programIds" value="${programId}">
        </c:forEach>
    </c:if>
    
    <form:hidden path="assignedProgram.applianceCategoryId"/>
    <form:hidden path="assignedProgram.assignedProgramId"/>
    <form:hidden path="assignedProgram.programId"/>
    <form:hidden id="programNameInput" path="assignedProgram.programName"/>
    <form:hidden path="assignedProgram.programOrder"/>
    
    <tags:nameValueContainer2 tableClass="with-form-controls">
        
        <tags:nameValue2 nameKey=".applianceCategory">${fn:escapeXml(applianceCategory.name)}</tags:nameValue2>
        
        <c:if test="${!backingBean.multiple}">
            
            <tags:nameValue2 nameKey=".programName" rowId="program-name">
                <c:if test="${!backingBean.virtual}">${fn:escapeXml(backingBean.assignedProgram.programName)}</c:if>
                <c:if test="${backingBean.virtual}"><i:inline key=".isVirtual"/></c:if>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".displayName" rowId="program-display-name">
                <spring:bind path="assignedProgram.displayName">
                    <cti:displayForPageEditModes modes="VIEW">${fn:escapeXml(status.value)}</cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <form:input path="assignedProgram.displayName" size="30" cssClass="${status.error ? 'error' : ''}"/>
                        <c:if test="${!backingBean.virtual}">
                            <c:if test="${backingBean.assignedProgram.displayName == backingBean.assignedProgram.programName}">
                                <c:set var="checked" value=" checked"/>
                            </c:if>
                            <label><input type="checkbox"${checked}><i:inline key=".sameAsProgramName"/></label>
                        </c:if>
                        <c:if test="${status.error}"><br><form:errors path="assignedProgram.displayName" cssClass="error"/></c:if>
                    </cti:displayForPageEditModes>
                </spring:bind>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".shortName" rowId="program-short-name">
                <spring:bind path="assignedProgram.shortName">
                    <cti:displayForPageEditModes modes="VIEW">${fn:escapeXml(status.value)}</cti:displayForPageEditModes>
                    <cti:displayForPageEditModes modes="EDIT,CREATE">
                        <form:input path="assignedProgram.shortName" size="30" cssClass="${status.error ? 'error' : ''}"/>
                        <c:if test="${backingBean.assignedProgram.shortName == backingBean.assignedProgram.displayName}">
                            <c:set var="checked" value=" checked"/>
                        </c:if>
                        <label><input type="checkbox"${checked}><i:inline key=".sameAsDisplayName"/></label>
                        <c:if test="${status.error}"><br><form:errors path="assignedProgram.shortName" cssClass="error"/></c:if>
                    </cti:displayForPageEditModes>
                </spring:bind>
            </tags:nameValue2>
            
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <tags:nameValue2 nameKey=".displayNameKey" rowId="program-display-name-key">
                </tags:nameValue2>
            </cti:displayForPageEditModes>
        </c:if>
        
        <tags:nameValue2 nameKey=".description">
            <tags:textarea path="assignedProgram.description" cols="40" rows="3"/>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".chanceOfControl">
            <cti:displayForPageEditModes modes="VIEW">${fn:escapeXml(chanceOfControl)}</cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <form:select path="assignedProgram.chanceOfControlId" 
                    items="${chanceOfControls}"
                    itemValue="chanceOfControlId" 
                    itemLabel="name"/>
            </cti:displayForPageEditModes>
        </tags:nameValue2>
        
        <tr><td colspan="2"><h3><i:inline key=".programDescriptionIcons"/></h3></td></tr>
        
        <tags:nameValue2 nameKey=".savings">
            <dr:iconChooser id="savings" path="assignedProgram.savingsIcon"
                icons="${savingsIcons}" value="${backingBean.assignedProgram.savingsIcon}"
                selectedIcon="${backingBean.assignedProgram.savingsIconEnum}"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".controlPercent">
            <dr:iconChooser id="controlPercent" path="assignedProgram.controlPercentIcon"
                icons="${controlPercentIcons}" value="${backingBean.assignedProgram.controlPercentIcon}"
                selectedIcon="${backingBean.assignedProgram.controlPercentIconEnum}"/>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".environment">
            <dr:iconChooser id="environment" path="assignedProgram.environmentIcon"
                icons="${environmentIcons}" value="${backingBean.assignedProgram.environmentIcon}"
                selectedIcon="${backingBean.assignedProgram.environmentIconEnum}"/>
        </tags:nameValue2>
        
        <c:if test="${showAlternateEnrollment}">
            <tags:nameValue2 nameKey=".alternateEnrollment">
                <form:hidden path="assignedProgram.alternateProgramId" id="alternateProgramId"/>
                <tags:pickerDialog type="assignedProgramPicker"
                    extraArgs="${energycompanyId}"
                    id="assignedProgramPicker" 
                    linkType="selection"
                    selectionProperty="displayName"
                    multiSelectMode="false"
                    memoryGroup="programPicker"
                    destinationFieldId="alternateProgramId"
                    excludeIds="${excludedProgramIds}"
                    allowEmptySelection="true"
                    initialId="${backingBean.assignedProgram.alternateProgramId}"
                    viewOnlyMode="${!isEditable}"/>
            </tags:nameValue2>
        </c:if>
    </tags:nameValueContainer2>
    
</form:form>

</cti:msgScope>
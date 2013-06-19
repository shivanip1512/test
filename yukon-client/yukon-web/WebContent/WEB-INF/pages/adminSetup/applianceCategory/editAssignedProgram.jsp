<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<tags:setFormEditMode mode="${mode}"/>
<cti:msgScope paths="modules.adminSetup.applianceCategory.editAssignedProgram">

<cti:displayForPageEditModes modes="EDIT,CREATE">
<script type="text/javascript">
<c:if test="${!backingBean.multiple}">
sameAsProgramNameClicked = function() {
    if (document.getElementById('sameAsProgramName').checked) {
        document.getElementById('displayNameInput').value = document.getElementById('programNameInput').value;
        document.getElementById('displayNameInput').disabled = "disabled";
    } else {
        document.getElementById('displayNameInput').removeAttribute("disabled"); 
    }
    sameAsDisplayNameClicked();
    updateDisplayNameKey();
}

sameAsDisplayNameClicked = function() {
    if (document.getElementById('sameAsDisplayName').checked) {
        document.getElementById('shortNameInput').value = document.getElementById('displayNameInput').value;
        document.getElementById('shortNameInput').disabled = "disabled";
    } else {
        document.getElementById('shortNameInput').removeAttribute("disabled"); 
    }
}

displayNameChanged = function() {
    if (document.getElementById('sameAsDisplayName').checked) {
        document.getElementById('shortNameInput').value = document.getElementById('displayNameInput').value;
    }
    updateDisplayNameKey();
}

updateDisplayNameKey = function() {
    // This prefix must match com.cannontech.stars.dr.program.model.Program.java
    var prefix = 'yukon.dr.program.displayname.'; 
    var displayNameKey = generateMessageCode(prefix, document.getElementById('displayNameInput').value);

    document.getElementById('displayNameKeyArea').innerHTML = displayNameKey;
}
</c:if>

submitForm = function() {
    <c:if test="${!backingBean.multiple}">
        document.getElementById('displayNameInput').removeAttribute("disabled");
        displayNameChanged();
        document.getElementById('shortNameInput').removeAttribute("disabled");
    </c:if>
    return submitFormViaAjax('acDialog', 'assignedProgramForm');
};

jQuery(function(){
    jQuery("#picker_assignedProgramPicker_label a").click(function(e) {
        jQuery("#acDialog").dialog("close");
        e.stopPropagation();
        return true;
    });
});

showProgramEditor = function() {
    jQuery("#acDialog").dialog("open");
};

jQuery(function(){
    jQuery("#sameAsProgramName").click(function(){sameAsProgramNameClicked();});
    jQuery("#sameAsDisplayName").click(function(){sameAsDisplayNameClicked();});
});
</script>
</cti:displayForPageEditModes>

<cti:flashScopeMessages/>

<c:if test="${backingBean.multiple}">
<i:inline key=".editingMultiple"/>
</c:if>

<form:form id="assignedProgramForm" commandName="backingBean" action="saveAssignedProgram" onsubmit="return submitForm()">
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
    
    <tags:nameValueContainer>
        <cti:msg2 var="fieldName" key=".applianceCategory"/>
        <tags:nameValue name="${fieldName}" nameColumnWidth="170px">
            <spring:escapeBody htmlEscape="true">${applianceCategory.name}</spring:escapeBody>
        </tags:nameValue>
        <c:if test="${!backingBean.multiple}">
            <cti:msg2 var="fieldName" key=".programName"/>
            <tags:nameValue name="${fieldName}">
                <c:if test="${!backingBean.virtual}">
                    <spring:escapeBody htmlEscape="true">${backingBean.assignedProgram.programName}</spring:escapeBody>
                </c:if>
                <c:if test="${backingBean.virtual}">
                    <i:inline key=".isVirtual"/>
                </c:if>
            </tags:nameValue>
        </c:if>

        <c:if test="${!backingBean.multiple}">
            <cti:msg2 var="fieldName" key=".displayName"/>
            <tags:nameValue name="${fieldName}">
                <tags:input id="displayNameInput"
                    path="assignedProgram.displayName" size="30"
                    onkeyup="displayNameChanged()" onblur="displayNameChanged()"/>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                <c:if test="${!backingBean.virtual}">
                    <c:if test="${backingBean.assignedProgram.displayName == backingBean.assignedProgram.programName}">
                        <c:set var="checked" value=" checked=\"true\""/>
                    </c:if>
                    <input id="sameAsProgramName" type="checkbox"${checked}/>
                    <label for="sameAsProgramName"><i:inline key=".sameAsProgramName"/></label>
                </c:if>
                </cti:displayForPageEditModes>
            </tags:nameValue>

            <cti:msg2 var="fieldName" key=".shortName"/>
            <tags:nameValue name="${fieldName}">
                <tags:input id="shortNameInput" path="assignedProgram.shortName" size="30"/>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                <c:if test="${backingBean.assignedProgram.shortName == backingBean.assignedProgram.displayName}">
                    <c:set var="checked" value=" checked=\"true\""/>
                </c:if>
                <input id="sameAsDisplayName" type="checkbox"${checked}/>
                <label for="sameAsDisplayName"><i:inline key=".sameAsDisplayName"/></label>
                </cti:displayForPageEditModes>
            </tags:nameValue>

            <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:msg2 var="fieldName" key=".displayNameKey"/>
            <tags:nameValue name="${fieldName}">
                <span id="displayNameKeyArea"></span>
            </tags:nameValue>
            </cti:displayForPageEditModes>
        </c:if>

        <cti:msg2 var="fieldName" key=".description"/>
        <tags:nameValue name="${fieldName}">
            <tags:textarea path="assignedProgram.description" cols="40" rows="3"/>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".chanceOfControl"/>
        <tags:nameValue name="${fieldName}">
            <cti:displayForPageEditModes modes="VIEW">
                <spring:escapeBody htmlEscape="true">${chanceOfControl}</spring:escapeBody>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <form:select path="assignedProgram.chanceOfControlId" items="${chanceOfControls}"
                    itemValue="chanceOfControlId" itemLabel="name"/>
            </cti:displayForPageEditModes>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".programDescriptionIcons"/>
        <tags:nameValue name="${fieldName}" isSection="true">
            <cti:msg2 var="nestedFieldName" key=".savings"/>
            <tags:nameValue name="${nestedFieldName}">
                <dr:iconChooser id="savings" path="assignedProgram.savingsIcon"
                    icons="${savingsIcons}" value="${backingBean.assignedProgram.savingsIcon}"
                    selectedIcon="${backingBean.assignedProgram.savingsIconEnum}"/>
            </tags:nameValue>
            <cti:msg2 var="nestedFieldName" key=".controlPercent"/>
            <tags:nameValue name="${nestedFieldName}">
                <dr:iconChooser id="controlPercent" path="assignedProgram.controlPercentIcon"
                    icons="${controlPercentIcons}" value="${backingBean.assignedProgram.controlPercentIcon}"
                    selectedIcon="${backingBean.assignedProgram.controlPercentIconEnum}"/>
            </tags:nameValue>
            <cti:msg2 var="nestedFieldName" key=".environment"/>
            <tags:nameValue name="${nestedFieldName}">
                <dr:iconChooser id="environment" path="assignedProgram.environmentIcon"
                    icons="${environmentIcons}" value="${backingBean.assignedProgram.environmentIcon}"
                    selectedIcon="${backingBean.assignedProgram.environmentIconEnum}"/>
            </tags:nameValue>
        </tags:nameValue>
        <c:if test="${showAlternateEnrollment}">
            <cti:msg2 var="fieldName" key=".alternateEnrollment"/>
    	 	<tags:nameValue name="${fieldName}">
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
     	 	        endAction="showProgramEditor"
     	 	        cancelAction="showProgramEditor" initialId="${backingBean.assignedProgram.alternateProgramId}"/>
     	 	</tags:nameValue>
        </c:if>
    </tags:nameValueContainer>
    <cti:displayForPageEditModes modes="EDIT,CREATE">
    <c:if test="${!backingBean.multiple}">
        <script type="text/javascript">
            <c:if test="${!backingBean.virtual}">
            sameAsProgramNameClicked();
            </c:if>
            <c:if test="${backingBean.virtual}">
            sameAsDisplayNameClicked();
            updateDisplayNameKey();
            </c:if>
        </script>
    </c:if>
    </cti:displayForPageEditModes>

    <div class="actionArea">
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <cti:button type="submit" nameKey="ok" classes="primary action"/>
        </cti:displayForPageEditModes>
        <cti:button nameKey="cancel" onclick="jQuery('#acDialog').dialog('close');"/>
    </div>

</form:form>

</cti:msgScope>
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
lastDisplayName = false;
sameAsProgramNameClicked = function() {
    if ($('sameAsProgramName').checked) {
        lastDisplayName = $('displayNameInput').value;
        $('displayNameInput').value = $('programNameInput').value;
        $('displayNameInput').disable();
    } else {
        if (lastDisplayName) {
            $('displayNameInput').value = lastDisplayName;
        }
        $('displayNameInput').enable(); 
    }
    sameAsDisplayNameClicked();
    updateDisplayNameKey();
}

lastShortName = false;
sameAsDisplayNameClicked = function() {
    if ($('sameAsDisplayName').checked) {
        lastShortName = $('shortNameInput').value;
        $('shortNameInput').value = $('displayNameInput').value;
        $('shortNameInput').disable();
    } else {
        if (lastShortName) {
            $('shortNameInput').value = lastShortName;
        }
        $('shortNameInput').enable(); 
    }
}

displayNameChanged = function() {
    if ($('sameAsDisplayName').checked) {
        $('shortNameInput').value = $('displayNameInput').value;
    }
    updateDisplayNameKey();
}

updateDisplayNameKey = function() {
    // This prefix must match com.cannontech.stars.dr.program.model.Program.java
    var prefix = 'yukon.dr.program.displayname.'; 
    var displayNameKey = generateMessageCode(prefix, $('displayNameInput').value);

    $('displayNameKeyArea').innerHTML = displayNameKey;
}
</c:if>


submitForm = function() {
    <c:if test="${!backingBean.multiple}">
        $('displayNameInput').enable();
        displayNameChanged();
        $('shortNameInput').enable();
    </c:if>
    return submitFormViaAjax('acDialog', 'inputForm')
}
</script>
</cti:displayForPageEditModes>

<cti:flashScopeMessages/>

<c:if test="${backingBean.multiple}">
<i:inline key=".editingMultiple"/>
</c:if>

<cti:url var="submitUrl" value="saveAssignedProgram"/>
<form:form id="inputForm" commandName="backingBean" action="${submitUrl}" onsubmit="return submitForm()">
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
                    <input id="sameAsProgramName" type="checkbox"${checked}
                        onclick="sameAsProgramNameClicked()"/>
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
                <input id="sameAsDisplayName" type="checkbox"${checked}
                    onclick="sameAsDisplayNameClicked()"/>
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
            <cti:button type="submit" key="ok"/>
        </cti:displayForPageEditModes>
        <cti:button key="cancel" onclick="$('acDialog').hide()"/>
    </div>

</form:form>

</cti:msgScope>

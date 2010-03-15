<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:msgScope paths="modules.energyCompanyAdmin.editAssignedProgram">

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
    // This prefix and pattern must match MessageCodeGenerator.java
    var prefix = 'yukon.dr.program.displayname.'; 
    var pattern = /[\.|\"|\s+|&|<]/g;
    var displayNameKey = prefix + $('displayNameInput').value.replace(pattern, '');

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

<c:if test="${backingBean.multiple}">
<i:inline key=".editingMultiple"/>
</c:if>

<cti:url var="submitUrl" value="/spring/stars/dr/admin/applianceCategory/saveAssignedProgram"/>
<form:form id="inputForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitForm()">
    <form:hidden path="virtual"/>
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
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".applianceCategory" nameColumnWidth="170px">
            <spring:escapeBody>${applianceCategory.name}</spring:escapeBody>
        </tags:nameValue2>
        <c:if test="${!backingBean.virtual && !backingBean.multiple}">
            <tags:nameValue2 nameKey=".programName">
                <spring:escapeBody>${backingBean.assignedProgram.programName}</spring:escapeBody>
            </tags:nameValue2>
        </c:if>

        <c:if test="${!backingBean.multiple}">
            <tags:nameValue2 nameKey=".displayName">
                <form:input id="displayNameInput"
                    path="assignedProgram.displayName" size="30"
                    onkeyup="displayNameChanged()" onblur="displayNameChanged()"/>
                <c:if test="${!backingBean.virtual}">
                    <c:if test="${backingBean.assignedProgram.displayName == backingBean.assignedProgram.programName}">
                        <c:set var="checked" value=" checked=\"true\""/>
                    </c:if>
                    <input id="sameAsProgramName" type="checkbox"${checked}
                        onclick="sameAsProgramNameClicked()"/>
                    <label for="sameAsProgramName"><i:inline key=".sameAsProgramName"/></label>
                </c:if>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".shortName">
                <form:input id="shortNameInput" path="assignedProgram.shortName" size="30"/>
                <c:if test="${backingBean.assignedProgram.shortName == backingBean.assignedProgram.displayName}">
                    <c:set var="checked" value=" checked=\"true\""/>
                </c:if>
                <input id="sameAsDisplayName" type="checkbox"${checked}
                    onclick="sameAsDisplayNameClicked()"/>
                <label for="sameAsDisplayName"><i:inline key=".sameAsDisplayName"/></label>
            </tags:nameValue2>

            <tags:nameValue2 nameKey=".displayNameKey">
                <span id="displayNameKeyArea"></span>
            </tags:nameValue2>
        </c:if>

        <tags:nameValue2 nameKey=".description">
            <form:textarea path="assignedProgram.description" cols="40" rows="5"/>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".chanceOfControl">
            <form:select path="assignedProgram.chanceOfControlId" items="${chanceOfControls}"
                itemValue="chanceOfControlId" itemLabel="name">
            </form:select>
        </tags:nameValue2>

        <tags:nameValue2 nameKey=".programDescriptionIcons" isSection="true">
            <tags:nameValue2 nameKey=".savings">
                <dr:iconChooser id="savings" path="assignedProgram.savingsIcon"
                    icons="${savingsIcons}"
                    selectedIcon="${backingBean.assignedProgram.savingsIconEnum}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".controlPercent">
                <dr:iconChooser id="controlPercent" path="assignedProgram.controlPercentIcon"
                    icons="${controlPercentIcons}"
                    selectedIcon="${backingBean.assignedProgram.controlPercentIconEnum}"/>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".environment">
                <dr:iconChooser id="environment" path="assignedProgram.environmentIcon"
                    icons="${environmentIcons}"
                    selectedIcon="${backingBean.assignedProgram.environmentIconEnum}"/>
            </tags:nameValue2>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <c:if test="${!backingBean.multiple}">
        <script type="text/javascript">
            <c:if test="${!backingBean.virtual}">
            sameAsProgramNameClicked();
            </c:if>
            <c:if test="${backingBean.virtual}">
            sameAsDisplayNameClicked();
            </c:if>
        </script>
    </c:if>

    <div class="actionArea">
        <input type="submit" value="<cti:msg2 key=".ok"/>"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="parent.$('acDialog').hide()"/>
    </div>

</form:form>

</cti:msgScope>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>


<cti:msgScope paths="modules.energyCompanyAdmin.editApplianceCategory">

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
	return submitFormViaAjax('acDialog', 'inputForm')
}
</script>

<cti:flashScopeMessages/>

<cti:url var="submitUrl" value="/spring/stars/dr/admin/applianceCategory/saveDetails"/>
<form:form id="inputForm" commandName="applianceCategory" action="${submitUrl}"
    onsubmit="return submitForm()">
    <form:hidden path="applianceCategoryId"/>
    <form:hidden path="webConfiguration.configurationId"/>
    <tags:nameValueContainer>
        <cti:msg2 var="fieldName" key=".name"/>
        <tags:nameValue name="${fieldName}" nameColumnWidth="150px">
            <tags:input id="nameInput" path="name" size="30" maxlength="40"
                onkeyup="nameChanged()" onblur="nameChanged()"/>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".displayName"/>
        <tags:nameValue name="${fieldName}">
            <tags:input id="displayNameInput" path="displayName" size="30" maxlength="100"/>
            <c:set var="selcted" value=""/>
            <c:if test="${applianceCategory.name == applianceCategory.displayName}">
                <c:set var="checked" value=" checked=\"true\""/>
            </c:if>
            <input id="sameAsName" type="checkbox"${checked}
                onclick="sameAsNameClicked()"/>
            <label for="sameAsName"><i:inline key=".sameAsName"/></label>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".type"/>
        <tags:nameValue name="${fieldName}">
            <form:select path="applianceType">
                <c:forEach var="applianceType" items="${applianceTypes}">
                    <cti:msg var="optionLabel" key="${applianceType}"/>
                    <form:option value="${applianceType}" label="${optionLabel}"/>
                </c:forEach>
            </form:select>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".icon"/>
        <tags:nameValue name="${fieldName}">
            <dr:iconChooser id="iconChooser" path="icon" icons="${icons}"
                selectedIcon="${applianceCategory.iconEnum}" applianceCategoryIconMode="true"/>
        </tags:nameValue>

        <cti:msg2 var="fieldName" key=".description"/>
        <tags:nameValue name="${fieldName}">
            <tags:textarea path="description" cols="40" rows="5"/>
        </tags:nameValue>

        <c:set var="fieldName">
            <label for="consumerSelectableCheckbox"><i:inline key=".consumerSelectable"/></label>
        </c:set>
        <tags:nameValue name="${fieldName}">
            <form:checkbox id="consumerSelectableCheckbox" path="consumerSelectable"/>
            <label for="consumerSelectableCheckbox"><i:inline key=".consumerSelectableDescription"/></label>
        </tags:nameValue>

    </tags:nameValueContainer>
    <script type="text/javascript">
        sameAsNameClicked();
    </script>

    <c:set var="oncloseJavascript" value="parent.$('acDialog').hide()"/>
    <c:if test="${applianceCategory.applianceCategoryId == 0}">
        <c:set var="oncloseJavascript"
            value="window.location='/operator/Admin/ConfigEnergyCompany.jsp'"/>
    </c:if>
    <div class="actionArea">
        <input type="submit" value="<cti:msg2 key=".ok"/>"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="${oncloseJavascript}"/>
    </div>

</form:form>

</cti:msgScope>

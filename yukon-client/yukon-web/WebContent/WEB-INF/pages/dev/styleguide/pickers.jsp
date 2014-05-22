<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="pickers">
<tags:styleguide page="pickers">

<cti:msgScope paths="modules.dev.pickers">

<c:set var="pickerType" value="pointPicker"/>
<c:if test="${!empty param.pickerType}">
    <c:set var="pickerType" value="${param.pickerType}"/>
</c:if>

<c:choose>
    <c:when test="${pickerType == 'pointPicker'}">
        <c:set var="itemIdProperty" value="pointId"/>
        <c:set var="itemNameProperty" value="pointName"/>
    </c:when>
    <c:when test="${pickerType == 'paoPicker'}">
        <c:set var="itemIdProperty" value="paoId"/>
        <c:set var="itemNameProperty" value="paoName"/>
    </c:when>
    <c:when test="${pickerType == 'assignedProgramPicker'}">
        <c:set var="itemIdProperty" value="assignedProgramId"/>
        <c:set var="itemNameProperty" value="displayName"/>
    </c:when>
    <c:when test="${pickerType == 'customerAccountPicker'}">
        <c:set var="itemIdProperty" value="accountId"/>
        <c:set var="itemNameProperty" value="accountNumber"/>
    </c:when>
    <c:when test="${pickerType == 'userPicker'}">
        <c:set var="itemIdProperty" value="userId"/>
        <c:set var="itemNameProperty" value="userName"/>
    </c:when>
    <c:when test="${pickerType == 'userGroupPicker'}">
        <c:set var="itemIdProperty" value="userGroupId"/>
        <c:set var="itemNameProperty" value="userGroupName"/>
    </c:when>
    <c:when test="${pickerType == 'loginGroupPicker'}">
        <c:set var="itemIdProperty" value="groupId"/>
        <c:set var="itemNameProperty" value="groupName"/>
    </c:when>
    <c:when test="${pickerType == 'notificationGroupPicker'}">
        <c:set var="itemIdProperty" value="notificationGroupId"/>
        <c:set var="itemNameProperty" value="name"/>
    </c:when>
</c:choose>

<script type="text/javascript">
var itemIdProperty = '${itemIdProperty}';
var itemNameProperty = '${itemNameProperty}';

function sampleEndAction(hits) {
    if (hits && hits.length > 0) {
        alert('You have selected ' + hits.length + ' item(s).');
    } else {
        alert('Nothing has been selected');
    }
    return true;
}

function requireEvenNumber(hits) {
    var evenNumberSelected = hits && hits.length > 0 && hits.length % 2 == 0;
    if (!evenNumberSelected) {
        alert('For this picker, you must selecte an even number of items.');
    }
    return evenNumberSelected;
}

function inlinePickerSelectionMade(selectedItems) {
    alert('You selected ' + selectedItems[0][itemNameProperty] + ' with an id of ' +
            selectedItems[0][itemIdProperty] + '.');
}

$(document).ready(function() {
    //inlinePicker.show.call(inlinePicker, true);
});
</script>

<form id="picker-select-form" action="<cti:url value="/dev/styleguide/pickers"/>">
    <label>Item Type:
        <select name="pickerType" onchange="$('#picker-select-form').submit()">
            <option value="pointPicker"<c:if test="${pickerType == 'pointPicker'}"> selected</c:if>>Point Picker</option>
            <option value="paoPicker"<c:if test="${pickerType == 'paoPicker'}"> selected</c:if>>PAO Picker (Lucene)</option>
            <option value="assignedProgramPicker"<c:if test="${pickerType == 'assignedProgramPicker'}"> selected</c:if>>Assigned Program Picker (Database)</option>
            <option value="customerAccountPicker"<c:if test="${pickerType == 'customerAccountPicker'}"> selected</c:if>>Customer Account Picker (Lucene)</option>
            <option value="userPicker"<c:if test="${pickerType == 'userPicker'}"> selected</c:if>>User Picker (Lucene)</option>
            <option value="userGroupPicker"<c:if test="${pickerType == 'userGroupPicker'}"> selected</c:if>>User Group Picker (Lucene)</option>
            <option value="loginGroupPicker"<c:if test="${pickerType == 'loginGroupPicker'}"> selected</c:if>>Role Group Picker (Lucene)</option>
            <option value="notificationGroupPicker"<c:if test="${pickerType == 'notificationGroupPicker'}"> selected</c:if>>Notification Group Picker (Custom)</option>
        </select>
    </label>
</form>

<%-- LINK PICKERS --%>
<tags:sectionContainer2 nameKey="linkPickers">
<table class="compact-results-table">
<tr>
  <th>Picker</th>
  <th>Input</th>
  <th>endAction</th>
  <th>allowEmptySelection</th>
  <th>multiSelectMode</th>
  <th>immediateSelectMode</th>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p1"
    type="${pickerType}"
    destinationFieldId="p1_input"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true">Pick 1</tags:pickerDialog></td>
  <td><input type="text" id="p1_input"></td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p2"
    type="${pickerType}"
    destinationFieldId="p2_input"
    linkType="link"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="false">Pick 2</tags:pickerDialog></td>
  <td><input type="text" id="p2_input"></td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p3"
    type="${pickerType}"
    destinationFieldId="p3_input"
    linkType="link"
    allowEmptySelection="false"
    multiSelectMode="true"
    immediateSelectMode="false">Pick 3</tags:pickerDialog></td>
  <td><input type="text" id="p3_input"></td>
  <td></td>
  <td>false</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p4"
    type="${pickerType}"
    destinationFieldId="p4_input"
    allowEmptySelection="true"
    multiSelectMode="true"
    immediateSelectMode="false">Pick 4</tags:pickerDialog></td>
  <td><input type="text" id="p4_input"></td>
  <td></td>
  <td>true</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p5"
    type="${pickerType}"
    destinationFieldId="p5_input"
    linkType="link"
    allowEmptySelection="true"
    multiSelectMode="true"
    immediateSelectMode="false"
    endAction="sampleEndAction">Pick 5</tags:pickerDialog></td>
  <td><input type="text" id="p5_input"></td>
  <td>alert</td>
  <td>true</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p6"
    type="${pickerType}"
    destinationFieldName="p6_input"
    allowEmptySelection="true"
    multiSelectMode="true"
    immediateSelectMode="false">Pick 6</tags:pickerDialog></td>
  <td><span title="Use IE dev tools or firebug to view.  The hidden input fields are all inside a span called picker_p6_inputArea just before the picker link.  When nothing is selected, this span will be empty.">hidden input fields</span></td>
  <td></td>
  <td>true</td>
  <td>true</td>
  <td>false</td>
</tr>
</table>
</tags:sectionContainer2>

<%-- BUTTON PICKERS --%>
<tags:sectionContainer2 nameKey="buttonPickers">
<table class="compact-results-table">
<tr>
  <th>Picker</th>
  <th>Input</th>
  <th>nameKey</th>
  <th>endAction</th>
  <th>allowEmptySelection</th>
  <th>multiSelectMode</th>
  <th>immediateSelectMode</th>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p7"
    type="${pickerType}"
    destinationFieldId="p7_input"
    linkType="button"
    nameKey="pick7"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="false"
    icon="icon-folder-edit"
    endAction="sampleEndAction"/></td>
  <td><input type="text" id="p7_input"></td>
  <td>pick7</td>
  <td>alert</td>
  <td>false</td>
  <td>false</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p8"
    type="${pickerType}"
    destinationFieldId="p8_input"
    linkType="button"
    icon="icon-folder-edit"
    nameKey="pick8"
    allowEmptySelection="false"
    multiSelectMode="true"
    immediateSelectMode="false"
    endAction="requireEvenNumber"/></td>
  <td><input type="text" id="p8_input"></td>
  <td>pick8</td>
  <td>require even</td>
  <td>false</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p9"
    type="${pickerType}"
    destinationFieldId="p9_input"
    linkType="button"
    nameKey="pick9"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true"/></td>
  <td><input type="text" id="p9_input"></td>
  <td>pick9 (no image)</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p10"
    type="${pickerType}"
    destinationFieldName="p10_input"
    linkType="button"
    nameKey="add"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true"/></td>
  <td><span title="Use IE dev tools or firebug to view.  The hidden input fields are all inside a span called picker_p9_input_inputArea just before the picker link.  When nothing is selected, this span will be empty.">hidden input fields</span></td>
  <td>add (component)</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p11"
    type="${pickerType}"
    destinationFieldId="p11_input"
    linkType="button"
    nameKey="pick11"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true"
    extraDestinationFields="${itemIdProperty}:p11Span;${itemNameProperty}:p11Input"/></td>
  <td><input type="text" id="p11_input"></td>
  <td>pick11</td>
  <td></td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
<tr>
  <td>&nbsp;</td>
  <td colspan="6">
    Selected Id: [<span id="p11Span"></span>]; Selected Name: <input type="text" id="p11Input" value="">
  </td>
</tr>
</table>
</tags:sectionContainer2>

<%-- MEMORY GROUPED PICKERS --%>
<tags:sectionContainer2 nameKey="memoryGroupedPickers">
<table class="compact-results-table">
<tr>
  <th>Picker</th>
  <th>Input</th>
  <th>nameKey</th>
  <th>allowEmptySelection</th>
  <th>multiSelectMode</th>
  <th>immediateSelectMode</th>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p12"
    type="${pickerType}"
    destinationFieldId="p12_input"
    linkType="button"
    nameKey="pick12"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="false"
    memoryGroup="group1"/></td>
  <td><input type="text" id="p12_input"></td>
  <td>pick12</td>
  <td>false</td>
  <td>false</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p13"
    type="${pickerType}"
    destinationFieldId="p13_input"
    linkType="button"
    nameKey="pick13"
    allowEmptySelection="false"
    multiSelectMode="true"
    immediateSelectMode="false"
    memoryGroup="group1"/></td>
  <td><input type="text" id="p13_input"></td>
  <td>pick13</td>
  <td>false</td>
  <td>true</td>
  <td>false</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p14"
    type="${pickerType}"
    destinationFieldId="p14_input"
    linkType="button"
    nameKey="pick14"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true"
    memoryGroup="group1"/></td>
  <td><input type="text" id="p14_input"></td>
  <td>pick14 (no image)</td>
  <td>false</td>
  <td>false</td>
  <td>true</td>
</tr>
</table>
</tags:sectionContainer2>

<%-- SELECTION PICKERS --%>
<tags:sectionContainer2 nameKey="selectionPickers">
<table class="compact-results-table">
<tr>
  <th>Picker</th>
  <th>Input</th>
  <th>selectionProperty</th>
  <th>nameKey</th>
  <th>endAction</th>
  <th>Other</th>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p15"
    type="${pickerType}"
    destinationFieldId="p15_input"
    linkType="selection"
    selectionProperty="${itemNameProperty}"/></td>
  <td><input type="text" id="p15_input"></td>
  <td>${itemNameProperty}</td>
  <td title="This picker uses the default nameKey (none is specified).  This will probably be the norm.">default</td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p16"
    type="${pickerType}"
    destinationFieldId="p16_input"
    linkType="selection"
    selectionProperty="${itemNameProperty}"
    icon="icon-add"
    nameKey="pick16"/></td>
  <td><input type="text" id="p16_input"></td>
  <td>${itemNameProperty}</td>
  <td>pick2</td>
  <td>&nbsp;</td>
  <td>&nbsp;</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p17"
    type="${pickerType}"
    destinationFieldId="p17_input"
    linkType="selection"
    selectionProperty="${itemNameProperty}"
    allowEmptySelection="true"/></td>
  <td><input type="text" id="p17_input"></td>
  <td>${itemNameProperty}</td>
  <td title="This picker uses the default nameKey (none is specified).  This will probably be the norm.">default</td>
  <td>&nbsp;</td>
  <td>allowEmptySelection</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p18"
    type="${pickerType}"
    destinationFieldId="p18_input"
    linkType="selection"
    icon="icon-add"
    selectionProperty="${itemNameProperty}"
    nameKey="pick18"
    allowEmptySelection="true"/></td>
  <td><input type="text" id="p18_input"></td>
  <td>${itemNameProperty}</td>
  <td>pick4</td>
  <td>&nbsp;</td>
  <td>allowEmptySelection</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p19"
    type="${pickerType}"
    destinationFieldId="p19_input"
    linkType="selection"
    icon="icon-add"
    selectionProperty="${itemNameProperty}"
    nameKey="pick19"
    allowEmptySelection="true"
    multiSelectMode="true"
    immediateSelectMode="false"/></td>
  <td><input type="text" id="p19_input"></td>
  <td>${itemNameProperty}</td>
  <td>pick5</td>
  <td>&nbsp;</td>
  <td>multiSelectMode</td>
</tr>
<tr>
  <td><tags:pickerDialog
    id="p20"
    type="${pickerType}"
    destinationFieldId="p20_input"
    linkType="selection"
    icon="icon-folder-edit"
    selectionProperty="${itemIdProperty}"
    nameKey="pick20"
    allowEmptySelection="false"
    multiSelectMode="false"
    immediateSelectMode="true"/></td>
  <td><input type="text" id="p20_input"></td>
  <td>${itemIdProperty}</td>
  <td>pick6</td>
  <td>&nbsp;</td>
  <td>immediateSelectMode</td>
</tr>
</table>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="rememberingPickers">
  With this picker, you can make a selection and submit the form and the selection should be
  reselected when the page reloads.

  <form action="<cti:url value="/dev/styleguide/pickers"/>">
    <c:if test="${!empty param.pickerType}">
      <input type="hidden" name="pickerType" value="${param.pickerType}"/>
    </c:if>
    <tags:pickerDialog
      id="rememberingPicker"
      type="${pickerType}"
      destinationFieldName="rememberingPickerInput"
      linkType="selection"
      selectionProperty="${itemNameProperty}"
      allowEmptySelection="true"
      multiSelectMode="true"
      initialIds="${paramValues['rememberingPickerInput']}"/>
    <cti:button type="submit" label="Submit"/>
  </form>
</tags:sectionContainer2>

<tags:sectionContainer2 nameKey="inlinePickers">
    <div id="inline-picker-container"></div>
    <tags:pickerDialog
         id="inlinePicker"
         type="${pickerType}"
         destinationFieldName="inlineId"
         linkType="none"
         container="inline-picker-container" 
         immediateSelectMode="true"
         endAction="inlinePickerSelectionMade"/>
     <script>$(function() { window['inlinePicker'].show(); });</script>
 </tags:sectionContainer2>

</cti:msgScope>

</tags:styleguide>
</cti:standardPage>
<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" required="true" description="Spring bean name of the Picker class"%>
<%@ attribute name="id" required="true" description="Unique id for this picker"%>

<%@ attribute name="destinationFieldId" description="Id of field to place selected items on picker close"%>
<%@ attribute name="excludeIds" description="The ids that cannot be selected in the picker" type="java.lang.Object"%>
<%@ attribute name="destinationFieldName"  description="Name of field to place selected items on picker close"%>
<%@ attribute name="multiSelectMode" type="java.lang.Boolean" description="True if this picker allows selection of multiple items"%>
<%@ attribute name="immediateSelectMode" type="java.lang.Boolean" description="True if picker should select and close when an item is clicked"%>
<%@ attribute name="endAction" description="Javascript function to call on picker ok button press"%>
<%@ attribute name="cancelAction" description="Javascript function to call on picker cancel button press"%>
<%@ attribute name="memoryGroup" description="Adds the picker to the memory group - picker will open up with previous search text populated (as long as no page refresh between)"%>
<%@ attribute name="linkType" description="Type of link to create which can be 'normal' (the default--a plain anchor tag link), 'button', 'selection' or 'none'"%>
<%@ attribute name="nameKey" description="i18n key; required if linkType is 'button'; unused for 'none' or 'link'"%>
<%@ attribute name="styleClass" description="CSS class names applied to the picker's outer container."%>
<%@ attribute name="extraArgs" description="Dynamic inputs to picker search"%>
<%@ attribute name="extraDestinationFields" description="used when a selection has been made and the picker is closed.  It's a semicolon separated list of: [property]:[fieldId]"%>
<%@ attribute name="buttonStyleClass" description="Class to style the button with"%>
<%@ attribute name="icon" description="Icon class to use for button."%>
<%@ attribute name="anchorStyleClass" description="Class to style the anchor with"%>
<%@ attribute name="selectionProperty" description="Required with a linkType of 'selection', used to determine name of property from selected item to display in label.  Not used with other linkType values."%>
<%@ attribute name="allowEmptySelection" description="Allow an empty selection.  Only valid when 'multiSelectMode' is true."%>
<%@ attribute name="initialId" description="Id of item selected at the start."%>
<%@ attribute name="initialIds" type="java.lang.Object" description="Ids of items selected at the start." %>
<%@ attribute name="useInitialIdsIfEmpty" type="java.lang.Boolean" description="Clears selection if initial id(s) is empty." %>
<%@ attribute name="container" description="The id of the container element. Used for placing a picker on the page rather than loaded into a dialog."%>
<%@ attribute name="viewOnlyMode" type="java.lang.Boolean" description="causes picker display the value only; only usable with selection linkType"%>
<%@ attribute name="buttonRenderMode" description="passes the render mode to the cti:button tag; only usable with button linkType" %>

<c:set var="containerArg" value="null"/>
<c:if test="${!empty  pageScope.container}">
    <c:set var="containerArg" value="$('#${pageScope.container}')"/>
</c:if>

<cti:msg2 var="okText" key="yukon.common.okButton"/>
<cti:msg2 var="cancelText" key="yukon.common.cancel"/>
<cti:msg2 var="noneSelectedText" key="yukon.web.components.button.selectionPicker.label"/>

<cti:pickerProperties var="idFieldName" property="ID_FIELD_NAME" type="${type}"/>
<cti:pickerProperties var="outputColumns" property="OUTPUT_COLUMNS" type="${type}"/>

<script type="text/javascript">
    // Only create picker if not already created.  This tag gets called more than
    // once if it's used inside a widget and the widget is updated.  Since the user
    // isn't navigating off the page, we want to keep the same picker.

    // jQuery dialogs get moved to the end of the body. When pickers are ajaxed
    // in and replaced, the dialogs might still be left over. Here we will remove any
    // still hanging around.

    $(document.getElementById('${id}')).remove();
    try {
        try {
            ${id} = new Picker('${okText}', '${cancelText}', '${noneSelectedText}', '${type}', '${pageScope.destinationFieldName}', '${id}', '${pageScope.extraDestinationFields}', ${containerArg});
            ${id}.idFieldName = '${idFieldName}';
            ${id}.outputColumns = ${outputColumns};
        } catch(pickerException) {
            debug.log('pickerDialog.tag: new Picker failed: ' + pickerException);
        }
        
        <c:if test="${pageScope.multiSelectMode}">
            ${id}.multiSelectMode = true;
        </c:if>
        <c:if test="${pageScope.immediateSelectMode}">
            ${id}.immediateSelectMode = true;
        </c:if>
        <c:if test="${!empty pageScope.endAction}">
            ${id}.endAction = ${pageScope.endAction};
        </c:if>
        <c:if test="${!empty pageScope.cancelAction}">
            ${id}.cancelAction = ${pageScope.cancelAction};
        </c:if>
        <c:if test="${!empty pageScope.destinationFieldId}">
            ${id}.destinationFieldId = '${pageScope.destinationFieldId}';
        </c:if>
        <c:if test="${!empty pageScope.memoryGroup}">
            ${id}.memoryGroup = '${pageScope.memoryGroup}';
        </c:if>
        <c:if test="${!empty pageScope.extraArgs}">
            ${id}.extraArgs = '<spring:escapeBody javaScriptEscape="true">${pageScope.extraArgs}</spring:escapeBody>';
        </c:if>
        <c:if test="${!empty pageScope.selectionProperty && pageScope.linkType == 'selection'}">
            ${id}.selectionProperty = '${pageScope.selectionProperty}';
        </c:if>
        <c:if test="${!empty pageScope.allowEmptySelection}">
            ${id}.allowEmptySelection = ${pageScope.allowEmptySelection};
        </c:if>
        <c:if test="${pageScope.linkType == 'selection'}">
            ${id}.selectedAndMsg = '<cti:msg2 javaScriptEscape="true" key="yukon.web.picker.selectedAnd"/>';
            ${id}.selectedMoreMsg = '<cti:msg2 javaScriptEscape="true" key="yukon.web.picker.selectedMore"/>';
        </c:if>
        <c:if test="${pageScope.useInitialIdsIfEmpty}">
            ${id}.useInitialIdsIfEmpty = true;
        </c:if>
    } catch (pickerEx) {
            debug.log("Could not create Picker: " + pickerEx);
    }

</script>

<c:if test="${pageScope.linkType != 'selection' && !empty pageScope.selectionProperty}">
    <span class="error">The "selectionProperty" attribute is
        only valid when using "selection" linkType on tags:pickerDialog.</span>
</c:if>

<c:if test="${pageScope.linkType == 'selection'}">
    <cti:msgScope paths="components.picker">
        <cti:msg2 var="selectedItemsDialogTitleMsg" key=".selectedItemsDialogTitle"/>
        <cti:msg2 var="closeMsg" key=".close"/>
    </cti:msgScope>
    <div title="${selectedItemsDialogTitleMsg}" id="picker_${id}_selectedItemsPopup">
        <div id="picker_${id}_selectedItemsDisplayArea"></div>
    </div>
</c:if>

<span id="picker_${id}_inputArea">
<c:if test="${!empty pageScope.initialIds}">
    <c:forEach var="initialId" items="${initialIds}">
        <input type="hidden" name="${destinationFieldName}" value="${initialId}">
    </c:forEach>
</c:if>
<c:if test="${!empty pageScope.initialId}">
    <input type="hidden" name="${destinationFieldName}" value="${initialId}">
</c:if>
</span>

<c:if test="${pageScope.viewOnlyMode}">
    <c:set var="viewMode" value="true"/>
    <span id="picker_${id}_btn"><span></span></span>
</c:if>
<c:if test="${!pageScope.viewOnlyMode}">
    <c:set var="viewMode" value="false"/>
    <c:if test="${pageScope.linkType != 'none'}">
        <span <c:if test="${not empty pageScope.styleClass}">class="${pageScope.styleClass}"</c:if>>
            <c:choose>
                <c:when test="${pageScope.linkType == 'button'}">
                    <c:set var="renderMode" value="button"/>
                    <c:if test="${not empty pageScope.buttonRenderMode}">
                        <c:set var="renderMode" value="${pageScope.buttonRenderMode}"/>
                    </c:if>
                    <cti:button id="picker_${id}_btn"
                                nameKey="${pageScope.nameKey}" 
                                onclick="${id}.show.call(${id})" 
                                renderMode="${pageScope.renderMode}" 
                                classes="${pageScope.buttonStyleClass}" 
                                icon="${pageScope.icon}"/>
                </c:when>
                <c:when test="${pageScope.linkType == 'selection'}">
                    <c:if test="${empty pageScope.selectionProperty}">
                        <span class="error">The "selectionProperty" attribute is
                            required when using "selection" linkType on tags:pickerDialog.</span>
                    </c:if>
                    <c:if test="${empty pageScope.nameKey}">
                        <c:set var="nameKey" value="selectionPicker"/>
                    </c:if>
                    <c:set var="icon" value="${empty pageScope.icon ? 'icon-database-add' : icon}"/>
                    <cti:button id="picker_${id}_btn" 
                                nameKey="${pageScope.nameKey}" 
                                classes="noSelectionPickerLabel ${pageScope.buttonStyleClass}" 
                                renderMode="labeledImage" 
                                icon="${icon}"
                                onclick="${id}.show.call(${id})"/>
                    <c:if test="${pageScope.multiSelectMode}">
                        <cti:icon id="picker_${id}_showSelectedImg" 
                                  href="javascript:${id}.showSelected.call(${id})" 
                                  nameKey="zoom" 
                                  icon="icon-magnifier"/>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <c:set var="anchorAttributes" value=""/>
                    <c:if test="${!empty pageScope.anchorStyleClass}">
                        <c:set var="anchorAttributes" value=" class=\"${pageScope.anchorStyleClass}\""/>
                    </c:if>
                    <a href="javascript:${id}.show.call(${id})"${anchorAttributes}><jsp:doBody/></a>
                </c:otherwise>
            </c:choose>
        </span>
    </c:if>
</c:if>

<script type="text/javascript">
try {
    $(function () {
        ${id}.init.bind(${id}, ${viewMode})();
    });
} catch (callAfterLoadex) {
    debug.log('exception for picker: ' + "${id}" + ': ' + callAfterLoadex);
    alert("pickerDialog.tag: yukon.picker.init exception: " + callAfterLoadex);
}

if (${!empty excludeIds}) {
    ${id}.excludeIds = ${cti:jsonString(excludeIds)};
}
</script>

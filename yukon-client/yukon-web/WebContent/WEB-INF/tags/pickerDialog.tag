<%@ tag trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="type" required="true" description="Spring bean name of the Picker class." %>
<%@ attribute name="id" required="true" description="Unique id for this picker, must be safe for javascript variable names and html element ids." %>
<%@ attribute name="okButtonKey" description="The key for the OK button for the dialog"%>

<%@ attribute name="destinationFieldId" description="Id of field to place selected items on picker close." %>
<%@ attribute name="excludeIds" type="java.lang.Object" description="The ids that cannot be selected in the picker." %>
<%@ attribute name="disabledIds" type="java.lang.Object" description="The ids that are displayed in the picker, but disabled." %>
<%@ attribute name="destinationFieldName"  description="Name of field to place selected items on picker close." %>
<%@ attribute name="multiSelectMode" type="java.lang.Boolean" description="If 'true', this picker allows selection of multiple items." %>
<%@ attribute name="immediateSelectMode" type="java.lang.Boolean" description="If 'true' this picker will select and close when an item is clicked." %>

<%@ attribute name="endAction" 
    description="Javascript function called when picker ok button press or when item is clicked if 
                 'immediateSelectMode' is 'true'." %>
<%@ attribute name="endEvent" 
    description="The name of an event to fire when item selection is finalized either immediately when using 
                 'immediateSelectMode' or when the 'ok' button is clicked. The event will also have the array 
                 of selected items and the javascript picker object itself passed with." %>
                 
<%@ attribute name="cancelAction" description="Javascript function called when picker cancel button press." %>
<%@ attribute name="memoryGroup" description="Adds the picker to the memory group - picker will open up with previous search text populated as long as no page refresh between." %>
<%@ attribute name="linkType" description="Type of link to create which can be 'normal' (the default--a plain anchor tag link), 'button', 'selection' or 'none'." %>
<%@ attribute name="nameKey" description="I18n key. Required if linkType is 'button'. Only used for 'button' and 'selection'." %>
<%@ attribute name="styleClass" description="CSS class names applied to the picker's outer container." %>
<%@ attribute name="extraArgs" description="Request parameters used inpicker search request. See jquery's ajax api." %>
<%@ attribute name="extraDestinationFields" description="Used when a selection has been made and the picker is closed.  It's a semicolon separated list of: [property]:[fieldId]" %>
<%@ attribute name="buttonStyleClass" description="CSS class names applied to the button." %>
<%@ attribute name="icon" description="Specifies and icon for the button." %>
<%@ attribute name="anchorStyleClass" description="CSS class names applied to the 'a' link element." %>
<%@ attribute name="selectionProperty" description="Required with a linkType of 'selection', used to determine name of property from selected item to display in label.  Not used with other linkType values." %>
<%@ attribute name="allowEmptySelection" description="Allow an empty selection.  Only valid when 'multiSelectMode' is true." %>
<%@ attribute name="initialId" description="Id of pre-selected item." %>
<%@ attribute name="initialIds" type="java.lang.Object" description="Id of pre-selected item. Should be parsable as a JSON array. i.e. ArrayList." %>
<%@ attribute name="useInitialIdsIfEmpty" type="java.lang.Boolean" description="Clears selection if initial id(s) is empty." %>
<%@ attribute name="container" description="The id of the container element. Used for placing a picker inline on the page rather than shown in a dialog." %>
<%@ attribute name="viewOnlyMode" type="java.lang.Boolean" description="Causes picker component to display the value but not be clickable.  Only usable with when linkType is 'selection'." %>
<%@ attribute name="buttonRenderMode" description="Passes the render mode to the cti:button tag.  Only usable 'linkType' is 'button'.  See cti:button 'renderMode' attribute." %>
<%@ attribute name="includeRemoveButton" description="Adds a red x next to the picker to clear out the selected item" %>
<%@ attribute name="removeValue" description="Value to set the item to when cleared.  Default will be blank" %>
<%@ attribute name="maxNumSelections" type="java.lang.Integer" description="An upper limit on the number of selections that the user can make. This can only be set if multiSelectMode is enabled." %>

<cti:default var="linkType" value="normal"/>
<cti:msg2 var="okText" key="yukon.common.okButton"/>
<c:if test="${!empty okButtonKey}">
    <cti:msg2 var="okText" key="${okButtonKey}"/>
</c:if>
<cti:msg2 var="cancelText" key="yukon.common.cancel"/>
<cti:msg2 var="noneSelectedText" key="yukon.web.components.button.selectionPicker.label"/>

<c:if test="${!empty pageScope.selectionProperty}">
    <c:if test="${linkType != 'selection' && linkType != 'selectionLabel'}">
    <span class="error">The "selectionProperty" attribute is
        only valid when using "selection" or "selectionLabel" linkType on tags:pickerDialog.</span>
        </c:if>
</c:if>

<c:if test="${linkType == 'selection'}">
    <cti:msgScope paths="components.picker">
        <cti:msg2 var="selectedItemsDialogTitleMsg" key=".selectedItemsDialogTitle"/>
    </cti:msgScope>
    <div title="${selectedItemsDialogTitleMsg}" id="picker-${id}-selected-items-popup">
        <div id="picker-${id}-selected-items-display-area"></div>
    </div>
</c:if>

<span id="picker-${id}-input-area">
    <div class="dn js-picker-output-columns"><cti:pickerProperties property="OUTPUT_COLUMNS" type="${type}"/></div>
    <div class="dn js-picker-id-field-name"><cti:pickerProperties property="ID_FIELD_NAME" type="${type}"/></div>
    <div class="dn js-picker-container">${pageScope.container}</div>
    <div class="dn js-picker-end-event">${pageScope.endEvent}</div>
    <div class="dn js-picker-exclude-ids"
    ><c:if test="${not empty pageScope.excludeIds}">${cti:jsonString(excludeIds)}</c:if></div>
    <div class="dn js-picker-disabled-ids"><c:if test="${not empty pageScope.disabledIds}">${cti:jsonString(disabledIds)}</c:if></div>
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
    <span id="picker-${id}-btn" data-picker-id="${id}"><span></span></span>
</c:if>
<c:if test="${!pageScope.viewOnlyMode}">
    <c:set var="viewMode" value="false"/>
    <c:if test="${linkType != 'none'}">
        <span <c:if test="${not empty pageScope.styleClass}">class="${pageScope.styleClass}"</c:if>>
            <c:choose>
                <c:when test="${linkType == 'button'}">
                    <c:set var="renderMode" value="button"/>
                    <c:if test="${not empty pageScope.buttonRenderMode}">
                        <c:set var="renderMode" value="${pageScope.buttonRenderMode}"/>
                    </c:if>
                    <cti:button id="picker-${id}-btn"
                                data-picker-id="${id}"
                                nameKey="${pageScope.nameKey}" 
                                onclick="${id}.show.call(${id})" 
                                renderMode="${pageScope.renderMode}" 
                                classes="${pageScope.buttonStyleClass}" 
                                icon="${pageScope.icon}"/>
                </c:when>
                <c:when test="${linkType == 'selection'}">
                    <c:if test="${empty pageScope.selectionProperty}">
                        <span class="error">The "selectionProperty" attribute is
                            required when using "selection" linkType on tags:pickerDialog.</span>
                    </c:if>
                    <c:if test="${empty pageScope.nameKey}">
                        <c:set var="nameKey" value="selectionPicker"/>
                    </c:if>
                    <c:set var="icon" value="${empty pageScope.icon ? 'icon-database-add' : icon}"/>
                    <cti:button id="picker-${id}-btn" 
                                data-picker-id="${id}"
                                nameKey="${pageScope.nameKey}" 
                                classes="picker-button noSelectionPickerLabel ${pageScope.buttonStyleClass}" 
                                renderMode="labeledImage" 
                                icon="${icon}"
                                onclick="${id}.show.call(${id})"/>
                    <c:if test="${pageScope.multiSelectMode}">
                        <cti:icon id="picker-${id}-show-selected-icon" 
                                  href="javascript:${id}.showSelected.call(${id})" 
                                  nameKey="zoom" 
                                  icon="icon-magnifier"/>
                    </c:if>
                </c:when>
                <c:when test="${linkType == 'selectionLabel'}">
                    <c:if test="${empty pageScope.selectionProperty}">
                        <span class="error">The "selectionProperty" attribute is
                            required when using "selection" linkType on tags:pickerDialog.</span>
                    </c:if>
                    <c:if test="${empty pageScope.nameKey}">
                        <c:set var="nameKey" value="selectionPicker"/>
                    </c:if>
                    <c:set var="icon" value="${empty pageScope.icon ? '' : icon}"/>
                    <cti:button id="picker-${id}-btn" 
                                data-picker-id="${id}"
                                nameKey="${pageScope.nameKey}" 
                                classes="picker-button noSelectionPickerLabel ${pageScope.buttonStyleClass}" 
                                renderMode="label" 
                                icon="${icon}"
                                onclick="${id}.show.call(${id})"/>
                    <c:if test="${pageScope.multiSelectMode}">
                        <cti:icon id="picker-${id}-show-selected-icon" 
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
                <c:if test="${includeRemoveButton}">

                    <cti:icon id="picker-${id}-remove-selected-icon" 
                              href="javascript:${id}.removeEvent()" 
                              nameKey="remove" 
                              classes="js-remove-point dn show-on-hover"
                              icon="icon-cross"/>
                              
               </c:if>
        </span>
    </c:if>

</c:if>                   

<c:set var="hasEndAction" value="${not empty endAction}"/>

<script type="text/javascript">

(function () {

    // Only create picker if not already created.  This tag gets called more than
    // once if it's used inside a widget and the widget is updated.  Since the user
    // isn't navigating off the page, we want to keep the same picker.
    
    // Jquery dialogs get moved to the end of the body. When pickers are ajaxed
    // in and replaced, the dialogs might still be left over. Here we will remove any
    // still hanging around.
    $('#${id}').remove();
    
    var picker;
    var data = $('#picker-${id}-input-area');
    var columns = JSON.parse(data.find('.js-picker-output-columns').text());
    var field = data.find('.js-picker-id-field-name').text().trim();
    var container = data.find('.js-picker-container').text().trim();
    if (container === '') {
        container = null;
    } else {
        container = '#' + container;
    }
    var excluedIds = data.find('.js-picker-exclude-ids').text().trim();
    var disabledIds = data.find('.js-picker-disabled-ids').text().trim();
    var endEvent = data.find('.js-picker-end-event').text().trim();
    
    try {
        try {
            picker = new Picker('${okText}', '${cancelText}', '${noneSelectedText}', '${type}', 
                    '${pageScope.destinationFieldName}', '${id}', 
                    '${pageScope.extraDestinationFields}', container);
            picker.id = '${id}';
            picker.idFieldName = field;
            picker.outputColumns = columns;
            picker.endEvent = endEvent;
            
            window['${id}'] = picker;
            if (!yukon.pickers) yukon.pickers = {};
            yukon.pickers['${id}'] = picker;
            
        } catch (pickerException) {
            debug.log('pickerDialog.tag: new Picker failed: ' + pickerException);
        }
        
        if ('${pageScope.multiSelectMode}' === 'true') {
            picker.multiSelectMode = true;
        }
        if ('${pageScope.immediateSelectMode}' === 'true') {
            picker.immediateSelectMode = true;
        }
        <c:if test="${hasEndAction}">
            picker.endAction = ${pageScope.endAction};
        </c:if>
        if ('${pageScope.cancelAction}' !== '') {
            picker.cancelAction = '${pageScope.cancelAction}';
        }
        if ('${pageScope.destinationFieldId}' !== '') {
            picker.destinationFieldId = '${destinationFieldId}';
        }
        if ('${pageScope.memoryGroup}' !== '') {
            picker.memoryGroup = '${memoryGroup}';
        }
        if ('${pageScope.extraArgs}' !== '') {
            picker.extraArgs = '<spring:escapeBody javaScriptEscape="true">${extraArgs}</spring:escapeBody>';
        }
        if ('${pageScope.selectionProperty}' !== '' && '${pageScope.linkType}' === 'selection') {
            picker.selectionProperty = '${selectionProperty}';
        }
        if ('${pageScope.selectionProperty}' !== '' && '${pageScope.linkType}' === 'selectionLabel') {
            picker.selectionProperty = '${selectionProperty}';
        }
        picker.allowEmptySelection = '${pageScope.allowEmptySelection}' === 'true';
        if ('${pageScope.maxNumSelections}' !== '') {
        	picker.maxNumSelections = '${pageScope.maxNumSelections}';
        }
        picker.useInitialIdsIfEmpty = '${pageScope.useInitialIdsIfEmpty}' === 'true';
        if ('${linkType}' === 'selection') {
            picker.selectedAndMsg = '<cti:msg2 javaScriptEscape="true" key="yukon.web.picker.selectedAnd"/>';
            picker.selectedMoreMsg = '<cti:msg2 javaScriptEscape="true" key="yukon.web.picker.selectedMore"/>';
        }
        
        if ('${pageScope.includeRemoveButton}' === 'true') {
            var removeEvent = function() {
                picker.clearSelected('${removeValue}');
                picker.clearEntireSelection();
            }
            
            picker.removeEvent = removeEvent;
        };
        
    } catch (pickerEx) {
        debug.log('Could not create Picker: ' + pickerEx);
    }
    
    try {
        $(function () {
            // Initialize this picker on document ready.
            picker.init.bind(picker, '${viewMode}' === 'true')();
        });
    } catch (callAfterLoadex) {
        debug.log('exception for picker: ' + '${id}' + ': ' + callAfterLoadex);
        alert("pickerDialog.tag: yukon.picker.init exception: " + callAfterLoadex);
    }
    
    if (excluedIds !== '') {
        picker.excludeIds = JSON.parse(excluedIds);
    };
    if (disabledIds !== '') {
        picker.disabledIds = JSON.parse(disabledIds);
    };
    

})();
</script>
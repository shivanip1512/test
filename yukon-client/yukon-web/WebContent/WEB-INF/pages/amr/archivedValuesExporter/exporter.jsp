<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="tools" page="bulk.archivedValueExporter.${mode}">

    <script type="text/javascript">
    function ajaxSubmitForm(rowIndex, action, dialogSelector) {
        jQuery("#exporterForm input[name=rowIndex]").val(rowIndex);
        jQuery('#exporterForm').ajaxSubmit({'target' : dialogSelector, 'url' : action});
    }
    
    function submitForm(index, action) {
        if (index != -2) {
            jQuery("#exporterForm input[name=rowIndex]").val(index);
        }

        var exporterForm = jQuery('#exporterForm');
        exporterForm.attr('action', action);
        exporterForm[0].submit();
    }
    
    function toggleFieldAndUpdateValue(element, event){
        var selection = jQuery(element.attr("data-selection")).val();
        var fieldToDisplay = jQuery(element.attr("data-field-to-display"));
        var skipDisable = element.attr("data-skip-disable");
        
        if (!skipDisable) {
            fieldToDisplay.attr("disabled", "disabled");
        } else {
            fieldToDisplay.removeAttr("disabled");
        }

        if (selection == 'Custom' || selection == 'FIXED_VALUE') {
           //only reset the field value if the select element is the same one clicked
            if (initialized && event && event.currentTarget == element[0]) {
                fieldToDisplay.val("");
            }
            fieldToDisplay.focus();
            fieldToDisplay.show().removeAttr("disabled");
            jQuery("input[name=exportField\\.pattern]").each(function(index, element){
                if (element != fieldToDisplay[0]) {
                    element.value="";
                }
            });
        } else {
            fieldToDisplay.hide();
            if (initialized) {
                fieldToDisplay.val(selection);
            }
        }
    };

    function toggleField(element){
        var selection = jQuery(element.attr("data-selection")).val();
        var fieldToDisplay = jQuery(element.attr("data-field-to-display"));
        var skipDisable = element.attr("data-skip-disable");
        
        if(!skipDisable){
            fieldToDisplay.attr("disabled", "disabled");
        }else{
            fieldToDisplay.removeAttr("disabled");
        }
        
        fieldToDisplay.hide();
        if (selection == 'LEFT' || selection == 'RIGHT'){
            fieldToDisplay.focus();
            fieldToDisplay.show().removeAttr("disabled");
        }
        if (selection == 'LEAVE_BLANK' || selection == 'SKIP_RECORD'){
            fieldToDisplay.val("");
        }
    }
    
    function changeFieldSelect(event){
        var selectedIndex = jQuery("#fieldSelect")[0].selectedIndex;
        var selectedOption = jQuery(jQuery("#fieldSelect")[0].options[selectedIndex]);
        var value = selectedOption.attr("data-fieldType");
        if(value == "ATTRIBUTE"){
            jQuery("#exportField\\.attributeField").show();
            jQuery("#exportField\\.attributeField").removeAttr("disabled");
            var secondValue = jQuery("#exportField\\.attributeField").val();
            
            if(event && event.currentTarget.id == "exportField.attributeField"){
                switch(secondValue){
                case 'TIMESTAMP':
                case 'VALUE':
                    jQuery("#patternSelect, #timestampPatternSelect").each(function(index, element){
                        element = jQuery(element);
                        element.val(null);
                    });
                    break;
                default:
                    break;
                }
            }
            
            renderFields("." + value + "_" + secondValue);
        }else{
            jQuery("#exportField\\.attributeField").hide();
            jQuery("#exportField\\.attributeField").attr("disabled", "disabled");
            renderFields("." + value);
        }
    };
    
    function renderFields(query , element){
        jQuery(".section-container", element).show();
        
        jQuery(".f-renderFields tr").hide();
        jQuery(".f-renderFields tr").find("input,select,textarea").attr('disabled', 'disabled');
        jQuery(".f-renderFields " + query).show();
        jQuery(".f-renderFields " + query).find("input,select,textarea").removeAttr('disabled');
        
        jQuery(".section-container", element).each(function(index, sectionContainer){
            var container = jQuery(sectionContainer);
            if(container.find("tr:visible").length == 0){
                container.hide();
            }
        });
    }
    
  //pass the event in to determine exactly which element was clicked.  This is used to
  //determine if we need to reset the value on a targeted field
  function renderPopup(event){
      //Select Field
      changeFieldSelect(event);
      
      //Define Layout
      toggleFieldAndUpdateValue(jQuery("#unsupportedFieldSelect"), event);
      toggleFieldAndUpdateValue(jQuery("#padCharSelect"), event);
      toggleField(jQuery("#padSideSelect"));
      
      toggleFieldAndUpdateValue(jQuery("#patternSelect"), event);
      toggleFieldAndUpdateValue(jQuery("#timestampPatternSelect"), event);
   };
  
   function populateFieldDialog(data){
      for(key in data) {
          jQuery("exportField\\."+key).val(data[key]);
      }
   };

    function renderContainer(){
        toggleFieldAndUpdateValue(jQuery("#delimiterSelect"));
    }
    
    var initialized = false;
    jQuery(document).ready(function() {
        <c:if test="${showAttributePopup}">
            <cti:msgScope paths=".${backingBean.pageNameKey},components.dialog.${backingBean.pageNameKey},components.dialog">
                <cti:msg2 var="titleMsg" key=".title"/>
                <cti:msg2 var="okBtnMsg" key=".ok"/>
                <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
            </cti:msgScope>
            var buttons = [];
            var okButton = {'text' : '${okBtnMsg}', 'class': 'primary'};
            okButton.click = function() { jQuery('#attributeDialog').trigger('editAttributeOkPressed'); };
            buttons.push(okButton);
            buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
            
            var dialogOpts = {
                      'title' : '${titleMsg}',
                      'position' : 'center',
                      'width' : 'auto',
                      'height' : 'auto',
                      'modal' : true,
                      'buttons' : buttons };
            jQuery('#attributeDialog').dialog(dialogOpts);
        </c:if>
        <c:if test="${showFieldPopup}">
            <cti:msgScope paths=".${backingBean.pageNameKey},components.dialog.${backingBean.pageNameKey},components.dialog">
               <cti:msg2 var="titleMsg" key=".title"/>
               <cti:msg2 var="okBtnMsg" key=".ok"/>
               <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
             </cti:msgScope>
             var buttons = [];
             var okButton = {'text' : '${okBtnMsg}', 'class': 'primary'};
             okButton.click = function() { jQuery('#fieldDialog').trigger('editFieldOkPressed'); };
             buttons.push(okButton);
             buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { jQuery(this).dialog('close'); }});
             var dialogOpts = {
                  'title' : '${titleMsg}',
                  'position' : 'center',
                  'width' : 'auto',
                  'height' : 'auto',
                  'modal' : true,
                  'buttons' : buttons };
             var fieldDialogDiv = jQuery('#fieldDialog');
             fieldDialogDiv.dialog(dialogOpts);
        </c:if>
        renderContainer();
        jQuery(document).on('change', '#fieldDialog select', renderPopup);
        jQuery(document).on('change', '#delimiterSelect', renderContainer);
        
        initialized = true;
        jQuery('#addAttributeBtn').click(function() {
            ajaxSubmitForm(-1, 'ajaxEditAttribute', '#attributeDialog');
        });
        jQuery('#addFieldBtn').click(function() {
            ajaxSubmitForm(-1, 'ajaxEditField', '#fieldDialog');
        });
        jQuery('.editAttributeBtn').click(function(event) {
            var rowIndex = jQuery(event.currentTarget).closest('tr').attr('data-row-index');
            ajaxSubmitForm(rowIndex, 'ajaxEditAttribute', '#attributeDialog');
        });
        jQuery('.editFieldBtn').click(function(event) {
            var rowIndex = jQuery(event.currentTarget).closest('tr').attr('data-row-index');
            ajaxSubmitForm(rowIndex, 'ajaxEditField', '#fieldDialog');
        });
        jQuery('.removeAttributeBtn').click(function(event) {
            var rowIndex = jQuery(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'removeAttribute');
        });
        jQuery('.removeFieldBtn').click(function(event) {
            var rowIndex = jQuery(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'removeField');
        });
        jQuery('.downFieldBtn').click(function(event) {
            var rowIndex = jQuery(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'moveFieldDown');
        });
        jQuery('.upFieldBtn').click(function(event) {
            var rowIndex = jQuery(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'moveFieldUp');
        });
        function editAttributeOkPressed() {
            jQuery('#attributeDialog').dialog('close');
            jQuery('#dialogFormElements').hide().remove().appendTo(jQuery('#exporterForm'));
            submitForm(-2, 'addAttribute');
        }
        jQuery('#attributeDialog').bind('editAttributeOkPressed', editAttributeOkPressed);

        jQuery("#attributeDialog").keydown(function (event) {
            if (13 === event.which) {
                editAttributeOkPressed();
            }
        });

        function editFieldOkPressed() {
            jQuery('#fieldDialog').dialog('close');
            jQuery('#dialogFieldFormElements').hide().remove().appendTo(jQuery('#exporterForm'));
            submitForm(-2, 'addField');
        }
        jQuery('#fieldDialog').bind('editFieldOkPressed', editFieldOkPressed);

        jQuery('.submitToName').click(function(event) {
            var exporterForm = jQuery('#exporterForm');
            exporterForm.attr('action', jQuery(event.currentTarget).attr('name'));
            exporterForm[0].submit();
        });
        jQuery('#saveBtn').click(function(event) {
            submitForm(-1, 'saveFormat');
        });
        jQuery(document).on('yukonDialogConfirmOk', '#yukon_dialog_confirm', function(event) {
            event.preventDefault();
            yukon.DialogConfirmationManager.cancel();
            submitForm(-1, 'deleteFormat');
        });
        jQuery('.f-initialFocus').focus();
    });
</script>

    <div class="dn">
        <div id="attributeDialog">
            <c:if test="${showAttributePopup}">
                <cti:flashScopeMessages />
                <%@ include file="editAttribute.jspf"%>
            </c:if>
        </div>

        <div id="fieldDialog">
            <c:if test="${showFieldPopup}">
                <cti:flashScopeMessages />
                <%@ include file="editField.jspf"%>
            </c:if>
        </div>
    </div>

    <form:form id="exporterForm" commandName="backingBean" action="${action}">
        <tags:setFormEditMode mode="${mode}" />
        <form:hidden path="format.formatId" />
        <form:hidden path="format.formatType" />
        <form:hidden path="rowIndex" />

        <!-- Helper Popups -->
        <dialog:inline nameKey="helper.rounding" okEvent="none" on="#helperRounding" options="{'modal' : false}">
            <cti:url var="roundingHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/roundingHelper.jsp" />
            <div class="helpDialogContents">
                <jsp:include page="${roundingHelperUrl}" />
            </div>
        </dialog:inline>

        <dialog:inline nameKey="helper.value" okEvent="none" on="#helperValue" options="{'modal' : false}">
            <cti:url var="valueHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/valueHelper.jsp" />
            <div class="helpDialogContents">
                <jsp:include page="${valueHelperUrl}" />
            </div>
        </dialog:inline>

        <dialog:inline nameKey="helper.timestamp" okEvent="none" on="#helperTimestamp" options="{'modal' : false}">
            <cti:url var="timestampHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/timestampHelper.jsp" />
            <div class="helpDialogContents">
                <jsp:include page="${timestampHelperUrl}" />
            </div>
        </dialog:inline>

        <tags:boxContainer2 nameKey="formatSettings" styleClass="stacked">
            <tags:nameValueContainer2>
                <tags:inputNameValue inputClass="f-initialFocus" nameKey=".nameOfFormat" path="format.formatName" size="50" maxlength="100" />
                <tags:nameValue2 nameKey=".delimiter">
                    <select name="delimiterSelect" id="delimiterSelect" data-selection="#delimiterSelect" data-field-to-display="#delimiter" data-skip-disable="true">
                        <option value="," <c:if test="${backingBean.format.delimiter == ','}"> selected="selected" </c:if>>
                            <cti:msg2 key=".comma" />
                        </option>
                        <option value=";" <c:if test="${backingBean.format.delimiter == ';'}"> selected="selected" </c:if>>
                            <cti:msg2 key=".semicolon" />
                        </option>
                        <option value=":" <c:if test="${backingBean.format.delimiter == ':'}"> selected="selected" </c:if>>
                            <cti:msg2 key=".colon" />
                        </option>
                        <option value=" " <c:if test="${backingBean.format.delimiter == ' '}"> selected="selected" </c:if>>
                            <cti:msg2 key=".spaceDelim" />
                        </option>
                        <option value="" <c:if test="${backingBean.format.delimiter == ''}"> selected="selected" </c:if>>
                            <cti:msg2 key=".noneDelim" />
                        </option>
                        <option value="Custom"
                            <c:if test="${backingBean.format.delimiter != ',' && 
                                backingBean.format.delimiter != ';' && 
                                backingBean.format.delimiter != ':' && 
                                backingBean.format.delimiter != ' ' &&
                                backingBean.format.delimiter != ''}"> selected="selected" </c:if>>
                            <cti:msg2 key=".custom" />
                        </option>
                    </select>
                    <tags:input id="delimiter" path="format.delimiter" size="1" maxlength="1" />
                </tags:nameValue2>
                <tags:inputNameValue nameKey=".header" path="format.header" size="100" maxlength="255" />
                <tags:inputNameValue nameKey=".footer" path="format.footer" size="100" maxlength="255" />

                <tags:nameValue2 nameKey=".timeZoneFormat">
                    <form:select path="format.dateTimeZoneFormat">
                        <c:forEach var="dateTZFormat" items="${dateTimeZoneFormats}">
                            <form:option value="${dateTZFormat}">
                                <cti:msg2 key="${dateTZFormat}" />
                            </form:option>
                        </c:forEach>
                    </form:select>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
        </tags:boxContainer2>

        <c:if test="${showAttributeSection}">
            <tags:boxContainer2 nameKey="attributeSetup" id="attributes" styleClass="stacked attributeSetupContainer">
                <c:set var="attributes" value="${backingBean.format.attributes}"/>
                <table class="compact-results-table nowrap">
                    <thead>
                        <tr>
                            <th><i:inline key=".attribute" /></th>
                            <th><i:inline key=".dataSelection" /></th>
                            <th><i:inline key=".daysPrevious" /></th>
                            <th><i:inline key=".actions" /></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="attribute" items="${attributes}" varStatus="row">
                            <tr data-row-index="${row.index}">
                                <td><tags:hidden path="format.attributes[${row.index}].attributeId" /> <tags:hidden path="format.attributes[${row.index}].formatId" /> <tags:hidden
                                        path="format.attributes[${row.index}].attribute" /> <tags:hidden path="format.attributes[${row.index}].dataSelection" /> <tags:hidden
                                        path="format.attributes[${row.index}].daysPrevious" /> <i:inline key="${attribute.attribute}" /></td>
                                <td><cti:msg2 key="${attribute.dataSelection}" />
                                </td>
                                <td>${fn:escapeXml(attribute.daysPrevious)}</td>
                                <td>
                                    <cti:button nameKey="edit" icon="icon-pencil" classes="editAttributeBtn f-blocker" renderMode="image" />
                                    <cti:button nameKey="remove" icon="icon-cross" classes="removeAttributeBtn f-blocker" renderMode="image" />
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <div class="action-area">
                    <cti:button id="addAttributeBtn" nameKey="add" classes="f-blocker" icon="icon-add"/>
                </div>
            </tags:boxContainer2>
        </c:if>

        <tags:boxContainer2 nameKey="fieldSetup" id="selectedFields" styleClass="stacked">
            <c:set var="fields" value="${backingBean.format.fields}"/>
            <table class="compact-results-table nowrap">
                <thead>
                    <tr>
                        <th><i:inline key=".field" /></th>
                        <th><i:inline key=".dataType" /></th>
                        <th><i:inline key=".dataSelection" /></th>
                        <th><i:inline key=".daysPrevious" /></th>
                        <th><i:inline key=".missingValue" /></th>
                        <th><i:inline key=".rounding" /></th>
                        <th><i:inline key=".pattern" /></th>
                        <th><i:inline key=".fieldSize" /></th>
                        <th><i:inline key=".padding" /></th>
                        <th><i:inline key=".actions" /></th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="field" items="${fields}" varStatus="row">
                        <tr data-row-index="${row.index}">
                            <td><spring:escapeBody htmlEscape="true"><i:inline key="${field}" /></spring:escapeBody></td>
                            <td><spring:escapeBody htmlEscape="true"><i:inline key="${field.attributeField}" /></spring:escapeBody></td>
                            <td><spring:escapeBody htmlEscape="true"><i:inline key="${field.attribute.dataSelection}" /></spring:escapeBody></td>
                            <td><spring:escapeBody htmlEscape="true"><c:if test="${not empty field.attributeField}">${field.attribute.daysPrevious}</c:if></spring:escapeBody></td>
                            <td><i:inline key="${field.missingAttribute}" />&nbsp&nbsp<spring:escapeBody htmlEscape="true">${field.missingAttributeValue}</spring:escapeBody></td>
                            <td><spring:escapeBody htmlEscape="true"><i:inline key="${field.roundingMode}" /></spring:escapeBody></td>
                            <td><span class="wsp">${fn:escapeXml(field.pattern)}</span></td>
                            <td><c:choose>
                                    <c:when test="${field.maxLength == 0}">
                                        <i:inline key=".noMax" />
                                    </c:when>
                                    <c:otherwise>
                                        <spring:escapeBody htmlEscape="true">${field.maxLength}</spring:escapeBody>
                                    </c:otherwise>
                                </c:choose></td>
                            <td><cti:msg2 key="${field.padSide}" />&nbsp&nbsp${field.padChar}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${row.first}">
                                        <cti:button nameKey="up.disabled" renderMode="image" disabled="true" icon="icon-bullet-go-up"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button nameKey="up" classes="upFieldBtn f-blocker" renderMode="image" icon="icon-bullet-go-up"/>
                                    </c:otherwise>
                                </c:choose> 
                                <c:choose>
                                    <c:when test="${row.last}">
                                        <cti:button nameKey="down.disabled" renderMode="image" disabled="true" icon="icon-bullet-go-down"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:button nameKey="down" classes="downFieldBtn f-blocker" renderMode="image" icon="icon-bullet-go-down"/>
                                    </c:otherwise>
                                </c:choose>
                                <cti:button nameKey="edit" icon="icon-pencil" classes="editFieldBtn f-blocker" renderMode="image" />
                                <cti:button nameKey="remove" classes="removeFieldBtn f-blocker" renderMode="image"  icon="icon-cross"/>
                            </td>
                            <tags:hidden path="format.fields[${row.index}].fieldId" />
                            <tags:hidden path="format.fields[${row.index}].attribute.attributeId" />
                            <tags:hidden path="format.fields[${row.index}].attribute.formatId" />
                            <tags:hidden path="format.fields[${row.index}].attribute.attribute" />
                            <tags:hidden path="format.fields[${row.index}].attribute.dataSelection" />
                            <tags:hidden path="format.fields[${row.index}].attribute.daysPrevious" />
                            <tags:hidden path="format.fields[${row.index}].fieldType" />
                            <tags:hidden path="format.fields[${row.index}].attributeField" />
                            <tags:hidden path="format.fields[${row.index}].pattern" />
                            <tags:hidden path="format.fields[${row.index}].maxLength" />
                            <tags:hidden path="format.fields[${row.index}].padChar" />
                            <tags:hidden path="format.fields[${row.index}].padSide" />
                            <tags:hidden path="format.fields[${row.index}].roundingMode" />
                            <tags:hidden path="format.fields[${row.index}].missingAttribute" />
                            <tags:hidden path="format.fields[${row.index}].missingAttributeValue" />
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <div class="action-area">
                <cti:button id="addFieldBtn" nameKey="add" classes="f-blocker" icon="icon-add"/>
            </div>
        </tags:boxContainer2>
        
        <c:if test="${not empty backingBean.format.fields}">
            <tags:boxContainer2 nameKey="preview">
               <div class="code">
            <!-- Please do not format this code -->
                <pre><c:forEach var="previewEntry" items="${preview}">${fn:escapeXml(previewEntry)}
</c:forEach></pre>
               </div>
            </tags:boxContainer2>
        </c:if>
        <div class="page-action-area">
            <cti:button id="saveBtn" nameKey="save" classes="f-blocker f-disable-after-click primary action"/>
            <dialog:confirm on="#deleteBtn" nameKey="confirmDelete" argument="${backingBean.format.formatName}" />
            <c:if test="${backingBean.format.formatId != 0}">
                <cti:button id="deleteBtn" nameKey="delete" />
            </c:if>

            <cti:button nameKey="cancel" href="view" />
        </div>
    </form:form>
</cti:standardPage>
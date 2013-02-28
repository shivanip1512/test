<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage page="archivedValueExporter.${mode}" module="amr">

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
        var fieldToDispay = jQuery(element.attr("data-field-to-display"));
        var skipDisable = element.attr("data-skip-disable");
        
        if(!skipDisable){
            fieldToDispay.attr("disabled", "disabled");
        }else{
            fieldToDispay.removeAttr("disabled");
        }
        
        if (selection == 'Custom' || selection == 'FIXED_VALUE'){
           //only reset the field value if the select element is the same one clicked
            if(initialized && event && event.currentTarget == element[0]){
                fieldToDispay.val("");
            }
            fieldToDispay.focus();
            fieldToDispay.show().removeAttr("disabled");
            jQuery("input[name=exportField\\.pattern]").each(function(index, element){
                if(element != fieldToDispay[0]){
                    element.value="";
                }
            });
        }else{
            fieldToDispay.hide();
            if(initialized){
                fieldToDispay.val(selection);
            }
        }
        
        if (selection == 'LEAVE_BLANK' || selection == 'SKIP_RECORD'){
            fieldToDispay.val("");
        }
    };

    function toggleField(element){
        var selection = jQuery(element.attr("data-selection")).val();
        var fieldToDispay = jQuery(element.attr("data-field-to-display"));
        var skipDisable = element.attr("data-skip-disable");
        
        if(!skipDisable){
            fieldToDispay.attr("disabled", "disabled");
        }else{
            fieldToDispay.removeAttr("disabled");
        }
        
        fieldToDispay.hide();
        if (selection == 'LEFT' || selection == 'RIGHT'){
            fieldToDispay.focus();
            fieldToDispay.show().removeAttr("disabled");
        }
        if (selection == 'LEAVE_BLANK' || selection == 'SKIP_RECORD'){
            fieldToDispay.val("");
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
        jQuery(".sectionContainer", element).show();
        
        jQuery(".f_renderFields tr").hide();
        jQuery(".f_renderFields tr").find("input,select,textarea").attr('disabled', 'disabled');
        jQuery(".f_renderFields " + query).show();
        jQuery(".f_renderFields " + query).find("input,select,textarea").removeAttr('disabled');
        
        jQuery(".sectionContainer", element).each(function(index, sectionContainer){
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
            var okButton = {'text' : '${okBtnMsg}'};
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
             var okButton = {'text' : '${okBtnMsg}'};
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
            Yukon.Dialog.ConfirmationManager.cancel();
            submitForm(-1, 'deleteFormat');
        });
    });
</script>

    <cti:includeScript link="/JavaScript/yukonGeneral.js" />

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
                <tags:inputNameValue nameKey=".nameOfFormat" path="format.formatName" size="50" maxlength="100" />
                <tags:nameValue2 nameKey=".delimiter">
                    <select name="delimiterSelect" id="delimiterSelect" data-selection="#delimiterSelect" data-field-to-display="#delimiter" data-skip-disable="true">
                        <option value="," <c:if test="${format.delimiter == ','}">selected="selected" </c:if>>
                            <i:inline key=".comma" />
                        </option>
                        <option value=";" <c:if test="${format.delimiter ==  ';'}">selected="selected" </c:if>>
                            <i:inline key=".semicolon" />
                        </option>
                        <option value=":" <c:if test="${format.delimiter  == ':'}">selected="selected" </c:if>>
                            <i:inline key=".colon" />
                        </option>
                        <option value="Custom"
                            <c:if test="${format.delimiter  !=  ',' && format.delimiter  != ';' && format.delimiter  != ':'}">selected="selected" </c:if>>
                            <i:inline key=".custom" />
                        </option>
                    </select>
                    <tags:input id="delimiter" path="format.delimiter" size="1" maxlength="1" />
                </tags:nameValue2>
                <tags:inputNameValue nameKey=".header" path="format.header" size="100" maxlength="255" />
                <tags:inputNameValue nameKey=".footer" path="format.footer" size="100" maxlength="255" />
            </tags:nameValueContainer2>
        </tags:boxContainer2>

        <c:if test="${showAttributeSection}">
            <tags:boxContainer2 nameKey="attributeSetup" id="attributes" styleClass="stacked attributeSetupContainer">
                <c:set var="attributes" value="${backingBean.format.attributes}"/>
                <table class="compactResultsTable nowrap">
                    <tr>
                        <th><i:inline key=".attribute" /></th>
                        <th><i:inline key=".dataSelection" /></th>
                        <th><i:inline key=".daysPrevious" /></th>
                        <th><i:inline key=".actions" /></th>
                    </tr>
    
                    <c:forEach var="attribute" items="${attributes}" varStatus="row">
                        <tr data-row-index="${row.index}">
                            <td><tags:hidden path="format.attributes[${row.index}].attributeId" /> <tags:hidden path="format.attributes[${row.index}].formatId" /> <tags:hidden
                                    path="format.attributes[${row.index}].attribute" /> <tags:hidden path="format.attributes[${row.index}].dataSelection" /> <tags:hidden
                                    path="format.attributes[${row.index}].daysPrevious" /> <i:inline key="${attribute.attribute}" /></td>
                            <td><cti:msg2 key="${attribute.dataSelection}" />
                            </td>
                            <td>${fn:escapeXml(attribute.daysPrevious)}</td>
                            <td><cti:button nameKey="edit" styleClass="editAttributeBtn f_blocker" renderMode="image" /> <cti:button nameKey="remove"
                                    styleClass="removeAttributeBtn f_blocker" renderMode="image" />
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td align="right" colspan="4"><br> <cti:button id="addAttributeBtn" nameKey="add" styleClass="f_blocker" />
                        </td>
                    </tr>
                </table>
            </tags:boxContainer2>
        </c:if>

        <tags:boxContainer2 nameKey="fieldSetup" id="selectedFields" styleClass="stacked">
            <c:set var="fields" value="${backingBean.format.fields}"/>
            <table class="compactResultsTable nowrap">
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
                <c:forEach var="field" items="${fields}" varStatus="row">
                    <tr data-row-index="${row.index}" class="<tags:alternateRow odd="" even="altRow"/>">
                        <td><spring:escapeBody htmlEscape="true"><i:inline key="${field}" /></spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true"><i:inline key="${field.attributeField}" /></spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true"><i:inline key="${field.attribute.dataSelection}" /></spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true"><c:if test="${not empty field.attributeField}">${field.attribute.daysPrevious}</c:if></spring:escapeBody></td>
                        <td><i:inline key="${field.missingAttribute}" />&nbsp&nbsp<spring:escapeBody htmlEscape="true">${field.missingAttributeValue}</spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true"><i:inline key="${field.roundingMode}" /></spring:escapeBody></td>
                        <td><spring:escapeBody htmlEscape="true">${field.pattern}</spring:escapeBody></td>
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
                                    <cti:button nameKey="up.disabled" renderMode="image" disabled="true"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:button nameKey="up" styleClass="upFieldBtn f_blocker" renderMode="image" />
                                </c:otherwise>
                            </c:choose> 
                            <c:choose>
                                <c:when test="${row.last}">
                                    <cti:button nameKey="down.disabled" renderMode="image" disabled="true"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:button nameKey="down" styleClass="downFieldBtn f_blocker" renderMode="image" />
                                </c:otherwise>
                            </c:choose>
                            <cti:button nameKey="edit" styleClass="editFieldBtn f_blocker" renderMode="image" />
                            <cti:button nameKey="remove" styleClass="removeFieldBtn f_blocker" renderMode="image" />
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
                <tr>
                    <td align="right" colspan="10"><br> <cti:button id="addFieldBtn" nameKey="add" styleClass="f_blocker" /></td>
                </tr>
            </table>
        </tags:boxContainer2>
        
        <c:if test="${not empty backingBean.format.fields}">
            <tags:boxContainer2 nameKey="preview">
               <div class="code">
            <!-- Please do not format this code -->
                <pre><c:forEach var="previewEntry" items="${preview}">${previewEntry}
</c:forEach></pre>
               </div>
            </tags:boxContainer2>
        </c:if>
        <div class="pageActionArea">
            <cti:button id="saveBtn" nameKey="save" styleClass="f_blocker" />
            <dialog:confirm on="#deleteBtn" nameKey="confirmDelete" argument="${backingBean.format.formatName}" />
            <c:if test="${backingBean.format.formatId != 0}">
                <cti:button id="deleteBtn" nameKey="delete" />
            </c:if>

            <cti:button nameKey="cancel" href="view" />
            <br>
        </div>
    </form:form>
</cti:standardPage>
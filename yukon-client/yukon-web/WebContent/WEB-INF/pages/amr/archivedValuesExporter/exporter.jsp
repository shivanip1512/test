<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:standardPage module="tools" page="bulk.archivedValueExporter.${mode}">

    <script type="text/javascript">
    function ajaxSubmitForm(rowIndex, action, dialogSelector) {
        $("#exporterForm input[name=rowIndex]").val(rowIndex);
        $('#exporterForm').ajaxSubmit({'target' : dialogSelector, 'url' : action});
    }
    
    function submitForm(index, action) {
        if (index != -2) {
            $("#exporterForm input[name=rowIndex]").val(index);
        }

        var exporterForm = $('#exporterForm');
        exporterForm.attr('action', action);
        exporterForm[0].submit();
    }
    
    function toggleFieldAndUpdateValue(element, event){
        var selection = $(element.attr("data-selection")).val();
        var fieldToDisplay = $(element.attr("data-field-to-display"));
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
            $("input[name=exportField\\.pattern]").each(function(index, element){
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
        var selection = $(element.attr("data-selection")).val();
        var fieldToDisplay = $(element.attr("data-field-to-display"));
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
        var selectedIndex = $("#fieldSelect")[0].selectedIndex;
        var selectedOption = $($("#fieldSelect")[0].options[selectedIndex]);
        var value = selectedOption.attr("data-fieldType");
        if(value == "ATTRIBUTE"){
            $("#exportField\\.attributeField").show();
            $("#exportField\\.attributeField").removeAttr("disabled");
            var secondValue = $("#exportField\\.attributeField").val();
            
            if(event && event.currentTarget.id == "exportField.attributeField"){
                switch(secondValue){
                case 'TIMESTAMP':
                case 'VALUE':
                    $("#patternSelect, #timestampPatternSelect").each(function(index, element){
                        element = $(element);
                        element.val(null);
                    });
                    break;
                default:
                    break;
                }
            }
            
            renderFields("." + value + "_" + secondValue);
        }else{
            $("#exportField\\.attributeField").hide();
            $("#exportField\\.attributeField").attr("disabled", "disabled");
            renderFields("." + value);
        }
    };
    
    function renderFields(query , element){
        $(".section-container", element).show();
        
        $(".f-renderFields tr").hide();
        $(".f-renderFields tr").find("input,select,textarea").attr('disabled', 'disabled');
        $(".f-renderFields " + query).show();
        $(".f-renderFields " + query).find("input,select,textarea").removeAttr('disabled');
        
        $(".section-container", element).each(function(index, sectionContainer){
            var container = $(sectionContainer);
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
      toggleFieldAndUpdateValue($("#unsupportedFieldSelect"), event);
      toggleFieldAndUpdateValue($("#padCharSelect"), event);
      toggleField($("#padSideSelect"));
      
      toggleFieldAndUpdateValue($("#patternSelect"), event);
      toggleFieldAndUpdateValue($("#timestampPatternSelect"), event);
   };
  
   function populateFieldDialog(data){
      for(key in data) {
          $("exportField\\."+key).val(data[key]);
      }
   };

    function renderContainer(){
        toggleFieldAndUpdateValue($("#delimiterSelect"));
    }
    
    var initialized = false;
    $(function() {
        <c:if test="${showAttributePopup}">
            <cti:msgScope paths=".${backingBean.pageNameKey},components.dialog.${backingBean.pageNameKey},components.dialog">
                <cti:msg2 var="titleMsg" key=".title"/>
                <cti:msg2 var="okBtnMsg" key=".ok"/>
                <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
            </cti:msgScope>
            var buttons = [];
            var okButton = {'text' : '${okBtnMsg}', 'class': 'primary'};
            okButton.click = function() { $('#attributeDialog').trigger('editAttributeOkPressed'); };
            buttons.push(okButton);
            buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { $(this).dialog('close'); }});
            
            var dialogOpts = {
                      'title' : '${titleMsg}',
                      'position' : 'center',
                      'width' : 'auto',
                      'height' : 'auto',
                      'modal' : true,
                      'buttons' : buttons };
            $('#attributeDialog').dialog(dialogOpts);
        </c:if>
        <c:if test="${showFieldPopup}">
            <cti:msgScope paths=".${backingBean.pageNameKey},components.dialog.${backingBean.pageNameKey},components.dialog">
               <cti:msg2 var="titleMsg" key=".title"/>
               <cti:msg2 var="okBtnMsg" key=".ok"/>
               <cti:msg2 var="cancelBtnMsg" key=".cancel"/>
             </cti:msgScope>
             var buttons = [];
             var okButton = {'text' : '${okBtnMsg}', 'class': 'primary'};
             okButton.click = function() { $('#fieldDialog').trigger('editFieldOkPressed'); };
             buttons.push(okButton);
             buttons.push({'text' : '${cancelBtnMsg}', 'click' : function() { $(this).dialog('close'); }});
             var dialogOpts = {
                  'title' : '${titleMsg}',
                  'position' : 'center',
                  'width' : 'auto',
                  'height' : 'auto',
                  'modal' : true,
                  'buttons' : buttons };
             var fieldDialogDiv = $('#fieldDialog');
             fieldDialogDiv.dialog(dialogOpts);
        </c:if>
        renderContainer();
        $(document).on('change', '#fieldDialog select', renderPopup);
        $(document).on('change', '#delimiterSelect', renderContainer);
        
        initialized = true;
        $('#addAttributeBtn').click(function() {
            ajaxSubmitForm(-1, 'ajaxEditAttribute', '#attributeDialog');
        });
        $('#addFieldBtn').click(function() {
            ajaxSubmitForm(-1, 'ajaxEditField', '#fieldDialog');
        });
        $('.editAttributeBtn').click(function(event) {
            var rowIndex = $(event.currentTarget).closest('tr').attr('data-row-index');
            ajaxSubmitForm(rowIndex, 'ajaxEditAttribute', '#attributeDialog');
        });
        $('.editFieldBtn').click(function(event) {
            var rowIndex = $(event.currentTarget).closest('tr').attr('data-row-index');
            ajaxSubmitForm(rowIndex, 'ajaxEditField', '#fieldDialog');
        });
        $('.removeAttributeBtn').click(function(event) {
            var rowIndex = $(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'removeAttribute');
        });
        $('.removeFieldBtn').click(function(event) {
            var rowIndex = $(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'removeField');
        });
        $('.downFieldBtn').click(function(event) {
            var rowIndex = $(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'moveFieldDown');
        });
        $('.upFieldBtn').click(function(event) {
            var rowIndex = $(event.currentTarget).closest('tr').attr('data-row-index');
            submitForm(rowIndex, 'moveFieldUp');
        });
        function editAttributeOkPressed() {
            $('#attributeDialog').dialog('close');
            $('#dialogFormElements').hide().remove().appendTo($('#exporterForm'));
            submitForm(-2, 'addAttribute');
        }
        $('#attributeDialog').bind('editAttributeOkPressed', editAttributeOkPressed);

        $("#attributeDialog").keydown(function (event) {
            if (13 === event.which) {
                editAttributeOkPressed();
            }
        });

        function editFieldOkPressed() {
            $('#fieldDialog').dialog('close');
            $('#dialogFieldFormElements').hide().remove().appendTo($('#exporterForm'));
            submitForm(-2, 'addField');
        }
        $('#fieldDialog').bind('editFieldOkPressed', editFieldOkPressed);

        $('.submitToName').click(function(event) {
            var exporterForm = $('#exporterForm');
            exporterForm.attr('action', $(event.currentTarget).attr('name'));
            exporterForm[0].submit();
        });
        $('#saveBtn').click(function(event) {
            submitForm(-1, 'saveFormat');
        });
        $(document).on('yukonDialogConfirmOk', '#yukon_dialog_confirm', function(event) {
            event.preventDefault();
            yukon.dialogConfirm.cancel();
            submitForm(-1, 'deleteFormat');
        });
        $('.f-initialFocus').focus();
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
        <d:inline nameKey="helper.rounding" okEvent="none" on="#helperRounding" options="{'modal' : false}">
            <div class="helpDialogContents">
                <jsp:include page="/WEB-INF/pages/amr/dynamicBilling/roundingHelper.jsp"/>
            </div>
        </d:inline>

        <d:inline nameKey="helper.value" okEvent="none" on="#helperValue" options="{'modal' : false}">
            <div class="helpDialogContents">
                <jsp:include page="/WEB-INF/pages/amr/dynamicBilling/valueHelper.jsp"/>
            </div>
        </d:inline>

        <d:inline nameKey="helper.timestamp" okEvent="none" on="#helperTimestamp" options="{'modal' : false}">
            <div class="helpDialogContents">
                <jsp:include page="/WEB-INF/pages/amr/dynamicBilling/timestampHelper.jsp"/>
            </div>
        </d:inline>

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
            <h4><i:inline key=".preview.title"/></h4>
            <pre><c:forEach var="previewEntry" items="${preview}">${fn:escapeXml(previewEntry)}</c:forEach></pre>
        </c:if>
        <div class="page-action-area">
            <cti:button id="saveBtn" nameKey="save" classes="f-blocker f-disable-after-click primary action"/>
            <d:confirm on="#deleteBtn" nameKey="confirmDelete" argument="${backingBean.format.formatName}" />
            <c:if test="${backingBean.format.formatId != 0}">
                <cti:button id="deleteBtn" nameKey="delete" />
            </c:if>

            <cti:button nameKey="cancel" href="view" />
        </div>
    </form:form>
</cti:standardPage>
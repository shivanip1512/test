<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jstl/xml"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>



<cti:standardPage page="archivedValueExporter.${mode}" module="amr">

    <script type="text/javascript">
    
    function fieldChanged(type) {
       alert(type);
    }
    
    function showPopup(popup_id) {
        jQuery('#'+popup_id).show();
        renderPopup();
    }

    function hidePopup(popup_id) {
        jQuery('#'+popup_id).hide();
    }
    
    function prepForm(index, action){
        jQuery("#exporterForm input[name=rowIndex]").val(index);
        jQuery("#exporterForm").append('<input type="hidden" name="'+ action +'"/>');
        jQuery("#exporterForm")[0].submit();
    }
    
    function toggleFieldAndUpdateValue(element){
        var selection = jQuery(element.attr("data-selection")).val();
        var fieldToDispay = jQuery(element.attr("data-field-to-display"));
        var skipDisable = element.attr("data-skip-disable");
        if(!skipDisable){
            fieldToDispay.attr("disabled", "disabled");
        }else{
            fieldToDispay.removeAttr("disabled");
        }
        
        fieldToDispay.hide();
        if(initialized){
            fieldToDispay.val(selection);
        }
        if (selection == 'Custom' || selection == 'Fixed Value'){
            if(initialized){
                fieldToDispay.val("");
            }
            fieldToDispay.focus();
            fieldToDispay.show().removeAttr("disabled");
        }
    }
    
    function toggleField(element){
        var selection = jQuery(element.attr("data-selection")).val();
        var fieldToDispay = jQuery(element.attr("data-field-to-display"));
        var skipDisable = element.attr("data-skip-disable");
        
        if(!skipDisable){
            fieldToDispay.attr("disabled", "disabled");
        }else{
            fieldToDispay.removeAttr("disabled")
        }
        
        fieldToDispay.hide();
        if (selection == 'Left' || selection == 'Right'){
            fieldToDispay.focus();
            fieldToDispay.show().removeAttr("disabled");
        }
    }
    
    function changeFieldSelect(){
        var selectedIndex = jQuery("#fieldSelect")[0].selectedIndex;
        var selectedOption = jQuery(jQuery("#fieldSelect")[0].options[selectedIndex]);
        var value = selectedOption.attr("data-fieldType");
        if(value == "ATTRIBUTE"){
            jQuery("#exportField\\.attributeField").show();
            jQuery("#exportField\\.attributeField").removeAttr("disabled");
            var secondValue = jQuery("#exportField\\.attributeField").val();
            renderFields("." + value + "_" + secondValue);
        }else{
            jQuery("#exportField\\.attributeField").hide();
            jQuery("#exportField\\.attributeField").attr("disabled", "disabled")
            renderFields("." + value);
        }
    };
    
    function renderFields(query){
        renderFields(query , document.getElementById("addFieldPopup"));
        renderFields(query , document.getElementById("formatContainer"));
    }
    
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
    
    //Render popup form back to front, bottom to top
    function renderPopup(){        
        //Define Layout
        toggleFieldAndUpdateValue(jQuery("#unsupportedFieldSelect"));
        toggleFieldAndUpdateValue(jQuery("#padCharSelect"));
        toggleField(jQuery("#padSideSelect"));
        toggleFieldAndUpdateValue(jQuery("#patternSelect"));
        toggleFieldAndUpdateValue(jQuery("#timestampPatternSelect"));
        
   
        //Select Field
        changeFieldSelect();
    };
    
    function renderContainer(){
        toggleFieldAndUpdateValue(jQuery("#delimiterSelect"));
    }
    var initialized = false;
    jQuery(document).ready(function() {
        <c:if test="${backingBean.popupToOpen != null}">
              showPopup("${backingBean.popupToOpen}");
        </c:if>
        renderContainer();
        jQuery("select", document.getElementById("addFieldPopup")).live('change', renderPopup);
        jQuery("#delimiterSelect").live('change', renderContainer);
        initialized = true;
    });
</script>

    <cti:includeScript link="/JavaScript/yukonGeneral.js" />
    <cti:includeScript link="SCRIPTACULOUS_EFFECTS" />

    <form:form id="exporterForm" commandName="backingBean" action="${action}">

        <tags:setFormEditMode mode="${mode}" />
        <form:hidden path="format.formatId" />
        <tags:hidden path="rowIndex" />


        <cti:url value="/spring/amr/archivedValuesExporter/view" var="viewUrl" />

        <!-- Helper Popups -->
        <i:simplePopup id="roundingHelper" titleKey=".roundingModeHelp" onClose="hidePopup('roundingHelper');" styleClass="secondLevelPopup">
            <cti:url var="roundingHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/roundingHelper.jsp" />
            <jsp:include page="${roundingHelperUrl}" />
        </i:simplePopup>

        <i:simplePopup id="readingHelper" titleKey=".formatHelp" onClose="hidePopup('readingHelper');" styleClass="secondLevelPopup">
            <cti:url var="valueHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/valueHelper.jsp" />
            <jsp:include page="${valueHelperUrl}" />
        </i:simplePopup>

        <i:simplePopup id="timestampHelper" titleKey=".formatHelp" onClose="hidePopup('timestampHelper');" styleClass="secondLevelPopup">
            <cti:url var="timestampHelperUrl" value="/WEB-INF/pages/amr/dynamicBilling/timestampHelper.jsp" />
            <jsp:include page="${timestampHelperUrl}" />
        </i:simplePopup>
        <!-- Add Attribute Popup -->
        <i:simplePopup titleKey=".addAttribute" id="addAttributePopup">
            <tags:nameValueContainer2>
                <tags:selectNameValue nameKey=".attribute" path="exportAttribute.attribute" items="${attributes}" itemLabel="description" itemValue="key" />
                <tags:selectNameValue items="${dataSelection}" nameKey=".dataSelection" path="exportAttribute.dataSelection" />
                <tags:inputNameValue nameKey=".daysPrevious" path="exportAttribute.daysPrevious"></tags:inputNameValue>
            </tags:nameValueContainer2>
            <div class="actionArea">
                <cti:button nameKey="save" type="submit" name="addAttribute" />
                <cti:button nameKey="cancel" onclick="hidePopup('addAttributePopup')" />
            </div>
        </i:simplePopup>

        <!-- Add Field Popup -->
        <i:simplePopup titleKey=".addField" id="addFieldPopup">
            <div class="smallBoldLabel notesSection">
                <i:inline key=".note" />
            </div>
            <tags:sectionContainer2 nameKey="selectField">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".field">
                        <form:select id="fieldSelect" path="selectedFieldId">
                            <c:forEach var="field" items="${backingBean.fieldSelect}">
                                <form:option value="${field.fieldId}" data-fieldType="${field.type.key}" title="${field.displayName}">
                                    ${field.displayName}
                                </form:option>
                            </c:forEach>
                        </form:select>
                        <form:select path="exportField.attributeField">
                            <c:forEach var="attributeField" items="${attributeFields}">
                                <form:option value="${attributeField}" title="${attributeField}">
                                    <i:inline key="${attributeField}" />
                                </form:option>
                            </c:forEach>
                        </form:select>
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="defineLayout" styleClass="f_renderFields">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".roundingMode" rowClass="ATTRIBUTE_VALUE">
                        <form:select path="exportField.roundingMode">
                            <c:forEach var="roundingMode" items="${roundingModes}">
                                <form:option value="${roundingMode}" title="${roundingMode}">${roundingMode}</form:option>
                            </c:forEach>
                        </form:select>
                        <a href="javascript:void(0);" onClick="showPopup('valueHelper');"><cti:img nameKey="helpRoundingMode" styleClass="hoverableImage" /> </a>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".readingPattern" rowClass="ATTRIBUTE_VALUE">
                        <select name="patternSelect" id="patternSelect" data-selection="#patternSelect" data-field-to-display="#readingPattern">
                            <option value="" <c:if test="${empty backingBean.exportField.pattern}">selected="selected" </c:if>>
                                <i:inline key=".noFormat" />
                            </option>
                            <option value="###.###" <c:if test="${backingBean.exportField.pattern ==  '###.###'}">selected="selected" </c:if>>
                                <i:inline key=".###.###" />
                            </option>
                            <option value="####.##" <c:if test="${backingBean.exportField.pattern  == '####.##'}">selected="selected" </c:if>>
                                <i:inline key=".####.##" />
                            </option>
                            <option value="Custom"
                                <c:if test="${backingBean.exportField.pattern  !=  '####.##' && not empty backingBean.exportField.pattern && backingBean.exportField.pattern  != '###.###'}">selected="selected" </c:if>>
                                <i:inline key=".custom" />
                            </option>
                        </select>
                        <tags:input id="readingPattern" path="exportField.pattern" size="10" maxlength="30" inputClass="Custom" />
                        <a href="javascript:void(0);" onClick="showPopup('readingHelper');"> <cti:img nameKey="helpReadingPattern" styleClass="hoverableImage" /> </a>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".timestampPattern" rowClass="ATTRIBUTE_TIMESTAMP">
                        <select id="timestampPatternSelect" data-selection="#timestampPatternSelect" data-field-to-display="#timestampPattern">
                            <option value="dd/MM/yyyy" <c:if test="${backingBean.exportField.pattern ==  'dd/MM/yyyy' || empty backingBean.exportField.pattern}">selected="selected" </c:if>>
                                <i:inline key=".dd/MM/yyyy" />
                            </option>
                            <option value="MM/dd/yyyy" <c:if test="${backingBean.exportField.pattern  == 'MM/dd/yyyy'}">selected="selected" </c:if>>
                                <i:inline key=".MM/dd/yyyy" />
                            </option>
                            <option value="hh:mm:ss a" <c:if test="${backingBean.exportField.pattern  == 'hh:mm:ss a'}">selected="selected" </c:if>>
                                <i:inline key=".hh:mm:ss a" />
                            </option>
                            <option value="HH:mm:ss" <c:if test="${backingBean.exportField.pattern  == 'HH:mm:ss'}">selected="selected" </c:if>>
                                <i:inline key=".HH:mm:ss" />
                            </option>
                            <option value="Custom"
                                <c:if test="${backingBean.exportField.pattern  !=  'dd/MM/yyyy' && not empty backingBean.exportField.pattern  && backingBean.exportField.pattern  != 'MM/dd/yyyy' && backingBean.exportField.pattern  != 'hh:mm:ss a' && backingBean.exportField.pattern  != 'HH:mm:ss'}">selected="selected" </c:if>>
                                <i:inline key=".custom" />
                            </option>
                        </select>
                        <tags:input id="timestampPattern" path="exportField.pattern" size="10" maxlength="30" inputClass="Custom" />
                        <a href="javascript:void(0);" onClick="showPopup('timestampHelper');"> <cti:img nameKey="helpTimestampPattern" styleClass="hoverableImage" /> </a>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".fieldSize" rowClass="METER_NUMBER DEVICE_NAME DLC_ADDRESS RF_MANUFACTURER RF_MODEL RF_SERIAL_NUMBER ATTRIBUTE_TIMESTAMP ATTRIBUTE_TIMESTAMP">
                        <tags:input id="exportField.maxLength" path="exportField.maxLength" size="5" maxlength="5" />
                        <i:inline key=".fieldSizeMax" />
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".padding" rowClass="METER_NUMBER DEVICE_NAME DLC_ADDRESS RF_MANUFACTURER RF_MODEL RF_SERIAL_NUMBER ATTRIBUTE_TIMESTAMP ATTRIBUTE_VALUE">
                        <form:select id="padSideSelect" path="exportField.padSide" data-selection="#padSideSelect" data-field-to-display="#padCharSelectFields">
                            <option value="" <c:if test="${backingBean.exportField.padSide  !=  'Left' && backingBean.exportField.padSide != 'Right'}">selected="selected" </c:if>>
                                <i:inline key=".none" />
                            </option>
                            <option value="Left" <c:if test="${backingBean.exportField.padSide == 'Left'}">selected="selected" </c:if>>
                                <i:inline key=".left" />
                            </option>
                            <option value="Right" <c:if test="${backingBean.exportField.padSide == 'Right'}">selected="selected" </c:if>>
                                <i:inline key=".right" />
                            </option>
                        </form:select>&nbsp
                        <div id="padCharSelectFields" class="di">
                            <select id="padCharSelect" data-selection="#padCharSelect" data-field-to-display="#padChar">

                                <option value="" <c:if test="${backingBean.exportField.padChar == ' '}">selected="selected" </c:if>>
                                    <i:inline key=".space" />
                                </option>
                                <option value="0" <c:if test="${backingBean.exportField.padChar == '0'}">selected="selected" </c:if>>
                                    <i:inline key=".zero" />
                                </option>
                                <option value="Custom" <c:if test="${backingBean.exportField.padChar  !=  '0' && not empty backingBean.exportField.padChar}">selected="selected" </c:if>>
                                    <i:inline key=".custom" />
                                </option>
                            </select>
                            <tags:input id="padChar" path="exportField.padChar" size="1" maxlength="1 " inputClass="Custom" />
                        </div>
                    </tags:nameValue2>
                    <tags:inputNameValue nameKey=".plainTextInput" rowClass="PLAIN_TEXT" path="exportField.pattern" size="25" maxlength="50" />
                </tags:nameValueContainer2>
            </tags:sectionContainer2>
            <tags:sectionContainer2 nameKey="otherOptions" styleClass="f_renderFields">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".unsupportedField"
                        rowClass="METER_NUMBER DEVICE_NAME DLC_ADDRESS RF_MANUFACTURER RF_MODEL RF_SERIAL_NUMBER ATTRIBUTE ATTRIBUTE_TIMESTAMP ATTRIBUTE_VALUE ATTRIBUTE_U_OF_M">
                        <select id="unsupportedFieldSelect" data-selection="#unsupportedFieldSelect" data-field-to-display="#fixedValue">
                            <option value="Leave Blank"
                                <c:if test="${backingBean.exportField.missingAttributeValue == 'Leave Blank'}">selected="selected" </c:if>>
                                <i:inline key=".leaveBlank" />
                            </option>
                            <option value="Skip Record" <c:if test="${backingBean.exportField.missingAttributeValue == 'Skip Record'}">selected="selected" </c:if>>
                                <i:inline key=".skipRecord" />
                            </option>
                            <option value="Fixed Value"
                                <c:if test="${backingBean.exportField.missingAttributeValue  !=  'Leave Blank' && backingBean.exportField.missingAttributeValue != 'Skip Record'}">selected="selected" </c:if>>
                                <i:inline key=".fixedValue" />
                            </option>
                        </select>
                        <tags:input id="fixedValue" path="exportField.missingAttributeValue" size="10" maxlength="30" />
                    </tags:nameValue2>
                    <tags:checkboxNameValue nameKey=".removeMultiplier" path="exportField.multiplierRemovedFlag" id="multiplierRemovedCheckbox" rowClass="ATTRIBUTE_VALUE" />
                </tags:nameValueContainer2>
            </tags:sectionContainer2>

            <div class="actionArea">
                <cti:button nameKey="save" type="submit" name="addField" styleClass="f_blocker" />
                <cti:button nameKey="cancel" onclick="hidePopup('addFieldPopup')" />
            </div>
        </i:simplePopup>
        <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
            <cti:dataGridCell>
                <cti:displayForPageEditModes modes="VIEW">
                    <tags:sectionLink>
                        <a href="/spring/amr/archivedValuesExporter/deviceSelection"><i:inline key=".selectDevice" /> </a>
                    </tags:sectionLink>
                    <br>
                    <div class="smallBoldLabel notesSection">
                        <c:choose>
                            <c:when test="${deviceCollection ==  null}">
                                <i:inline key=".noSelectedDevice" />
                            </c:when>
                            <c:otherwise>
                                <tags:selectedDevices id="deviceColletion" deviceCollection="${deviceCollection}" />
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <tags:nameValueContainer2 id="formatContainer">
                        <tags:nameValue2 nameKey=".format">
                            <c:if test="${not empty backingBean.allFormats}">
                                <form:select path="selectedFormatId" onchange="prepForm(-1, 'view')">
                                    <c:forEach var="format" items="${backingBean.allFormats}">
                                        <form:option value="${format.formatId}" title="${format.formatId}">${format.formatName}</form:option>
                                    </c:forEach>
                                </form:select>
                            </c:if>
           
                           &nbsp<span style="color: #CC0000;">*</span>
                            &nbsp <c:if test="${not empty backingBean.allFormats}">
                                <cti:button name="copy" nameKey="copy" type="submit" />
                            </c:if>&nbsp<cti:button name="create" nameKey="create" type="submit" />&nbsp
                            <c:if test="${not empty backingBean.allFormats}">
                                <cti:button name="edit" nameKey="edit" type="submit" />
                            </c:if>
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    <br>
                    <c:if test="${not empty backingBean.allFormats}">
                        <tags:boxContainer2 nameKey="generateReport">
                            <tags:nameValueContainer2>
                                <tags:nameValue2 nameKey=".endDate">
                                    <tags:dateInputCalendar fieldName="endDate" fieldValue="${origEndDate}"></tags:dateInputCalendar>
                    <i:inline key=".cdt" />
                                    <cti:button name="generateReport" nameKey="generateReport" type="submit" disabled="${ empty deviceCollection }" />
                                </tags:nameValue2>
                            </tags:nameValueContainer2>
                        </tags:boxContainer2>
                    </c:if>

                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="VIEW">
                    <c:if test="${not empty backingBean.allFormats}">
                        <br>
                        <tags:boxContainer2 nameKey="preview">
                   ${backingBean.preview}
        </tags:boxContainer2>
                        <br>
                    </c:if>
                </cti:displayForPageEditModes>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <tags:boxContainer2 nameKey="formatSettings">
                        <tags:nameValueContainer2>
                            <tags:inputNameValue nameKey=".nameOfFormat" path="format.formatName" size="50" maxlength="100" />
                            <tags:nameValue2 nameKey=".delimiter">
                                <select name="delimiterSelect" id="delimiterSelect" data-selection="#delimiterSelect" data-field-to-display="#delimiter" data-skip-disable="true">
                                    <option value="," <c:if test="${backingBean.format.delimiter == ','}">selected="selected" </c:if>>
                                        <i:inline key=".comma" />
                                    </option>
                                    <option value=";" <c:if test="${backingBean.format.delimiter ==  ';'}">selected="selected" </c:if>>
                                        <i:inline key=".semicolon" />
                                    </option>
                                    <option value=":" <c:if test="${backingBean.format.delimiter  == ':'}">selected="selected" </c:if>>
                                        <i:inline key=".colon" />
                                    </option>
                                    <option value="Custom"
                                        <c:if test="${backingBean.format.delimiter  !=  ',' && backingBean.format.delimiter  != ';' && backingBean.format.delimiter  != ':'}">selected="selected" </c:if>>
                                        <i:inline key=".custom" />
                                    </option>
                                </select>
                                <tags:input id="delimiter" path="format.delimiter" size="1" maxlength="1" />
                            </tags:nameValue2>
                            <tags:inputNameValue nameKey=".header" path="format.header" size="100" maxlength="255" />
                            <tags:inputNameValue nameKey=".footer" path="format.footer" size="100" maxlength="255" />
                        </tags:nameValueContainer2>
                    </tags:boxContainer2>
                    <br>
                    <tags:boxContainer2 nameKey="attributeSetup" id="attributes">
                        <table class="compactResultsTable">
                            <tr>
                                <th class="nonwrapping"><i:inline key=".attribute" /></th>
                                <th class="nonwrapping"><i:inline key=".dataSelection" /></th>
                                <th class="nonwrapping"><i:inline key=".daysPrevious" /></th>
                                <cti:displayForPageEditModes modes="CREATE,EDIT">
                                    <th class="nonwrapping"><i:inline key=".actions" /></th>
                                </cti:displayForPageEditModes>
                            </tr>
                            <c:set var="format" value="${backingBean.format}" />
                            <c:forEach var="attribute" items="${backingBean.format.attributes}" varStatus="row">
                                <tags:hidden path="format.attributes[${row.index}].attributeId" />
                                <tags:hidden path="format.attributes[${row.index}].formatId" />
                                <tags:hidden path="format.attributes[${row.index}].attribute" />
                                <tags:hidden path="format.attributes[${row.index}].dataSelection" />
                                <tags:hidden path="format.attributes[${row.index}].daysPrevious" />
                                <tr>
                                    <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${attribute.attribute.description}</spring:escapeBody>
                                    </td>
                                    <td class="nonwrapping"><spring:escapeBody htmlEscape="true">
                                            <cti:msg2 key="${attribute.dataSelection}" />
                                        </spring:escapeBody>
                                    </td>
                                    <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${attribute.daysPrevious}</spring:escapeBody>
                                    </td>
                                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                                        <td class="nonwrapping"><cti:button nameKey="edit" onclick="prepForm(${row.index}, 'editAttribute')" type="button" renderMode="image" /> <cti:button
                                                nameKey="remove" onclick="prepForm(${row.index}, 'removeAttribute')" type="button" renderMode="image" /></td>
                                    </cti:displayForPageEditModes>
                                </tr>
                            </c:forEach>
                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <tr>
                                    <td align="right" colspan="4"><br> <cti:button name="addNewAttribute" nameKey="add" type="submit" />
                                    </td>
                                </tr>
                            </cti:displayForPageEditModes>
                        </table>
                    </tags:boxContainer2>
                    <br>
                </cti:displayForPageEditModes>
            </cti:dataGridCell>
        </cti:dataGrid>
        <c:if test="${(not empty backingBean.allFormats && mode == 'VIEW') || (mode != 'VIEW')}">
            <tags:boxContainer2 nameKey="fieldSetup" id="selectedFields">
                <table class="compactResultsTable">
                    <tr>
                        <th class="nonwrapping"><i:inline key=".field" /></th>
                        <th class="nonwrapping"><i:inline key=".multiplier" /></th>
                        <th class="nonwrapping"><i:inline key=".missingValue" /></th>
                        <th class="nonwrapping"><i:inline key=".roundingMode" /></th>
                        <th class="nonwrapping"><i:inline key=".pattern" /></th>
                        <th class="nonwrapping"><i:inline key=".fieldSize" /></th>
                        <th class="nonwrapping"><i:inline key=".padding" /></th>
                        <cti:displayForPageEditModes modes="CREATE,EDIT">
                            <th class="nonwrapping"><i:inline key=".actions" /></th>
                        </cti:displayForPageEditModes>
                    </tr>
                    <c:forEach var="field" items="${backingBean.format.fields}" varStatus="row">
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
                        <tags:hidden path="format.fields[${row.index}].missingAttributeValue" />
                        <tags:hidden path="format.fields[${row.index}].multiplierRemovedFlag" />
                        <tr>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">
                                    <c:choose>
                                        <c:when test="${field.fieldType != 'ATTRIBUTE'}">
                                            ${field.fieldType.description}
                                        </c:when>
                                        <c:otherwise>
                                            ${field.attribute.attribute.description} - <cti:msg2 key="${field.attributeField}" />
                                        </c:otherwise>
                                    </c:choose>
                                </spring:escapeBody>
                            </td>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">
                                    <c:if test="${field.attributeField.key == 'VALUE'}">
                                        <c:choose>
                                            <c:when test="${field.multiplierRemovedFlag == 'true'}">
                                                <i:inline key=".unused" />
                                            </c:when>
                                            <c:otherwise>
                                                <i:inline key=".applied" />
                                            </c:otherwise>
                                        </c:choose>
                                    </c:if>
                                </spring:escapeBody>
                            </td>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${field.missingAttributeValue}</spring:escapeBody>
                            </td>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${field.roundingMode}</spring:escapeBody>
                            </td>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${field.pattern}</spring:escapeBody>
                            </td>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">
                                    <c:choose>
                                        <c:when test="${field.maxLength == 0}"> <i:inline key=".noMax" /></c:when>
                                        <c:otherwise> ${field.maxLength}</c:otherwise>
                                    </c:choose>
                                </spring:escapeBody></td>
                            <td class="nonwrapping"><spring:escapeBody htmlEscape="true">${field.padSide}&nbsp&nbsp${field.padChar}</spring:escapeBody>
                            </td>
                            <cti:displayForPageEditModes modes="CREATE,EDIT">
                                <td class="nonwrapping"><c:choose>
                                        <c:when test="${row.index == 0}">
                                            <cti:button nameKey="up.disabled" onclick="prepForm(${row.index}, 'moveFieldUp')" type="button" renderMode="image" disabled="true" />
                                        </c:when>
                                        <c:otherwise>
                                            <cti:button nameKey="up" onclick="prepForm(${row.index}, 'moveFieldUp')" type="button" renderMode="image" />
                                        </c:otherwise>
                                    </c:choose> <c:choose>
                                        <c:when test="${row.index == fn:length(backingBean.format.fields)-1}">
                                            <cti:button nameKey="down.disabled" onclick="prepForm(${row.index}, 'moveFieldDown')" type="button" renderMode="image" disabled="true" />
                                        </c:when>
                                        <c:otherwise>
                                            <cti:button nameKey="down" onclick="prepForm(${row.index}, 'moveFieldDown')" type="button" renderMode="image" />
                                        </c:otherwise>
                                    </c:choose> <cti:button nameKey="edit" onclick="prepForm(${row.index}, 'editField')" type="button" renderMode="image" /> <cti:button nameKey="remove"
                                        onclick="prepForm(${row.index}, 'removeField')" type="button" renderMode="image" /></td>
                            </cti:displayForPageEditModes>
                        </tr>
                    </c:forEach>
                    <cti:displayForPageEditModes modes="CREATE,EDIT">
                        <tr>
                            <td align="right" colspan="9"><br> <cti:button name="addNewField" nameKey="add" type="submit" />
                            </td>
                        </tr>
                    </cti:displayForPageEditModes>
                </table>
            </tags:boxContainer2>
        </c:if>
        <cti:displayForPageEditModes modes="CREATE,EDIT">
            <br>
            <tags:boxContainer2 nameKey="preview">
               ${backingBean.preview}
        </tags:boxContainer2>
            <div class="pageActionArea">
                <cti:button name="saveFormat" nameKey="save" type="submit" styleClass="f_blocker" />
                <c:if test="${backingBean.format.formatId != 0}">
                    <cti:button name="deleteFormat" nameKey="delete" type="submit" styleClass="f_blocker" />
                </c:if>
                <cti:button nameKey="cancel" href="${viewUrl}" />
                <br>
            </div>
        </cti:displayForPageEditModes>
    </form:form>
</cti:standardPage>
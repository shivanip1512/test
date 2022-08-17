<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.components.image">
 
 <tr data-point-picker-id="calcPoint${nextIndex}Picker" class="js-add-calc-row">
        <form:hidden path="pointModel.pointBase.calcComponents[${nextIndex}].componentOrder" value="${nextIndex + 1}" class="js-component-order"/>
        <form:hidden path="pointModel.pointBase.calcComponents[${nextIndex}].pointID" value="${pointModel.id}"/>
        <td>
            <tags:selectWithItems path="pointModel.pointBase.calcComponents[${nextIndex}].componentType" items="${types}" inputClass="js-component-type"/>
        </td>
        <td>
            <span class="js-constant dn"><tags:input path="pointModel.pointBase.calcComponents[${nextIndex}].constant" inputClass="js-constant-value"/></span>
            <span class="js-point-picker">
                <form:hidden id="calc-component-point-${nextIndex}-input"
                    path="pointModel.pointBase.calcComponents[${nextIndex}].componentPointID" />
                <tags:pickerDialog
                    id="calcPoint${nextIndex}Picker"
                    type="notSystemPointPicker"
                    linkType="selectionLabel"
                    selectionProperty="paoPoint"
                    buttonStyleClass="M0"
                    destinationFieldId="calc-component-point-${nextIndex}-input"
                    viewOnlyMode="${false}"
                    includeRemoveButton="${true}"
                    removeValue="0" />
            </span>
        </td>
        <td>
            <span class="js-function-operations dn"><tags:selectWithItems path="pointModel.pointBase.calcComponents[${nextIndex}].functionName" items="${functionOperators.yukonListEntries}" inputClass="js-function-options" defaultItemValue="(none)" defaultItemLabel="(none)"/></span>
            <span class="js-operations"><tags:selectWithItems path="pointModel.pointBase.calcComponents[${nextIndex}].operation" items="${operators}" inputClass="js-operation-options" /></span>
        </td>
        <td class="js-baseline dn">
            <span class="js-baseline-picker dn">
                <form:hidden path="pointModel.pointBase.calcBaselinePoint.pointID" id="calBasePointId" />
                <tags:selectWithItems path="pointModel.pointBase.calcBaselinePoint.baselineID" items="${baseLines}" itemValue="baselineID" inputClass="js-baseline-options"/>
            </span>
        </td>
        <td>
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <div class="button-group fr wsnw oh">
                <cti:button icon="icon-cross" renderMode="buttonImage" classes="js-remove-calc"/>
                <cti:button icon="icon-bullet-go-up" renderMode="buttonImage" classes="js-up"/>
                <cti:button icon="icon-bullet-go-down" renderMode="buttonImage" classes="js-down" disabled="true"/>
            </div>      
            </cti:displayForPageEditModes> 
        </td>
    </tr>
    
    </cti:msgScope>
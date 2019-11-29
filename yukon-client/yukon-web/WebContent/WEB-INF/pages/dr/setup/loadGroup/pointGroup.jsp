<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<input type="hidden" class="js-is-point-group-selected" value="${isPointGroupSelected}">
<input type="hidden" class="js-create-mode" value="${isCreateMode}">
<input type="hidden" class="js-edit-mode" value="${isEditMode}">
<input type="hidden" class="js-device-error" value="${deviceIdUsageHasError}">
<tags:sectionContainer2 nameKey="pointGroup">
    <tags:nameValueContainer2 tableClass="spaced-form-controls" >
        <tags:nameValue2 nameKey=".controlDevice">
            <c:choose>
                <c:when test="${not isViewMode}">
                    <div class="${deviceIdUsageHasError ? 'MT5 MB5' : ''}">
                        <form:hidden id="js-control-device-selected" path="deviceUsage.id"/>
                        <form:hidden id="js-control-point-selected" path="pointUsage.id"/>
                        <tags:pickerDialog id="pointGroupControlDevicePicker" 
                                           type="pointGroupControlDevicePicker" 
                                           destinationFieldId="js-control-point-selected"
                                           linkType="selection" 
                                           selectionProperty="paoPoint"
                                           endEvent="yukon:pointGroup:point:selected"
                                           initialId="${loadGroup.pointUsage.id}"/>
                        <br>
                        <form:errors path="deviceUsage.id" cssClass="error"/>
                    </div>
                </c:when>
                <c:otherwise>
                    ${fn:escapeXml(loadGroup.deviceUsage.name)}:&nbsp;${fn:escapeXml(loadGroup.pointUsage.name)}
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>

        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:nameValue2 nameKey=".controlStartState" id="js-start-state" rowClass="dn">
                <tags:selectWithItems items="${startStates}" path="startControlRawState.rawState" 
                                      defaultItemValue="0" id="js-control-start-state"
                                      itemLabel="stateText" itemValue="rawState"/>
            </tags:nameValue2>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="VIEW">
            <tags:nameValue2 nameKey=".controlStartState">
                <cti:pointStatus pointId="${loadGroup.pointUsage.id}" rawState="${loadGroup.startControlRawState.rawState}"/>&nbsp;
                ${fn:escapeXml(loadGroup.startControlRawState.stateText)}
            </tags:nameValue2>
        </cti:displayForPageEditModes>

    </tags:nameValueContainer2>
</tags:sectionContainer2>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<input type="hidden" class="js-point-group-page-mode" value="${mode}">
<input type="hidden" class="js-group-type" value="${isPointGroupSelected}">
<input type="hidden" class="js-create-mode" value="${isCreateMode}">
<input type="hidden" class="js-edit-mode" value="${isEditMode}">
<input type="hidden" class="js-device-error" value="${deviceIdUsageHasError}">
<tags:sectionContainer2 nameKey="pointGroup">
    <tags:nameValueContainer2 tableClass="spaced-form-controls" >
        <tags:nameValue2 nameKey=".controlDevice">
            <c:choose>
                <c:when test="${not isViewMode}">
                    <div class="${deviceIdUsageHasError ? 'MT5 MB5' : ''}">
                        <form:hidden id="js-control-device-selected" path="deviceIdUsage"/>
                        <form:hidden id="js-control-point-selected" path="pointIdUsage"/>
                        <tags:pickerDialog id="pointGroupControlDevicePicker" 
                                           type="pointGroupControlDevicePicker" 
                                           destinationFieldId="js-control-point-selected"
                                           linkType="selection" 
                                           selectionProperty="paoPoint"
                                           endEvent="yukon:pointGroup:point:selected"
                                           initialId="${loadGroup.pointIdUsage}"/>
                        <br>
                        <form:errors path="deviceIdUsage" cssClass="error"/>
                    </div>
                </c:when>
                <c:otherwise>
                    <%-- To DO - This will be updated in YUK-21025 --%>
                    ${loadGroup.deviceIdUsage}" : ${loadGroup.pointIdUsage}
                </c:otherwise>
            </c:choose>
        </tags:nameValue2>

        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:nameValue2 nameKey=".controlStartState" id="js-start-state" rowClass="dn">
                <tags:selectWithItems items="${startStates}" path="startControlRawStateId" 
                                      defaultItemValue="0" id="js-control-start-state"
                                      itemLabel="name" itemValue="id"/>
            </tags:nameValue2>
        </cti:displayForPageEditModes>
        <cti:displayForPageEditModes modes="VIEW">
            <tags:nameValue2 nameKey=".controlStartState">
                <%-- To DO - This will be updated in YUK-21025 --%>
                ${loadGroup.startControlRawState}
            </tags:nameValue2>
        </cti:displayForPageEditModes>

    </tags:nameValueContainer2>
</tags:sectionContainer2>
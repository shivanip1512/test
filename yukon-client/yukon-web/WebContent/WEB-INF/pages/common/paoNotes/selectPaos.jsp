<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<div class="js-pao-selection-container dib">
    <cti:uniqueIdentifier var="id" />
    <input type="hidden" class="js-unique-identifier" value="${id}"/>
    <form:select path="paoSelectionMethod" class="js-select-devices">
        <c:forEach var="paoSelectionMethod" items="${paoSelectionMethods}">
            <form:option id="js-${paoSelectionMethod}" value="${paoSelectionMethod}">
                <i:inline key=".${paoSelectionMethod}"/>
            </form:option>
        </c:forEach>
    </form:select>
    <div class="dib dn push-down-3 js-picker-dialog">
        <tags:bind path="paoIds">
            <tags:pickerDialog type="meterPicker"
                               id="paoPicker_${id}"
                               linkType="selection"
                               selectionProperty="paoName"
                               destinationFieldName="paoIds"
                               allowEmptySelection="true"
                               multiSelectMode="true"
                               initialIds="${paoNoteFilter.paoIds}"
                               endEvent="yukon:paonotessearch:paosselected"/>
        </tags:bind>
    </div>
    <div class="dib dn push-down-3 js-device-group-picker-for-notes">
        <cti:list var="groups">
            <c:forEach var="subGroup" items="${deviceGroupNames}">
                <cti:item value="${subGroup}"/>
            </c:forEach>
        </cti:list>
        <tags:deviceGroupPicker inputName="deviceGroupNames"
                                inputValue="${groups}"
                                multi="true"
                                dialogId="js-paonotes-device-group-picker_${id}"/>
        <input type="hidden" id="js-clear-device-group-selection_${id}"/>
        <tags:bind path="deviceGroups"/>
    </div>
</div>

<cti:includeScript link="/resources/js/common/yukon.paonotes.paoselection.js"/>
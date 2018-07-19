<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<form:select id="js-select-devices" path="paoSelectionMethod">
    <c:forEach var="paoSelectionMethod" items="${paoSelectionMethods}">
        <form:option id="js-${paoSelectionMethod}" value="${paoSelectionMethod}">
            <i:inline key=".${paoSelectionMethod}"/>
        </form:option>
    </c:forEach>
</form:select>
<div id="js-picker-dialog" class="dib dn push-down-3">
    <tags:bind path="paoIds">
        <tags:pickerDialog type="meterPicker"
                       id="paoPicker"
                       linkType="selection"
                       selectionProperty="paoName"
                       destinationFieldName="paoIds"
                       allowEmptySelection="true"
                       multiSelectMode="true"
                       initialIds="${paoNoteFilter.paoIds}"/>
    </tags:bind>
</div>
<div id="js-device-group-picker" class="dib dn push-down-3">
    <cti:list var="groups">
        <c:forEach var="subGroup" items="${deviceGroupNames}">
            <cti:item value="${subGroup}"/>
        </c:forEach>
    </cti:list>
    <tags:deviceGroupPicker inputName="deviceGroupNames" inputValue="${groups}" multi="true"/>
    <tags:bind path="deviceGroups"/>
</div>
<cti:includeScript link="/resources/js/common/yukon.paonotes.paoselection.js"/>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:msgScope paths="modules.tools.schedule">
    <c:set var="pathKey" value="${templateReceived ? 'schedule.simpleOptions.' : 'simpleOptions.'}"/>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".simpleOptions.start">
            <tags:input path="${pathKey}startCommand" size="50" maxlength="120" />
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".simpleOptions.stop">
            <tags:input path="${pathKey}stopCommand" size="50" maxlength="120" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".simpleOptions.repeatInterval">
            <tags:input path="${pathKey}repeatInterval" size="5" />
        </tags:nameValue2>
        <c:set var="paoId" value="${schedule.simpleOptions.targetPAObjectId}" />
        <tags:hidden id="pao-id" path="${pathKey}targetPAObjectId" />
        <cti:displayForPageEditModes modes="EDIT,CREATE">
            <tags:nameValue2 nameKey=".simpleOptions.target" rowId="target-row">
                <div class="button-group button-group-toggle">
                    <c:set var="clazz" value="${target == 'DEVICE' ? 'on' : ''}" />
                    <cti:button icon="icon-database-add" nameKey="simpleOptions.device"
                        data-show="#device-row" classes="${clazz} M0" />
                    <c:set var="clazz" value="${target == 'LOADGROUP' ? 'on' : ''}" />
                    <cti:button icon="icon-database-add" nameKey="simpleOptions.loadGroup"
                        data-show="#load-group-row" classes="${clazz}" />
                </div>
            </tags:nameValue2>
        </cti:displayForPageEditModes>
        <c:set var="clazz" value="${target != 'DEVICE' ? 'dn' : ''}" />
        <tags:nameValue2 nameKey=".simpleOptions.device.label" rowId="device-row" rowClass="${clazz}">
            <tags:pickerDialog type="commanderDevicePicker" id="commanderDevicePicker"
                linkType="selection" selectionProperty="paoName" destinationFieldId="pao-id"
                immediateSelectMode="true" viewOnlyMode="${mode == 'VIEW'}" />
        </tags:nameValue2>
        <c:set var="clazz" value="${target != 'LOADGROUP' ? 'dn' : ''}" />
        <tags:nameValue2 nameKey=".simpleOptions.loadGroup.label" rowId="load-group-row"
            rowClass="${clazz}">
            <tags:pickerDialog type="lmGroupPaoPermissionCheckingPicker" id="lmGroupPicker"
                linkType="selection" selectionProperty="paoName" destinationFieldId="pao-id"
                immediateSelectMode="true" initialId="${paoId}" viewOnlyMode="${mode == 'VIEW'}" />
        </tags:nameValue2>
    </tags:nameValueContainer2>
</cti:msgScope>
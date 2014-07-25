<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.ecobee.details">
<form>
    <cti:csrfToken/>
    <tags:nameValueContainer2 tableClass="with-form-controls" naturalWidth="false">
        <tags:nameValue2 nameKey=".download.startDate" valueClass="full-width">
            <input type="hidden" id="ecobee-start-report-date">
            <input type="hidden" id="ecobee-end-report-date">
            <dt:dateTime name="ecobeeStartReportDate" value="${oneDayAgo}" maxDate="${now}"/>
            <span class="fl" style="margin-right: 5px;"><i:inline key="yukon.common.to"/></span>
            <dt:dateTime name="ecobeeEndReportDate" value="${now}" maxDate="${now}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>
    <div class="error dn" id="bad-date-range"><i:inline key=".download.dateOutOfRange"/></div>
    <h3 class="stacked"><i:inline key=".loadGroups"/></h3>
    <div id="loadGroup">
        <input type="hidden" id="loadGroupId" name="loadGroup">
        <tags:pickerDialog
            type="ecobeeGroupPicker"
            id="loadGroupPicker"
            linkType="none"
            container="loadGroup" 
            multiSelectMode="true"
            destinationFieldName="loadGroupIds"
            destinationFieldId="loadGroupId"
            endAction="yukon.dr.ecobee.assignInputs"/>
    </div>
</form>
</cti:msgScope>
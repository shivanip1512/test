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
            <dt:date name="ecobeeStartReportDate" id="startReportDate" value="${oneDayAgo}" maxDate="${now}"/>
            <span class="fl" style="margin-right: 5px;"><i:inline key="yukon.common.to"/></span>
            <dt:date name="ecobeeEndReportDate" id="endReportDate" value="${now}" maxDate="${now}"/>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div style="width:500px;">
        <div class="dib error dn" style="width: 160px;float:left;margin-left: 144px;" id="start-date-invalid"><i:inline key="yukon.web.error.date.validRequired"/></div>
        <div class="dib error dn" style="width: 150px;float:right;margin-right: 35px;" id="end-date-invalid"><i:inline key="yukon.web.error.date.validRequired"/></div>
    </div>
    <div style="clear: both;"></div>
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
    <div class="error dn" id="missing-serial-numbers"><i:inline key=".download.missingSerialNumbers"/></div>
</form>
</cti:msgScope>
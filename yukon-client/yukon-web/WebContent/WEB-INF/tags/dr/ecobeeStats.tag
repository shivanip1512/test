<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ attribute name="value" required="true" type="com.cannontech.web.dr.model.EcobeeQueryStats" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:msgScope paths="yukon.web.modules.dr.ecobee.details">

<div class="progress query-statistics" style="width: 80px;float:left;">
    <div class="progress-bar progress-bar-success" 
        role="progressbar" 
        aria-valuenow="0.0%" 
        aria-valuemin="0" 
        aria-valuemax="100" 
        style="width: ${value.demandResponsePercent}%;"></div>
    <div class="progress-bar progress-bar-info" 
        role="progressbar" 
        aria-valuenow="0.0%" 
        aria-valuemin="0" 
        aria-valuemax="100" 
        style="width: ${value.dataCollectionPercent}%;"></div>
    <div class="progress-bar progress-bar-default" 
        role="progressbar" 
        aria-valuenow="0.0%" 
        aria-valuemin="0" 
        aria-valuemax="100" 
        style="width: ${value.systemPercent}%;"></div>
</div>
<div class="fl query-counts" 
    style="margin-left: 10px;" 
    title="<cti:msg2 key=".statistics.title"/>">
    <span class="query-total" style="margin-right: 10px;width:48px;display: inline-block;">${value.countsTotal}</span>
    <span class="label label-success">${value.demandResponseCount}</span>
    <span class="label label-info">${value.dataCollectionCount}</span>
    <span class="label label-default">${value.systemCount}</span>
</div>

</cti:msgScope>
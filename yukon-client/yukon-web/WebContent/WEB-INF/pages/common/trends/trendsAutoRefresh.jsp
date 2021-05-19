<span class="form-control fl"><i:inline key="yukon.web.modules.tools.trends.autoRefresh"/></span>
<div id="trend-updater" class="button-group button-group-toggle">
    <c:set var="onClasses" value="${autoUpdate ? 'on yes' : 'yes'}"/>
    <cti:button nameKey="on" classes="${onClasses}"/>
    <c:set var="offClasses" value="${autoUpdate ? 'no' : 'on no'}"/>
    <cti:button nameKey="off" classes="${offClasses}"/>
</div>
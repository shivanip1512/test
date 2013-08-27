<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="${type}.assetDetails">

<script type="text/javascript">
jQuery(function() {
    
    // makes clicked button appear pushed in or popped out.
    jQuery('[data-filter]').click(function(e) {
        jQuery(e.currentTarget).toggleClass('on');
    });
    
    // Gets string representation of the current filters.
    function getFilter() {
        var filter = [];
        jQuery('[data-filter].on').each(function (idx, item) {
            filter.push(jQuery(item).data('filter'));
        });
        if (filter.length > 0) {
            return JSON.stringify(filter);
        } else {
            return null;
        }
    }
    // Used for filtering...
    jQuery(document).on('click', '[data-filter]', function (event) {
        event.stopPropagation();
        var data = {'assetId': '${assetId}', 'type': '${type}', 'filter': getFilter()};
        if (${not empty itemsPerPage}) {
            data.itemsPerPage = '${itemsPerPage}';
        }
        jQuery('.device-detail-table').load('page', data);
    });

    
    // Used for paging...
    jQuery(document).on('click', '.f-ajaxPaging', function(event) {
        event.stopPropagation();
        var url = jQuery(event.currentTarget).attr('data-url');
        var data = {'filter': getFilter()}
        jQuery.ajax({
            url: url,
            method: 'get',
            data: data
        }).done(function(data) {
            var parent = jQuery(event.currentTarget).closest(".device-detail-table");
            parent.html(data);
        });
        return false;
    });

    // Used for sorting...
    jQuery(document).on('click', '.f-sortLink', function(event) {
        event.stopPropagation();
        var url = this.href;
        var data = {'filter': getFilter()}
        jQuery.ajax({
            url: url,
            method: 'get',
            data: data
        }).done(function(data) {
            var parent = jQuery(event.currentTarget).closest(".device-detail-table");
            parent.html(data);
        });
        return false;
    });

    // Used for download to csv...
    jQuery('#dd-download').click(function(e) {
        e.stopPropagation();
        var url = "downloadToCsv?filter=" + getFilter() + "&assetId=${assetId}&type=${type}";
        window.location.href = url;
    });
});
</script>

<cti:msgScope paths="modules.operator.hardware.assetAvailability">
<!-- Page Dropdown Actions -->
<%--  Temporarily disable until Ping feature is available -- using button below instead.
    <div id="f-page-actions" class="dn">
        <cm:dropdownOption icon="icon-transmit-blue">Ping All</cm:dropdownOption>
        <cm:dropdownOption icon="icon-transmit">Ping Unavailable</cm:dropdownOption>
        <li class="divider"></li>
        <cm:dropdownOption id="dd-download" icon="icon-page-white-excel" key="yukon.web.defaults.download" ></cm:dropdownOption>
    </div>
--%>
<div id="f-page-buttons" class="dn">
    <cti:button id="dd-download" icon="icon-page-white-excel" nameKey="download"/>
</div>
    
    <div class="column_12_12">
        <div class="column one">
            <%-- Temporarily disable until Ping feature is available.
            <div class="ping-results">
                <em>Pinging - 123 unavailable devices...</em>
                <div>PROGRESS BAR GOES HERE...</div>
            </div>
            --%>
            <div class="pageActionArea stacked">
                <strong class="fl">Filter:</strong>
                <button data-filter="RUNNING" class="left on">
                    <span class="label green"><cti:msg2 key=".running"/></span></button>
                <button data-filter="NOT_RUNNING" class="middle on">
                    <span class="label orange"><cti:msg2 key=".notRunning"/></span></button>
                <button data-filter="OPTED_OUT" class="middle on">
                    <span class="label grey"><cti:msg2 key=".optedOut"/></span></button>
                <button data-filter="UNAVAILABLE" class="right on">
                    <span class="label red"><cti:msg2 key=".unavailable"/></span></button>
            </div>
        </div>
        <div class="column two nogutter">
            <!-- show Legend with Pie Chart -->
            <div style="max-height: 100px;">
                <dr:assetAvailabilityStatus assetId="${assetId}"
                        assetAvailabilitySummary="${assetAvailabilitySummary}" pieJSONData="${pieJSONData}"/>
            </div>
        </div>
    </div>
</cti:msgScope>

    <%-- Paged results table goes here... --%>
    <div class="clear device-detail-table">
        <h2><cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.devices"/></h2>
        <dr:assetDetailsResult result="${result}" type="${type}" assetId="${assetId}" itemsPerPage="${itemsPerPage}"/>
    </div>

</cti:standardPage>
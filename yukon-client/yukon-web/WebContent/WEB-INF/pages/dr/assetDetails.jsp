<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="dr" page="${type}.assetDetails">

<script type="text/javascript">
jQuery(function() {
    
    jQuery('[data-filter]').click(function(e) {
        // makes clicked button appear pushed in or popped out.
        jQuery(e.currentTarget).toggleClass('on');
    });
    
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
});
</script>

<!-- Page Dropdown Actions -->
<div id="f-page-actions" class="dn">
    <cm:dropdownOption icon="icon-transmit-blue">Ping All</cm:dropdownOption>
    <cm:dropdownOption icon="icon-transmit">Ping Unavailable</cm:dropdownOption>
    <li class="divider"></li>
    <cm:dropdownOption icon="icon-page-white-excel">Download</cm:dropdownOption>
</div>
    
    <div class="column_12_12">
        <div class="column one">
            <div class="ping-results">
                <em>Pinging - 123 unavailable devices...</em>
                <div>PROGRESS BAR GOES HERE...</div>
            </div>
            <div class="pageActionArea stacked">
                <strong class="fl">Filter:</strong>
                <button data-filter="RUNNING" class="left on"><span class="label green">Running</span></button>
                <button data-filter="NOT_RUNNING" class="middle on"><span class="label orange">Not Running</span></button>
                <button data-filter="OPTED_OUT" class="middle on"><span class="label grey">Opted Out</span></button>
                <button data-filter="UNAVAILABLE" class="right on"><span class="label red">Unavailable</span></button>
            </div>
        </div>
        <div class="column two nogutter">
            <!-- Pie Chart Only? -->
            <div style="max-height: 100px;">
<%--                 <flot:jsonPieChart data="${pieJSONData}" classes="small-pie"/> --%>
                <dr:assetAvailabilityStatus assetId="${assetId}"
                        assetAvailabilitySummary="${assetAvailabilitySummary}" pieJSONData="${pieJSONData}"/>
            </div>

        </div>
    </div>

    <%-- Paged results table goes here... --%>
    <div class="clear device-detail-table">
        <h2>Devices</h2>
        <dr:assetDetailsResult result="${result}" type="${type}" assetId="${assetId}" itemsPerPage="${itemsPerPage}"/>
    </div>

</cti:standardPage>
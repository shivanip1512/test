<%@ tag trimDirectiveWhitespaces="true" body-content="empty" 
        description="Sets some global javascript values for Yukon on the global variable 'YG'" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<c:set var="appContextPath" value="${pageContext.request.contextPath}"/>
<c:if test="${appContextPath == '/'}">
    <%-- When we're running as the root web application, we need an empty string for a prefix. --%>
    <c:set var="appContextPath" value=""/>
</c:if>
<script type="text/javascript">
var yg = {
    
    app_context_path: '${appContextPath}',
    
    dev_mode: '<cti:getBooleanConfigParam param="DEVELOPMENT_MODE"/>' === 'true',
    
    map_devices_street_url: '<cti:getMappingUrl viewType="STREET"/>',
    map_devices_satellite_url: '<cti:getMappingUrl viewType="SATELLITE"/>',
    map_devices_hybrid_url: '<cti:getMappingUrl viewType="HYBRID"/>',
    map_devices_elevation_url: '<cti:getMappingUrl viewType="ELEVATION"/>',
    
    events: {
        animationend: 'webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend',
        pagingend: 'yukon:paging:end'
    },
    
    formats: {
        date: {
            both: 'MM/DD/YYYY HH:mm:ss',
            full: 'MM/DD/YYYY HH:mm:ss zz',
            full_hm: 'MM/DD/YYYY HH:mm zz',
            long_date_time: 'MMM DD hh:mm:ss A',
            long_date_time_hm: 'MMM DD hh:mm A',
            both_with_ampm: 'MM/DD/YYYY hh:mm:ss A',
            date: 'MM/DD/YYYY',
            date_hms_12: 'MM/DD/YYYY hh:mm:ss a'
        },
        phone: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
    },
    
    highcharts_options: {
        disable_credits: {
            enabled: false
        },
        lang: {
            thousandsSep: ","
        },
        global: {
            useUTC: false,
            timezoneOffset : new Date().getTimezoneOffset()
        },
        chart_options: {
            renderTo: 'chart',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
        },
        pie_chart_options: {
            legend: {
                symbolPadding: -60,
                symbolWidth: 0.001,
                symbolHeight: 0.001,
                symbolRadius: 0,
                align: 'right',
                borderWidth: 0,
                useHTML: true,
                layout: 'vertical',
                verticalAlign: 'middle'
            },
            title: { 
                text: null
            },
            tooltip: {
                pointFormat: '<b>{point.displayPercentage}, {point.x} <cti:msg2 key="yukon.common.devices"/></b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    cursor: 'pointer',
                    dataLabels: {
                        enabled: false
                    },
                    showInLegend: true,
                    borderWidth: 0.25,
                }
            },
            series_type_pie: 'pie',
            series_type_area: 'area',
        }
    },
    
    keys: { up: 38, down: 40, left: 37, right: 39, enter: 13, escape: 27 },
    
    // Common selectors in all of Yukon.
    selectors: {
        // Any of the paging controls (previous, next, page counts)
        paging : '.paging-area .previous-page .button, .paging-area .next-page .button',
        pageSize: '.paging-area .page-size .js-items-per-page'
    },
    
    colors: JSON.parse('<cti:getYukonColorPaletteJson/>'),
    
    text: {
        cancel: '<cti:msg2 key="yukon.web.components.button.cancel.label"/>',
        close: '<cti:msg2 key="yukon.web.components.button.close.label"/>',
        confirm: '<cti:msg2 key="yukon.web.components.button.confirm.label"/>',
        confirmQuestion: '<cti:msg2 key="yukon.common.confirm"/>',
        complete: '<cti:msg2 key="yukon.common.complete"/>',
        deleteButton: '<cti:msg2 key="yukon.web.components.button.delete.label"/>',
        disable: '<cti:msg2 key="yukon.web.components.button.disable.label"/>',
        edit: '<cti:msg2 key="yukon.web.components.button.edit.label"/>',
        enable: '<cti:msg2 key="yukon.web.components.button.enable.label"/>',
        failed: '<cti:msg2 key="yukon.common.failed"/>',
        filter: '<cti:msg2 key="yukon.web.components.button.filter.label"/>',
        finished: '<cti:msg2 key="yukon.common.finished"/>',
        map: '<cti:msg2 key="yukon.common.map"/>',
        more: '<cti:msg2 key="yukon.common.selectGroup.more"/>',
        no: '<cti:msg2 key="yukon.common.no"/>',
        ok: '<cti:msg2 key="yukon.web.components.button.ok.label"/>',
        save: '<cti:msg2 key="yukon.web.components.button.save.label"/>',
        send: '<cti:msg2 key="yukon.web.components.button.send.label"/>',
        successful: '<cti:msg2 key="yukon.common.successful"/>',
        view: '<cti:msg2 key="yukon.web.components.button.view.label"/>',
        yes: '<cti:msg2 key="yukon.common.yes"/>',
        ajaxError: '<cti:msg2 key="yukon.web.error.genericMainMessage"/>',
        forbiddenError: '<cti:msg2 key="yukon.web.error.403Message"/>',
        internalServerError: '<cti:msg2 key="yukon.web.error.500Message"/>',
        serviceUnavailableError: '<cti:msg2 key="yukon.web.error.503Message"/>',
        next: '<cti:msg2 key="yukon.web.components.button.next.label"/>',
        back: '<cti:msg2 key="yukon.web.components.button.back.label"/>',
        create: '<cti:msg2 key="yukon.web.components.button.create.label"/>',
        noneChoice: '<cti:msg2 key="yukon.common.none.choice"/>',
        nextRefresh: '<cti:msg2 key="yukon.web.widgets.nextRefresh"/>',
        remove: '<cti:msg2 key="yukon.web.components.ajaxConfirm.confirmRemove.ok"/>',
        confirmRemoveMessage: '<cti:msg2 key="yukon.web.components.ajaxConfirm.confirmRemove.message"/>',
        confirmRemoveTitle: '<cti:msg2 key="yukon.web.components.ajaxConfirm.confirmRemove.title"/>'
    },
    
    iconSvg: {
    	iconCloseX: '<cti:getIconSvg iconClass="icon-close-x"/>',
    	iconMagnifier: '<cti:getIconSvg iconClass="icon-magnifier"/>',
    	iconHelp: '<cti:getIconSvg iconClass="icon-help"/>',
    	iconError: '<cti:getIconSvg iconClass="icon-error"/>',
    	iconAdd: '<cti:getIconSvg iconClass="icon-add"/>',
    	iconStar: '<cti:getIconSvg iconClass="icon-star"/>',
       	iconFavoriteNot: '<cti:getIconSvg iconClass="icon-favorite-not"/>',
       	iconPlusGreen: '<cti:getIconSvg iconClass="icon-plus-green"/>',
       	iconCross: '<cti:getIconSvg iconClass="icon-cross"/>',
       	iconRemove: '<cti:getIconSvg iconClass="icon-remove"/>',
       	iconDelete: '<cti:getIconSvg iconClass="icon-delete"/>',
       	iconExpand: '<cti:getIconSvg iconClass="icon-expand"/>',
       	iconCollapse: '<cti:getIconSvg iconClass="icon-collapse"/>',
       	iconTick: '<cti:getIconSvg iconClass="icon-tick"/>',
       	iconDatabaseAdd: '<cti:getIconSvg iconClass="icon-database-add"/>',
       	iconArrowForward: '<cti:getIconSvg iconClass="icon-arrow-forward"/>'
    },
    
    timezone: '<cti:getUsersTimezone/>',
    
    // Role properties
    rp: {
        updater_delay: +'<cti:getProperty property="DATA_UPDATER_DELAY_MS"/>'
    },
    
    _updateInterval: 6000,

    
};

</script>
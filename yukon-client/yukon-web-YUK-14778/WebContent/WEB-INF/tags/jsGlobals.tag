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
        },
        phone: <cti:msg2 key="yukon.common.phoneNumberFormatting.formats"/>
    },
    
    highcharts_options: {
        global: {
            useUTC: false,
            timezoneOffset : new Date().getTimezoneOffset()
        }
    },
    
    keys: { up: 38, down: 40, left: 37, right: 39, enter: 13, escape: 27 },
    
    // Common selectors in all of Yukon.
    selectors: {
        // Any of the paging controls (previous, next, page counts)
        paging : '.paging-area .previous-page .button, .paging-area .next-page .button, .paging-area .page-size a'
    },
    
    text: {
        cancel: '<cti:msg2 key="yukon.web.components.button.cancel.label"/>',
        close: '<cti:msg2 key="yukon.web.components.button.close.label"/>',
        confirm: '<cti:msg2 key="yukon.web.components.button.confirm.label"/>',
        confirmQuestion: '<cti:msg2 key="yukon.common.confirm"/>',
        complete: '<cti:msg2 key="yukon.common.complete"/>',
        'delete': '<cti:msg2 key="yukon.web.components.button.delete.label"/>',
        edit: '<cti:msg2 key="yukon.web.components.button.edit.label"/>',
        failed: '<cti:msg2 key="yukon.common.failed"/>',
        filter: '<cti:msg2 key="yukon.web.components.button.filter.label"/>',
        finished: '<cti:msg2 key="yukon.common.finished"/>',
        map: '<cti:msg2 key="yukon.common.map"/>',
        more: '<cti:msg2 key="yukon.common.selectGroup.more"/>',
        no: '<cti:msg2 key="yukon.common.no"/>',
        ok: '<cti:msg2 key="yukon.web.components.button.ok.label"/>',
        save: '<cti:msg2 key="yukon.web.components.button.save.label"/>',
        successful: '<cti:msg2 key="yukon.common.successful"/>',
        view: '<cti:msg2 key="yukon.web.components.button.view.label"/>',
        yes: '<cti:msg2 key="yukon.common.yes"/>',
        ajaxError: '<cti:msg2 key="yukon.web.error.genericMainMessage"/>'
    },
    
    timezone: jstz.determine().name(),
    
    // Role properties
    rp: {
        updater_delay: +'<cti:getProperty property="DATA_UPDATER_DELAY_MS"/>'
    }
    
};

</script>
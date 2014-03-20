<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dev" page="uiDemos">

    <dt:pickerIncludes/>
    <cti:includeScript link="/resources/js/lib/google-code-prettify/prettify.js"/>
    <cti:includeCss link="/resources/js/lib/google-code-prettify/prettify.css"/>
    <cti:includeScript link="JQUERY_COOKIE"/>

    <div class="column-4-20">
        <div class="column one side-nav">
            <ul id="dev-sections">
                <li class="selected"><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/containers"/>">Containers</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/buttons"/>">Buttons</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/pickers"/>">Pickers</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/i18n"/>">I18N</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/i18nScopes"/>">I18N Scopes</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/dialogs"/>">Dialogs</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/jsTesting"/>">JS Testing</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/sprites"/>">Icons</a></li>
                <li><a href="javascript:void(0);" data-url="<cti:url value="/support/development/uiDemos/more"/>">More</a></li>
            </ul>
        </div>
        <div class="column two nogutter">
            <div id="dev-content" class="clearfix">
                <%@ include file="containers.jsp"%>
            </div>
        </div>
    </div>

<script>
$(function () {
    $('#dev-sections a').click(function(e) {
        var url = $(this).data('url');
        $('#dev-content').load(url, function() {
            prettyPrint();
            yukon.ui.initDateTimePickers();
        });
        $('#dev-sections li').removeClass('selected');
        $(this).closest('li').addClass('selected');
    });
    prettyPrint();
});
</script>
</cti:standardPage>
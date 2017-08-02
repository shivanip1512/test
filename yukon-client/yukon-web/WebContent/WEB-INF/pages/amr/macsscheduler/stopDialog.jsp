<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<script>
$(function () {
    if (typeof yukon.ui.initDateTimePickers !== 'undefined') {
        yukon.ui.initDateTimePickers();
    }
});
</script>

<cti:msgScope paths="yukon.web.modules.tools.scripts.stop">

    <form id="stopform">
        <cti:csrfToken/>
        <input type="hidden" id="id" value="${schedule.id}" />
        <tags:sectionContainer2 nameKey=".start" styleClass="clearfix">
            <div>
                <input type="checkbox" name="stopNow" checked="checked"/><i:inline key=".stopNow"/>
            </div>
            <dt:dateTime name="stop" value="${stopTime}" />
        </tags:sectionContainer2>
    </form>
</cti:msgScope>
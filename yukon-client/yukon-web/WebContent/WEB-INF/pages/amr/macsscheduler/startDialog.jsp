<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<script>
    $(function() {
        if (typeof yukon.ui.initDateTimePickers !== 'undefined') {
            yukon.ui.initDateTimePickers();
        }
    });
</script>

<cti:msgScope paths="yukon.web.modules.tools.scripts.start">
    <div>
        <form id="startform">
            <cti:csrfToken />
            <input type="hidden" id="id" value="${schedule.id}" />
            <div id="errorMsg" class="error">${errorMsg}</div>
            <div class="column_12_12 clearfix">
                <div class="column one">
                    <cti:msg2 var="startText" key="yukon.common.start" />
                    <tags:sectionContainer title="${startText}">
                        <div>
                            <input type="checkbox" name="startNow" checked="checked" />
                            <i:inline key=".startNow" />
                        </div>
                        <dt:dateTime name="start" value="${currentTime}" />
                    </tags:sectionContainer>
                </div>
                <div class="column two nogutter">
                    <cti:msg2 var="stopText" key="yukon.common.stop" />
                    <tags:sectionContainer title="${stopText}">
                        <div>&nbsp;</div>
                        <dt:dateTime name="stop" value="${stopTime}" />
                    </tags:sectionContainer>
                </div>
            </div>
        </form>
    </div>
</cti:msgScope>

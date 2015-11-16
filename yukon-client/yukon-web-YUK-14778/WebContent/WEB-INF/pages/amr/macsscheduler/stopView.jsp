<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="scripts.stop">
    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
    <form id="stopform" method="post" action="action">
        <cti:csrfToken/>
        <tags:sectionContainer2 nameKey=".start" styleClass="clearfix">
            <div>
                <input type="checkbox" name="stopNow" checked="checked"/><i:inline key=".stopNow"/>
            </div>
            <dt:dateTime name="stop" value="${stopTime}" />
        </tags:sectionContainer2>

        <div class="page-action-area">
            <cti:button type="submit" nameKey="apply" classes="primary action"/>
            <cti:button nameKey="back" href="view"/>
        </div>

        <input type="hidden" name="id" value="${schedule.id}" />
        <input type="hidden" name="sortBy" value="${sortBy}"/>
        <input type="hidden" name="descending" value="${descending}"/>
        <input type="hidden" name="isStart" value="false"/>
    </form>
</cti:standardPage>

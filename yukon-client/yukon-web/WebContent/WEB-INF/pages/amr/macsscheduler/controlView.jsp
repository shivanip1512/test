<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="scripts.start">
    <cti:includeScript link="/JavaScript/macsscheduledscripts.js" />
    <div>
        <form id="startform" method="post" action="action">
            <c:if test="${errorMsg != null}">
                <div class="error">${errorMsg}</div>
            </c:if>
            <div class="column_12_12 clearfix">
                <div class="column one">
                    <tags:sectionContainer2 nameKey=".start">
                    <div>
                        <input type="checkbox" name="startNow" checked="checked"/><i:inline key=".startNow"/>
                        <dt:dateTime name="start" value="${currentTime}" />
                    </div>
                    </tags:sectionContainer2>
                </div>
                <div class="column two nogutter">
                    <tags:sectionContainer2 nameKey=".stop">
                        <div>&nbsp;</div>
                        <dt:dateTime name="stop" value="${stopTime}" />
                    </tags:sectionContainer2>
                </div>
            </div>

            <div class="page-action-area">
                <cti:button type="submit" name="buttonAction" nameKey="apply" classes="primary action"/>
                <cti:button nameKey="back" href="view"/>
            </div>
            <input type="hidden" name="id" value="${schedule.id}" />
            <input type="hidden" name="sortBy" value="${sortBy}"/>
            <input type="hidden" name="descending" value="${descending}"/>
            <input type="hidden" name="isStart" value="true"/>
        </form>
    </div>
</cti:standardPage>

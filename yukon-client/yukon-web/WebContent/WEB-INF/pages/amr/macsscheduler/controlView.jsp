<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="tools" page="scripts.start">
    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
    <div>
        <form id="startform" method="post" action="action">
            <cti:csrfToken/>
            <c:if test="${errorMsg != null}">
                <div class="error">${errorMsg}</div>
            </c:if>
            <div class="column_12_12 clearfix">
                <div class="column one">
                    <cti:msg2 var="startText" key="defaults.start"/>
                    <tags:sectionContainer title="${startText}">
                    <div>
                        <input type="checkbox" name="startNow" checked="checked"/><i:inline key=".startNow"/>
                    </div>
                        <dt:dateTime name="start" value="${currentTime}" />
                    </tags:sectionContainer>
                </div>
                <div class="column two nogutter">
                    <cti:msg2 var="stopText" key="defaults.stop"/>
                    <tags:sectionContainer title="${stopText}">
                        <div>&nbsp;</div>
                        <dt:dateTime name="stop" value="${stopTime}" />
                    </tags:sectionContainer>
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

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="tdc.home">
    
    <audio id="alarm-audio" loop>
        <source src="<cti:url value="/WebConfig/yukon/audio/beep1.mp3"/>" type="audio/mpeg">
    </audio>

    <cti:includeScript link="/resources/js/pages/yukon.tools.tdc.js" />

    <div class="column-8-16">
        <div class="column one">
            <div id="tdc-display-tabs" class="section">
                <ul>
                    <li><a href="#custom_displays"><i:inline key=".display.custom" /></a></li>
                    <li><a href="#events_displays"><i:inline key=".display.event" /></a></li>
                </ul>
                <div id="events_displays" class="clearfix scroll-lg lite-container">
                    <ul class="simple-list display-list">
                        <c:forEach var="topEvent" items="${topEvents}">
                            <cti:url var="view" value="/tools/data-viewer/${topEvent.displayId}" />
                            <li><c:choose>
                                    <c:when test="${fn:length(topEvent.columns) > 0}">
                                        <a href="${view}">${fn:escapeXml(topEvent.name)}</a>
                                    </c:when>
                                    <c:otherwise>
                                       ${fn:escapeXml(topEvent.name)}
                                    </c:otherwise>
                                </c:choose></li>
                        </c:forEach>
                    </ul>
                    <hr>
                    <ul class="simple-list display-list">
                        <c:forEach var="event" items="${events}">
                            <cti:url var="view" value="/tools/data-viewer/${event.displayId}" />
                            <li><c:choose>
                                    <c:when test="${fn:length(event.columns) > 0}">
                                        <a href="${view}">${fn:escapeXml(event.name)}</a>
                                    </c:when>
                                    <c:otherwise>
                                       ${fn:escapeXml(event.name)}
                                    </c:otherwise>
                                </c:choose></li>
                        </c:forEach>
                    </ul>
                </div>
                <div id="custom_displays">
                    <div class="clearfix scroll-lg">
                        <ul class="display-list simple-list">
                            <c:forEach var="customEvent" items="${customEvents}">
                                <cti:url var="view"
                                    value="/tools/data-viewer/${customEvent.displayId}" />
                                <li><c:choose>
                                        <c:when test="${fn:length(customEvent.columns) > 0}">
                                            <a href="${view}">${fn:escapeXml(customEvent.name)}</a>
                                        </c:when>
                                        <c:otherwise>
                                       ${fn:escapeXml(customEvent.name)}
                                    </c:otherwise>
                                    </c:choose></li>
                            </c:forEach>
                        </ul>
                    </div>
                    <div class="action-area">
                        <cti:url var="createUrl" value="/tools/data-viewer/create" />
                        <cti:button nameKey="create" icon="icon-plus-green" href="${createUrl}" />
                    </div>
                </div>
            </div>
        </div>
        <div class="column two nogutter" id="tdc-latest-data"></div>
    </div>
    <div id="page-buttons" class="dn">
        <tags:dynamicChoose updaterString="TDC/ALARM" suffix="${display.displayId}">
            <tags:dynamicChooseOption optionId="MULT_ALARMS">
                <cti:button nameKey="tdc.alarm.acknowledgeAll" icon="icon-tick" classes="js-ack-all ML15" />
            </tags:dynamicChooseOption>
            <tags:dynamicChooseOption optionId="NONE">
                <cti:button nameKey="tdc.alarm.acknowledgeAll" icon="icon-tick" classes="js-ack-all dn ML15" />
            </tags:dynamicChooseOption>
        </tags:dynamicChoose>
   
        <cti:button id="tdc-mute-btn" nameKey="mute" icon="icon-sound" renderMode="buttonImage" classes="ML15"/>
        <cti:button id="tdc-unmute-btn" nameKey="unmute" icon="icon-sound-mute" renderMode="buttonImage" classes="dn ML15"/>
      
        <cti:dataUpdaterCallback function="yukon.tools.tdc.toggleAlarm" initialize="true"  value="TDC/ALARM/${display.displayId}"/>
    </div>
</cti:standardPage>
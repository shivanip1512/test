<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="tdc.home">
    
    <audio id="alarm-audio" loop>
        <source src="/WebConfig/yukon/audio/beep1.mp3" type="audio/mpeg">
    </audio>

    <cti:includeScript link="JQUERY_COOKIE" />
    <cti:includeScript link="/JavaScript/yukon/yukon.tdc.js" />

    <div class="column-8-16">
        <div class="column one">
            <div id="display_tabs" class="section">
                <ul>
                    <li><a href="#custom_displays"><i:inline key=".display.custom" /></a></li>
                    <li><a href="#events_displays"><i:inline key=".display.event" /></a></li>
                </ul>
                <div id="events_displays" class="clearfix scroll-large lite-container">
                    <ul class="simple-list display-list">
                        <c:forEach var="topEvent" items="${topEvents}">
                            <cti:url var="view" value="/tdc/${topEvent.displayId}" />
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
                            <cti:url var="view" value="/tdc/${event.displayId}" />
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

                <div id="custom_displays" class="clearfix scroll-large">
                    <ul class="display-list simple-list">
                        <c:forEach var="customEvent" items="${customEvents}">
                            <cti:url var="view" value="/tdc/${customEvent.displayId}" />
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
            </div>
        </div>
        <div class="column two nogutter" id="latestData"></div>
    </div>
    <div id="f-page-buttons" class="dn">
        <tags:dynamicChoose updaterString="TDC/ALARM" suffix="${display.displayId}">
            <tags:dynamicChooseOption optionId="MULT_ALARMS">
                <cti:button nameKey="tdc.alarm.acknowledgeAll" icon="icon-tick" classes="f-ack-all" />
            </tags:dynamicChooseOption>
            <tags:dynamicChooseOption optionId="NONE">
                <cti:button nameKey="tdc.alarm.acknowledgeAll" icon="icon-tick" classes="f-ack-all dn" />
            </tags:dynamicChooseOption>
        </tags:dynamicChoose>
   
        <cti:button id="b_mute" nameKey="mute" icon="icon-sound" renderMode="buttonImage"/>
        <cti:button id="b_unmute" nameKey="unmute" icon="icon-sound-mute" renderMode="buttonImage" classes="dn"/>
      
        <cti:dataUpdaterCallback function="yukon.Tdc.toggleAlarm" initialize="true"  value="TDC/ALARM/${display.displayId}"/>
    </div>
</cti:standardPage>
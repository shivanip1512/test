<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="savedSchedules">
<cti:standardMenu/>

<cti:includeCss link="/WebConfig/yukon/styles/thermostat.css"/>
<cti:includeCss link="/WebConfig/yukon/styles/jqueryui/jquery.ui.css"/>

<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
<cti:includeScript link="${timeFormatter}"/>

<cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>

<cti:includeScript link="JQUERY"/>
<cti:includeScript link="JQUERY_UI"/>
<cti:includeScript link="/JavaScript/lib/JSON/2.0/json2.js"/>

<div id="editingScreen" class="ui-widget-overlay dn"></div>

<cti:flashScopeMessages/>

<script>
var DATA = ${savedSchedules};
$j(document).ready(function(){
    Yukon.ThermostatScheduleEditor.init({schedules:DATA.SCHEDULES, thermostat:DATA.thermostat, default:DATA.DEFAULT});
    
    //scroll the schedule into view
    var scheduleId = Yukon.ui.getParameterByName('scheduleId');
    if(scheduleId != ''){
        window.location.hash = "#scheduleId_" + scheduleId;
    }
});
</script>

<h3>
    <cti:msg key="yukon.dr.consumer.savedSchedules.header" /><br>
    <cti:msg var="label" key="${thermostatLabel}" htmlEscape="true"/><br>
    ${label}<br>
    <small><a href="/spring/stars/consumer/thermostat/schedule/history?thermostatIds=${thermostatIds}"><cti:msg2 key="yukon.web.menu.config.consumer.thermostat.history" /></a></small>
</h3>

<c:if test="${empty schedules}">
    <cti:msg2 key="yukon.dr.consumer.savedSchedules.noSchedulesHelper" />
</c:if>

<div class="schedules"></div>
<div id="slider" ></div>
   
<cti:button key="create" styleClass="create"/>
   
<!-- Templated Elements -->	
    <div class="templates dn">
        <ul>
            <li class="schedule">
<%--             <tags:boxContainer2 nameKey="callsBox" styleClass="schedule" hideEnabled="true" showInitially="true" > --%>
                <form method="POST" action="/spring/stars/consumer/thermostat/schedule/saveJSON">
                    <input type="hidden" name="thermostatId" value="${thermostatId}">
                    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
                    <input type="hidden" name="thermostatType" value="">
                    <input type="hidden" name="scheduleId" value="">
                    <input type="hidden" name="scheduleMode" value="">
                    <input type="hidden" name="schedules" value="">
                    <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
                    
                    <div class="heading">
                        <span class="title"></span>
                        
                    </div>
                    <div class="days fl">
                        <span class="labels">
                        <label class="label fl"></label>
                        </span>
                    </div>
                    <div class="actions">
<!--                         <div class="toggle"> -->
                            <a href="#" class="edit toggle icon_labeled">Edit</a>
                            <a href="#" class="send f_blocker icon_labeled">Send Now</a>
<!--                         </div> -->
<!--                         <div class="toggle dn"> -->
<!--                             <a href="#" class="default icon_labeled fr">Recommended Settings</a> -->
<!--                             <a href="#" class="save f_blocker toggle icon_labeled">Save</a> -->
<!--                             <a href="#" class="cancel toggle icon_labeled">Cancel</a> -->
<!--                             <a href="#" class="delete f_blocker icon_labeled fr" scheduleId="">Delete</a> -->
<!--                         </div> -->
                    </div>
                </form>
<%--             </tags:boxContainer2> --%>
            </li>
        </ul>

        <div class="day active">
            <div class="periods">
                <label class="label fl"></label>
            </div>
        </div>
        
        <div class="period period_view">
            <input type="hidden" name="timeOfWeek">
            <div class="info">
                <span class="name">Wake</span>
                <span class="time ">6:00am</span>
                <input type="hidden" name="secondsFromMidnight">
            </div>
            <div class="temp heat" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/>">
                <span class="value ">0</span><input type="hidden" value="0.000" name="heat_F"><span class="hide_when_editing">°<small>Heat</small></span>
            </div>
            <div class="temp cool" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/>">
                <span class="value ">0</span><input type="hidden" value="0.000" name="cool_F"><span class="hide_when_editing">°<small>Cool</small></span>
            </div>
        </div>
        
        <small class="heat_cool_label"><span style="float:right"><cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/> <cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/></span></small>
        
        <label class="mode"><input type="radio"><span class="text"></span></label>
        
        <!-- Editor -->
        <div class="schedule_editor">
            <form method="POST" action="/spring/stars/consumer/thermostat/schedule/saveJSON">
                <input type="hidden" name="thermostatId" value="${thermostatId}">
                <input type="hidden" name="thermostatIds" value="${thermostatIds}">
                <input type="hidden" name="thermostatType" value="">
                <input type="hidden" name="scheduleId" value="">
                <input type="hidden" name="scheduleMode" value="">
                <input type="hidden" name="schedules" value="">
                <input type="hidden" name="temperatureUnit" value="${temperatureUnit}">
                
                <label>::SCHEDULE NAME::<input type="text" name="scheduleName" maxlength="60">

                <div class="days fl">
                    <span class="labels"></span>
                </div>

                <div class="actions">
                    <a href="#" class="default icon_labeled fr">Recommended Settings</a>
                </div>
            </form>
        </div>
        
        <div class="period period_edit">
            <input type="hidden" name="timeOfWeek">
            <div class="info">
                <input type="hidden" name="secondsFromMidnight">
                <input type="text" class="time" maxlength="8">
            </div>
            
            <div class="temp heat" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.HEAT"/>">
                <input type="text" class="heat_F" maxlength="4">°
                <input type="hidden" value="0.000" name="heat_F">
            </div>
            <div class="temp cool" title="<cti:msg2 key="yukon.dr.consumer.thermostat.mode.COOL"/>">
                <input type="text" class="cool_F" maxlength="4">°
                <input type="hidden" value="0.000" name="cool_F">
            </div>
        </div>
        <!-- END Editor -->       
    </div>

    
    
<tags:simplePopup title="::editSchedule::" id="editSchedule" on=".edit">
<div class="container"></div>
<div class="actionArea">
    <div class="fr">
        <cti:button key="save" styleClass="save f_blocker" />
        <cti:button key="delete" styleClass="delete f_blocker" />
        <cti:button key="cancel" styleClass="cancel" />
    </div>
</div>
</tags:simplePopup>

<tags:simplePopup title="::createNewSchedule::" id="createSchedule" on=".create">
    <div class="f_wizard">
        <div class="f_page page_0">
            <div class="box">
                <div class="info box">
                    <cti:msg2 key="yukon.dr.consumer.savedSchedules.modeHint" />
                </div>
                <div class="box">
                    <c:forEach var="mode" items="${allowedModes}">
                        <label><input type="radio" name="scheduleMode" value="${mode}"> <cti:msg key="yukon.dr.consumer.thermostat.${mode}" /> </label>
                        <br>
                    </c:forEach>
                </div>
            </div>
        
            <div class="actionArea">
                <div class="fr">
                    <cti:button key="next" styleClass="f_next"/>
                </div>
            </div>
        </div>
        
        <div class="f_page page_1">
            <div class="createSchedule box">
            </div>
        
            <div class="actionArea">
                <cti:button key="previous" styleClass="f_prev"/>
                <div class="fr">
                    <cti:button key="save" styleClass="save f_blocker" />
                    <cti:button key="cancel" styleClass="cancel" />
                </div>
            </div>
        </div>
    </div>
</tags:simplePopup>


<!-- action forms -->
<form name="deleteSchedule" method="POST" action="/spring/stars/consumer/thermostat/schedule/delete">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <tags:confirmDialog nameKey=".deleteConfirm" styleClass="smallSimplePopup" submitName="delete" on=".delete"/>
</form>

<form name="sendSchedule" method="POST" action="/spring/stars/consumer/thermostat/schedule/send">
    <input type="hidden" name="scheduleId">
    <input type="hidden" name="thermostatIds" value="${thermostatIds}">
    <tags:confirmDialog nameKey="yukon.dr.consumer.savedSchedules.sendConfirm" styleClass="smallSimplePopup" submitName="send" on=".send"/>
</form>
<!--  END action forms -->

</cti:standardPage>
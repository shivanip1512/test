<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script language="JavaScript" type="text/javascript"  src="/JavaScript/yukonGeneral.js"></script>

<cti:standardPage module="adminSetup" page="schedules.${mode}">

    <cti:includeCss link="/WebConfig/yukon/styles/shared/thermostat.css"/>
    <cti:includeCss link="/WebConfig/yukon/styles/consumer/StarsConsumerStyles.css"/>
    
    <!-- Add language specific time formatter -->
    <cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
    <cti:includeScript link="${timeFormatter}"/>
    <cti:includeScript link="/JavaScript/temperature.js"/>
    <cti:includeScript link="/JavaScript/thermostatScheduleEditor.js"/>
    <cti:includeScript link="/JavaScript/lib/JSON/2.0/json2.js"/>
    
    <script>
    var TIME_SLIDER = null;
    jQuery(function(){
        Yukon.ThermostatScheduleEditor.init({
            
            thermostat: {
                HEAT: {
                    upper: new Temperature({degrees: parseFloat(${schedule.thermostatType.upperLimitHeat.value}), unit:'F'}),
                    lower: new Temperature({degrees: parseFloat(${schedule.thermostatType.lowerLimitHeat.value}), unit:'F'}),
                    temperature: new Temperature()
                },
                COOL: {
                    upper: new Temperature({degrees: parseFloat(${schedule.thermostatType.upperLimitCool.value}), unit:'F'}),
                    lower: new Temperature({degrees: parseFloat(${schedule.thermostatType.lowerLimitCool.value}), unit:'F'}),
                    temperature: new Temperature()
                },
                mode: '${schedule.thermostatType.defaultThermostatScheduleMode}',
                fan: '',
                secondsResolution: ${schedule.thermostatType.resolution.standardSeconds},
                secondsBetweenPeriods: ${schedule.thermostatType.minimumTimeBetweenPeriods.standardSeconds}
            },
            unit: '${temperatureUnit}'
        });
    });
    </script>
    
<tags:setFormEditMode mode="${mode}"/>

<!-- Sliders -->
<div id="timeSlider" class="slider">
    <div class="chevron"></div>
    <div class="startLabel fl label"></div>
    <div class="track"></div>
    <div class="endLabel fr label"></div>
</div>

<div id="tempSlider" class="slider">
    <div class="chevron"></div>
    <div class="box fl startLabel ${temperatureUnit} label">
        <div class="tempHolder fl"></div><span class="C"><i:inline key="yukon.web.defaults.celsius"/></span><span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
    </div>
    <div class="track"></div>
    <div class="box fr endLabel ${temperatureUnit} label">
        <div class="tempHolder fl"></div><span class="C"><i:inline key="yukon.web.defaults.celsius"/></span><span class="F"><i:inline key="yukon.web.defaults.fahrenheit"/></span>
    </div>
</div>
<!-- END Sliders -->

    <div class="stacked tempControlsNoUpdate">
        <label><input name="units" type="radio" value="C" <c:if test="${temperatureUnit eq 'C'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.celsius"/></label>
        <label><input name="units" type="radio" value="F" <c:if test="${temperatureUnit eq 'F'}" >checked="checked"</c:if>><i:inline key="yukon.web.defaults.fahrenheit"/></label>
    </div>
    
    <tags:thermostatScheduleWidget schedule="${schedule}"
                                thermostatId=""
                                thermostatIds=""
                                accountId=""
                                temperatureUnit="${temperatureUnit}"
                                actionPath="/adminSetup/energyCompany/schedules"
                                thermostatType="${schedule.thermostatType}"
                                styleClass="vh"
                                customActions="true"
                                omitEditor="true">
                                
        <div class="actions pageActionArea">
            <cti:button nameKey="edit" icon="icon-pencil" renderMode="labeledImage" classes="editDefaultSchedule edit_${schedule.accountThermostatScheduleId}" />
        </div>
    </tags:thermostatScheduleWidget>
    
    <i:simplePopup titleKey=".editSchedule.title" id="createSchedule" on=".edit_${schedule.accountThermostatScheduleId}" >
        <div class="f_wizard">
            <div class="f_page page_0">
                <div class="box">
                    <div class="helper box">
                        <i:inline key=".modeHint" />
                    </div>
                    <div class="box pad">
                        <c:forEach var="mode" items="${allowedModes}">
                            <label><input type="radio" name="defaultScheduleMode" value="${mode}" <c:if test="${status.first}">checked</c:if>> <cti:msg key="yukon.web.modules.operator.thermostatMode.${mode}" /> </label>
                            <br>
                        </c:forEach>
                    </div>
                </div>
            
                <div class="actionArea">
                    <div class="fr">
                        <cti:button nameKey="next" classes="f_next"/>
                    </div>
                </div>
            </div>
            
            <div class="f_page page_1">
                <div class="createSchedule box">
                    <c:forEach var="schedule" items="${defaultSchedules}">
                        <tags:thermostatScheduleEditor schedule="${schedule}"
                                        thermostatId="${thermostatId}"
                                        thermostatIds="${thermostatIds}"
                                        accountId="${accountId}"
                                        temperatureUnit="${temperatureUnit}"
                                        actionPath="/adminSetup/energyCompany/schedules/save?ecId=${ecId}"
                                        thermostatType="${thermostatType}"/>
                    </c:forEach>
                </div>
            
                <div class="actions">
                    <div class="fr">
                        <cti:button nameKey="cancel" classes="f-cancel" />
                        <cti:button nameKey="chooseMode" classes="f_prev"/>
                        <cti:button nameKey="save" classes="f-save f_blocker primary action" />
                    </div>
                </div>
            </div>
        </div>
    </i:simplePopup>

</cti:standardPage>
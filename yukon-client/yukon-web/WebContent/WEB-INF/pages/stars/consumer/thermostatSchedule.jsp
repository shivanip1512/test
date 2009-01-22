<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="thermostatSchedule">
    <cti:standardMenu/>
    
    <!-- Add language specific time formatter -->
    <cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
    <cti:includeScript link="${timeFormatter}"/>

    <cti:includeCss link="/WebConfig/yukon/CannonStyle.css"/>
    
    <cti:includeScript link="/JavaScript/nav_menu.js" />
    <cti:includeScript link="/JavaScript/drag.js" />
    <cti:includeScript link="/JavaScript/thermostatSchedule.js" />
    <cti:includeScript link="/JavaScript/temp_conversion.js" />

    
    <cti:msg var="noScheduleName" key="yukon.dr.consumer.thermostatSchedule.noScheduleName" />
    <cti:msg var="saveScheduleText" key="yukon.dr.consumer.thermostatSchedule.saveScheduleText" />
    <cti:msg var="modeChangeText" key="yukon.dr.consumer.thermostatSchedule.modeChangeText" />
    
    
<script type="text/javascript">

    Event.observe(window, 'load', function(){init();});
    
    // Set global variable in thermostat2.js
    tempUnit = '${temperatureUnit}';
    
    function setToDefault() {
        schedules = $H('${defaultScheduleJSONString}'.evalJSON());
        setCurrentSchedule(currentTimePeriod);
    }
    
    var schedules = null;

    function init() {
    
        schedules = $H('${scheduleJSONString}'.evalJSON());
        
        $('schedules').value = schedules.toJSON();
        
        setCurrentSchedule(currentTimePeriod);
    }
    
    
    function saveSchedule(action) {
    
        var scheduleName = $('scheduleName');
        if(scheduleName != null && $F(scheduleName) == ''){
            alert('${noScheduleName}');
            $('scheduleName').focus();
        } else {
            
            // Save the current settings before submitting form
            getCurrentSchedule(currentTimePeriod);
            
            $('saveAction').value = action;
            
            $('scheduleForm').submit();
        }
        
    }
    
    function alertSaveSchedule() {
    
	    if(!viewHasChanged){
	        return true;
	    }
	    
	    var continueNoSave = confirm('${saveScheduleText}');
	    if(continueNoSave) {
	       viewHasChanged = false;
	    }
	    
	    return continueNoSave;
	}
    
    function alertModeChange() {
    
	    var continueNoSave = confirm('${modeChangeText}');
	    if(continueNoSave) {
	       viewHasChanged = false;
	    }
	    
	    return continueNoSave;
	}
    
    
</script>

    <h3>
        <cti:msg key="yukon.dr.consumer.thermostatSchedule.header" /><br>
        <cti:msg key="${thermostatLabel}" htmlEscape="true"/><br>
    </h3>
    
    <br>
    
    <!-- Images for schedule 'slidey bar' -->
    <cti:theme var="thermostatScheduleBackground" key="yukon.dr.consumer.thermostatSchedule.thermostatScheduleBackground" default="/WebConfig/yukon/ThermImages/TempBG2.gif" url="true"/>
    
    <cti:theme var="grayLeftArrow" key="yukon.dr.consumer.thermostatSchedule.grayLeftArrow" default="/WebConfig/yukon/ThermImages/GrayArrowL.gif" url="true"/>
    <cti:theme var="grayRightArrow" key="yukon.dr.consumer.thermostatSchedule.grayRightArrow" default="/WebConfig/yukon/ThermImages/GrayArrowR.gif" url="true"/>
    <cti:theme var="blueArrow" key="yukon.dr.consumer.thermostatSchedule.blueArrow" default="/WebConfig/yukon/ThermImages/BlueArrow.gif" url="true"/>
    <cti:theme var="redArrow" key="yukon.dr.consumer.thermostatSchedule.redArrow" default="/WebConfig/yukon/ThermImages/RedArrow.gif" url="true"/>
    
    <cti:theme var="thermW" key="yukon.dr.consumer.thermostatSchedule.thermW" default="/WebConfig/yukon/ThermImages/ThermW.gif" url="true"/>
    <cti:theme var="thermL" key="yukon.dr.consumer.thermostatSchedule.thermL" default="/WebConfig/yukon/ThermImages/ThermL.gif" url="true"/>
    <cti:theme var="thermR" key="yukon.dr.consumer.thermostatSchedule.thermR" default="/WebConfig/yukon/ThermImages/ThermR.gif" url="true"/>
    <cti:theme var="thermS" key="yukon.dr.consumer.thermostatSchedule.thermS" default="/WebConfig/yukon/ThermImages/ThermS.gif" url="true"/>
    <cti:theme var="thermO" key="yukon.dr.consumer.thermostatSchedule.thermO" default="/WebConfig/yukon/ThermImages/OcTherm.gif" url="true"/>
    <cti:theme var="thermU" key="yukon.dr.consumer.thermostatSchedule.thermU" default="/WebConfig/yukon/ThermImages/UnOcTherm.gif" url="true"/>
    
    
    <c:set var="twoBars" value="${schedule.thermostatType == 'COMMERCIAL_EXPRESSSTAT'}" />
    
    <c:set var="timeOfWeek" value="WEEKDAY" />
    <c:set var="scheduleMode" value="WEEKDAY_SAT_SUN" />
    
    <cti:msg var="degreesCelsius" key="yukon.dr.consumer.thermostatSchedule.degreesCelsius" /> 
    <cti:msg var="degreesFahrenheit" key="yukon.dr.consumer.thermostatSchedule.degreesFahrenheit" /> 
    
    
    
    <div align="center">
    
        <form id="scheduleForm" name="scheduleForm" method="POST" action="/spring/stars/consumer/thermostat/schedule/save">
            <input id="thermostatId" type="hidden" name="thermostatIds" value="${thermostatIds}">

            <input id="temperatureUnit" type="hidden" name="temperatureUnit" value="${temperatureUnit}">
            <input id="schedules" type="hidden" name="schedules">
            <input id="timeOfWeek" type="hidden" name="timeOfWeek" value="WEEKDAY">
            <input id="saveAction" type="hidden" name="saveAction">
            

            <input type="hidden" name="ConfirmOnMessagePage">
            <input type="hidden" name="tempC1" id="tempC1">
            <input type="hidden" name="tempC2" id="tempC2">
            <input type="hidden" name="tempC3" id="tempC3">
            <input type="hidden" name="tempC4" id="tempC4">

            <input type="hidden" name="tempH1" id="tempH1">
            <input type="hidden" name="tempH2" id="tempH2">
            <input type="hidden" name="tempH3" id="tempH3">
            <input type="hidden" name="tempH4" id="tempH4">


            <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
            <c:if test="${multipleThermostatsSelected}">
                <c:url var="allUrl" value="/spring/stars/consumer/thermostat/view/all">
                    <c:param name="thermostatIds" value="${thermostatIds}"></c:param>
                </c:url>
                <a href="${allUrl}"><cti:msg key="yukon.dr.consumer.thermostatSchedule.changeSelected" /></a><br><br>
            </c:if>
            

            <table width="80%" border="1" cellspacing="0" cellpadding="2">
                <tr> 
                    <td align="center" valign="bottom" class="Background"> 
                        <table width="90%" border="0" height="8">
                            <tr> 
                                <td align="left"> 
                                    <input id="allRadio" type="radio" name="scheduleMode" value="ALL" onclick="changeScheduleMode()" ${scheduleMode == 'ALL' ? 'checked' : '' } />
                                    <label class="timePeriodText" for="allRadio">
                                        <cti:msg key="yukon.dr.consumer.thermostatSchedule.scheduleModeAll" />
                                    </label><br>
                                    <cti:checkProperty property="ResidentialCustomerRole.THERMOSTAT_SCHEDULE_5_2">
	                                    <input id="WEEKDAY_WEEKEND" type="radio" name="scheduleMode" value="WEEKDAY_WEEKEND" onclick="changeScheduleMode()" ${scheduleMode == 'WEEKDAY_WEEKEND' ? 'checked' : '' } />
	                                    <label class="timePeriodText" for="52Radio">
	                                        <cti:msg key="yukon.dr.consumer.thermostatSchedule.scheduleMode511" />
	                                    </label><br>
	                                </cti:checkProperty>
                                    <input id="WEEKDAY_SAT_SUN" type="radio" name="scheduleMode" value="WEEKDAY_SAT_SUN" onclick="changeScheduleMode()" ${scheduleMode == 'WEEKDAY_SAT_SUN' ? 'checked' : '' } />
                                    <label class="timePeriodText" for="511Radio">
                                        <cti:msg key="yukon.dr.consumer.thermostatSchedule.scheduleMode52" />
                                    </label><br>
                                </td>
                                <td align="right"> 
                                    <span id="weekdayText" class="timePeriodText" style="display: ${(timeOfWeek != 'WEEKDAY')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.WEEKDAY" /></span> 
                                    <a id="weekdayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'WEEKDAY')? 'none' : ''}" href="javascript:changeTimePeriod('WEEKDAY')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.WEEKDAY" /></a> 

                                    <span id="saturdayText" class="timePeriodText" style="display: ${(timeOfWeek != 'SATURDAY')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SATURDAY" /></span>
                                    <a id="saturdayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'SATURDAY' || scheduleMode == '5-1-1')? '' : 'none'}" href="javascript:changeTimePeriod('SATURDAY')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SATURDAY" /></a> 

                                    <span id="sundayText" class="timePeriodText" style="display: ${(timeOfWeek != 'SUNDAY')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SUNDAY" /></span> 
                                    <a id="sundayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'SUNDAY' || scheduleMode == '5-1-1')? '' : 'none'}" href="javascript:changeTimePeriod('SUNDAY')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SUNDAY" /></a> 

                                    <span id="weekendText" class="timePeriodText" style="display: ${(timeOfWeek != 'WEEKEND')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.WEEKEND" /></span>
                                    <a id="weekendLink" class="timePeriodText" style="display: ${(timeOfWeek == 'WEEKEND' || scheduleMode == '5-2')? '' : 'none'}" href="javascript:changeTimePeriod('WEEKEND')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.WEEKEND" /></a> 
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr> 
                    <td align="center"> 
                        <table width="80%" border="0">
                            <tr>
                                <td class="TableCell" style="width: 50%; text-align: left; vertical-align: top;"> 
                                    <cti:msg key="yukon.dr.consumer.thermostatSchedule.instructions" />
                                </td>
                                <td class="TableCell"  style="width: 50%; text-align: left; vertical-align: top; padding-top: 11px;"> 
                                    &nbsp;
                                </td>
                            </tr>
                            <tr>
                                <td class="TableCell" style="width: 50%; text-align: center; vertical-align: top;"> 
                                    <cti:msg key="yukon.dr.consumer.thermostatSchedule.hints" arguments="/spring/stars/consumer/thermostat/schedule/hints?thermostatIds=${thermostatIds}" />
                                </td>
                                <td class="TableCell"  style="width: 50%; text-align: center; vertical-align: top;"> 
                                    <cti:msg key="yukon.dr.consumer.thermostatSchedule.temporaryAdjustments" arguments="/spring/stars/consumer/thermostat/view?thermostatIds=${thermostatIds}" />
                                </td>
                            </tr>
                        </table>
                        <br>
                        <table width="478" height="186" background="${thermostatScheduleBackground}" style="background-repeat: no-repeat" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="50">
                                    <div id="MovingLayer1" class="movingBar" style="z-index:1;" onMouseDown="beginDrag(event,0,0,getRightBound(1),getLeftBound(1),'showTimeWake()','horizontal','MovingLayer1');viewChanged();">
                                        <table class="movingTable">
                                            <tr>
                                                <td align="center" colspan="2">
                                                    <img src="${(twoBars)? thermO : thermW}" class="barImage">
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%">
                                                    <div id="div1C" class="barCool">
                                                        <img id="arrow1C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateCoolTemp(1)','vertical','div1C');viewChanged();"></img>
                                                    </div>
                                                </td>
                                                <td width="50%">
                                                    <div id="div1H" class="barHeat">
                                                        <img id="arrow1H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateHeatTemp(1)','vertical','div1H');viewChanged();"></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td width="50">
                                    <div id="MovingLayer2" class="movingBar" style="z-index:2; ${(twoBars)? 'display: none;': ''}" onMouseDown="beginDrag(event,0,0,getRightBound(2),getLeftBound(2),'showTimeLeave()','horizontal','MovingLayer2');viewChanged();"> 
                                        <table class="movingTable">
                                            <tr>
                                                <td align="center" colspan="2">
                                                    <img src="${thermL}" class="barImage"> 
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%">
                                                    <div id="div2C" class="barCool">
                                                        <img id="arrow2C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateCoolTemp(2)','vertical','div2C');viewChanged();" ></img>
                                                    </div>
                                                </td>
                                                <td width="50%">
                                                    <div id="div2H" class="barHeat">
                                                        <img id="arrow2H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateHeatTemp(2)','vertical','div2H');viewChanged();" ></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td width="50">
                                    <div id="MovingLayer3" class="movingBar" style="z-index:3; ${(twoBars)? 'display: none;': ''}" onMouseDown="beginDrag(event,0,0,getRightBound(3),getLeftBound(3),'showTimeReturn()','horizontal','MovingLayer3');viewChanged();"> 
                                        <table class="movingTable">
                                            <tr> 
                                                <td align="center" colspan="2">
                                                    <img src="${thermR}" class="barImage"> 
                                                </td>
                                            </tr>
                                            <tr> 
                                                <td width="50%"> 
                                                    <div id="div3C" class="barCool"> 
                                                        <img id="arrow3C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateCoolTemp(3)','vertical','div3C');viewChanged();" ></img>
                                                    </div>
                                                </td>
                                                <td width="50%"> 
                                                    <div id="div3H" class="barHeat"> 
                                                        <img id="arrow3H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateHeatTemp(3)','vertical','div3H');viewChanged();" ></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td width="50">
                                    <div id="MovingLayer4" class="movingBar" style="z-index:4;" onMouseDown="beginDrag(event,0,0,getRightBound(4),getLeftBound(4),'showTimeSleep()','horizontal','MovingLayer4');viewChanged();"> 
                                        <table class="movingTable">
                                            <tr> 
                                                <td align="center" colspan="2">
                                                    <img src="${(twoBars)? thermU : thermS}" class="barImage"> 
                                                </td>
                                            </tr>
                                            <tr> 
                                                <td width="50%"> 
                                                    <div id="div4C" class="barCool"> 
                                                        <img id="arrow4C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateCoolTemp(4)','vertical','div4C');viewChanged();" ></img>
                                                    </div>
                                                </td>
                                                <td width="50%"> 
                                                    <div id="div4H" class="barHeat"> 
                                                        <img id="arrow4H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateHeatTemp(4)','vertical','div4H');viewChanged();" ></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td>&nbsp;</td>
                            </tr>
                        </table>
                        <c:choose>
                            <c:when test="${twoBars}">
                                <table width="100%" border="0" height="27">
                                    <tr>
                                        <td width="10%">&nbsp;</td> 
                                        <td class="timeOfDayLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.occupied" />
                                        </td>
                                        <td width="10%">&nbsp;</td>
                                        <td class="timeOfDayLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.unoccupied" />
                                        </td>
                                    </tr>
                                    <tr> 
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="time1" type="text" size="8" name="time1" onchange="timeChange(this,1);viewChanged();">
                                            <input id="time1min" type="hidden" size="8" name="time1min">
                                            <input id="time2" type="hidden" size="8" name="time2" onchange="timeChange(this,2);viewChanged();">
                                            <input id="time2min" type="hidden" size="8" name="time2min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time3" type="hidden" size="8" name="time3" onchange="timeChange(this,3);viewChanged();">
                                            <input id="time3min" type="hidden" size="8" name="time3min">
                                            <input id="time4" type="text" size="8" name="time4" onchange="timeChange(this,4);viewChanged();">
                                            <input id="time4min" type="hidden" size="8" name="time4min">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.coolTemp" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="tempCin1" type="text" size="3" value="" name="tempCin1" onchange="tempChange(1, coolMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempCin1F">${degreesFahrenheit}</span><span id="tempCin1C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempCin1F" style="display: none;">${degreesFahrenheit}</span><span id="tempCin1C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <span style="display: none;">
                                                <span id="tempCin2F"></span>
                                                <span id="tempCin2C"></span>
                                                <input id="tempCin2" type="hidden" size="3" value="" name="tempCin2" onchange="tempChange(2, coolMode);viewChanged();">
                                            </span>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.coolTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <span style="display: none;">
                                                <input id="tempCin3" type="text" size="3" value="" name="tempCin3" onchange="tempChange(3, coolMode);viewChanged();">
                                                <span id="tempCin3F"></span>
                                                <span id="tempCin3C"></span>
                                             </span>
                                            <input id="tempCin4" type="text" size="3" value="" name="tempCin4" onchange="tempChange(4, coolMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempCin4F">${degreesFahrenheit}</span><span id="tempCin4C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempCin4F" style="display: none;">${degreesFahrenheit}</span><span id="tempCin4C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.heatTemp" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="tempHin1" type="text" size="3" value="" name="tempHin1" onchange="tempChange(1, heatMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempHin1F">${degreesFahrenheit}</span><span id="tempHin1C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempHin1F" style="display: none;">${degreesFahrenheit}</span><span id="tempHin1C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <span style="display: none;">
                                                <span id="tempHin2F"></span>
                                                <span id="tempHin2C"></span>
                                                <input id="tempHin2" type="hidden" size="3" value="" name="tempHin2" onchange="tempChange(2, heatMode);viewChanged();">
                                            </span>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.heatTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <span style="display: none;">
                                                <input id="tempHin3" type="text" size="3" value="" name="tempHin3" onchange="tempChange(3, heatMode);viewChanged();">
                                                <span id="tempHin3F"></span>
                                                <span id="tempHin3C"></span>
                                             </span>
                                            <input id="tempHin4" type="text" size="3" value="" name="tempHin4" onchange="tempChange(4, heatMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempHin4F">${degreesFahrenheit}</span><span id="tempHin4C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempHin4F" style="display: none;">${degreesFahrenheit}</span><span id="tempHin4C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <table width="100%" border="0" height="27">
                                    <tr>
                                        <td width="10%">&nbsp;</td> 
                                        <td class="timeOfDayLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.wake" />
                                        </td>
                                        <td width="10%">&nbsp;</td>
                                        <td class="timeOfDayLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.leave" />
                                        </td>
                                        <td width="10%">&nbsp;</td>
                                        <td class="timeOfDayLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.return" />
                                        </td>
                                        <td width="10%">&nbsp;</td>
                                        <td class="timeOfDayLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.sleep" />
                                        </td>
                                    </tr>
                                    <tr> 
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="time1" type="text" size="8" name="time1" onchange="timeChange(this,1);viewChanged();">
                                            <input id="time1min" type="hidden" size="8" name="time1min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time2" type="text" size="8" name="time2" onchange="timeChange(this,2);viewChanged();">
                                            <input id="time2min" type="hidden" size="8" name="time2min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time3" type="text" size="8" name="time3" onchange="timeChange(this,3);viewChanged();">
                                            <input id="time3min" type="hidden" size="8" name="time3min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time4" type="text" size="8" name="time4" onchange="timeChange(this,4);viewChanged();">
                                            <input id="time4min" type="hidden" size="8" name="time4min">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.coolTemp" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="tempCin1" type="text" size="3" value="" name="tempCin1" onchange="tempChange(1, coolMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempCin1F">${degreesFahrenheit}</span><span id="tempCin1C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempCin1F" style="display: none;">${degreesFahrenheit}</span><span id="tempCin1C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.coolTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempCin2" type="text" size="3" value="" name="tempCin2" onchange="tempChange(2, coolMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempCin2F">${degreesFahrenheit}</span><span id="tempCin2C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempCin2F" style="display: none;">${degreesFahrenheit}</span><span id="tempCin2C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.coolTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempCin3" type="text" size="3" value="" name="tempCin3" onchange="tempChange(3, coolMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempCin3F">${degreesFahrenheit}</span><span id="tempCin3C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempCin3F" style="display: none;">${degreesFahrenheit}</span><span id="tempCin3C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.coolTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempCin4" type="text" size="3" value="" name="tempCin4" onchange="tempChange(4, coolMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempCin4F">${degreesFahrenheit}</span><span id="tempCin4C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempCin4F" style="display: none;">${degreesFahrenheit}</span><span id="tempCin4C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.heatTemp" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="tempHin1" type="text" size="3" value="" name="tempHin1" onchange="tempChange(1, heatMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempHin1F">${degreesFahrenheit}</span><span id="tempHin1C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempHin1F" style="display: none;">${degreesFahrenheit}</span><span id="tempHin1C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.heatTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempHin2" type="text" size="3" value="" name="tempHin2" onchange="tempChange(2, heatMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempHin2F">${degreesFahrenheit}</span><span id="tempHin2C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempHin2F" style="display: none;">${degreesFahrenheit}</span><span id="tempHin2C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.heatTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempHin3" type="text" size="3" value="" name="tempHin3" onchange="tempChange(3, heatMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempHin3F">${degreesFahrenheit}</span><span id="tempHin3C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempHin3F" style="display: none;">${degreesFahrenheit}</span><span id="tempHin3C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.heatTemp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempHin4" type="text" size="3" value="" name="tempHin4" onchange="tempChange(4, heatMode);viewChanged();">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempHin4F">${degreesFahrenheit}</span><span id="tempHin4C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempHin4F" style="display: none;">${degreesFahrenheit}</span><span id="tempHin4C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </table>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <td class="modeText">
                        <cti:msg key="yukon.dr.consumer.thermostatSchedule.mode" />
                        <c:choose>
                            <c:when test="${temperatureUnit == 'F'}">
                                <a id="celsiusLink" href="javascript:setTempUnits('C');">${degreesCelsius}</a>
                                <span id="celsiusSpan" style="display: none;">${degreesCelsius}</span> | 
                                <a id="fahrenheitLink" style="display: none;" href="javascript:setTempUnits('F');">${degreesFahrenheit}</a>
                                <span id="fahrenheitSpan">${degreesFahrenheit}</span>
                            </c:when>
                            <c:otherwise>
                                <a id="celsiusLink" style="display: none;" href="javascript:setTempUnits('C');">${degreesCelsius}</a>
                                <span id="celsiusSpan">${degreesCelsius}</span> | 
                                <a id="fahrenheitLink" href="javascript:setTempUnits('F');">${degreesFahrenheit}</a>
                                <span id="fahrenheitSpan" style="display: none;">${degreesFahrenheit}</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </table>
            
            <br>
            
            <table width="80%" border="0" cellpadding="5">
                <tr>
                    <td style="text-align: center; font-size: .9em;"> 
                        <c:if test="${!multipleThermostatsSelected}">
                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.name"></cti:msg>
                            <input type="text" id="scheduleName" name="scheduleName" value="<spring:escapeBody htmlEscape="true">${schedule.name}</spring:escapeBody>" ></input>
                        </c:if>
                        <input type="hidden" name="scheduleId" value="${schedule.id}" ></input>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;"> 
                        <cti:msg var="saveApply" key="yukon.dr.consumer.thermostatSchedule.saveApply"></cti:msg>
                        <input type="button" name="saveApply" value="${saveApply}" onclick="saveSchedule('saveApply')"></input>
                        <c:if test="${!multipleThermostatsSelected}">
                            <cti:msg var="save" key="yukon.dr.consumer.thermostatSchedule.save"></cti:msg>
                            <input type="button" name="save" value="${save}" onclick="saveSchedule('save');"></input>
                        </c:if>
                        <cti:msg var="recommend" key="yukon.dr.consumer.thermostatSchedule.recommend"></cti:msg>
                        <input type="button" id="Default" value="${recommend}" onclick="setToDefault();"></input>
                    </td>
                </tr>
                <c:if test="${thermostatType == 'UTILITY_PRO'}">
	                <tr>
	                    <td>
	                        <cti:msg key="yukon.dr.consumer.thermostatSchedule.periodMessage"></cti:msg>
	                    </td>
	                </tr>
                </c:if>
            </table>
        </form>
    </div>
                
</cti:standardPage>
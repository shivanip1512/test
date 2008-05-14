<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<cti:standardPage module="consumer" page="thermostatSchedule">
    <cti:standardMenu/>
    
    <cti:includeCss link="/WebConfig/yukon/CannonStyle.css"/>
    
    <cti:includeScript link="/JavaScript/nav_menu.js" />
    <cti:includeScript link="/JavaScript/drag.js" />
    <cti:includeScript link="/JavaScript/thermostatSchedule.js" />
    <cti:includeScript link="/JavaScript/temp_conversion.js" />

    
    <cti:msg var="noScheduleName" key="yukon.dr.consumer.thermostatSchedule.noScheduleName" />
    <c:set var="isCooling" value="${mode == 'COOL'}" />
    
    <!-- Add language specific time formatter -->
    <tags:timeFormatter locale="${localeString}"></tags:timeFormatter>
    
<script type="text/javascript">

    Event.observe(window, 'load', function(){init();});
    
    // Set global variable in thermostat2.js
    thermMode = '${(mode == 'COOL')? "C" : "H" }';
    tempUnit = '${temperatureUnit}';
    
    function setToDefault() {
        schedules = $H('${defaultScheduleJSONString}'.evalJSON());
        setCurrentSchedule(currentTimePeriod);
    }
    
    var schedules = null;
    var currentTimePeriod = 'WEEKDAY';
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
    
    
    <c:set var="coolVisible" value="${(isCooling)? '' : 'none'}" />
    <c:set var="heatVisible" value="${(!isCooling)? '' : 'none'}" />

    <c:set var="twoBars" value="${schedule.thermostatType == 'COMMERCIAL_EXPRESSSTAT'}" />
    
    <c:set var="timeOfWeek" value="WEEKDAY" />
    
    <cti:msg var="degreesCelsius" key="yukon.dr.consumer.thermostatSchedule.degreesCelsius" /> 
    <cti:msg var="degreesFahrenheit" key="yukon.dr.consumer.thermostatSchedule.degreesFahrenheit" /> 
    
    
    
    <div align="center">
    
        <form id="scheduleForm" name="scheduleForm" method="POST" action="/spring/stars/consumer/thermostat/schedule/save">
            <input id="thermostatId" type="hidden" name="thermostatIds" value="${thermostatIds}">

            <input id="temperatureUnit" type="hidden" name="temperatureUnit" value="${temperatureUnit}">
            <input id="schedules" type="hidden" name="schedules">
            <input id="timeOfWeek" type="hidden" name="timeOfWeek" value="WEEKDAY">
            <input id="mode" type="hidden" name="mode" value="${mode}">
            <input id="saveAction" type="hidden" name="saveAction">
            

            <input type="hidden" name="ConfirmOnMessagePage">
            <input type="hidden" name="temp1" id="temp1">
            <input type="hidden" name="temp2" id="temp2">
            <input type="hidden" name="temp3" id="temp3">
            <input type="hidden" name="temp4" id="temp4">


            <c:set var="multipleThermostatsSelected" value="${fn:length(fn:split(thermostatIds, ',')) > 1}"></c:set>
            <c:if test="${multipleThermostatsSelected}">
                <c:url var="allUrl" value="/spring/stars/consumer/thermostat/view/all">
                    <c:param name="thermostatIds" value="${thermostatIds}"></c:param>
                </c:url>
                <a href="${allUrl}"><cti:msg key="yukon.dr.consumer.thermostatSchedule.changeSelected" /></a><br><br>
            </c:if>
            

            <table width="80%" border="1" cellspacing="0" cellpadding="2">
                <tr> 
                    <td align="center"  valign="bottom" class="Background"> 
                        <table width="478" border="0" height="8">
                            <tr> 
                                <td align="left"> 
                                    <span id="weekdayText" class="timePeriodText" style="display: ${(timeOfWeek != 'WEEKDAY')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.WEEKDAY" /></span> 
                                    <a id="weekdayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'WEEKDAY')? 'none' : ''}" href="javascript:switchSchedule('WEEKDAY')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.WEEKDAY" /></a> 

                                    <span id="saturdayText" class="timePeriodText" style="display: ${(timeOfWeek != 'SATURDAY')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SATURDAY" /></span>
                                    <a id="saturdayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'SATURDAY')? 'none' : ''}" href="javascript:switchSchedule('SATURDAY')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SATURDAY" /></a> 

                                    <span id="sundayText" class="timePeriodText" style="display: ${(timeOfWeek != 'SUNDAY')? 'none' : ''}"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SUNDAY" /></span> 
                                    <a id="sundayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'SUNDAY')? 'none' : ''}" href="javascript:switchSchedule('SUNDAY')"><cti:msg key="yukon.dr.consumer.thermostat.schedule.SUNDAY" /></a> 
                                </td>
                                <td align="right"> 
                                    <span style="visibility:${(timeOfWeek == 'WEEKDAY')? 'visible' : 'hidden'}" id="applyToWeekendSpan"> 
                                        <input type="checkbox" id="applyToWeekend" name="applyToWeekend" value="true" onclick="applySettingsToWeekend(this)">
                                        <span class="timePeriodText"><cti:msg key="yukon.dr.consumer.thermostatSchedule.saturdayAndSunday" /></span>
                                    </span>
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
                        <table width="175" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td class="TableCell" style="text-align: right; color: #003366;"> 
                                    <cti:msg key="yukon.dr.consumer.thermostatSchedule.cooling" />
                                    <img src="${blueArrow}"> 
                                </td>
                                <td>
                                    <input id="coolHeat" type="radio" name="coolHeat" value="COOL" onclick="updateMode('COOL')" ${(mode == 'COOL')? 'checked' : ''} />
                                </td>
                                <td>
                                    <input id="coolHeat" type="radio" name="coolHeat" value="HEAT" onclick="updateMode('HEAT')" ${(mode == 'HEAT')? 'checked' : ''} />
                                </td>
                                <td class="TableCell" style="color: #FF0000;">
                                    <img src="${redArrow}">
                                    <cti:msg key="yukon.dr.consumer.thermostatSchedule.heating" />
                                </td>
                            </tr>
                        </table>
                        <table width="478" height="186" background="${thermostatScheduleBackground}" style="background-repeat: no-repeat" border="0" cellspacing="0" cellpadding="0">
                            <tr>
                                <td width="50">
                                    <div id="MovingLayer1" class="movingBar" style="z-index:1;" onMouseDown="beginDrag(event,0,0,getRightBound(1),getLeftBound(1),'showTimeWake()','horizontal','MovingLayer1');">
                                        <table border="0">
                                            <tr>
                                                <td class="barTemperature" colspan="2">
                                                    <span id="tempdisp1"></span>
                                                    <c:choose>
                                                        <c:when test="${temperatureUnit == 'F'}">
                                                            <span id="tempdisp1F">${degreesFahrenheit}</span><span id="tempdisp1C" style="display: none;">${degreesCelsius}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span id="tempdisp1F" style="display: none;">${degreesFahrenheit}</span><span id="tempdisp1C">${degreesCelsius}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td align="center" colspan="2">
                                                    <img src="${(twoBars)? thermO : thermW}" class="barImage">
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%">
                                                    <div id="div1C" class="barCool">
                                                        <img id="arrow1C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(1)','vertical','div1C');" style="display: ${coolVisible};"></img>
                                                        <img id="arrow1C_Gray" src="${grayLeftArrow}" class="barGrayArrow" style="display: ${heatVisible}"></img>
                                                    </div>
                                                </td>
                                                <td width="50%">
                                                    <div id="div1H" class="barHeat">
                                                        <img id="arrow1H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(1)','vertical','div1H');" style="display: ${heatVisible}"></img>
                                                        <img id="arrow1H_Gray" src="${grayRightArrow}" class="barGrayArrow" style="display: ${coolVisible};"></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td width="50">
                                    <div id="MovingLayer2" class="movingBar" style="z-index:2; ${(twoBars)? 'display: none;': ''}" onMouseDown="beginDrag(event,0,0,getRightBound(2),getLeftBound(2),'showTimeLeave()','horizontal','MovingLayer2');"> 
                                        <table border="0">
                                            <tr>
                                                <td class="barTemperature" colspan="2">
                                                    <span id="tempdisp2"></span>
                                                    <c:choose>
                                                        <c:when test="${temperatureUnit == 'F'}">
                                                            <span id="tempdisp2F">${degreesFahrenheit}</span><span id="tempdisp2C" style="display: none;">${degreesCelsius}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span id="tempdisp2F" style="display: none;">${degreesFahrenheit}</span><span id="tempdisp2C">${degreesCelsius}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr>
                                                <td align="center" colspan="2">
                                                    <img src="${thermL}" class="barImage"> 
                                                </td>
                                            </tr>
                                            <tr>
                                                <td width="50%">
                                                    <div id="div2C" class="barCool">
                                                        <img id="arrow2C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(2)','vertical','div2C');" style="display: ${coolVisible}"></img>
                                                        <img id="arrow2C_Gray" src="${grayLeftArrow}" class="barGrayArrow" style="display: ${heatVisible}"></img>
                                                    </div>
                                                </td>
                                                <td width="50%">
                                                    <div id="div2H" class="barHeat">
                                                        <img id="arrow2H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(2)','vertical','div2H');" style="display: ${heatVisible}"></img>
                                                        <img id="arrow2H_Gray" src="${grayRightArrow}" class="barGrayArrow" style="display: ${coolVisible}"></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td width="50">
                                    <div id="MovingLayer3" class="movingBar" style="z-index:3; ${(twoBars)? 'display: none;': ''}" onMouseDown="beginDrag(event,0,0,getRightBound(3),getLeftBound(3),'showTimeReturn()','horizontal','MovingLayer3');"> 
                                        <table border="0">
                                            <tr> 
                                                <td class="barTemperature" colspan="2"> 
                                                    <span id="tempdisp3"></span>
                                                    <c:choose>
                                                        <c:when test="${temperatureUnit == 'F'}">
                                                            <span id="tempdisp3F">${degreesFahrenheit}</span><span id="tempdisp3C" style="display: none;">${degreesCelsius}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span id="tempdisp3F" style="display: none;">${degreesFahrenheit}</span><span id="tempdisp3C">${degreesCelsius}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr> 
                                                <td align="center" colspan="2">
                                                    <img src="${thermR}" class="barImage"> 
                                                </td>
                                            </tr>
                                            <tr> 
                                                <td width="50%"> 
                                                    <div id="div3C" class="barCool"> 
                                                        <img id="arrow3C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(3)','vertical','div3C');" style="display: ${coolVisible}"></img>
                                                        <img id="arrow3C_Gray" src="${grayLeftArrow}" class="barGrayArrow" style="display: ${heatVisible}"></img>
                                                    </div>
                                                </td>
                                                <td width="50%"> 
                                                    <div id="div3H" class="barHeat"> 
                                                        <img id="arrow3H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(3)','vertical','div3H');" style="display: ${heatVisible}"></img>
                                                        <img id="arrow3H_Gray" src="${grayRightArrow }" class="barGrayArrow" style="display: ${coolVisible}"></img>
                                                    </div>
                                                </td>
                                            </tr>
                                        </table>
                                    </div>
                                </td>
                                <td width="50">
                                    <div id="MovingLayer4" class="movingBar" style="z-index:4;" onMouseDown="beginDrag(event,0,0,getRightBound(4),getLeftBound(4),'showTimeSleep()','horizontal','MovingLayer4');"> 
                                        <table border="0">
                                            <tr> 
                                                <td class="barTemperature" colspan="2"> 
                                                    <span id="tempdisp4"></span>
                                                    <c:choose>
                                                        <c:when test="${temperatureUnit == 'F'}">
                                                            <span id="tempdisp4F">${degreesFahrenheit}</span><span id="tempdisp4C" style="display: none;">${degreesCelsius}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span id="tempdisp4F" style="display: none;">${degreesFahrenheit}</span><span id="tempdisp4C">${degreesCelsius}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                            <tr> 
                                                <td align="center" colspan="2">
                                                    <img src="${(twoBars)? thermU : thermS}" class="barImage"> 
                                                </td>
                                            </tr>
                                            <tr> 
                                                <td width="50%"> 
                                                    <div id="div4C" class="barCool"> 
                                                        <img id="arrow4C" src="${blueArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(4)','vertical','div4C');" style="display: ${coolVisible}"></img>
                                                        <img id="arrow4C_Gray" src="${grayLeftArrow}" class="barGrayArrow" style="display: ${heatVisible}"></img>
                                                    </div>
                                                </td>
                                                <td width="50%"> 
                                                    <div id="div4H" class="barHeat"> 
                                                        <img id="arrow4H" src="${redArrow}" onmousedown="beginDrag(event,arrowTopBnd,arrowBottomBnd,0,0,'handleUpdateTemp(4)','vertical','div4H');" style="display: ${heatVisible}"></img>
                                                        <img id="arrow4H_Gray" src="${grayRightArrow}" class="barGrayArrow" style="display: ${coolVisible}"></img>
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
                                            <input id="time1" type="text" size="8" name="time1" onchange="timeChange(this,1);">
                                            <input id="time1min" type="hidden" size="8" name="time1min">
                                            <input id="time2" type="hidden" size="8" name="time2" onchange="timeChange(this,2);">
                                            <input id="time2min" type="hidden" size="8" name="time2min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time3" type="hidden" size="8" name="time3" onchange="timeChange(this,3);">
                                            <input id="time3min" type="hidden" size="8" name="time3min">
                                            <input id="time4" type="text" size="8" name="time4" onchange="timeChange(this,4);">
                                            <input id="time4min" type="hidden" size="8" name="time4min">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.temp" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="tempin1" type="text" size="3" value="" name="tempin1" onchange="tempChange(1);">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempin1F">${degreesFahrenheit}</span><span id="tempin1C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempin1F" style="display: none;">${degreesFahrenheit}</span><span id="tempin1C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                            <span style="display: none;">
                                                <span id="tempin2F"></span>
                                                <span id="tempin2C"></span>
                                                <input id="tempin2" type="hidden" size="3" value="" name="tempin2" onchange="tempChange(2);">
                                            </span>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.temp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <span style="display: none;">
                                                <input id="tempin3" type="text" size="3" value="" name="tempin3" onchange="tempChange(3);">
                                                <span id="tempin3F"></span>
                                                <span id="tempin3C"></span>
                                             </span>
                                            <input id="tempin4" type="text" size="3" value="" name="tempin4" onchange="tempChange(4);">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempin4F">${degreesFahrenheit}</span><span id="tempin4C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempin4F" style="display: none;">${degreesFahrenheit}</span><span id="tempin4C">${degreesCelsius}</span>
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
                                            <input id="time1" type="text" size="8" name="time1" onchange="timeChange(this,1);">
                                            <input id="time1min" type="hidden" size="8" name="time1min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time2" type="text" size="8" name="time2" onchange="timeChange(this,2);">
                                            <input id="time2min" type="hidden" size="8" name="time2min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time3" type="text" size="8" name="time3" onchange="timeChange(this,3);">
                                            <input id="time3min" type="hidden" size="8" name="time3min">
                                        </td>
                                        <td class="fieldLabel" width="10%">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.start" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="time4" type="text" size="8" name="time4" onchange="timeChange(this,4);">
                                            <input id="time4min" type="hidden" size="8" name="time4min">
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.temp" />
                                        </td>
                                        <td class="timeTempField">  
                                            <input id="tempin1" type="text" size="3" value="" name="tempin1" onchange="tempChange(1);">
                                                <c:choose>
                                                    <c:when test="${temperatureUnit == 'F'}">
                                                        <span id="tempin1F">${degreesFahrenheit}</span><span id="tempin1C" style="display: none;">${degreesCelsius}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span id="tempin1F" style="display: none;">${degreesFahrenheit}</span><span id="tempin1C">${degreesCelsius}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.temp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempin2" type="text" size="3" value="" name="tempin2" onchange="tempChange(2);">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempin2F">${degreesFahrenheit}</span><span id="tempin2C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempin2F" style="display: none;">${degreesFahrenheit}</span><span id="tempin2C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.temp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempin3" type="text" size="3" value="" name="tempin3" onchange="tempChange(3);">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempin3F">${degreesFahrenheit}</span><span id="tempin3C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempin3F" style="display: none;">${degreesFahrenheit}</span><span id="tempin3C">${degreesCelsius}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="fieldLabel">
                                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.temp" />
                                        </td>
                                        <td class="timeTempField"> 
                                            <input id="tempin4" type="text" size="3" value="" name="tempin4" onchange="tempChange(4);">
                                            <c:choose>
                                                <c:when test="${temperatureUnit == 'F'}">
                                                    <span id="tempin4F">${degreesFahrenheit}</span><span id="tempin4C" style="display: none;">${degreesCelsius}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span id="tempin4F" style="display: none;">${degreesFahrenheit}</span><span id="tempin4C">${degreesCelsius}</span>
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
                    <td width="85%" align="center"> 
                        <c:if test="${!multipleThermostatsSelected}">
                            <cti:msg key="yukon.dr.consumer.thermostatSchedule.name"></cti:msg>
                            <input type="text" id="scheduleName" name="scheduleName" value="<spring:escapeBody htmlEscape="true">${schedule.name}</spring:escapeBody>" ></input>
                        </c:if>
                        <input type="hidden" name="scheduleId" value="${schedule.id}" ></input>
                    </td>
                </tr>
                <tr>
                    <td width="85%" align="center"> 
                        <c:if test="${!multipleThermostatsSelected}">
                            <cti:msg var="save" key="yukon.dr.consumer.thermostatSchedule.save"></cti:msg>
                            <input type="button" name="save" value="${save}" onclick="saveSchedule('save');"></input>
                        </c:if>
                        <cti:msg var="saveApply" key="yukon.dr.consumer.thermostatSchedule.saveApply"></cti:msg>
                        <input type="button" name="saveApply" value="${saveApply}" onclick="saveSchedule('saveApply')"></input>
                        <cti:msg var="recommend" key="yukon.dr.consumer.thermostatSchedule.recommend"></cti:msg>
                        <input type="button" id="Default" value="${recommend}" onclick="setToDefault();"></input>
                    </td>
                </tr>
            </table>
        </form>
    </div>
                
</cti:standardPage>
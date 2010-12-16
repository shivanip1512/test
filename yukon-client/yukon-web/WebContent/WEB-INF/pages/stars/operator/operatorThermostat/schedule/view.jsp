<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib tagdir="/WEB-INF/tags/dr" prefix="dr"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
    
<cti:standardPage module="operator" page="thermostatSchedule">

    <!-- Add language specific time formatter -->
	<cti:msg var="timeFormatter" key="yukon.common.timeFormatter" />
	<cti:includeScript link="${timeFormatter}"/>
	
	<cti:includeScript link="/JavaScript/drag.js"/>
	<cti:includeScript link="/JavaScript/nav_menu.js"/>
	<cti:includeScript link="/JavaScript/thermostatSchedule.js"/>
	<cti:includeScript link="/JavaScript/temp_conversion.js"/>
	<cti:includeCss link="/WebConfig/yukon/styles/StarsConsumerStyles.css"/>
	<cti:includeCss link="/WebConfig/yukon/styles/operator/thermostat.css"/>
	
	<cti:msg2 var="noScheduleName" key=".noScheduleName" />
	<cti:msg2 var="duplicateScheduleName" key=".duplicateScheduleName" />
	<cti:msg2 var="saveScheduleText" key=".saveScheduleText" />
	<cti:msg2 var="modeChangeText" key=".modeChangeText" />
	<cti:msg2 var="otherThermostatsUsingScheduleConfirm" key=".otherThermostatsUsingScheduleConfirm" />
	
	<script type="text/javascript">

	    Event.observe(window, 'load', function(){init();});

		// things that thermostat.js is expecting to get set dynamically
	    currentScheduleMode = '${scheduleMode}';

	    lowerLimitCool = ${schedule.thermostatType.lowerLimitCoolInFahrenheit};
	    upperLimitCool = ${schedule.thermostatType.upperLimitCoolInFahrenheit};
	    lowerLimitHeat = ${schedule.thermostatType.lowerLimitHeatInFahrenheit};
	    upperLimitHeat = ${schedule.thermostatType.upperLimitHeatInFahrenheit};
	
	    tempUnit = '${temperatureUnit}';
	    
	    function setToDefault() {
	        if(tempUnit == 'F') {
	            schedules = $H('${defaultFahrenheitScheduleJSON}'.evalJSON());
	        } else {
	            schedules = $H('${defaultCelsiusScheduleJSON}'.evalJSON());
	        } 
	        setCurrentSchedule(currentTimePeriod);
	    }
	    
	    var schedules = null;
	
	    function init() {
	    
	        schedules = $H('${scheduleJSONString}'.evalJSON());

	        // convert raw objects to $H objects.  As of prototype 1.6 you cannot directly reference
	        // hash members.  The code in thermostatSchedule.js assumes that the members of  schedules.season
	        // have been constructed as a Hash
	        for(i in schedules.get('season')){
	            var tmp = schedules.get('season')[i];
	            for(var j=0; j<tmp.length; j++){
	                tmp[j] = $H(tmp[j]);
	            }
	        }
	        
	        $('schedules').value = Object.toJSON(schedules);
	        
	        setCurrentSchedule(currentTimePeriod);
	    }
	    
	    function toggleSaveAsNewSchedule() {

			var checked = $('saveAsNewSchedule').checked;

			if (checked) {
				$('scheduleId').value = -1;
			} else {
				$('scheduleId').value = $('initialScheduleId').value;
			}
		}
		
	    function saveSchedule(action) {

		    // edit when other are tied to it?
		    if (!$('saveAsNewSchedule').checked && 
				$F('totalNumberOfThermostatsUsingSchedule') > $F('selectedNumberOfThermostatsUsingSchedule') && 
				!confirm('${otherThermostatsUsingScheduleConfirm}')) {
			    
				return; // noop
		    	
			// save as new without name change?
		    } else if ($('saveAsNewSchedule').checked && $F('initialScheduleName') == $F('scheduleName')) {

				alert('${duplicateScheduleName}');
	            $('scheduleName').focus();

		    // no name?
	    	} else if($F('initialScheduleName').empty()){

	            alert('${noScheduleName}');
	            $('scheduleName').focus();

	        // OK to submit
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
	
    <table class="thermostatPageContent">
    	<tr>
    	
    		<%-- THE SCHEDULE UI --%>
    		<td>
   
   				<tags:formElementContainer nameKey="scheduleUiContainerHeader"> 		
    		
				    <!-- Images for schedule 'slidey bar' -->
				    <cti:theme var="thermostatScheduleBackground" key="yukon.web.modules.operator.thermostatSchedule.thermostatScheduleBackground" default="/WebConfig/yukon/ThermImages/TempBG2.gif" url="true"/>
				    
				    <cti:theme var="grayLeftArrow" key="yukon.web.modules.operator.thermostatSchedule.grayLeftArrow" default="/WebConfig/yukon/ThermImages/GrayArrowL.gif" url="true"/>
				    <cti:theme var="grayRightArrow" key="yukon.web.modules.operator.thermostatSchedule.grayRightArrow" default="/WebConfig/yukon/ThermImages/GrayArrowR.gif" url="true"/>
				    <cti:theme var="blueArrow" key="yukon.web.modules.operator.thermostatSchedule.blueArrow" default="/WebConfig/yukon/ThermImages/BlueArrow.gif" url="true"/>
				    <cti:theme var="redArrow" key="yukon.web.modules.operator.thermostatSchedule.redArrow" default="/WebConfig/yukon/ThermImages/RedArrow.gif" url="true"/>
				    
				    <cti:theme var="thermW" key="yukon.web.modules.operator.thermostatSchedule.thermW" default="/WebConfig/yukon/ThermImages/ThermW.gif" url="true"/>
				    <cti:theme var="thermL" key="yukon.web.modules.operator.thermostatSchedule.thermL" default="/WebConfig/yukon/ThermImages/ThermL.gif" url="true"/>
				    <cti:theme var="thermR" key="yukon.web.modules.operator.thermostatSchedule.thermR" default="/WebConfig/yukon/ThermImages/ThermR.gif" url="true"/>
				    <cti:theme var="thermS" key="yukon.web.modules.operator.thermostatSchedule.thermS" default="/WebConfig/yukon/ThermImages/ThermS.gif" url="true"/>
				    <cti:theme var="thermO" key="yukon.web.modules.operator.thermostatSchedule.thermO" default="/WebConfig/yukon/ThermImages/OcTherm.gif" url="true"/>
				    <cti:theme var="thermU" key="yukon.web.modules.operator.thermostatSchedule.thermU" default="/WebConfig/yukon/ThermImages/UnOcTherm.gif" url="true"/>
				    
				    <c:set var="twoBars" value="${schedule.thermostatType.periodStyle == 'TWO_TIMES'}" />
				    
				    <c:set var="timeOfWeek" value="WEEKDAY" />
				    
				    <div>
				    
				        <form id="scheduleForm" name="scheduleForm" method="POST" action="/spring/stars/operator/thermostatSchedule/confirmOrSave">
				            
				            <input name="accountId" type="hidden" value="${accountId}" />
				            <input name="thermostatIds" type="hidden" value="${thermostatIds}" />
				            <input id="temperatureUnit" type="hidden" name="temperatureUnit" value="${temperatureUnit}">
				            <input id="schedules" type="hidden" name="schedules">
				            <input id="type" type="hidden" name="type" value="${schedule.thermostatType}">
				            <input id="saveAction" type="hidden" name="saveAction">

				            <input id="selectedNumberOfThermostatsUsingSchedule" type="hidden" value="${selectedNumberOfThermostatsUsingSchedule}">
				            <input id="totalNumberOfThermostatsUsingSchedule" type="hidden" value="${totalNumberOfThermostatsUsingSchedule}">
				
				            <input type="hidden" name="ConfirmOnMessagePage">
				            <input type="hidden" name="tempC1" id="tempC1">
				            <input type="hidden" name="tempC2" id="tempC2">
				            <input type="hidden" name="tempC3" id="tempC3">
				            <input type="hidden" name="tempC4" id="tempC4">
				
				            <input type="hidden" name="tempH1" id="tempH1">
				            <input type="hidden" name="tempH2" id="tempH2">
				            <input type="hidden" name="tempH3" id="tempH3">
				            <input type="hidden" name="tempH4" id="tempH4">
				
				            <table width="80%" border="1" cellspacing="0" cellpadding="2">
				                <tr> 
				                    <td align="center" valign="bottom" class="Background"> 
				                        <table width="90%" border="0" height="8">
				                            <tr> 
				                                <td align="left"> 
				                                    <input id="radioALL" type="radio" name="scheduleMode" value="ALL" onclick="changeScheduleMode()" ${scheduleMode == 'ALL' ? 'checked' : '' } />
				                                    <label class="timePeriodText" for="radioALL">
				                                        <cti:msg2 key=".scheduleModeAll" />
				                                    </label><br>
				                                    <c:if test="${schedule.thermostatType == 'UTILITY_PRO' && schedule52Enabled}">
					                                    <input id="radioWEEKDAY_WEEKEND" type="radio" name="scheduleMode" value="WEEKDAY_WEEKEND" onclick="changeScheduleMode()" ${scheduleMode == 'WEEKDAY_WEEKEND' ? 'checked' : '' } />
					                                    <label class="timePeriodText" for="radioWEEKDAY_WEEKEND">
					                                        <cti:msg2 key=".scheduleMode52" />
					                                    </label><br>
						                            </c:if>
				                                    <input id="radioWEEKDAY_SAT_SUN" type="radio" name="scheduleMode" value="WEEKDAY_SAT_SUN" onclick="changeScheduleMode()" ${scheduleMode == 'WEEKDAY_SAT_SUN' ? 'checked' : '' } />
				                                    <label class="timePeriodText" for="radioWEEKDAY_SAT_SUN">
				                                        <cti:msg2 key=".scheduleMode511" />
				                                    </label><br>
				                                </td>
				                                <td align="right"> 
				                                    <span id="everyDayText" class="timePeriodText" style="display: ${(timeOfWeek != 'WEEKDAY' || scheduleMode != 'ALL')? 'none' : ''}"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.EVERYDAY" /></span> 
				
				                                    <span id="weekdayText" class="timePeriodText" style="display: ${(timeOfWeek != 'WEEKDAY' || scheduleMode == 'ALL')? 'none' : ''}"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.WEEKDAY" /></span> 
				                                    <a id="weekdayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'WEEKDAY')? 'none' : ''}" href="javascript:changeTimePeriod('WEEKDAY')"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.WEEKDAY" /></a> 
				
				                                    <span id="saturdayText" class="timePeriodText" style="display: ${(timeOfWeek != 'SATURDAY')? 'none' : ''}"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.SATURDAY" /></span>
				                                    <a id="saturdayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'SATURDAY' || scheduleMode == 'WEEKDAY_SAT_SUN')? '' : 'none'}" href="javascript:changeTimePeriod('SATURDAY')"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.SATURDAY" /></a> 
				
				                                    <span id="sundayText" class="timePeriodText" style="display: ${(timeOfWeek != 'SUNDAY')? 'none' : ''}"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.SUNDAY" /></span> 
				                                    <a id="sundayLink" class="timePeriodText" style="display: ${(timeOfWeek == 'SUNDAY' || scheduleMode == 'WEEKDAY_SAT_SUN')? '' : 'none'}" href="javascript:changeTimePeriod('SUNDAY')"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.SUNDAY" /></a> 
				
				                                    <span id="weekendText" class="timePeriodText" style="display: ${(timeOfWeek != 'WEEKEND')? 'none' : ''}"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.WEEKEND" /></span>
				                                    <a id="weekendLink" class="timePeriodText" style="display: ${(timeOfWeek == 'WEEKEND' || scheduleMode == 'WEEKDAY_WEEKEND')? '' : 'none'}" href="javascript:changeTimePeriod('WEEKEND')"><cti:msg key="yukon.web.modules.operator.thermostat.schedule.WEEKEND" /></a> 
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
				                                    <cti:msg2 key=".instructions" />
				                                </td>
				                                <td class="TableCell"  style="width: 50%; text-align: left; vertical-align: top; padding-top: 11px;"> 
				                                    &nbsp;
				                                </td>
				                            </tr>
				                            <tr>
				                                <td class="TableCell" style="width: 50%; text-align: center; vertical-align: top;"> 
				                                	<cti:url var="hintsUrl" value="/spring/stars/operator/thermostatSchedule/hints">
				                                		<cti:param name="accountId" value="${accountId}"/>
				                                		<cti:param name="thermostatIds" value="${thermostatIds}"/>
				                                	</cti:url>
				                                    <cti:msg2 key=".hints" arguments="${hintsUrl}" htmlEscape="false"/>
				                                </td>
				                                <td class="TableCell"  style="width: 50%; text-align: center; vertical-align: top;"> 
				                                	<cti:url var="tempUrl" value="/spring/stars/operator/thermostatManual/view">
				                                		<cti:param name="accountId" value="${accountId}"/>
				                                		<cti:param name="thermostatIds" value="${thermostatIds}"/>
				                                	</cti:url>
				                                    <cti:msg2 key=".temporaryAdjustments" arguments="${tempUrl}" htmlEscape="false"/>
				                                </td>
				                            </tr>
				                        </table>
				                        <br>
				                        <table width="478" height="186" background="${thermostatScheduleBackground}" style="background-repeat: no-repeat" border="0" cellspacing="0" cellpadding="0">
				                            <tr>
				                                <td width="50">
				                                    <div id="MovingLayer1" class="movingBar" style="z-index:1; ${(twoBars)? 'display: none;': ''}" onMouseDown="beginDrag(event,0,0,getRightBound(1),getLeftBound(1),'showTimeWake()','horizontal','MovingLayer1');viewChanged();">
				                                        <table class="movingTable">
				                                            <tr>
				                                                <td align="center" colspan="2">
				                                                    <img src="${thermW}" class="barImage">
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
				                                    <div id="MovingLayer3" class="movingBar" style="z-index:3;" onMouseDown="beginDrag(event,0,0,getRightBound(3),getLeftBound(3),'showTimeReturn()','horizontal','MovingLayer3');viewChanged();">  
				                                        <table class="movingTable">
				                                            <tr> 
				                                                <td align="center" colspan="2">
				                                                    <img src="${(twoBars)? thermO : thermR}" class="barImage">  
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
				                                            <cti:msg2 key=".occupied" />
				                                        </td>
				                                        <td width="10%">&nbsp;</td>
				                                        <td class="timeOfDayLabel">
				                                            <cti:msg2 key=".unoccupied" />
				                                        </td>
				                                    </tr>
				                                    <tr> 
				                                        <td class="fieldLabel">
				                                            <cti:msg2 key=".start" />
				                                        </td>
				                                        <td class="timeTempField">  
				                                            <input id="time1" type="hidden" size="8" name="time1" onchange="timeChange(this,1);viewChanged();">
				                                            <input id="time1min" type="hidden" size="8" name="time1min">
				                                            <input id="time2" type="hidden" size="8" name="time2" onchange="timeChange(this,2);viewChanged();">
				                                            <input id="time2min" type="hidden" size="8" name="time2min">
				                                            <input id="time3" type="text" size="8" name="time3" onchange="timeChange(this,3);viewChanged();">
				                                            <input id="time3min" type="hidden" size="8" name="time3min">
				                                        </td>
				                                        <td class="fieldLabel" width="10%">
				                                            <cti:msg2 key=".start" />
				                                        </td>
				                                        <td class="timeTempField"> 
				                                            <input id="time4" type="text" size="8" name="time4" onchange="timeChange(this,4);viewChanged();">
				                                            <input id="time4min" type="hidden" size="8" name="time4min">
				                                        </td>
				                                    </tr>
				                                    <tr>
				                                        <td class="fieldLabel">
				                                            <cti:msg2 key=".coolTemp" />
				                                        </td>
				                                        <td class="timeTempField">  
				                                            <span style="display: none;">
				                                                <input id="tempCin1" type="text" size="3" value="" name="tempCin1" onchange="tempChange(1, coolMode);viewChanged();">
				                                                <span id="tempCin1F"></span>
				                                                <span id="tempCin1C"></span>
				                                             </span>
				                                            <span style="display: none;">
				                                                <span id="tempCin2F"></span>
				                                                <span id="tempCin2C"></span>
				                                                <input id="tempCin2" type="hidden" size="3" value="" name="tempCin2" onchange="tempChange(2, coolMode);viewChanged();">
				                                            </span>
				
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
				                                            <cti:msg2 key=".coolTemp" />
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
				                                            <cti:msg2 key=".heatTemp" />
				                                        </td>
				                                        <td class="timeTempField">  
				                                            <span style="display: none;">
				                                                <span id="tempHin1F"></span>
				                                                <span id="tempHin1C"></span>
				                                                <input id="tempHin1" type="hidden" size="3" value="" name="tempHin1" onchange="tempChange(1, heatMode);viewChanged();">
				                                            </span>
				                                            
				                                            <span style="display: none;">
				                                                <span id="tempHin2F"></span>
				                                                <span id="tempHin2C"></span>
				                                                <input id="tempHin2" type="hidden" size="3" value="" name="tempHin2" onchange="tempChange(2, heatMode);viewChanged();">
				                                            </span>
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
				                                            <cti:msg2 key=".heatTemp" />
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
				                            </c:when>
				                            <c:otherwise>
				                                <table width="100%" border="0" height="27">
				                                    <tr>
				                                        <td width="10%">&nbsp;</td> 
				                                        <td class="timeOfDayLabel">
				                                            <cti:msg2 key=".wake" />
				                                        </td>
				                                        <td width="10%">&nbsp;</td>
				                                        <td class="timeOfDayLabel">
				                                            <cti:msg2 key=".leave" />
				                                        </td>
				                                        <td width="10%">&nbsp;</td>
				                                        <td class="timeOfDayLabel">
				                                            <cti:msg2 key=".return" />
				                                        </td>
				                                        <td width="10%">&nbsp;</td>
				                                        <td class="timeOfDayLabel">
				                                            <cti:msg2 key=".sleep" />
				                                        </td>
				                                    </tr>
				                                    <tr> 
				                                        <td class="fieldLabel">
				                                            <cti:msg2 key=".start" />
				                                        </td>
				                                        <td class="timeTempField">  
				                                            <input id="time1" type="text" size="8" name="time1" onchange="timeChange(this,1);viewChanged();">
				                                            <input id="time1min" type="hidden" size="8" name="time1min">
				                                        </td>
				                                        <td class="fieldLabel" width="10%">
				                                            <cti:msg2 key=".start" />
				                                        </td>
				                                        <td class="timeTempField"> 
				                                            <input id="time2" type="text" size="8" name="time2" onchange="timeChange(this,2);viewChanged();">
				                                            <input id="time2min" type="hidden" size="8" name="time2min">
				                                        </td>
				                                        <td class="fieldLabel" width="10%">
				                                            <cti:msg2 key=".start" />
				                                        </td>
				                                        <td class="timeTempField"> 
				                                            <input id="time3" type="text" size="8" name="time3" onchange="timeChange(this,3);viewChanged();">
				                                            <input id="time3min" type="hidden" size="8" name="time3min">
				                                        </td>
				                                        <td class="fieldLabel" width="10%">
				                                            <cti:msg2 key=".start" />
				                                        </td>
				                                        <td class="timeTempField"> 
				                                            <input id="time4" type="text" size="8" name="time4" onchange="timeChange(this,4);viewChanged();">
				                                            <input id="time4min" type="hidden" size="8" name="time4min">
				                                        </td>
				                                    </tr>
				                                    <tr>
				                                        <td class="fieldLabel">
				                                            <cti:msg2 key=".coolTemp" />
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
				                                            <cti:msg2 key=".coolTemp" />
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
				                                            <cti:msg2 key=".coolTemp" />
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
				                                            <cti:msg2 key=".coolTemp" />
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
				                                            <cti:msg2 key=".heatTemp" />
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
				                                            <cti:msg2 key=".heatTemp" />
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
				                                            <cti:msg2 key=".heatTemp" />
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
				                                            <cti:msg2 key=".heatTemp" />
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
				                    
				                    	<cti:msg2 var="degreesCelsius" key=".degreesCelsius" htmlEscape="false"/> 
				    					<cti:msg2 var="degreesFahrenheit" key=".degreesFahrenheit" htmlEscape="false"/>
				                    
				                        <cti:msg2 key=".mode" />
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
				            
				            	<%-- schedule name --%>
				                <tr>
				                    <td> 
			                            <cti:msg2 key=".name"/>
				                        <input type="hidden" id="initialScheduleId" name="initialScheduleId" value="${schedule.accountThermostatScheduleId}"/>
				                        <input type="hidden" id="scheduleId" name="scheduleId" value="${schedule.accountThermostatScheduleId}"/>
			                            <input type="hidden" id="initialScheduleName" value="<spring:escapeBody htmlEscape="true">${schedule.scheduleName}</spring:escapeBody>">
			                            <input type="text" id="scheduleName" name="scheduleName" value="<spring:escapeBody htmlEscape="true">${schedule.scheduleName}</spring:escapeBody>" maxlength="60"/>
				                    	<br><br>
				                    </td>
				                </tr>
				                
				                <%-- checkbox --%>
				                <tr>
				                    <td>
				                    	<label>
					                    	<input type="checkbox" id="saveAsNewSchedule" name="saveAsNewSchedule" onclick="toggleSaveAsNewSchedule();">
					                    	Save as a new Schedule?
				                    	</label>
			                            <br><br>
				                    </td>
				                </tr>
				                
				                <%-- buttons --%>
				                <tr>
				                    <td>
				                    
				                    	<%-- save and send --%>
				                        <cti:msg var="saveApply" key="yukon.web.modules.operator.thermostatSchedule.saveApply"/>
				                        <input type="button" name="saveApply" value="${saveApply}" onclick="saveSchedule('saveApply')"/>
				                    
			                    		<%-- save only --%>
			                            <cti:msg var="save" key="yukon.web.modules.operator.thermostatSchedule.save"/>
			                            <input type="button" name="save" value="${save}" onclick="saveSchedule('save');"/>
			                            
			                            <%-- revert to ec default settings --%>
				                        <cti:msg var="recommend" key="yukon.web.modules.operator.thermostatSchedule.recommend"/>
				                        <input type="button" id="Default" value="${recommend}" onclick="setToDefault();"/>
				                        
				                    </td>
				                </tr>
				                <c:if test="${schedule.thermostatType == 'UTILITY_PRO'}">
					                <tr>
					                    <td>
					                        <cti:msg2 key=".periodMessage"/>
					                    </td>
					                </tr>
				                </c:if>
				            </table>
				        </form>
				    </div>
				    
				</tags:formElementContainer>
    		
    		</td>
    	
    		<td class="selectedThermostatsTd">
    			<%-- THERMOSTAT NAMES --%>
    			<jsp:include page="/WEB-INF/pages/stars/operator/operatorThermostat/selectedThermostatsFragment.jsp" />
    		</td>
    	</tr>
    </table>
    
    
    
	

</cti:standardPage>

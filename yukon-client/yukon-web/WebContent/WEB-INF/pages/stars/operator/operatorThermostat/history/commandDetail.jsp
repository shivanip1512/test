<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<script>
var TIME_SLIDER = null;
$(function() {
    yukon.ThermostatScheduleEditor.init({
        thermostat: {
            HEAT: {
                upper: new Temperature({degrees: parseFloat(${thermostatType.upperLimitHeat.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostatType.lowerLimitHeat.value}), unit:'F'}),
                temperature: new Temperature()
            },
            COOL: {
                upper: new Temperature({degrees: parseFloat(${thermostatType.upperLimitCool.value}), unit:'F'}),
                lower: new Temperature({degrees: parseFloat(${thermostatType.lowerLimitCool.value}), unit:'F'}),
                temperature: new Temperature()
            },
            mode: '${thermostatType.defaultThermostatScheduleMode}',
            fan: '',
            secondsResolution: ${thermostatType.resolution.standardSeconds},
            secondsBetweenPeriods: ${thermostatType.minimumTimeBetweenPeriods.standardSeconds}
        },
        unit: '${temperatureUnit}'
    });
});
</script>
 
   <tags:thermostatScheduleWidget schedule="${schedule}"
                                        thermostatId="${thermostatId}"
                                        thermostatIds="${thermostatIds}"
                                        accountId="${accountId}"
                                        temperatureUnit="${temperatureUnit}"
                                        actionPath=""
                                        omitEditor="true"
                                        customActions="true"
                                        thermostatType="${thermostatType}"/>

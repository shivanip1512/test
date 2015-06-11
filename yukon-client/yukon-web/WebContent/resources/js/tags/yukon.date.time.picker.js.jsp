<%@ page contentType="text/javascript" %>
/* this file consists only of JavaScript */
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

$(function(){
    var cfgLocalization = {
            clearText: "<cti:msg key="yukon.common.js.dateTimePicker.clearText"/>",
            clearStatus: "<cti:msg key="yukon.common.js.dateTimePicker.clearStatus"/>",
            closeText: "<cti:msg key="yukon.common.js.dateTimePicker.closeText"/>",
            closeStatus: "<cti:msg key="yukon.common.js.dateTimePicker.closeStatus"/>",
            prevText: "<cti:msg key="yukon.common.js.dateTimePicker.prevText"/>",
            prevBigText: "<cti:msg key="yukon.common.js.dateTimePicker.prevBigText"/>",
            prevStatus: "<cti:msg key="yukon.common.js.dateTimePicker.prevStatus"/>",
            prevBigStatus: "<cti:msg key="yukon.common.js.dateTimePicker.prevBigStatus"/>",
            nextText: "<cti:msg key="yukon.common.js.dateTimePicker.nextText"/>",
            nextBigText: "<cti:msg key="yukon.common.js.dateTimePicker.nextBigText"/>",
            nextStatus: "<cti:msg key="yukon.common.js.dateTimePicker.nextStatus"/>",
            nextBigStatus: "<cti:msg key="yukon.common.js.dateTimePicker.nextBigStatus"/>",
            currentText: "<cti:msg key="yukon.common.js.dateTimePicker.currentText"/>",
            currentStatus: "<cti:msg key="yukon.common.js.dateTimePicker.currentStatus"/>",
            monthNames: <cti:msg key="yukon.common.js.dateTimePicker.monthNames"/>,
            monthNamesShort: <cti:msg key="yukon.common.js.dateTimePicker.monthNamesShort"/>,
            monthStatus: "<cti:msg key="yukon.common.js.dateTimePicker.monthStatus"/>",
            yearStatus: "<cti:msg key="yukon.common.js.dateTimePicker.yearStatus"/>",
            weekHeader: "<cti:msg key="yukon.common.js.dateTimePicker.weekHeader"/>",
            weekStatus: "<cti:msg key="yukon.common.js.dateTimePicker.weekStatus"/>",
            dayNames: <cti:msg key="yukon.common.js.dateTimePicker.dayNames"/>,
            dayNamesShort: <cti:msg key="yukon.common.js.dateTimePicker.dayNamesShort"/>,
            dayNamesMin: <cti:msg key="yukon.common.js.dateTimePicker.dayNamesMin"/>,
            dayStatus: "<cti:msg key="yukon.common.js.dateTimePicker.dayStatus"/>",
            dateStatus: "<cti:msg key="yukon.common.js.dateTimePicker.dateStatus"/>",
            dateFormat: "<cti:msg key="yukon.common.js.dateTimePicker.dateFormat"/>",
            firstDay: "<cti:msg key="yukon.common.js.dateTimePicker.firstDay"/>",
            initStatus: "<cti:msg key="yukon.common.js.dateTimePicker.initStatus"/>",
            isRTL: <cti:msg key="yukon.common.js.dateTimePicker.isRTL"/>
        },
        cfgTimepickerArgs = {
            timeOnlyTitle: "<cti:msg key="yukon.common.js.timePicker.timeOnlyTitle"/>",
            timeText: "<cti:msg key="yukon.common.js.timePicker.timeText"/>",
            hourText: "<cti:msg key="yukon.common.js.timePicker.hourText"/>",
            minuteText: "<cti:msg key="yukon.common.js.timePicker.minuteText"/>",
            secondText: "<cti:msg key="yukon.common.js.timePicker.secondText"/>",
            millisecText: "<cti:msg key="yukon.common.js.timePicker.millisecText"/>",
            currentText: "<cti:msg key="yukon.common.js.timePicker.currentText"/>",
            closeText: "<cti:msg key="yukon.common.js.timePicker.closeText"/>",
            ampm: <cti:msg key="yukon.common.js.timePicker.ampm"/>
        };
    yukon.ui.initDateTimePickers(cfgLocalization, cfgTimepickerArgs);
});

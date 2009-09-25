function combineDateAndTimeFields(dateTimeInputId) {
    // TODO:  this should happen automatically as these fields are updated
    $(dateTimeInputId).value = $(dateTimeInputId + 'DatePart').value + ' '
        + $(dateTimeInputId + 'TimePart').value;
}

function enableDateTimeInput(inputId) {
    setDateTimeInputEnabled(inputId, true);
}

function disableDateTimeInput(inputId) {
    setDateTimeInputEnabled(inputId, false);
}

function setDateTimeInputEnabled(inputId, enabled) {
	var calendarIdSuffix = '';
    if ($(inputId + 'DatePart')) {
    	calendarIdSuffix = 'DatePart';
        $(inputId + 'TimePart').disabled = !enabled;
    }
    var calendarId = inputId + calendarIdSuffix;
    $('calImg_' + calendarId).disabled = !enabled;
    $(calendarId).disabled = !enabled;
    disabledCalendars[calendarId] = !enabled;
}

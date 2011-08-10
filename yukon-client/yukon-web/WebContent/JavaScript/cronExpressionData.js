cronExpFreqChange = function(id, sel) {
    var selectedFreqVal = sel.options[sel.selectedIndex].value;
    if (selectedFreqVal == 'DAILY') {
        $(id + '_cronExpTimeDiv').show();
        $(id + '_cronExpDailyDiv').show();
        $(id + '_cronExpWeeklyDiv').hide();
        $(id + '_cronExpMonthlyDiv').hide();
        $(id + '_cronExpOneTimeDiv').hide();
        $(id + '_cronExpCustomDiv').hide();
    }
    else if (selectedFreqVal == 'WEEKLY') {
        $(id + '_cronExpTimeDiv').show();
        $(id + '_cronExpDailyDiv').hide();
        $(id + '_cronExpWeeklyDiv').show();
        $(id + '_cronExpMonthlyDiv').hide();
        $(id + '_cronExpOneTimeDiv').hide();
        $(id + '_cronExpCustomDiv').hide();
    }
    else if (selectedFreqVal == 'MONTHLY') {
        $(id + '_cronExpTimeDiv').show();
        $(id + '_cronExpDailyDiv').hide();
        $(id + '_cronExpWeeklyDiv').hide();
        $(id + '_cronExpMonthlyDiv').show();
        $(id + '_cronExpOneTimeDiv').hide();
        $(id + '_cronExpCustomDiv').hide();
    }
    else if (selectedFreqVal == 'ONETIME') {
        $(id + '_cronExpTimeDiv').show();
        $(id + '_cronExpDailyDiv').hide();
        $(id + '_cronExpWeeklyDiv').hide();
        $(id + '_cronExpMonthlyDiv').hide();
        $(id + '_cronExpOneTimeDiv').show();
        $(id + '_cronExpCustomDiv').hide();
    }
    else if (selectedFreqVal == 'CUSTOM') {
        $(id + '_cronExpTimeDiv').hide();
        $(id + '_cronExpDailyDiv').hide();
        $(id + '_cronExpWeeklyDiv').hide();
        $(id + '_cronExpMonthlyDiv').hide();
        $(id + '_cronExpOneTimeDiv').hide();
        $(id + '_cronExpCustomDiv').show();
    }
}
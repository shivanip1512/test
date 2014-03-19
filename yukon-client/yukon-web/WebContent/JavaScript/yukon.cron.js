cronExpFreqChange = function (id, sel) {
    var selectedFreqVal = sel.options[sel.selectedIndex].value,
        cronTime = $('#' + id + '_cronExpTimeDiv'),
        cronDaily = $('#' + id + '_cronExpDailyDiv'),
        cronWeekly = $('#' + id + '_cronExpWeeklyDiv'),
        cronMonthly = $('#' + id + '_cronExpMonthlyDiv'),
        cronOneTime = $('#' + id + '_cronExpOneTimeDiv'),
        cronCustom = $('#' + id + '_cronExpCustomDiv'),
        cronFuncs = [cronTime, cronDaily, cronWeekly, cronMonthly, cronOneTime, cronCustom],
        plan = [],
        cfi;
    switch (selectedFreqVal) {
    case 'DAILY':
        plan = ['s', 's', 'h', 'h', 'h', 'h'];
        break;
    case 'WEEKLY':
        plan = ['s', 'h', 's', 'h', 'h', 'h'];
        break;
    case 'MONTHLY':
        plan = ['s', 'h', 'h', 's', 'h', 'h'];
        break;
    case 'ONETIME':
        plan = ['s', 'h', 'h', 'h', 's', 'h'];
        break;
    case 'CUSTOM':
        plan = ['h', 'h', 'h', 'h', 'h', 's'];
        break;
    default:
        return;
    }
    for (cfi = 0; cfi < plan.length; cfi += 1) {
        cronFuncs[cfi]['s' === plan[cfi] ? 'show' : 'hide']();
    }
};
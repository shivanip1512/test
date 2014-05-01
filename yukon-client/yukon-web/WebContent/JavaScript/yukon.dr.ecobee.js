yukon.namespace('yukon.dr.ecobee');
yukon.dr.ecobee = (function () {
    var timeFormatter = yukon.timeFormatter,
        slideOrChange = function (event, ui) {
            var curTime = timeFormatter.formatTime(ui.value, 0);
            $('#ecobee-download-schedule .f-time-label').html(curTime);
            $('#ecobee-download-time').val(ui.value);
        },
        _addNumbers = function () {
            var nargs = arguments.length,
                ni,
                total = 0;
            for(ni = 0; ni < nargs; ni += 1) {
                total += arguments[ni];
            }
            return total;
        },
        convertToPercentage = function (count, total) {
            return (count / total) * 100;
        },
        pad = function (val, padVal) {
            var newVal = val.toString();
            while(newVal.length < padVal) {
                newVal = '&nbsp;' + newVal;
            }
            return newVal;
        },
        queryStatisticElements = $('.query-statistics'),
        mod;

    mod = {
        init : function (debugFlag) {
            $('#ecobee-download-schedule .f-time-slider').slider({
                max: 24 * 60 - 15,
                min: 0,
                value: 120,
                step: 15,
                slide: function (event, ui) {
                    slideOrChange(event, ui);
                },
                change: function (event, ui) {
                    slideOrChange(event, ui);
                }
            });
    
            $('#ecobee-download-schedule .f-time-label').html(timeFormatter.formatTime($('#ecobee-download-time').val(), 0));

            queryStatisticElements = $('.query-statistics');
            queryStatisticElements.each(function (index, elem) {
                var queryCounts = $(elem).data('queryCounts'),
                    currentMonthDataCollectionCount = parseInt(queryCounts.currentMonthDataCollectionCount, 10),
                    currentMonthDemandResponseCount = parseInt(queryCounts.currentMonthDemandResponseCount, 10),
                    currentMonthSystemCount = parseInt(queryCounts.currentMonthSystemCount, 10),
                    queryTotal,
                    progressSuccess,
                    progressInfo,
                    progressDefault,
                    nextSib = $(elem).next();
            
                queryTotal = _addNumbers(currentMonthDataCollectionCount, currentMonthDemandResponseCount, currentMonthSystemCount);
                if ('undefined' !== typeof debugFlag && true === debugFlag) {
                    console.log(index + ': queryTotal=' + queryTotal + ' currentMonthDataCollectionCount=' + currentMonthDataCollectionCount,
                        ' currentMonthDemandResponseCount=' + currentMonthDemandResponseCount,
                        ' currentMonthSystemCount=' + currentMonthSystemCount);
                }
                progressSuccess = convertToPercentage(currentMonthDemandResponseCount, queryTotal).toString() + '%';
                progressInfo = convertToPercentage(currentMonthDataCollectionCount, queryTotal).toString() + '%';
                progressDefault = convertToPercentage(currentMonthSystemCount, queryTotal).toString() + '%';
                if ('undefined' !== typeof debugFlag && true === debugFlag) {
                    console.log(index + ': progressSuccess=' + progressSuccess + ' progressInfo=' + progressInfo + ' progressDefault=' + progressDefault);
                }
                nextSib.find('.query-total').html(queryTotal);
                nextSib.find('.label-success').html(pad(currentMonthDemandResponseCount, 5));
                nextSib.find('.label-info').html(pad(currentMonthDataCollectionCount, 5));
                nextSib.find('.label-default').html(pad(currentMonthSystemCount, 5));
                $(elem).find('div.progress-bar-success').css('width', progressSuccess);
                $(elem).find('div.progress-bar-info').css('width', progressInfo);
                $(elem).find('div.progress-bar-default').css('width', progressDefault);
            });
        }
    };
    return mod;
})();
$(function () {
    var debugOn = false;
    yukon.dr.ecobee.init(debugOn);
});

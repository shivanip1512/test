
yukon.namespace('yukon.ami');
yukon.namespace('yukon.ami.peakDayProfile');

yukon.ami.peakDayProfile = (function () {
    var mod,
        // helper function to reset days after options
        _setAvailableValuesForDaysAfterSelectElement = function (selectElement, values) {
            values.forEach(function (optVal, index, arr) {
                var newOpt = document.createElement('option');
                newOpt.setAttribute('value', optVal);
                newOpt.appendChild(document.createTextNode(optVal));
                selectElement.appendChild(newOpt);
           });
        };

    mod = {
        peakDayProfile_start: function (divId, profileRequestOrigin) {
            var initiateComplete,
                args,
                url,
                divSel = '#' + divId;
        
            $(divSel + '_startButton').prop('disabled', true);
        
            initiateComplete = function (transport, json) {
                alert(json['returnMsg']);
                $(divSel + '_startButton').prop('disabled', false);
            };
        
            args = {};
            args.deviceId = $(divSel + '_deviceId').val();
            args.email = $(divSel + '_email').val();
            args.peakDate = $(divSel + '_selectedPeakDate').val();
            args.startDate = $(divSel + '_startDate').val();
            args.stopDate = $(divSel + '_stopDate').val();
            args.beforeDays = $(divSel + '_beforeDays').val();
            args.afterDays = $(divSel + '_afterDays').val();
            args.profileRequestOrigin = profileRequestOrigin;
        
            url = '/meter/highBill/initiateLoadProfile';
            $.ajax({
                url: url,
                type: 'POST',
                data: args
            }).done(function (data, textStatus, jqXHR) {
                var jsonData = yukon.ui.util.getHeaderJSON(jqXHR);
                initiateComplete(jqXHR, jsonData);
            }).fail(function (jqXHR, textStatus, errorThrown) {
                var jsonData = {};
                jsonData.returnMsg = jqXHR.responseText;
                initiateComplete(jqXHR, jsonData);
            });
        },
        changePeak: function () {
            // convert el lists into js arrays
            var preAvailableDaysAfterPeak = [],
                postAvailableDaysAfterPeak = [],
                afterDaysSelectElement,
                afterDaysItemsCount,
                i,
                peakDaySelectElement,
                idsAndDates = $('[data-ids-and-dates]').data('idsAndDates');

            preAvailableDaysAfterPeak = $('[data-pre-days]').data('preDays').preDaysAfterPeak;

            postAvailableDaysAfterPeak = $('[data-post-days]').data('postDays').postDaysAfterPeak;

            // remove after days options so they can be reset
            afterDaysSelectElement = $('#' + idsAndDates.afterDaysId)[0];
            afterDaysItemsCount = afterDaysSelectElement.options.length;
            for (i = afterDaysItemsCount - 1; i >= 0; i -= 1) {
                afterDaysSelectElement.remove(i);
            }

            // set start stop date for selected peak date
            // reset days after drop down options
            peakDaySelectElement = $('#' + idsAndDates.peakDateId)[0];

            if (peakDaySelectElement.selectedIndex === 0) {
                $('#' + idsAndDates.startDateId).val(idsAndDates.preStartDate);
                $('#' + idsAndDates.stopDateId).val(idsAndDates.preStopDate);

                _setAvailableValuesForDaysAfterSelectElement(afterDaysSelectElement, preAvailableDaysAfterPeak);
            }
            else {
                $('#' + idsAndDates.startDateId).val(idsAndDates.postStartDate);
                $('#' + idsAndDates.stopDateId).val(idsAndDates.postStopDate);

                _setAvailableValuesForDaysAfterSelectElement(afterDaysSelectElement, postAvailableDaysAfterPeak);
            }
        }
    };
    return mod;
})();
// initiate profile collection
yukon.namespace('yukon.PeakDayProfile');

yukon.PeakDayProfile = (function () {
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
        
            jQuery(divSel + '_startButton').prop('disabled', true);
        
            initiateComplete = function (transport, json) {
                alert(json['returnMsg']);
                jQuery(divSel + '_startButton').prop('disabled', false);
            };
        
            args = {};
            args.deviceId = jQuery(divSel + '_deviceId').val();
            args.email = jQuery(divSel + '_email').val();
            args.peakDate = jQuery(divSel + '_selectedPeakDate').val();
            args.startDate = jQuery(divSel + '_startDate').val();
            args.stopDate = jQuery(divSel + '_stopDate').val();
            args.beforeDays = jQuery(divSel + '_beforeDays').val();
            args.afterDays = jQuery(divSel + '_afterDays').val();
            args.profileRequestOrigin = profileRequestOrigin;
        
            url = '/meter/highBill/initiateLoadProfile';
            jQuery.ajax({
                url: url,
                type: 'POST',
                data: args
            }).done(function (data, textStatus, jqXHR) {
                var jsonData = yukon.ui.aux.getHeaderJSON(jqXHR);
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
                idsAndDates = jQuery('[data-ids-and-dates]').data('idsAndDates');

            preAvailableDaysAfterPeak = jQuery('[data-pre-days]').data('preDays').preDaysAfterPeak;

            postAvailableDaysAfterPeak = jQuery('[data-post-days]').data('postDays').postDaysAfterPeak;

            // remove after days options so they can be reset
            afterDaysSelectElement = jQuery('#' + idsAndDates.afterDaysId)[0];
            afterDaysItemsCount = afterDaysSelectElement.options.length;
            for (i = afterDaysItemsCount - 1; i >= 0; i -= 1) {
                afterDaysSelectElement.remove(i);
            }

            // set start stop date for selected peak date
            // reset days after drop down options
            peakDaySelectElement = jQuery('#' + idsAndDates.peakDateId)[0];

            if (peakDaySelectElement.selectedIndex === 0) {
                jQuery('#' + idsAndDates.startDateId).val(idsAndDates.preStartDate);
                jQuery('#' + idsAndDates.stopDateId).val(idsAndDates.preStopDate);

                _setAvailableValuesForDaysAfterSelectElement(afterDaysSelectElement, preAvailableDaysAfterPeak);
            }
            else {
                jQuery('#' + idsAndDates.startDateId).val(idsAndDates.postStartDate);
                jQuery('#' + idsAndDates.stopDateId).val(idsAndDates.postStopDate);

                _setAvailableValuesForDaysAfterSelectElement(afterDaysSelectElement, postAvailableDaysAfterPeak);
            }
        }
    };
    return mod;
})();
/**
 * Singleton that serves the DR home page ecobee pane and the ecobee details page
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */
yukon.namespace('yukon.dr.ecobee');
yukon.dr.ecobee = (function () {
    var _timeFormatter = yukon.timeFormatter,
        /** 
         * Setup a slider.
         * @param {String} containingDivSelector. div containing the .f-time-slider and .f-time-label
         * @param {String} inputSelector. - the actual hidden input that holds the current value of this slider
         */
        _setupSlider = function (containingDivSelector, inputSelector) {
            var value = $(inputSelector).val();
            $(containingDivSelector +' .f-time-slider').slider({
                max: 24 * 60 - 15,
                min: 0,
                value: value,
                step: 15,
                slide: function (event, ui) {
                    $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                    $(inputSelector).val(ui.value);
                },
                change: function (event, ui) {
                    $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(ui.value, 0));
                    $(inputSelector).val(ui.value);
                }
            });
            $(containingDivSelector + ' .f-time-label').text(_timeFormatter.formatTime(value, 0));
        },
        mod;

    mod = {
        /** 
         * Initialize ecobee module: time slider and initialize value for time label.
         */
        init : function () {
            _setupSlider('#ecobee-download-schedule', '#ecobee-download-time');
            _setupSlider('#ecobee-data-collection-schedule', '#ecobee-data-collection-time');
            _setupSlider('#ecobee-error-check-schedule', '#ecobee-error-check-time');

            $('#ecobee-download-schedule .f-time-label').html(_timeFormatter.formatTime($('#ecobee-download-time').val(), 0));
            try {
                if ('undefined' !== typeof loadGroupPicker) {
                    loadGroupPicker.show.call(loadGroupPicker, true);
                }
            } catch (pickerException) {
                debug.log('pickerException: ' + pickerException);
            };
            $(document).on('ecobeeDownload', function(event) {
                
                loadGroupPicker.endAction.call(loadGroupPicker, loadGroupPicker.selectedItems);
                
                $('#ecobee-download form').ajaxSubmit({
                    url: 'ecobee/download/start', 
                    type: 'post',
                    success: function(data, status, xhr, $form) {
                        debug.log('it worked. resultKey: ' + data.resultKey);
                        $('#ecobee-download').dialog('close');
                    },
                    error: function(xhr, status, error, $form) {
                        debug.log('oh gawd!');
                    }
                });
            });

            $(document).on('click', '#ecobee-error-checking-toggle .toggle-on-off .button', function () {
                var checkErrorsOn = $('#ecobee-error-checking-toggle .toggle-on-off .button.yes').hasClass('on');
                if (checkErrorsOn) {
                    $('#ecobee-check-errors').val('true');
                    $('#ecobee-error-check-schedule').show('fade');
                } else {
                    $('#ecobee-check-errors').val('false');
                    $('#ecobee-error-check-schedule').hide('fade');
                }
            });

            $(document).on('click', '#ecobee-data-collection-toggle .toggle-on-off .button', function() {
                var dataCollectionOn = $('#ecobee-data-collection-toggle .toggle-on-off .button.yes').hasClass('on');
                if (dataCollectionOn) {
                    $('#ecobee-data-collection').val('true');
                    $('#ecobee-data-collection-schedule').show('fade');
                } else {
                    $('#ecobee-data-collection').val('false');
                    $('#ecobee-data-collection-schedule').hide('fade');
                    
                }
            });

            if ('true' === $('#ecobee-check-errors').val()) {
                $('#ecobee-error-checking-toggle .toggle-on-off .yes').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-on-off .no').removeClass('on');
                $('#ecobee-error-check-schedule').show();
            } else {
                $('#ecobee-error-checking-toggle .toggle-on-off .no').addClass('on');
                $('#ecobee-error-checking-toggle .toggle-on-off .yes').removeClass('on');
                $('#ecobee-error-check-schedule').hide();
            }

            if ('true' === $('#ecobee-data-collection').val()) {
                $('#ecobee-data-collection-toggle .toggle-on-off .yes').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-on-off .no').removeClass('on');
                $('#ecobee-data-collection-schedule').show();
            } else {
                $('#ecobee-data-collection-toggle .toggle-on-off .no').addClass('on');
                $('#ecobee-data-collection-toggle .toggle-on-off .yes').removeClass('on');
                $('#ecobee-data-collection-schedule').hide();
            }
        },
        assignInputs : function (devices) {
            var pickerThis = loadGroupPicker,
                loadGroupDiv = document.getElementById('loadGroup'),
                ssInputId = 'picker_' + pickerThis.pickerId + '_ss',
                ssInputElem = document.getElementById(ssInputId);
            console.log('devices=%O',devices);
            $.each(devices, function (key, selectedItem) {
                var inputElement = document.createElement('input');
                inputElement.type = 'hidden';
                inputElement.value = selectedItem[pickerThis.idFieldName];
                inputElement.name = pickerThis.destinationFieldName;
                loadGroupDiv.appendChild(inputElement);
            });
            // Inline pickers create a hidden input named 'ss'.
            // We don't want to send that up to the server here.
            if ('undefined' !== typeof ssInputElem && ssInputElem) {
                ssInputElem.parentNode.removeChild(ssInputElem);
            }
            console.log("awesome! populated " + pickerThis.destinationFieldName + " with " + devices.length + " values");
            return true;
        },
        updater : function (stuff) {
            var i;
            for (i = 0; i < stuff.length; i += 1) {
                console.log('arg[' + i + ']=' + arguments[i]);
            }
        }
    };
    return mod;
})();
$(function () {
    yukon.dr.ecobee.init();
});

yukon.namespace('yukon.dev.simulators.eatonCloudSimulator');

/**
 * Module handling Eaton Cloud Simulator
 * @module yukon.dev.simulators.pwMWSimulator
 * @requires JQUERY
 * @requires yukon
 */

yukon.dev.simulators.eatonCloudSimulator = ( function() {
    
    'use strict';

    var 
    _initialized = false,
    _json= {
        TREND_DATA_RETRIEVAL: {
            "devices": [{
                "device_id": "12343adc-4e23-4a12-3456-e4ca6841111",
                "tag_trait": "110739,112058,112059,112060,110744,112067,112068,112069,111870,110745,110752,110597,110596,112064,112065,112066,110751,110741,110595,112061,112062,112063,110749,110750,110746,110743,110599,110747,112070,112071,112072,110748,112073,112074,112075,110600,110742"
            },
            {
                "device_id": "12343adc-4e23-4a12-3456-e4ca684e2222",
                "tag_trait": "110739,112058"
            },
            {
                "device_id": "12343adc-4e23-4a12-3456-e4ca6843333",
                "tag_trait": "110739,112058"
            }],
            "start_time": "2021-02-03T00:00:00Z",
            "end_time": "2021-02-16T00:00:00Z"
        },
        COMMANDS: {
            "method": "LCR_Control",
            "params": {
                "vrelay": "1",
                "cycle percent": "50",
                "cycle period": "30",
                "cycle count": "4",
                "start time": "1599137389",
                "event ID": "1234",
                "criticality": "3",
                "randomization": "controlled",
                "flags": "standard"
            }
        }
    },

    mod = {
        init : function() {
            
            if (_initialized) return;
            
            //prefill json data
            $('.js-json-text').each(function(i, item) {
                var endpoint = $(item).data('endpoint');
                $(item).val(JSON.stringify(_json[endpoint], undefined, 4));
            });
            
            $(document).on('change', '.js-selected-status', function () {
            	//hide success percentage if status is not OK
            	var okSelected = $(this).val() === 'OK';
            	$(this).siblings('.js-success-percentage-fields').toggleClass('dn', !okSelected);
                $('#eatonCloudForm').submit();
            });
            
            $(document).on('change', '.js-success-percentage', function () {
                //submit all settings
                $('#eatonCloudForm').submit();
            });
            
            $(document).on('click', '.js-test-endpoint', function () {
                var endpoint = $(this).data('endpoint'),
                params = $('#' + endpoint + '_parameters').val(),
                json = $('#' + endpoint + '_json').val(),
                parameters = params ? '&params=' + encodeURIComponent(params) : '',
                jsonParameters = json ? '&jsonParam=' + encodeURIComponent(json) : '';
                $.getJSON(yukon.url('/dev/eatonCloud/testEndpoint?endpoint=' + endpoint + parameters + jsonParameters))
                .done(function (json) {
                    var resultJson = $('.js-test-endpoint-results');
                    if (json.testResultJson) {
                        resultJson.html(json.testResultJson)
                    }
                    if (json.errorMessage) {
                        resultJson.html(json.errorMessage);
                    }
                    if (json.alertError) {
                        yukon.ui.alertError(json.alertError);
                    }
                    resultJson.removeClass('dn');
                    
                });
            });
            
            $(document).on('click', '.js-clear-cache', function () {
                $.ajax({
                    type: 'POST',
                    url: yukon.url('/dev/eatonCloud/clearCache')
                }).done(function(json) {
                   if (json.userMessage) {
                       yukon.ui.alertSuccess(json.userMessage);
                   } 
                });
            });

            $(document).on('click', '.js-enter-json', function () {
                var endpoint = $(this).data('endpoint'),
                    textarea = $('#' + endpoint + '_json');
                if (textarea.val() === '') {
                    textarea.val(JSON.stringify(_json[endpoint], undefined, 4));
                }
                textarea.toggleClass('dn');
            });
            

            $(document).on('click', '.js-auto-creation-submit', function () {
                var form = $('#autoCreationForm');
                $.ajax({
                    type: 'POST',
                    data: form.serialize(),
                    url: yukon.url('/dev/eatonCloud/deviceAutoCreation')
                }).done(function(data) {
                   if (data.successMessage) {
                       yukon.ui.alertSuccess(data.successMessage);
                   } else if (data.errorMessage) {
                       yukon.ui.alertError(data.errorMessage);
                   }
                });
            });
            
            _initialized = true;
        },

    };
    return mod;
}());

$(function() { yukon.dev.simulators.eatonCloudSimulator.init(); });

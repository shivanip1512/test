yukon.namespace('yukon.ami.meterProgramming.summary');
/**
 * Module for the Meter Programming Summary pages
 * @module yukon.ami.meterProgramming.summary
 * @requires JQUERY
 * @requires YUKON_UPDATER
 * @requires yukon
 */
yukon.ami.meterProgramming.summary = (function () {
    

    'use strict';
    var _initialized = false,

    mod = {

            /** Initialize this module. */
            init: function () {
                                
                if (_initialized) return;
                
                $('.js-selected-programs').chosen({width: "300px"});
                
                $(document).on("yukon:program:delete", function(event) {
                    var dropdownOption = $(event.target),
                        guid = dropdownOption.data('programGuid');
                    $.ajax({
                        url: yukon.url('/amr/meterProgramming/' + guid + '/delete'),
                        type: 'delete'
                    }).done(function () {
                        window.location.reload();
                    });
                });
                
                $(document).on('click', '.js-filter-programs', function() {
                    var selectedOptions = $('.js-selected-programs option:selected'),
                        length = selectedOptions.length,
                        tableContainer = $('#results-container'),
                        form = $('#filter-form');
                    //remove any existing program fields
                    form.find('.js-program-fields').remove();
                    for (var i = 0; i < length; i++) {
                        var program =  selectedOptions[i],
                            guid = program.dataset.guid,
                            source = program.dataset.source,
                            name = program.text;
                        form.append('<input type="hidden" class="js-program-fields" name="programs[' + i + '].guid" value="' + guid + '" />');
                        form.append('<input type="hidden" class="js-program-fields" name="programs[' + i + '].name" value="' + name + '" />');
                        form.append('<input type="hidden" class="js-program-fields" name="programs[' + i + '].source" value="' + source + '" />');
                    }
                    form.ajaxSubmit({
                        success: function(data, status, xhr, $form) {
                            tableContainer.html(data);
                            tableContainer.data('url', yukon.url('/amr/meterProgramming/summaryFilter?' + form.serialize()));
                        }
                    });   
                });
                
                $(document).on('click', '.js-download', function () {
                    var form = $('#filter-form'),
                        data = form.serialize();
                    window.location = yukon.url('/amr/meterProgramming/summaryDownload?' + data);
                });
                
                $(document).on('click', '.js-read', function () {
                    var id = $(this).data('id');
                    $.ajax({
                        type: 'POST',
                        url: yukon.url('/amr/meterProgramming/' + id + '/readProgramming')
                    }).done(function(data) {
                        if (data.successMsg) {
                            yukon.ui.alertSuccess(data.successMsg);
                        } else {
                            yukon.ui.alertError(data.errorMsg);
                        }
                    });
                });
                
                $(document).on('click', '.js-resend', function () {
                    var id = $(this).data('id'),
                        guid = $(this).data('guid');
                    $.ajax({
                        type: 'POST',
                        data: {
                            guid: guid
                        },
                        url: yukon.url('/amr/meterProgramming/' + id + '/resendProgramming')
                    }).done(function(data) {
                        if (data.successMsg) {
                            yukon.ui.alertSuccess(data.successMsg);
                        } else {
                            yukon.ui.alertError(data.errorMsg);
                        }
                    });
                });
                
                $(document).on('click', '.js-cancel', function () {
                    var id = $(this).data('id'),
                        guid = $(this).data('guid');
                    $.ajax({
                        type: 'POST',
                        data: {
                            guid: guid
                        },
                        url: yukon.url('/amr/meterProgramming/' + id + '/cancelProgramming')
                    }).done(function(data) {
                        if (data.successMsg) {
                            yukon.ui.alertSuccess(data.successMsg);
                        } else {
                            yukon.ui.alertError(data.errorMsg);
                        }
                    });
                });
                
                $(document).on('click', '.js-accept', function () {
                    var id = $(this).data('id'),
                        guid = $(this).data('guid');
                    $.ajax({
                        type: 'POST',
                        data: {
                            guid: guid
                        },
                        url: yukon.url('/amr/meterProgramming/' + id + '/acceptProgramming')
                    }).done(function(data) {
                        if (data.successMsg) {
                            yukon.ui.alertSuccess(data.successMsg);
                        } else {
                            yukon.ui.alertError(data.errorMsg);
                        }
                    });
                });

                _initialized = true;

            },
            
            refreshCheck: function (deviceId) {
                return function (data) {
                    if (data.isInProgress == 'false') {
                        //refresh row
                        $.ajax(yukon.url('/amr/meterProgramming/' + deviceId + '/refreshDeviceRow'))
                        .done(function (rowData) {
                            var deviceRow = $('#summary-table').find('tr[data-device-id=' + deviceId + ']');
                            deviceRow.html(rowData);
                            //stop data updaters
                            var idMap = {isInProgress : "METER_PROGRAMMING/" + deviceId + "/IS_IN_PROGRESS"};
                            yukon.dataUpdater.unRegisterCallback(idMap);
                            var idMap = {completedCount : "METER_PROGRAMMING/" + deviceId + "/PROGRESS"};
                            yukon.dataUpdater.unRegisterCallback(idMap);
                        });
                    }
                }
            },

    };

    return mod;

})();

$(function () { yukon.ami.meterProgramming.summary.init(); });
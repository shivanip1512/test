yukon.namespace('yukon.dr.setup.program');

/**
 * Module that handles the behavior on the setup program page.
 * 
 * @module yukon.dr.setup.program
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.program = (function() {

    'use strict';

    var _initialized = false,
    _timeFormatter = yukon.timeFormatter,
    
    _updateStartTimeWindowOne = function () {
        var start = _timeFormatter.parse24HourTime($('#startTimeWindowOne').val());
        $('#startTimeInMinutesWindowOne').val(start);
    },

    _updateStopTimeWindowOne = function () {
        var stop = _timeFormatter.parse24HourTime($('#stopTimeWindowOne').val());
        $('#stopTimeInMinutesWindowOne').val(stop);
    },

    _updateStartTimeWindowTwo = function () {
        var start = _timeFormatter.parse24HourTime($('#startTimeWindowTwo').val());
        $('#startTimeInMinutesWindowTwo').val(start);
    },

    _updateStopTimeWindowTwo = function () {
        var stop = _timeFormatter.parse24HourTime($('#stopTimeWindowTwo').val());
        $('#stopTimeInMinutesWindowTwo').val(stop);
    },

    _gearMovableChange = function () {
        $("#js-assigned-gear").sortable({
            stop : function(event, ui) {
                ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            }
        });
    },

    _loadGroupPicker = function() {
        yukon.pickers['js-avaliable-groups-picker'].show();
        $('#js-assigned-groups').sortable({
            stop : function(event, ui) {
                ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            }
        });
    },

    _memberControlPicker = function() {
        yukon.pickers['js-avaliable-members-picker'].show();
        $('#js-assigned-members').sortable({
            stop : function(event, ui) {
                ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            }
        });
    },

    _notificationGroupPicker = function() {
        yukon.pickers['js-avaliable-notification-groups-picker'].show();
        $('#js-assigned-notification-groups').sortable({
            stop : function(event, ui) {
                ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            }
        });
    },
    
    _initConfirmGearDeletePopup = function(gearId, gearName){
        yukon.dialogConfirm.add({
            'on': '#js-remove-gear-' + gearId,
            'strings':{
                'title': yg.text.confirmRemoveTitle,
                'message': yg.text.confirmRemoveMessage.replace("{0}", gearName),
                'ok': yg.text.remove,
                'cancel': yg.text.cancel
            }
        });
    },

    _toggleProgramStartStop = function(action) {
        var checked = $('#js-program-' + action + '-check').is(':checked');
        $('#js-program-' + action).prop("disabled", !checked)
        $('#js-program-' + action + '-span').toggleClass('dn', !checked);
    },

    _handleRampInFields = function(gearType) {
    	var isRampInSelected= $("#js-"+ gearType +"-rampInSwitch").prop('checked');
        if (!isRampInSelected) {
            $("#js-" + gearType +"-rampInPercent").val("");
            $("#js-"+ gearType +"-rampInInterval").val("");
        }
    },

    _loadProgram = function() {

        var type = $('#type').val();
        $("#gear-create-popup-"+ type).empty();
        if (type !== '') {
            yukon.ui.block($('#js-load-program-container'));
            var name = $('#name').val();
            $.ajax({
                url : yukon.url('/dr/setup/loadProgram/create/' + type),
                type : 'get',
                data : {name : name}
            }).done(function(data) {
                $("#js-load-program-container").html('');
                $("#js-load-program-container").html(data);

                yukon.ui.initContent();

                _notificationGroupPicker();
                _loadGroupPicker();
                _gearMovableChange();
                _toggleProgramStartStop('start');
                _toggleProgramStartStop('stop');

                yukon.ui.initDateTimePickers().ancestorInit("#js-load-program-container");
                yukon.ui.unblock($('#js-load-program-container'));
            });
           
        } else {
            $('.noswitchtype').html('');
        }
    },
    
    _initCss = function () {
        $("#js-program-gear-form").find(".timeOffsetWrap").css({"margin-left" : "-5px"});
        var selectedGearType = $("#controlMethod option:selected").val();
        $(".js-help-btn-span").toggleClass("dn", selectedGearType != 'ThermostatRamping' && selectedGearType != 'SimpleThermostatRamping');
        yukon.ui.initDateTimePickers().ancestorInit('.js-simple-thermostat-ramping-gear');
    },

    _checkForPopup = function(id) {
        var selector = "#" + id;
        var length = $("#js-assigned-gear").children(selector).length;
        return length;
    },

    mod = {

        /** Initialize this module. */
        init : function() {

            if (_initialized)
                return;

            _initCss();
            _toggleProgramStartStop('start');
            _toggleProgramStartStop('stop');

            // This will set focus on program name
            $("#name").focus();

            $(document).on('click', '#js-program-cancel-btn', function(event) {
                window.history.back();
            });

            $(document).on("yukon:load-program:delete", function () {
                yukon.ui.blockPage();
                $('#js-delete-load-program-form').submit();
            });

            $(document).on('click', '#js-program-start-check', function(event) {
                _toggleProgramStartStop('start');
            });
            
            $(document).on('click', '#js-program-stop-check', function(event) {
                _toggleProgramStartStop('stop');
            });

            var _mode = $('.js-page-mode').val();
            if (_mode !== 'VIEW' && $("#type").val() !== '') {
                _gearMovableChange();
                if (_mode === 'EDIT' && $('#js-inline-member-picker-container').exists()) {
                    _memberControlPicker();
                }
                _notificationGroupPicker();
                _loadGroupPicker();
            }

            $(document).on('click', '#js-program-save', function(event) {
                var selectedGroups = [];
                var selectedMembers = [];
                var selectedNotificationGroups = [];
                var selectedGears = [];

                $("#js-assigned-groups").find('.select-box-item').each(function(idx, item) {
                    selectedGroups.push($(item).data('id'));
                });

                $("#js-assigned-members").find('.select-box-item').each(function(idx, item) {
                    selectedMembers.push($(item).data('id'));
                });

                $("#js-assigned-notification-groups").find('.select-box-item').each(function(idx, item) {
                    selectedNotificationGroups.push($(item).data('id'));
                });

                $("#js-assigned-gear").find('.select-box-item').each(function(idx, item) {
                    selectedGears.push($(item).data('id'));
                });

                $('#js-selected-group-ids').val(selectedGroups.join(','));
                $('#js-selected-gear-ids').val(selectedGears.join(','));
                $('#js-selected-member-ids').val(selectedMembers.join(','));
                $('#js-selected-notification-group-ids').val(selectedNotificationGroups.join(','));
            });

            $(document).on("yukon:loadProgram:copy", function () {
                yukon.ui.blockPage();
                $('#loadProgram-copy-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        if (!$.isEmptyObject(data.redirectUrl))
                            window.location.href=yukon.url(data.redirectUrl);
                        else
                            window.location.href=yukon.url('/dr/setup/loadProgram/' + data.loadProgramId);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#copy-loadProgram-popup').html(xhr.responseText);
                        yukon.ui.initContent('#copy-loadProgram-popup');
                        yukon.ui.unblockPage();
                    }
                });
            });

            $(document).on("click", ".js-add-group", function() {
                var picker = yukon.pickers['js-avaliable-groups-picker'];

                picker.selectedItems.forEach(function(loadGroup) {
                    var clonedRow = $('.js-template-group-row').clone();
                    clonedRow.attr('data-id', loadGroup.paoId);
                    clonedRow.find(".js-group-name").text(loadGroup.paoName);
                    clonedRow.removeClass('dn js-template-group-row');
                    clonedRow.appendTo($("#js-assigned-groups"));
                    picker.disableItem(loadGroup.paoId);
                });
                $("#js-assigned-groups").closest('.select-box').find('.js-with-movables').trigger(
                'yukon:ordered-selection:added-removed');
                picker.clearEntireSelection();
            });

            $(document).on('click', '.js-group-remove', function() {
                var picker = yukon.pickers['js-avaliable-groups-picker'], 
                selectedId = $(this).parent().attr('data-id');
                $(this).parent().remove();

                $('#js-assigned-groups').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.enableItem(selectedId);
            });

            $(document).on("click", ".js-add-member", function() {
                var picker = yukon.pickers['js-avaliable-members-picker'];

                picker.selectedItems.forEach(function(memberControl) {
                    var clonedRow = $('.js-template-member-row').clone();
                    clonedRow.attr('data-id', memberControl.paoId);
                    clonedRow.find(".js-member-name").text(memberControl.paoName);
                    clonedRow.removeClass('dn js-template-member-row');
                    clonedRow.appendTo($("#js-assigned-members"));
                    picker.disableItem(memberControl.paoId);
                });
                $("#js-assigned-members").closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.clearEntireSelection();
            });

            $(document).on('click', '.js-member-remove', function() {
                var picker = yukon.pickers['js-avaliable-members-picker'], 
                selectedId = $(this).parent().attr('data-id');

                $(this).parent().remove();
                $('#js-assigned-members').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.enableItem(selectedId);
            });

            $(document).on("click", ".js-add-notification-group", function() {
                var picker = yukon.pickers['js-avaliable-notification-groups-picker'];

                picker.selectedItems.forEach(function(notificationGroup) {
                    var clonedRow = $('.js-template-notification-row').clone();
                    clonedRow.attr('data-id', notificationGroup.notificationGroupId);
                    clonedRow.find(".js-notification-group-name").text(notificationGroup.name);
                    clonedRow.removeClass('dn js-template-notification-row');
                    clonedRow.appendTo($("#js-assigned-notification-groups"));
                    picker.disableItem(notificationGroup.notificationGroupId);
                });

                $("#js-assigned-notification-groups").closest('.select-box').find('.js-with-movables').trigger(
                'yukon:ordered-selection:added-removed');
                picker.clearEntireSelection();
            });

            $(document).on('click', '.js-notification-group-remove', function() {
                var picker = yukon.pickers['js-avaliable-notification-groups-picker'], 
                selectedId = $(this).parent().attr('data-id');

                $(this).parent().remove();
                $("#js-assigned-notification-groups").closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.enableItem(selectedId);
            });

            $(document).on('change', '#type', function(event) {
                _loadProgram();
            });

            $(document).on('dialogopen', '.js-gearPopup', function() {
                $("#name").blur();
                $("#gearName").focus();
            });

            $(document).on('change', '#controlMethod', function(event) {
                var isControlMethodBlank = $("#controlMethod option:selected").val() == "" ? true :false;
                $("#js-gear-fields-container").toggleClass('dn', isControlMethodBlank);
                if (!isControlMethodBlank) {
                    yukon.ui.block($('#js-gear-fields-container'));
                    var gearType = $(this).val(), 
                        gearName = $(this).closest("div.js-program-gear-container").find("input[name='gearName']").val(),
                        selectedProgramType = $("#js-selected-program").val();
                    $.ajax({
                        url : yukon.url('/dr/setup/loadProgram/populateGearFields/' + gearType),
                        type : 'get',
                        data : {
                            gearName : gearName,
                            programType : selectedProgramType
                        }
                    }).done(function(data) {
                        $(".js-program-gear-container").empty();
                        $(".js-program-gear-container").html(data);
                        yukon.ui.unblock($('#js-gear-fields-container'));
                        _initCss();
                    });
                    
                }
            });

            $(document).on("yukon:dr:setup:program:saveGear", function (event) {
                var dialog = $(event.target),
                    popupDiv = $(event.target)[0],
                    popupId = popupDiv.id, gearId,
                    selectedProgramType = $("#js-selected-program").val();
                if(popupId !== 'gear-create-popup-'+ selectedProgramType) {
                    var arr = popupId.split('-');
                    gearId = arr[arr.length - 1];
                } else {
                    gearId = '';
                }
                var form = dialog.find('#js-program-gear-form'),
                    gearNameElement = dialog.find("#gearName"),
                    controlMethodElement = dialog.find("#controlMethod"),
                    isGearNameBlank = $.trim(gearNameElement.val()).length == 0 ? true : false,
                    isControlMethodBlank = controlMethodElement.find("option:selected").val() == "" ? true :false;
                
                gearNameElement.toggleClass("error", isGearNameBlank);
                dialog.find("#gearNameIsBlankError").toggleClass("dn", !isGearNameBlank);
                controlMethodElement.toggleClass("error", isControlMethodBlank);
                dialog.find("#gearTypeIsRequiredError").toggleClass("dn", !isControlMethodBlank);
                
                var selectedGearType = controlMethodElement.find("option:selected").val();
                if(!selectedGearType) {
                    selectedGearType = controlMethodElement.val();
                }
                if(selectedGearType === 'TimeRefresh' || selectedGearType === 'MasterCycle'){
                    _handleRampInFields(selectedGearType); 
                }
                /*
                 * Find all fields in the form that use <dt:timeOffset/>. If any of these fields have invalid numeric value,
                 * set the field value to 0 for such fields. This is required since non-numeric value cannot be bind to an Integer
                 * value.
                 * */
                $("#js-program-gear-form .timeOffsetWrap input:hidden").each(function (index, item) {
                    if (!$.isNumeric($(item).val())) {
                        $(item).val(0);
                    }
                });
                
                if (!isGearNameBlank && !isControlMethodBlank) {
                $.ajax({
                    type: "POST",
                    url: yukon.url("/dr/setup/loadProgram/gear/save"),
                    data: form.serialize() + "&tempGearId=" + gearId
                }).done(function(data) {
                    var id = data.id;
                    if(_checkForPopup(id) == 0) {
                        var gearName = data.gearName,
                        mode = $("#js-form-mode").val(),
                        anchorTag = $("<a>"),
                        url = yukon.url("/dr/setup/loadProgram/gear/" + id + "?mode=" + mode),
                        clonedRow = $('.js-template-gears-row').clone();
                        anchorTag.attr("href", url);
                        anchorTag.text(gearName);
                        clonedRow.attr('id', id);
                        clonedRow.attr('data-id', id);
                        clonedRow.find(".js-gear-name").append(anchorTag);
                        clonedRow.addClass('js-gear-link');
                        clonedRow.removeClass('dn js-template-gears-row');
                        clonedRow.find(".js-gear-remove").attr("id", "js-remove-gear-" + data.id);
                        clonedRow.appendTo($("#js-assigned-gear"));

                        var clonedDialog = $(".js-gear-dialog-template").clone();
                        clonedDialog.attr("id", "js-gear-dialog-" + id);
                        clonedDialog.attr("data-url", url);
                        clonedDialog.removeClass("js-gear-dialog-template");
                        $("#gear-popups").append(clonedDialog);
                        anchorTag.attr("data-popup", "#js-gear-dialog-" + id);
                    } else {
                        $('#' + id + ' a').text(data.gearName);
                        $(event.target).dialog('close');
                    }
                    _initConfirmGearDeletePopup(data.id, data.gearName);
                    $('#js-assigned-gear').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                });
                    dialog.dialog('close');
                    dialog.empty();
                }
            });
            
            $(document).on('click', '.js-gear-link', function(event) {
                event.preventDefault();
            });

            $(document).on('click', '#controlWindowOne', function() {
                var controlWindowRow = $(this).closest('tr'),
                useControlWindow = controlWindowRow.find('.switch-btn-checkbox').prop('checked');
                if (!useControlWindow) {
                    $('#startTimeInMinutesWindowOne').val('');
                    $('#stopTimeInMinutesWindowOne').val('');
                } else {
                    _updateStartTimeWindowOne();
                    _updateStopTimeWindowOne();
                }
            });

            $(document).on('click', '#controlWindowTwo', function() {
                var controlWindowRow = $(this).closest('tr'),
                useControlWindow = controlWindowRow.find('.switch-btn-checkbox').prop('checked');
                if (!useControlWindow) {
                    $('#startTimeInMinutesWindowTwo').val('');
                    $('#startTimeInMinutesWindowTwo').val('');
                } else {
                    _updateStartTimeWindowTwo();
                    _updateStopTimeWindowTwo();
                }
            });

            $(document).on('change', '#startTimeWindowOne', function() {
                _updateStartTimeWindowOne();
            });

            $(document).on('change', '#stopTimeWindowOne', function () {
                _updateStopTimeWindowOne();
            });

            $(document).on('change', '#startTimeWindowTwo', function() {
                _updateStartTimeWindowTwo();
            });

            $(document).on('change', '#stopTimeWindowTwo', function () {
                _updateStopTimeWindowTwo();
            });

            $(document).on('click', '#js-program-save', function (event) {
                if (($('#js-program-start-check').is(':checked'))) {
                    if(!$('#js-program-start').val()) {
                        $('#js-program-start').val(0);
                    }
                }
                if (($('#js-program-stop-check').is(':checked'))) {
                    if(!$('#js-program-stop').val()) {
                        $('#js-program-stop').val(0);
                    }
                }
            });

            $(document).on("yukon:dr:setup:program:gearRemoved", function (event) {
                $(event.target).closest("div.js-assigned-gear").remove();
                $('#js-assigned-gear').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            });

            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.dr.setup.program.init();
});
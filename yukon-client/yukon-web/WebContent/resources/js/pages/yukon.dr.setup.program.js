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

    _loadProgram = function() {

        var type = $('#type').val();
        $("#gear-create-popup").html('')
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

                yukon.ui.initDateTimePickers().ancestorInit("#js-load-program-container");
                $(' #js-assigned-gear').sortable({
                    stop : function(event, ui) {
                        ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                    }
                });
                yukon.ui.unblock($('#js-load-program-container'));
            });
           
        } else {
            $('.noswitchtype').html('');
        }
    },
    
    _initCss = function () {
        $("#js-program-gear-form").find(".timeOffsetWrap").css({"margin-left" : "-5px"});
        $("#js-program-gear-form").find(".js-delta-value").css({"margin-left" : "-5px"});
        var selectedGearType = $("#controlMethod option:selected").val();
        $(".js-help-btn-span").toggleClass("dn", selectedGearType != 'ThermostatRamping' && selectedGearType != 'SimpleThermostatRamping');
        yukon.ui.initDateTimePickers().ancestorInit('.js-simple-thermostat-ramping-gear');
    },

    mod = {

        /** Initialize this module. */
        init : function() {

            if (_initialized)
                return;
            
            _initCss();

            $(document).on('click', '#js-program-cancel-btn', function(event) {
                window.history.back();
            });

            $(document).on("yukon:load-program:delete", function () {
                yukon.ui.blockPage();
                $('#js-delete-load-program-form').submit();
            });

            $(document).on('click', '#js-program-start-check', function(event) {
                if(!$('#js-program-start-check').is(':checked')) {
                    $('#js-program-start').prop("disabled", true);
                } else {
                    $('#js-program-start').prop("disabled", false);
                }
            });
            
            $(document).on('click', '#js-program-stop-check', function(event) {
                if(!$('#js-program-stop-check').is(':checked')) {
                    $('#js-program-stop').prop("disabled", true);
                } else {
                    $('#js-program-stop').prop("disabled", false);
                }
            });

            var _mode = $('.js-page-mode').val();
            if (_mode !== 'VIEW' && $("#type").val() !== '') {
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
                $('#js-load-program-form').submit();
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

            $(document).on('change', '#controlMethod', function(event) {
                var isControlMethodBlank = $("#controlMethod option:selected").val() == "" ? true :false;
                $("#js-gear-fields-container").toggleClass('dn', isControlMethodBlank);
                
                if (!isControlMethodBlank) {
                    yukon.ui.block($('#js-gear-fields-container'));
                    var gearType = $(this).val(), 
                        gearName = $(this).closest("div.js-program-gear-container").find("input[name='gearName']").val(), 
                        programType = $(this).closest("div.js-program-gear-container").find("input[name='programType']").val();

                    $.ajax({
                        url : yukon.url('/dr/setup/loadProgram/populateGearFields/' + gearType),
                        type : 'get',
                        data : {
                            gearName : gearName,
                            programType : programType
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
                form = dialog.find('#js-program-gear-form');
                form.ajaxSubmit({
                    success: function (data) {
                        var id = data.id,
                            gearName = data.gearName,
                            mode = $("#js-form-mode").val(),
                            anchorTag = $("<a>"),
                            saveUrl = yukon.url("/dr/setup/loadProgram/gear/save"),
                            url = yukon.url("/dr/setup/loadProgram/gear/" + id + "?mode=" + mode),
                            clonedRow = $('.js-template-gears-row').clone();
                            anchorTag.attr("href", url);
                            anchorTag.text(gearName);

                            clonedRow.attr('data-id', id);
                            clonedRow.find(".js-gear-name").append(anchorTag);
                            clonedRow.addClass('js-gear-link');
                            clonedRow.removeClass('dn js-template-gears-row');
                            clonedRow.appendTo($("#js-assigned-gear"));
                            
                            var clonedDialog = $(".js-gear-dialog-template").clone();
                            clonedDialog.attr("id", "js-gear-dialog-" + id);
                            clonedDialog.attr("data-url", url);
                            clonedDialog.removeClass("js-gear-dialog-template");
                            $(".js-assigned-gear").append(clonedDialog);
                            
                            anchorTag.attr("data-popup", "#js-gear-dialog-" + id);
                    }
                });
           
               dialog.dialog('close');
               dialog.empty();
            });
            
            $(document).on('click', '.js-gear-link', function(event) {
                event.preventDefault();
            });

            $(document).on('click', '.js-gear-remove', function() {
                $(this).parent().remove();
                $("#js-assigned-gear").closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
            });

            _initialized = true;
        }
    };

    return mod;
})();

$(function() {
    yukon.dr.setup.program.init();
});
yukon.namespace('yukon.dr.setup.macroloadGroup');

/**
 * Module that handles the behavior on the setup Macro Load Group page.
 * @module yukon.dr.setup.macroloadGroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.macroloadGroup = (function() {
    
    'use strict';
    
    var
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            if ($("#js-inline-picker-container").is(":visible")) {
                yukon.pickers['js-avaliable-load-groups-picker'].show();
                $('#js-assigned-load-groups').sortable({
                    stop: function(event, ui) {
                        ui.item.closest('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                    }
                });
            } else {
                $("#js-assigned-load-groups-table").scrollTableBody({rowsToDisplay: 20});
            }
            
            $(document).on('click', '#js-save', function (event) {
                var selectedLoadGroups = [];
                $("#js-assigned-load-groups").find('.select-box-item').each(function (idx, item) {
                    if ($(item).is(':visible')) {
                        selectedLoadGroups.push($(item).data('id'));
                    }
                });
                $('#js-selected-load-group-ids').val(selectedLoadGroups.join(','));
                $('#js-macro-load-group-form').submit();
            });
            
            $(document).on("yukon:macro-load-group:copy", function () {
                yukon.ui.blockPage();
                $('#js-copy-macro-load-group-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        if (!$.isEmptyObject(data.copiedLoadGroupId)) {
                            window.location.href=yukon.url('/dr/setup/macroLoadGroup/' + data.copiedLoadGroupId);
                        } else {
                            window.location.href=yukon.url(data.redirectUrl);
                        }
                    },
                    error: function (xhr, status, error, $form) {
                        $('#js-copy-popup').html(xhr.responseText);
                        yukon.ui.initContent('#js-copy-popup');
                        yukon.ui.unblockPage();
                    }
                });
            });
            
            $(document).on("yukon:macro-load-group:delete", function () {
                yukon.ui.blockPage();
                $("#js-delete-macro-load-group-form").submit();
            });
            
            $(document).on("click", ".js-add-load-group", function () {
                var picker = yukon.pickers['js-avaliable-load-groups-picker'];
                
                picker.selectedItems.forEach(function (loadGroup) {
                    var divSelector = 'div[data-id="' + loadGroup.paoId + '"]';
                    if (!$(divSelector).exists()) {
                        var clonedRow = $('.js-template-row').clone();
                        clonedRow.attr('data-id', loadGroup.paoId);
                        clonedRow.find(".js-load-group-name").text(loadGroup.paoName);
                        clonedRow.removeClass('dn js-template-row');
                        clonedRow.appendTo($("#js-assigned-load-groups"));
                        picker.disableItem(loadGroup.paoId);
                    }
                });
                $("#js-assigned-load-groups").closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.clearEntireSelection();
            });
            
            $(document).on('click', '.js-remove', function () {
                var picker = yukon.pickers['js-avaliable-load-groups-picker'],
                    selectedId = $(this).parent().attr('data-id');
                $(this).parent().remove();
                $('#js-assigned-load-groups').closest('.select-box').find('.js-with-movables').trigger('yukon:ordered-selection:added-removed');
                picker.enableItem(selectedId);
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.macroloadGroup.init(); });
yukon.namespace('yukon.dr.setup.controlScenario');

/**
 * Module that handles the behavior on control scenario page.
 * @module yukon.dr.setup.controlScenario
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.controlScenario = (function() {

    'use strict';

    var 
    _initialized = false,

    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            if ($("#js-inline-picker-container").is(":visible")) {
                yukon.pickers['js-available-programs-picker'].show();
                $("#js-assigned-programs-table select").width("100px");
            } else {
                $("#js-assigned-programs-table").scrollTableBody({rowsToDisplay: 20});
            }
            
            $(document).on("yukon:control-scenario:delete", function () {
                yukon.ui.blockPage();
                $("#js-delete-control-scenario-form").submit();
            });
            
            $(document).on('click', '#js-save', function () {
                $("#js-assigned-programs-table .timeOffsetWrap input:hidden").each(function (index, item) {
                    if (!$.isNumeric($(item).val())) {
                        $(item).val(0);
                    }
                });
                yukon.ui.reindexInputs($("#js-assigned-programs-table"));
                $("#js-control-scenario-form").submit();
            });
            
            $(document).on("click", ".js-add-programs", function () {
                var picker = yukon.pickers['js-available-programs-picker'],
                    assignedProgramsCount = $("#js-assigned-programs-table tbody tr").length == 0 ? 0 : $("#js-assigned-programs-table tr").length - 1;
                
                if (picker.selectedItems.length > 0) {
                    var assignedProgramIds = [];
                    picker.selectedItems.forEach(function (selectedProgram) {
                        if (!$("#js-assigned-program-" + selectedProgram.id).exists()) {
                            picker.disableItem(selectedProgram.id);
                            assignedProgramIds.push(selectedProgram.id);
                        }
                    });
                    
                    var url = yukon.url("/dr/setup/controlScenario/renderAssignedPrograms?programIds=" + assignedProgramIds 
                                           + "&controlScenario=" + $("#js-control-scenario-form").serialize());
                    $.post(url, function (data) {
                        $("#js-assigned-programs-div").empty();
                        $("#js-assigned-programs-div").html(data);
                        yukon.ui.initDateTimePickers().ancestorInit('#js-assigned-programs-div');
                        $("#js-assigned-programs-table select").width("100px");
                    });
                    picker.clearEntireSelection();
                }
            });
            
            $(document).on('click', '.js-remove', function () {
                var picker = yukon.pickers['js-available-programs-picker'],
                    selectedId = $(this).closest('tr').attr('data-id');
                $(this).closest('tr').remove();
                picker.enableItem(selectedId);
                yukon.ui.reindexInputs($("#js-assigned-programs-table"));
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.controlScenario.init(); });
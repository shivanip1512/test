yukon.namespace('yukon.dr.formula');

/**
 * Handles the operations related to formula and formula assignment for
 * estimated Load.
 * 
 * @module yukon.dr.formula
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.dr.formula = (function() {
    var _initialized = false, _rowIndex, 
    
    _addedAssignments = [],

    /**
     * Assigns gear to a formula.
     * @param {Object} gear - Selected gear.
     */
    _addGearAssignment = function(gear) {
        var id = gear.gearId;

        var $newAssignmentInput = $("#assignmentInput_-1").clone();
        $newAssignmentInput.attr("id", "assignmentInput_" + id);
        $newAssignmentInput.val(id);
        $("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element
        var $newAssignment = $("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".js-drFormula-program").html(gear.programName);
        $newAssignment.find(".js-drFormula-name").html(gear.gearName);
        $newAssignment.find(".js-drFormula-control-method").html(
                gear.controlMethod);
        $newAssignment.find(".js-drFormula-remove").data("assign-id", id);
        $newAssignment.show();
        $("#assignments").append($newAssignment);

        var $assignRemoved = $("#assignmentRemove_-1").clone();
        $assignRemoved.attr("id", "assignmentRemove_" + id);
        $assignRemoved.find(".js-drFormula-undo").data("assign-id", id);
        $("#assignments").append($assignRemoved);

        _addedAssignments.push(id);
        $("#assignments").show();
        $("#noAssignments").hide();
    },

    /**
     * Assigns appliance category to a formula.
     * @param {Object} appCat - Selected appliance category.
     */
    _addAppCatAssignment = function(appCat) {
        var id = appCat.applianceCategoryId;

        var $newAssignmentInput = $("#assignmentInput_-1").clone();
        $newAssignmentInput.attr("id", "assignmentInput_" + id);
        $newAssignmentInput.val(id);
        $("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element
        var $newAssignment = $("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".js-drFormula-average-load").html(
                appCat.applianceLoad + "  kW");
        $newAssignment.find(".js-drFormula-name").html(appCat.name);
        $newAssignment.find(".js-drFormula-type").html(appCat.applianceType);
        $newAssignment.find(".js-drFormula-remove").data("assign-id", id);
        $newAssignment.show();
        $("#assignments").append($newAssignment);

        var $assignRemoved = $("#assignmentRemove_-1").clone();
        $assignRemoved.attr("id", "assignmentRemove_" + id);
        $assignRemoved.find(".js-drFormula-undo").data("assign-id", id);
        $("#assignments").append($assignRemoved);

        _addedAssignments.push(id);
        $("#assignments").show();
        $("#noAssignments").hide();
    },

    /** Remove the assigned appliance category. */
    _removeAssignmentBtnClick = function() {
        var id = $(this).data("assign-id");
        $("#assignmentInput_" + id).prop("disabled", true);
        $("#assignmentAdd_" + id).hide();
        $("#assignmentRemove_" + id).show();
    },

    /** Undo the unassigned appliance category and assign it again. */
    _undoBtnClick = function() {
        var id = $(this).data("assign-id");
        $("#assignmentInput_" + id).prop("disabled", false);
        $("#assignmentAdd_" + id).show();
        $("#assignmentRemove_" + id).hide();
    },

    /**
     * This is called when Add function button is clicked. Add new set of input
     * components
     */
    _newFunctionBtnClick = function() {
        var $newRow = $('#functionRow_-1').clone().removeAttr("id");
        var tableId = _rowIndex;

        $newRow.find(".js-drFormula-appendTableId").each(function() {
            this.id = this.id + tableId;
        }).promise().done(function() {
            $newRow.appendTo('#formulaFunctions').slideDown(150);
            var $inputSelect = $("#formulaInputSelect_" + tableId);
            $inputSelect.addClass("js-drFormula-formulaInputSelect");
            _updateFormulaInputs($inputSelect.val(), tableId);
        });

        _rowIndex++;
    },
    /**
     * This is called when value in Input drop down is changed. It calls method
     * to update the input formula fields
     */
    _formulaInputSelectChange = function() {
        // FormulaInput type
        // com.cannontech.dr.estimatedload.FormulaInput
        var inputVal = $(this).val();
        var tableId = this.id.split("_").pop();
        _updateFormulaInputs(inputVal, tableId);
    },

    /**
     * Show and hide the weather station drop down based on selection in Input
     * drop down.
     * @param {Object} inputVal - Value selected in input drop down.
     * @param {Object} tableId - Id of table.
     */
    _updateFormulaInputs = function(inputVal, tableId) {
        if (inputVal === 'POINT') {
            $("#formulaPointPicker_" + tableId).fadeIn(150);
            $("#formulaWeatherStationTemp_" + tableId).hide();
            $("#formulaWeatherStationHumidity_" + tableId).hide();
        } else {
            $("#formulaPointPicker_" + tableId).hide();
            if (inputVal === 'TEMP_C' || inputVal == 'TEMP_F') {
                $("#formulaWeatherStationTemp_" + tableId).fadeIn(150);
                $("#formulaWeatherStationHumidity_" + tableId).hide();
            } else if (inputVal == 'HUMIDITY') {
                $("#formulaWeatherStationHumidity_" + tableId).fadeIn(150);
                $("#formulaWeatherStationTemp_" + tableId).hide();
            } else {
                $("#formulaWeatherStationHumidity_" + tableId).hide();
                $("#formulaWeatherStationTemp_" + tableId).hide();
            }
        }
    },

    /**
     * Hides and show the input fields based on the selection in Input drop
     * down. If Time is selected the time specific input fields are displayed
     * else normal inputs are displayed.
     */
    _formulaInputSelectChangeTable = function() {
        var inputVal = $(this).val();
        var tableId = this.id.split("_").pop();
        if (inputVal === 'TIME') {
            // Hide & disable all normal inputs
            // show all time inputs
            $(".js-drFormula-notTimeInput_" + tableId).prop("disabled", true)
                    .hide();
            $(".js-drFormula-timeInput_" + tableId).prop("disabled", false)
                    .show();
        } else {
            // Hide & disable all time inputs
            // show all normal inputs
            $(".js-drFormula-timeInput_" + tableId).prop("disabled", true)
                    .hide();
            $(".js-drFormula-notTimeInput_" + tableId).prop("disabled", false)
                    .show();
        }
    },

    /** Loads the point picker when point picker link is clicked. */
    _pointPickerClick = function() {
        var tableId = this.id.split("_").pop();
        $("#picker-pointPicker-btn").data("table-id", tableId).click();
    },

    /** Removes the lookup table when remove button is clicked */
    _removeBtnClick = function() {
        $(this).parents(".js-drFormula-removeable").slideUp(150).promise()
                .done(function() {
                    this.remove();
                });
    },

    /** Create structure for lookup table */
    _addTableBtnClick = function() {
        var $newRow = $('#tableRow_-1').clone().removeAttr("id");
        var tableId = _rowIndex;

        $newRow.find(".tableIndex").val(tableId);
        $newRow.find(".js-drFormula-appendTableIdData").each(function() {
            $(this).data("table-id", tableId);
        });
        $newRow.find(".js-drFormula-newEntryButton").data("table-id", tableId);
        $newRow.find(".js-drFormula-timeInput_").removeClass(
                "js-drFormula-timeInput_").addClass(
                "js-drFormula-timeInput_" + tableId);
        $newRow.find(".js-drFormula-notTimeInput_").removeClass(
                "js-drFormula-notTimeInput_").addClass(
                "js-drFormula-notTimeInput_" + tableId);

        $newRow.find(".js-drFormula-appendTableId").each(function() {
            this.id = this.id + tableId;
        }).promise().done(
                function() {
                    $newRow.hide().appendTo('#formulaTables').slideDown(150);
                    var $inputSelect = $("#formulaInputSelect_" + tableId);
                    $inputSelect.addClass("js-drFormula-formulaInputSelect");
                    _updateFormulaInputs($inputSelect.val(), tableId);
                    // add entry to this row
                    _addTableEntryBtnClick.call($newRow
                            .find(".js-drFormula-newEntryButton"));
                });

        _rowIndex++;
    },

    /** Create structure for lookup table entry */
    _addTableEntryBtnClick = function() {
        var tableId = $(this).data("table-id");
        var entryId = $(this).data("entry-id-next");

        var isTimeInput = $("#formulaInputSelect_" + tableId).val() === 'TIME';

        var $newRow = $('#tableEntry_-1_-1').clone();

        $newRow.attr("id", "tableEntry_" + tableId + "_" + entryId);

        $newRow.find(".js-drFormula-appendTableId").each(function() {
            this.id = this.id + tableId + "_" + entryId;
        });

        $newRow.find(".js-drFormula-tableEntryKey_").removeClass(
                "js-drFormula-tableEntryKey_").addClass(
                "js-drFormula-tableEntryKey_" + tableId);

        $newRow.find(".js-drFormula-timeInput_").removeClass(
                "js-drFormula-timeInput_").addClass(
                "js-drFormula-timeInput_" + tableId);

        $newRow.find(".js-drFormula-notTimeInput_").removeClass(
                "js-drFormula-notTimeInput_").addClass(
                "js-drFormula-notTimeInput_" + tableId);

        if (!isTimeInput) {
            $newRow.find(".js-drFormula-notTimeInput_" + tableId).show();
            $newRow.find(".js-drFormula-timeInput_" + tableId).hide();
        }

        $newRow.children().each(
                function() {
                    this.id = this.id + tableId + "_" + entryId;
                    // Setup the remove button
                    $(this).find(".js-drFormula-removeEntry").data("table-id",
                            tableId).data("entry-id", entryId);

                });

        $newRow.insertBefore(
                "#tableEntries_" + tableId + " .js-drFormula-inputMax")
                .slideDown(150).promise().done(
                        function() {
                            $newRow.css("overflow", "visible");
                            // Forces scroll down to focused input field
                            $("#tableEntries_" + tableId).parent().scrollTop(
                                    $("#tableEntries_" + tableId).parent()
                                            .height() + 20);

                            var numberEntries = $(
                                    ".js-drFormula-tableEntryKey_" + tableId)
                                    .size();
                            if (numberEntries === 1) {
                                $("#tableEntries_" + tableId).find(
                                        ".js-drFormula-removeEntry").hide();
                            } else {
                                $("#tableEntries_" + tableId).find(
                                        ".js-drFormula-removeEntry").show();
                            }
                        });

        $("#tableEntryKey_" + tableId + "_" + entryId + " > input").focus();

        // update the add button
        entryId++;
        $(this).data("entry-id-next", entryId);
    },

    /**
     * This is called when remove button is clicked for lookup table entries. It
     * remove the lookup entry from lookup table.
     */
    _removeTableEntryBtnClick = function() {
        var entryId = $(this).data("entry-id");
        var tableId = $(this).data("table-id");

        $("#tableEntry_" + tableId + "_" + entryId).slideUp(
                150,
                function() {
                    this.remove();
                    var numberEntries = $(
                            ".js-drFormula-tableEntryKey_" + tableId).size();
                    if (numberEntries === 1) {
                        $("#tableEntries_" + tableId).find(
                                ".js-drFormula-removeEntry").hide();
                    }
                });
    },

    /**
     * Disable the fields before the form is submitted.
     * @param {Object} e - jquery event object.
     */
    _beforeFormSubmit = function(e) {
        $(".js-drFormula-formulaInputSelect").each(
                function() {
                    $this = $(this);
                    $this.removeAttr("disabled");

                    var inputVal = $this.val();
                    var tableId = this.id.split("_").pop();

                    if (inputVal === 'TIME') {
                        $(".js-drFormula-notTimeInput_" + tableId)
                                .find("input").prop('disabled', true);
                    } else {
                        $(".js-drFormula-timeInput_" + tableId).find("input")
                                .prop('disabled', true);
                    }

                    if (inputVal === 'POINT') {
                        $("#formulaWeatherStationTemp_" + tableId).find(
                                "select").prop('disabled', true);
                        $("#formulaWeatherStationHumidity_" + tableId).find(
                                "select").prop('disabled', true);
                    } else if (inputVal === 'TEMP_C' || inputVal == 'TEMP_F') {
                        $("#inputPointId_" + tableId).prop('disabled', true);
                        $("#formulaWeatherStationHumidity_" + tableId).find(
                                "select").prop('disabled', true);
                    } else if (inputVal == 'HUMIDITY') {
                        $("#inputPointId_" + tableId).prop('disabled', true);
                        $("#formulaWeatherStationTemp_" + tableId).find(
                                "select").prop('disabled', true);
                    } else {
                        $("#inputPointId_" + tableId).prop('disabled', true);
                        $("#formulaWeatherStationTemp_" + tableId).find(
                                "select").prop('disabled', true);
                        $("#formulaWeatherStationHumidity_" + tableId).find(
                                "select").prop('disabled', true);
                    }
                });

        // adjust indexes to be sequential for java List
        $(".js-drFormula-table, .js-drFormula-function").each(
                function(index) {
                    var $inputs = $(this).find(":input[name]");
                    // Add table index to all table inputs
                    $inputs.each(function() {
                        var $self = $(this);
                        var name = $self.attr("name").replace(/\[.*?\]/,
                                "[" + index + "]");
                        $self.attr("name", name);
                    });

                    $(this).find(".js-drFormula-tableEntries").each(
                            function(index2) {
                                var inputs = $(this).find(":input[name]");
                                inputs.each(function(entryLoopIndex) {

                                    var $self = $(this);
                                    var name = $self.attr("name");
                                    // there are four entry inputs for one entry
                                    // row (key, value, timeKey, valueKey)
                                    // Each entry input needs the same entry id
                                    var entryIndex = Math
                                            .floor(entryLoopIndex / 4);

                                    name = name.replace(/entries\[.*?\]/,
                                            "entries[" + entryIndex + "]");
                                    name = name.replace(/timeEntries\[.*?\]/,
                                            "timeEntries[" + entryIndex + "]");
                                    $self.attr("name", name);
                                });
                            });
                });
    },

    mod = {
        init : function() {
            if (_initialized) {
                return;
            }

            _rowIndex = $("#formulaRowIndex").val();

            $('#display_tabs').tabs().show();
            $("#assignments").on("click", ".js-drFormula-remove",
                    _removeAssignmentBtnClick).on("click",
                    ".js-drFormula-undo", _undoBtnClick);
            $("#newFunctionBtn").click(_newFunctionBtnClick);
            $("#formulaFunctions, #formulaTables").on("change",
                    ".js-drFormula-formulaInputSelect",
                    _formulaInputSelectChange).on("click",
                    ".js-drFormula-pointPicker", _pointPickerClick);
            $("#formulaFunctions, #formulaTables").on("click",
                    ".js-drFormula-remove", _removeBtnClick);
            $("#newTableBtn").click(_addTableBtnClick);
            $("#formulaTables").on("click", ".js-drFormula-removeEntry",
                    _removeTableEntryBtnClick).on("change",
                    ".js-drFormula-formulaInputSelect",
                    _formulaInputSelectChangeTable).on("click",
                    ".js-drFormula-newEntryButton", _addTableEntryBtnClick);
            $("#formulaForm").submit(_beforeFormSubmit);

            _initialized = true;
        },

        /**
         * This is called when point is selected from point picker. It sets the
         * selected point.
         * @param {Object} selectedItems - Selected point.
         * @param {Object} picker - picker object.
         */
        pickerClose : function(selectedItems, picker) {
            var tableId = $("#picker-pointPicker-btn").data("table-id");
            $("#inputPointId_" + tableId).val(selectedItems[0].pointId);
            $("#formulaPointPickerLbl_" + tableId).html(
                    selectedItems[0].pointName);
        },

        /**
         * This is called when application category picker is closed from
         * formula page. List of included application category ids are set.
         * @param {Object} selectedGears - Appliance category selected in picker.
         * @param {Object} picker - Picker object.
         */
        unassignedAppCatPickerClose : function(selectedApCats, picker) {
            var choosenIds = [];
            for ( var i = 0; i < selectedApCats.length; i++) {
                choosenIds.push(selectedApCats[i].applianceCategoryId);
                if (_addedAssignments
                        .indexOf(selectedApCats[i].applianceCategoryId) < 0) {
                    _addAppCatAssignment(selectedApCats[i]);
                }
            }
            picker.excludeIds = choosenIds;
        },

        /**
         * This is called when gear picker is closed from formula page. List of
         * included gear ids are set.
         * @param {Object} selectedGears - Gear selected in picker.
         * @param {Object} picker - Picker object.
         */
        unassignedGearPickerClose : function(selectedGears, picker) {
            var choosenIds = [];
            for ( var i = 0; i < selectedGears.length; i++) {
                choosenIds.push(selectedGears[i].gearId);
                if (_addedAssignments.indexOf(selectedGears[i].gearId) < 0) {
                    _addGearAssignment(selectedGears[i]);
                }
            }
            picker.excludeIds = choosenIds;
        },

        /**
         * This is called when formula picker for appliance categories is
         * closed. It sends ajax request to set selected formula for a appliance
         * category.
         * @param {Object} selectedFormula - Formula selected in picker.
         * @param {Object} picker - Picker object.
         */
        appCatFormulaPickerClose : function(selectedFormula, picker) {
            var appCatId = picker.pickerId.split("_").pop();
            var unassign = false;
            var formulaId = 0;

            if (selectedFormula.length < 1) {
                unassign = true;
            } else {
                formulaId = selectedFormula[0].formulaId;
            }
            
            $.ajax("assignFormulaToAppCat", {
                data : {
                    'appCatId' : appCatId,
                    'formulaId' : formulaId,
                    'unassign' : unassign
                }
            }).done(function(data) {
                $("#formulaPickerRowAppCat_" + appCatId).html(data).flash();
            });
        },
        
        /** This is called when formula picker for gears is closed.
         *  It sends ajax request to set selected formula for a gear.
         *  @param {Object} selectedFormula - Formula selected in picker.
         *  @param {Object} picker - Picker object.
         */
        gearFormulaPickerClose : function(selectedFormula, picker) {
            var gearId = picker.pickerId.split("_").pop();
            var unassign = false;
            var formulaId = 0;

            if (selectedFormula.length < 1) {
                unassign = true;
            } else {
                formulaId = selectedFormula[0].formulaId;
            }

            $.ajax("assignFormulaToGear", {
                data : {
                    'gearId' : gearId,
                    'formulaId' : formulaId,
                    'unassign' : unassign
                }
            }).done(function(data) {
                $("#formulaPickerRowGear_" + gearId).html(data).flash();
            });
        }
    };
    return mod;
}());

$(function() {
    
    yukon.dr.formula.init();
    
    $("#assignments").on("mouseenter", ".js-drFormula-show-on-hover-target", function() { 
        $(this).find(".js-drFormula-show-on-hover").show();
    }).on("mouseleave", ".js-drFormula-show-on-hover-target", function() {
        $(this).find(".js-drFormula-show-on-hover").hide();
    });
});
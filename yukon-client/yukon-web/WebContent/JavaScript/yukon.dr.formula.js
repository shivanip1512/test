
yukon.namespace('yukon.dr');
yukon.namespace('yukon.dr.formula');

yukon.dr.formula = (function() {
    var
    _initialized = false, 
    _rowIndex, // Set in intializer
    _addedAssignments = [],
    
    _addGearAssignment = function (gear) {
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
        $newAssignment.find(".js-drFormula-control-method").html(gear.controlMethod);
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

    _addAppCatAssignment = function (appCat) {
        var id = appCat.applianceCategoryId;

        var $newAssignmentInput = $("#assignmentInput_-1").clone();
        $newAssignmentInput.attr("id", "assignmentInput_" + id);
        $newAssignmentInput.val(id);
        $("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element 
        var $newAssignment = $("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".js-drFormula-average-load").html(appCat.applianceLoad + "  kW");
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

    _removeAssignmentBtnClick = function() {
        var id = $(this).data("assign-id");
        $("#assignmentInput_"+id).prop("disabled", true);
        $("#assignmentAdd_"+id).hide();
        $("#assignmentRemove_"+id).show();
    },

    _undoBtnClick = function() {
        var id = $(this).data("assign-id");
        $("#assignmentInput_"+id).prop("disabled", false);
        $("#assignmentAdd_"+id).show();
        $("#assignmentRemove_"+id).hide();
    },

    _newFunctionBtnClick = function() {
        var $newRow = $('#functionRow_-1').clone().removeAttr("id");
        var tableId = _rowIndex;

        $newRow.find(".js-drFormula-appendTableId").each(function () {
            this.id = this.id + tableId;
        }).promise().done(function () {
            $newRow.appendTo('#formulaFunctions').slideDown(150);
            var $inputSelect = $("#formulaInputSelect_"+tableId);
            $inputSelect.addClass("js-drFormula-formulaInputSelect");
            _updateFormulaInputs($inputSelect.val(), tableId);
        });

        _rowIndex++;
    },

    _formulaInputSelectChange = function() {
        // FormulaInput type
        // com.cannontech.dr.estimatedload.FormulaInput
        var inputVal = $(this).val();
        var tableId = this.id.split("_").pop();
        _updateFormulaInputs(inputVal, tableId);
    },

    _updateFormulaInputs = function(inputVal, tableId) {
        if (inputVal === 'POINT') {
            $("#formulaPointPicker_"+tableId).fadeIn(150);
            $("#formulaWeatherStationTemp_"+tableId).hide();
            $("#formulaWeatherStationHumidity_"+tableId).hide();
        } else {
            $("#formulaPointPicker_"+tableId).hide();
            if (inputVal === 'TEMP_C' || inputVal == 'TEMP_F') {
                $("#formulaWeatherStationTemp_"+tableId).fadeIn(150);
                $("#formulaWeatherStationHumidity_"+tableId).hide();
            } else if (inputVal == 'HUMIDITY') {
                $("#formulaWeatherStationHumidity_"+tableId).fadeIn(150);
                $("#formulaWeatherStationTemp_"+tableId).hide();
            } else {
                $("#formulaWeatherStationHumidity_"+tableId).hide();
                $("#formulaWeatherStationTemp_"+tableId).hide();
            }
        }
    },

    _formulaInputSelectChangeTable = function() {
        var inputVal = $(this).val();
        var tableId = this.id.split("_").pop();
        if (inputVal === 'TIME') {
            // Hide & disable all normal inputs
            // show all time inputs
            $(".js-drFormula-notTimeInput_"+tableId)
                .prop("disabled", true)
                .hide();
            $(".js-drFormula-timeInput_"+tableId)
                .prop("disabled", false)
                .show();
        } else {
            // Hide & disable all time inputs
            // show all normal inputs
            $(".js-drFormula-timeInput_"+tableId)
                .prop("disabled", true)
                .hide();
            $(".js-drFormula-notTimeInput_"+tableId)
                .prop("disabled", false)
                .show();
        }
    },

    _pointPickerClick = function() {
        var tableId = this.id.split("_").pop();
        $("#picker_pointPicker_label").data("table-id", tableId).click();
    },

    _removeBtnClick = function() {
        $(this).parents(".js-drFormula-removeable").slideUp(150)
            .promise().done(function (){this.remove();});
    },

    _addTableBtnClick = function() {
        var $newRow = $('#tableRow_-1').clone().removeAttr("id");
        var tableId = _rowIndex;

        $newRow.find(".tableIndex").val(tableId);
        $newRow.find(".js-drFormula-appendTableIdData").each(function () {
            $(this).data("table-id", tableId);
        });
        $newRow.find(".js-drFormula-newEntryButton")
            .data("table-id", tableId);
        $newRow.find(".js-drFormula-timeInput_")
            .removeClass("js-drFormula-timeInput_")
            .addClass("js-drFormula-timeInput_" + tableId);
        $newRow.find(".js-drFormula-notTimeInput_")
            .removeClass("js-drFormula-notTimeInput_")
            .addClass("js-drFormula-notTimeInput_" + tableId);
        
        $newRow.find(".js-drFormula-appendTableId").each(function () {
            this.id = this.id + tableId;
        }).promise().done(function() {
            $newRow.hide().appendTo('#formulaTables').slideDown(150);
            var $inputSelect = $("#formulaInputSelect_"+tableId);
            $inputSelect.addClass("js-drFormula-formulaInputSelect");
            _updateFormulaInputs($inputSelect.val(), tableId);
            // add entry to this row
            _addTableEntryBtnClick.call($newRow.find(".js-drFormula-newEntryButton"));
        });

        _rowIndex++;
    },

    _addTableEntryBtnClick = function () {
        var tableId = $(this).data("table-id");
        var entryId = $(this).data("entry-id-next");


        var isTimeInput = $("#formulaInputSelect_" + tableId).val() === 'TIME';

        var $newRow = $('#tableEntry_-1_-1').clone();

        $newRow.attr("id","tableEntry_"+ tableId + "_" + entryId);

        $newRow.find(".js-drFormula-appendTableId").each(function () {
            this.id = this.id + tableId + "_" + entryId;
        });

        $newRow.find(".js-drFormula-tableEntryKey_")
            .removeClass("js-drFormula-tableEntryKey_")
            .addClass("js-drFormula-tableEntryKey_" + tableId);

       $newRow.find(".js-drFormula-timeInput_")
            .removeClass("js-drFormula-timeInput_")
            .addClass("js-drFormula-timeInput_" + tableId);

       $newRow.find(".js-drFormula-notTimeInput_")
            .removeClass("js-drFormula-notTimeInput_")
            .addClass("js-drFormula-notTimeInput_" + tableId);

       if (!isTimeInput) {
           $newRow.find(".js-drFormula-notTimeInput_" + tableId).show();
           $newRow.find(".js-drFormula-timeInput_" + tableId).hide();
       }

       $newRow.children().each(function() {
            this.id = this.id + tableId + "_" + entryId;
            // Setup the remove button
            $(this).find(".js-drFormula-removeEntry")
                .data("table-id", tableId)
                .data("entry-id", entryId);

        });

        $newRow.insertBefore("#tableEntries_"+tableId+" .js-drFormula-inputMax")
        .slideDown(150).promise().done(function() {
            $newRow.css("overflow","visible");
            // Forces scroll down to focused input field
            $("#tableEntries_"+tableId)
                .parent().scrollTop($("#tableEntries_"+tableId).parent().height() + 20);

            var numberEntries = $(".js-drFormula-tableEntryKey_" + tableId).size();
            if (numberEntries === 1) {
                $("#tableEntries_"+tableId)
                    .find(".js-drFormula-removeEntry").hide();
            } else {
                $("#tableEntries_"+tableId)
                 .find(".js-drFormula-removeEntry").show();
            }
        });

        $("#tableEntryKey_" + tableId + "_" + entryId + " > input").focus();

        // update the add button
        entryId++;
        $(this).data("entry-id-next", entryId);
    },

    _removeTableEntryBtnClick = function() {
        var entryId = $(this).data("entry-id");
        var tableId = $(this).data("table-id");

        $("#tableEntry_" + tableId + "_" + entryId)
            .slideUp(150, function() {
            this.remove();
            var numberEntries = $(".js-drFormula-tableEntryKey_" + tableId).size();
            if (numberEntries === 1) {
                $("#tableEntries_"+tableId)
                    .find(".js-drFormula-removeEntry").hide();
            }
        });
    },

    _beforeFormSubmit = function (e) {

        $(".js-drFormula-formulaInputSelect").each(function() {
            $this = $(this);
            $this.removeAttr("disabled");

            var inputVal = $this.val();
            var tableId = this.id.split("_").pop();

            if (inputVal === 'TIME') {
                $(".js-drFormula-notTimeInput_"+tableId).find("input")
                    .prop('disabled',true);
            } else {                
                $(".js-drFormula-timeInput_"+tableId).find("input")
                   .prop('disabled',true);
            }

            if (inputVal === 'POINT') {
                $("#formulaWeatherStationTemp_"+tableId)
                    .find("select").prop('disabled', true);
                $("#formulaWeatherStationHumidity_"+tableId)
                    .find("select").prop('disabled', true);
            } else if (inputVal === 'TEMP_C' || inputVal == 'TEMP_F') {
                $("#inputPointId_"+tableId).prop('disabled', true);
                $("#formulaWeatherStationHumidity_"+tableId)
                    .find("select").prop('disabled', true);
            } else if (inputVal == 'HUMIDITY') {
                $("#inputPointId_"+tableId).prop('disabled', true);
                $("#formulaWeatherStationTemp_"+tableId)
                    .find("select").prop('disabled', true);
            } else {
                $("#inputPointId_"+tableId)
                    .prop('disabled', true);
                $("#formulaWeatherStationTemp_"+tableId)
                    .find("select").prop('disabled', true);
                $("#formulaWeatherStationHumidity_"+tableId)
                    .find("select").prop('disabled', true);
            }
        });

        // adjust indexes to be sequential for java List
        $(".js-drFormula-table, .js-drFormula-function").each(function (index) {
            var $inputs = $(this).find(":input[name]");
            // Add table index to all table inputs
            $inputs.each(function() {
                var $self = $(this);
                var name = $self.attr("name").replace(/\[.*?\]/, "["+index+"]");
                $self.attr("name", name);
            });

            $(this)
                .find(".js-drFormula-tableEntries")
                .each(function (index2) {
                    var inputs = $(this).find(":input[name]");
                    inputs.each(function(entryLoopIndex) {

                    var $self = $(this);
                    var name = $self.attr("name");
                    // there are four entry inputs for one entry row (key, value, timeKey, valueKey)
                    // Each entry input needs the same entry id
                    var entryIndex = Math.floor(entryLoopIndex/4);

                    name = name.replace(/entries\[.*?\]/, "entries["+entryIndex+"]");
                    name = name.replace(/timeEntries\[.*?\]/, "timeEntries["+entryIndex+"]");
                    $self.attr("name", name);
                });
            });
        });
    },

    mod = {
        init: function () {
            if (_initialized) {
                return;
            }

            _rowIndex = $("#formulaRowIndex").val();

            $('#display_tabs').tabs().show();
            $("#assignments")
                .on("click", ".js-drFormula-remove", _removeAssignmentBtnClick)
                .on("click", ".js-drFormula-undo", _undoBtnClick);
            $("#newFunctionBtn").click(_newFunctionBtnClick);
            $("#formulaFunctions, #formulaTables")
                .on("change", ".js-drFormula-formulaInputSelect", _formulaInputSelectChange)
                .on("click", ".js-drFormula-pointPicker", _pointPickerClick);
            $("#formulaFunctions, #formulaTables").on("click", ".js-drFormula-remove", _removeBtnClick);
            $("#newTableBtn").click(_addTableBtnClick);
            $("#formulaTables")
                .on("click", ".js-drFormula-removeEntry",_removeTableEntryBtnClick)
                .on("change", ".js-drFormula-formulaInputSelect", _formulaInputSelectChangeTable)
                .on("click", ".js-drFormula-newEntryButton", _addTableEntryBtnClick);
            $("#formulaForm").submit(_beforeFormSubmit);

            _initialized = true;
        },

        pickerClose : function (selectedItems, picker) {
            var tableId = $("#picker_pointPicker_label").data("table-id");
            $("#inputPointId_"+tableId).val(selectedItems[0].pointId);
            $("#formulaPointPickerLbl_"+tableId).html(selectedItems[0].pointName);
        },

        unassignedAppCatPickerClose : function(selectedApCats, picker) {
            var choosenIds = [];
            for (var i = 0; i < selectedApCats.length; i++) {
                choosenIds.push(selectedApCats[i].applianceCategoryId);
                if (_addedAssignments.indexOf(selectedApCats[i].applianceCategoryId) < 0) {
                    _addAppCatAssignment(selectedApCats[i]);
                }
            }
            picker.excludeIds = choosenIds;
        },

        unassignedGearPickerClose : function(selectedGears, picker) {
            var choosenIds = [];
            for (var i = 0; i < selectedGears.length; i++) {
                choosenIds.push(selectedGears[i].gearId);
                if (_addedAssignments.indexOf(selectedGears[i].gearId) < 0) {
                    _addGearAssignment(selectedGears[i]);
                }
            }
            picker.excludeIds = choosenIds;
        },

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
                data: {'appCatId' : appCatId, 'formulaId' : formulaId, 'unassign' : unassign}
            })
            .done(function(data) {
                $("#formulaPickerRowAppCat_"+appCatId).html(data).flashYellow(.75);
            });
        },

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
                data: {'gearId' : gearId, 'formulaId' : formulaId, 'unassign' : unassign}
            })
            .done(function(data) {
                $("#formulaPickerRowGear_"+gearId).html(data).flashYellow(.75);
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
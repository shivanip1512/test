var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

Yukon.namespace('Yukon.DrFormula');

Yukon.DrFormula = (function() {
    var
    _initialized = false, 
    _rowIndex, // Set in intializer
    _addedAssignments = [],
    
    _addGearAssignment = function (gear) {
        var id = gear.gearId;

        var $newAssignmentInput = jQuery("#assignment_-1").clone();
        $newAssignmentInput.attr("id", "assignment_" + id);
        $newAssignmentInput.val(id);
        jQuery("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element 
        var $newAssignment = jQuery("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".f-id").html(id);
        $newAssignment.find(".f-name").html(gear.gearName);
        $newAssignment.find(".f-control-method").html(gear.controlMethod);
        $newAssignment.find(".f-remove").data("assign-id", id);
        $newAssignment.show();
        jQuery("#assignments").append($newAssignment);

        $assignRemoved = jQuery("#assignmentRemove_-1").clone();
        $assignRemoved.attr("id", "assignmentRemove_" + id);
        $assignRemoved.find(".f-undo").data("assign-id", id);
        jQuery("#assignments").append($assignRemoved);
        
        _addedAssignments.push(id);
        jQuery("#assignments").show();
        jQuery("#noAssignments").hide();
    },

    _addAppCatAssignment = function (appCat) {
        var id = appCat.applianceCategoryId;
        
        var $newAssignmentInput = jQuery("#assignment_-1").clone();
        $newAssignmentInput.attr("id", "assignment_" + id);
        $newAssignmentInput.val(id);
        jQuery("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element 
        var $newAssignment = jQuery("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".f-id").html(id);
        $newAssignment.find(".f-name").html(appCat.name);
        $newAssignment.find(".f-type").html(appCat.applianceType);
        $newAssignment.find(".f-remove").data("assign-id", id);
        $newAssignment.show();
        jQuery("#assignments").append($newAssignment);

        $assignRemoved = jQuery("#assignmentRemove_-1").clone();
        $assignRemoved.attr("id", "assignmentRemove_" + id);
        $assignRemoved.find(".f-undo").data("assign-id", id);
        jQuery("#assignments").append($assignRemoved);
        
        _addedAssignments.push(id);
        jQuery("#assignments").show();
        jQuery("#noAssignments").hide();
    },

    _removeAssignmentBtnClick = function() {
        var id = jQuery(this).data("assign-id");
        jQuery("#assignmentAdd_"+id).hide();
        jQuery("#assignmentRemove_"+id).show();
        jQuery("#assignment_"+id).prop("disabled", true);
    },

    _undoBtnClick = function() {
        var id = jQuery(this).data("assign-id");
        jQuery("#assignmentAdd_"+id).show();
        jQuery("#assignmentRemove_"+id).hide();
        jQuery("#assignment_"+id).prop("disabled", false);
    },

    _newFunctionBtnClick = function() {
        var $newRow = jQuery('#dummyFunction').clone().removeAttr("id");

        $newRow.find("input, select").each(function() {
            jQuery(this).attr("name", "functions[" + _rowIndex + "]." + jQuery(this).attr("name"));
        });
        $newRow.find(".f-appendTableId").each(function () {
            this.id = this.id + _rowIndex;
        });

        $newRow.appendTo('#formulaFunctions').slideDown(150);
        _rowIndex++;
    },

    _formulaInputSelectChange = function() {
        var inputVal = jQuery(this).val();
        var tableId = this.id.split("_").pop();

        if (inputVal === 'POINT') {
            jQuery("#formulaPointPicker_"+tableId).slideDown(150);
        } else {
            jQuery("#formulaPointPicker_"+tableId).slideUp(150);
        }
    },

    _formulaInputSelectChangeTable = function() {
        var inputVal = jQuery(this).val();
        var tableId = this.id.split("_").pop();
        if (inputVal === 'TIME') {
            jQuery("#inputMax_" + tableId).hide();
            jQuery("#timeInputMax_" + tableId).show();
        } else {
            jQuery("#timeInputMax_" + tableId).hide();
            jQuery("#inputMax_" + tableId).show();
        }
    },

    _pointPickerClick = function() {
        var tableId = this.id.split("_").pop();
        jQuery("#picker_pointPicker_label").data("table-id", tableId).click();
    },

    _removeBtnClick = function() {
        jQuery(this).parents(".f-removeable").slideUp(150).promise().done(function (){this.remove();});
    },

    _addTableBtnClick = function() {

        var $newRow = jQuery('#dummyTable').clone().removeAttr("id");

        $newRow.find(":input[name], select").each(function() {
            var $this = jQuery(this);
            $this.attr("name", "tables[]." + $this.attr("name"));
        });

        $newRow.find(".tableIndex").val(_rowIndex);
        $newRow.find(".f-appendTableId").each(function () {
            this.id = this.id + _rowIndex;
        });
        $newRow.find(".f-appendTableIdData").each(function () {
            jQuery(this).data("table-id", _rowIndex);
        });

        $newRow.hide().appendTo('#formulaTables').slideDown(150);
        _rowIndex++;
    },

    _addTableEntryBtnClick = function () {

        var tableId = jQuery(this).data("table-id");
        var entryId = jQuery(this).data("entry-id-next");
        var isTimeInput = jQuery("#formulaInputSelect_" + tableId).val() === 'TIME';
        jQuery("#noEntriesMessage_" + tableId).hide();

        var $newRow;
        if (isTimeInput) {
            $newRow = jQuery('#dummyTimeEntry').clone();
        } else {
            $newRow = jQuery('#dummyEntry').clone();
        }

        $newRow.attr("id","tableEntry_"+ tableId + "_" + entryId);

        $newRow.find(".f-appendTableId").each(function () {
            this.id = this.id + tableId + "_" + entryId;
        });
        $newRow.find(".f-tableEntryKey_")
            .removeClass("f-tableEntryKey_")
            .addClass("f-tableEntryKey_" + tableId);

        $newRow.children().each(function() {
            var $this = jQuery(this);
            $this.id = $this.id + tableId + "_" + entryId;

            $this.find("input").each(function () {
                var $thisInput = jQuery(this);
                if (isTimeInput) {
                    var newName = "tables[].timeEntries[]." + $thisInput.attr("name");
                    $thisInput.attr("name", newName);
                    $thisInput.id = newName;
                } else {
                    var newName = "tables[].entries[]." + $thisInput.attr("name");
                    $thisInput.attr("name", newName);
                    $thisInput.id = newName;
                }

            });
            // Setup the remove button
            $this.find(".f-removeEntry").data("table-id", tableId).data("entry-id", entryId);

        });

        $newRow.appendTo("#tableEntries_" + tableId)
        .slideDown(150).promise().done(function() {
            // Forces scroll down to focused input field
            jQuery("#tableEntries_"+tableId).parent().scrollTop(jQuery("#tableEntries_"+tableId).parent().height() + 20);
        });

        jQuery("#tableEntryKey_" + tableId + "_" + entryId + " > input").focus();
        jQuery("#formulaInputSelect_" + tableId).attr("disabled", "disabled");

        // update the add button
        entryId++;
        jQuery(this).data("entry-id-next", entryId);
    },

    _removeTableEntryBtnClick = function() {
        var entryId = jQuery(this).data("entry-id");
        var tableId = jQuery(this).data("table-id");

        jQuery("#tableEntry_" + tableId + "_" + entryId)
            .slideUp(150, function() {
            this.remove();
            if (jQuery(".f-tableEntryKey_" + tableId).size() < 1) {
                jQuery("#noEntriesMessage_" + tableId).show(150);
                jQuery("#formulaInputSelect_" + tableId).removeAttr("disabled");
            }
        });
    },

    _beforeFormSubmit = function (e) {
        // Need to send these along with form submit
        jQuery(".f-formulaInputSelect").removeAttr("disabled");

        // adjust indexes to be sequential for java List
        jQuery(".f-table, .f-function").each(function (index) {
            var $inputs = jQuery(this).find(":input[name]");
            $inputs.each(function() {
                var $self = jQuery(this);
                var name = $self.attr("name").replace(/\[.*?\]/, "["+index+"]");
                $self.attr("name", name);
            });
            jQuery(this).find(".f-tableEntries").each(function (index2) {
                var inputs = jQuery(this).find(":input[name]");
                inputs.each(function(entryLoopIndex) {
                    var $self = jQuery(this);
                    var name = $self.attr("name");
                    var entryIndex = Math.floor(entryLoopIndex/2); // key/value needs to match (every other one)
                    name = name.replace(/entries\[.*?\]/, "entries["+entryIndex+"]");
                    name = name.replace(/timeEntries\[.*?\]/, "timeEntries["+entryIndex+"]");
                    $self.attr("name", name);
                });
            });
        });
    },

    drFormulaModule = {
        init: function () {
            if (_initialized) {
                return;
            }

            _rowIndex = jQuery("#formulaRowIndex").val();

            jQuery("#assignments")
                .on("click", ".f-remove", _removeAssignmentBtnClick)
                .on("click", ".f-undo", _undoBtnClick);
            jQuery("#newFunctionBtn")
                .click(_newFunctionBtnClick);
            jQuery("#formulaFunctions, #formulaTables")
                .on("change", ".f-formulaInputSelect", _formulaInputSelectChange)
                .on("click", ".f-pointPicker", _pointPickerClick);
            jQuery("#formulaFunctions, #formulaTables")
                .on("click", ".f-remove", _removeBtnClick);
            jQuery("#newTableBtn")
                .click(_addTableBtnClick);
            jQuery("#formulaTables").on("click", ".f-removeEntry",_removeTableEntryBtnClick)
                .on("change", ".f-formulaInputSelect", _formulaInputSelectChangeTable)
                .on("click", ".f-newEntryButton", _addTableEntryBtnClick);
            jQuery("#formulaForm")
                .submit(_beforeFormSubmit);

            _initialized = true;
        },

        pickerClose : function (selectedItems, picker) {
            var tableId = jQuery("#picker_pointPicker_label").data("table-id");
            jQuery("#inputPointId_"+tableId).val(selectedItems[0].pointId);
            jQuery("#formulaPointPickerLbl_"+tableId).html(selectedItems[0].pointName);
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
        }
    };
    return drFormulaModule;
}());

jQuery(function() {

    Yukon.DrFormula.init();

    jQuery("#assignments").on("mouseenter", ".f-show-on-hover-target", function() {
        jQuery(this).find(".f-show-on-hover").show();
    }).on("mouseleave", ".f-show-on-hover-target", function() {
        jQuery(this).find(".f-show-on-hover").hide();
    });
});
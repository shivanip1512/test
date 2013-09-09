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

        var $newAssignmentInput = jQuery("#assignmentInput_-1").clone();
        $newAssignmentInput.attr("id", "assignmentInput_" + id);
        $newAssignmentInput.val(id);
        jQuery("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element 
        var $newAssignment = jQuery("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".f-drFormula-program").html(gear.programName);
        $newAssignment.find(".f-drFormula-name").html(gear.gearName);
        $newAssignment.find(".f-drFormula-control-method").html(gear.controlMethod);
        $newAssignment.find(".f-drFormula-remove").data("assign-id", id);
        $newAssignment.show();
        jQuery("#assignments").append($newAssignment);

        var $assignRemoved = jQuery("#assignmentRemove_-1").clone();
        $assignRemoved.attr("id", "assignmentRemove_" + id);
        $assignRemoved.find(".f-drFormula-undo").data("assign-id", id);
        jQuery("#assignments").append($assignRemoved);
        
        _addedAssignments.push(id);
        jQuery("#assignments").show();
        jQuery("#noAssignments").hide();
    },

    _addAppCatAssignment = function (appCat) {
        var id = appCat.applianceCategoryId;
        
        var $newAssignmentInput = jQuery("#assignmentInput_-1").clone();
        $newAssignmentInput.attr("id", "assignmentInput_" + id);
        $newAssignmentInput.val(id);
        jQuery("#assignments").append($newAssignmentInput);

        // #assignment_-1 is the dummy assignment element 
        var $newAssignment = jQuery("#assignmentAdd_-1").clone();
        $newAssignment.attr("id", "assignmentAdd_" + id);
        $newAssignment.find(".f-drFormula-average-load").html(appCat.applianceLoad + "  kW");
        $newAssignment.find(".f-drFormula-name").html(appCat.name);
        $newAssignment.find(".f-drFormula-type").html(appCat.applianceType);
        $newAssignment.find(".f-drFormula-remove").data("assign-id", id);
        $newAssignment.show();
        jQuery("#assignments").append($newAssignment);

        var $assignRemoved = jQuery("#assignmentRemove_-1").clone();
        $assignRemoved.attr("id", "assignmentRemove_" + id);
        $assignRemoved.find(".f-drFormula-undo").data("assign-id", id);
        jQuery("#assignments").append($assignRemoved);
        
        _addedAssignments.push(id);
        jQuery("#assignments").show();
        jQuery("#noAssignments").hide();
    },

    _removeAssignmentBtnClick = function() {
        var id = jQuery(this).data("assign-id");
        jQuery("#assignmentAdd_"+id).hide();
        jQuery("#assignmentRemove_"+id).show();
        jQuery("#assignmentInput_"+id).prop("disabled", true);
    },

    _undoBtnClick = function() {
        var id = jQuery(this).data("assign-id");
        jQuery("#assignmentAdd_"+id).show();
        jQuery("#assignmentRemove_"+id).hide();
        jQuery("#assignmentInput_"+id).prop("disabled", false);
    },

    _newFunctionBtnClick = function() {
        var $newRow = jQuery('#dummyFunction').clone().removeAttr("id");

        $newRow.find("input, select").each(function() {
            jQuery(this).attr("name", "functions[" + _rowIndex + "]." + jQuery(this).attr("name"));
        });
        $newRow.find(".f-drFormula-appendTableId").each(function () {
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
        jQuery(this).parents(".f-drFormula-removeable").slideUp(150).promise().done(function (){this.remove();});
    },

    _addTableBtnClick = function() {

        var $newRow = jQuery('#dummyTable').clone().removeAttr("id");

        $newRow.find(":input[name], select").each(function() {
            var $this = jQuery(this);
            $this.attr("name", "tables[]." + $this.attr("name"));
        });

        $newRow.find(".tableIndex").val(_rowIndex);
        $newRow.find(".f-drFormula-appendTableId").each(function () {
            this.id = this.id + _rowIndex;
        });
        $newRow.find(".f-drFormula-appendTableIdData").each(function () {
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

        $newRow.find(".f-drFormula-appendTableId").each(function () {
            this.id = this.id + tableId + "_" + entryId;
        });
        $newRow.find(".f-drFormula-tableEntryKey_")
            .removeClass("f-drFormula-tableEntryKey_")
            .addClass("f-drFormula-tableEntryKey_" + tableId);

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
            $this.find(".f-drFormula-removeEntry").data("table-id", tableId).data("entry-id", entryId);

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
            if (jQuery(".f-drFormula-tableEntryKey_" + tableId).size() < 1) {
                jQuery("#noEntriesMessage_" + tableId).show(150);
                jQuery("#formulaInputSelect_" + tableId).removeAttr("disabled");
            }
        });
    },

    _beforeFormSubmit = function (e) {
        // Need to send these along with form submit
        jQuery(".f-drFormula-formulaInputSelect").removeAttr("disabled");

        // adjust indexes to be sequential for java List
        jQuery(".f-drFormula-table, .f-drFormula-function").each(function (index) {
            var $inputs = jQuery(this).find(":input[name]");
            $inputs.each(function() {
                var $self = jQuery(this);
                var name = $self.attr("name").replace(/\[.*?\]/, "["+index+"]");
                $self.attr("name", name);
            });
            jQuery(this).find(".f-drFormula-tableEntries").each(function (index2) {
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

    _ajaxPagingBtnClick = function(event) {
        var url = jQuery(event.currentTarget).data('url');
        jQuery(event.target).parents(".f-drFormula-replaceViaAjax").load(url);
        return false;
    },

    _sortBtnClick = function(event) {
        jQuery(event.target).parents(".f-drFormula-replaceViaAjax").load(this.href);
        return false;
    },

    drFormulaModule = {
        init: function () {
            if (_initialized) {
                return;
            }

            _rowIndex = jQuery("#formulaRowIndex").val();
            jQuery('#display_tabs').tabs({'cookie' : {}}).show();

            jQuery("#assignments")
                .on("click", ".f-drFormula-remove", _removeAssignmentBtnClick)
                .on("click", ".f-drFormula-undo", _undoBtnClick);
            jQuery("#newFunctionBtn")
                .click(_newFunctionBtnClick);
            jQuery("#formulaFunctions, #formulaTables")
                .on("change", ".f-drFormula-formulaInputSelect", _formulaInputSelectChange)
                .on("click", ".f-drFormula-pointPicker", _pointPickerClick);
            jQuery("#formulaFunctions, #formulaTables")
                .on("click", ".f-drFormula-remove", _removeBtnClick);
            jQuery("#newTableBtn")
                .click(_addTableBtnClick);
            jQuery("#formulaTables").on("click", ".f-drFormula-removeEntry",_removeTableEntryBtnClick)
                .on("change", ".f-drFormula-formulaInputSelect", _formulaInputSelectChangeTable)
                .on("click", ".f-drFormula-newEntryButton", _addTableEntryBtnClick);
            jQuery("#formulaForm")
                .submit(_beforeFormSubmit);
            jQuery(".f-drFormula-replaceViaAjax")
                .on('click', '.f-ajaxPaging', _ajaxPagingBtnClick)
                .on('click', ".f-drFormula-sortLink", _sortBtnClick);

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

            jQuery.ajax("assignFormulaToAppCat", {
                data: {'appCatId' : appCatId, 'formulaId' : formulaId, 'unassign' : unassign}
            })
            .done(function(data) {
                jQuery("#formulaPickerRowAppCat_"+appCatId).html(data).flashYellow(.75);
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

            jQuery.ajax("assignFormulaToGear", {
                data: {'gearId' : gearId, 'formulaId' : formulaId, 'unassign' : unassign}
            })
            .done(function(data) {
                jQuery("#formulaPickerRowGear_"+gearId).html(data).flashYellow(.75);
            });
        }
    };
    return drFormulaModule;
}());

jQuery(function() {
    Yukon.DrFormula.init();

    jQuery("#assignments").on("mouseenter", ".f-drFormula-show-on-hover-target", function() {
        jQuery(this).find(".f-drFormula-show-on-hover").show();
    }).on("mouseleave", ".f-drFormula-show-on-hover-target", function() {
        jQuery(this).find(".f-drFormula-show-on-hover").hide();
    });
});
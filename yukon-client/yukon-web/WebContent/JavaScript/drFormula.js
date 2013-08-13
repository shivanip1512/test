pickerClose = function (selectedItems, picker) {
    var tableId = jQuery("#picker_pointPicker_label").data("table-id");
    jQuery("#inputPointId_"+tableId).val(selectedItems[0].pointId);
    jQuery("#formulaPointPickerLbl_"+tableId).html(selectedItems[0].pointName);
};

jQuery(function () {
    var rowIndex = jQuery("#formulaRowIndex").val();

    // *****  Functions  *****

    // Add new Function row
    jQuery("#newFunctionBtn").click(function() {
        var $newRow = jQuery('#dummyFunction').clone().removeAttr("id");

        $newRow.find("input, select").each(function() {
            jQuery(this).attr("name", "functions[" + rowIndex + "]." + jQuery(this).attr("name"));
        });
        $newRow.find(".f-appendTableId").each(function () {
            this.id = this.id + rowIndex;
        });

        $newRow.appendTo('#formulaFunctions').slideDown(150);
        rowIndex++;
    });

    jQuery("#formulaFunctions, #formulaTables").on("change", ".f-formulaInputSelect", function() {
        var inputVal = jQuery(this).val();
        var tableId = this.id.split("_").pop();

        if (inputVal === 'POINT') {
            jQuery("#formulaPointPicker_"+tableId).slideDown(150);
        } else {
            jQuery("#formulaPointPicker_"+tableId).slideUp(150);
        }
    })
    .on("click", ".f-pointPicker", function() {
        var tableId = this.id.split("_").pop();
        jQuery("#picker_pointPicker_label").data("table-id", tableId).click();
    });

    jQuery("#formulaFunctions, #formulaTables").on("click", ".f-remove", function() {
        jQuery(this).parents(".f-removeable").slideUp(150).promise().done(function (){this.remove();});
    });

    // *****  Tables  *****

    // Add new Table row
    jQuery("#newTableBtn").click(function() {

        var $newRow = jQuery('#dummyTable').clone().removeAttr("id");

        $newRow.find(":input[name], select").each(function() {
            var $this = jQuery(this);
            $this.attr("name", "tables[]." + $this.attr("name"));
        });

        $newRow.find(".tableIndex").val(rowIndex);
        $newRow.find(".f-appendTableId").each(function () {
            this.id = this.id + rowIndex;
        });
        $newRow.find(".f-appendTableIdData").each(function (){
            jQuery(this).data("table-id", rowIndex);
        });

        $newRow.hide().appendTo('#formulaTables').slideDown(150);
        rowIndex++;
    });

    // Remove Table Entry
    jQuery("#formulaTables").on("click", ".f-removeEntry", function() {
        var entryId = jQuery(this).data("entry-id");
        var tableId = jQuery(this).data("table-id");

        jQuery("#tableEntryValue_" + tableId + "_" + entryId + " , #tableEntryKey_" + tableId + "_" + entryId)
            .slideUp(150)
            .promise().done(function() {
            this.remove();
            if (jQuery(".f-tableEntryKey_" + tableId).size() < 1) {
                jQuery("#noEntriesMessage_" + tableId).show(150);
                jQuery("#formulaInputSelect_" + tableId).removeAttr("disabled");
            }
        });
    })
    .on("change", ".f-formulaInputSelect", function() {
        var inputVal = jQuery(this).val();
        var tableId = this.id.split("_").pop();
        if (inputVal === 'TIME') {
            jQuery("#inputMax_" + tableId).hide();
            jQuery("#timeInputMax_" + tableId).show();
        } else {
            jQuery("#timeInputMax_" + tableId).hide();
            jQuery("#inputMax_" + tableId).show();
        }
    })
    .on("click", ".f-newEntryButton", function () {
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

        $newRow.find(".f-appendTableId").each(function () {
            this.id = this.id + tableId + "_" + entryId;
        });
        $newRow.children(".f-tableEntryKey_")
            .removeClass("f-tableEntryKey_")
            .addClass("f-tableEntryKey_" + tableId);

        // remove the div id=dummyEntry and grab the key/value divs
        $newRow = $newRow.children().unwrap().hide();

        $newRow.each(function() {
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

        })
        .appendTo("#tableEntries_" + tableId)
        .slideDown(150).promise().done(function() {
            // Forces scroll down to focused input field
            jQuery("#tableEntries_"+tableId).parent().scrollTop(jQuery("#tableEntries_"+tableId).parent().height() + 20);
        });

        jQuery("#tableEntryKey_" + tableId + "_" + entryId + " > input").focus();
        jQuery("#formulaInputSelect_" + tableId).attr("disabled", "disabled");

        // update the add button
        entryId++;
        jQuery(this).data("entry-id-next", entryId);
    });


    jQuery("#formulaForm").submit(function (e) {
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

    });
});
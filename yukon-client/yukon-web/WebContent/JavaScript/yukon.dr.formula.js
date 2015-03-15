yukon.namespace('yukon.dr.formula');

/**
 * Handles the operations related to formula and formula assignment for
 * estimated Load.
 * 
 * @module yukon.dr.formula
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.dr.formula = (function () {
    
    var _initialized = false, _rowIndex, 
    
    _addedAssignments = [],
    
    /**
     * Show and hide inputs based on input type.
     * @param {Object} type - Value selected in input drop down.
     * @param {Object} tableId - Id of table.
     */
    _updateFormulaOptions = function (type, tableId) {
        
        var select = $('#formula-input-select_' + tableId);
        var functionContainer = select.closest('.js-formula-function');
        var min = functionContainer.find('.js-input-min');
        var max = functionContainer.find('.js-input-max');
        
        if (type === 'POINT') {
            $('#formulaPointPicker_' + tableId).fadeIn(150);
            $('#formula-weather-temp_' + tableId).hide();
            $('#formula-weather-humidity_' + tableId).hide();
            min.prop('disabled', false);
            max.prop('disabled', false);
        } else if (type == 'TIME_FUNCTION') {
            $('#formulaPointPicker_' + tableId).hide();
            $('#formula-weather-temp_' + tableId).hide();
            $('#formula-weather-humidity_' + tableId).hide();
            min.val('0.0').prop('disabled', true);
            max.val('24.0').prop('disabled', true);
        } else {
            // Temp or humidity
            min.prop('disabled', false);
            max.prop('disabled', false);
            $('#formulaPointPicker_' + tableId).hide();
            if (type === 'TEMP_C' || type == 'TEMP_F') {
                $('#formula-weather-temp_' + tableId).fadeIn(150);
                $('#formula-weather-humidity_' + tableId).hide();
            } else if (type == 'HUMIDITY') {
                $('#formula-weather-humidity_' + tableId).fadeIn(150);
                $('#formula-weather-temp_' + tableId).hide();
            } else {
                $('#formula-weather-humidity_' + tableId).hide();
                $('#formula-weather-temp_' + tableId).hide();
            }
        }
    },
    
    /** Create structure for lookup table entry */
    _addTableEntryBtnClick = function (ev) {
        
        var tableId = $(this).data('table-id');
        var entryId = $(this).data('entry-id-next');
        
        var isTimeInput = $('#formula-input-select_' + tableId).val() === 'TIME_LOOKUP';
        
        var row = $('#table-entry_-1_-1').clone()
        .attr('id', 'table-entry_' + tableId + '_' + entryId);
        
        row.find('.js-formula-append-table-id').each(function () {
            this.id = this.id + tableId + '_' + entryId;
        });
        
        row.find('.js-formula-table-entry-key_')
        .removeClass('js-formula-table-entry-key_')
        .addClass('js-formula-table-entry-key_' + tableId);
        
        row.find('.js-formula-time-input_')
        .removeClass('js-formula-time-input_')
        .addClass('js-formula-time-input_' + tableId);
        
        row.find('.js-formula-not-time-input_')
        .removeClass('js-formula-not-time-input_')
        .addClass('js-formula-not-time-input_' + tableId);
        
        if (!isTimeInput) {
            row.find('.js-formula-not-time-input_' + tableId).show();
            row.find('.js-formula-time-input_' + tableId).hide();
        }
        
        row.children().each(function () {
            this.id = this.id + tableId + '_' + entryId;
            // Setup the remove button
            $(this).find('.js-formula-remove-entry')
            .data('table-id', tableId).data('entry-id', entryId);
        });
        
        row.insertBefore('#table-entries_' + tableId + ' .js-formula-input-max')
        .slideDown(150).promise().done(function () {
            row.css('overflow', 'visible');
            // Forces scroll down to focused input field
            var entry = $('#table-entries_' + tableId);
            entry.parent().scrollTop(entry.parent().height() + 20);
            
            var numberEntries = $('.js-formula-table-entry-key_' + tableId).size();
            entry.find('.js-formula-remove-entry').toggle(numberEntries !== 1);
        });
        
        $('#table-entry-key_' + tableId + '_' + entryId + ' > input').focus();
        
        // update the add button
        entryId++;
        $(this).data('entry-id-next', entryId);
    },
    
    mod = {
        init : function () {
            
            if (_initialized) { return; }
            
            _rowIndex = $('#formula-row-index').val();
            
            /** Remove and undo remove assignments. */
            $('#assignments').on('click', '.js-formula-remove', function (ev) {
                
                var id = $(this).data('assign-id');
                $('#assignment-input_' + id).prop('disabled', true);
                $('#assignment-add_' + id).hide();
                $('#assignment-remove_' + id).show();
                
            }).on('click', '.js-formula-undo', function (ev) {
                
                var id = $(this).data('assign-id');
                $('#assignment-input_' + id).prop('disabled', false);
                $('#assignment-add_' + id).show();
                $('#assignment-remove_' + id).hide();
                
            });
            
            /** Add a new function. */
            $('#new-function-btn').click(function () {
                
                var row = $('#function-row_-1').clone().removeAttr('id');
                var tableId = _rowIndex;
                
                row.find('.js-formula-append-table-id').each(function () {
                    this.id = this.id + tableId;
                }).promise().done(function () {
                    
                    row.appendTo('#formula-functions').slideDown(150);
                    
                    var select = $('#formula-input-select_' + tableId)
                    .addClass('js-formula-formula-input-select');
                    
                    _updateFormulaOptions(select.val(), tableId);
                    
                });
                
                _rowIndex++;
            });
            
            /** Update the forumla options when the forumula input type changes. */
            $('#formula-functions, #formula-tables').on('change', '.js-formula-formula-input-select', function (ev) {
                // FormulaInput type
                // com.cannontech.dr.estimatedload.FormulaInput
                var tableId = this.id.split('_').pop();
                _updateFormulaOptions($(this).val(), tableId);
                
            });
            
            /** Record the table id on the button when point picker is used. */
            $('#formula-functions, #formula-tables').on('click', '.js-formula-point-picker', function (ev) {
                var tableId = this.id.split('_').pop();
                $('#picker-pointPicker-btn').data('table-id', tableId).click();
            });
            
            /** Remove a function or table from a forumula. */
            $('#formula-functions, #formula-tables').on('click', '.js-formula-remove', function (ev) {
                $(this).parents('.js-formula-removeable').slideUp(150).promise()
                .done(function () { this.remove(); });
            });
            
            /** Add a new lookup table. */
            $('#new-lookup-table-btn').click(function (ev) {
                
                var tableId = _rowIndex;
                var row = $('#table-row_-1').clone().removeAttr('id');
                
                row.find('.tableIndex').val(tableId);
                row.find('.js-formula-new-entry-btn').data('table-id', tableId);
                
                row.find('.js-formula-time-input_')
                .removeClass('js-formula-time-input_')
                .addClass('js-formula-time-input_' + tableId);
                
                row.find('.js-formula-not-time-input_')
                .removeClass('js-formula-not-time-input_')
                .addClass('js-formula-not-time-input_' + tableId);
                
                row.find('.js-formula-append-table-id').each(function () {
                    this.id = this.id + tableId;
                }).promise().done(function () {
                    
                    row.hide().appendTo('#formula-tables').slideDown(150);
                    
                    var select = $('#formula-input-select_' + tableId);
                    select.addClass('js-formula-formula-input-select');
                    _updateFormulaOptions(select.val(), tableId);
                    // add entry to this row
                    _addTableEntryBtnClick.call(row.find('.js-formula-new-entry-btn'));
                });
                
                _rowIndex++;
            });
            
            $('#formula-tables').on('click', '.js-formula-remove-entry', function (ev) {
                
                var entryId = $(this).data('entry-id');
                var tableId = $(this).data('table-id');
                
                $('#table-entry_' + tableId + '_' + entryId).slideUp(150, function () {
                    this.remove();
                    var numberEntries = $('.js-formula-table-entry-key_' + tableId).size();
                    if (numberEntries === 1) {
                        $('#table-entries_' + tableId).find('.js-formula-remove-entry').hide();
                    }
                });
            }).on('change', '.js-formula-formula-input-select', function (ev) {
                
                var type = $(this).val();
                var tableId = this.id.split('_').pop();
                if (type === 'TIME_LOOKUP') {
                    // Hide & disable all normal inputs
                    // show all time inputs
                    $('.js-formula-not-time-input_' + tableId).prop('disabled', true)
                            .hide();
                    $('.js-formula-time-input_' + tableId).prop('disabled', false)
                            .show();
                } else {
                    // Hide & disable all time inputs
                    // show all normal inputs
                    $('.js-formula-time-input_' + tableId).prop('disabled', true).hide();
                    $('.js-formula-not-time-input_' + tableId).prop('disabled', false).show();
                }
            }).on('click', '.js-formula-new-entry-btn', _addTableEntryBtnClick);
            
            /** Disable the fields before the form is submitted. */
            $('#formulaForm').submit(function (e) {
                
                $('.js-formula-formula-input-select').each(function (index) {
                    
                    $(this).removeAttr('disabled');
                    
                    var type = $(this).val();
                    var tableId = this.id.split('_').pop();
                    
                    if (type === 'TIME_LOOKUP') {
                        $('.js-formula-not-time-input_' + tableId).find('input').prop('disabled', true);
                    } else {
                        $('.js-formula-time-input_' + tableId).find('input').prop('disabled', true);
                    }
                    
                    if (type === 'POINT') {
                        $('#formula-weather-temp_' + tableId).find('select').prop('disabled', true);
                        $('#formula-weather-humidity_' + tableId).find('select').prop('disabled', true);
                    } else if (type === 'TEMP_C' || type == 'TEMP_F') {
                        $('#input-point-id_' + tableId).prop('disabled', true);
                        $('#formula-weather-humidity_' + tableId).find('select').prop('disabled', true);
                    } else if (type == 'HUMIDITY') {
                        $('#input-point-id_' + tableId).prop('disabled', true);
                        $('#formula-weather-temp_' + tableId).find('select').prop('disabled', true);
                    } else {
                        $('#input-point-id_' + tableId).prop('disabled', true);
                        $('#formula-weather-temp_' + tableId).find('select').prop('disabled', true);
                        $('#formula-weather-humidity_' + tableId).find('select').prop('disabled', true);
                    }
                });
                
                /** Adjust indexes to be sequential for java List */
                $('.js-formula-table, .js-formula-function').each(function (index) {
                    
                    var inputs = $(this).find(':input[name]');
                    
                    // Add table index to all table inputs
                    inputs.each(function () {
                        var name = $(this).attr('name').replace(/\[.*?\]/, '[' + index + ']');
                        $(this).attr('name', name);
                    });
                    
                    $(this).find('.js-formula-table-entries').each(function (index2) {
                        
                        var inputs = $(this).find(':input[name]');
                        inputs.each(function (entryLoopIndex) {
                            
                            var name = $(this).attr('name');
                            // there are four entry inputs for one entry
                            // row (key, value, timeKey, valueKey)
                            // Each entry input needs the same entry id
                            var entryIndex = Math.floor(entryLoopIndex / 4);
                            
                            name = name.replace(/entries\[.*?\]/, 'entries[' + entryIndex + ']');
                            name = name.replace(/timeEntries\[.*?\]/, 'timeEntries[' + entryIndex + ']');
                            $(this).attr('name', name);
                        });
                    });
                });
            });
            
            _initialized = true;
        },
        
        /**
         * This is called when point is selected from point picker. It sets the
         * selected point.
         * @param {Array} selected - Selected items array.
         * @param {Object} picker - Picker object.
         */
        pointChosen : function (selected, picker) {
            var point = selected[0];
            var tableId = $('#picker-pointPicker-btn').data('table-id');
            $('#input-point-id_' + tableId).val(point.pointId);
            $('#formula-point-picker-label_' + tableId).html(point.pointName);
        },
        
        /**
         * Used on the home page > assignments tab and the forumla
         * page in edit mode when a selection is made in the 
         * appliance category picker. 
         * @param {Array} selectedApCats - Selected items array.
         * @param {Object} picker - Picker object.
         */
        unassignedAppCatPickerClose : function (selected, picker) {
            
            var choosenIds = [];
            
            for (var i = 0; i < selected.length; i++) {
                
                var appCat = selected[i],
                    id = appCat.applianceCategoryId;
                    
                choosenIds.push(id);
                
                if (_addedAssignments.indexOf(id) < 0) {
                    
                    $('#assignment-input_-1').clone()
                    .attr('id', 'assignment-input_' + id)
                    .val(id)
                    .appendTo('#assignments');
                    
                    // #assignment_-1 is the dummy assignment element
                    var row = $('#assignment-add_-1').clone()
                    .attr('id', 'assignment-add_' + id);
                    
                    row.find('.js-formula-average-load').html(appCat.applianceLoad + ' kW');
                    row.find('.js-formula-name').html(appCat.name);
                    row.find('.js-formula-type').html(appCat.applianceType);
                    row.find('.js-formula-remove').data('assign-id', id);
                    row.show().appendTo('#assignments');
                    
                    $('#assignment-remove_-1').clone()
                    .attr('id', 'assignment-remove_' + id)
                    .appendTo('#assignments')
                    .find('.js-formula-undo').data('assign-id', id);
                    
                    _addedAssignments.push(id);
                    $('#assignments').show();
                    $('#no-assignments').hide();
                }
            }
            
            picker.excludeIds = choosenIds;
        },
        
        /**
         * This is called when gear picker is closed from formula page. List of
         * included gear ids are set.
         * @param {Array} selected - Selected items array.
         * @param {Object} picker - Picker object.
         */
        unassignedGearPickerClose : function (selected, picker) {
            
            var choosenIds = [];
            for (var i = 0; i < selected.length; i++) {
                
                var gear = selected[i],
                    id = gear.gearId;
                    
                choosenIds.push(id);
                
                if (_addedAssignments.indexOf(id) < 0) {
                    
                    var rowInput = $('#assignment-input_-1').clone();
                    rowInput.attr('id', 'assignment-input_' + id);
                    rowInput.val(id);
                    $('#assignments').append(rowInput);
                    
                    // #assignment_-1 is the dummy assignment element
                    var row = $('#assignment-add_-1').clone()
                    .attr('id', 'assignment-add_' + id);
                    
                    row.find('.js-formula-program').html(gear.programName);
                    row.find('.js-formula-name').html(gear.gearName);
                    row.find('.js-formula-control-method').html(gear.controlMethod);
                    row.find('.js-formula-remove').data('assign-id', id);
                    row.show().appendTo('#assignments');
                    
                    $('#assignment-remove_-1').clone()
                    .attr('id', 'assignment-remove_' + id)
                    .appendTo('#assignments')
                    .find('.js-formula-undo').data('assign-id', id);
                    
                    _addedAssignments.push(id);
                    $('#assignments').show();
                    $('#no-assignments').hide();
                }
            }
            
            picker.excludeIds = choosenIds;
        },
        
        /**
         * This is called when formula picker for appliance categories is
         * closed. It sends ajax request to set selected formula for a appliance
         * category.
         * @param {Array} selected - Selected items array.
         * @param {Object} picker - Picker object.
         */
        appCatFormulaPickerClose : function (selected, picker) {
            
            var appCatId = picker.pickerId.split('_').pop();
            var unassign = false;
            var formulaId = 0;
            
            if (selected.length < 1) {
                unassign = true;
            } else {
                formulaId = selected[0].formulaId;
            }
            
            $.ajax('assignFormulaToAppCat', {
                data : {
                    'appCatId' : appCatId,
                    'formulaId' : formulaId,
                    'unassign' : unassign
                }
            }).done(function (data) {
                $('#formula-picker-row-app-cat_' + appCatId).html(data).flash();
            });
        },
        
        /** This is called when formula picker for gears is closed.
         *  It sends ajax request to set selected formula for a gear.
         *  @param {Object} selected - Selected items array.
         *  @param {Object} picker - Picker object.
         */
        gearFormulaPickerClose : function (selected, picker) {
            
            var gearId = picker.pickerId.split('_').pop();
            var unassign = false;
            var formulaId = 0;
            
            if (selected.length < 1) {
                unassign = true;
            } else {
                formulaId = selected[0].formulaId;
            }
            
            $.ajax('assignFormulaToGear', {
                data : {
                    'gearId' : gearId,
                    'formulaId' : formulaId,
                    'unassign' : unassign
                }
            }).done(function (data) {
                $('#formula-picker-row-gear_' + gearId).html(data).flash();
            });
        }
    };
    
    return mod;
}());

$(function () { yukon.dr.formula.init(); });
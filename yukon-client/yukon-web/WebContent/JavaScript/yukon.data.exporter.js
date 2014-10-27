yukon.namespace('yukon.dataExporter');

/**
 * Singleton that manages the data exporter page.
 * @module yukon.dataExporter
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.dataExporter = (function () {
    
    var mod = {},
        _initialized = false,
        _config = {},
    
	/** Displays the dynamic attributes. */
    _showAttributeRow = function() {
        if ($('#archivedValuesExportFormatType').val() == 'DYNAMIC_ATTRIBUTE') {
            $('#attributeRow').show();
        } else {
            $('#attributeRow').hide();
        }
    },
    
    /** Exports the format. */
    _runOkPressed = function() {
        $('#runDialog').dialog('close');
        $('#runInputsDiv').clone().appendTo($('#exporterForm'));
        _submitForm('generateReport');
        $('#exporterForm #runInputsDiv').remove();
    },
    
    /** Opens the schedule details form. */
    _scheduleOkPressed = function() {
        $('#scheduleDialog').dialog('close');
        $('#scheduleInputsDiv').clone().appendTo($('#exporterForm'));
        _submitForm('scheduleReport');
        $('#exporterForm #scheduleInputsDiv').remove();
    },
    
    /** Handles the OK click of Export/Schedule dialog.
     * @param {Object} dialogIdentifier - id of dialog that needs to be opened.
     * @param {string} titleMsg - Title of dialog.
     * @param {string} okFunction - Function name that needs to be invoked on OK button click.
     **/
    _createOkCancelDialog = function(dialogIdentifier, titleMsg, okFunction) {
        var buttons = [];
        buttons.push({'text' : _config.text.ok, 'class' : 'primary', 'click' : okFunction });
        buttons.push({'text' : _config.text.cancel, 'click' : function() { $(this).dialog('close'); }});
        var dialogOpts = {
                  'title' : titleMsg,
                  'width' : 'auto',
                  'height' : 'auto',
                  'modal' : true,
                  'buttons' : buttons };
        if ($('#archivedValuesExportFormatType').val() == 'FIXED_ATTRIBUTE' && titleMsg == _config.text.schedule) {
            //format=fixed and scheduling, no further user feedback required
            //DataRangeType.END_DATE is the only option
            $(dialogIdentifier).dialog(dialogOpts);
            okFunction();
        } else {
            $(dialogIdentifier).dialog(dialogOpts);
        }
    },
    
    /** Submits the form.
     *  @param {Object} action - action url
     */
    _submitForm = function(action) {
        var exporterForm = $('#exporterForm');
        exporterForm.attr('action', action);
        exporterForm[0].submit();
    },
    
    /** Toggles between export and schedule dialog.
     * @param {Object} dialogId - id of dialog opened.
     * @param {string} archivedValuesExporterFormat - Format in which we want to export data.
     * @param {Array.<string>} dataRangeTypes - Date ranges.
     * @param {Array.<string>} fixedDataRangeTypes - Fixed date range..
     * @param {Array.<string>} dynamicDataRangeTypes - Dynamic date range.
     **/
    _toggleForm = function(dialogId, archivedValuesExporterFormat, dataRangeTypes, fixedDataRangeTypes, dynamicDataRangeTypes) {

        for (var i = 0;  i < dataRangeTypes.length; i++) {
            var dataRangeType = dataRangeTypes[i];
            var dataRangeTypeDiv= dialogId+' .'+dataRangeType;
            var dataRangeTypeInput = dataRangeTypeDiv + ' [name $= \'DataRange.dataRangeType\'] ';

            if (archivedValuesExporterFormat == 'FIXED_ATTRIBUTE' && $.inArray(dataRangeType, fixedDataRangeTypes) != -1 ) {
                $(dataRangeTypeDiv).show();
                $(dataRangeTypeInput).click();
            } else if (archivedValuesExporterFormat == 'DYNAMIC_ATTRIBUTE' && $.inArray(dataRangeType, dynamicDataRangeTypes) != -1) {
                $(dataRangeTypeDiv).show();
                $(dataRangeTypeInput).click();
            } else {
                $(dataRangeTypeDiv).hide();
            }
        }
    };

    mod = {
        
    
        /*
         * Initializes the module hooking up event handlers to components.
         * Depends on localized text in the jsp, so only run after DOM is ready.
         */
        init: function() {
            
            if (_initialized) return;
            
            $('#attribute-select').chosen();
            
            _config = yukon.fromJson('#module-config');
            
            _showAttributeRow();
            
            _toggleForm('#runDialog', $('#archivedValuesExportFormatType').val(), _config.dataRangeTypes, _config.fixedRunDataRangeTypes, _config.dynamicRunDataRangeTypes);
            _toggleForm('#scheduleDialog', $('#archivedValuesExportFormatType').val(), _config.dataRangeTypes, _config.fixedScheduleDataRangeTypes, _config.dynamicScheduleDataRangeTypes);
            
            $('#runButton').click(function(event) {
                _createOkCancelDialog('#runDialog', _config.text.run, function() {
                    _runOkPressed();
                });
            });
            $('#scheduleButton').click(function(event) {
                _createOkCancelDialog('#scheduleDialog', _config.text.schedule, function() {
                    _scheduleOkPressed();
                });
            });
            
            $('#b-edit').click(function(ev) {
                var formatId = $('#formatId').val();
                window.location.href = 'format/' + formatId;
            });
            $('#b-copy').click(function(ev) {
                var formatId = $('#formatId').val();
                window.location.href = 'format/' + formatId + '/copy';
            });
            $('#b-create').click(function(ev) {
                
                var buttons = [{text: _config.text.cancel, click: function() { $(this).dialog('close'); }},
                               {text: _config.text.create, 
                                click: function() {
                                    window.location.href = 'format/create?formatType=' + $('input[name=newFormatType]:checked').val();
                                },
                                'class': 'primary action'}];
                
                $('#create-format-dialog').dialog({
                    'buttons': buttons, 
                    'height': 'auto',
                    'width': 500
                });
            });
            
            $('.selectDevices').click(function(event) {
                _submitForm('selectDevices');
            });
            
            $('#formatId').change(function(event) {
                _toggleForm('#runDialog', $('#archivedValuesExportFormatType').val(), _config.dataRangeTypes, _config.fixedRunDataRangeTypes, _config.dynamicRunDataRangeTypes);
                _toggleForm('#scheduleDialog', $('#archivedValuesExportFormatType').val(), _config.dataRangeTypes, _config.fixedScheduleDataRangeTypes, _config.dynamicScheduleDataRangeTypes);
                _showAttributeRow();
                _submitForm('view');
            });
            
            _initialized = true;
        }
    };
    
    return mod;
}());

$(function() {yukon.dataExporter.init();});
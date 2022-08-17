yukon.namespace('yukon.tools.dataExporter');

/**
 * Singleton that manages the data exporter page.
 * @module yukon.tools.dataExporter
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.tools.dataExporter = (function () {
    
    var mod = {},
        _initialized = false,
        _config = {},
    
	/** Displays the dynamic attributes. */
    _showAttributeRow = function() {
        if ($('#format-type').val() == 'DYNAMIC_ATTRIBUTE') {
            $('#attributeRow').removeClass('dn');
        } else {
            $('#attributeRow').addClass('dn');
        }
    },
    
    /** Exports the format. */
    _runOkPressed = function() {
        $('#run-dialog').dialog('close');
        $('.js-run-inputs').clone().appendTo('#exporter-form');
        _submitForm('generateReport');
        $('#exporter-form').find('.js-run-inputs').remove();
    },
    
    /** Opens the schedule details form. */
    _scheduleOkPressed = function() {
        $('#schedule-dialog').dialog('close');
        $('.js-schedule-inputs').clone().appendTo('#exporter-form');
        _submitForm('scheduleReport');
        $('#exporter-form').find('.js-schedule-inputs').remove();
    },
    
    /** Handles the OK click of Export/Schedule dialog.
     * @param {Object} dialogIdentifier - id of dialog that needs to be opened.
     * @param {string} titleMsg - Title of dialog.
     * @param {string} okFunction - Function name that needs to be invoked on OK button click.
     **/
    _createOkCancelDialog = function(dialog, titleMsg, okFunction) {
        var dialog = $(dialog),
            buttons = [];
        
        buttons.push({'text' : _config.text.cancel, 'click' : function() { $(this).dialog('close'); }});
        buttons.push({'text' : _config.text.ok, 'class' : 'primary', 'click' : okFunction });
        var dialogOpts = {
                  'title' : titleMsg,
                  'width' : 450,
                  'height' : 'auto',
                  'modal' : true,
                  'buttons' : buttons };
        if ($('#format-type').val() == 'FIXED_ATTRIBUTE') {
            dialog.find('.js-dynamic').addClass('dn');
            dialog.find('.js-fixed').removeClass('dn');
            
        } else {
            dialog.find('.js-fixed').addClass('dn');
            dialog.find('.js-dynamic').removeClass('dn');
        }
        dialog.dialog(dialogOpts);
        dialog.find('input[type="radio"]:visible').first().prop('checked', true);
        dialog.find('.js-focus:visible').first().focus();
    },
    
    /** Submits the form.
     *  @param {Object} action - action url
     */
    _submitForm = function(action) {
        var exporterForm = $('#exporter-form');
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
                $(dataRangeTypeDiv).removeClass('dn');
                $(dataRangeTypeInput).click();
            } else if (archivedValuesExporterFormat == 'DYNAMIC_ATTRIBUTE' && $.inArray(dataRangeType, dynamicDataRangeTypes) != -1) {
                $(dataRangeTypeDiv).removeClass('dn');
                $(dataRangeTypeInput).click();
            } else {
                $(dataRangeTypeDiv).addClass('dn');
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
            
            _toggleForm('#run-dialog', $('#format-type').val(), _config.dataRangeTypes, _config.fixedRunDataRangeTypes, _config.dynamicRunDataRangeTypes);
            _toggleForm('#schedule-dialog', $('#format-type').val(), _config.dataRangeTypes, _config.fixedScheduleDataRangeTypes, _config.dynamicScheduleDataRangeTypes);
            
            $('#run-btn').click(function(event) {
                _createOkCancelDialog('#run-dialog', _config.text.run, _runOkPressed);
            });
            $('#schedule-btn').click(function(event) {
                _createOkCancelDialog('#schedule-dialog', _config.text.schedule, _scheduleOkPressed);
            });
            
            $('#b-edit').click(function(ev) {
                window.location.href = 'format/' + $('#format-id').val();
            });
            $('#b-copy').click(function(ev) {
                window.location.href = 'format/' + $('#format-id').val() + '/copy';
            });
            $('#b-create').click(function(ev) {
                
                var buttons = [{text: _config.text.cancel, click: function() { $(this).dialog('close'); }},
                               {text: _config.text.create, 
                                    click: function() {
                                        window.location.href = 'format/create?formatType=' + $('input[name=newFormatType]:checked').val();
                                    },
                                    'class': 'primary action'}
                               ];
                
                $('#create-format-dialog').dialog({
                    'buttons': buttons, 
                    'height': 'auto',
                    'width': 500
                });
            });
            
            $('.selectDevices').click(function(event) {
                _submitForm('selectDevices');
            });
            
            $('.js-time-check').each( function () {
                $(this).closest('.js-dynamic,.js-fixed').find('.js-time').prop('disabled', ! $(this).is(':checked'));
            });
            
            $('.js-time-check').click(function () {
                $(this).closest('.js-dynamic,.js-fixed').find('.js-time').prop('disabled', ! $(this).is(':checked'));
            });
            
            $('#format-id').change(function(event) {
                _toggleForm('#run-dialog', $('#format-type').val(), _config.dataRangeTypes, _config.fixedRunDataRangeTypes, _config.dynamicRunDataRangeTypes);
                _toggleForm('#schedule-dialog', $('#format-type').val(), _config.dataRangeTypes, _config.fixedScheduleDataRangeTypes, _config.dynamicScheduleDataRangeTypes);
                _showAttributeRow();
                _submitForm('view');
            });
            
            _initialized = true;
        }
    };
    
    return mod;
}());

$(function() {yukon.tools.dataExporter.init();});
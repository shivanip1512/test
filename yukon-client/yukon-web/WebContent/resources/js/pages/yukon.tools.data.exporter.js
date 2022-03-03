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
        var selectedIndex = $('.js-run-inputs').find('select.js-intervals').prop('selectedIndex');
        
        $('#run-dialog').dialog('close');
        $('.js-run-inputs').clone().appendTo('#exporter-form');
        // Clone doesn't copy select values, so manually copy.
        $('#exporter-form').find('select.js-intervals').prop('selectedIndex', selectedIndex);
        _submitForm('generateReport');
        $('#exporter-form').find('.js-run-inputs').remove();
    },
    
    /** Opens the schedule details form. */
    _scheduleOkPressed = function() {
        var selectedIndex = $('.js-schedule-inputs').find('select.js-intervals').prop('selectedIndex');
        
        $('#schedule-dialog').dialog('close');
        $('.js-schedule-inputs').clone().appendTo('#exporter-form');
        // Clone doesn't copy select values, so manually copy.
        $('#exporter-form').find('select.js-intervals').prop('selectedIndex', selectedIndex);
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
    },
    
    _displayCreateFormatDialogContent = function (formatOptionRadioButton) {
        if (formatOptionRadioButton.hasClass("js-use-template")) {
            $(".js-create-format-option.js-do-not-use-template").prop("checked", false);
            $.getJSON(yukon.url("/tools/data-export/getAvaliableFormatTemplates"), function (json) {
                $(".js-template-formats-dropdown").find("option").remove();
                if (json.hasOwnProperty('templateFileNames')) {
                    $.each(json.templateFileNames, function (key, val) {
                        $(".js-template-formats-dropdown").append(new Option(val, val));
                    });
                } else {
                    $("#create-format-dialog").find(".user-message").remove();
                    $("#create-format-dialog").addMessage({
                        message: json.errorMessage,
                        messageClass: 'error'
                    }); 
                }
                $(".js-avaliable-template-formats").toggleClass("dn", json.hasOwnProperty('errorMessage'));
            });
        } else {
            $("#create-format-dialog").find(".user-message").remove();
            $(".js-create-format-option.js-use-template").prop("checked", false);
            $(".js-avaliable-template-formats").addClass("dn");
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
                                        var useTemplate = $('.js-use-template').is(':checked'),
                                            fileName = "",
                                            selectedFormat = $('input[name=newFormatType]:checked').exists() ? $('input[name=newFormatType]:checked').val() : '';
                                        if (useTemplate) {
                                            fileName = $(".js-avaliable-template-formats option:selected").val();
                                        }
                                        window.location.href = 'format/create?formatType=' + selectedFormat + '&useTemplate=' + useTemplate + '&fileName=' + fileName;
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
            
            $('.js-time-check').each(function() {
                $(this).closest('.js-dynamic,.js-fixed').find('.js-time').prop('disabled', ! $(this).is(':checked'));
            });
            
            $('.js-time-check').click(function() {
                $(this).closest('.js-dynamic,.js-fixed').find('.js-time').prop('disabled', ! $(this).is(':checked'));
                if ($(this).is(':checked')) {
                    $(this).closest('.js-dynamic,.js-fixed').find('.js-on-interval-check').prop('checked', false);
                    $(this).closest('.js-dynamic,.js-fixed').find('.js-intervals').prop('disabled', true);
                }
            });
            
            $('.js-on-interval-check').each(function() {
                $(this).closest('.js-dynamic,.js-fixed').find('.js-intervals').prop('disabled', ! $(this).is(':checked'));
            });
            
            $('.js-on-interval-check').click(function() {
                $(this).closest('.js-dynamic,.js-fixed').find('.js-intervals').prop('disabled', ! $(this).is(':checked'));
                if ($(this).is(':checked')) {
                    $(this).closest('.js-dynamic,.js-fixed').find('.js-time-check').prop('checked', false);
                    $(this).closest('.js-dynamic,.js-fixed').find('.js-time').prop('disabled', true);
                }
            });
            
            $('#format-id').change(function(event) {
                _toggleForm('#run-dialog', $('#format-type').val(), _config.dataRangeTypes, _config.fixedRunDataRangeTypes, _config.dynamicRunDataRangeTypes);
                _toggleForm('#schedule-dialog', $('#format-type').val(), _config.dataRangeTypes, _config.fixedScheduleDataRangeTypes, _config.dynamicScheduleDataRangeTypes);
                _showAttributeRow();
                _submitForm('view');
            });
            
            $(document).on("click", ".js-create-format-option", function () {
                _displayCreateFormatDialogContent($(this));
            });
            
            $(document).on("click", ".js-template-preview-link", function (event) {
                event.preventDefault();	
                window.open(yukon.url("/tools/data-exporter/format/renderTemplatePreview/" + $(".js-avaliable-template-formats option:selected").val()));
            });
            
            $("#create-format-dialog").on("dialogopen", function (event, ui) {
                _displayCreateFormatDialogContent($(this).find(".js-create-format-option:checked"));
            });

            _initialized = true;
        }
    };
    
    return mod;
}());

$(function() {yukon.tools.dataExporter.init();});
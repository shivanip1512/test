yukon.namespace('yukon.tools.dataExporterFormat');

/**
 * Module that manages the data exporter format page.
 * @module yukon.tools.dataExporterFormat
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.ui
 */
yukon.tools.dataExporterFormat = (function () {
    
    var
    _initialized = false,
    
    /** Configuration object containing any data this module needs when initializing (i18n text) */
    _config = {},
    
    /** @constant {string} - URL for attribute popup retrieval and posting. */
    _attributeUrl = yukon.url('/tools/data-exporter/format/attribute'),
    
    /** @constant {string} - URL for field popup retrieval and posting. */
    _fieldUrl = yukon.url('/tools/data-exporter/format/field'),
    
    /** @constant {string} - URL that returns the preview for the format in JSON. */
    _previewUrl = yukon.url('/tools/data-exporter/format/preview'), 
    
    /** Retrieves a preview of the format */
    _updatePreview = function () {
        $('#format-form').ajaxSubmit({
            url: _previewUrl, 
            type: 'post',
            success: function (data, status, xhr, $form) {
                $('[preview-body]').empty();
                $.each(data.body, function (idx, line) {
                    $('[preview-body]').append($('<div>').text(line));
                });
            }
        });
    },
    
    /** Gets the timestamp pattern selected or the custom timestamp pattern if defined. */
    _getTimestampPattern = function () {
        var pattern = $('#timestamp-pattern-select option:selected').data('pattern');
        if (pattern === 'CUSTOM') pattern = $('#timestamp-pattern-input').val();
        return pattern;
    },
    
    /** Gets the reading pattern selected or the custom reading pattern if defined. */
    _getReadingPattern = function () {
        var pattern = $('#reading-pattern-select option:selected').data('pattern');
        if (pattern === 'CUSTOM') pattern = $('#reading-pattern-input').val();
        return pattern;
    },
    
    /** 
     * Returns an object of extra post data needed to post a field 
     * using the ajaxSubmit method on the jquery.form plugin. 
     */
    _getExtraFieldRequestData = function () {
        
        var data = {formatType: $('#format-type').val()};
        // The current attributes will be needed in the response if validation fails.
        $('#attributes-table tbody :input').each(function () {
            data[$(this).attr('name')] = $(this).val();
        });
        
        return data;
    },
    
    mod = {
        
        /**
         * Initializes the module, hooking up event handlers to components.
         * Depends on localized text in the jsp, so only run after DOM is ready.
         */
        init: function () {
            
            if (_initialized) return;
            
            _config = yukon.fromJson('#module-config');
            
            var delimiters = $('#delimiters'),
                delimiter = $('#delimiter'),
                showField = delimiters.find('option:selected').is('[type=CUSTOM]'),
                lastHeaderValue = $('.js-header').val(),
                lastFooterValue = $('.js-footer').val();
            /** Hide delimiter text field initially if it's not custom. */
            if (showField) delimiter.show(); else delimiter.hide();
            
            /** Show the delimiter text field if they choose custom, otherwise hide. */
            $('#delimiters').change(function (ev) {
                
                var showField = delimiters.find('option:selected').is('[type=CUSTOM]');
                delimiter.val(delimiters.val());
                if (showField) delimiter.show(); else delimiter.hide();
                _updatePreview();
            });
            
            /** Update preview if they change the delimiter */
            delimiter.on('input', function (ev) { _updatePreview(); });
            
            /** Update preview if they change the timezone */
            $('#date-timezone-format').on('change', function (ev) { _updatePreview(); });
            
            /** Add attribute button clicked, shows add attribute popup. */
            $('#b-add-attribute').click(function (ev) {
                
                var popup = $('#format-popup');
                
                popup.load(_attributeUrl, function () {
                    popup.dialog({
                        title: _config.text.addAttribute,
                        width: '450px',
                        classes: {
                            "ui-dialog": 'ov'
                        },
                        modal: true,
                        buttons: yukon.ui.buttons({event: 'yukon.data.export.format.attribute.add', target: popup})
                    });
                });
            });
            
            /** Add field button clicked, shows add field popup. */
            $('#b-add-field').click(function (ev) {
                
                var popup = $('#format-popup');
                
                $.get(_fieldUrl, _getExtraFieldRequestData()).done(function (data) {
                    popup.html(data);
                    popup.dialog({
                        title: _config.text.addField,
                        width: 'auto',
                        modal: true,
                        buttons: yukon.ui.buttons({event: 'yukon.data.export.format.field.add', target: popup})
                    });
                });
                
            });
            
            /** 
             * OK button on the add attribute popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and updates row if validation succeded. 
             */
            $('#format-popup').on('yukon.data.export.format.attribute.add', function (ev) {
                
                $('#attribute-form').ajaxSubmit({
                    url: _attributeUrl, 
                    type: 'post',
                    success: function (data, status, xhr, $form) {
                        
                        $('#format-popup').dialog('close');
                        
                        var row = $('#attribute-template tr').clone(),
                            attribute = row.find('td:first-child'),
                            dataSelection = attribute.next(),
                            daysPrevious = dataSelection.next();
                        
                        attribute.find('input').val(JSON.stringify(data.attribute));
                        attribute.append('<span>' + data.text.attribute + '</span>');
                        dataSelection.append('<span>' + data.text.dataSelection + '</span>');
                        daysPrevious.append('<span>' + data.text.daysPrevious + '</span>');
                        
                        $('#attributes-table tbody').append(row);
                        yukon.ui.reindexInputs(row.closest('table'));
                    },
                    error: function (xhr, status, error, $form) {
                        $('#format-popup').html(xhr.responseText);
                    }
                });
            });
            
            /** 
             * OK button on the add field popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and updates row if validation succeded. 
             */
            $('#format-popup').on('yukon.data.export.format.field.add', function (ev) {

                $('#field-form').ajaxSubmit({
                    url: _fieldUrl, 
                    type: 'post',
                    data: _getExtraFieldRequestData(),
                    success: function (data, status, xhr, $form) {
                        
                        $('#format-popup').dialog('close');
                        
                        var row = $('#field-template tr').clone(),
                            field = row.find('td:first-child'),
                            attributeField = field.next(),
                            dataSelection = attributeField.next(),
                            daysPrevious = dataSelection.next(),
                            missingValue = daysPrevious.next(),
                            rounding = missingValue.next(),
                            pattern = rounding.next(),
                            fieldSize = pattern.next(),
                            padding = fieldSize.next();
                        
                        field.find('input').val(JSON.stringify(data.exportField));
                        field.append('<span>' + data.text.exportField + '</span>');
                        attributeField.append('<span>' + data.text.attributeField + '</span>');
                        dataSelection.append('<span>' + data.text.dataSelection + '</span>');
                        daysPrevious.append('<span>' + data.text.daysPrevious + '</span>');
                        missingValue.append('<span>' + data.text.missingAttribute + '</span>');
                        rounding.append('<span>' + data.text.roundingMode + '</span>');
                        pattern.append('<span style="max-width: 200px;" class="db wrbw wsn">').find('span').text(data.text.pattern);
                        fieldSize.append('<span>' + data.text.maxLength + '</span>');
                        padding.append('<span>' + data.text.padding + '</span>');
                        
                        $('#fields-table tbody').append(row);
                        yukon.ui.reindexInputs(row.closest('table'));
                        _updatePreview();
                    },
                    error: function (xhr, status, error, $form) {
                        $('#format-popup').html(xhr.responseText);
                    }
                });
            });
            
            /** Edit attribute button clicked, show edit attribute popup. */
            $(document).on('click', '#attributes-table .js-edit', function (ev) {
                
                var row = $(this).closest('tr'),
                    attribute = JSON.parse(row.find('td:first-child input').val()),
                    popup = $('#format-popup');
                
                popup.load(_attributeUrl, function () {
                    popup.find('select[name=attribute]').addClass("dn");
                    popup.find('select[name=attribute]').val(attribute.attribute).trigger("chosen:updated");
                    popup.find('select[name=attribute]').removeClass("dn");
                    popup.find('select[name=dataSelection]').val(attribute.dataSelection);
                    popup.find('input[name=daysPrevious]').val(attribute.daysPrevious);
                    popup.dialog({
                        title: _config.text.editAttribute,
                        width: '450px',
                        classes: {
                            "ui-dialog": 'ov'
                        },
                        modal: true,
                        buttons: yukon.ui.buttons({event: 'yukon.data.export.format.attribute.edit', target: row})
                    });
                });
            });
            
            /** Edit field button clicked, show edit field popup. */
            $(document).on('click', '#fields-table .js-edit', function (ev) {
                
                var 
                row = $(this).closest('tr'),
                popup = $('#format-popup'),
                data = _getExtraFieldRequestData();
                
                data.exportFieldJson = row.find('td:first-child input').val();
            
                // send attributes and format type in request
                $.get(_fieldUrl, data)
                .done(function (data) {
                    
                    popup.html(data);
                    popup.dialog({
                        title: _config.text.editField,
                        width: 'auto',
                        modal: true,
                        buttons: yukon.ui.buttons({event: 'yukon.data.export.format.field.edit', target: row})
                    });
                });
                
            });
            
            /** 
             * OK button on the edit attribute popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and updates row if validation succeded. 
             */
            $(document).on('yukon.data.export.format.attribute.edit', function (ev) {
                
                var row = $(ev.target),
                    attribute = row.find('td:first-child'),
                    originalValue = JSON.parse(attribute.find('input').val()),
                    dataSelection = attribute.next(),
                    daysPrevious = dataSelection.next();
                
                $('#attribute-form').ajaxSubmit({
                    url: _attributeUrl, 
                    type: 'post',
                    success: function (data, status, xhr, $form) {
                        
                        $('#format-popup').dialog('close');
                        
                        // update the row with the new values
                        attribute.find('input').val(JSON.stringify(data.attribute));
                        attribute.find('span').text(data.text.attribute);
                        dataSelection.find('span').text(data.text.dataSelection);
                        daysPrevious.find('span').text(data.text.daysPrevious);
                        
                        // update any fields using this attribute
                        $('#fields-table tbody td:first-child').each(function (idx, el) {
                            var td = $(el),
                                field = JSON.parse(td.find('input').val()), 
                                fieldAttribute = field.field.attribute;
                            
                            if (JSON.stringify(fieldAttribute) == JSON.stringify(originalValue)) {
                                field.field.attribute = data.attribute;
                                td.find('input').val(JSON.stringify(field));
                                td.find('span').text(data.text.attribute);
                                td.next().next().find('span').text(data.text.dataSelection);
                                td.next().next().next().find('span').text(data.text.daysPrevious);
                            }
                        });
                    },
                    error: function (xhr, status, error, $form) {
                        // show the validated form
                        $('#format-popup').html(xhr.responseText);
                    }
                });
            });
            
            /** 
             * OK button on the edit field popup clicked.
             * Submits form via ajax and shows validated form if validation failed
             * or closes popup and updates row if validation succeded.
             */
            $(document).on('yukon.data.export.format.field.edit', function (ev) {
                
                var row = $(ev.target);
                field = row.find('td:first-child'),
                attributeField = field.next(),
                dataSelection = attributeField.next(),
                daysPrevious = dataSelection.next(),
                missingValue = daysPrevious.next(),
                roudingMode = missingValue.next(),
                pattern = roudingMode.next(),
                maxLength = pattern.next(),
                padding = maxLength.next();
                
                $('#field-form').ajaxSubmit({
                    url: _fieldUrl, 
                    type: 'post',
                    data: _getExtraFieldRequestData(),
                    success: function (data, status, xhr, $form) {
                        
                        $('#format-popup').dialog('close');
                        
                        // Update the row with the new values
                        field.find('input').val(JSON.stringify(data.exportField));
                        field.find('span').text(data.text.exportField);
                        attributeField.find('span').text(data.text.attributeField);
                        dataSelection.find('span').text(data.text.dataSelection);
                        daysPrevious.find('span').text(data.text.daysPrevious);
                        missingValue.find('span').text(data.text.missingAttribute);
                        roudingMode.find('span').text(data.text.roundingMode);
                        pattern.find('span').text(data.text.pattern);
                        maxLength.find('span').text(data.text.maxLength);
                        padding.find('span').text(data.text.padding);
                        
                        _updatePreview();
                    },
                    error: function (xhr, status, error, $form) {
                        // show the validated form
                        $('#format-popup').html(xhr.responseText);
                    }
                });
            });
            
            // ADD/EDIT FIELD POPUP EVENT BINDING
            /**
             * When field select in add/edit field popup changes, adjust other elements visibility. 
             * Set pattern input value if field is 'attribute' type; either timestamp or value pattern.
             */
            $(document).on('change', '#field-select', function (ev) {
                
                var
                field = JSON.parse($(this).val()),
                type = field.type,
                attributeSelect = $('#attribute-field'),
                attrVal = attributeSelect.val(),
                fieldSize = $('#field-size'),
                padding = $('#padding'),
                timestampPattern = $('#timestamp-pattern'),
                readingPattern = $('#reading-pattern'),
                roundingMode = $('#rounding-mode'),
                pattern = $('#pattern'),
                plainText = $('#plain-text'),
                otherOptions = $('#other-options');
                
                if (type !== 'PLAIN_TEXT') {
                    fieldSize.show();
                    padding.show();
                    plainText.hide();
                    if (type == 'DEVICE_TYPE') {
                        otherOptions.hide();
                    } else {
                        otherOptions.show();
                    }
                }
                
                // show hide reading/timestamp patterns
                if (type === 'ATTRIBUTE' || type === 'POINT_TIMESTAMP' || type === 'POINT_VALUE' || type == 'RUNTIME') {
                    if (type === 'ATTRIBUTE') {
                        attributeSelect.show();
                        attributeSelect.find("option:selected").removeAttr('selected');
                        attributeSelect.find("option[value='POINT_STATE']").toggleClass('dn', !field.attribute.statusType);
                    }
                    if (type == 'RUNTIME') {
                        otherOptions.hide();
                    } else {
                        otherOptions.show();
                    }
                    
                    if ((type === 'ATTRIBUTE' && attrVal === 'TIMESTAMP') || type === 'POINT_TIMESTAMP' || type === 'RUNTIME') {
                        pattern.val(_getTimestampPattern());
                        timestampPattern.show();
                        readingPattern.hide();
                        roundingMode.hide();
                    } else if ((type === 'ATTRIBUTE' && attrVal === 'VALUE') || type === 'POINT_VALUE') {
                        pattern.val(_getReadingPattern());
                        timestampPattern.hide();
                        readingPattern.show();
                        roundingMode.show();
                    } else {
                        timestampPattern.hide();
                        readingPattern.hide();
                        roundingMode.hide();
                    }
                } else {
                    // Not an attribute or point value/timestamp, hide pattern/rounding fields.
                    attributeSelect.hide();
                    timestampPattern.hide();
                    readingPattern.hide();
                    roundingMode.hide();
                    
                    if (type === 'PLAIN_TEXT') {
                        plainText.show();
                        otherOptions.hide();
                        fieldSize.hide();
                        padding.hide();
                        pattern.val(plainText.find('input').val());
                    }
                }
            });
            
            /**
             * Attribute value changed, show timestamp or value fields or hide both
             * if selection is not timestamp or value.
             */
            $(document).on('change', '#attribute-field', function (ev) {
                var val = $(this).val(),
                    pattern = $('#pattern'),
                    timestampPattern = $('#timestamp-pattern'),
                    readingPattern = $('#reading-pattern'),
                    roundingMode = $('#rounding-mode');
                
                if (val === 'TIMESTAMP') {
                    timestampPattern.show();
                    readingPattern.hide();
                    roundingMode.hide();
                    pattern.val(_getTimestampPattern());
                } else if (val === 'VALUE') {
                    timestampPattern.hide();
                    readingPattern.show();
                    roundingMode.show();
                    pattern.val(_getReadingPattern());
                } else {
                    timestampPattern.hide();
                    readingPattern.hide();
                    roundingMode.hide();
                }
            });
            
            /** Show custom textfield if they choose custom, hide otherwise for timestamp and value. */
            $(document).on('change', '#timestamp-pattern-select, #reading-pattern-select', function (ev) {
                var val = $(this).val(),
                    customField = $(this).next(),
                    pattern = $('#pattern');
                
                if (val === 'CUSTOM') {
                    customField.show();
                    pattern.val(customField.val());
                } else {
                    customField.hide();
                    pattern.val($(this).find('option:selected').data('pattern'));
                }
            });
            
            /** Show padding fields when padding option is not 'None'. */
            $(document).on('change', '#pad-side-select', function (ev) {
                var notNone = $(this).val() !== 'NONE',
                    padCharSelect = $('#pad-char-select'),
                    padCharInput = padCharSelect.next(),
                    padCharValue = padCharSelect.val();
                
                $('#pad-char-fields').toggle(notNone);
                if (notNone) {
                    if (padCharValue === 'CUSTOM') {
                        padCharInput.val('').focus();
                    } else {
                        padCharInput.val(padCharValue);
                    }
                }
            });
            
            /** Show custom padding field when padding field is 'Custom'. */
            $(document).on('change', '#pad-char-select', function (ev) {
                var val = $(this).val(),
                    isCustom = val === 'CUSTOM',
                    padChar = $('#pad-char-input');
                
                padChar.toggle(isCustom);
                if (isCustom) {
                    padChar.val('');
                    padChar.focus();
                } else {
                    padChar.val(val);
                }
            });
            
            /** Show fixed value field when 'Fixed Value' is selected for 'Unsupported Field'. */
            $(document).on('change', '#unsupported-field-select', function (ev) {
                $('#fixed-value').toggle($(this).val() === 'FIXED_VALUE');
            });
            
            /** Update pattern field when custom timestamp pattern, custom reading pattern or plain text changes. */
            $(document).on('input', '#timestamp-pattern-input, #reading-pattern-input, #plain-text-input', function (ev) { $('#pattern').val($(this).val()); });
            
            /** Move row up. */
            $(document).on('click','.js-up', function (ev) {
                var row = $(this).closest('tr'),
                    prevRow = row.prev();
                
                row.insertBefore(prevRow);
                yukon.ui.reindexInputs(row.closest('table'));
                _updatePreview();
            });
            
            /** Move row down. */
            $(document).on('click','.js-down', function (ev) {
                var row = $(this).closest('tr'),
                    nextRow = row.next();
                
                row.insertAfter(nextRow);
                yukon.ui.reindexInputs(row.closest('table'));
                _updatePreview();
            });
            
            /** Remove attribute button clicked, remove row and re-index the rest. Update any fields necessary */
            $(document).on('click', '#attributes-table .js-remove', function (ev) {
                var row = $(this).closest('tr'),
                    table = row.closest('table'),
                    attribute = row.find('td:first-child'),
                    originalValue = JSON.parse(attribute.find('input').val());
                row.remove();
                
                // update any fields using this attribute
                $('#fields-table tbody td:first-child').each(function (idx, el) {
                    var td = $(el),
                        field = JSON.parse(td.find('input').val()), 
                        fieldAttribute = field.field.attribute;
                    
                    if (JSON.stringify(fieldAttribute) == JSON.stringify(originalValue)) {
                        // trigger remove on any field rows using the attribute we just removed
                        td.closest('tr').find('.js-remove').trigger('click');
                    }
                });
                
                yukon.ui.reindexInputs(table);
                _updatePreview();
            });
            
            /** Remove field button clicked, remove row and re-index the rest. */
            $(document).on('click', '#fields-table .js-remove', function (ev) {
                var row = $(this).closest('tr'),
                table = row.closest('table');
                row.remove();
                yukon.ui.reindexInputs(table);
                _updatePreview();
            });
            
            /** Update the preview when changing the header and footer fields. */
            $(".js-header").on('input', function () {
                if ($(this).val() != lastHeaderValue) {
                    lastHeaderValue = $(this).val();
                    $('[preview-header]').text(lastHeaderValue);
                }
            });
            $(".js-footer").on('input', function () {
                if ($(this).val() != lastFooterValue) {
                    lastFooterValue = $(this).val();
                    $('[preview-footer]').text(lastFooterValue);
                }
            });
            
            _initialized = true;
        }
    };
    
    return mod;
}());

$(function () { yukon.tools.dataExporterFormat.init(); });
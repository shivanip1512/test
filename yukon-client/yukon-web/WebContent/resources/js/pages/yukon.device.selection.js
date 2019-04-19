yukon.namespace('yukon.device.selection');

/**
 * Manages the deviceCollectionPicker.tag file.
 * @requires JQUERY
 * @requires JQUERY_FILE_UPLOAD
 * @requires JQUERY_IFRAME_TRANSPORT
 * @requires JQUERY_TREE
 * @requires JQUERY_TREE_HELPERS
 * @requires yukon
 * @requires yukon.picker
 */
yukon.device.selection = (function () {
    
    'use strict';
    
    var
    _initialized = false,
    
    /** Initialize the device group tab using yukon's dynatree helper module. */
    _initDeviceGroupTab = function (container) {
        
        var tab = container.find('[data-select-by="group"]'),
            groups = tab.data('groups'),
            groupsUrl = tab.data('groupsUrl'),
            multi = tab.is('[data-multi]'),
            initialized = tab.is('.js-initialized');
        
        if (!initialized) {
            
            yukon.dynatree.build({
                container: tab,
                groups: groups,
                groupDataUrl: groupsUrl,
                multi: multi,
                nodeId: 'groupName'
            }, {
                onSelect: function(selected, node) {
                    var groups = node.tree.getSelectedNodes(false)
                    .map(function (item) { return item.data.metadata['groupName']; });
                    
                    $(node.span).closest('[data-select-by="group"]').find('[data-group-names]').data('groupNames', groups);
                }
            });
            
            // Dump this massive attribute since we don't need it anymore.
            tab.removeAttr('data-groups').addClass('js-initialized');
        }
        
    },
    
    /** Initialize the file upload tab using jquery fileupload plugin. */
    _initFileUploadTab = function (container) {
        
        var fakeForm = container.find('[data-form]'),
            fileInput = fakeForm.find(':input[type="file"]'),
            fileType = container.find(':input[name="fileUpload.uploadType"]'),
            dataFile = container.find(':input[name="fileUpload.dataFile"]'),
            progressBar = fakeForm.find('.progress'),
            options = {
                url: yukon.url('/group/device-group'),
                //Need this for IE9
                dataType: 'text',
                replaceFileInput: false,
                add: function (e, data) {
                    
                    var copy = $.extend(true, {}, data);
                    
                    data.submit();
                    data = copy;
                    
                    fileType.off('change');
                    
                    fileType.on('change', function () {
                        copy = $.extend(true, {}, data);
                        data.submit();
                        data = copy;
                    });
                    
                    dataFile .on('change', function () {
                        copy = $.extend(true, {}, data);
                        data.submit();
                        data = copy;
                    });
                },
                done: function (e, data) {
                    
                    var result = JSON.parse(data.result),
                        successContainer = container.find('.js-upload-results'),
                        errorContainer = container.find('.js-upload-errors');
                    
                    progressBar.hide();
                    
                    if (!result.error) {
                        errorContainer.hide();
                        successContainer.find('.device-count').text(result.deviceCount);
                        successContainer.find('.js-error-count').text(result.deviceErrorCount);
                        if (result.deviceErrorCount > 0) {
                            document.getElementById("download").disabled = false;
                        } else {
                            document.getElementById("download").disabled = true;
                        }
                        successContainer.find(':input[name="group.name"]').val(result['group.name']);
                        successContainer.find(':input[name="deviceErrors"]').val(result['deviceErrors']);
                        successContainer.find(':input[name="uploadFileName"]').val(result['group.description']);
                        successContainer.show();
                    } else {
                        successContainer.hide();
                        errorContainer.text(result.error);
                        errorContainer.show();
                    }
                },
                
                progressall: function (e, data) {
                    var progress = parseInt(data.loaded / data.total * 100, 10);
                    progressBar.show().find('.progress-bar').css('width', progress + '%');
                }
            };
        
        fileInput.fileupload(options);
        
        fileInput.bind('fileuploadsubmit', function (e, data) {
            data.formData = {
                'com.cannontech.yukon.request.csrf.token': fakeForm.data('csrfToken'),
                'collectionType': 'fileUpload',
                'isFileUpload': true,
                'fileUpload.uploadType' : fileType.val()
            };
        });
    },
    
    _selectDevices = function (container) {
        
        var destination = container.find('[data-ids]'),
            picker = window[container.data('idPicker')],
            devices = picker.selectedItems,
            ids = [],
            i;
        
        for (i = 0; i < devices.length; i += 1) {
            ids[i] = devices[i].paoId;
        }
        
        destination.val(ids);
        
        return devices.length !== 0;
    },
    
    _validateFileUpload = function (container) {
        return container.find('.js-upload-results').is(':visible');
    },
    
    /** Validate address range selection. */
    _validateAddressRange = function (container) {
        
        var startInput = container.find(':input[name="addressRange.start"]'),
            endInput = container.find(':input[name="addressRange.end"]'),
            start = parseInt(startInput.val(), 10),
            end = parseInt(endInput.val(), 10),
            //Address is represented internally as a 32-bit integer
            maxAddress = 2147483648,
            success = true,
            rangeErrors = container.find('.range-errors');
        
        rangeErrors.children().hide();
        startInput.removeClass('error');
        endInput.removeClass('error');
        
        if (start !== start) {
            rangeErrors.find('.js-undefined-start').show();
            startInput.addClass('error');
            success = false;
        }
        if (end !== end) {
            rangeErrors.find('.js-undefined-end').show();
            endInput.addClass('error');
            success = false;
        }
        if (start < 0) {
            rangeErrors.find('.js-invalid-start').show();
            startInput.addClass('error');
            success = false;
        }
        if (end > maxAddress) {
            rangeErrors.find('.js-invalid-end').show();
            endInput.addClass('error');
            success = false;
        }
        if (start > end) {
            rangeErrors.find('.js-invalid-range').show();
            startInput.addClass('error');
            endInput.addClass('error');
            success = false;
        }
        
        return success;
    },
    
    /** Validate device group selection. */
    _validateGroup = function (container) {
        
        var groupName = container.find('[data-select-by="group"]').find('[data-group-names]').data('groupNames');
        return groupName.length > 0;
    },
    
    /** Validate the selection based on visible tab. */
    _validate = function (container) {
        
        var source = container.find('[data-select-by]:visible'),
            selectBy = source.data('selectBy');
        
        if (selectBy === 'device') {
            return _selectDevices(container);
        }
        if (selectBy === 'group') {
            return _validateGroup(container);
        }
        if (selectBy === 'address') {
            return _validateAddressRange(container);
        }
        if (selectBy === 'file') {
            return _validateFileUpload(container);
        }
    };
    
    /** Exported Module Object */
    return {
        
        init: function () {
            
            if (_initialized) return;
            
            /** Dialog loaded, fired right before dialog is shown */
            $(document).on('yukon:device:selection:load', function (ev) {
                
                var container = $(ev.target),
                    inlinePicker = window[container.data('idPicker')];
                
                inlinePicker.show(true);
                _initFileUploadTab(container);
                _initDeviceGroupTab(container);
            });
            
            /** OK button was clicked, check validation and perform selection. */
            $(document).on('yukon:device:selection:made', function (ev) {
                
                var dialog = $(ev.target),
                    destination = dialog.data('trigger'),
                    picker = window[dialog.data('idPicker')],
                    count = destination.find('.collection-picker-count'),
                    label = destination.find('.collection-picker-label'),
                    selection = destination.data('type') == 'button-selection',
                    source = dialog.find('[data-select-by]:visible');
                
                if (!_validate(dialog)) {
                    return;
                }
                
                destination.find('.js-device-collection-inputs').remove();
                
                source.find('.js-device-collection-inputs').each(function (index, elem) {
                    $(elem).clone().hide().appendTo(destination);
                });
                
                if (source.is('[data-select-by="group"]')) {
                    var groups = source.find('[data-group-names]').data('groupNames');
                    groups.forEach(function (group) {
                        $('<input type="hidden" name="groups.name">')
                        .addClass('js-device-collection-inputs')
                        .val(group)
                        .appendTo(destination);
                    });
                    
                    if (selection) {
                        if (groups.length == 1) {
                            count.hide();
                            label.text(label.data('group').replace('{0}', groups[0]));
                        } else {
                            count.text(groups.length).show();
                            label.text(label.data('groups'));
                        }
                    }
                } else if (source.is('[data-select-by="device"]') && selection) {
                    count.text(picker.selectedItems.length).show();
                    label.text(label.data('devices'));
                } else if (source.is('[data-select-by="address"]') && selection) {
                    count.hide();
                    var from = source.find('[name="addressRange.start"]').val();
                    var to = source.find('[name="addressRange.end"]').val();
                    label.text(label.data('address').replace('{0}', from).replace('{1}', to));
                } else if (source.is('[data-select-by="file"]') && selection) {
                    count.hide();
                    var fileName = source.find('.file-name').text();
                    label.text(label.data('file').replace('{1}', fileName));
                }
                
                dialog.dialog('close');
                
                if (destination.is('[data-event]')) {
                    destination.trigger(destination.data('event'));
                }
                if (destination.is('[data-submit]')) {
                    destination.closest('form').submit();
                } 
            });
            
            /** Adjust the tree max height when dialog is resized or shown to avoid double scrollbars. */
            $(document).on('dialogresize dialogopen', '.js-device-collection-picker-dialog', function (ev, ui) {
                yukon.dynatree.adjustMaxHeight($(this).find('[data-select-by="group"]'));
            });
            
            /** Adjust the tree max height when the device group tab is clicked. */
            $(document).on('click', '.js-device-collection-picker .js-group-tab', function (ev, ui) {
                var container = $(this).closest('.js-device-collection-picker')
                .find('[data-select-by="group"]');
                yukon.dynatree.adjustMaxHeight(container);
            });
            
            /** Connect the dialog to the trigger when collection picker is opened. */
            $(document).on('click', '.collection-picker', function (ev) {
                var trigger = $(this),
                    dialog = $(trigger.data('popup'));
                
                dialog.data('trigger', trigger);
            });
            
            $(document).on('click', '.js-error-devices-download', function () {
                var downloadForm = $('<form>');
                downloadForm.attr('action', yukon.url('/group/downloadResult'));
                downloadForm.attr('method', 'GET');
                downloadForm.append($('.js-error-devices-input').clone());
                downloadForm.appendTo("body").submit();
                downloadForm.remove();
            });
        }
        
    };
    
}());


$(function () { yukon.device.selection.init(); });
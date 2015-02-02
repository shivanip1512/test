yukon.namespace('yukon.device.selection');
/**
 * Singleton that manages Device selection
 * @requires JQUERY
 * @requires JQUERY_FILE_UPLOAD
 * @requires JQUERY_IFRAME_TRANSPORT
 * @requires yukon.picker.js
 */

yukon.device.selection = (function () {

    var _initFileUpload = function (container) {
            var fakeForm = container.find('[data-form]'),
                fileInput = fakeForm.find(':input[type="file"]'),
                fileType = container.find(':input[name="fileUpload.uploadType"]'),
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
                    },
                    done: function (e, data) {
                        var result = JSON.parse(data.result),
                            successContainer = container.find('.js-upload-results'),
                            errorContainer = container.find('.js-upload-errors');

                        progressBar.hide();

                        if (!result.error) {
                            errorContainer.hide();
                            successContainer.find('.device-count').text(result.deviceCount);
                            successContainer.find(':input[name="group.name"]').val(result['group.name']);
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

        _validateFileUpload = function (container) {
            return container.find('.js-upload-results').is(':visible');
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

        _validateGroup = function (container) {
            var groupName = container.find('[data-select-by="group"]').find('[data-group-name]').val();
            return groupName.length > 0;
        },

        _validate = function (container) {
            var inputSource = container.find('[data-select-by]:visible'),
                selectBy = inputSource.data('selectBy');

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
        },

        mod = {};

    mod = {

        init: function () {
            $(document).on('yukon.device.selection.start', function (ev) {
                var container = $(ev.target),
                    inlinePicker = window[container.data('idPicker')];

                inlinePicker.show(true);
                _initFileUpload(container);

                container.find('.tabbed-container.f-init').tabs().show();
            });

            $(document).on('yukon.device.selection.end', function (ev) {
                var container = $(ev.target),
                    inputDestination = container.data('collectionInputs'),
                    inputSource = container.find('[data-select-by]:visible');

                if (!_validate(container)) {
                    return;
                }

                inputDestination.find('.js-device-inputs').remove();

                inputSource.find('.js-device-inputs').each(function (index, elem) {
                    $(elem).clone().hide().appendTo(inputDestination);
                });

                container.dialog('close');
                if (inputDestination.is('[data-submit-on-completion]')) {
                    inputDestination.closest('form').submit();
                }
            });

            $(document).on('click', '[data-device-collection]', function (ev) {
                var link = $(this),
                    linkContainer = link.closest('.js-device-collection'),
                    popup = $(link.data('popup'));

                popup.data('collectionInputs', linkContainer);
            });
        },

        selectGroup: function (node) {
            var span = $(node.span),
                groupName = node.data.metadata.groupName;
            span.closest('[data-select-by="group"]').find('[data-group-name]').val(groupName);
        }
    };
    return mod;
}());

$(function () {
    yukon.device.selection.init();
});
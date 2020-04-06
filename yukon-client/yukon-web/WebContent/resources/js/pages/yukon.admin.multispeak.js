yukon.namespace('yukon.admin.multispeak');

/**
 * Module that manages the Yukon Setup and Vendor Setup tabs for multispeak under Admin menu.
 * @module yukon.admin.multispeak
 * @requires JQUERY
 * @requires JQUERY UI
 * @requires yukon
 * @requires yukon.ui
 */
yukon.admin.multispeak = (function() {
    'use strict';
    var _initialized = false;
    mod = {
        enableEndpointValue: function(selected, version, mspInterface) {
            if ($('#endpointURL_' + version + "_" + mspInterface).val() != null) {
                $('#endpointURL_' + version + "_" + mspInterface).prop('disabled', !selected);
            }
            if ($('#select' + mspInterface) != null) {
                $('#select' + mspInterface).prop('disabled', !selected);
            }
            if ($('#ping' + mspInterface) != null) {
                $('#ping' + mspInterface).prop('disabled', !selected);
            }
            if ($('#getMethods' + mspInterface) != null) {
                $('#getMethods' + mspInterface).prop('disabled', !selected);
            }
            if($('#interfaceAuth'+mspInterface) != null){
                $('#interfaceAuth'+mspInterface).prop('disabled', !selected);
            }
        },

        enableExtension: function(selected) {

            document.getElementById("mspPaoNameAliasExtension").disabled = !selected;
        },

        executeRequest: function(service, call, version, mode) {
            $('#actionService').val(service);
            var endpointURL = $("#endpointURL_" + version + "_" + service).text();
            if (typeof endpointURL === "undefined" || endpointURL === null || endpointURL === "") {
                endpointURL = $("#endpointURL_" + version + "_" + service).val();
            }
            $('#endpointURL').val(endpointURL);
            var formData = $('#mspForm').serialize();
            $.ajax({
                url: yukon.url('/multispeak/setup/' + call + '/' + version),
                type: 'post',
                data: formData
            }).done(function(data) {
                $('#results').css('color', data.resultColor);
                $('#results').val(data.MSP_RESULT_MSG);
            }).fail(function(data) {
                $('#start-simulator').removeAttr("disabled");
                if (data.hasError) {
                    $('#taskStatusMessage').addMessage({
                        message: data.errorMessage,
                        messageClass: 'error'
                    }).show();
                } else {
                    $('#taskStatusMessage').hide();
                }
            });
        },

        vendorChanged: function() {
            document.mspForm.action = yukon.url("/multispeak/setup/vendorHome/" + $('#mspVendorId').val());
            document.mspForm.submit();
        },

        showHideData: function(id, showData) {
            $('#' + id).attr('type', showData ? 'text' : 'password');
        },

        /**
         * Initializes the module, hooking up event handlers to components.
         * Depends on localized text in the jsp, so only run after DOM is ready.
         */
        init: function() {
            $(document).on('click', '.js-eye-icon', function() {
                var targetRow = $(this).closest('.switch-btn');
                var id = targetRow.find('.switch-btn-checkbox').attr('id');
                var isSelected = targetRow.find('.switch-btn-checkbox').prop('checked');
                yukon.admin.multispeak.showHideData(id, !isSelected);
            });
            $('#vendor-edit').click(function(ev) {
                document.mspForm.action = yukon.url("/multispeak/setup/editVendorSetup/" + $('#mspVendorId').val());
                document.mspForm.submit();
            });

            $(document).on('yukon:multispeak:vendor:delete', function() {
                $('#delete-vendor').submit();
            });
            $(document).on("click", ".js-endpoint-auth-btn", function() {
                var dialogDivJson = {
                    "data-url": $(this).data("url"),
                    "data-width": "500",
                    "data-height": "350",
                    "data-title": $(this).data("title"),
                    "data-destroy-dialog-on-close": "",
                };
                if ($(".js-create-or-edit-mode").exists()) {
                    dialogDivJson['data-dialog'] = '';
                    dialogDivJson['data-event'] = "yukon:multispeak:saveVendorEndPointAuth";
                    dialogDivJson['data-ok-text'] = yg.text.save;
                }
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));
            });

            $(document).on("yukon:multispeak:saveVendorEndPointAuth", function(event) {
                var dialog = $(event.target),
                       form = dialog.find('#js-vendor-endpointauth-form');
                $.ajax({
                    type: "POST",
                    url: yukon.url("/multispeak/setup/endpointAuth/save"),
                    data: form.serialize()
                });
                dialog.dialog('close');
                dialog.empty();
            });

            if (_initialized)
                return;
            _initialized = true;
        }
    };
    return mod;
}());

$(function() {
    yukon.admin.multispeak.init();
});
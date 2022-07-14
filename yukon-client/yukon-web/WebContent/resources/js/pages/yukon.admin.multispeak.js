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
            if($('#interfaceAuth' + mspInterface) != null){
                $('#interfaceAuth' + mspInterface).prop('disabled', !selected);
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
                $('#results').text(data.MSP_RESULT_MSG);
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
            
            $(document).on("click", ".js-endpoint-auth-btn", function(ev) {
                var indexValue = $(this).closest('tr').attr('data-id'),
                    mode = $('#js-page-mode').val(),
                    popuptitle = $(this).data("title"),
                    inUserName = $('#js-msp-inusername-' + indexValue).val(),
                    inPassword = $('#js-msp-inpassword-' + indexValue).val(),
                    outUserName = $('#js-msp-outusername-' + indexValue).val(),
                    outPassword = $('#js-msp-outpassword-' + indexValue).val(),
                    useVendorAuth = $('#js-msp-uservendorauth-' + indexValue).val(),
                    validateCertificate = $('#js-msp-validatecertificate-' + indexValue).val();

                $.ajax({
                    type: 'POST',
                    data: {
                        'indexValue': indexValue,
                        'inUserName': inUserName,
                        'inPassword': inPassword,
                        'outUserName': outUserName,
                        'outPassword': outPassword,
                        'useVendorAuth': useVendorAuth,
                        'validateCertificate': validateCertificate,
                        'mode': mode
                    },
                    url: yukon.url('/multispeak/setup/renderEndpointAuthPopup')
                }).done(function(view, status, xhr) {
                    var dialogDivJson = {
                            "data-width": "500",
                            "data-height": "365",
                            "data-title": popuptitle,
                            "data-destroy-dialog-on-close": "",
                        };
                        if ($(".js-create-or-edit-mode").exists()) {
                            dialogDivJson['data-dialog'] = '';
                            dialogDivJson['data-event'] = "yukon:multispeak:saveVendorEndPointAuth";
                        }
                        yukon.ui.dialog($("<div/>").attr(dialogDivJson).html(view));
                });
            });

            $(document).on("yukon:multispeak:saveVendorEndPointAuth", function(event) {
                var dialog = $(event.target),
                    indexRow = $('#js-interface-index-value').val(),
                    form = dialog.find('#js-vendor-endpointauth-form');

                $('#js-msp-uservendorauth-' + indexRow).val(form.find('#js-dialog-usevendorauth').is(':checked'));
                $('#js-msp-inusername-' + indexRow).val(form.find('#js-dialog-inusername').val());
                $('#js-msp-inpassword-' + indexRow).val(form.find('.js-dialog-inpassword').val());
                $('#js-msp-outusername-' + indexRow).val(form.find('#js-dialog-outusername').val());
                $('#js-msp-outpassword-' + indexRow).val(form.find('.js-dialog-outpassword').val());
                $('#js-msp-validatecertificate-' + indexRow).val(form.find('#js-dialog-validatecertificate').is(':checked'));
                dialog.dialog('close');
                dialog.empty();
            });
				if ($("#attributes").is(":visible")) {
                $("#attributes").chosen({width: "250px"});}
                
                 if(document.querySelector('[id^="mspVendor.attributes.errors"]')!==null){
              $('#attributes').val([]).trigger('chosen:updated');
              }
             
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
yukon.namespace('yukon.widget.commChannel.info.js');
 
/** 
 * TODO 
 * @module yukon.widget.commChannel.info.js 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.widget.commChannel.info = (function () {
 
    'use strict';
 
    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;
            $(document).on("yukon:assets:commChannel:save", function(event) {
                var dialog = $(event.target),
                    form = dialog.find('#commChannel-info-form'),
                    popup = $('#js-edit-comm-channel-popup'),
                    errorMessage = popup.find('.user-message'),
                    errorMessageFound = errorMessage.is(":visible"),
                    globalError = popup.find('.js-global-error'),
                    globalErrorFound = globalError.is(":visible");

                if (!errorMessageFound || globalErrorFound) {
                    yukon.ui.blockPage();
                    $.ajax({
                        type: "POST",
                        url: yukon.url("/widget/commChannelInfoWidget/save"),
                        data: form.serialize()
                    }).done(function (data) {
                        window.location.href = window.location.href;
                        dialog.dialog('close');
                        dialog.empty();
                    }).fail(function (xhr, status, error){
                        popup.html(xhr.responseText);
                        yukon.ui.initContent(popup);
                        yukon.ui.highlightErrorTabs();
                        yukon.ui.unblockPage();
                    });
                } else {
                    yukon.ui.unblockPage();
                    window.location.href = window.location.href;
                }
            });

            $(document).on('change', '.js-carrier-detect-wait-switch', function (event) {
                var isCarrierDetectSelected = $(event.target).prop('checked'),
                    container = $(this).closest(".js-general-tbl");
                if (isCarrierDetectSelected) {
                    container.find(".js-carrierDetectWait").val("1");
                } else {
                    container.find(".js-carrierDetectWait").val("0");
                    $('.js-carrierDetectWait').removeClass("error");
                    $("span[id='carrierDetectWaitInMilliseconds.errors']").remove();
                }
            });

            $(document).on('change', '.js-encryption-key-switch', function (event) {
                var isEncryptionKeySelected = $(event.target).prop('checked'),
                    container = $(this).closest(".js-general-tbl");
                if (!isEncryptionKeySelected) {
                    container.find(".js-encryptionKey").val("");
                    $('.js-encryptionKey').removeClass("error");
                    $("span[id='keyInHex.errors']").remove();
                }
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.commChannel.info.init(); });
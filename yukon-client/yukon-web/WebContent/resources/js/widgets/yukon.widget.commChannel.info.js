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
                    globalErrorFound = globalError.is(":visible"),
                    isCarrierDetectWaitSelected = popup.find(".js-carrier-detect-wait").find(".js-carrier-detect-wait-switch").find(".switch-btn-checkbox").prop("checked");

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
                        if (!isCarrierDetectWaitSelected) {
                            popup.find(".js-carrier-detect-wait").find(".js-units").addClass("dn");
                        }
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
                    container.find(".js-units").removeClass("dn");
                } else {
                    container.find(".js-carrierDetectWait").val("0");
                    container.find(".js-units").addClass("dn");
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

            $(document).on("yukon:assets:commChannel:load", function(event) {
                var container = $(this).find(".js-carrier-detect-wait"),
                    isCarrierDetectWaitSelected = container.find(".js-carrier-detect-wait-switch").find(".switch-btn-checkbox").prop("checked");
                if (!isCarrierDetectWaitSelected) {
                    container.find(".js-units").addClass("dn");
                }
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.commChannel.info.init(); });
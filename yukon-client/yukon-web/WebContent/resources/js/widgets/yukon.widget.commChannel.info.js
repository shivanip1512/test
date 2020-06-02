yukon.namespace('yukon.widget.commChannel.info.js');
 
/** 
 * TODO 
 * @module yukon.widget.commChannel.info.js 
 * @requires JQUERY 
 * @requires yukon 
 */
yukon.widget.commChannel.info = (function () {
 
    'use strict';
 
    /**
     * Shows user entered physical port field based on the drop down value selected
     */
    var togglePhysicalPort = function () {
        if ($('.js-physical-port').is(":visible")) {
            var selectedPort = $('.js-physical-port option:selected').val(),
                otherEnumValue = $('#otherPhysicalPortEnumValue').val(),
                isOtherSelected = selectedPort === otherEnumValue;
            $('.js-user-physical-port-value').toggleClass('dn', !isOtherSelected);
            var physicalPortError = $("#physicalPortErrors").val();
            if (physicalPortError) {
                var physicalPortRow = $('.js-physical-port-row'),
                    portError = physicalPortRow.find("span[id='physicalPort.errors']");
                portError.toggleClass('dn', !isOtherSelected);
            }
        }
    };
    
    /**
     * Provides physical port error formatting
     */
    var formatPhysicalPortErrors = function () {
        var physicalPort = $('.js-physical-port');
        if (physicalPort.exists()) {
            var physicalPortError = $("#physicalPortErrors").val();
            if (physicalPortError) {
                var physicalPortRow = $('.js-physical-port-row'),
                    firstPortError = physicalPortRow.find("span[id='physicalPort.errors']").first();
                //remove second validation error and line break
                firstPortError.prev('br').remove();
                firstPortError.remove();
                physicalPort.removeClass("error");
                physicalPortRow.find("span[id='physicalPort.errors']").css({'margin-left':'80px'});
            }
        }
    };
    
    /**
     * Provides initial physical port other selection
     */
    var loadPhysicalPort = function () {
        var physicalPort = $('.js-physical-port');
        if (physicalPort.exists()) {
            var isOtherSelected = $('#isOtherSelected').val(),
                otherEnumValue = $('#otherPhysicalPortEnumValue').val(),
                userEnteredPhysicalPort = $('.js-user-physical-port-value');
            if (isOtherSelected) {
                physicalPort.val(otherEnumValue);
                userEnteredPhysicalPort.toggleClass('dn', !isOtherSelected);
            }
            var physicalPortError = $("#physicalPortErrors").val();
            if (!physicalPortError && !isOtherSelected) {
                userEnteredPhysicalPort.val("");
            }

        }

    };

    var
    _initialized = false,
 
    mod = {
 
        /** Initialize this module. */
        init: function () {
 
            if (_initialized) return;

            $(document).on('change', '.js-physical-port', function (event) {
                togglePhysicalPort();
            });

            $(document).on("yukon:assets:commChannel:save", function(event) {
                var dialog = $(event.target),
                    form = dialog.find('#commChannel-info-form'),
                    popup = $('#js-edit-comm-channel-popup'),
                    errorMessage = popup.find('.user-message'),
                    errorMessageFound = errorMessage.is(":visible"),
                    globalError = popup.find('.js-global-error'),
                    globalErrorFound = globalError.is(":visible"),
                    userPortEntered = popup.find('.js-user-physical-port-value').is(':visible'),
                    carrierDetectWait = popup.find('.js-carrier-detect-wait-switch'),
                    encryptionKey = popup.find('.js-encryption-key-switch');

                popup.find('.js-physical-port').prop('disabled', userPortEntered);
                popup.find('.js-user-physical-port-value').prop('disabled', !userPortEntered);


                if (carrierDetectWait.is(':visible')) {
                    var carrierDetectWaitRow = carrierDetectWait.closest('tr'),
                        isCarrierChecked = carrierDetectWaitRow.find('.switch-btn-checkbox').prop('checked');
                    if (!isCarrierChecked) {
                        popup.find(".js-carrierDetectWait").val("0");
                    }
                }

                if (encryptionKey.is(':visible')) {
                    var encryptionKeyRow = encryptionKey.closest('tr'),
                        isEncryptionKeyChecked = encryptionKeyRow.find('.switch-btn-checkbox').prop('checked');
                    if (!isEncryptionKeyChecked) {
                        popup.find(".js-encryptionKey").val("");
                    }
                }

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
                        loadPhysicalPort();
                        formatPhysicalPortErrors();
                        $('.js-carrier-detect-wait').find("span[id='carrierDetectWaitInMilliseconds.errors']").css({'margin-left':'80px'});
                        $('.js-encryption-key').find("span[id='keyInHex.errors']").css({'margin-left':'80px'});
                        $('.js-encryption-key').find("span[id='keyInHex.errors']").addClass("dib");
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
                    var findPreviousCarrierDetectValue = container.find(".js-carrierDetectWait").val();
                    if (findPreviousCarrierDetectValue === '0') {
                        container.find(".js-carrierDetectWait").val("1");
                    }
                    $("span[id='carrierDetectWaitInMilliseconds.errors']").removeClass('dn');
                    $('.js-carrier-detect-wait').find("span[id='carrierDetectWaitInMilliseconds.errors']").css({'margin-left':'80px'});
                } else {
                    $("span[id='carrierDetectWaitInMilliseconds.errors']").addClass('dn');
                }
            });

            $(document).on('change', '.js-encryption-key-switch', function (event) {
                var isEncryptionKeySelected = $(event.target).prop('checked'),
                    container = $(this).closest(".js-general-tbl");
                if (!isEncryptionKeySelected) {
                    $("span[id='keyInHex.errors']").addClass('dn');
                } else {
                    $("span[id='keyInHex.errors']").removeClass('dn');
                    $('.js-encryption-key').find("span[id='keyInHex.errors']").css({'margin-left':'80px'});
                }
            });

            $(document).on("yukon:assets:commChannel:load", function(event) {
                loadPhysicalPort();
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.commChannel.info.init(); });
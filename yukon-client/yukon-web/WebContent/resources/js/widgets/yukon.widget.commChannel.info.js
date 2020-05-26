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
     * Shows physical port field based on the drop down value selected
     */
    var updatePhysicalPort = function (event) {
        if ($(".js-physical-port").is(":visible")) {
            var physicalPortError = $("#physicalPortErrors").val();
            if (physicalPortError) {
                var otherPhysicalPort = $("#otherPhysicalPort").val();
                $(".js-physical-port").val(otherPhysicalPort);
                $('.js-user-physical-port-value').removeClass('dn');
                $('.js-physical-port-row').find('br').eq(0).remove();
                $('.js-physical-port-row').find('span:first').remove();
            } else {
                var physicalPort = $('.js-physical-port option:selected').val();
                var phyicalPortField = physicalPort === 'Other';
                $('.js-user-physical-port-value').toggleClass('dn', !phyicalPortField);
                $('.js-user-physical-port-value').val("");
                var userValue = $('.js-user-physical-port-value').val();
                $("input[name='physicalPort']").val(userValue);
            }
        } else {
            var dialog = $(event.target),
                commChannelForm = dialog.find('#commChannel-info-form'),
                physicalPortElement = commChannelForm.find('.js-user-physical-port-value').val(),
                isPhysicalPortOther = $("#otherPhysicalPort").val();
            if (!$.isEmptyObject(isPhysicalPortOther)) {
                var otherPhysicalPort = $("#otherPhysicalPort").val();
                $(".js-physical-port").val(otherPhysicalPort);
                $('.js-user-physical-port-value').removeClass('dn');
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
                var physicalPortError = $("#physicalPortErrors").val();
                if (physicalPortError) {
                    $("span[id='physicalPort.errors']").remove();
                    $('.js-physical-port').removeClass("error");
                    $('.js-user-physical-port-value').removeClass("error");
                    $("#physicalPortErrors").val("");
                }
                updatePhysicalPort(event);
            });

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
                        updatePhysicalPort(event);
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

            $(document).on("yukon:assets:commChannel:load", function(event) {
                updatePhysicalPort(event);
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.commChannel.info.init(); });
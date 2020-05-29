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
                var otherPhysicalPort = $("#portEnumValue").val();
                $(".js-physical-port").val(otherPhysicalPort);
                $('.js-user-physical-port-value').removeClass('dn');
                $('.js-physical-port-row').find("span[id='physicalPort.errors']").css({'margin-left':'80px'});
            } else {
                var physicalPort = $('.js-physical-port option:selected').val();
                var phyicalPortField = physicalPort === $("#otherPhysicalPortEnumValue").val();
                $('.js-user-physical-port-value').toggleClass('dn', !phyicalPortField);
                if (phyicalPortField) {
                    if ($("span[class='error']").length === 0) {
                        $('.js-user-physical-port-value').val("");
                    }
                } else {
                    $("input[name='physicalPort']").val(physicalPort);
                }
            }
        } else {
            var isPhysicalPortOther = $("#portEnumValue").val();
            if (!$.isEmptyObject(isPhysicalPortOther)) {
                var otherPhysicalPort = $("#portEnumValue").val();
                $(".js-physical-port").val(otherPhysicalPort);
                $('.js-user-physical-port-value').removeClass('dn');
            } else {
                $(".js-physical-port").val($("#portValue").val());
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
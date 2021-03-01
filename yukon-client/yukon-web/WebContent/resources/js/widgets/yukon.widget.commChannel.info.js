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

            $(document).on('change', '.js-physical-port', function (event) {
                yukon.comm.channel.togglePhysicalPort($(this).closest('.ui-dialog'));
            });

            $(document).on("yukon:assets:commChannel:save", function(event) {
                var dialog = $(event.target),
                    form = dialog.find('#commChannel-info-form'),
                    popup = $('#js-edit-comm-channel-popup'),
                    errorMessage = popup.find('.user-message'),
                    errorMessageFound = errorMessage.is(":visible"),
                    globalError = popup.find('.js-global-error'),
                    globalErrorFound = globalError.is(":visible"),
                    userPortField = popup.find('.js-user-physical-port-value'),
                    userPortEntered = userPortField.exists() && !userPortField.hasClass('dn'),
                    carrierDetectWaitField = popup.find('.js-carrier-detect-wait-switch'),
                    carrierDetectWait = carrierDetectWaitField.exists() && !carrierDetectWaitField.hasClass('dn'),
                    encryptionKeyField = popup.find('.js-encryption-key-switch'),
                    encryptionKey = encryptionKeyField.exists() && !encryptionKeyField.hasClass('dn'),
                    selectedSocketType = popup.find("input[class='js-socket-type-val']:checked").val();

                popup.find('.js-physical-port').prop('disabled', userPortEntered);
                userPortField.prop('disabled', !userPortEntered);
                if (selectedSocketType === $('#socketTypeNone').val()) {
                    popup.find("input[id='js-socket-number-val']").val("1025");
                }

                if (carrierDetectWait) {
                    var carrierDetectWaitRow = carrierDetectWaitField.closest('tr'),
                        isCarrierChecked = carrierDetectWaitRow.find('.switch-btn-checkbox').prop('checked');
                    if (!isCarrierChecked) {
                        popup.find(".js-carrierDetectWait").val("0");
                    }
                }

                if (encryptionKey) {
                    var encryptionKeyRow = encryptionKeyField.closest('tr'),
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
                        yukon.comm.channel.loadPhysicalPort(popup);
                        yukon.comm.channel.formatPhysicalPortErrors(popup);
                        var carrierDetectWaitErrorContainer = $('.js-carrier-detect-wait').find("span[id='carrierDetectWaitInMilliseconds.errors']"),
                            encryptionKeyErrorContainer = $('.js-encryption-key').find("span[id='keyInHex.errors']");
                        carrierDetectWaitErrorContainer.css({'margin-left':'80px'});
                        carrierDetectWaitErrorContainer.addClass("dib");
                        encryptionKeyErrorContainer.css({'margin-left':'80px'});
                        encryptionKeyErrorContainer.addClass("dib");
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
                    container = $(this).closest(".js-general-tbl"),
                    carrierDetectErrorSpan = $("span[id='carrierDetectWaitInMilliseconds.errors']");
                if (isCarrierDetectSelected) {
                    var findPreviousCarrierDetectValue = container.find(".js-carrierDetectWait").val();
                    if (findPreviousCarrierDetectValue === '0') {
                        container.find(".js-carrierDetectWait").val("1");
                    }
                    carrierDetectErrorSpan.removeClass('dn');
                    $('.js-carrier-detect-wait').find(carrierDetectErrorSpan).css({'margin-left':'80px'});
                } else {
                    carrierDetectErrorSpan.addClass('dn');
                }
            });

            $(document).on('change', '.js-encryption-key-switch', function (event) {
                var isEncryptionKeySelected = $(event.target).prop('checked'),
                    encryptionKeyErrorSpan = $("span[id='keyInHex.errors']");
                if (!isEncryptionKeySelected) {
                    encryptionKeyErrorSpan.addClass('dn');
                } else {
                    encryptionKeyErrorSpan.removeClass('dn');
                    $('.js-encryption-key').find(encryptionKeyErrorSpan).css({'margin-left':'80px'});
                }
            });

            $(document).on("yukon:assets:commChannel:load", function(event) {
                var popup = $(event.target);
                yukon.comm.channel.loadPhysicalPort(popup);
                var isUserMessageVisible = popup.find('#user-message').is(":visible");
                if (isUserMessageVisible) {
                    popup.closest('.ui-dialog').find('.ui-dialog-buttonset').find('.js-primary-action').prop('disabled', true);
                }
            });

            $(document).on('change', '#js-socket-type', function (event) {
                var selectedSocketType = $("input[class='js-socket-type-val']:checked").val(),
                    socketTypeNone = $('#socketTypeNone').val();
                $('.js-socket-number').toggleClass('dn', selectedSocketType === socketTypeNone);
            });

            _initialized = true;
        }
 
    };
 
    return mod;
})();
 
$(function () { yukon.widget.commChannel.info.init(); });
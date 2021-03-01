yukon.namespace('yukon.ami.meterDetails');

/**
 * Handles behavior on the meter details page.
 * @module yukon.ami.meterDetails
 * @requires JQUERY
 * @requires yukon
 */
yukon.ami.meterDetails = (function () {

    'use strict';
    
    var _initialized = false;
    
    mod = {
            
        updateMeterTypeFields : function() {
            var rfMeterTypes = yukon.fromJson('#rf-meter-types'),
                mctMeterTypes = yukon.fromJson('#mct-meter-types'),
                virtualMeterType = yukon.fromJson('#virtual-meter-type'),
                meterType = $('#meter-type').val(),
                isRF = rfMeterTypes.indexOf(meterType) !== -1,
                isMCT = mctMeterTypes.indexOf(meterType) !== -1,
                isVirtual = virtualMeterType.indexOf(meterType) !== -1;
            if (isRF) {
                $('.js-rf-fields').removeClass('dn');
                $('.js-mct-fields').addClass('dn');
                $('.js-ied-fields').addClass('dn');
            } else if (isMCT) {
                $('.js-mct-fields').removeClass('dn');
                $('.js-rf-fields').addClass('dn');
                $('.js-ied-fields').addClass('dn');
            } else if (isVirtual) {
                $('.js-mct-fields').addClass('dn');
                $('.js-rf-fields').addClass('dn');
                $('.js-ied-fields').addClass('dn');
            }
            else {
                $('.js-ied-fields').removeClass('dn');
                $('.js-mct-fields').addClass('dn');
                $('.js-rf-fields').addClass('dn');
            }
            
        },
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return; 
            
            $(document).on('yukon.ami.meterDetails.saveMeter', function (ev) {
                $('#meter-create-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        window.location.href=yukon.url('/meter/home?deviceId=' + data.deviceId);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#contentPopup').html(xhr.responseText);
                        mod.updateMeterTypeFields();
                    }
                });
            });

            $('#commander-menu-option').click(function (ev) {
                var params = {
                    target: 'DEVICE',
                    paoId: $('#device-id').val()
                };
                $.ajax({
                    type: 'POST',
                    url: yukon.url('/tools/commander/updateCommanderPreferences'),
                    data: params 
                }).done(function() {
                    window.location.href = yukon.url('/tools/redirectToCommander');
                });
            });

            $('.js-create-meter').click(function () {
                var content = $('#contentPopup'),
                popupTitle = content.data('create-title');
                
                content.load(yukon.url('/meter/create'), function () {
                    content.dialog({
                        title: popupTitle, 
                        width: 500,
                        minWidth: 500,
                        minHeight: 310,
                        classes: {
                            "ui-dialog": 'ov'
                        },
                        buttons: yukon.ui.buttons({ okText: yg.text.create, event: 'yukon.ami.meterDetails.saveMeter' }),
                        modal: true});
                });
            });
            
            $('.js-copy-meter').click(function () {
                var content = $('#contentPopup'),
                deviceId = $('#device-id').val(),
                popupTitle = content.data('copy-title');
                
                content.load(yukon.url('/meter/copy/'+deviceId), function () {
                    content.dialog({
                        title: popupTitle, 
                        width: 500,
                        minWidth: 500,
                        minHeight: 310,
                        classes: {
                            "ui-dialog": 'ov'
                        },
                        buttons: yukon.ui.buttons({ okText: yg.text.create, event: 'yukon.ami.meterDetails.saveMeter' }),
                        modal: true});
                });
            });
            
            $('.js-hide-dropdown').click(function () {
                $('.dropdown-menu').hide()
            });
            
            $(document).on('yukon:meter:delete', function (event) {
                yukon.ui.blockPage();
                $('#delete-meter-form').submit();
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () { yukon.ami.meterDetails.init(); });
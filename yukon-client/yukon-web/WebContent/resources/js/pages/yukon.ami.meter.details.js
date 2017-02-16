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
                    meterType = $('#meter-type').val(),
                    isRF = rfMeterTypes.indexOf(meterType) !== -1,
                    isMCT = mctMeterTypes.indexOf(meterType) !== -1;
                if (isRF) {
                    $('.js-rf-fields').removeClass('dn');
                    $('.js-mct-fields').addClass('dn');
                    $('.js-ied-fields').addClass('dn');
                } else if (isMCT) {
                    $('.js-mct-fields').removeClass('dn');
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
                },
                url = yukon.url('/tools/commander/updateCommanderPreferences');
            
                $.ajax({ type: 'POST', url: url, data: params });
                window.location.href = yukon.url('/tools/commander');
            });
            
            $('.js-create-meter').click(function () {
                var content = $('#contentPopup'),
                popupTitle = content.data('title');
                
                content.load(yukon.url('/meter/create'), function () {
                    content.dialog({
                        title: popupTitle, 
                        width: 500, 
                        dialogClass: 'ov',
                        buttons: yukon.ui.buttons({ okText: yg.text.create, event: 'yukon.ami.meterDetails.saveMeter' }),
                        modal: true});
                });
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () { yukon.ami.meterDetails.init(); });
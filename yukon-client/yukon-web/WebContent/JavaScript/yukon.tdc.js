/**
 * Singleton that manages the data view feature (TDC)
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.tdc');

yukon.tdc = (function () {
    var _playAudio = function () {
        try {
            $('#alarm-audio')[0].play();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
    _pauseAudio = function () {
        try {
            $('#alarm-audio')[0].pause();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
    tdcMod; 
        
    tdcMod = {
        
        toggleAlarm : function (alarms) {
            if($('#alarm-audio')[0].mute == null || $('#alarm-audio')[0].mute == false){
                if (alarms.value == 'MULT_ALARMS') {
                    _playAudio();
                } else if (alarms.value == 'NO_ALARMS') {
                    _pauseAudio();
                }
            }
        },
        init : function () {
            
            $('#latestData').load(yukon.url('/tools/data-viewer/refresh'));
            setInterval(function () {
                $('#latestData').load(yukon.url('/tools/data-viewer/refresh'));
            }, 4000);

            $('#display_tabs').tabs({
                'class' : 'section'
            });
       
            $('#b_mute').click(function (e) {
                $('#b_mute').hide();
                $('#b_unmute').show();
                _pauseAudio();
                $('#alarm-audio')[0].mute = true;
            });

            $('#b_unmute').click(function (e) {
                $('#b_mute').show();
                $('#b_unmute').hide();
                $('#alarm-audio')[0].mute = false;
            });
            
            $('.js-ack-all').click(function () {
                $.post(yukon.url('/tools/data-viewer/acknowledgeAll'), {}).done(function (data) {
                    yukon.ui.alertSuccess(data.success);
                });
            });    
            
            $(document).on('click', '.js-ack', function (ev) {
                
                var btn = $(this),
                    pointId = btn.data('pointId'),
                    condition = btn.data('condition'),
                    args = {};
                
                args.pointId = pointId;
                args.condition = condition;
                
                $.post(yukon.url('/tools/data-viewer/acknowledge'), args).done(function (data) {
                    $('#latestData').load(yukon.url('/tools/data-viewer/refresh'));
                });
            });
            
            $('.js-trend').click(function (ev) {
                
                var option = $(this),
                    popupTitle = option.data('popupTitle'),
                    pointId = option.data('pointId'),
                    url = yukon.url('/tools/data-viewer/trend'),
                    data = { pointId : pointId };
                
                $('#tdc-popup').load(url, data, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-tags').click(function () {
                
                var option = $(this),
                    popupTitle = option.data('popupTitle'),
                    pointId = option.data('pointId'),
                    deviceId = option.data('deviceId'),
                    url = yukon.url('/tools/data-viewer/tags'),
                    data = { deviceId : deviceId, pointId : pointId };
                
                $('#tdc-popup').load(url, data, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-enableDisable').click(function (ev) {
                
                var option = $(this),
                    popupTitle = option.data('popupTitle'),
                    pointId = option.data('pointId'),
                    url = yukon.url('/tools/data-viewer/enableDisable');
                
                $('#tdc-popup').load(url, { pointId : pointId }, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 400,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-manualEntry').click(function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/data-viewer/manualEntry');
                
                $('#tdc-popup').load(url, { pointId : pointId }, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-manualControl').click(function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    deviceId = option.data('deviceId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/data-viewer/manualControl'),
                    data = { deviceId : deviceId, pointId : pointId };
                
                $('#tdc-popup').load(url, data, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-altScan').click(function (ev) {
                
                var option = $(this),
                    deviceId = option.data('deviceId'),
                    popupTitle = option.data('popupTitle'),
                    url = '/tools/data-viewer/alt-scan-rate?deviceId=' + deviceId;
                
                $('#tdc-popup').load(yukon.url(url), function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-display-alarm-ack').click(function (ev) {
                $.post(yukon.url('/tools/data-viewer/acknowledgeAlarmsForDisplay'), { displayId: $(this).data('displayId') })
                .done(function (data) { yukon.ui.alertSuccess(data.success); });
            });
            
           $('.js-one-alarm-ack').click(function (ev) {
               
               var option = $(this),
                   pointId = option.data('pointId'),
                   condition = option.data('condition'),
                   data = { pointId: pointId, condition: condition };
               
                   $('#dropdown_' + pointId).find('ul').hide();
                   $('#dropdown_' + pointId).removeClass('menu-open');
                   $.post(yukon.url('/tools/data-viewer/acknowledgeAlarm'), data);
               });
            
            $(document).on('click', '.js-one-alarm-ack-b', function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    condition = option.data('condition'),
                    data = { pointId : pointId, condition : condition };
                
                $.post(yukon.url('/tools/data-viewer/acknowledgeAlarm'), data);
            });
            
            $('.js-mult-alarm-ack').click(function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/data-viewer/unacknowledged');
                
                $('#tdc-popup').load(url, { pointId : pointId }, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            
            $(document).on('click', '.js-altScanRate-send', function (ev) {
                
                $.ajax({
                    url : $('#altScanRateForm').attr('action'),
                    data : $('#altScanRateForm').serialize(),
                    type : 'POST'
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.js-manualEntry-send', function (ev) {
                submitFormViaAjax('tdc-popup', 'manualEntryForm');
            });
            
            $(document).on('click', '.js-enableDisable-send', function (ev) {
                
                $.ajax({
                    url : $('#enableDisableForm').attr('action'),
                    data : $('#enableDisableForm').serialize(),
                    type : 'POST'
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.js-manualControl-send', function (ev) {
                submitFormViaAjax('tdc-popup', 'manualControlForm');
            });
            
            $(document).on('click', '.js-tags-save', function (ev) {
                
                $.ajax({
                    url: yukon.url('/tools/data-viewer/tagsSave'),
                    data: $('#tagsForm').serialize(),
                    type: 'POST'
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.js-tags-add', function (ev) {
                submitFormViaAjax('tdc-popup', 'tagsForm', yukon.url('/tools/data-viewer/tagAdd'));
            });
            
            $(document).on('click', '.js-tags-remove', function (ev) {
                var rowIndex = $(this).data('row');
                $('#rowIndex').val(rowIndex);
                submitFormViaAjax('tdc-popup', 'tagsForm', yukon.url('/tools/data-viewer/tagRemove'));
            });
            
            $(document).on('click', '.js-ack-alarm', function (ev) {
                var data = { pointId: $(this).data('pointId'), condition: $(this).data('condition') };
                $('#' + data.condition).hide();
                $('#' + data.condition).removeClass('js-ack-alarm');
                $.post(yukon.url('/tools/data-viewer/acknowledgeAlarm'), data).done(function (json) {
                    if ($('.js-ack-alarm').length === 0) $('.js-ack-alarms-for-point').hide();
                });
            });
            
            $(document).on('click', '.js-ack-alarms-for-point', function (ev) {
                $.post(yukon.url('/tools/data-viewer/acknowledgeAlarmsForPoint'), { pointid: $(this).data('pointId') })
                .done(function (data) {  
                    yukon.ui.alertSuccess(data.success);
                    $('#tdc-popup').dialog('close');
                });
            });
        }
    };
    
    return tdcMod;
}());

$(function () { yukon.tdc.init(); });
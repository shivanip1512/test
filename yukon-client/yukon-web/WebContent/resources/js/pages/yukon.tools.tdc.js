yukon.namespace('yukon.tools.tdc');

/**
 * Module that manages the data view feature (TDC)
 * @module   yukon.tools.tdc
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.tools.tdc = (function () {
    
    
    /** Plays the alarm audio. */
    var _playAudio = function () {
        try {
            $('#alarm-audio')[0].play();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
    
    /** Pause the alarm audio. */
    _pauseAudio = function () {
        try {
            $('#alarm-audio')[0].pause();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
        
    _mod = {
        
        /** 
         * Toggle the playing and pausing of alarms.
         * @param {Object} alarm - alarm 
         */
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
            
            $('#tdc-latest-data').load(yukon.url('/tools/data-viewer/refresh'));
            setInterval(function () {
                $('#tdc-latest-data').load(yukon.url('/tools/data-viewer/refresh'));
            }, 4000);

            $('#tdc-display-tabs').tabs({
                'class' : 'section'
            });
       
            $('#tdc-mute-btn').click(function (e) {
                $('#tdc-mute-btn').hide();
                $('#tdc-unmute-btn').show();
                _pauseAudio();
                $('#alarm-audio')[0].mute = true;
            });

            $('#tdc-unmute-btn').click(function (e) {
                $('#tdc-mute-btn').show();
                $('#tdc-unmute-btn').hide();
                $('#alarm-audio')[0].mute = false;
            });
            
            $('.js-ack-all').click(function () {
                $.post(yukon.url('/tools/data-viewer/acknowledge-all'), {}).done(function (data) {
                    yukon.ui.alertSuccess(data.success);
                });
            });    
            
            $(document).on('click', '.js-tdc-ack', function (ev) {
                var btn = $(this),
                    pointId = btn.data('pointId'),
                    condition = btn.data('condition'),
                    args = {};
                
                args.pointId = pointId;
                args.condition = condition;
                
                $.post(yukon.url('/tools/data-viewer/acknowledge'), args).done(function (data) {
                    $('#tdc-latest-data').load(yukon.url('/tools/data-viewer/refresh'));
                });
            });
            
            $('.js-tdc-trend').click(function (ev) {
                
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
            
            $('.js-tdc-tags').click(function () {
                
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
            
            $('.js-tdc-enable-disable').click(function (ev) {
                
                var option = $(this),
                    popupTitle = option.data('popupTitle'),
                    pointId = option.data('pointId'),
                    url = yukon.url('/tools/data-viewer/enable-disable');
                
                $('#tdc-popup').load(url, { pointId : pointId }, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 400,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-tdc-manual-entry').click(function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/data-viewer/manual-entry');
                
                $('#tdc-popup').load(url, { pointId : pointId }, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-tdc-manual-control').click(function (ev) {
                
                var option = $(this),
                    pointId = option.data('pointId'),
                    deviceId = option.data('deviceId'),
                    popupTitle = option.data('popupTitle'),
                    url = yukon.url('/tools/data-viewer/manual-control'),
                    data = { deviceId : deviceId, pointId : pointId };
                
                $('#tdc-popup').load(url, data, function () {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            
            $('.js-tdc-alt-scan').click(function (ev) {
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
            
            $('.js-tdc-ack-alarms').click(function (ev) {
                $.post(yukon.url('/tools/data-viewer/acknowledge-alarms-for-display'), { displayId: $(this).data('displayId') })
                .done(function (data) { yukon.ui.alertSuccess(data.success); });
            });
            
           $('.js-tdc-one-alarm-ack').click(function (ev) {
               var option = $(this),
                   pointId = option.data('pointId'),
                   condition = option.data('condition'),
                   data = { pointId: pointId, condition: condition };
                   $.post(yukon.url('/tools/data-viewer/acknowledge-alarm'), data);
               });
            
            $(document).on('click', '.js-tdc-one-alarm-ack-btn', function (ev) {
                var option = $(this),
                    pointId = option.data('pointId'),
                    condition = option.data('condition'),
                    data = { pointId : pointId, condition : condition };
                
                $.post(yukon.url('/tools/data-viewer/acknowledge-alarm'), data);
            });
            
            $('.js-tdc-mult-alarm-ack').click(function (ev) {
                
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
            
            $(document).on('click', '.js-tdc-alt-scan-rate-send', function (ev) {
                
                $.ajax({
                    url : $('#tdc-alt-scan-rate-form').attr('action'),
                    data : $('#tdc-alt-scan-rate-form').serialize(),
                    type : 'POST'
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.js-tdc-manual-entry-send', function (ev) {
                submitFormViaAjax('tdc-popup', 'tdc-manual-entry-form');
            });
            
            $(document).on('click', '.js-tdc-enable-disable-send', function (ev) {
                
                $.ajax({
                    url : $('#tdc-enable-disable-form').attr('action'),
                    data : $('#tdc-enable-disable-form').serialize(),
                    type : 'POST'
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.js-tdc-manual-control-send', function (ev) {
                submitFormViaAjax('tdc-popup', 'tdc-manual-control-form');
            });
            
            $(document).on('click', '.js-tdc-tags-save', function (ev) {
                
                $.ajax({
                    url: yukon.url('/tools/data-viewer/tags-save'),
                    data: $('#tdc-tags-form').serialize(),
                    type: 'POST'
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.js-tdc-tags-add', function (ev) {
                submitFormViaAjax('tdc-popup', 'tdc-tags-form', yukon.url('/tools/data-viewer/tag-add'));
            });
            
            $(document).on('click', '.js-tdc-tags-remove', function (ev) {
                var rowIndex = $(this).data('row');
                $('#rowIndex').val(rowIndex);
                submitFormViaAjax('tdc-popup', 'tdc-tags-form', yukon.url('/tools/data-viewer/tag-remove'));
            });
            
            $(document).on('click', '.js-tdc-ack-alarm', function (ev) {
                var data = { pointId: $(this).data('pointId'), condition: $(this).data('condition') };
                $('#' + data.condition).hide();
                $('#' + data.condition).removeClass('js-tdc-ack-alarm');
                $.post(yukon.url('/tools/data-viewer/acknowledge-alarm'), data).done(function (json) {
                    if ($('.js-tdc-ack-alarm').length === 0) $('.js-tdc-ack-alarms-for-point').hide();
                });
            });
            
            $(document).on('click', '.js-tdc-ack-alarms-for-point', function (ev) {
                $.post(yukon.url('/tools/data-viewer/acknowledge-alarms-for-point'), { pointId: $(this).data('pointId') })
                .done(function (data) {  
                    yukon.ui.alertSuccess(data.success);
                    $('#tdc-popup').dialog('close');
                });
            });
        }
    };
    
    return _mod;
}());

$(function () { yukon.tools.tdc.init(); });
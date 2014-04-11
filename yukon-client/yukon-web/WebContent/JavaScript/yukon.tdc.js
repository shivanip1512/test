/**
 * Singleton that manages the data view feature (TDC)
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.tdc');

yukon.tdc = (function () {
    var _playAudio = function() {
        try {
            $('#alarm-audio')[0].play();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
    _pauseAudio = function() {
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
            
            $('.f-ack-all').click(function () {
                $.post(yukon.url('/tools/data-viewer/acknowledgeAll'), {}).done(function (data) {
                    yukon.ui.flashSuccess(data.success);
                });
            });    
            
            $(document).on('click', '.f-ack', function () {
                var pointId = $(this).attr("pointId");
                var condition = $(this).attr("condition");
                var args = {};
                args.pointId = pointId;
                args.condition = condition;
                $.post(yukon.url('/tools/data-viewer/acknowledge'), args).done(function (data) {
                    $('#latestData').load(yukon.url('/tools/data-viewer/refresh'));
                });
            });
            $('.f-trend').click(function (event) {
                var popupTitle = $(this).parent().attr("popupTitle");
                var pointId = $(this).parent().attr("pointId");
                var url = yukon.url("/tools/data-viewer/trend");
                var data = {
                    pointId : pointId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            $('.f-tags').click(function () {
                var popupTitle = $(this).parent().attr("popupTitle");
                var pointId = $(this).parent().attr("pointId");
                var deviceId = $(this).parent().attr("deviceId");
                var url = yukon.url("/tools/data-viewer/tags");
                var data = {
                    deviceId : deviceId,
                    pointId : pointId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            $('.f-enableDisable').click(function (event) {
                var pointId = $(this).parent().attr("pointId");
                var popupTitle = $(this).parent().attr("popupTitle");
                var url = yukon.url("/tools/data-viewer/enableDisable");
                var data = {
                    pointId : pointId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 400,
                        autoOpen : true
                    });
                });
            });
            $('.f-manualEntry').click(function (event) {
                var pointId = $(this).parent().attr("pointId");
                var url = yukon.url("/tools/data-viewer/manualEntry");
                var popupTitle = $(this).parent().attr("popupTitle");
                var data = {
                    pointId : pointId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            $('.f-manualControl').click(function (event) {
                var pointId = $(this).parent().attr("pointId");
                var deviceId = $(this).parent().attr("deviceId");
                var url = yukon.url("/tools/data-viewer/manualControl");
                var popupTitle = $(this).parent().attr("popupTitle");
                var data = {
                    deviceId : deviceId,
                    pointId : pointId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            $('.f-altScan').click(function (event) {
                var popupTitle = $(this).parent().attr("popupTitle");
                var deviceId = $(this).parent().attr("deviceId");
                var deviceName = $(this).parent().attr("deviceName");
                var url = yukon.url("/tools/data-viewer/altScanRate");
                var data = {
                    deviceName : deviceName,
                    deviceId : deviceId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            $('.f-display-alarm-ack').click(
				function () {
	   			 var displayId = $(this).attr("displayId");
	   			 var data = {
	        		displayId: displayId
	    		};
	    		$.post(yukon.url('/tools/data-viewer/acknowledgeAlarmsForDisplay'), data)
	        		.done(function (data) {
	            	yukon.ui.flashSuccess(data.success);
	        	});
			});
           $('.f-one-alarm-ack').click(function (event) {
				var pointId = $(this).parent().attr( "pointId");
				var condition = $(this).parent().attr("condition");
                   var data = {
                       pointId: pointId,
                       condition: condition
                   };
                   $("#dropdown_"+pointId).find("ul").hide();
                   $("#dropdown_"+pointId).removeClass("menu-open");
                   $.post(yukon.url('/tools/data-viewer/acknowledgeAlarm'), data).done(function (data) {});
               });
            
            $(document).on('click', '.f-one-alarm-ack-b', function () {
                var pointId = $(this).attr("pointId");
                var condition = $(this).attr("condition");
                        var data = {
                            pointId : pointId,
                            condition : condition
                        };
                        $.post(yukon.url('/tools/data-viewer/acknowledgeAlarm'), data).done(
                                function (data) {
                                });
                    });
            $('.f-mult-alarm-ack').click(function (event) {
                var pointId = $(this).parent().attr("pointId");
                var popupTitle = $(this).parent().attr("popupTitle");
                var url = yukon.url("/tools/data-viewer/unacknowledged");
                var data = {
                    pointId : pointId
                };
                $('#tdc-popup').load(url, data, function() {
                    $('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            $(document).on('click', '.f-altScanRate-send', function () {
                $.ajax({
                    url : $("#altScanRateForm").attr("action"),
                    data : $("#altScanRateForm").serialize(),
                    type : "POST"
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            $(document).on('click', '.f-manualEntry-send', function() {
                submitFormViaAjax('tdc-popup', 'manualEntryForm');
            });
            
            $(document).on('click', '.f-enableDisable-send', function() {
                $.ajax({
                    url : $("#enableDisableForm").attr("action"),
                    data : $("#enableDisableForm").serialize(),
                    type : "POST"
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });

            });
            
            $(document).on('click', '.f-manualControl-send', function() {
                submitFormViaAjax('tdc-popup', 'manualControlForm');
            });
            
            $(document).on('click', '.f-tags-save', function() {
                $.ajax({
                    url: yukon.url('/tools/data-viewer/tagsSave'),
                    data: $("#tagsForm").serialize(),
                    type: "POST"
                }).done(function (data, textStatus, jqXHR) {
                    $('#tdc-popup').dialog('close');
                });
            });
            
            $(document).on('click', '.f-tags-add', function() {
                submitFormViaAjax('tdc-popup', 'tagsForm', yukon.url('/tools/data-viewer/tagAdd'));
            });
            
            $(document).on('click', '.f-tags-remove', function() {
                var rowIndex = $(this).attr("rowIndex");
                console.info(rowIndex);
                
                $("#rowIndex").val(rowIndex);
                submitFormViaAjax('tdc-popup', 'tagsForm', yukon.url('/tools/data-viewer/tagRemove'));
            });
            
            $(document).on('click', '.f-ack-alarm', function() {
                var data = {};
                data.pointId = $(this).attr("pointId");
                data.condition = $(this).attr("condition");
                $("#"+data.condition).hide();
                $("#"+data.condition).removeClass("f-ack-alarm");
                $.post(yukon.url('/tools/data-viewer/acknowledgeAlarm'), data).done(function(json) {
                    if ($('.f-ack-alarm').length === 0) $('.f-ack-alarms-for-point').hide();
                });
            });
            
            $(document).on('click', '.f-ack-alarms-for-point', function() {                
                var pointId = $(this).attr("pointId");
                var data = {};
                data.pointId = pointId;
                $.post(yukon.url('/tools/data-viewer/acknowledgeAlarmsForPoint'), data).done(function(data) {  
                    yukon.ui.flashSuccess(data.success);
                    $('#tdc-popup').dialog('close');
                });
            });
        }
    };
    return tdcMod;
}());

$(function () {
    yukon.tdc.init();
});
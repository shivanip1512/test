/**
 * Singleton that manages the data view feature (TDC)
 * 
 * @requires jQuery 1.8.3+
 * @requires jQuery UI 1.9.2+
 */

yukon.namespace('yukon.Tdc');

yukon.Tdc = (function () {
    var _playAudio = function() {
        try {
            jQuery('#alarm-audio')[0].play();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
    _pauseAudio = function() {
        try {
            jQuery('#alarm-audio')[0].pause();
        } catch (err) {
            // IE will throw js exceptions sometimes,
            // probably caused by no audio service started or audio driver installed.
        }
    },
    tdcMod; 
        
    tdcMod = {
        
        toggleAlarm : function (alarms) {
            if(jQuery('#alarm-audio')[0].mute == null || jQuery('#alarm-audio')[0].mute == false){
                if (alarms.value == 'MULT_ALARMS') {
                    _playAudio();
                } else if (alarms.value == 'NO_ALARMS') {
                    _pauseAudio();
                }
            }
        },
        init : function () {
            
            jQuery('#latestData').load('/tdc/refresh');
            setInterval(function () {
                jQuery('#latestData').load('/tdc/refresh');
            }, 4000);

            jQuery('#display_tabs').tabs({
                'class' : 'section',
                'cookie' : {}
            });
       
            jQuery('#b_mute').click(function (e) {
                jQuery('#b_mute').hide();
                jQuery('#b_unmute').show();
                _pauseAudio();
                jQuery('#alarm-audio')[0].mute = true;
            });

            jQuery('#b_unmute').click(function (e) {
                jQuery('#b_mute').show();
                jQuery('#b_unmute').hide();
                jQuery('#alarm-audio')[0].mute = false;
            });
            
            jQuery('.f-ack-all').click(function () {
                jQuery.post('/tdc/acknowledgeAll', {}).done(function (data) {
                    yukon.ui.flashSuccess(data.success);
                });
            });    
            
            jQuery(document).on('click', '.f-ack', function () {
                var pointId = jQuery(this).attr("pointId");
                var condition = jQuery(this).attr("condition");
                var args = {};
                args.pointId = pointId;
                args.condition = condition;
                jQuery.post('/tdc/acknowledge', args).done(function (data) {
                    jQuery('#latestData').load('/tdc/refresh');
                });
            });
            jQuery('.f-trend').click(function (event) {
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var pointId = jQuery(this).parent().attr("pointId");
                var url = "/tdc/trend";
                var data = {
                    pointId : pointId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            jQuery('.f-tags').click(function () {
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var pointId = jQuery(this).parent().attr("pointId");
                var deviceId = jQuery(this).parent().attr("deviceId");
                var url = "/tdc/tags";
                var data = {
                    deviceId : deviceId,
                    pointId : pointId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            jQuery('.f-enableDisable').click(function (event) {
                var pointId = jQuery(this).parent().attr("pointId");
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var url = "/tdc/enableDisable";
                var data = {
                    pointId : pointId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 400,
                        autoOpen : true
                    });
                });
            });
            jQuery('.f-manualEntry').click(function (event) {
                var pointId = jQuery(this).parent().attr("pointId");
                var url = "/tdc/manualEntry";
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var data = {
                    pointId : pointId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            jQuery('.f-manualControl').click(function (event) {
                var pointId = jQuery(this).parent().attr("pointId");
                var deviceId = jQuery(this).parent().attr("deviceId");
                var url = "/tdc/manualControl";
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var data = {
                    deviceId : deviceId,
                    pointId : pointId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            jQuery('.f-altScan').click(function (event) {
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var deviceId = jQuery(this).parent().attr("deviceId");
                var deviceName = jQuery(this).parent().attr("deviceName");
                var url = "/tdc/altScanRate";
                var data = {
                    deviceName : deviceName,
                    deviceId : deviceId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 500,
                        autoOpen : true
                    });
                });
            });
            jQuery('.f-display-alarm-ack').click(
				function () {
	   			 var displayId = jQuery(this).attr("displayId");
	   			 var data = {
	        		displayId: displayId
	    		};
	    		jQuery.post('/tdc/acknowledgeAlarmsForDisplay', data)
	        		.done(function (data) {
	            	yukon.ui.flashSuccess(data.success);
	        	});
			});
           jQuery('.f-one-alarm-ack').click(function (event) {
				var pointId = jQuery(this).parent().attr( "pointId");
				var condition = jQuery(this).parent().attr("condition");
                   var data = {
                       pointId: pointId,
                       condition: condition
                   };
                   jQuery("#dropdown_"+pointId).find("ul").hide();
                   jQuery("#dropdown_"+pointId).removeClass("menu-open");
                   jQuery.post('/tdc/acknowledgeAlarm', data).done(function (data) {});
               });
            
            jQuery(document).on('click', '.f-one-alarm-ack-b', function () {
                var pointId = jQuery(this).attr("pointId");
                var condition = jQuery(this).attr("condition");
                        var data = {
                            pointId : pointId,
                            condition : condition
                        };
                        jQuery.post('/tdc/acknowledgeAlarm', data).done(
                                function (data) {
                                });
                    });
            jQuery('.f-mult-alarm-ack').click(function (event) {
                var pointId = jQuery(this).parent().attr("pointId");
                var popupTitle = jQuery(this).parent().attr("popupTitle");
                var url = "/tdc/unacknowledged";
                var data = {
                    pointId : pointId
                };
                jQuery('#tdc-popup').load(url, data, function() {
                    jQuery('#tdc-popup').dialog({
                        title : popupTitle,
                        width : 700,
                        autoOpen : true
                    });
                });
            });
            jQuery(document).on('click', '.f-altScanRate-send', function () {
                jQuery.ajax({
                    url : jQuery("#altScanRateForm").attr("action"),
                    data : jQuery("#altScanRateForm").serialize(),
                    type : "POST",
                    success : function (data) {
                        jQuery('#tdc-popup').dialog('close');
                    }
                });
            });
            jQuery(document).on('click', '.f-manualEntry-send', function() {
                submitFormViaAjax('tdc-popup', 'manualEntryForm');
            });
            
            jQuery(document).on('click', '.f-enableDisable-send', function() {
                jQuery.ajax({
                    url : jQuery("#enableDisableForm").attr("action"),
                    data : jQuery("#enableDisableForm").serialize(),
                    type : "POST",
                    success : function (data) {
                        jQuery('#tdc-popup').dialog('close');
                    }
                });

            });
            
            jQuery(document).on('click', '.f-manualControl-send', function() {
                submitFormViaAjax('tdc-popup', 'manualControlForm');
            });
            
            jQuery(document).on('click', '.f-tags-save', function() {
                jQuery.ajax({
                    url: '/tdc/tagsSave',
                    data: jQuery("#tagsForm").serialize(),
                    type: "POST",
                    success: function(data) {
                        jQuery('#tdc-popup').dialog('close');
                    }
                });
            });
            
            jQuery(document).on('click', '.f-tags-add', function() {
                submitFormViaAjax('tdc-popup', 'tagsForm', '/tdc/tagAdd');
            });
            
            jQuery(document).on('click', '.f-tags-remove', function() {
                var rowIndex = jQuery(this).attr("rowIndex");
                console.info(rowIndex);
                
                jQuery("#rowIndex").val(rowIndex);
                submitFormViaAjax('tdc-popup', 'tagsForm', '/tdc/tagRemove');
            });
            
            jQuery(document).on('click', '.f-ack-alarm', function() {
                var data = {};
                data.pointId = jQuery(this).attr("pointId");
                data.condition = jQuery(this).attr("condition");
                jQuery("#"+data.condition).hide();
                jQuery("#"+data.condition).removeClass("f-ack-alarm");
                jQuery.post('/tdc/acknowledgeAlarm', data).done(function(json) {
                    if (jQuery('.f-ack-alarm').length === 0) jQuery('.f-ack-alarms-for-point').hide();
                });
            });
            
            jQuery(document).on('click', '.f-ack-alarms-for-point', function() {                
                var pointId = jQuery(this).attr("pointId");
                var data = {};
                data.pointId = pointId;
                jQuery.post('/tdc/acknowledgeAlarmsForPoint', data).done(function(data) {  
                    yukon.ui.flashSuccess(data.success);
                    jQuery('#tdc-popup').dialog('close');
                });
            });
        }
    };
    return tdcMod;
}());

jQuery(function () {
    yukon.Tdc.init();
});
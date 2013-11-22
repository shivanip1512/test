var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.DeviceConfig');
Yukon.DeviceConfig = (function () { 
    
    /*******************
     * Private Methods *
     *******************/
    
    var _determineDisplayItemAddButtonVisibility = function() {
        var visibleElems = jQuery("[id^='displayItem']").filter(":visible");
        if (visibleElems.length < 26) {
            jQuery('#showNextDiv').show();
        } else {
            jQuery('#showNextDiv').hide();
        }
    },
    
    _showSandwichedMidnightEntries = function(num) {
        var scheduleVisibles = jQuery("td").filter(function() { 
            return this.id.match('^schedule' + num + '_time\\d+$') && jQuery(this.parentElement).is(':visible');
        });
        scheduleVisibles.last().closest('tr').prevAll().show();
    },
    
    _showSandwichedSlotDisabledEntries = function() {
        var visibleElems = jQuery("[id^='displayItem']").filter(":visible");
        visibleElems.last().closest('div').prevAll().show();
    },
    
    _handleVisibleElemsAndButtons = function() {
        var hiddenElems = jQuery("[id^='displayItem']").filter(":hidden");
        if (hiddenElems.length < 26) {
            jQuery('#showNextDiv').show();
        } else {
            jQuery('#showNextDiv').hide();
        }
        
        for (var i = 1; i < 5; i++) {
            _determineScheduleAddButtonVisibility(i);
        }
    },
    
    _determineScheduleAddButtonVisibility = function(num) {
        var total = jQuery("tr").filter(function() { 
                return jQuery(this).attr("data-schedule-name") === 'schedule' + num; 
            }).length,
            scheduleVisibles = jQuery("tr").filter(function() { 
                return jQuery(this).attr("data-schedule-name") === 'schedule' + num && jQuery(this).is(':visible'); 
            });

        if (scheduleVisibles.length < total) {
            var btn = jQuery('#showNextDivSchedule' + num);
            btn.show();
            jQuery(scheduleVisibles[scheduleVisibles.length - 1].lastElementChild).append(btn);
        } else {
            jQuery('#showNextDivSchedule' + num).hide();
        }
    },
    
    _enableButton = function(button) {
        button.removeAttr('disabled');
        button.find('.busy').hide();
        button.find('.icon').last().show();
    },
    
    _makeAjaxCall = function(method, params, button) {
        var url = '/deviceConfiguration/category/' + method;
        jQuery('#' + button.attr('id')).mouseleave();
        jQuery('#categoryPopup').load(url, params, function() {
            _handleVisibleElemsAndButtons();
            _enableButton(button);
            _registerScheduleButtons();
        });
    },
    
    _registerScheduleButtons = function() {
        jQuery(".f-addScheduleBtn").click(function() {
            var button = jQuery(this),
                num = button.attr('data-add-schedule'),
                hiddenElems = jQuery("tr").filter(function() { 
                    return jQuery(this).attr('data-schedule-name') === "schedule" + num && !jQuery(this).is(':visible');
                });
            
            jQuery(hiddenElems[0]).show();
            _determineScheduleAddButtonVisibility(num);
        });
    },
    
    deviceConfigMod = {
        /******************
         * Public Methods *
         ******************/
        configInit : function() {
            jQuery(function() {
                var typesPopup = jQuery('#supportedTypePopup');
                if (typesPopup.attr('data-show-on-load') === 'true') {
                    typesPopup.load('processAddTypes', {configId : typesPopup.attr('data-config-id')});
                }
            
                jQuery("#addTypeBtn").click(function() {
                    jQuery('#supportedTypePopup').load('processAddTypes', {configId : jQuery(this).attr('data-config-id')});
                });
                
                jQuery(".f-editBtn").click(function() {
                    var btn = jQuery(this),
                        catType = btn.attr('data-category-type'),
                        params = {'categoryId' : jQuery('#categoryId_' + catType).val(),
                                  'configId' : btn.attr('data-config-id') };
                    
                    _makeAjaxCall('editInPlace', params, btn);
                });
                
                jQuery(".f-createBtn").click(function() {
                    var btn = jQuery(this),
                        catType = btn.attr('data-category-type'),
                        params = {'categoryType' : catType, 
                                  'configId' : btn.attr('data-config-id') };
            
                    _makeAjaxCall('createInPlace', params, btn);        
                });
            });
        }, 
        
        changeOut : function(type) {
            var form = jQuery('#categoryChange_' + type);
            form.submit();
            return true;
        }
    };

    jQuery(function() {
        _determineDisplayItemAddButtonVisibility();
        _showSandwichedSlotDisabledEntries();
        
        for (var num = 1; num < 5; num++) {
            _determineScheduleAddButtonVisibility(num);
            _showSandwichedMidnightEntries(num);
        }
        
        jQuery("#showNextFieldBtn").on("click", function() {
            // Show the next hidden display item.
            var hiddenElems = jQuery("[id^='displayItem']").filter(":hidden");
            jQuery('#' + hiddenElems[0].id).removeClass('dn');
            _determineDisplayItemAddButtonVisibility();
        });
        
        jQuery(".f-categories").click(function() {
            var deviceType = jQuery(this).attr('data-device-type');
            jQuery(".pipe").css('visibility', 'hidden');
            jQuery(".pipe[data-device-type-" + deviceType + "]").css('visibility', 'visible');
            jQuery(".pipe[data-device-type=" + deviceType + "]").css('visibility', 'visible');
        });
        
        jQuery("#categoryPopup").on("click", function(event) {
            if (jQuery(event.target).closest('#showNextFieldBtn').length > 0) {
                // Show the next hidden display item.
                var hiddenElems = jQuery("[id^='displayItem']").filter(":hidden"),
                    visibleElems = jQuery("[id^='displayItem']").filter(":visible");
                jQuery('#' + hiddenElems[0].id).removeClass('dn');
                if (visibleElems.length < 26) {
                    jQuery('#showNextDiv').show();
                } else {
                    jQuery('#showNextDiv').hide();
                }
            }
        });
        
        _registerScheduleButtons();
        
        // Find the first type and select his categories
        var pipe = jQuery(".pipe").get(0);
        if (pipe) {
            var deviceType = pipe.attributes["data-device-type"].value;
            jQuery('.pipe[data-device-type-' + deviceType + ']').css('visibility', 'visible');
            jQuery(pipe).css('visibility', 'visible');
        }
    });
    
    return deviceConfigMod;
}());

jQuery(function() {
    Yukon.DeviceConfig.configInit();
});
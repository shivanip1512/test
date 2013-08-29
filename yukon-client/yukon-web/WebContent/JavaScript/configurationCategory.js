var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});
Yukon.namespace('Yukon.DeviceConfig');
Yukon.DeviceConfig = (function () { 
    
    /*******************
     * Private Methods *
     *******************/
    
    var _determineDisplayItemAddButtonVisibility = function() {
        var visibleElems = jQuery("[id^='displayItem']").filter(function() { return !this.classList.contains('dn'); });
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
        var visibleElems = jQuery("[id^='displayItem']").filter(function() { return !this.classList.contains('dn'); });
        visibleElems.last().closest('div').prevAll().show();
    },
    
    _handleVisibleElemsAndButtons = function() {
        var hiddenElems = jQuery("[id^='displayItem']").filter(function() { return this.classList.contains('dn'); });
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
        var regex = '^schedule' + num + '_time\\d+$';
        var total = jQuery("td").filter(function() { return this.id.match(regex); }).length;
        var schedule1visibles = jQuery("td").filter(function() { return this.id.match(regex) && jQuery(this.parentElement).is(':visible'); });
        if (schedule1visibles.length < total) {
            var btn = jQuery('#showNextDivSchedule' + num);
            btn.show();
            jQuery('#' + schedule1visibles[schedule1visibles.length - 1].id).parent().append(btn);
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
        jQuery('#' + button.attr('id')).mouseleave();
        var temp = '/deviceConfiguration/category/' + method;
        jQuery('#categoryPopup').load(temp, params, function() {
            _handleVisibleElemsAndButtons();
            _enableButton(button);
        });
    },
    
    deviceConfigMod = {
        /******************
         * Public Methods *
         ******************/
        configInit : function(config) {
            jQuery(function() {
                if (config.supportedTypesEmpty && config.mode === 'VIEW') {
                    jQuery('#supportedTypePopup').load('processAddTypes', {configId : config.configId});
                }
            
                jQuery("#addTypeBtn").click(function() {
                    jQuery('#supportedTypePopup').load('processAddTypes', {configId : config.configId});
                });
                
                jQuery(".f-editBtn").click(function() {
                    var catTypeClass = jQuery(this).attr('class').match(/f-devType-\w*/)[0].split('-');
                    var params = {'categoryId' : jQuery('#categoryId_' + catTypeClass[catTypeClass.length - 1]).val(),
                                  'configId' : config.configId };
                    
                    _makeAjaxCall('editInPlace', params, jQuery(this));
                });
                
                jQuery(".f-createBtn").click(function() {
                    var catTypeClass = jQuery(this).attr('class').match(/f-devType-\w*/)[0].split('-');
                    var params = {'categoryType' : catTypeClass[catTypeClass.length - 1], 
                                  'configId' : config.configId };
            
                    _makeAjaxCall('createInPlace', params, jQuery(this));        
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
            var hiddenElems = jQuery("[id^='displayItem']").filter(function() { return this.classList.contains('dn'); });
            jQuery('#' + hiddenElems[0].id).removeClass('dn');
            _determineDisplayItemAddButtonVisibility();
        });
        
        jQuery(".f-categories").click(function() {
            jQuery(".pipe").css('visibility', 'hidden');
            var devTypeClass = jQuery(this).attr('class').match(/f-devtype-\w*/)[0];
            jQuery('.' + devTypeClass + '.pipe').css('visibility', 'visible');
        });
        
        jQuery("#categoryPopup").on("click", function(event) {
            if (jQuery(event.target).closest('#showNextFieldBtn').length > 0) {
                // Show the next hidden display item.
                var hiddenElems = jQuery("[id^='displayItem']").filter(function() { return this.classList.contains('dn'); });
                jQuery('#' + hiddenElems[0].id).removeClass('dn');
                var visibleElems = jQuery("[id^='displayItem']").filter(function() { return !this.classList.contains('dn'); });
                if (visibleElems.length < 26) {
                    jQuery('#showNextDiv').show();
                } else {
                    jQuery('#showNextDiv').hide();
                }
            }
        });
        
        jQuery(".f-addScheduleBtn").click(function() {
            var button = jQuery(this);
            var num = button.attr('data-add-schedule');
            var regex = '^schedule' + num + '_time\\d+$';
            var hiddenElems = jQuery("td").filter(function() { return this.id.match(regex) && !jQuery(this.parentElement).is(':visible'); });
            jQuery('#' + hiddenElems[0].id).parent().show();
            _determineScheduleAddButtonVisibility(num);
        });
        
        // Find the first type and select his categories
        var pipe = jQuery(".pipe").get(0);
        if (pipe) {
            var devTypeClass = jQuery(pipe).attr('class').match(/f-devtype-\w*/)[0];
            jQuery('.' + devTypeClass + '.pipe').css('visibility', 'visible');
            jQuery(pipe).css('visibility', 'visible');
        }
    });
    
    return deviceConfigMod;
}());
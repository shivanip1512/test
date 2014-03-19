
yukon.namespace('yukon.deviceConfig');

yukon.deviceConfig = (function () {
    
    /*******************
     * Private Methods *
     *******************/
    
    var _determineDisplayItemAddButtonVisibility = function() {
        var visibleElems = $("[id^='displayItem']").filter(":visible");
        if (visibleElems.length < 26) {
            $('#showNextDiv').show();
        } else {
            $('#showNextDiv').hide();
        }
    },
    
    _showSandwichedMidnightEntries = function(num) {
        var scheduleVisibles = $("td").filter(function() { 
            return this.id.match('^schedule' + num + '_time\\d+$') && $(this.parentElement).is(':visible');
        });
        scheduleVisibles.last().closest('tr').prevAll().show();
    },
    
    _showSandwichedSlotDisabledEntries = function() {
        var visibleElems = $("[id^='displayItem']").filter(":visible");
        visibleElems.last().closest('div').prevAll().show();
    },
    
    _handleVisibleElemsAndButtons = function() {
        var hiddenElems = $("[id^='displayItem']").filter(":hidden");
        if (hiddenElems.length < 26) {
            $('#showNextDiv').show();
        } else {
            $('#showNextDiv').hide();
        }
        
        for (var i = 1; i < 5; i++) {
            _determineScheduleAddButtonVisibility(i);
        }
    },
    
    _determineScheduleAddButtonVisibility = function(num) {
        var total = $("tr").filter(function() { 
                return $(this).attr("data-schedule-name") === 'schedule' + num; 
            }).length,
            scheduleVisibles = $("tr").filter(function() { 
                return $(this).attr("data-schedule-name") === 'schedule' + num && $(this).is(':visible'); 
            });

        if (scheduleVisibles.length < total) {
            var btn = $('#showNextDivSchedule' + num);
            btn.show();
            $(scheduleVisibles[scheduleVisibles.length - 1].lastElementChild).append(btn);
        } else {
            $('#showNextDivSchedule' + num).hide();
        }
    },
    
    _enableButton = function(button) {
        button.removeAttr('disabled');
        button.find('.busy').hide();
        button.find('.icon').last().show();
    },
    
    _makeAjaxCall = function(method, params, button) {
        var url = '/deviceConfiguration/category/' + method;
        $('#' + button.attr('id')).mouseleave();
        $('#categoryPopup').load(url, params, function() {
            _handleVisibleElemsAndButtons();
            _enableButton(button);
            _registerScheduleButtons();
        });
    },
    
    _registerScheduleButtons = function() {
        $(".f-addScheduleBtn").click(function() {
            var button = $(this),
                num = button.attr('data-add-schedule'),
                hiddenElems = $("tr").filter(function() { 
                    return $(this).attr('data-schedule-name') === "schedule" + num && !$(this).is(':visible');
                });
            
            $(hiddenElems[0]).show();
            _determineScheduleAddButtonVisibility(num);
        });
    },

    _hidingMap = [
        {
            keyHolder: '#disconnectMode',
            valueHideMap : [
                {
                    value : 'ON_DEMAND',
                    hide : ['#demandInterval', '#disconnectDemandThreshold', '#disconnectLoadLimitConnectDelay', '#maxDisconnects', '#disconnectMinutes', '#connectMinutes'],
                    show : ['#reconnectParam']
                },
                {
                    value : 'DEMAND_THRESHOLD',
                    hide : ['#disconnectMinutes', '#connectMinutes'],
                    show : ['#reconnectParam', '#demandInterval', '#disconnectDemandThreshold', '#disconnectLoadLimitConnectDelay', '#maxDisconnects']
                },
                {
                    value : 'CYCLING',
                    hide : ['#reconnectParam', '#demandInterval', '#disconnectDemandThreshold', '#disconnectLoadLimitConnectDelay', '#maxDisconnects'],
                    show : ['#disconnectMinutes', '#connectMinutes']
                }
            ]
        }
    ],

    _hideShowElement = function (hidingMapEntry, timeout) {
        var value = $(hidingMapEntry.keyHolder).find(':input').val(),
            currentEntry,
            ii,
            jj,
            elem;

        if (typeof timeout !== 'number') {
            timeout = 0;
        }

        for (ii = 0; ii < hidingMapEntry.valueHideMap.length; ii += 1) {
            currentEntry = hidingMapEntry.valueHideMap[ii];
            if (value === currentEntry.value) {
                for (jj = 0; jj < currentEntry.hide.length; jj += 1) {
                    elem = currentEntry.hide[jj];
                    $(elem).slideUp(timeout);
                }
                for (jj = 0; jj < currentEntry.show.length; jj += 1) {
                    elem = currentEntry.show[jj];
                    $(elem).slideDown(timeout);
                }
            }
        }
    },

    _hideThingsInMap = function () {
        var currentEntry,
            ii,
            input;

        for (ii = 0; ii < _hidingMap.length; ii += 1) {
            currentEntry = _hidingMap[ii];
            input = $(currentEntry.keyHolder).find(':input');
            _hideShowElement(currentEntry, 0);

            $(document).on('change', input, function () {
                _hideShowElement(currentEntry, 200);
            });
        }
    },

    mod = {
        /******************
         * Public Methods *
         ******************/
        configInit : function() {
            $(function() {
                var typesPopup = $('#supportedTypePopup');
                if (typesPopup.attr('data-show-on-load') === 'true') {
                    typesPopup.load('processAddTypes', {configId : typesPopup.attr('data-config-id')});
                }
            
                $("#addTypeBtn").click(function() {
                    $('#supportedTypePopup').load('processAddTypes', {configId : $(this).attr('data-config-id')});
                });
                
                $(".f-editBtn").click(function() {
                    var btn = $(this),
                        catType = btn.attr('data-category-type'),
                        params = {'categoryId' : $('#categoryId_' + catType).val(),
                                  'configId' : btn.attr('data-config-id') };
                    
                    _makeAjaxCall('editInPlace', params, btn);
                });
                
                $(".f-createBtn").click(function() {
                    var btn = $(this),
                        catType = btn.attr('data-category-type'),
                        params = {'categoryType' : catType, 
                                  'configId' : btn.attr('data-config-id') };
            
                    _makeAjaxCall('createInPlace', params, btn);        
                });

                _hideThingsInMap();
            });
        }, 
        
        changeOut : function(type) {
            var form = $('#categoryChange_' + type);
            form.submit();
            return true;
        }
    };

    $(function() {
        _determineDisplayItemAddButtonVisibility();
        _showSandwichedSlotDisabledEntries();
        
        for (var num = 1; num < 5; num++) {
            _determineScheduleAddButtonVisibility(num);
            _showSandwichedMidnightEntries(num);
        }
        
        $("#showNextFieldBtn").on("click", function() {
            // Show the next hidden display item.
            var hiddenElems = $("[id^='displayItem']").filter(":hidden");
            $('#' + hiddenElems[0].id).removeClass('dn');
            _determineDisplayItemAddButtonVisibility();
        });
        
        $(".f-categories").click(function() {
            var deviceType = $(this).attr('data-device-type');
            $(".pipe").css('visibility', 'hidden');
            $(".pipe[data-device-type-" + deviceType + "]").css('visibility', 'visible');
            $(".pipe[data-device-type=" + deviceType + "]").css('visibility', 'visible');
        });
        
        $("#categoryPopup").on("click", function(event) {
            if ($(event.target).closest('#showNextFieldBtn').length > 0) {
                // Show the next hidden display item.
                var hiddenElems = $("[id^='displayItem']").filter(":hidden"),
                    visibleElems = $("[id^='displayItem']").filter(":visible");
                $('#' + hiddenElems[0].id).removeClass('dn');
                if (visibleElems.length < 26) {
                    $('#showNextDiv').show();
                } else {
                    $('#showNextDiv').hide();
                }
            }
        });
        
        _registerScheduleButtons();
        
        // Find the first type and select his categories
        var pipe = $(".pipe").get(0);
        if (pipe) {
            var deviceType = pipe.attributes["data-device-type"].value;
            $('.pipe[data-device-type-' + deviceType + ']').css('visibility', 'visible');
            $(pipe).css('visibility', 'visible');
        }
    });
    
    return mod;
}());

$(function() {
    yukon.deviceConfig.configInit();
});
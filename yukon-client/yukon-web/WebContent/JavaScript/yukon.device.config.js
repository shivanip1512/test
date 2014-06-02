yukon.namespace('yukon.deviceConfig');

/** 
 * This module handles behavior on the device configuration pages.
 * @module yukon.deviceConfig 
 */
yukon.deviceConfig = (function () {

    var _determineDisplayItemAddButtonVisibility = function() {
        var totalElems = $('[data-display-item]'),
            visibleElems = totalElems.filter(":visible");
        
        if (visibleElems.length < totalElems.length) {
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
        var visibleElems = $('[data-display-item]').filter(":visible");
        visibleElems.last().closest('div').prevAll().show();
    },
    
    _hideSlotDisabledEntries = function() {
        var displayItems = $('[data-display-item]'),
            filter = function() {
                return $(this).find(':input').val() !== '0';
            },
            filteredItems = displayItems.filter(filter);
            
        if(filteredItems.length > 0) {
            //edit
            $(displayItems.splice(filteredItems.last().index()+1)).hide();
        } else {
            //create
            $(displayItems.splice(1)).hide();
        }
    },
    
    _handleVisibleElemsAndButtons = function() {
        var totalElems = $('[data-display-item]'),
            hiddenElems = totalElems.filter(":hidden");
        
        if (hiddenElems.length <= totalElems.length) {
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
        
        /** Initialize the module. Depends on DOM elements so call after page load. */
        init : function() {
            
            var typesLink = $('[data-show-device-types]');
            if (typesLink.data('showDeviceTypes') === true) {
                typesLink.trigger('click');
            }
            
            /** Edit button clicked for category, show category edit popup. */
            $('.f-edit-category').click(function(ev) {
                
                var btn = $(this),
                    categoryId = $('#categoryId_' + btn.data('categoryType')).val(),
                    configId = btn.data('configId'),
                    url = yukon.url('/deviceConfiguration/category/editInPlace?categoryId=' 
                        + categoryId + '&configId=' + configId);
                
                $('#category-popup').load(url, function() {
                    yukon.ui.unbusy(btn);
                    _hideSlotDisabledEntries();
                    _showSandwichedSlotDisabledEntries();
                    _handleVisibleElemsAndButtons();
                    _registerScheduleButtons();
                    _hideThingsInMap();
                    var title = $('#popup-title').val(),
                        buttons = yukon.ui.buttons({ okText: yg.text.save, event: 'yukon.deviceConfigs.category.save' });
                    $('#category-popup').dialog({ width: 900, height: 600, title: title, buttons: buttons });
                });
            });
            
            /** Save button click on cateogry edit or create popup. Post form and handle results */
            $('#category-popup').on('yukon.deviceConfigs.category.save', function(ev) {
                $('#category-form').ajaxSubmit({
                    type: 'post',
                    success: function(data, status, xhr, $form) {
                        
                        $('#category-popup').dialog('close');
                        // TODO add message
                        
                    },
                    error: function(xhr, status, error, $form) {
                        $('#category-popup').html(xhr.responseText);
                        _hideThingsInMap();
                        _hideSlotDisabledEntries();
                        _showSandwichedSlotDisabledEntries();
                    }
                });
            });
            
            /** Create button clicked for category, show category create popup. */
            $(".f-create-category").click(function(ev) {
                var btn = $(this),
                    type = btn.data('categoryType'),
                    configId = btn.data('configId'),
                    url = yukon.url('/deviceConfiguration/category/createInPlace?categoryType=' 
                        + type + '&configId=' + configId);
            
                $('#category-popup').load(url, function() {
                    yukon.ui.unbusy(btn);
                    _handleVisibleElemsAndButtons();
                    _hideSlotDisabledEntries();
                    _registerScheduleButtons();
                    _hideThingsInMap();
                    var title = $('#popup-title').val(),
                        buttons = yukon.ui.buttons({ okText: yg.text.save, event: 'yukon.deviceConfigs.category.save' });
                    $('#category-popup').dialog({ width: 900, height: 600, title: title, buttons: buttons });
                });
            });

            _hideThingsInMap();
            _hideSlotDisabledEntries();
            _determineDisplayItemAddButtonVisibility();
            _showSandwichedSlotDisabledEntries();
            
            for (var num = 1; num < 5; num++) {
                _determineScheduleAddButtonVisibility(num);
                _showSandwichedMidnightEntries(num);
            }
            
            $("#showNextFieldBtn").on("click", function() {
                // Show the next hidden display item.
                var hiddenElems = $('[data-display-item]').filter(":hidden");
                $(hiddenElems[0]).show();
                _determineDisplayItemAddButtonVisibility();
            });
            
            $(".f-categories").click(function() {
                var deviceType = $(this).attr('data-device-type');
                $(".pipe").css('visibility', 'hidden');
                $(".pipe[data-device-type-" + deviceType + "]").css('visibility', 'visible');
                $(".pipe[data-device-type=" + deviceType + "]").css('visibility', 'visible');
            });
            
            $("#category-popup").on("click", function(event) {
                if ($(event.target).closest('#showNextFieldBtn').length > 0) {
                    // Show the next hidden display item.
                    var hiddenElems = $('[data-display-item]').filter(":hidden"),
                        visibleElems = $('[data-display-item]').filter(":visible");
                    if(hiddenElems.length < $('[data-display-item]').length) {
                        $(hiddenElems[0]).show();
                    }
                    if (visibleElems.length + 1 < $('[data-display-item]').length) {
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
        }, 
        
        changeOut : function(type) {
            var form = $('#categoryChange_' + type);
            form.submit();
            return true;
        }
    };

    return mod;
}());

$(function() { yukon.deviceConfig.init(); });
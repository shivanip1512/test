yukon.namespace('yukon.deviceConfig');

/**
 * This module handles behavior on the device configuration pages.
 * @module yukon.deviceConfig
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.deviceConfig = (function () {
    'use strict';

    /** Show and hide the div that have add button */
    var _determineDisplayItemAddButtonVisibility = function () {
        var totalElems = $('[data-display-item]'),
            visibleElems = totalElems.filter(":visible");

        if (visibleElems.length < totalElems.length) {
            $('.js-show-next').show();
        } else {
            $('.js-show-next').hide();
        }
    },

        /**
         * Filter and shows the midnight entries.
         * @param {Object} num - number.
         */
        _showSandwichedMidnightEntries = function (num) {
            var scheduleVisibles = $("td").filter(function () {
                return this.id.match('^schedule' + num + '_time\\d+$') && $(this.parentElement).is(':visible');
            });
            scheduleVisibles.last().closest('tr').prevAll().show();
        },

        /** Shows the entries that are before slot disabled entries */
        _showSandwichedSlotDisabledEntries = function () {
            var visibleElems = $('[data-display-item]').filter(":visible");
            visibleElems.last().closest('div').prevAll().show();
        },

        /** Hides the entries having slot disabled */
        _hideSlotDisabledEntries = function () {
            var displayItems = $('[data-display-item]'),
                filter = function () {
                    return $(this).find(':input').val() !== '0';
                },
                filteredItems = displayItems.filter(filter);

            if (filteredItems.length > 0) {
                //edit
                $(displayItems.splice(filteredItems.last().index() + 1)).hide();
            } else {
                //create
                $(displayItems.splice(1)).hide();
            }
        },

        /**
         * Determines the visibility of the schedule (Add Rate) button
         * @param {Object} num - number.
         */
        _determineScheduleAddButtonVisibility = function (num) {
            var allSchedules = $("tr").filter(function () {
                    return $(this).data('scheduleName') === 'schedule' + num;
                }),
                visableSchedules = allSchedules.filter(':visible'),
                btn;

            if (visableSchedules.length < allSchedules.length) {
                btn = $('.js-add-schedule').filter(function (idx, elem) {
                    return $(elem).is('[data-schedule="' + num + '"]');
                });
                btn.show();

                visableSchedules.last().find(':last').append(btn);

            } else {
                $('.js-add-schedule').filter(function (idx, elem) {
                    return $(elem).is('[data-schedule="' + num + '"]');
                }).hide();
            }
        },

        /**
         * Show/hide the div having the add button and set the visibility of
         * schedule button.
         */
        _handleVisibleElemsAndButtons = function () {
            var totalElems = $('[data-display-item]'),
                hiddenElems = totalElems.filter(":hidden"),
                i;

            if (hiddenElems.length <= totalElems.length) {
                $('.js-show-next').show();
            } else {
                $('.js-show-next').hide();
            }

            for (i = 1; i <= 4; i += 1) {
                _determineScheduleAddButtonVisibility(i);
                _showSandwichedMidnightEntries(i);
            }
        },

        /** Shows the schedule (Add Rate) button */
        _registerScheduleButtons = function () {
            $(".js-add-schedule").click(function () {
                var button = $(this),
                    num = button.data('schedule'),
                    hiddenElems = $("tr").filter(function () {
                        return $(this).data('scheduleName') === "schedule" + num && !$(this).is(':visible');
                    });

                hiddenElems.first().show();
                _determineScheduleAddButtonVisibility(num);
            });
        },

        _hidingMap = [
            {
                keyHolder: 'disconnectMode',
                valueHideMap : [
                    {
                        value : 'ON_DEMAND',
                        hide : ['disconnectDemandInterval', 'disconnectDemandThreshold', 'disconnectLoadLimitConnectDelay', 'maxDisconnects', 'disconnectMinutes', 'connectMinutes'],
                        show : ['reconnectParam']
                    },
                    {
                        value : 'DEMAND_THRESHOLD',
                        hide : ['disconnectMinutes', 'connectMinutes'],
                        show : ['reconnectParam', 'disconnectDemandInterval', 'disconnectDemandThreshold', 'disconnectLoadLimitConnectDelay', 'maxDisconnects']
                    },
                    {
                        value : 'CYCLING',
                        hide : ['reconnectParam', 'disconnectDemandInterval', 'disconnectDemandThreshold', 'disconnectLoadLimitConnectDelay', 'maxDisconnects'],
                        show : ['disconnectMinutes', 'connectMinutes']
                    }
                ]
            }
        ],

        /**
         * Hide/show elements in map.
         * @param {Object} hidingMapEntry - Map entry.
         * @param {Object} timeout - timeout for slideup() and slidedown().
         */
        _hideShowElement = function (hidingMapEntry, timeout) {
            var value = $('[data-field="' + hidingMapEntry.keyHolder + '"]').find(':input').val(),
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
                        $('[data-field="' + elem + '"]').slideUp(timeout);
                    }
                    for (jj = 0; jj < currentEntry.show.length; jj += 1) {
                        elem = currentEntry.show[jj];
                        $('[data-field="' + elem + '"]').slideDown(timeout);
                    }
                }
            }
        },

        /** Hide elements in map */
        _hideThingsInMap = function () {
            var currentEntry,
                ii,
                input;

            for (ii = 0; ii < _hidingMap.length; ii += 1) {
                currentEntry = _hidingMap[ii];
                input = $('[data-field="' + currentEntry.keyHolder + '"]').find(':input');
                _hideShowElement(currentEntry, 0);

                $(document).on('change', input, function () {
                    _hideShowElement(currentEntry, 200);
                });
            }
        },

        _initFields = function () {

            yukon.ui.initChosen();

            _handleVisibleElemsAndButtons();
            _hideSlotDisabledEntries();
            _showSandwichedSlotDisabledEntries();
            _registerScheduleButtons();
            _determineDisplayItemAddButtonVisibility();
            _hideThingsInMap();
        },

        /**
         * Open category popup page when create/edit category button is clicked from
         * configuration page.
         * @param {Object} btn - Reference of button clicked.
         * @param {Object} url - url of page to open.
         */
        _openCategoryPopup = function (btn, url) {
            $('#category-popup').load(url, function () {
                var title = $('#popup-title').val(),
                    buttons = yukon.ui.buttons({ okText: yg.text.save, event: 'yukon.deviceConfigs.category.save' });

                $('#category-popup').dialog({ width: 900, height: 600, title: title, buttons: buttons });
                _initFields();
                yukon.ui.unbusy(btn);
            });
        },

        /**
         * Show pipes for the device type associated with the input
         * @param {jQuery} elem - Element with data "deviceType" on it.
         */
        _setPipeVisibilityForType = function (elem) {

            var deviceType = $(elem).data('deviceType');

            $('.pipe').invisible();
            $('.pipe[data-device-type-' + deviceType + ']').visible();
        },


        mod = {

            /** Initialize the module. Depends on DOM elements so call after page load. */
            init : function () {

                var typesLink = $('[data-show-device-types]');

                if (typesLink.data('showDeviceTypes') === true) {
                    typesLink.trigger('click');
                }

                /** Edit button clicked for category, show category edit popup. */
                $('.js-edit-category').click(function (ev) {

                    var btn = $(this),
                        categoryId = $('#categoryId_' + btn.data('categoryType')).val(),
                        configId = btn.data('configId'),
                        url = yukon.url('/deviceConfiguration/category/editInPlace?categoryId='
                            + categoryId + '&configId=' + configId);

                    _openCategoryPopup(btn, url);
                });

                /** Save button click on cateogry edit or create popup. Post form and handle results */
                $('#category-popup').on('yukon.deviceConfigs.category.save', function (ev) {
                    $('#category-form').ajaxSubmit({
                        type: 'post',
                        success: function (data, status, xhr, $form) {

                            $('#category-popup').dialog('close');
                            window.location.reload();
                            // TODO add message

                        },
                        error: function (xhr, status, error, $form) {
                            $('#category-popup').html(xhr.responseText);
                            _initFields();
                        }
                    });
                });

                /** Create button clicked for category, show category create popup. */
                $(".js-create-category").click(function (ev) {
                    var btn = $(this),
                        type = btn.data('categoryType'),
                        configId = btn.data('configId'),
                        url = yukon.url('/deviceConfiguration/category/createInPlace?categoryType='
                            + type + '&configId=' + configId);

                    _openCategoryPopup(btn, url);
                });

                $(document).on('change', 'input[data-channel-read]', function () {
                    var buttons = $('.button-group [data-value]'),
                        inputs = $('.button-group input[data-channel-read]'),
                        intervals = buttons.filter(function () {return $(this).data('value') === 'INTERVAL'; }),
                        midnights = buttons.filter(function () {return $(this).data('value') === 'MIDNIGHT'; }),
                        numIntervals = inputs.filter(function () {return $(this).val() === 'INTERVAL'; }).length,
                        numMidnights = inputs.filter(function () {return $(this).val() === 'MIDNIGHT'; }).length;

                    if (numIntervals >= 15) {
                        intervals.not('.on').prop('disabled', true);
                        $('.js-reporting').show();
                    } else {
                        intervals.not('.on').prop('disabled', false);
                    }
                    if (numIntervals + numMidnights >= 80) {
                        intervals.add(midnights).each(function () {
                            if ($(this).siblings('input[data-channel-read]').val() === 'DISABLED') {
                                $(this).prop('disabled', true);
                            }
                        });
                        $('.js-midnight').show();
                    } else {
                        midnights.not('.on').prop('disabled', false);
                    }
                });

                _initFields();

                // Find the first type and select his categories
                _setPipeVisibilityForType($(".js-categories").first());

                $(".js-categories").click(function () {
                    _setPipeVisibilityForType(this);
                });

                // Show the next hidden display item.
                $(document).on('click', '.js-show-next', function (event) {

                    var btn = $(this),
                        hiddenElems = $('[data-display-item]').filter(":hidden"),
                        container = btn.closest('.ui-dialog-content');

                    if (!container.length) { container = $('html'); }

                    hiddenElems.first().slideDown(200);
                    container.scrollTo(btn, 100);

                    if (hiddenElems.length > 1) {
                        $('.js-show-next').show();
                    } else {
                        $('.js-show-next').hide();
                    }
                });

            },

            changeOut : function (type) {
                var form = $('#categoryChange_' + type);
                form.submit();
                return true;
            }
        };

    return mod;
}());

$(function () { yukon.deviceConfig.init(); });
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
    var
    _actionOnFormWith = function (selctor, action) {

        var forms = $('form').filter(function (idx, form) {
            return $(form).find(selctor).length;
        });

        forms.each(function (idx, form) {
            action($(form));
        });
    },

    /** Hides entries after the last non-disabled entry */
    _setupDisplayItems = function  () {

        _actionOnFormWith('.js-display-item', function (form) {

            var displayItems = form.find('.js-display-item');

            var lastShownIdx = 0;

            displayItems.each(function (idx, displayItem) {
                var value = $(displayItem).find(':input').val();
                if (value !== 'SLOT_DISABLED' && value !== '0' ) {
                    lastShownIdx = idx;
                }
            });

            $(displayItems.slice(lastShownIdx + 1)).hide();


            if (displayItems.length > lastShownIdx + 1) {
                form.find('.js-show-next').show();
            } else {
                form.find('.js-show-next').hide();
            }

        });
    },

    /**
     * Filter and shows the midnight entries.
     * @param {Object} num - number.
     */
    _showSandwichedMidnightEntries = function () {

        _actionOnFormWith('.js-rate-schedule .js-rate-schedule-entry', function (form) {

            form.find('.js-rate-schedule').each(function (idx, table) {

                var entries = $(table).find('.js-rate-schedule-entry');

                var lastVisibleIdx = 0;

                entries.each(function (idx, entry) {
                    if ($(entry).is(':visible')) { lastVisibleIdx = idx; }
                });

                $(entries.slice(0, lastVisibleIdx + 1)).show();
            });

        });
    },

    /**
     * Determines the visibility of the schedule (Add Rate) button
     * @param {Object} num - number.
     */
    _determineScheduleAddButtonVisibility = function (form) {

        form.find('.js-rate-schedule').each(function (idx, table) {

            table = $(table);

            var scheduleName = table.data('scheduleName');

            var entries = table.find('.js-rate-schedule-entry');
            var visibleEntries = entries.filter(':visible');

            var btn = form.find('.js-add-schedule').filter(function (idx, elem) {
                return $(elem).is('[data-schedule-name="' + scheduleName + '"]');
            });

            if (visibleEntries.length < entries.length) {
                visibleEntries.last().find('.js-add-button-column').append(btn);
                btn.show();
            } else {
                btn.hide();
            }
        });
    },

    /** Shows the schedule (Add Rate) button */
    _registerScheduleButtons = function () {

        $(".js-add-schedule").off('click.add-schedule').on('click.add-schedule', function () {

            var button = $(this);
            var form = button.closest('form');
            var scheduleName = button.data('scheduleName');

            var table = form.find('.js-rate-schedule').filter(function (idx, elem) {
                return $(elem).is('[data-schedule-name="' + scheduleName + '"]');
            });

            table.find('.js-rate-schedule-entry').filter(':hidden:first').show();

            _determineScheduleAddButtonVisibility(form);
        });

        _actionOnFormWith('.js-add-schedule', function (form) {

            _determineScheduleAddButtonVisibility(form);

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
        },
        {
            keyHolder: 'heartbeatMode',
            valueHideMap : [
                {
                    value : 'NONE',
                    hide : ['heartbeatPeriod', 'heartbeatValue'],
                    show : []
                },
                {
                    value : 'INCREMENT',
                    hide : ['heartbeatValue'],
                    show : ['heartbeatPeriod']
                },
                {
                    value : 'COUNTDOWN',
                    hide : [],
                    show : ['heartbeatValue', 'heartbeatPeriod']
                },
            ]
        },
        {
            keyHolder: 'enableDataStreaming',
            valueHideMap : [
                {
                    value : 'true',
                    hide : [],
                    show : ['voltageDataStreamingIntervalMinutes']
                },
                {
                    value : 'false',
                    hide : ['voltageDataStreamingIntervalMinutes'],
                    show : []
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

        if (typeof timeout !== 'number') {
            timeout = 0;
        }

        _actionOnFormWith('[data-field="' + hidingMapEntry.keyHolder +'"]', function (form) {

            var field = form.find('[data-field="' + hidingMapEntry.keyHolder + '"]');

            if (!field.is(':visible')) return;

            var value;
            if (field.find('[data-input]').length) {
                value = field.find('[data-input]').val();
            } else {
                value = field.find(':input').val();
            }

            hidingMapEntry.valueHideMap.forEach(function (currentEntry) {
                if (value === currentEntry.value) {
                    currentEntry.hide.forEach(function (fieldName) {
                        form.find('[data-field="' + fieldName + '"]').slideUp(timeout);
                    });
                    currentEntry.show.forEach(function (fieldName) {
                        form.find('[data-field="' + fieldName + '"]').slideDown(timeout);
                    });
                }
            });

        });

    },

    /** Hide elements in map */
    _hideThingsInMap = function () {

        _hidingMap.forEach(function (currentEntry) {

            var input = $('[data-field="' + currentEntry.keyHolder + '"]').find(':input');
            _hideShowElement(currentEntry, 0);

            $(document).on('change', input, _hideShowElement.bind(null, currentEntry, 200));
        });
    },

    _initFields = function () {

        _setupDisplayItems();
        _showSandwichedMidnightEntries();
        _registerScheduleButtons();
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
            yukon.ui.initContent($('#category-popup'));
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
                        yukon.ui.initContent($('#category-popup'));
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
                    intervals = buttons.filter(function () {return $(this).data('value') === 'INTERVAL' && !$(this).is(".peakPoint"); }),
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

            $(document).on('yukon:device:config:quick-view:loaded', function () {
                _initFields();
            });

            // Find the first type and select his categories
            _setPipeVisibilityForType($(".js-categories").first());

            $(".js-categories").click(function () {
                _setPipeVisibilityForType(this);
            });

            // Show the next hidden display item.
            $(document).on('click', '.js-show-next', function (event) {

                var btn = $(this),
                    hiddenElems = btn.closest('form').find('.js-display-item').filter(":hidden"),
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
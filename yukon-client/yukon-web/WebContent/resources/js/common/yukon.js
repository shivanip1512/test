/** POLYFILLS */ 

/** Array.forEach */
if (!Array.prototype.forEach) {
    Array.prototype.forEach = function forEach(callback, thisArg) {
        'use strict';
        var T, k;
        
        if (this == null) {
            throw new TypeError('this is null or not defined');
        }
        
        var kValue,
            O = Object(this),
            len = O.length >>> 0;
            
        if ({}.toString.call(callback) !== '[object Function]') {
            throw new TypeError(callback + ' is not a function');
        }
        if (arguments.length >= 2) {
            T = thisArg;
        }
        k = 0;
        while (k < len) {
            if (k in O) {
                kValue = O[k];
                callback.call(T, kValue, k, O);
            }
            k++;
        }
    };
}

/** Object.create */
if (!Object.create) {
    Object.create = (function () {
        function F() {};

        return function (o) {
            if (arguments.length != 1) {
                throw new Error('Object.create implementation only accepts one parameter.');
            }
            F.prototype = o;
            return new F();
        };
    })();
}

/** Function.prototype.bind */
if (!Function.prototype.bind) {
    Function.prototype.bind = function (oThis) {
        
        if (typeof this !== 'function') {
            // closest thing possible to the ECMAScript 5 internal IsCallable function
            throw new TypeError('Function.prototype.bind - what is trying to be bound is not callable');
        }
        
        var 
        aArgs = Array.prototype.slice.call(arguments, 1), 
        fToBind = this, 
        fNOP = function () {},
        fBound = function () {
            return fToBind.apply(this instanceof fNOP && oThis ? this : oThis, 
                aArgs.concat(Array.prototype.slice.call(arguments)));
        };
        
        fNOP.prototype = this.prototype;
        fBound.prototype = new fNOP();
        
        return fBound;
    };
}

/** String.startsWith */
if (!String.prototype.startsWith) {
    Object.defineProperty(String.prototype, 'startsWith', {
        enumerable: false,
        configurable: false,
        writable: false,
        value: function (searchString, position) {
            position = position || 0;
            return this.lastIndexOf(searchString, position) === position;
        }
    });
}

/** String.endsWith */
if (!String.prototype.endsWith) {
    Object.defineProperty(String.prototype, 'endsWith', {
        value: function (searchString, position) {
            var subjectString = this.toString();
            if (position === undefined || position > subjectString.length) {
                position = subjectString.length;
            }
            position -= searchString.length;
            var lastIndex = subjectString.indexOf(searchString, position);
            return lastIndex !== -1 && lastIndex === position;
        }
    });
}

/** String.contains */
if (!String.prototype.contains) {
    String.prototype.contains = function () {
        return String.prototype.indexOf.apply(this, arguments) !== -1;
    };
}

/** String.trim */
if (!String.prototype.trim) {
    String.prototype.trim = function () {
        return this.replace(/^[\s\xA0]+|[\s\xA0]+$/g, '');
    };
}

/** Yukon Module */
var yukon = (function () {
    
    var mod = {
        
        /** Support for inheritance: inherit superType's prototype. */
        inheritPrototype : function (subType, superType) {
            var prototype = Object.create(superType.prototype);
            prototype.constructor = subType;
            subType.prototype = prototype;
        },
        
        /** Build application url, applying any application context needed. */
        url : function (url) {
            if (url.startsWith('/')) {
                return yg.app_context_path + url;
            } else {
                return url;
            }
        },
        
        /** 
         * Parse the inner text contents of 'selector' as JSON.
         * Used in conjunction with JsonTag.java 
         * @param {string} selector - The css selector to the element parse text contents of.
         */
        fromJson : function (selector) {
            return JSON.parse($(selector).text());
        },
        
        /** Convenient 'do nothing' function that doesn't require an argument like void(0); */
        nothing: function () {},
        
        /** 
         * Return a percent formatted to the decimal places specified.
         * i.e.
         * yukon.percent(13, 205, 3) results in "6.341%"
         * yukon.percent(5, 10, 3) results in "50%"
         */
        percent: function (count, total, decimals) {
            return Number((count / total * 100).toFixed(decimals)).toString() + '%';
        },
        
        /** 
         * Returns and array of the values in an object.  It only returns the objects own 
         * values, not those from the prototype chain.
         */
        values: function (obj) {
            var values = Object.keys(obj || {}).map(function (key) {
                return obj[key];
            });
            return values;
        },
        
        /** Fire a callback after a duration. */
        later: function (millis, callback) {
            setTimeout(function () { callback(); }, millis);
        },
        
        /** General purpose validators */
        validate: {
            
            /** 
             * Returns true if the supplied latitude is valid, otherwise false.
             * i.e. >= -90 and <= 90, with 6 or less decimal digits
             */
            latitude: function (lat) {
                if (lat > 90 || lat < -90) {
                    return false;
                }
                var validator = /^-?([0-8]?[0-9]|90)\.[0-9]{1,6}$/;
                return validator.test(lat);
            },
            
            /** 
             * Returns true if the supplied longitude is valid, otherwise false.
             * i.e. >= -180 and <= 180, with 6 or less decimal digits 
             */
            longitude: function (long) {
                if (long > 180 || long < -180) {
                    return false;
                }
                var validator = /^-?((1?[0-7]?|[0-9]?)[0-9]|180)\.[0-9]{1,6}$/;
                return validator.test(long);
            }
            
        },
        
        /**
         * Returns a random number between min (inclusive) and max (exclusive)
         */
        random: function (min, max) {
            return Math.random() * (max - min) + min;
        },
        
        /**
         * Returns a random integer between min (inclusive) and max (inclusive)
         * Using Math.round() will give you a non-uniform distribution!
         */
        randomInt: function (min, max) {
            return Math.floor(Math.random() * (max - min + 1)) + min;
        }
        
    };
    
    return mod;
})();

/**
 * Namespace function: so we don't have to put all those checks to see if
 * modules exist and either create empty ones or set a reference to one
 * that was previously created.
 * Creates a global namespace. If 'yukon' is in leading part of name,
 * strip it off and hang the rest off of yukon
 * See Zakas, Maintainable JavaScript, pp. 72-73, and Stefanov,
 * Javascript Patterns, pp. 89-90
 */
yukon.namespace = function (ns) {
    
    var parts = ns.split('.'), object = this, i, len;
    
    // strip parts[0] if it is the initial name
    // if first element in namespace exists, skip it
    if (window[parts[0]]) {
        parts = parts.slice(1);
    }
    
    for (i=0, len=parts.length; i < len; i++) {
        if (!object[parts[i]]) {
            object[parts[i]] = {};
        }
        object = object[parts[i]];
    }
    
    return object;
};

/** 
 * UI module - General purpose ui functionality for yukon.
 * 
 * @requires JQUERY
 * @requires blockUI 2.39
 */
yukon.namespace('yukon.ui');
yukon.ui = (function () {
    
    var initialized = false,
    
    /** Initialize the site wide search autocomplete. */
    _initSearch = function () {
        
        var field = $('.yukon-search-form .search-field');
        field.autocomplete({
            delay: 100, // Delay 100ms after keyUp before sending request.
            minLength: 2, // User must type 2 characters before any search is done.
            source: function (request, response) {
                $.ajax({
                    type: 'get',
                    url: yukon.url('/search/autocomplete.json'),
                    dataType: 'json',
                    data: {
                        q: request.term
                    }
                }).done(function (data) {
                    response($.map(data, function (item) {
                        return {
                            label: item,
                            value: item
                        };
                    }));
                });
            },
            select: function (event, ui) {
                field.val(ui.item.value);
                field.parents('.yukon-search-form').submit();
            }
        });
    },
    
    mod = {
        
        init: function () {
            if (!initialized) {
                
                _initSearch();
                
                mod.autowire();
                
                initialized = true;
                
                mod.wizard.init();
            }
        },
        
        exclusiveSelect: function (item) {
            item = $(item);
            item.siblings().removeClass('selected');
            item.addClass('selected');
        },
        
        busy: function (item) {
            var btn = $(item),
                spinner = btn.children('.icon.busy'),
                label,
                busyText,
                originalText;
            
            btn.prop('disabled', true);
            btn.addClass('busy');
            // if this button has an icon hide it
            btn.children('.icon').hide();
            if (!spinner.length) {
                btn.prepend('<i class="icon busy"></i>');
                spinner = btn.children('.icon.busy');
            }
            spinner.show();
            
            label = btn.children('.b-label');
            busyText = btn.attr('data-busy');
            if (busyText && label.length > 0) {
                originalText = label.html();
                label.html(busyText);
                btn.data('originalText', originalText);
            }
            
            return btn;
        },
        
        unbusy: function (item) {
            var btn = $(item),
                label,
                originalText;
            
            btn.prop('disabled', false);
            btn.removeClass('busy');
            // if this button has an icon show it
            btn.children('.icon').show();
            btn.children('.icon.busy').hide();
        
            label = btn.children('.b-label');
            originalText = btn.data('originalText');
            if (originalText && label.length > 0) {
                label.html(originalText);
            }
            
            return btn;
        },
        
        /** 
         * Returns button array for jquery ui dialogs that contain a 'Cancel' and 'OK' buttons and an
         * optional 'Delete' button.
         * The 'OK' action can have a custom target and/or event.
         * 
         * @param {object} [options]                   - An object literal with the following properties:
         *        {string} [event=yukon.dialog.ok]     - The name of the event to fire when 'ok' button is clicked. 
         *                                               Defaults to 'yukon.dialog.ok'.
         *        {string} [mode=VIEW]                 - When set to 'CREATE', the delete button will not be shown. 
         *                                               Defaults to 'VIEW'.
         *        {string, element} [form]             - If present, submits the form supplied or the first form element 
         *                                               found inside the popup. 'event' is not fired when 'form' is 
         *                                               present. 
         *        {string|element} [target]            - The target of the event (element or selector). 
         *                                               Defaults to the popup.
         *        {string} [okClass='']                - CSS class names applied to the primary (ok) button.
         *        {string} [okText=yg.text.ok]         - The text of the ok button. Defaults to yg.text.ok.
         *        {boolean} [okDisabled=false]         - If true, the primary (ok) button will be initially disabled.
         *        {boolean} [confirm=false]            - If true, after clicking the primary (ok) button, 
         *                                               the dialog will ask for confirmation.
         *        {string} [cancelClass='']            - CSS class names applied to the secondary (cancel) button.
         *        {string} [cancelText=yg.text.cancel] - The text of the cancel button. Defaults to yg.text.cancel.
         *        {boolean} [delete=false]             - If true, a 'Delete' button will also be included.
         *                                               Only used when mode is not 'CREATE'.
         */
        buttons: function (options) {
            
            options = $.extend({
                cancelClass : '',
                cancelText : yg.text.cancel,
                confirm : false,
                'delete' : false,
                event : 'yukon.dialog.ok',
                mode : 'VIEW',
                okClass : '',
                okDisabled : false,
                okText : yg.text.ok
            }, options || {});
            
            var buttons = [],
            
            okClicked = function (dialog, target) {
                
                if (options.hasOwnProperty('form')) {
                    var form = $(options.form);
                    if (!form.is('form')) form = dialog.find('form');
                    form.submit();
                } else {
                    $(target).trigger(options.event);
                }
            };
            
            // Cancel Button
            buttons.push({
                text : options.cancelText,
                click : function (ev) { $(this).dialog('close'); },
                'class': 'js-secondary-action ' + (options.cancelClass || '')
            });
            
            if (options['delete'] && options.mode !== 'CREATE') {
                // Delete Button
                buttons.push({
                    text : yg.text['delete'],
                    click: function (ev) {
                        
                        var dialog = $(this).closest('.ui-dialog-content'),
                            target = options.target || dialog;
                        mod.confirm({ dialog : dialog });
                        dialog.off('yukon:ui:dialog:confirm').on('yukon:ui:dialog:confirm', function (ev) {
                            $(target).trigger('yukon:ui:dialog:delete');
                        });
                    },
                    'class' : 'delete'
                });
            }
            
            // OK Button
            buttons.push({
                text : options.okText,
                click : function (ev) {
                    
                    var dialog = $(this).closest('.ui-dialog-content'),
                    target = options.target ? options.target : dialog;
                    if (options.confirm) {
                        mod.confirm({ dialog : dialog });
                        dialog.off('yukon:ui:dialog:confirm').on('yukon:ui:dialog:confirm', function (ev) {
                            okClicked(dialog, target);
                        });
                    } else {
                        okClicked(dialog, target);
                    }
                },
                'class' : 'primary action js-primary-action ' + options.okClass,
                disabled : options.okDisabled
            });
            
            return buttons;
        },
        
        /** 
         * Show a popup. Popup style/behavior should be stored in data attributes described below.
         * 
         * @param {sting|object} popup - DOM Object, jQuery DOM object, or css selector string of the popup
         *                               element, usually a div, to use as a popup.
         * @param {string} [url] - If provided, the popup element will be loaded with the response from an
         *                         ajax request to that url. It will override the [data-url] attribute value 
         *                         if it exists.
         *                               
         * The popup element's attributes are as follows:
         * 
         * data-dialog        - If present the popup will have 'ok', 'cancel' buttons. See yukon.ui.buttons
         *                      function for button behaviors.
         * data-dialog-tabbed - If present, the title bar will be tabs. Correct JQuery tabs markup is expected.
         * data-width         - Width of the popup. Default is 'auto'.
         * data-height        - Height of the popup. Default is 'auto'.
         * data-title         - The title of the popup.
         * data-event         - If present and [data-dialog] is present, the value of [data-event] will be the name
         *                      of the event to fire when clicking the 'ok' button.
         * data-target        - If present and [data-dialog] is present' the value of [data-target] will be the 
         *                      target of the event fired when clicking the ok button.
         * data-url           - If present, the contents of the popup element will be replaced with the 
         *                      response of an ajax request to the url before the popup is shown.
         * data-load-event    - If present, this event will be fired right before the popup is shown.
         *                      If 'data-url' is used, the event will be fired after the dialog is loaded with
         *                      the response body.
         * data-popup-toggle  - If present, the trigger element can be clicked to close the popup as well.
         * data-confirm       - If present, after clicking the 'OK' button, the dialog will ask for confirmation.
         * data-mode          - If present, sets a page mode for the popup, passed to buttons. 
         *                      Values: 'CREATE', 'EDIT', 'VIEW'.
         *
         * Positioning options: see http://api.jqueryui.com/position/
         * data-position-my   - 'left|center|right top|center|bottom', Order matters. Default is 'center'
         * data-position-at   - 'left|center|right top|center|bottom', Order matters. Default is 'center'
         * data-position-of   - selector|element.  Default is 'window'.
         */
        dialog: function (popup, url) {
            
            popup = $(popup);
            
            var dialog = popup.is('[data-dialog]'),
                tabbed = popup.is('[data-dialog-tabbed]'),
                loadEvent = popup.data('loadEvent'),
                options = {
                    minWidth: popup.is('[data-min-width]') ? popup.data('minWidth') : '150',
                    width: popup.is('[data-width]') ? popup.data('width') : 'auto',
                    height: popup.is('[data-height]') ? popup.data('height') : 'auto',
                    minHeight: popup.is('[data-min-height]') ? popup.data('minHeight') : '150',
                    dialogClass: popup.is('[data-class]') ? 'yukon-dialog ' + popup.data('class') : 'yukon-dialog'
                },
                buttonOptions = {},
                positionOptions = {};
            
            if (popup.is('[data-title]')) options.title = popup.data('title');
            if (popup.is('[title]')) options.title = popup.attr('title');
            
            if (dialog) {
                if (popup.is('[data-ok-text]')) buttonOptions.okText = popup.data('okText');
                if (popup.is('[data-ok-class]')) buttonOptions.okClass = popup.data('okClass');
                if (popup.is('[data-ok-disabled]')) buttonOptions.okDisabled = true;
                if (popup.is('[data-cancel-text]')) buttonOptions.cancelText = popup.data('cancelText');
                if (popup.is('[data-cancel-class]')) buttonOptions.cancelClass = popup.data('cancelClass');
                if (popup.is('[data-event]')) buttonOptions.event = popup.data('event');
                if (popup.is('[data-target]')) buttonOptions.target = $(popup.data('target'));
                if (popup.is('[data-form]')) buttonOptions.form = popup.data('form');
                if (popup.is('[data-confirm]')) buttonOptions.confirm = true;
                if (popup.is('[data-mode]')) buttonOptions.mode = popup.data('mode');
                if (popup.is('[data-delete]')) buttonOptions['delete'] = true;
                options.buttons = mod.buttons(buttonOptions);
            }
            
            if (popup.is('[data-position-my]')) positionOptions.my = popup.data('positionMy');
            if (popup.is('[data-position-at]')) positionOptions.at = popup.data('positionAt');
            if (popup.is('[data-position-of]')) positionOptions.of = popup.data('positionOf');
            options.position = positionOptions;
            
            if (popup.is('[data-url]') || url) {
                url = url || popup.data('url');
                popup.load(url, function () {
                    // if no title provided, try to find one hidden in the popup contents
                    if (!options.title) {
                        var title = popup.find('.js-popup-title');
                        if (title[0]) options.title = title[0].value;
                    }
                    if (loadEvent) popup.trigger(loadEvent);
                    
                    if (tabbed) {
                        popup.tabbedDialog(options);
                    } else {
                        popup.dialog(options);
                    }
                });
            } else {
                
                if (loadEvent) popup.trigger(loadEvent);
                if (tabbed) {
                    popup.tabbedDialog(options);
                } else {
                    popup.dialog(options);
                }
            }
        },
        
        /** Initialize any chosen selects on page load. */
        initChosen : function () {
            
            $('.js-init-chosen').each(function () {
                $(this).chosen({ 'width' : $(this).getHiddenDimensions().innerWidth + 11 + 'px' });
            }).removeClass('js-init-chosen');
            
            $(document).off('click.yukon.chosen', '.chosen-single');
            $(document).on('click.yukon.chosen', '.chosen-single', function () {
                
                var chosenElem = $(this),
                    chosenContainer = chosenElem.closest('.chosen-container'),
                    chosenHeight = chosenElem.outerHeight() + chosenContainer.find('.chosen-drop').outerHeight(),
                    offsetInContainer = chosenContainer.offset().top - chosenContainer.offsetParent().offset().top,
                    minBottom = offsetInContainer + chosenHeight,
                    
                    /* The following properties are specific to being in a dialog */
                    scrollContainer = chosenElem.closest('.ui-dialog-content'),
                    currentBottom = scrollContainer.outerHeight(),
                    scrollOffset = scrollContainer.scrollTop();
                
                /* If we're not in a dialog, scroll the page */
                if (!scrollContainer.length) {
                    
                    scrollContainer = $(window);
                    currentBottom = scrollContainer.outerHeight() + window.pageYOffset;
                    scrollOffset = 0;
                }
                
                if (minBottom > currentBottom) {
                    scrollContainer.scrollTop(minBottom - scrollContainer.outerHeight() + scrollOffset);
                }
            });
        },
        
        autowire: function () {
            
            /** Follow clicks on top level nav menus when not using a touch screen. */
            $(document).on('click', '.yukon-header .menu-title', function (ev) {
                if ($(this).is('[data-url]') && !Modernizr.touch) {
                    window.location.href = $(this).data('url');
                }
            });
            
            mod.initChosen();
            
            /** Initialize any tabbed containers on page load. */
            $('.js-init-tabs').each(function (idx, elem) {
                elem = $(elem);
                elem.tabs({'active': elem.data('selectedTab')}).show().removeClass('js-init-tabs');
            });
                
            /** Add placeholder functionality if needed. */
            if (!Modernizr.input.placeholder) $('input, textarea').placeholder();
            
            /** Fix file path on all yukon <tags:file> usage in windows. */
            $(document).on('change', '.file-upload input[type="file"]', function (ev) {
                
                var input = $(this),
                    container = input.closest('.file-upload'),
                    nameInput = container.find('.file-name'),
                    value = input.val();
                
                // Windwows adds C:\fakepath\ for security reasons.
                value = value.replace('C:\\fakepath\\', '');
                nameInput.text(value);
            });
            
            /** Toggle selection (pipe) on rows of selectable tables. */
            $(document).on('click', 'table.selectable tbody tr, ul.selectable li', function (ev) {
                $(this).addClass('selected').siblings().removeClass('selected');
            });
            
            /** Sorting Handler: Sort a table by column. */
            $(document).on('click', '.sortable', function (ev) {
                
                var anchor = $(this),
                    dir = anchor.is('.desc') ? 'asc' : 'desc',
                    sort = anchor.data('sort'),
                    container = anchor.closest('[data-url]'),
                    url = container.data('url'),
                    pagingArea = container.find('.paging-area'),
                    params = {sort: sort, dir: dir};
                
                // Add page size if paging is available
                if (pagingArea.length) {
                    params.itemsPerPage = pagingArea.data('pageSize');
                    params.page = 1;
                }
                if (container.is('[data-static]')) {
                    var joiner = url.indexOf('?') === -1 ? '?' : '&';
                    window.location.href = url + joiner + $.param(params);
                } else {
                    $.get(url, params).done(function (data) {
                        container.html(data);
                    });
                }
            });
            
            /** Paging Handler: Get the next or previous page, or change page size. */
            $(document).on('click', yg.selectors.paging, function (ev) {
                
                var
                target = $(this),
                container = target.closest('[data-url]'),
                sortables = container.find('.sortable'),
                url = container.data('url'),
                pagingArea = container.find('.paging-area'),
                page = pagingArea.data('currentPage'),
                pageSize = pagingArea.data('pageSize'),
                changePage = target.parent().is('.previous-page') || target.parent().is('.next-page'),
                params = {},
                sort;
                
                if (changePage) {
                    // they clicked the next or previous page buttons
                    params.page = target.parent().is('.previous-page') ? page - 1 : page + 1;
                    params.itemsPerPage = pageSize;
                } else {
                    // they clicked one of the page size links
                    params.page = 1;
                    params.itemsPerPage = target.data('pageSize');
                }
                
                // add sorting parameters if necessary
                if (sortables.length) {
                    sort = sortables.filter('.desc');
                    if (sort.length) {
                        params.dir = 'desc';
                        params.sort = sort.data('sort');
                    } else {
                        sort = sortables.filter('.asc');
                        if (sort.length) {
                            params.dir = 'asc';
                            params.sort = sort.data('sort');
                        }
                    }
                }
                
                if (container.is('[data-static]')) {
                    var joiner = url.indexOf('?') === -1 ? '?' : '&';
                    window.location.href = url + joiner + $.param(params);
                } else {
                    $.get(url, params).done(function (data) {
                        container.html(data);
                        container.trigger(yg.events.pagingend);
                    });
                }
                return false; // return false to stop form submissions
            });
            
            /** Show or hide an element when something is clicked. */
            $(document).on('click', '[data-show-hide]', function (ev) {
                var trigger = $(this);
                var target = $(trigger.data('showHide'));
                if (target.is('[data-url]')) {
                    if (target.is(':visible')) {

                        target.slideUp(150);

                        if (trigger.is('.revealer')) {
                            trigger.removeClass('revealer-expanded');
                        }
                    } else {
                        target.load(target.data('url'), function () {

                            target.slideDown(150);

                            if (target.is('[data-event]')) {
                                target.trigger(target.data('event'));
                            }
                            if (trigger.is('.revealer')) {
                                trigger.addClass('revealer-expanded');
                            }
                        });
                    }
                } else {
                    if (target.is(':visible')) {

                        target.slideUp(150);

                        if (trigger.is('.revealer')) {
                            trigger.removeClass('revealer-expanded');
                        }
                    } else {

                        target.slideDown(150);
                        if (target.is('[data-event]')) {
                            target.trigger(target.data('event'));
                        }
                        if (trigger.is('.revealer')) {
                            trigger.addClass('revealer-expanded');
                        }
                    }
                }
            });
            
            /** Toggle buttons in a button group */
            $(document).on('click', '.button-group-toggle .button', function (ev) {
                
                var btn = $(this), input, value;
                
                btn.addClass('on').siblings('.button').removeClass('on');
                // Toggle visibility of show/hide elements
                if (btn.is('[data-show]')) {
                    btn.siblings('[data-show]').each(function () {
                        $($(this).data('show')).hide();
                    });
                    $(btn.data('show')).show();
                }
                // Set an input if we need to
                if (btn.is('[data-value]')) {
                    value = btn.data('value');
                    input = btn.is('[data-input]') ? $(btn.data('input')) : btn.siblings('[data-input]');
                    if (input.length) input.val(value).change();
                }
            });
            
            /** Elements that navigate on click */
            $(document).on('click', '[data-href]', function (ev) { window.location = $(this).attr('data-href'); });
            
            /** Page blockers */
            $(document).on('click', '.js-blocker', mod.block);
            $(document).on('resize', '#modal-glass', mod.blockPage);
            
            /** Clear page blocker */
            $(document).on('click', '.js-clearBlocker', mod.unblockPage);
            
            /** Disable a form element after clicked */
            $(document).on('click', '.js-disable-after-click', function (ev) {
                
                var button = $(this), group, form;
                
                if (button.is(':input')) {
                    this.disabled = true;
                    group = button.attr('data-disable-group');
                    if (group !== '') {
                        $("[data-disable-group='" + group + "']").each(function (idx) {
                            this.disabled = true;
                        });
                    }
                    
                    // if this is a busy button, add the spinner icon and use the busy text
                    if (button.is('[data-busy]')) {
                        mod.busy(button);
                    }
                    
                    // if this is a submit button, trigger the submit event on the form
                    if (button.is(':submit')) {
                        form = $(this.form);
                        
                        // insert the name and or value of the button into the form action
                        if (typeof button.attr('name') != 'undefined' && button.attr('name').length !== 0) {
                            form.prepend('<input name="'+ button.attr('name') +
                                '" value="' + button.attr('value') + '" type="hidden"/>');
                        }
                        form.trigger('submit');
                    }
                }
                return false;
            });
            
            /** Prevent forms from submitting via enter key */
            $(document).on('keydown', 'form.js-no-submit-on-enter', function (e) {
                // allow override submission elements
                if ($(e.target).hasClass('js-submit-on-enter')) {
                    return true;
                }
                if (e.keyCode == yg.keys.enter) {
                    return false;
                }
            });
            
            /** Close dialogs when clicking .js-close elements or the yukon.dialog.ok event fires. */
            $(document).on('click', '.js-close', function (ev) {
                var dialog = $(ev.target).closest('.ui-dialog');
                if (dialog.length) dialog.find('.ui-dialog-content').dialog('close');
            });
            $(document).on('yukon.dialog.ok', function (ev) {
                $(ev.target).closest('.ui-dialog-content').dialog('close');
            });
            
            /** Format phone numbers initially and on input blur */
            $('input.js-format-phone').each(function (idx, elem) {
                mod.formatPhone(elem);
            });
            $(document).on('blur', 'input.js-format-phone', function (event) {
                mod.formatPhone(event.target);
            });
            
            /** Enable or disable elements based on the state of a checkbox. */
            $(document).on('change click', '[data-toggle]', function (ev) {
                mod.toggleInputs($(this));
            });
            $('[data-toggle]').each(function (idx, item) { mod.toggleInputs(item); });
            
            /** Select all checkbox was clicked, select or unselect all items in a .js-select-all-container. */
            $(document).on('click', '.js-select-all', function (ev) {
                $(this).closest('.js-select-all-container').find('.js-select-all-item').prop('checked', $(this).prop('checked'));
            });
            
            /** A checkbox in a 'select all' container was clicked. */
            $(document).on('click', '.js-select-all-item', function (ev) {
                
                var selectAll = true,
                    selected = $(this).prop('checked'),
                    container = $(this).closest('.js-select-all-container'),
                    allItems = container.find('.js-select-all-item');
                
                if (selected) {
                    allItems.each(function (idx, item) { if (!$(item).prop('checked')) selectAll = false; });
                    container.find('.js-select-all').prop('checked', selectAll);
                } else {
                    container.find('.js-select-all').prop('checked', false);
                }
            });
            
            /** STEPPER SELECT BEHAVIOR. */
            /** Move selection backward when previous button clicked. */
            $(document).on('click', '.stepper .stepper-prev', function (ev) {
                
                var btn = $(this);
                var wrapper = btn.closest('.stepper');
                var select = wrapper.find('.stepper-select');
                var prev = select.find('option:selected').prev('option');
                
                if (!prev.length) {
                    prev = select.find('option:last-child');
                }
                prev.prop('selected', true);
                select.trigger('change');
                
                return true;
            });
            
            /** Move selection backward when previous button clicked. */
            $(document).on('click', '.stepper .stepper-next', function (ev) {
                
                var btn = $(this);
                var wrapper = btn.closest('.stepper');
                var select = wrapper.find('.stepper-select');
                var next = select.find('option:selected').next('option');
                
                if (!next.length) {
                    next = select.find('option:first-child');
                }
                next.prop('selected', true);
                select.trigger('change');
                
                return true;
            });
            
            /** Prevent select default behavior. */
            $(document).on('mousedown', '.stepper .stepper-select', function (ev) {
                ev.preventDefault();
            });
            /** END STEPPER SELECT BEHAVIOR. */
            
            /** Focus the designated input element */
            mod.autofocus();
            
            /** Init page 'Actions' button */
            var pageActions = $('#page-actions');
            if (pageActions.length) {
                $('#page-actions').remove();
                var menu = $('#b-page-actions .dropdown-menu');
                menu.html(pageActions.html());
                if (menu.find('.icon').length === menu.find('.icon-blank').length) {
                    menu.addClass('no-icons');
                }
                $('#b-page-actions').show();
            }
            
            /** Init page buttons */
            var pageButtons = $('#page-buttons');
            if (pageButtons.length) {
                $('#page-buttons').remove();
                $('.page-actions').append(pageButtons.html());
            }
            
            /** Add additional options to page 'Actions' button */
            $('.js-page-additional-actions').each(function (index, elem) {
                elem = $(elem);
                $('#b-page-actions .dropdown-menu').append(elem.html());
                elem.remove();
            });
            
            /** 
             * Show a popup when a popup trigger (element with a [data-popup] attribute) is clicked.
             * If the trigger element has a [data-popup-toggle] attribute and the popup is currently open,
             * the popup will be closed instead and the event propigated normally...otherwise yukon.ui.dialog is 
             * called passing the popup element.
             */
            $(document).on('click', '[data-popup]', function (ev) {
                
                var trigger = $(this),
                    popup = $(trigger.data('popup'));
                
                try { /* Close popup if the trigger is a toggle and the popup is open */
                    if (trigger.is('[data-popup-toggle]') && popup.dialog('isOpen')) {
                        popup.dialog('close');
                        // Return so we don't re-open it, return true to propigate event incase others are listening.
                        return true;
                    }
                } catch (error) {/* Ignore error, occurs when dialog not initialized yet. */ }
                
                // Set a create/edit/view mode if possible
                if (trigger.is('[data-mode]')) {
                    var mode = trigger.data('mode');
                    popup.attr('data-mode', mode).data('mode', mode);
                }
                
                // show the popup
                mod.dialog(popup);
                
                // check for focus
                var focus = popup.find('.js-focus');
                if (focus.length) focus.focus();
            });
        },
        
        /**
         * Turn dialogs buttons into confirm
         * @param {jQuery} options.dialog        - jQuery element that has had .dialog called on it
         * @param {string} [options.event]       - Event to be fired if the confirm button is pressed
                                                   default is 'yukon:ui:dialog:confirm'
         * @param {jQuery} [options.target]      - element to trigger the event. Default is dialog.
         * @param {string} [options.confirmText] - Confirm text next to buttons
         * @param {string} [options.yesText]
         * @param {string} [options.noText]
         */
        confirm: function (options) {
            
            var dialog = options.dialog,
                target = options.target || dialog,
                event = options.event || 'yukon:ui:dialog:confirm',
                confirmText = options.confirmText || yg.text.confirm,
                yesText = options.yesText || yg.text.yes,
                noText = options.noText || yg.text.no,
                oldButtons = dialog.dialog('option', 'buttons'),
                confirmButton = {
                    text: yesText,
                    click: function (ev) {
                        confirmSpan.remove();
                        dialog.dialog('option', 'buttons', oldButtons);
                        $(target).trigger(event);
                    },
                    'class': 'primary action js-primary-action'
                },
                cancelButton = {
                   text: noText,
                   click: function (ev) {
                       confirmSpan.remove();
                       dialog.dialog('option', 'buttons', oldButtons);
                   },
                   'class': 'js-secondary-action '
                },
                confirmSpan = $('<span>')
                .attr('class', 'fr')
                .css({'line-height': '36px'})
                .text(confirmText);
            
            dialog.dialog('option', 'buttons', [cancelButton, confirmButton]);
            dialog.closest('.ui-dialog').find('.ui-dialog-buttonpane').append(confirmSpan);
            dialog.on('dialogclose', function () {
                confirmSpan.remove();
            });
        },
        
        AUTOFOCUS_TRIES: 0,
        
        autofocus: function () {
            
            var focusElement = $('[autofocus], .js-focus:first')[0];
            
            if (focusElement) {
                try { //Play nice with IE
                    focusElement.focus();
                } catch(err) {
                    //give the autofocus element 3 seconds to show itself
                    if (mod.AUTOFOCUS_TRIES < 30) {
                        //certain browsers will error if the element to be focused is not yet visible
                        setTimeout(mod.autofocus, 100);
                    }
                }
            }
        },
        
        /** Sets the focus to the first input/textarea found on the page having a class of "error" */
        focusFirstError: function () {
            mod.setFocusFirstError();
            mod.autofocus();
        },
        
        /** Applies the "js-focus" class to the first input/textarea element having a class of "error" */
        setFocusFirstError: function () {
            
            var error_field = $('input.error, textarea.error').first();
            
            if (error_field.length === 1) {
                $('.js-focus').removeClass('js-focus');
                error_field.addClass('js-focus');
            }
        },
        
        /** Block out the closest valid container to the event's target, or the page */
        block: function (ev) {
            var blockElement = $(ev.target).closest('.js-block-this')[0];
           if (blockElement) {
               mod.elementGlass.show(blockElement);
           } else {
               mod.pageGlass.show();
           }
        },
        
        /** Unblock the closest valid container to the event's target, or the page */
        unblock: function (ev) {
            var blockElement = $(ev.target).closest('.js-block-this')[0];
            if (blockElement) {
                mod.elementGlass.hide(blockElement);
            } else {
                mod.pageGlass.hide();
            }
        },
        
        /** Block out the whole page */
        blockPage: function (args) {
            mod.pageGlass.show();
        },
        
        /** Unblock the whole page */
        unblockPage: function () {
            mod.pageGlass.hide();
        },
        
        /** Add a success alert box. */
        alertSuccess: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'success' });
        },
        
        /** Add an info alert box. */
        alertInfo: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'info' });
        },
        
        /** Add a warning alert box. */
        alertWarning: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'warning' });
        },
        
        /** Add an error alert box. */
        alertError: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'error' });
        },
        
        /** Add a pending alert box. */
        alertPending: function (markup) {
            $('.main-container').addMessage({ message: markup, messageClass: 'pending' });
        },
        
        /** Remove all alert boxes from the page. */
        removeAlerts : function () { $('.main-container').removeMessages(); },
        
        /** Format a phone number input */
        formatPhone: function (input) {
            // strip the input down to just numbers, then format
            var stripped = input.value.replace(/[^\d]/g, ''),
                i,
                regex,
                format;
                
            if (stripped.length > 0) {
                for (i=0; i<yg.formats.phone.length; i++) {
                    regex = yg.formats.phone[i].regex;
                    format = yg.formats.phone[i].format;
                    if (regex.test(stripped)) {
                        input.value = stripped.replace(regex, format);
                        break;
                    }
                }
            } else {
                input.value = '';
            }
        },
        
        /** Enable or disable elements based on the state of a checkbox. */
        toggleInputs: function (checkbox) {
            
            checkbox = $(checkbox);
            var enable = checkbox.is('[data-toggle-inverse]') ? checkbox.not(':checked') : checkbox.is(':checked'),
                inputs = $('[data-toggle-group="' + checkbox.data('toggle') + '"]');
                
            inputs.each(function (idx, input) { $(input).prop('disabled', !enable); });
        },
        
        /** 
         * Reindex the name of every input in a table to support spring binding.
         * Will also enable/disable any move up/move down buttons properly.
         * 
         * @param {element, string} table - Element or css selector for the table/table ancestor.
         * @param {function} [rowCallback] - Optional function to fire after processing each row.
         *                                   Takes the row element as an arg.
         */
        reindexInputs: function (table, rowCallback) {
            
            table = $(table);
            var rows = table.find('tbody tr');
                
            rows.each(function (idx, row) {
                row = $(row);
                var inputs = row.find('input, select, textarea, button');
                
                inputs.each(function (inputIdx, input) {
                    
                    input = $(input);
                    var name;
                    
                    if (input.is('[name]')) {
                        name = $(input).attr('name');
                        input.attr('name', name.replace(/\[(\d+|\?)\]/, '[' + idx + ']'));
                    }
                });
                
                // fix up the move up/down buttons if there are any
                if (row.has('.js-up, .js-down').length) {
                    if (rows.length === 1) { // only one row
                        row.find('.js-up, .js-down').prop('disabled', true);
                    } else if (idx === 0) { // first row
                        row.find('.js-up').prop('disabled', true);
                        row.find('.js-down').prop('disabled', false);
                    } else if (idx === rows.length -1) { // last row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', true);
                    } else { // middle row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', false);
                    }
                }
                
                if (typeof(rowCallback) === 'function') rowCallback(row);
            });
        },
        
        /** 
         * Adjusts row move up/down buttons so that the first row's move up
         * and the last row's move down buttons are disabled. 
         * 
         * @param {element, string} table - Element or css selector for the table/table ancestor.
         * @param {function} [rowCallback] - Optional function to fire after processing each row.
         *                                   Takes the row element as an arg.
         */
        adjustRowMovers: function (table, rowCallback) {
            
            table = $(table);
            var rows = table.find('tbody tr');
            
            rows.each(function (idx, row) {
                
                row = $(row);
                
                // fix up the move up/down buttons
                if (row.has('.js-up, .js-down').length) {
                    if (rows.length === 1) { // only one row
                        row.find('.js-up, .js-down').prop('disabled', true);
                    } else if (idx === 0) { // first row
                        row.find('.js-up').prop('disabled', true);
                        row.find('.js-down').prop('disabled', false);
                    } else if (idx === rows.length -1) { // last row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', true);
                    } else { // middle row
                        row.find('.js-up').prop('disabled', false);
                        row.find('.js-down').prop('disabled', false);
                    }
                }
                
                if (typeof(rowCallback) === 'function') rowCallback(row);
            });
        },
        
        /** Pad a string */
        pad: function (number, length) {
            
            var str = '' + number;
            while (str.length < length) {
                str = '0' + str;
            }
            
            return str;
        },
        
        /** Format time, (do we still need yukon.format.time.js?) */
        formatTime : function (time, opts) {
            
            var timeStr = '';
            if (!opts) {
                opts = {};
            }
            
            if (opts['24']) {
                if (opts.pad) {
                    timeStr = mod.pad(time.getHours(), 2) + ':' + mod.pad(time.getMinutes(), 2);
                } else {
                    timeStr = time.getHours() + ':' + mod.pad(time.getMinutes(), 2);
                }
            } else {
                var hours = time.getHours() % 12 === 0 ? 12 : time.getHours() % 12,
                    meridian = '';
                if (opts.meridian) {
                    meridian = time.getHours() >= 11 ? 'pm' : 'am';
                }
                
                if (opts.pad) {
                    timeStr = pad(hours, 2) + ':' + mod.pad(time.getMinutes(), 2) + meridian;
                } else {
                    timeStr = hours + ':' + mod.pad(time.getMinutes(), 2) + meridian;
                }
            }
            
            return timeStr;
        },
        
        /** Retrieve a request parameter by name */
        getParameterByName: function (name) {
            
            var regexS,
                regex,
                results;
                
            name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
            regexS = "[\\?&]"+name+"=([^&#]*)";
            regex = new RegExp(regexS);
            results = regex.exec(window.location.href);
            
            if (results == null) {
                return null;
            } else {
                return decodeURIComponent(results[1].replace(/\+/g, " "));
            }
        },
        
        /** Manages wizard style paging, useful in complex popups. */
        wizard: {
            
            _initialized: false,
            
            init: function () {
                $('.js-wizard').each(function (idx, elem) {
                    $(elem).find('.js-next').each(function (index, nextButton) {
                        $(nextButton).on('click', function (ev) {
                            mod.wizard.nextPage($(ev.target).closest('.js-page'));
                        });
                    });
                    
                    $(elem).find('.js-prev').each(function (index, prevButton) {
                        $(prevButton).on('click', function (ev) {
                            mod.wizard.prevPage($(ev.target).closest('.js-page'));
                        });
                    });
                    
                    $(elem).find('.js-page').each(function (index, elem) {
                        if (idx > 0) {
                            $(elem).hide();
                        } else {
                            $(elem).show();
                        }
                    });
                });
                
                mod.wizard._initialized = true;
            },
            
            nextPage: function (page) {
                
                page = $(page);
                
                var nextPage;
                    
                if (typeof page !== 'undefined') {
                    nextPage = page.nextAll('.js-page')[0];
                    if (typeof nextPage !== 'undefined') {
                        page.hide();
                        $(nextPage).show();
                    }
                }
            },
            
            prevPage: function (page) {
                
                var prevPage;
                
                if (typeof page !== 'undefined') {
                    prevPage = page.prevAll('.js-page')[0];
                    if (typeof prevPage !== 'undefined') {
                        $(page).hide();
                        $(prevPage).show();
                    }
                }
            },
            
            /**
             * Resets the page of the wizard to the first/initial page. Does NOT do
             * anything with the contents
             * 
             * wizard: can be any element in the DOM. * If it is the js-wizard
             * container itself, it will reset the page * If it is an arbitrary
             * node, it will search for and reset ALL js-wizard containers within
             * 
             */
            reset: function (wizard) {
                
                wizard = $(wizard);
                
                if (wizard.hasClass('js-wizard')) {
                    $('.js-page', wizard).hide();
                    $('.js-page:first', wizard).show();
                } else {
                    $('.js-wizard .js-page').hide();
                    $('.js-wizard .js-page:first').show();
                }
            }
        },
        
        /** Object to glass out an element, used by #block and #unblock */
        elementGlass: {
            show: function (element) {
                
                element = $(element);
                var glass;
                
                if (element[0]) {
                    glass = element.find('.glass');
                    if (!glass[0]) {
                        element.prepend($('<div>').addClass('glass'));
                        glass = element.find('.glass');
                    }
                    return mod.elementGlass.redraw(glass);
                }
                // nothing to block
                return null;
            },
            
            hide: function (element) {
                $(element).find('.glass:first').fadeOut(200, function () {$(this).remove();});
            },
            
            redraw: function (glass) {
                var container = glass.closest('.js-block-this');
                if (!container.length) {
                    container = $(glass).parent();
                }
                // resize the glass
                glass.css('width', container.width())
                .css('height', container.height()).fadeIn(200);
            },
            
            resize: function (ev) {
                mod.elementGlass.redraw($(ev.currentTarget));
            }
        },
        
        /** Object to glass out the page, used by #block and #unblock */
        pageGlass: {
            
            show: function (args) {
                
                var defaults = $.extend({ color:'#000', alpha: 0.25 }, args || {}),
                    glass = $('#modal-glass');
                
                if (glass == null) {
                    glass = $('<div>').attr('id', 'modal-glass')
                    .append($('<div>').addClass('tint').append($('<div>').addClass('loading')))
                    .prependTo('body');
                }
                glass.find('.tint').css('opacity', defaults.alpha).css('background-color', defaults.color);
                glass.fadeIn(200);
            },
            
            hide: function () {
                $('#modal-glass').fadeOut(200);
            }
        },
        
        /**
         * @param {string|Object} row - Element (or selector to element) to be removed.
         *    Expected to have attributes:
         *    data-removed-text: text to confirm operation (eg. 'Item removed from list')
         *    data-undo-text: text to revert change (eg. 'Undo')
         * @param {Function} actionDo - Remove action
         * @param {Function} actionUndo - Undo remove action
         */
        removeWithUndo: function (row, actionDo, actionUndo) {
            
            row = $(row);
            
            var type = row.prop('tagName'),
                removedText = row.closest('[data-removed-text]').attr('data-removed-text'),
                undoText = row.closest('[data-undo-text]').attr('data-undo-text'),
                undoLink = $('<a class="fr" href="javascript:void(0)">' + undoText + '</a>'),
                undo;
            
            undo = $('<' + type + ' class="undo-row"></' + type + '>').hide();
            undo.append($('<span>' + removedText + '</span>'));
            undo.append(undoLink);
            
            actionDo();
            
            undoLink.click(function () {
                actionUndo();
                undo.fadeOut(100, function () {
                    undo.after(row);
                    row.fadeIn(100);
                    undo.remove();
                });
            });
            
            row.fadeOut(100, function () {
                row.after(undo);
                undo.fadeIn(100);
            });
        }
        
    };
    
    return mod;
})();

/** JQUERY PLUGINS */
(function ($) {
    
    /**
     * jQueryUI Tabbed Dialog Based on
     * http://forum.jquery.com/topic/combining-ui-dialog-and-tabs Modified
     * to work by Joseph T. Parsons For jQueryUI 1.10 and jQuery 2.0
     */
    $.fn.tabbedDialog = function (dialogOptions, tabOptions) {
        
        var initialized = this.hasClass('ui-dialog-content');
        var dynamic = this.is('[data-url]');
        
        dialogOptions = dialogOptions || {};
        tabOptions = tabOptions || {};
        
        if (!dialogOptions.dialogClass) {
            dialogOptions.dialogClass = 'ui-dialog-tabbed';
        } else {
            dialogOptions.dialogClass = dialogOptions.dialogClass + ' ui-dialog-tabbed';
        }
        
        if (initialized && dynamic) {
            // Tab markup is included in the ajaxed content, nuke our old tabbed title bar.
            this.tabs('destroy');
            this.parent().find('.ui-dialog-titlebar-tabbed').remove();
        }
        
        this.tabs(tabOptions);
        this.dialog(dialogOptions);
        
        // Bail out here when calling on an existing dialog that does not have dynamic content
        if (initialized && !dynamic) return;
        
        // Create the Tabbed Dialogue
        var tabul = this.find('ul:first');
        this.parent().addClass('ui-tabs').prepend(tabul).draggable('option', 'handle', tabul);
        tabul.append(
            $('<button>').attr('type', 'button').attr('role', 'button').attr('title', yg.text.close)
            .addClass('ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only ui-dialog-titlebar-close js-close')
            .append($('<span>').addClass('ui-button-icon-primary ui-icon ui-icon-closethick'))
            .append($('<span>').addClass('ui-button-text').text(yg.text.close))
        )
        .addClass('ui-dialog-titlebar-tabbed');
        
        // Remove the dialog titlebar when creating for the first time, it won't be there later.
        if (!initialized) this.prev().remove();
        
        this.attr('tabIndex', -1).attr('role', 'dialog');
        
        // Add a title if needed
        var title = this.dialog('option', 'title');
        if (title) {
            tabul.prepend($('<li>').addClass('ui-dialog-tabbed-title').text(title));
        }
        
        // Make Only The Content of the Tab Tabbable
        this.bind('keydown.ui-dialog', function (ev) {
            
            if (ev.keyCode !== $.ui.keyCode.TAB) {
                return;
            }
            
            var tabbables = $(':tabbable', this).add('ul.ui-tabs-nav.ui-dialog-titlebar-tabbed > li > a'),
                first = tabbables.filter(':first'),
                last = tabbables.filter(':last');
            
            if (ev.target === last[0] && !ev.shiftKey) {
                first.focus(1);
                return false;
            } else if (ev.target === first[0] && ev.shiftKey) {
                last.focus(1);
                return false;
            }
        });
        
        // Give the First Element in the Dialog Focus
        var hasFocus = this.find('.ui-tabs-panel:visible :tabbable');
        if (hasFocus.length) hasFocus.eq(0).focus();
    };
    
    /** Selects all text inside an element. Useful for copy action. */
    $.fn.selectText = function () {
        var text = this[0],
            range,
            selection;
        if (document.body.createTextRange) {
            range = document.body.createTextRange();
            range.moveToElementText(text);
            range.select();
        } else if (window.getSelection) {
            selection = window.getSelection();
            range = document.createRange();
            range.selectNodeContents(text);
            selection.removeAllRanges();
            selection.addRange(range);
        }
    };
    
    /** Disable everything in the collection that is an html form input type. */
    $.fn.toggleDisabled = function () {
        return this.each(function () {
            if ($(this).is(':input')) {
                this.disabled = !this.disabled;
            }
        });
    };
    
    /** 
     * Flash an element's background with a color for a duration.
     * @param {object} [options] - Options hash containing color and duration.
     * @param {number} [duration=1500] - The duration in milliseconds the animation will last. Default 1500. 
     * @param {string} [color=#fff288] - The color to flash the background. Default #fff288 (yellowish).
     */
    $.fn.flash = function (options) {
        return this.each(function () {
            
            options = $.extend({ color: '#fff288', duration: 1500 }, options || {});
            
            var me = $(this),
                prev = me.data('previousColor') ? me.data('previousColor') : me.css('background-color');
                me.data('previousColor', prev);
                
            me.stop(true)
            .css({ backgroundColor: options.color })
            .animate({ backgroundColor: prev }, options.duration);
        });
    };
    
    /** Set visibility to visible */
    $.fn.visible = function () {
        return this.each(function () {
            $(this).css('visibility', 'visible');
        });
    };
    
    /** Set visibility to hidden */
    $.fn.invisible = function () {
        return this.each(function () {
            $(this).css('visibility', 'hidden');
        });
    };
    
    /** Toggle visibility */
    $.fn.visibilityToggle = function () {
        return this.each(function () {
            $(this).css('visibility', function (i, visibility) {
                return (visibility === 'visible') ? 'hidden' : 'visible';
            });
        });
    };
    
    /** Add an alert box to an element. */
    $.fn.addMessage = function (args) {
        
        return this.each(function () {
            
            var i, list, alertbox, messages, type = typeof (args.message),
                create = !$(this).children('.user-message').length;
            
            messages = [];
            
            if (create) {
                $(this).prepend('<div class="user-message">');
            }
            alertbox = $(this).children('.user-message');
            
            if (type === 'string') {
                messages.push(args.message);
            } else if (type === 'object') {
                //array
                if (typeof (args.message.length) != 'undefined') {
                    for (i = 0; i < args.message.length; i++) {
                        messages.push(args.message[i]);
                    }
                } else {
                    for (var key in args.message) {
                        if (typeof (args.message[key]) === 'number' ||
                            typeof (args.message[key]) === 'string') {
                            messages.push(args.message[key]);
                        }
                    }
                }
            }
            
            alertbox.empty().removeClass('error success info warning pending').addClass(args.messageClass);
            
            if (messages.length > 1) {
                list = $('<ul class="simple-list">');
                for (i = 0; i < messages.length; i++) {
                    list.append('<li>' + messages[i] + '</li>');
                }
                alertbox.prepend(list);
            } else {
                alertbox.html(messages[0]);
            }
        });
    };
    
    /** Remove all alert boxes from an element. */
    $.fn.removeMessages = function() {
        return this.each(function() {
            $(this).children('.user-message').remove();
        });
    };
    
    /** 
     * A widget for creating a timeline to view events
     * @param {number} [begin=Yesterday Midnight] - Endpoint of the timeline, expressed as epoch timestamp.
     * @param {number} [end=Now] - Endpoint of the timeline, expressed as epoch timestamp.
     * @param {number} [tickInterval=1hr] - Interval between tickmarks.
     */
    $.widget('yukon.timeline', {
        
        /** @type {Object} options - Default settings to use. */
        options: {
            begin: new Date(new Date().setDate(new Date().getDate() - 1)),
            end: new Date().getTime(),
            events: {}
        },
        
        /** Constructor */
        _create: function () {
            this.element.addClass('timeline-container');
            this.element.data('timelineInitialized', true);
            this.draw();
        },
        
        /** 
         * Add an event to the timeline
         * @param {string} event.id - Unique identifier key. Will override any existing event with that id.
         * @param {number} event.timestamp - Time of event. expressed as epoch timestamp.
         * @param {string} [event.icon=icon-blank] - Icon to show on the timeline.
         * @param {string} [event.message] - html to display in the tooltip.
         */
        addEvent: function (event) {
            this.options.events[event.id] = event;
            this.draw();
        },
        
        /** 
         * Add events to the timeline
         * @param {string} events[].id - Unique identifier key. Will override any existing event with that id.
         * @param {number} events[].timestamp - Time of event. expressed as epoch timestamp.
         * @param {string} [events[].icon=icon-blank] - Icon to show on the timeline.
         * @param {string} [events[].message] - html to display in the tooltip.
         */
        addEvents: function (events) {
            var _self = this;
            events.forEach(function (event) {
                _self.options.events[event.id] = event;
            });
            this.draw();
        },
        
        /** 
         * Remove an event from the timeline and redraws the timeline.
         * @param {string} eventId - Unique identifier of the event to remove.
         */
        removeEvent: function (eventId) {
            delete this.options.events[eventId];
            this.draw();
        },
        
        /** 
         * Remove an event from the timeline and redraws the timeline.
         * @param {string[]} eventId - Unique identifiers of the events to remove.
         */
        removeEvents: function (eventIds) {
            var _self = this;
            eventIds.forEach(function (eventId) {
                delete _self.options.events[eventId];
            });
            this.draw();
        },
        
        /** 
         * Removes all events from the timeline.
         */
        clear: function () {
            this.options.events = {};
            this.draw();
        },
        
        /** 
         * Adds text timestamps to the endpoints.
         */
        _drawTicks: function () {
            
            var container = this.element;
            
            var begin = this.options.begin;
            var end = this.options.end;
            
            var beginText = moment(begin).tz(yg.timezone).format(yg.formats.date.long_date_time_hm);
            var endText = moment(end).tz(yg.timezone).format(yg.formats.date.long_date_time_hm);
            
            $('<span class="timeline-label-begin">')
            .text(beginText)
            .appendTo(container);
            
           $('<span class="timeline-label-end">')
            .text(endText)
            .appendTo(container);
            
        },
        
        /** 
         * Put all events on the timeline and cluster nearby events.
         */
        _drawEvents: function () {
            
            var container = this.element;
            
            var begin = this.options.begin;
            var end = this.options.end;
            var events = this.options.events;
            
            // eventIds will only include elements between the bounds, and be sorted by time.
            var eventIds = Object.keys(events).filter(function (id) {
                return begin < events[id].timestamp && events[id].timestamp < end;
            })
            .sort(function (lhs, rhs) {
                return events[lhs].timestamp - events[rhs].timestamp;
            });
            
            eventIds.forEach(function (id) {
                
                var event = events[id];
                
                var percent = yukon.percent(event.timestamp - begin, end - begin, 5);
                
                var icon = $('<i class="M0 icon ' + (event.icon || 'icon-blank') + '"/>');
                
                var span = $('<span class="timeline-event">')
                .toggleClass('timeline-icon', event.icon !== undefined)
                .css({'left': 'calc(' + percent + ' - 8px)' })
                .append(icon);
                
                var prevEvent = container.find('.timeline-event:last');
                
                container.append(span);
                
                var tooltipped;
                var offset = parseFloat(span.css('left')) - parseFloat(prevEvent.css('left'));
                
                // Cluster if closer than 10 px away.
                if (prevEvent.length && offset < 10) {
                    
                    span.remove();
                    prevEvent.addClass('multi')
                    .data('count', prevEvent.data('count') + 1);
                    
                    prevEvent.find('> .icon').remove();
                    
                    if (prevEvent.find('.timeline-event-count').length === 0) {
                        prevEvent.append('<span class="timeline-event-count">');
                    }
                    
                    prevEvent.find('.timeline-event-count')
                        .text(prevEvent.data('count'));
                    
                    tooltipped = prevEvent;
                    
                } else {
                    
                    var tooltipTemplate = $('<ul class="dn simple-list">')
                    .addClass('js-event-tooltip js-sticky-tooltip')
                    .attr('data-event-id', id);
                    
                    span.data('count', 1)
                    .append(tooltipTemplate)
                    .attr('data-tooltip', '.js-event-tooltip[data-event-id="' + id + '"]');
                    
                    tooltipped = span;
                }
                
                var tooltip = tooltipped.find('.js-event-tooltip');

                if (tooltip.find('li').length > 10) {
                    
                    tooltip.find('li:gt(10)').remove();
                    
                    var moreCount = tooltipped.data('count') - 10;
                    var moreText = yg.text.more.replace(/\{0\}/g, '<strong>' + moreCount + '</strong>');
                    
                    $('<li class="tac">')
                    .html(moreText)
                    .appendTo(tooltip);
                    return;
                }
                
                var timeText = moment(event.timestamp).tz(yg.timezone).format(yg.formats.date.full);
                var message = event.message ? ' - ' + event.message : '';
                
                $('<li>').html(timeText + message)
                .prepend(icon.clone())
                .appendTo(tooltip);
                
                var tooltipIcons = tooltip.find('.icon');
                
                /* 
                 * If there is at least 1 'real' icon, show them with a margin.
                 * If all icons are icon-blank, hide them
                 */
                if (tooltipIcons.is(':not(.icon-blank)')) {
                    tooltipIcons.removeClass('dn M0');
                } else {
                    tooltipIcons.addClass('dn M0');
                }
                
           });
            
        },
        
        /** 
         * Manually trigger a redraw of all events.
         */
        draw: function () {
            
            var container = this.element;
            
            container.empty()            
            .append('<span class="timeline-axis">')
            .show();
            
            this._drawTicks();
            this._drawEvents();
        }
    });
    
})(jQuery);

/** Initialize the lib */
$(function () {
    
    yukon.ui.init();
    
    //turn off ajax caching application-wide by default
    $.ajaxSetup({cache: false});
});
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
            return fToBind.apply(this instanceof fNOP && oThis ? this : oThis, aArgs.concat(Array.prototype.slice.call(arguments)));
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
    var mod;

    mod = {
        /** support for inheritance: inherit superType's prototype */
        inheritPrototype : function (subType, superType) {
            var prototype = Object.create(superType.prototype);
            prototype.constructor = subType;
            subType.prototype = prototype;
        },
        /** Handle app name prepending here */
        url : function (url) {
            if (url.startsWith('/')) {
                return yg.app_context_path + url;
            } else {
                return url;
            }
        },
        /** JavaScript side of JsonTag.java */
        fromJson : function (selector) {
            return JSON.parse($(selector).text());
        },
        /** Convenient 'do nothing' function that doesn't require an argument like void(0); */
        nothing: function () {},
        
        /** 
         * Return a percent formatted to the decimal places specified.
         * i.e.
         * yukon.percent(13, 205, 3); results in "6.341%"
         */
        percent: function (count, total, decimals) {
            return (parseFloat(count / total, 10) * 100).toFixed(decimals) + '%';
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
                var validator = /^-?([0-8]?[0-9]|90).[0-9]{1,6}$/;
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
    var parts = ns.split('.'),
        object = this,
        i,
        len;
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
 * @requires jQuery 1.6.4
 * @requires blockUI 2.39
 *     
 *     USAGE:
 *     In day to day use, the yukon.ui class will be your best bet at getting the functionality you
 *     need on a page.  The former yukon.uiUtils are more low level functions for use by other libraries,
 *     but have been subsumed into yukon.ui.
 */
yukon.namespace('yukon.ui');
yukon.ui = (function () {
    var initialized = false,
        mod = {};

    mod = {
            
        init: function () {
            if (!initialized) {
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
         * Returns button array for jquery ui dialogs.  The 'ok' action can have a 
         * custom target and/or event.
         * @param {object} options - An object literal with the following properties:
         *        {string} [event=yukon.dialog.ok] - The name of the event to fire when 'ok' button is clicked. Defaults to 'yukon.dialog.ok'.
         *        {string, element} [form] - If present, submits the form supplied or the first form element found inside the popup. 
         *                                   'event' is not fired when 'form' is present. 
         *        {string|element} [target=the popup element] - The target of the event (element or selector). Defaults to the popup.
         *        {string} [okText=yg.text.ok] - The text of the ok button. Defaults to yg.text.ok.
         *        {string} [cancelText=yg.text.cancel] - The text of the cancel button. Defaults to yg.text.cancel.
         */
        buttons: function (options) {
            
            var defaults = {
                    event: 'yukon.dialog.ok',
                    okText: yg.text.ok,
                    cancelText: yg.text.cancel,
                };
            if (typeof(options) !== 'undefined') {
                $.extend(defaults, options);
            }
            
            return [{
                        text: defaults.cancelText, click: function (ev) { $(this).dialog('close'); }, 
                        'class': 'js-secondary-action' 
                    },
                    { 
                        text: defaults.okText, 
                        click: function (ev) {
                            // Don't close the popup here.  We may want it to stay open, ie: validation failed.
                            if (defaults.hasOwnProperty('form')) {
                                var form = $(defaults.form);
                                if (!form.is('form')) form = $(this).closest('.ui-dialog-content').find('form');
                                form.submit();
                            } else {
                                defaults.target = defaults.target ? defaults.target : $(this).closest('.ui-dialog-content');
                                $(defaults.target).trigger(defaults.event);
                            }
                        }, 
                        'class': 'primary action js-primary-action' 
                    }];
        },
        
        autowire: function () {
            
            /** Initialize any chosen selects on page load. */
            $('.js-init-chosen').chosen();
            
            /** Initialize any tabbed containers on page load. */
            $('.js-init-tabs').tabs().show();
                
            /** Add fancy tooltip support (tipsy)
             *  To give an item a custom tooltip, give it a class of 'js-has-tooltip'
             *  and precede it, in the markup, with an item of class js-tooltip that
             *  has the content in HTML. The code below searches backward in the
             *  document for the element with class js-tooltip
             * 
             *  tipsy initialization:
             *    attaches tooltip handlers to all elements with a class of js-has-tooltip
             *    or a title attribute
             */
            // some pages do not include the tipsy libary
            if ('undefined' !== typeof $.fn.tipsy) {
                // use browser-native tooltips for all but the fancy HTML ones
                $('.js-has-tooltip').each(function (index, element) {
                    $(element).tipsy({
                        html: true,
                        // some tooltips actually are around 175 px in height
                        gravity: $.fn.tipsy.autoBounds(175, 'nw'),
                        opacity: 1.0,
                        title: function () {
                            var elem = $(this),
                            tip,
                            toolTipped = elem.closest('.js-has-tooltip');
                            if ( 0 < toolTipped.length ) {
                                tip = toolTipped.prevAll('.js-tooltip').first();
                                if (0 < tip.length) { // if a .js-tooltip was found...
                                    // voodoo so inner div has full height of its container
                                    setTimeout(function () { $('.tipsy-inner').addClass('clearfix'); }, 100);
                                    return tip.html();
                                }
                            }
                        },
                        delayIn: 300,
                        fade: true
                    });
                });
            }
                
            /** Add placeholder functionality if needed */
            if (!Modernizr.input.placeholder) $('input, textarea').placeholder();
            
            /** Sorting Handler: Sort a table by column. */
            $(document).on('click', '.sortable', function (ev) {
                
                var anchor = $(this),
                    dir = anchor.is('.desc') ? 'asc' : 'desc',
                    sort = anchor.data('sort'),
                    container = anchor.closest('[data-url]'),
                    url = container.data('url'),
                    pagingArea = container.find('.paging-area'), 
                    params = {sort: sort, dir: dir};
                
                // add page size if paging is available
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
            $(document).on('click', '.paging-area .previous-page .button, .paging-area .next-page .button, .paging-area .page-size a', function (ev) {
                
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
                    });
                }
                return false; // return false to stop form submissions
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
                    input.val(value).change();
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
                    if (group != '') {
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
                        if (typeof button.attr('name') != 'undefined' && button.attr('name').length != 0) {
                            form.prepend('<input name="'+ button.attr('name') + '" value="' + button.attr('value') + '" type="hidden"/>');
                        }
                        form.trigger('submit');
                    }
                }
                return false;
            });
    
            /** Prevent forms from submitting via enter key */
            $(document).on('keydown', 'form.js-preventSubmitViaEnterKey', function (e) {
                // allow override submission elements
                if ($(e.target).hasClass('js-allowSubmitViaEnterKey')) {
                    return true;
                }
                if (e.keyCode == yg.keys.enter) {
                    return false;
                }
            });
    
            /** Close dialogs when clicking .js-close elements or the yukon.dialog.ok event fires. */
            $(document).on('click', '.js-close', function (ev) {
                $(ev.target).closest('.ui-dialog-content').dialog('close');
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

            /** Disable or enable all form controls */
            $('input.js-toggle:checkbox').each(function (idx, elem) {
                $(elem).on('change', function (e) {
                    mod.toggleInputs(e.target);
                });
                $(elem).on('click', function (e) {
                    mod.toggleInputs(e.target);
                });
                mod.toggleInputs(elem);
            });

            /** Select all ? */
            $("input.js-select-all").each(function (index, input) {
                $(input).on('focus', function (elem) {
                    elem.target.select();
                });
            });

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
                    popup = $(trigger.data('popup')),
                    focus;
                
                try { /* Close popup if the trigger is a toggle and the popup is open */
                    if (trigger.is('[data-popup-toggle]') && popup.dialog('isOpen')) {
                        popup.dialog('close');
                        // Return so we don't re-open it, return true to propigate event incase others are listening.
                        return true; 
                    }
                } catch (error) {/* Ignore error, occurs when dialog not initialized yet. */ }
                
                // show the popup
                mod.dialog(popup);
                
                // check for focus
                focus = popup.find('js-focus');
                if (focus.length) focus.focus();
            });
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
         * data-dialog -       If present the popup will have 'ok', 'cancel' buttons. See yukon.ui.buttons
         *                     function for button behaviors.
         * data-width  -       Width of the popup. Default is 'auto'.
         * data-height -       Height of the popup. Default is 'auto'.
         * data-title  -       The title of the popup.
         * data-event  -       If present and [data-dialog] is present, the value of [data-event] will be the name
         *                     of the event to fire when clicking the 'ok' button.
         * data-target -       If present and [data-dialog] is present' the value of [data-target] will be the 
         *                     target of the event fired when clicking the ok button.
         * data-url -          If present, the contents of the popup element will be replaced with the 
         *                     response of an ajax request to the url before the popup is shown.
         * data-load-event -   If present, this event will be fired right before the popup is shown.
         *                     If 'data-url' is used, the event will be fired after the dialog is loaded with
         *                     the response body.
         * data-popup-toggle - If present, the trigger element can be clicked to close the popup as well.
         *
         * Positioning options: see http://api.jqueryui.com/position/
         * data-position-my -  'left|center|right top|center|bottom', Order matters. Default is 'center'
         * data-position-at -  'left|center|right top|center|bottom', Order matters. Default is 'center'
         * data-position-of -  selector|element.  Default is 'window'.
         */
        dialog: function (popup, url) {
            
            popup = $(popup);
            var dialog = popup.is('[data-dialog]'),
                options = {
                    minWidth: popup.is('[data-min-width]') ? popup.data('minWidth') : '150',
                    width: popup.is('[data-width]') ? popup.data('width') : 'auto',
                    height: popup.is('[data-height]') ? popup.data('height') : 'auto',
                    minHeight: popup.is('[data-min-height]') ? popup.data('minHeight') : '150'
                },
                buttonOptions = {},
                positionOptions = {};
            
            if (popup.is('[data-title]')) options.title = popup.data('title');
            if (popup.is('[title]')) options.title = popup.attr('title');
            
            if (dialog) {
                if (popup.is('[data-ok-text]')) buttonOptions.okText = popup.data('okText');
                if (popup.is('[data-event]')) buttonOptions.event = popup.data('event');
                if (popup.is('[data-target]')) buttonOptions.target = popup.data('target');
                if (popup.is('[data-form]')) buttonOptions.form = popup.data('form');
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
                    if (popup.is('[data-load-event]')) {
                        popup.trigger(popup.data('loadEvent'));
                    }
                    popup.dialog(options);
                });
            } else {
                if (popup.is('[data-load-event]')) {
                    popup.trigger(popup.data('loadEvent'));
                }
                popup.dialog(options);
            }
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
        block: function (event) {
            var blockElement = $(event.target).closest('.js-block-this')[0];
           if (blockElement) {
               mod.elementGlass.show(blockElement);
           } else {
               mod.pageGlass.show();
           }
        },

        /** Unblock the closest valid container to the event's target, or the page */
        unblock: function (event) {
            var blockElement = $(event.target).closest('.js-block-this')[0];
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

        /** Add a success 'flash scope' message */
        flashSuccess: function (markup) {
            $('.main-container').addMessage({message: markup, messageClass: 'success'});
        },

        /** Add an error 'flash scope' message */
        flashError: function (markup) {
            $('.main-container').addMessage({message: markup, messageClass: 'error'});
        },

        /** Format a phone number input */
        formatPhone: function (input) {
            // strip the input down to just numbers, then format
            var stripped = input.value.replace(/[^\d]/g, ''),
                i,
                regex,
                format;
            if (stripped.length > 0) {
                for (i=0; i<yg.phone.formats.length; i++) {
                    regex = yg.phone.formats[i].regex;
                    format = yg.phone.formats[i].format;
                    if (regex.test(stripped)) {
                        input.value = stripped.replace(regex, format);
                        break;
                    }
                }
            } else {
                input.value = '';
            }
        },

        /** Disable or enable all inputs in a contianer */
        toggleInputs: function (input) {
            // find matching inputs. Note: jQuery next gets the immediately following
            // sibling, so we have to use nextAll here.
            var container = $($(input).nextAll('div.js-toggle')[0]),
                enable = input.checked,
                inputList = ['input', 'select', 'textarea', 'button'],
                inputInd;
            if (enable) {
                container.removeClass('disabled');
            } else {
                container.addClass('disabled');
            }
            for (inputInd = 0; inputInd < inputList.length; inputInd += 1) {
                container.find(inputList[inputInd]).each(function (idx, elem) {
                    $(elem).prop('disabled', !enable);
                });
            }
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
                var hours = time.getHours() % 12 == 0 ? 12 : time.getHours() % 12,
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
                        $(nextButton).on('click', function (event) {
                                mod.wizard.nextPage($(event.target).closest('.js-page'));
                        });
                    });

                    $(elem).find('.js-prev').each(function (index, prevButton) {
                        $(prevButton).on('click', function (event) {
                                mod.wizard.prevPage($(event.target).closest('.js-page'));
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
                var nextPage;
                    page = $(page);
                    
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
                // resize the glass
                glass.css('width', container.outerWidth()).css('height', container.outerHeight()).fadeIn(200);
            },
    
            resize: function (ev) {
                mod.elementGlass.redraw($(ev.currentTarget));
            }
        },

        /** Object to glass out the page, used by #block and #unblock */
        pageGlass: {
            show: function (args) {
                var defaults = $.extend({color:'#000', alpha: 0.25}, args),
                    glass = $('#modal-glass');
                
                if (glass == null) {
                        glass = $('<div>').attr('id', 'modal-glass').append(
                                    $('<div>').addClass('tint').append(
                                        $('<div>').addClass('loading')));
                    $('body').prepend(glass);
                }
                glass.find('.tint').css('opacity', defaults.alpha).css('background-color', defaults.color);
                glass.fadeIn(200);
            },
    
            hide: function () {
                $('#modal-glass').fadeOut(200);
            }
        },

        /**
         * @param jElem - JQuery element to be removed.  Expected to have attributes:
         *     data-removed-text: text to confirm operation (eg. 'Item removed from list')
         *     data-undo-text: text to revert change (eg. 'Undo')
         * @param actionDo - Remove action
         * @param actionUndo - Undo remove action
         */
        removeWithUndo: function (jElem, actionDo, actionUndo) {
            
            var elemType = jElem.prop('tagName'),
                undoneElements = $('#undone-elements'),
                removedText = jElem.closest('[data-removed-text]').attr('data-removed-text'),
                undoText = jElem.closest('[data-undo-text]').attr('data-undo-text'),
                undoLink = $('<a class="fr" href="javascript:void(0)">' + undoText + '</a>'),
                undo,
                undoTd;
    
            if (elemType === 'TR') {
                elemType = 'td colspan=100';
            }
    
            if (undoneElements.length === 0) {
                undoneElements = $('<div id="undone-elements"></div>');
                undoneElements.hide();
            }
    
            undo = $('<' + elemType + ' class="undo-row"></' + elemType + '>').hide();
            undo.append($('<span>' + removedText + '</span>'));
            undo.append(undoLink);
    
            actionDo();
    
            undoLink.click(function () {
                actionUndo();
                undo.fadeOut(100, function () {
                    undo.after(jElem);
                    jElem.fadeIn(100);
                    undo.remove();
                });
            });
    
            if (elemType === 'td colspan=100') {
                undoTd= undo;
                undoTd.show();
                undo = $('<tr></tr>').hide();
                undo.append(undoTd);
            }
    
            jElem.fadeOut(100, function () {
                jElem.after(undo);
                undoneElements.append(jElem);
                undo.fadeIn(100);
            });
        },

        initSitewideSearchAutocomplete: function () {
            var theInput = $('.yukon-search-form .search-field');
            theInput.autocomplete({
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
                    theInput.val(ui.item.value);
                    theInput.parents('.yukon-search-form').submit();
                },
                open: function () {
                    $(this).removeClass('ui-corner-all').addClass('ui-corner-top');
                },
                close: function () {
                    $(this).removeClass('ui-corner-top').addClass('ui-corner-all');
                }
            });
        }
    };
    
    return mod;
})();

/** JQUERY PLUGINS */
(function () {
    
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
    
})();

/** Initialize the lib */
$(function () {
    
    yukon.ui.init();
    yukon.ui.initSitewideSearchAutocomplete();
    
    //turn off ajax caching application-wide by default
    $.ajaxSetup({cache: false});
});
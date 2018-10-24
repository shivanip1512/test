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
        
        /** Returns the values of an Object.keys call. */
        values: function (obj) {
            var values = Object.keys(obj).map(function (key) {
                return obj[key];
            });
            return values;
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
            },
            
            email: function (email) {
                var isEmail = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
                return isEmail.test(email);
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
     * Convenience function for checking if any instances of the selected element(s) exist. 
     * @returns {(object|false)} The jQuery object containing the selection, or false if no elements match the selection. 
     */
    $.fn.exists = function () {
        return this.length > 0 ? this : false;
    };
    
    /** 
     * Flash an element's background with a color for a duration.
     * @param {object} [options] - Options hash containing color and duration.
     * @param {number} [options.duration=1500] - The duration in milliseconds the animation will last. Default 1500.
     * @param {string} [options.color=#fff288] - The color to flash the background. Default #fff288 (yellowish).
     */
    $.fn.flash = function (options) {
        return this.each(function () {
            
            options = $.extend({ 
                color: '#fff288', 
                duration: 1500,
                complete: yukon.nothing
            }, options || {});
            
            var me = $(this),
                prev = me.data('previousColor') ? me.data('previousColor') : me.css('background-color');
                me.data('previousColor', prev);
                
            me.stop(true)
            .css({ backgroundColor: options.color })
            .animate({ backgroundColor: prev }, options.duration, options.complete);
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
                    Object.keys(args.message).forEach(function (key) {
                        if (typeof (args.message[key]) === 'number' ||
                            typeof (args.message[key]) === 'string') {
                            messages.push(args.message[key]);
                        }
                    });
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
            showLabels: false,
            events: []
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
            this.options.events.push(event);
            this.draw();
        },
        
        /** 
         * Add events to the timeline
         * @param {Object[]} events
         * @param {string} events[].id - Unique identifier key. Will override any existing event with that id.
         * @param {number} events[].timestamp - Time of event. expressed as epoch timestamp.
         * @param {string} [events[].icon=icon-blank] - Icon to show on the timeline.
         * @param {string} [events[].message] - html to display in the tooltip.
         */
        addEvents: function (events) {
            var _self = this;
            events.forEach(function (event) {
                var exists = _self.options.events.filter(function (existingEvent) { return existingEvent.id === event.id});
                if (exists.length > 0) {
                    exists = event;
                } else {
                    _self.options.events.push(event);
                }
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
         * @param {string[]} eventIds - Unique identifiers of the events to remove.
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
            
            var container = this.element,
                begin = this.options.begin,
                end = this.options.end,
                beginText = moment(begin).tz(yg.timezone).format(yg.formats.date.long_date_time_hm),
                endText = moment(end).tz(yg.timezone).format(yg.formats.date.long_date_time_hm);
            
            $('<span class="timeline-label-begin">').text(beginText).appendTo(container);
            $('<span class="timeline-label-end">').text(endText).appendTo(container);
           
           //add labels
           if (this.options.showLabels) {
               var quarters = (this.options.end - this.options.begin) / 4,
               firstQuarterDate = new Date(this.options.begin + quarters),
               firstQuarterText = moment(firstQuarterDate).tz(yg.timezone).format(yg.formats.date.long_date_time_hm),
               secondQuarterDate = new Date(this.options.begin + quarters * 2),
               secondQuarterText = moment(secondQuarterDate).tz(yg.timezone).format(yg.formats.date.long_date_time_hm),
               thirdQuarterDate = new Date(this.options.begin + quarters * 3),
               thirdQuarterText = moment(thirdQuarterDate).tz(yg.timezone).format(yg.formats.date.long_date_time_hm);
               
               $('<span class="timeline-label-first">').text(firstQuarterText).appendTo(container);
               $('<span class="timeline-label-second">').text(secondQuarterText).appendTo(container);
               $('<span class="timeline-label-third">').text(thirdQuarterText).appendTo(container);
           }
            
        },
        
        /** 
         * Put all events on the timeline and cluster nearby events.
         */
        _drawEvents: function () {
            
            var container = this.element;
            var containerWidth = container.width();
            
            var begin = this.options.begin;
            var end = this.options.end;
            var events = this.options.events;
            
            // positionedEvents will only include elements between the bounds, and be sorted by time.
            var positionedEvents = events.filter(function (event) {
                return begin < event.timestamp && event.timestamp < end;
            })
            .sort(function (lhs, rhs) {
                return lhs.timestamp - rhs.timestamp;
            })
            .map(function (event) {
                var position = (event.timestamp - begin) / (end - begin) * containerWidth;
                return { event: event, position: position };
            });
            
            //  Bin the items together by proximity - if they are within 16 pixels, put them in the same bin
            var lastBin;
            var binnedEvents = positionedEvents.reduce(function (bins, positionedEvent) {
                if (!lastBin || positionedEvent.position - lastBin > 16) {
                    lastBin = positionedEvent.position;
                } 
                bins[lastBin] = bins[lastBin] || [];
                bins[lastBin].push(positionedEvent.event);
                return bins;
            }, {});
            
            //  Create timeline event spans for each set of binned events
            var timelineEvents = Object.keys(binnedEvents).map(function (position) {                
                var events = binnedEvents[position],
                    firstEvent = events[0],
                    span = $('<span class="timeline-event">')
                        .css({'left': position - 8 + 'px' });
                
                if (events.length > 1) {
                    // If there is there more than one event to cluster together, use the count as the icon
                    span.addClass('timeline-icon multi');

                    var count = events.length;
                    if (count > 99) {
                        count = ">99";
                        span.addClass('greaterThan99');
                    }
                    
                    $('<span class="timeline-event-count">')
                        .text(count)
                        .appendTo(span);
                    
                } else {
                    //  otherwise use the single event's icon
                    
                    $('<i class="M0 icon ' + (firstEvent.icon || 'icon-blank') + '"/>')
                        .appendTo(span);

                    if (firstEvent.icon) {
                        span.addClass('timeline-icon');
                    }
                }
                    
                var tooltip = $('<ul class="dn simple-list">')
                    .addClass('js-event-tooltip js-sticky-tooltip')
                    .attr('data-event-id', firstEvent.id);

                span.attr('data-tooltip', '.js-event-tooltip[data-event-id="' + firstEvent.id + '"]');
                
                //  Only use the first 10 events for the tooltip
                var tooltipEvents = events.slice(0, 10);

                var shouldDisplayIcons = tooltipEvents.some(function (event) {
                    return event.icon;
                });
                
                var tooltipEventItems = tooltipEvents.map(function (event) {
                    var text = '';
                    
                    if (shouldDisplayIcons) {
                        text += '<i class="icon ' + (event.icon || 'icon-blank') + '"></i>'; 
                    }
                    
                    text += moment(event.timestamp).tz(yg.timezone).format(yg.formats.date.full);
                    
                    if (event.message) {
                        text += ' - ' + event.message; 
                    }
                    
                    var tooltipEventItem = document.createElement("li");
                    tooltipEventItem.innerHTML = text;
                    
                    return tooltipEventItem;
                });
                
                tooltip.append(tooltipEventItems);
                
                if (events.length > 10) {
                    
                    var moreCount = events.length - 10;
                    var moreText = yg.text.more.replace(/\{0\}/g, '<strong>' + moreCount + '</strong>');
                    
                    $('<li class="tac">')
                        .html(moreText)
                        .appendTo(tooltip);
                }
                
                span.append(tooltip);
                
                return span;
            });
            
            container.append(timelineEvents);
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

$(function () {
    $(document).ajaxSend(function (ev, req, settings) {
        var csrfName = $('#ajax-csrf-token').attr("name");
        if (!(settings.type === 'GET' || settings.type === 'get' ||
            settings.method === 'GET' || settings.method === 'get')) {
            var data = {};
            var type = 'object';
            if (typeof settings.data === 'string') {
                try {
                    data = JSON.parse(settings.data);
                    type = 'json';
                } catch (e) {
                    type = 'urlEncode';
                    data = settings.data;
                    var joiner = '';
                    if(data!= ""){
                    	if(data.indexOf('?') === -1){
                            if(data.indexOf('=') === -1){
                                joiner = '?';
                            }else{
                                joiner = '&';
                            }
                        }else{
                            joiner = '&';
                        }
                        var csrfData = {},
                            csrfVal = $('#ajax-csrf-token').val();
                        csrfData[csrfName] = csrfVal;
                        data = data + joiner + $.param(csrfData);
                        
                    }else{
                        var mapData = {"com.cannontech.yukon.request.csrf.token" : $('#ajax-csrf-token').val()};
                        data = JSON.stringify(mapData);
                    }
                    
                }
            }
           
            if (typeof settings.data === 'object') {
                data = settings.data;
            }
            
            if(settings.data == null || (typeof settings.data != 'string' && typeof settings.data != 'object' && typeof settings.data != 'json')){
                var mapData = {"com.cannontech.yukon.request.csrf.token" : $('#ajax-csrf-token').val()};
                data = JSON.stringify(mapData);
            }else{
                data[csrfName] = $('#ajax-csrf-token').val();	
            }
            
            if (type === 'json') {
                data = JSON.stringify(data);
            }
            
            settings.data = data;
        }
    });
});
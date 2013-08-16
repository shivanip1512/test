var Yukon = (function (yukonMod) {
    return yukonMod;
})(Yukon || {});

// namespace function, so we don't have to put all those checks to see if
// modules exist and either create empty ones or set a reference to one
// that was previously created.
// Creates a global namespace. If 'Yukon' is in leading part of name,
// strip it off and hang the rest off of Yukon
// See Zakas, Maintainable JavaScript, pp. 72-73, and Stefanov,
// Javascript Patterns, pp. 89-90
Yukon.namespace = function (ns) {
    var parts = ns.split("."),
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

Yukon.namespace('Yukon.modules.base'); // creates our base module namespace

// define our base module
Yukon.modules.base = function (box) {
    // replacement for prototype.js bind call
    // Note: All "modern" browsers support Function.prototype.bind, IE9+, Safari
    // 5.1.4+ (but not ipad1!).
    // However, until prototype.js is purged entirely, its own version of bind
    // will be invoked. This can be verified this by setting a breakpoint just
    // before a call to bind.
    // prototype bind: func.bind(context[, arg1, arg2, ...])
    // Function.prototype.bind: same as prototype ECMAScript 5th edition
    // var yukonApi = {
    box.doBind = function (func, context) {
        var nargs = arguments.length, args = Array.prototype.slice
                .call(arguments), extraArgs = args.slice(2, args.length);
        return function () {
            var addIndex, argLength;
            if (nargs > 2) {
                // tack on addition arguments
                argLength = arguments.length;
                // to emulate the way prototype adds extra arguments, we must
                // prepend
                // additional arguments to whatever arguments this function is
                // called with
                for (addIndex = extraArgs.length - 1; addIndex >= 0; addIndex -= 1, argLength += 1) {
                    // the following allows us to "steal" an Array method and
                    // apply it to the passed in arguments object, which is, as
                    // they say,
                    // "array-like" but not an array proper
                    // passed to this function:
                    // func(a, b, ...)
                    // passed to doBind:
                    // doBind(func, context, arg1, arg2, ...)
                    // we want the following arguments passed to func, the
                    // callback, in the following order
                    // func(arg1, arg2, ..., a, b, ...)
                    [].unshift.call(arguments, extraArgs[addIndex]);
                }
            }
            if ('undefined' === typeof func) {
                throw "Yukon.doBind: func undefined";
            } else {
                func.apply(context, arguments);
            }
        };
    };
    // support for inheritance: inherit superType's prototype
    box.inheritPrototype = function (subType, superType) {
        var prototype = Object.create(superType.prototype);
        prototype.constructor = subType;
        subType.prototype = prototype;
    };
};

// create a sandbox with the functionality of the module names passed
// Copied from Javascript Patterns, Stefanov
function Sandbox () {
    // turning arguments into an array
    var args = Array.prototype.slice.call(arguments),
        // the last argument is the callback
        callback = args.pop(),
        // modules can be passed as an array or as individual parameters
        modules = (args[0] && typeof args[0] === "string") ? args : args[0],
        i;
    // make sure the function is called
    // as a constructor
    if (!(this instanceof Sandbox)) {
        return new Sandbox(modules, callback);
    }
    // add properties to 'this' as needed:
    // this.a = 1;

    // now add modules to the core 'this' object
    // no modules or "*" both mean "use all modules"
    if (!modules || modules === '*') {
        modules = [];
        for (i in Yukon.modules) {
            if (Yukon.modules.hasOwnProperty(i)) {
                modules.push(i);
            }
        }
    }
    // initialize the required modules
    for (i = 0; i < modules.length; i += 1) {
        Yukon.modules[modules[i]](this);
    }
    // call the callback
    callback(this);
}
// any prototype properties as needed
Sandbox.prototype = {
    name: "Yukon",
    version: "1.0",
    getName: function () {
        return this.name;
    }
};

// for modules outside the Yukon umbrella
Sandbox.modules = {};

/*
*   General purpose ui functionality for Yukon.
*   
*     REQUIRES:
*     * jQuery 1.6.4
*     * blockUI 2.39
*     * jQuery placeholder plug-in
*     
*     USAGE:
*     In day to day use, the Yukon.ui class will be your best bet at getting the functionality you
*     need on a page.  The former Yukon.uiUtils are more low level functions for use by other libraries,
*     but have been subsumed into Yukon.ui.
*/
Yukon.namespace("Yukon.modules.ui"); // creates our ui module ns
// create the Yukon.ui module sandbox
Yukon.modules.ui = function (mod) {
    var initialized = false;

    mod.init = function() {
        if (!initialized) {
            mod.autoWire();
            initialized = true;
            mod.wizard.init();
        }
    };
    
    mod.exclusiveSelect = function(item) {
        item = jQuery(item);
        item.siblings().removeClass('selected');
        item.addClass('selected');
    };

    mod.autoWire = function() {
        var html;
        // register listeners
        
        // Setup tipsy tooltips, all elements with a 'title' attribute
        // will have a nice looking tooltip.
        // To give an item a custom tooltip, give it a class of 'f-has-tooltip'
        // and precede it, in the markup, with an item of class f-tooltip that
        // has the content in HTML. The code below searches backward in the
        // document for the element with class f-tooltip
        //
        // tipsy initialization:
        //   attaches tooltip handlers to all elements with a class of f-has-tooltip
        //   or a title attribute
        jQuery (function() {
            var tooltipTargets = ['.f-has-tooltip', '[title]'],
                resetTipsyInner = function () {
                    // voodoo so inner div has full height of its container
                    setTimeout ( function () {
                        jQuery('.tipsy-inner').addClass('clearfix');
                    }, 300);
                };
            // some pages do not include the tipsy libary
            if ('undefined' === typeof jQuery.fn.tipsy) {
                return;
            }
            jQuery(tooltipTargets).each(function (index, element) {
                jQuery(element).tipsy({
                    html: true,
                    // some tooltips actually are around 175 px in height
                    gravity: jQuery.fn.tipsy.autoBounds(175, 'nw'),
                    opacity: 1.0,
                    title: function () {
                        var elem = jQuery(this),
                            tip,
                            toolTipped = elem.closest(".f-has-tooltip");
                        if ( 0 < toolTipped.length ) {
                            tip = toolTipped.prevAll(".f-tooltip").first();
                            if (0 < tip.length) { // if a .f-tooltip was found...
                                resetTipsyInner();
                                return tip.html();
                            } else {
                                    tip = elem.attr('original-title'); // tipsy stashes it here
                                    resetTipsyInner();
                                    return tip;
                            }
                        } else {
                            // in theory, we should never get here, but you never know
                            tip = elem.attr('original-title');
                            if ('undefined' === typeof tip || '' === tip) {
                                tip = '';
                            }
                            resetTipsyInner();
                            return tip;
                        }
                    }
                });
            });
        });

        /* initialize our keyboard table traversal (j/k keys) */
        jQuery(".compactResultsTable.f-traversable").traverse('tr', {
            table_row_helper: true
        });
        
        //ajaxPage
        jQuery(document).on('click', '.f-ajaxPage', function(e) {
            e.stopPropagation();
            jQuery(this.getAttribute("data-selector")).load(this.getAttribute("href"));
            return false;
        });
        
        //html5 placeholder support for IE
        jQuery.placeholder();
        
        //buttons that redirect the page on click
        jQuery(document).on('click', 'button[data-href]', function(event){window.location = jQuery(this).attr("data-href");});
        
        // page blockers
        jQuery(document).on('click', 'a.f-blocker, button.f-blocker', mod.block);
        jQuery(document).on('resize', '#modal_glass', mod.blockPage);
        
        // clear page blocker
        jQuery(document).on('click', '.f-clearBlocker', mod.unblockPage);
    
        // Disable a form element after clicked
        jQuery(document).on('click', '.f-disableAfterClick', function() {
            var button = jQuery(this),
                group,
                label,
                busyText,
                originalText,
                form;
            if (button.is(":input")) {
                this.disabled = true;
                group = button.attr('data-disable-group');
                if (group != '') {
                    jQuery("[data-disable-group='" + group + "']").each( function(idx) {
                        this.disabled = true;
                    });
                }
              
                // if this is a busy button, add the spinner icon and use the busy text
                if (button.is("[data-busy]")) {
                    button.addClass('busy');
                    // if this button has an icon hide it
                    button.children(".icon").hide();
                    button.children(".icon.busy").show();
                    
                    label = button.children(".label");
                    busyText = button.attr("data-busy");
                    if (label.length > 0 && busyText.length > 0) {
                        originalText = label.html(); 
                        label.html(busyText);
                        button.attr("data-busy", originalText); // just incase we need to put it back
                    }
                }
                
                //if this is a submit button, trigger the submit event on the form
                if (button.is(":submit")) {
                    form = jQuery(this.form);
                    
                    //insert the name and or value of the button into the form action
                    if (typeof button.attr("name") != "undefined" && button.attr("name").length != 0) {
                        form.prepend('<input name="'+ button.attr("name") + '" value="' + button.attr("value") + '" type="hidden"/>');
                    }
                    form.trigger("submit");
                }
            }
            return false;
        });
    
        //prevent forms from submitting via enter key
        jQuery(document).on('keydown', 'form.f-preventSubmitViaEnterKey', function(e) {
            //allow override submission elements
            if (jQuery(e.target).hasClass("f-allowSubmitViaEnterKey")) {
                return true;
            }
            if (e.keyCode == 13) {
                return false;
            }
        });
    
        // close popup on submit event
        jQuery(document).on('click', 'button.f-closePopupOnSubmit', function(event) {
            jQuery(event).closest('.popUpDiv').dialog('close');
        });

        jQuery(document).on('click', '.f-setHomePage', function(event) {
            var userId = jQuery("#changeHomepage_userId").val(),
                send = document.location.href;
            event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
            event.stopPropagation();

            jQuery.ajax({
                type: "POST",
                url: "/user/updatePreference.json",
                data: {'userId': userId, 'prefName': 'HOME_URL', 'prefValue': send},
            }).done( function(data) {
                if (data.success) {
                    alert("SAVED");
                } else {
                    alert("INTERNAL ERROR #9471: FAILED SAVING");
                }
            }).fail( function(nada) {
                alert("INTERNAL ERROR #9472: FAILED SAVING");
            });
            return false;
        });

        jQuery(".f-closeYukonApplicationDialog").click(function() {
            jQuery("#yukonApplicationDialog").dialog("close");
        });

        // resize it with the window
        
        // resize blocked elements w/ page resize
        
        $$("input.f-formatPhone").each(function(elem) {
            elem.observe('blur', function(event) {
                mod.formatPhone(event.element());
            });
            
            mod.formatPhone(elem);
        });
        jQuery(document).on('blur', 'input.f-formatPhone', function(event) {
            mod.formatPhone(event.target);
        });

        $$("input.f-toggle:checkbox").each(function(elem) {
            elem.observe('change', function(event) {
                mod.toggleInputs(event.element());
            });
            
            elem.observe('click', function(event) {
                mod.toggleInputs(event.element());
            });
            
            mod.toggleInputs(elem);
        });
    
        $$(".f-toggleSwitch").each(function(container) {
            
            container.select("input:radio").each(function(input, index) {
                var elem;
                input.hide();
                elem = input.up('.f-toggleSwitch').down('.f-switchInterface');
                if (!elem) {
                    input.up('.f-toggleSwitch').appendChild('<div class="f-switchInterface"></div>');
                    elem = input.up('.f-toggleSwitch').down('.f-switchInterface');
                }
            });
        });
        
        $$("input.f-selectAll").each(function(input) {
            input.observe('focus', function(elem) {
                elem.target.select();
            });
        });
        
        /* Focus the designated input element */
        mod._autofocus();
        
        html = jQuery('#f-page-actions')[0];
    
        if (typeof html !== 'undefined') {
            jQuery('#f-page-actions').remove();
            jQuery('#b-page-actions .dropdown-menu').html(html.innerHTML);
            jQuery('#b-page-actions').show();
        }

        /* Update page buttons */
        html = jQuery('#f-page-buttons')[0];
        
        if (typeof html !== 'undefined') {
            jQuery('#f-page-buttons').remove();
            jQuery('.page-actions').append(html.innerHTML);
        }
    };

    mod._AUTOFOCUS_TRIES = 0;
    mod._autofocus = function() {
        var focusElement = jQuery("[autofocus], .f-focus:first")[0];
        
        if (focusElement) {
            try { //Play nice with IE
                focusElement.focus();
            } catch(err) {
                //give the autofocus element 3 seconds to show itself
                if (mod._AUTOFOCUS_TRIES < 30) {
                    //certain browsers will error if the element to be focused is not yet visible
                    setTimeout('Yukon.ui._autofocus()', 100);
                }
            }
        }
    };

    /*
     * Sets the focus to the first input/textarea found on the page having a class of "error"
     */
    mod.focusFirstError = function() {
        mod._setFocusFirstError();
        mod._autofocus();
    };
    
    /*
     * Applies the "f-focus" class to the first input/textarea element having a class of "error"
     */
    mod._setFocusFirstError = function() {
        var error_field = jQuery("input.error, textarea.error").first();
        if (error_field.length === 1) {
            jQuery(".f-focus").removeClass("f-focus");
            error_field.addClass("f-focus");
        }
    };

    mod.block = function(event) {
       var blockElement = jQuery(event.target).closest(".f-block_this")[0];
       if (blockElement) {
           mod.elementGlass.show(blockElement);
       } else {
           mod.pageGlass.show();
       }
    };

    mod.unblock = function(element) {
        var blockElement = jQuery(event.target).closest(".f-block_this")[0];
        if (blockElement) {
            mod.elementGlass.hide(blockElement);
        } else {
            mod.pageGlass.hide();
        }
    };

    mod.blockPage = function(args) {
        mod.pageGlass.show();
    };

    mod.unblockPage = function() {
        mod.pageGlass.hide();
    };

    mod.flashSuccess = function(markup) {
        jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage CONFIRM"});
    };

    mod.flashError = function(markup) {
        jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage ERROR"});
    };

    mod.formatPhone = function(input) {
        // strip the input down to just numbers, then format
        var stripped = input.value.replace(/[^\d]/g, ""),
            i,
            regex,
            format;
        if (stripped.length > 0) {
            for (i=0; i<YG.PHONE.FORMATS.length; i++) {
                regex = YG.PHONE.FORMATS[i].regex;
                format = YG.PHONE.FORMATS[i].format;
                if (regex.test(stripped)) {
                    input.value = stripped.replace(regex, format);
                    break;
                }
            }
        } else {
            input.value = "";
        }
    };

    /**
     * args: { selector: String css selector for use by $$ classes: Array or
     * string of class names to toggle on targeted elements }
     */
    mod.toggleClass = function(args) {
        var classNames = args.classes,
            i;

        if (typeof(args.classes) == "string") {
            classNames = classNames.split(",");
        }

        for (i=0; i<classNames.length; i++) {
            $$(args.selector).invoke('toggleClassName', classNames);
        }
    };

    mod.toggleInputs = function(input) {
        // find matching inputs
        var container = input.next("div.f-toggle"),
            enable = input.checked;
        
        if (enable) {
            container.removeClassName('disabled');
        } else {
            container.addClassName('disabled');
        }
        
        container.select("input").each(function(elem) {
            if (enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });

        container.select("select").each(function(elem) {
            if (enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });
        
        container.select("textarea").each(function(elem) {
            if (enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });

        container.select("button").each(function(elem) {
            if (enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });
    };

    mod.pad = function(number, length) {
        
        var str = '' + number;
        while (str.length < length) {
            str = '0' + str;
        }
        return str;
    };

    mod.formatTime = function(time, opts) {
        var timeStr = ''; 
        if (!opts) {
            opts = {};
        }
        if (opts['24']) {
            if (opts.pad) {
                timeStr = mod.pad(time.getHours(), 2) + ':' + mod.pad(time.getMinutes(),2);
            } else {
                timeStr = time.getHours() + ':' + mod.pad(time.getMinutes(),2);
            }
        } else {
            var hours = time.getHours()%12 == 0 ? 12 : time.getHours()%12,
                meridian = '';
            if (opts.meridian) {
                meridian = time.getHours() >= 11 ? 'pm' : 'am';
            }
            
            if (opts.pad) {
                timeStr = pad(hours, 2) + ':' + mod.pad(time.getMinutes(),2) + meridian;
            } else {
                timeStr = hours + ':' + mod.pad(time.getMinutes(),2) + meridian;
            }
        }
        return timeStr;
    };

    mod.getParameterByName = function( name ) {
        var regexS,
            regex,
            results;
        name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
        regexS = "[\\?&]"+name+"=([^&#]*)";
        regex = new RegExp( regexS );
        results = regex.exec( window.location.href );
        if ( results == null ) {
            return null;
        } else {
            return decodeURIComponent(results[1].replace(/\+/g, " "));
        }
    };

    mod.wizard = {
        _initialized: false,
        
        init: function() {
            $$(".f-wizard").each(function(elem) {
                elem.select(".f-next").each(function(nextButton) {
                    nextButton.observe('click', function(event) {
                        mod.wizard.nextPage(event.element().up(".f-page"));
                    });
                });
                
                elem.select(".f-prev").each(function(prevButton) {
                    prevButton.observe('click', function(event) {
                        mod.wizard.prevPage(event.element().up(".f-page"));
                    });
                });
                
                elem.select(".f-page").each(function(elem, idx) {
                    if (idx > 0) {
                        elem.hide();
                    } else {
                        elem.show();
                    }
                });
            });
            
            mod.wizard._initialized = true;
        },
    
        nextPage : function(page) {
            var nextPage;
            page = jQuery(page);
            if (typeof page !== 'undefined') {
                nextPage = page.next(".f-page");
                if (typeof nextPage !== 'undefined') {
                    page.hide();
                    nextPage.show();
                }
            }
        },
    
        prevPage : function(page) {
            var prevPage;
            if (typeof page !== 'undefined') {
                prevPage = page.previous(".f-page");
                if (typeof prevPage !== 'undefined') {
                    page.hide();
                    prevPage.show();
                }
            }
        },
    
        /**
         * Resets the page of the wizard to the first/initial page. Does NOT do
         * anything with the contents
         * 
         * wizard: can be any element in the DOM. * If it is the f-wizard
         * container itself, it will reset the page * If it is an arbitrary
         * node, it will search for and reset ALL f-wizard containers within
         * 
         */
        reset : function(wizard) {
            wizard = jQuery(wizard);
            if (wizard.hasClass("f-wizard")) {
                jQuery(".f-page", wizard).hide();
                jQuery(".f-page:first", wizard).show();
            } else {
                jQuery(".f-wizard .f-page").hide();
                jQuery(".f-wizard .f-page:first").show();
            }
        }
    };
    
    mod.elementGlass = {
        show: function(element) {
            var element = jQuery(element),
                glass;
            if (element[0]) {
                glass = element.find(".glass");
                if (!glass[0]) {
                    element.prepend(jQuery("<div>").addClass("glass"));
                    glass = element.find(".glass");
                }
                return mod.elementGlass.redraw(glass);
            }
            // nothing to block
            return null;
        },
        
        hide: function(element) {
            jQuery(element).find('.glass:first').fadeOut(200, function(){jQuery(this).remove();});
        },
        
        redraw: function(glass) {
            var container = glass.closest(".f-block_this");
            // resize the glass
            glass.css('width', container.outerWidth()).css('height', container.outerHeight()).fadeIn(200);
        },

        resize: function(event) {
            mod.elementGlass.redraw(jQuery(event.currentTarget));
        }
    };
        
    mod.pageGlass = {
        show : function(args) {
            var defaults = jQuery.extend({color:'#000', alpha: 0.25}, args),
                glass = jQuery("#modal_glass");
            
            if (glass == null) {
                glass = jQuery('<div>').attr("id", "modal_glass").append(
                            jQuery('<div>').addClass('tint').append(
                                jQuery('<div>').addClass('loading')));
                jQuery('body').prepend(glass);
            }
            glass.find('.tint').css('opacity', defaults.alpha).css('background-color', defaults.color);
            glass.fadeIn(200);
        },

        hide : function() {
            jQuery("#modal_glass").fadeOut(200);
        }
    };
};

// merge created sandbox module with existing Yukon barebones module
Sandbox('base', function(basemod) {
    jQuery.extend(Yukon, basemod);
});

Sandbox('ui', function(uimod) {
    Yukon.ui = uimod; // so for now, 90 references to Yukon.ui don't have to be changed
});

// Really Prototype doesn't have this!
// does not appear to be used anywhere. generally has been replaced with jQuery().trigger
Element.prototype.trigger = function (eventName) {
    debugger;//force us into the debugger to see if this code executes anymore
    if (document.createEvent) {
        var evt = document.createEvent('HTMLEvents');
        evt.initEvent(eventName, true, true);
        return this.dispatchEvent(evt);
    }

    if (this.fireEvent) {
        return this.fireEvent('on' + eventName);
    }
};

jQuery.fn.selectText = function() {
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
// toggleDisabled, flashColor, flashYellow and flashColor are moving to jqueryAddons.js
jQuery.fn.toggleDisabled = function() {
    return this.each(function() {
        if (jQuery(this).is(":input")) {
            this.disabled = !this.disabled;
        }
    });
};

jQuery.fn.flashColor = function(args) {
    return this.each(function() {
        var _self = jQuery(this),
            prevColor = _self.data('previous_color') ? _self.data('previous_color') : _self.css('background-color');
        _self.data('previous_color', prevColor);

        if (typeof(args) === 'string') {
            _self.stop(true);
            _self.css({backgroundColor: args}).animate({backgroundColor: prevColor, duration: 1000});
        } else if (typeof(args) === 'object' && typeof(args.color) === 'string') {
            _self.stop(true);
            _self.css({backgroundColor: args.color}).animate({backgroundColor: prevColor}, args);
        }
    });
};

jQuery.fn.flashYellow = function (duration) {
    return this.each(function() {
        if (typeof(duration) != 'number') {
            duration = 0.8;
        }
        jQuery(this).flashColor({color: "#FF0", duration: duration*1000});
    });
};

// initialize the lib
jQuery(document).ready(function() {
    Yukon.ui.init();

    //turn off ajax caching application-wide by default
    jQuery.ajaxSetup({cache: false});
});


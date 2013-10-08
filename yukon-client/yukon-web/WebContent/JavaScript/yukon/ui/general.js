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

    mod.init = function () {
        if (!initialized) {
            mod.autoWire();
            initialized = true;
            mod.wizard.init();
        }
    };
    
    mod.exclusiveSelect = function (item) {
        item = jQuery(item);
        item.siblings().removeClass('selected');
        item.addClass('selected');
    };

    mod.busy = function (item) {
        var btn = jQuery(item),
          label,
          busyText,
          originalText;

        if (btn.is("[data-busy]")) {
            btn.attr('disabled', 'disabled');
            btn.addClass('busy');
            // if this button has an icon hide it
            btn.children(".icon").hide();
            btn.children(".icon.busy").show();

            label = btn.children(".label");
            busyText = btn.attr("data-busy");
            if (label.length > 0 && busyText.length > 0) {
                originalText = label.html(); 
                label.html(busyText);
                btn.data("data-original-text", originalText);
            }
        }
        return btn;
    };

    mod.unbusy = function (item) {
        var btn = jQuery(item),
        label,
        originalText;

        if (btn.is("[data-busy]")) {
            btn.removeAttr('disabled');
            btn.removeClass('busy');
            // if this button has an icon show it
            btn.children(".icon").show();
            btn.children(".icon.busy").hide();
            
            label = btn.children(".label");
            originalText = btn.data("data-original-text");
            if (typeof originalText !== 'undefined' && label.length > 0 && originalText.length > 0) {
                label.html(originalText);
            }
        }
        return btn;
    };

    mod.autoWire = function () {
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
        jQuery (function () {
            var tooltipTargets = ['.f-has-tooltip'],// use browser-native tooltips for all but the fancy HTML ones
                resetTipsyInner = function () {
                    // voodoo so inner div has full height of its container
                    setTimeout ( function () {
                        jQuery('.tipsy-inner').addClass('clearfix');
                    }, 100);
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
                            }
                        }
                    },
                    delayIn: 300,
                    fade: true
                });
            });
        });

        /* initialize our keyboard table traversal (j/k keys) */
        jQuery(".compactResultsTable.f-traversable").traverse('tr', {
            table_row_helper: true
        });
        
        //ajaxPage
        jQuery(document).on('click', '.f-ajaxPage', function (e) {
            e.stopPropagation();
            jQuery(this.getAttribute("data-selector")).load(this.getAttribute("href"));
            return false;
        });
        
        //html5 placeholder support for IE
        jQuery.placeholder();
        
        //buttons that redirect the page on click
        jQuery(document).on('click', 'button[data-href]', function (event){window.location = jQuery(this).attr("data-href");});
        
        // page blockers
        jQuery(document).on('click', 'a.f-blocker, button.f-blocker', mod.block);
        jQuery(document).on('resize', '#modal_glass', mod.blockPage);
        
        // clear page blocker
        jQuery(document).on('click', '.f-clearBlocker', mod.unblockPage);
    
        // Disable a form element after clicked
        jQuery(document).on('click', '.f-disableAfterClick', function () {
            var button = jQuery(this),
                group,
                form;
            if (button.is(":input")) {
                this.disabled = true;
                group = button.attr('data-disable-group');
                if (group != '') {
                    jQuery("[data-disable-group='" + group + "']").each( function (idx) {
                        this.disabled = true;
                    });
                }
              
                // if this is a busy button, add the spinner icon and use the busy text
                if (button.is("[data-busy]")) {
                    mod.busy(button);
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
        jQuery(document).on('keydown', 'form.f-preventSubmitViaEnterKey', function (e) {
            //allow override submission elements
            if (jQuery(e.target).hasClass("f-allowSubmitViaEnterKey")) {
                return true;
            }
            if (e.keyCode == 13) {
                return false;
            }
        });
    
        // close popup on submit event
        jQuery(document).on('click', 'button.f-closePopupOnSubmit', function (event) {
            jQuery(event).closest('.popUpDiv').dialog('close');
        });

        jQuery(document).on('click', '.f-setHomePage', function (event) {
            var userId = jQuery("#changeHomepage_userId").val(),
                send = document.location.href;
            event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
            event.stopPropagation();

            jQuery.ajax({
                type: "POST",
                url: "/user/updatePreference.json",
                data: {'userId': userId, 'prefName': 'HOME_URL', 'prefValue': send},
            }).done( function (data) {
                if (data.success) {
                    alert("SAVED");
                } else {
                    alert("INTERNAL ERROR #9471: FAILED SAVING");
                }
            }).fail( function (nada) {
                alert("INTERNAL ERROR #9472: FAILED SAVING");
            });
            return false;
        });

        jQuery(".f-closeYukonApplicationDialog").click(function () {
            jQuery("#yukonApplicationDialog").dialog("close");
        });

        // resize it with the window

        // resize blocked elements w/ page resize
 
        jQuery('input.f-formatPhone').each( function (idx, elem) {
            mod.formatPhone(elem);
        });
        jQuery(document).on('blur', 'input.f-formatPhone', function (event) {
            mod.formatPhone(event.target);
        });

        jQuery('input.f-toggle:checkbox').each(function (idx, elem) {
            jQuery(elem).on('change', function (e) {
                mod.toggleInputs(e.target);
            });
            jQuery(elem).on('click', function (e) {
                mod.toggleInputs(e.target);
            });
            mod.toggleInputs(elem);
        });

        jQuery("input.f-selectAll").each(function (index, input) {
            jQuery(input).on('focus', function (elem) {
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

        var additionalActions = jQuery('.f-page-additional-actions');
        additionalActions.each( function(index, html) {
            if (typeof html !== 'undefined') {
                jQuery('#b-page-actions .dropdown-menu').append(html.innerHTML);
                jQuery(html).remove();
            }
        });
    };

    mod._AUTOFOCUS_TRIES = 0;
    mod._autofocus = function () {
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
    mod.focusFirstError = function () {
        mod._setFocusFirstError();
        mod._autofocus();
    };

    /*
     * Applies the "f-focus" class to the first input/textarea element having a class of "error"
     */
    mod._setFocusFirstError = function () {
        var error_field = jQuery("input.error, textarea.error").first();
        if (error_field.length === 1) {
            jQuery(".f-focus").removeClass("f-focus");
            error_field.addClass("f-focus");
        }
    };

    mod.block = function (event) {
       var blockElement = jQuery(event.target).closest(".f-block_this")[0];
       if (blockElement) {
           mod.elementGlass.show(blockElement);
       } else {
           mod.pageGlass.show();
       }
    };

    mod.unblock = function (element) {
        var blockElement = jQuery(event.target).closest(".f-block_this")[0];
        if (blockElement) {
            mod.elementGlass.hide(blockElement);
        } else {
            mod.pageGlass.hide();
        }
    };

    mod.blockPage = function (args) {
        mod.pageGlass.show();
    };

    mod.unblockPage = function () {
        mod.pageGlass.hide();
    };

    mod.flashSuccess = function (markup) {
        jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage CONFIRM"});
    };

    mod.flashError = function (markup) {
        jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage ERROR"});
    };

    mod.formatPhone = function (input) {
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

    mod.toggleInputs = function (input) {
        // find matching inputs. Note: jQuery next gets the immediately following
        // sibling, so we have to use nextAll here.
        var container = jQuery(jQuery(input).nextAll("div.f-toggle")[0]),
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
                jQuery(elem).prop('disabled', !enable);
            });
        }
    };

    mod.pad = function (number, length) {

        var str = '' + number;
        while (str.length < length) {
            str = '0' + str;
        }
        return str;
    };

    mod.formatTime = function (time, opts) {
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

    mod.getParameterByName = function ( name ) {
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

        init: function () {
            jQuery(".f-wizard").each(function (idx, elem) {
                jQuery(elem).find(".f-next").each(function (index, nextButton) {
                    jQuery(nextButton).on('click', function (event) {
                        mod.wizard.nextPage(jQuery(event.target).closest(".f-page"));
                    });
                });

                jQuery(elem).find(".f-prev").each(function (index, prevButton) {
                    jQuery(prevButton).on('click', function (event) {
                        mod.wizard.prevPage(jQuery(event.target).closest(".f-page"));
                    });
                });

                jQuery(elem).find(".f-page").each(function (index, elem) {
                    if (idx > 0) {
                        jQuery(elem).hide();
                    } else {
                        jQuery(elem).show();
                    }
                });
            });

            mod.wizard._initialized = true;
        },

        nextPage : function (page) {
            var nextPage;
            page = jQuery(page);
            if (typeof page !== 'undefined') {
                nextPage = page.nextAll(".f-page")[0];
                if (typeof nextPage !== 'undefined') {
                    page.hide();
                    jQuery(nextPage).show();
                }
            }
        },

        prevPage : function (page) {
            var prevPage;
            if (typeof page !== 'undefined') {
                prevPage = page.prevAll(".f-page")[0];
                if (typeof prevPage !== 'undefined') {
                    jQuery(page).hide();
                    jQuery(prevPage).show();
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
        reset : function (wizard) {
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
        show: function (element) {
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

        hide: function (element) {
            jQuery(element).find('.glass:first').fadeOut(200, function () {jQuery(this).remove();});
        },

        redraw: function (glass) {
            var container = glass.closest(".f-block_this");
            // resize the glass
            glass.css('width', container.outerWidth()).css('height', container.outerHeight()).fadeIn(200);
        },

        resize: function (event) {
            mod.elementGlass.redraw(jQuery(event.currentTarget));
        }
    };

    mod.pageGlass = {
        show : function (args) {
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

        hide : function () {
            jQuery("#modal_glass").fadeOut(200);
        }
    };

    /**
     * @param jElem -jQuery element to be remvoed
     * @param actionDo - remove action
     * @param actionUndo - undo remove action
     * 
     * jElem (or an ancestor) is expected to have attributes
     *     data-removed-text: text to confirm operation (eg. 'Item removed from list')
     *     data-undo-text: text to revert change (eg. 'Undo')
     *
     */
    mod.removeWithUndo = function(jElem, actionDo, actionUndo) {
        var elemType = jElem.prop('tagName'),
            undoneElements = jQuery('#undone-elements'),
            removedText = jElem.closest('[data-removed-text]').attr('data-removed-text'),
            undoText = jElem.closest('[data-undo-text]').attr('data-undo-text'),
            undoLink = jQuery('<a class="fr" href="javascript:void(0)">' + undoText + '</a>'),
            undo,
            undoTd;

        if (elemType === 'TR') {
            elemType = 'td colspan=100';
        }

        if (undoneElements.length === 0) {
            undoneElements = jQuery('<div id="undone-elements"></div>');
            undoneElements.hide();
        }

        undo = jQuery('<' + elemType + ' class="undo-row"></' + elemType + '>').hide();
        undo.append(jQuery('<span>' + removedText + '</span>'));
        undo.append(undoLink);

        actionDo();

        undoLink.click(function() {
            actionUndo();
            undo.fadeOut(100, function() {
                undo.after(jElem);
                jElem.fadeIn(100);
                undo.remove();
            });
        });

        if (elemType === 'td colspan=100') {
            undoTd= undo;
            undoTd.show();
            undo = jQuery('<tr></tr>').hide();
            undo.append(undoTd);
        }

        jElem.fadeOut(100, function() {
            jElem.after(undo);
            undoneElements.append(jElem);
            undo.fadeIn(100);
        });
    };
};

// merge created sandbox module with existing Yukon barebones module
Sandbox('base', function (basemod) {
    jQuery.extend(Yukon, basemod);
});

Sandbox('ui', function (uimod) {
    Yukon.ui = uimod; // so for now, 90 references to Yukon.ui don't have to be changed
});

jQuery.fn.selectText = function () {
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
jQuery.fn.toggleDisabled = function () {
    return this.each(function () {
        if (jQuery(this).is(":input")) {
            this.disabled = !this.disabled;
        }
    });
};

jQuery.fn.flashColor = function (args) {
    return this.each(function () {
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
    return this.each(function () {
        if (typeof(duration) != 'number') {
            duration = 0.8;
        }
        jQuery(this).flashColor({color: "#FF0", duration: duration*1000});
    });
};

// initialize the lib
jQuery(document).ready(function () {
    Yukon.ui.init();

    //turn off ajax caching application-wide by default
    jQuery.ajaxSetup({cache: false});
});


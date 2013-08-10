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
*     need on a page.  Yukon.uiUtils are more low level functions for use by other libraries.
*     
*/

var Yukon = (function(mod) {
    //  replacement for prototype.js bind call
    //  Note: All "modern" browsers support Function.prototype.bind, IE9+, Safari
    //  5.1.4+ (but not ipad1!).
    //  However, until prototype.js is purged entirely, its own version of bind
    //  will be invoked.
    //  I verified this by setting a breakpoint just before a call to bind.
    //  prototype bind: func.bind(context[, arg1, arg2, ...])
    //  Function.prototype.bind: same as prototype ECMAScript 5th edition
    mod.doBind = function(func, context) {
        var nargs = arguments.length,
            args = Array.prototype.slice.call(arguments),
            extraArgs = args.slice(2, args.length);
        return function() {
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
    return mod;
}(Yukon || {}));

Yukon.ui = (function () {
    var initialized = false,
        uimod = {
        init : function() {
            if (!initialized) {
                this.autoWire();
                initialized = true;
                this.wizard.init();
            }
        },
        
        exclusiveSelect: function(item) {
            item = jQuery(item);
            item.siblings().removeClass('selected');
            item.addClass('selected');
            
        },
        
        autoWire: function() {
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
            
            /** TODO: add js to update criteria button when options are clicked */

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
            jQuery(document).on('click', 'a.f-blocker, button.f-blocker', Yukon.ui.block);
            jQuery(document).on('resize', '#modal_glass', Yukon.ui.blockPage);
            
            // clear page blocker
            jQuery(document).on('click', '.f-clearBlocker', Yukon.ui.unblockPage);
        
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
                        // if this button has an icon hide it
                        button.children(".icon").hide();
                        // only one of the spinner icons should be there
                        button.children(".icon-spinner-white").show();
                        button.children(".icon-spinner").show();
                        
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

            jQuery(".f-closeYukonApplicationDialog").click(function() {
                jQuery("#yukonApplicationDialog").dialog("close");
            });

            // resize it with the window
            
            // resize blocked elements w/ page resize
            
            $$("input.f-formatPhone").each(function(elem) {
                elem.observe('blur', function(event) {
                    Yukon.ui.formatPhone(event.element());
                });
                
                Yukon.ui.formatPhone(elem);
            });
            jQuery(document).on('blur', 'input.f-formatPhone', function(event) {
                Yukon.ui.formatPhone(event.target);
            });
            
            $$("input.f-toggle:checkbox").each(function(elem) {
                elem.observe('change', function(event) {
                    Yukon.ui.toggleInputs(event.element());
                });
                
                elem.observe('click', function(event) {
                    Yukon.ui.toggleInputs(event.element());
                });
                
                Yukon.ui.toggleInputs(elem);
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
            Yukon.ui._autofocus();
            
            /* Update page actions */
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
        },
        
        
        _AUTOFOCUS_TRIES: 0,
        _autofocus: function() {
            var focusElement = jQuery("[autofocus], .f-focus:first")[0];
            
            if (focusElement) {
                try { //Play nice with IE
                    focusElement.focus();
                } catch(err) {
                    //give the autofocus element 3 seconds to show itself
                    if (Yukon.ui._AUTOFOCUS_TRIES < 30) {
                        //certain browsers will error if the element to be focused is not yet visible
                        setTimeout('Yukon.ui._autofocus()', 100);
                    }
                }
            }
        },
    
        /*
         * Sets the focus to the first input/textarea found on the page having a class of "error"
         */
        focusFirstError: function() {
            Yukon.ui._setFocusFirstError();
            Yukon.ui._autofocus();
        },
        
        /*
         * Applies the "f-focus" class to the first input/textarea element having a class of "error"
         */
        _setFocusFirstError: function() {
            var error_field = jQuery("input.error, textarea.error").first();
            if (error_field.length === 1) {
                jQuery(".f-focus").removeClass("f-focus");
                error_field.addClass("f-focus");
            }
        },
        
        block: function(event) {
           var blockElement = jQuery(event.target).closest(".f-block_this")[0];
           if (blockElement) {
               Yukon.uiUtils.elementGlass.show(blockElement);
           } else {
               Yukon.uiUtils.pageGlass.show();
           }
        },
    
        unblock: function(element) {
            var blockElement = jQuery(event.target).closest(".f-block_this")[0];
            if (blockElement) {
                Yukon.uiUtils.elementGlass.hide(blockElement);
            } else {
                Yukon.uiUtils.pageGlass.hide();
            }
        },

        blockPage: function(args) {
            Yukon.uiUtils.pageGlass.show();
        },

        unblockPage: function() {
            Yukon.uiUtils.pageGlass.hide();
        },
    
        flashSuccess: function(markup) {
            jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage CONFIRM"});
        },
    
        flashError: function(markup) {
            jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage ERROR"});
        },
    
        formatPhone: function(input) {
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
        },
    
        /**
         * args: { selector: String css selector for use by $$ classes: Array or
         * string of class names to toggle on targeted elements }
         */
        toggleClass: function(args) {
            var classNames = args.classes,
                i;

            if (typeof(args.classes) == "string") {
                classNames = classNames.split(",");
            }

            for (i=0; i<classNames.length; i++) {
                $$(args.selector).invoke('toggleClassName', classNames);
            }
        },

        toggleInputs: function(input) {
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
        },
    
        pad: function(number, length) {
            
            var str = '' + number;
            while (str.length < length) {
                str = '0' + str;
            }
            return str;
        },
    
        formatTime: function(time, opts) {
            var timeStr = ''; 
            if (!opts) {
                opts = {};
            }
            if (opts['24']) {
                if (opts.pad) {
                    timeStr = this.pad(time.getHours(), 2) + ':' + this.pad(time.getMinutes(),2);
                } else {
                    timeStr = time.getHours() + ':' + this.pad(time.getMinutes(),2);
                }
            } else {
                var hours = time.getHours()%12 == 0 ? 12 : time.getHours()%12,
                    meridian = '';
                if (opts.meridian) {
                    meridian = time.getHours() >= 11 ? 'pm' : 'am';
                }
                
                if (opts.pad) {
                    timeStr = pad(hours, 2) + ':' + this.pad(time.getMinutes(),2) + meridian;
                } else {
                    timeStr = hours + ':' + this.pad(time.getMinutes(),2) + meridian;
                }
            }
            return timeStr;
        },
    
        getParameterByName: function( name ) {
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
        },
    
        wizard: {
            _initialized: false,
            
            init: function() {
                $$(".f-wizard").each(function(elem) {
                    elem.select(".f-next").each(function(nextButton) {
                        nextButton.observe('click', function(event) {
                            Yukon.ui.wizard.nextPage(event.element().up(".f-page"));
                        });
                    });
                    
                    elem.select(".f-prev").each(function(prevButton) {
                        prevButton.observe('click', function(event) {
                            Yukon.ui.wizard.prevPage(event.element().up(".f-page"));
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
                
                Yukon.ui.wizard._initialized = true;
            },
        
            nextPage: function(page) {
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
        
            prevPage: function(page) {
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
            reset: function(wizard) {
                wizard = jQuery(wizard);
                if (wizard.hasClass("f-wizard")) {
                    jQuery(".f-page", wizard).hide();
                    jQuery(".f-page:first", wizard).show();
                } else {
                    jQuery(".f-wizard .f-page").hide();
                    jQuery(".f-wizard .f-page:first").show();
                }
            }
        }
    };

    return uimod;
}());


Yukon.uiUtils = (function () {
    var uiUtilsMod = {
        elementGlass: {
            show: function(element) {
                var element = jQuery(element),
                    glass;
                if (element[0]) {
                    glass = element.find(".glass");
                    if (!glass[0]) {
                        element.prepend(jQuery("<div>").addClass("glass"));
                        glass = element.find(".glass");
                    }
                    return Yukon.uiUtils.elementGlass.redraw(glass);
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
                Yukon.uiUtils.elementGlass.redraw(jQuery(event.currentTarget));
            }
        }, 
            
        pageGlass : {
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
        }
    };
    return uiUtilsMod;
}());

// Really Prototype doesn't have this!
Element.prototype.trigger = function (eventName) {
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


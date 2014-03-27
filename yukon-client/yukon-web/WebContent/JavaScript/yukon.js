/** Polyfills */ 

// Array.forEach Polyfill
// From https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Array/forEach
// Production steps of ECMA-262, Edition 5, 15.4.4.18
// Reference: http://es5.github.com/#x15.4.4.18
if (!Array.prototype.forEach) {
  Array.prototype.forEach = function forEach(callback, thisArg) {
    'use strict';
    var T, k;

    if (this == null) {
      throw new TypeError("this is null or not defined");
    }

    var kValue,
        O = Object(this),
        len = O.length >>> 0;
    if ({}.toString.call(callback) !== "[object Function]") {
      throw new TypeError(callback + " is not a function");
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

// Object.create Polyfill
if (!Object.create) {
    Object.create = (function(){
        function F(){}

        return function(o){
            if (arguments.length != 1) {
                throw new Error('Object.create implementation only accepts one parameter.');
            }
            F.prototype = o;
            return new F();
        };
    })();
}

// Function.prototype.bind polyfill
if (!Function.prototype.bind) {
    Function.prototype.bind = function (oThis) {
      if (typeof this !== "function") {
        // closest thing possible to the ECMAScript 5 internal IsCallable function
        throw new TypeError("Function.prototype.bind - what is trying to be bound is not callable");
      }

      var aArgs = Array.prototype.slice.call(arguments, 1), 
          fToBind = this, 
          fNOP = function () {},
          fBound = function () {
            return fToBind.apply(this instanceof fNOP && oThis
                                   ? this
                                   : oThis,
                                 aArgs.concat(Array.prototype.slice.call(arguments)));
          };

      fNOP.prototype = this.prototype;
      fBound.prototype = new fNOP();

      return fBound;
    };
}

/** Yukon Module */
var yukon = (function (yukonMod) {
    return yukonMod;
})(yukon || {});

// namespace function, so we don't have to put all those checks to see if
// modules exist and either create empty ones or set a reference to one
// that was previously created.
// Creates a global namespace. If 'yukon' is in leading part of name,
// strip it off and hang the rest off of yukon
// See Zakas, Maintainable JavaScript, pp. 72-73, and Stefanov,
// Javascript Patterns, pp. 89-90
yukon.namespace = function (ns) {
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

yukon.namespace('yukon.modules.base'); // creates our base module namespace

// define our base module
yukon.modules.base = function (box) {
    // support for inheritance: inherit superType's prototype
    box.inheritPrototype = function (subType, superType) {
        var prototype = Object.create(superType.prototype);
        prototype.constructor = subType;
        subType.prototype = prototype;
    };

    // Handle app name prepending here
    box.url = function (url) {
        return YG.APP_NAME + url;
    };

    // JavaScript side of JsonTag.java
    box.fromJson = function(selector) {
        return JSON.parse($(selector).text());
    };
};

/** 
 * Create a sandbox with the functionality of the module names passed
 * Copied from Javascript Patterns, Stefanov
 */
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
        for (i in yukon.modules) {
            if (yukon.modules.hasOwnProperty(i)) {
                modules.push(i);
            }
        }
    }
    // initialize the required modules
    for (i = 0; i < modules.length; i += 1) {
        yukon.modules[modules[i]](this);
    }
    // call the callback
    callback(this);
}
// any prototype properties as needed
Sandbox.prototype = {
    name: "yukon",
    version: "1.0",
    getName: function () {
        return this.name;
    }
};

// for modules outside the yukon umbrella
Sandbox.modules = {};

/** 
 * UI module - General purpose ui functionality for yukon.
 * 
 *     REQUIRES:
 *     * jQuery 1.6.4
 *     * blockUI 2.39
 *     
 *     USAGE:
 *     In day to day use, the yukon.ui class will be your best bet at getting the functionality you
 *     need on a page.  The former yukon.uiUtils are more low level functions for use by other libraries,
 *     but have been subsumed into yukon.ui.
 */
yukon.namespace("yukon.modules.ui");

yukon.modules.ui = function (mod) {
    var initialized = false;

    mod.init = function () {
        if (!initialized) {
            mod.autoWire();
            initialized = true;
            mod.wizard.init();
        }
    };
    
    mod.exclusiveSelect = function (item) {
        item = $(item);
        item.siblings().removeClass('selected');
        item.addClass('selected');
    };

    mod.busy = function (item) {
        var btn = $(item),
          label,
          busyText,
          originalText;

        if (btn.is("[data-busy]")) {
            btn.prop('disabled', true);
            btn.addClass('busy');
            // if this button has an icon hide it
            btn.children(".icon").hide();
            btn.children(".icon.busy").show();

            label = btn.children(".b-label");
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
        var btn = $(item),
        label,
        originalText;

        if (btn.is("[data-busy]")) {
            btn.prop('disabled', false);
            btn.removeClass('busy');
            // if this button has an icon show it
            btn.children(".icon").show();
            btn.children(".icon.busy").hide();
            
            label = btn.children(".b-label");
            originalText = btn.data("data-original-text");
            if (typeof originalText !== 'undefined' && label.length > 0 && originalText.length > 0) {
                label.html(originalText);
            }
        }
        return btn;
    };

    mod.autoWire = function () {
        var html;
        
        $ (function () {
            
            /** Add placeholdr functionality if needed */
            if (!Modernizr.input.placeholder) {
                $('input, textarea').placeholder();
            }
            
            /** Add fancy tooltip support (tipsy)
             *  To give an item a custom tooltip, give it a class of 'f-has-tooltip'
             *  and precede it, in the markup, with an item of class f-tooltip that
             *  has the content in HTML. The code below searches backward in the
             *  document for the element with class f-tooltip
             * 
             *  tipsy initialization:
             *    attaches tooltip handlers to all elements with a class of f-has-tooltip
             *    or a title attribute
             */
            var tooltipTargets = ['.f-has-tooltip'],// use browser-native tooltips for all but the fancy HTML ones
                resetTipsyInner = function () {
                    // voodoo so inner div has full height of its container
                    setTimeout ( function () {
                        $('.tipsy-inner').addClass('clearfix');
                    }, 100);
                };
            // some pages do not include the tipsy libary
            if ('undefined' === typeof $.fn.tipsy) {
                return;
            }
            $(tooltipTargets).each(function (index, element) {
                $(element).tipsy({
                    html: true,
                    // some tooltips actually are around 175 px in height
                    gravity: $.fn.tipsy.autoBounds(175, 'nw'),
                    opacity: 1.0,
                    title: function () {
                        var elem = $(this),
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

        /** Initialize our keyboard table traversal (j/k keys) */
        $(".compact-results-table.f-traversable").traverse('tr', {
            table_row_helper: true
        });
        
        /** ajaxPage TODO Convert these to the use the one below and delete it */
        $(document).on('click', '.f-ajaxPage', function (e) {
            $(this.getAttribute("data-selector")).load(this.getAttribute("href"));
            return false;
        });
        
        /** Reload or page a container */
        $(document).on('click', '[data-reload]', function(event) {
            var elem = $(event.currentTarget),
            url = elem.data('reload'),
            target = elem.closest('[data-reloadable]'),
            completeEvent = target.data('loadable');
            
            if (target.length > 0) {
                target.load(url, function() {
                    if (completeEvent) {
                        target.trigger(completeEvent);
                    }
                });
            } else {
                window.location = url;
            }
            
            return false;
        });

        /** Elements that navigate on click */
        $(document).on('click', '[data-href]', function (event){window.location = $(this).attr("data-href");});
        
        /** Page blockers */
        $(document).on('click', '.f-blocker', mod.block);
        $(document).on('resize', '#modal-glass', mod.blockPage);
        
        /** Clear page blocker */
        $(document).on('click', '.f-clearBlocker', mod.unblockPage);
    
        /** Disable a form element after clicked */
        $(document).on('click', '.f-disable-after-click', function () {
            var button = $(this),
                group,
                form;
            if (button.is(":input")) {
                this.disabled = true;
                group = button.attr('data-disable-group');
                if (group != '') {
                    $("[data-disable-group='" + group + "']").each( function (idx) {
                        this.disabled = true;
                    });
                }
              
                // if this is a busy button, add the spinner icon and use the busy text
                if (button.is("[data-busy]")) {
                    mod.busy(button);
                }
                
                //if this is a submit button, trigger the submit event on the form
                if (button.is(":submit")) {
                    form = $(this.form);
                    
                    //insert the name and or value of the button into the form action
                    if (typeof button.attr("name") != "undefined" && button.attr("name").length != 0) {
                        form.prepend('<input name="'+ button.attr("name") + '" value="' + button.attr("value") + '" type="hidden"/>');
                    }
                    form.trigger("submit");
                }
            }
            return false;
        });
    
        /** Prevent forms from submitting via enter key */
        $(document).on('keydown', 'form.f-preventSubmitViaEnterKey', function (e) {
            //allow override submission elements
            if ($(e.target).hasClass("f-allowSubmitViaEnterKey")) {
                return true;
            }
            if (e.keyCode == 13) {
                return false;
            }
        });
    
        /** Close 'Apps' popup */
        $('.f-closeYukonApplicationDialog').click(function () {
            $('#yukonApplicationDialog').dialog('close');
            });

        /** Close a dialog */
        $(document).on('click', '.f-close', function (event) {
            $(this).closest('.ui-dialog-content').dialog('close');
        });

        /** Format phone numbers initially and on input blur */
        $('input.f-formatPhone').each( function (idx, elem) {
            mod.formatPhone(elem);
        });
        $(document).on('blur', 'input.f-formatPhone', function (event) {
            mod.formatPhone(event.target);
        });

        /** Disable or enable all form controls */
        $('input.f-toggle:checkbox').each(function (idx, elem) {
            $(elem).on('change', function (e) {
                mod.toggleInputs(e.target);
            });
            $(elem).on('click', function (e) {
                mod.toggleInputs(e.target);
            });
            mod.toggleInputs(elem);
        });

        /** Select all ? */
        $("input.f-selectAll").each(function (index, input) {
            $(input).on('focus', function (elem) {
                elem.target.select();
            });
        });

        /** Focus the designated input element */
        mod._autofocus();

        /** Init page 'Actions' button */
        html = $('#f-page-actions')[0];
        if (typeof html !== 'undefined') {
            $('#f-page-actions').remove();
            var menu = $('#b-page-actions .dropdown-menu');
            menu.html(html.innerHTML);
            if (menu.find('.icon').length === menu.find('.icon-blank').length) {
                menu.addClass('no-icons');
            }
            $('#b-page-actions').show();
        }

        /** Init page buttons */
        html = $('#f-page-buttons')[0];
        if (typeof html !== 'undefined') {
            $('#f-page-buttons').remove();
            $('.page-actions').append(html.innerHTML);
        }
        
        /** Add additional options to page 'Actions' button */
        $('.f-page-additional-actions').each(function(index, elem) {
            $('#b-page-actions .dropdown-menu').append(elem.innerHTML);
            $(elem).remove();
        });
        
        $('.tabbed-container.f-init').tabs().show();
    };

    mod._AUTOFOCUS_TRIES = 0;
    mod._autofocus = function () {
        var focusElement = $("[autofocus], .f-focus:first")[0];
        
        if (focusElement) {
            try { //Play nice with IE
                focusElement.focus();
            } catch(err) {
                //give the autofocus element 3 seconds to show itself
                if (mod._AUTOFOCUS_TRIES < 30) {
                    //certain browsers will error if the element to be focused is not yet visible
                    setTimeout('yukon.ui._autofocus()', 100);
                }
            }
        }
    };

    /** Sets the focus to the first input/textarea found on the page having a class of "error" */
    mod.focusFirstError = function () {
        mod._setFocusFirstError();
        mod._autofocus();
    };

    /** Applies the "f-focus" class to the first input/textarea element having a class of "error" */
    mod._setFocusFirstError = function () {
        var error_field = $("input.error, textarea.error").first();
        if (error_field.length === 1) {
            $(".f-focus").removeClass("f-focus");
            error_field.addClass("f-focus");
        }
    };

    /** Block out the closest valid container, or the page */
    mod.block = function (event) {
       var blockElement = $(event.target).closest(".f-block-this")[0];
       if (blockElement) {
           mod.elementGlass.show(blockElement);
       } else {
           mod.pageGlass.show();
       }
    };

    /** Unblock the closest valid container, or the page */
    mod.unblock = function (event) {
        var blockElement = $(event.target).closest(".f-block-this")[0];
        if (blockElement) {
            mod.elementGlass.hide(blockElement);
        } else {
            mod.pageGlass.hide();
        }
    };

    /** Block out the whole page */
    mod.blockPage = function (args) {
        mod.pageGlass.show();
    };

    /** Unblock the whole page */
    mod.unblockPage = function () {
        mod.pageGlass.hide();
    };

    /** Add a success 'flash scope' message */
    mod.flashSuccess = function (markup) {
        $(".main-container").addMessage({message: markup, messageClass: "success"});
    };

    /** Add an error 'flash scope' message */
    mod.flashError = function (markup) {
        $(".main-container").addMessage({message: markup, messageClass: "error"});
    };

    /** Format a phone number input */
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

    /** Disable or enable all inputs in a contianer */
    mod.toggleInputs = function (input) {
        // find matching inputs. Note: jQuery next gets the immediately following
        // sibling, so we have to use nextAll here.
        var container = $($(input).nextAll("div.f-toggle")[0]),
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
    };

    /** Pad a string */
    mod.pad = function (number, length) {

        var str = '' + number;
        while (str.length < length) {
            str = '0' + str;
        }
        return str;
    };

    /** Format time, (do we still need yukon.format.time.js?) */
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

    /** Retrieve a request parameter by name */
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

    /** Manages wizard style paging, useful in complex popups. */
    mod.wizard = {
        _initialized: false,

        init: function () {
            $(".f-wizard").each(function (idx, elem) {
                $(elem).find(".f-next").each(function (index, nextButton) {
                    $(nextButton).on('click', function (event) {
                        mod.wizard.nextPage($(event.target).closest(".f-page"));
                    });
                });

                $(elem).find(".f-prev").each(function (index, prevButton) {
                    $(prevButton).on('click', function (event) {
                        mod.wizard.prevPage($(event.target).closest(".f-page"));
                    });
                });

                $(elem).find(".f-page").each(function (index, elem) {
                    if (idx > 0) {
                        $(elem).hide();
                    } else {
                        $(elem).show();
                    }
                });
            });

            mod.wizard._initialized = true;
        },

        nextPage : function (page) {
            var nextPage;
            page = $(page);
            if (typeof page !== 'undefined') {
                nextPage = page.nextAll(".f-page")[0];
                if (typeof nextPage !== 'undefined') {
                    page.hide();
                    $(nextPage).show();
                }
            }
        },

        prevPage : function (page) {
            var prevPage;
            if (typeof page !== 'undefined') {
                prevPage = page.prevAll(".f-page")[0];
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
         * wizard: can be any element in the DOM. * If it is the f-wizard
         * container itself, it will reset the page * If it is an arbitrary
         * node, it will search for and reset ALL f-wizard containers within
         * 
         */
        reset : function (wizard) {
            wizard = $(wizard);
            if (wizard.hasClass("f-wizard")) {
                $(".f-page", wizard).hide();
                $(".f-page:first", wizard).show();
            } else {
                $(".f-wizard .f-page").hide();
                $(".f-wizard .f-page:first").show();
            }
        }
    };

    /** Object to glass out an element, used by #block and #unblock */
    mod.elementGlass = {
        show: function (element) {
            var jqElement = $(element),
                glass;
            if (jqElement[0]) {
                glass = jqElement.find(".glass");
                if (!glass[0]) {
                    jqElement.prepend($("<div>").addClass("glass"));
                    glass = jqElement.find(".glass");
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
            var container = glass.closest(".f-block-this");
            // resize the glass
            glass.css('width', container.outerWidth()).css('height', container.outerHeight()).fadeIn(200);
        },

        resize: function (event) {
            mod.elementGlass.redraw($(event.currentTarget));
        }
    };

    /** Object to glass out the page, used by #block and #unblock */
    mod.pageGlass = {
        show : function (args) {
            var defaults = $.extend({color:'#000', alpha: 0.25}, args),
                glass = $("#modal-glass");
            
            if (glass == null) {
                glass = $('<div>').attr("id", "modal-glass").append(
                            $('<div>').addClass('tint').append(
                                $('<div>').addClass('loading')));
                $('body').prepend(glass);
            }
            glass.find('.tint').css('opacity', defaults.alpha).css('background-color', defaults.color);
            glass.fadeIn(200);
        },

        hide : function () {
            $("#modal-glass").fadeOut(200);
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
            undo = $('<tr></tr>').hide();
            undo.append(undoTd);
        }

        jElem.fadeOut(100, function() {
            jElem.after(undo);
            undoneElements.append(jElem);
            undo.fadeIn(100);
        });
    };

    mod.initSitewideSearchAutocomplete = function() {
        var theInput = $( ".yukon-search-form .search-field" );
        theInput.autocomplete({
            delay: 100, // Delay 100ms after keyUp before sending request
            minLength: 2, // user must type 2 characters before any search is done
            source: function(request, response) {
                $.ajax({
                    type: 'get',
                    url: yukon.url('/search/autocomplete.json'),
                    dataType: "json",
                    data: {
                        q: request.term
                    }
                }).done(function(data) {
                    response($.map(data, function(item) {
                        return {
                            label: item,
                            value: item
                        };
                    }));
                });
            },
            select: function( event, ui ) {
                theInput.val(ui.item.value);
                theInput.parents(".yukon-search-form").submit();
            },
            open: function() {
                $( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
            },
            close: function() {
                $( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
            }
        });
    };
};

/** Merge created sandbox module with existing yukon barebones module */
Sandbox('base', function (basemod) {
    $.extend(yukon, basemod);
});

Sandbox('ui', function (uimod) {
    yukon.ui = uimod; // so for now, 90 references to yukon.ui don't have to be changed
});

/** Add some helpful functionality to jQuery */
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

$.fn.toggleDisabled = function () {
    return this.each(function () {
        if ($(this).is(":input")) {
            this.disabled = !this.disabled;
        }
    });
};

$.fn.flashColor = function (args) {
    return this.each(function () {
        var _self = $(this),
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

$.fn.flashYellow = function (duration) {
    return this.each(function () {
        if (typeof(duration) != 'number') {
            duration = 0.8;
        }
        $(this).flashColor({color: "#FF0", duration: duration*1000});
    });
};

/** Initialize the lib */
$( function () {
    yukon.ui.init();
    yukon.ui.initSitewideSearchAutocomplete();

    //turn off ajax caching application-wide by default
    $.ajaxSetup({cache: false});
});


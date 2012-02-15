/*
*   General purpose ui functionality for Yukon.
*   
*     REQUIRES:
*     * jQuery 1.6.4
*     * blockUI 2.39
*     
*     USAGE:
*     In day to day use, the Yukon.ui class will be your best bet at getting the functionality you
*     need on a page.  Yukon.uiUtils are more low level functions for use by other libraries.
*     
*/

if(typeof(Yukon) == 'undefined') {
    Yukon = {};
}

Yukon.ui = {
    _initialized : false,

    init : function() {
        if (!this._initialized) {
            this.autoWire();
            this._initialized = true;
            this.wizard.init();
        }
    },
    
    exclusiveSelect: function(item) {
        item = jQuery(item);
        item.siblings().removeClass('selected');
        item.addClass('selected');
        
    },
    
    autoWire: function() {
        // register listeners
        
        //buttons that redirect the page on click
        jQuery(document).delegate("button[data-href]", 'click', function(event){window.location = jQuery(this).attr("data-href");});
        
        // page blockers
        jQuery(document).delegate('a.f_blocker, button.f_blocker', 'click', Yukon.ui.block);
        jQuery(document).delegate('#modal_glass', 'resize', Yukon.ui.blockPage);
        
        // clear page blocker
        jQuery(document).delegate('.f_clearBlocker', 'click', Yukon.ui.unblockPage);
        
        // close popup on submit event
        
        // close popup on submit event
        jQuery(document).delegate("button.f_closePopupOnSubmit", 'click', function(event){
            jQuery(event).closest('.popUpDiv').hide();
        });
        
        // resize it with the window
        
        // resize blocked elements w/ page resize
        
        $$("input.f_formatPhone").each(function(elem){
            elem.observe('blur', function(event){
                Yukon.ui.formatPhone(event.element());
            });
            
            Yukon.ui.formatPhone(elem);
        });
        

        $$("input.f_toggle:checkbox").each(function(elem){
            elem.observe('change', function(event){
                Yukon.ui.toggleInputs(event.element());
            });
            
            elem.observe('click', function(event){
                Yukon.ui.toggleInputs(event.element());
            });
            
            Yukon.ui.toggleInputs(elem);
        });
        
        $$(".f_toggleSwitch").each(function(container){
            
            container.select("input:radio").each(function(input, index){
                input.hide();
                var elem = input.up('.f_toggleSwitch').down('.f_switchInterface');
                if(!elem){
                    input.up('.f_toggleSwitch').appendChild('<div class="f_switchInterface"></div>');
                    elem = input.up('.f_toggleSwitch').down('.f_switchInterface');
                }
            });
        });
        
        $$("input.f_selectAll").each(function(input){
            input.observe('focus', function(elem){
                elem.target.select();
            });
        });
        
        Yukon.uiUtils.tabs.init();
        
        /*
         * Focus the designated input element
         */
        var focusElement = $$(".f_focus:first")[0];
        if(focusElement) {
            focusElement.focus();
        }
    },
    
    block: function(element){
       var blockElement = jQuery(element).closest(".f_block_this");
       if(blockElement[0]){
           Yukon.uiUtils.elementGlass.show(blockElement[0]);
       }else{
           Yukon.uiUtils.pageGlass.show();
       }
    },
    
    unblock: function(element){
        element = jQuery(element);
        if(element.hasClass("f_block_this")){
            Yukon.uiUtils.elementGlass.hide(element);
        }else{
            Yukon.uiUtils.elementGlass.hide(element.closest(".f_block_this"));
        }
    },

    blockPage: function(args) {
        Yukon.uiUtils.pageGlass.show();
    },

    unblockPage: function() {
        Yukon.uiUtils.pageGlass.hide();
    },
    
    flashSuccess: function(markup){
        jQuery("#Content").addMessage({message: markup, messageClass: "userMessage CONFIRM"});
    },
    
    formatPhone: function(input){
        // strip the input down to just numbers, then format
        var stripped = input.value.replace(/[^\d]/g, "");
        if(stripped.length > 0) {
            for(var i=0; i<YG.PHONE.FORMATS.length; i++){
                var regex = YG.PHONE.FORMATS[i].regex;
                var format = YG.PHONE.FORMATS[i].format;
                if(regex.test(stripped)){
                    input.value = stripped.replace(regex, format);
                    break;
                }
            }
        } else {
            input.value = "";
            input.removeClassName('error');
        }
    },
    
    /**
     * args: { selector: String css selector for use by $$ classes: Array or
     * string of class names to toggle on targeted elements }
     */
    toggleClass: function(args){
        var classNames = args.classes;
        
        if(typeof(args.classes) == "string"){
            classNames = classNames.split(",");
        }
        
        for(var i=0; i<classNames.length; i++){
            $$(args.selector).invoke('toggleClassName', classNames);
        }
    },

    toggleInputs: function(input){
        // find matching inputs
        var container = input.next("div.f_toggle");
        var enable = input.checked;
        
        if(enable) {
            container.removeClassName('disabled');
        } else {
            container.addClassName('disabled');
        }
        
        container.select("input").each(function(elem){
            if(enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });
        
        container.select("select").each(function(elem){
            if(enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });
        
        container.select("textarea").each(function(elem){
            if(enable) {
                elem.enable();
            } else {
                elem.disable();
            }
        });
        
        container.select("button").each(function(elem){
            if(enable) {
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
        if(!opts){
            opts = {};
        }
        var timeStr = ''; 
        if(opts['24']){
            if(opts.pad){
                timeStr = this.pad(time.getHours(), 2) + ':' + this.pad(time.getMinutes(),2);
            }else{
                timeStr = time.getHours() + ':' + this.pad(time.getMinutes(),2);
            }
        }else{
            var hours = time.getHours()%12 == 0 ? 12 : time.getHours()%12;
            var meridian = '';
            if(opts.meridian){
                meridian = time.getHours() >= 11 ? 'pm' : 'am';
            }
            
            if(opts.pad){
                timeStr = pad(hours, 2) + ':' + this.pad(time.getMinutes(),2) + meridian;
            }else{
                timeStr = hours + ':' + this.pad(time.getMinutes(),2) + meridian;
            }
        }
        return timeStr;
    },
    
    getParameterByName: function( name ) {
      name = name.replace(/[\[]/,"\\\[").replace(/[\]]/,"\\\]");
      var regexS = "[\\?&]"+name+"=([^&#]*)";
      var regex = new RegExp( regexS );
      var results = regex.exec( window.location.href );
      if( results == null )
        return null;
      else
        return decodeURIComponent(results[1].replace(/\+/g, " "));
    },
    
    wizard: {
        _initialized: false,
        
        init: function() {
            $$(".f_wizard").each(function(elem){
                elem.select(".f_next").each(function(nextButton){
                    nextButton.observe('click', function(event){
                        Yukon.ui.wizard.nextPage(event.element().up(".f_page"))
                    });
                });
                
                elem.select(".f_prev").each(function(prevButton){
                    prevButton.observe('click', function(event){
                        Yukon.ui.wizard.prevPage(event.element().up(".f_page"))
                    });
                });
                
                elem.select(".f_page").each(function(elem, idx){
                    if(idx > 0){
                        elem.hide();
                    }else{
                        elem.show();
                    }
                });
            });
            
            Yukon.ui.wizard._initialized = true;
        },
        
        nextPage: function(page) {
            if(typeof(page) != 'undefined') {
                var nextPage = page.next(".f_page");
                if(typeof(nextPage) != 'undefined') {
                    page.hide();
                    nextPage.show();
                }
            }
        },
        
        prevPage: function(page) {
            if(typeof(page) != 'undefined') {
                var prevPage = page.previous(".f_page");
                if(typeof(prevPage) != 'undefined') {
                    page.hide();
                    prevPage.show();
                }
            }
        },
        
        /**
         * Resets the page of the wizard to the first/initial page. Does NOT do
         * anything with the contents
         * 
         * wizard: can be any element in the DOM. * If it is the f_wizard
         * container itself, it will reset the page * If it is an arbitrary
         * node, it will search for and reset ALL f_wizard containers within
         * 
         */
        reset: function(wizard) {
            if(wizard.hasClassName("f_wizard")){
                wizard.select(".f_page").each(function(elem, idx){
                    if(idx > 0){
                        elem.hide();
                    }else{
                        elem.show();
                    }
                });
            }else{
                wizard.select(".f_wizard").each(function(elem){
                    elem.select(".f_page").each(function(elem, idx){
                        if(idx > 0){
                            elem.hide();
                        }else{
                            elem.show();
                        }
                    });
                });
            }
        }
    }
};


Yukon.uiUtils = {
    elementGlass: {
        show: function(element) {
            var element = jQuery(element);
            if(element[0]){
                var glass = element.find(".glass");
                if(!glass[0]){
                    element.prepend(jQuery("<div>").addClass("glass"));
                    glass = element.find(".glass");
                }
                return Yukon.uiUtils.elementGlass.redraw(glass);
            }
            // nothing to block
            return null;
        },
        
        hide: function(element) {
            jQuery(element).find('.glass:first').fadeOut(200, function(){jQuery(this).remove()});
        },
        
        redraw: function(glass) {
            var container = glass.closest(".f_block_this");
            // resize the glass
            glass.css('width', container.outerWidth()).css('height', container.outerHeight()).fadeIn(200);
        },
        
        resize: function(event) {
            Yukon.uiUtils.elementGlass.redraw(jQuery(event.currentTarget));
        }
    }, 
        
    pageGlass : {
        show : function(args) {
            var defaults = jQuery.extend({color:'#000', alpha: 0.25}, args);
            var glass = jQuery("#modal_glass");
            
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
    },
    
    tabs: {
        init: function(args){
            var defaults = {};
            jQuery(".f_tabs").tabs(jQuery.extend(defaults, args));
        }
    }
};

// Really Prototype doesn't have this!
Element.prototype.trigger = function(eventName)
{
    if (document.createEvent){
        var evt = document.createEvent('HTMLEvents');
        evt.initEvent(eventName, true, true);
        return this.dispatchEvent(evt);
    }

    if (this.fireEvent){
        return this.fireEvent('on' + eventName);
    }
}

jQuery.fn.selectText = function() {
    var text = this[0];
    if (document.body.createTextRange) {
        var range = document.body.createTextRange();
        range.moveToElementText(text);
        range.select();
    } else if (window.getSelection) {
        var selection = window.getSelection();        
        var range = document.createRange();
        range.selectNodeContents(text);
        selection.removeAllRanges();
        selection.addRange(range);
    }
};

// initialize the lib
jQuery(document).ready(function(){
    Yukon.ui.init();
});
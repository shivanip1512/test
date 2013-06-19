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

if(typeof(Yukon) == 'undefined') {
    Yukon = {};
}

jQuery.fn.flashColor = function(args) {
	return this.each(function() {
		var _self = jQuery(this);
		var prevColor = _self.data('previous_color') ? _self.data('previous_color') : _self.css('background-color');
		_self.data('previous_color', prevColor);

		if (typeof(args) === 'string') {
			_self.stop(true);
			_self.css({backgroundColor: args}).animate({backgroundColor: prevColor, duration: 1000});
		} else if(typeof(args) === 'object' && typeof(args.color) === 'string'){
			_self.stop(true);
			_self.css({backgroundColor: args.color}).animate({backgroundColor: prevColor}, args);
		}
	});
};

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
        
        /* Setup jquery ui tooltips, all elements with a 'title' attribute
         * will have a nice looking tooltip.
         * To give an item a custom tooltip, give it a class of 'f-has-tooltip'
         * and follow it with an item of class f-tooltip that has the content */
        jQuery(document).tooltip({
            items: "*",
            content: function() {
                var element = jQuery(this);
                
                var toolTipped = element.closest(".f-has-tooltip");
                
                if ( toolTipped.length ) {
                    var tip = toolTipped.nextAll(".f-tooltip").first();
                    return tip.html();
                }
                else {
                    var tip = element.attr('title');
                    return tip;
                }
            }
        });
                
        /* initialize our keyboard table traversal (j/k keys) */
        jQuery(".compactResultsTable.f_traversable").traverse('tr', {
            table_row_helper: true
        });
        
        //ajaxPage
        jQuery(document).on('click', '.f_ajaxPage', function(e){
            e.stopPropagation();
            jQuery(this.getAttribute("data-selector")).load(this.getAttribute("href"));
            return false;
        });
    	
    	//html5 placeholder support for IE
		jQuery.placeholder();
        
        //buttons that redirect the page on click
        jQuery(document).on('click', 'button[data-href]', function(event){window.location = jQuery(this).attr("data-href");});
        
        // page blockers
        jQuery(document).on('click', 'a.f_blocker, button.f_blocker', Yukon.ui.block);
        jQuery(document).on('resize', '#modal_glass', Yukon.ui.blockPage);
        
        // clear page blocker
        jQuery(document).on('click', '.f_clearBlocker', Yukon.ui.unblockPage);
        
        // Disable a form element after clicked
        jQuery(document).on('click', '.f_disableAfterClick', function() {
        	var button = jQuery(this);
            if (button.is(":input")) {
                this.disabled = true;
                var group = button.attr('data-disable-group');
                if (group != '') {
                    jQuery("[data-disable-group='" + group + "']").each(function(idx){
                        this.disabled = true;
                    });
                }
              
                // if this is a busy button, add the spinner icon and use the busy text
                if (button.is("[data-busy]")) {
                    // if this button has an icon hide it
                    button.children(".icon").hide();
                    // only one of the spinner icons should be there
                    button.children(".icon-action-spinner").show();
                    button.children(".icon-loading").show();
                    
                    var label = button.children(".label");
                    var busyText = button.attr("data-busy");
                    if (label.length > 0 && busyText.length > 0) {
                        var originalText = label.html(); 
                        label.html(busyText);
                        button.attr("data-busy", originalText); // just incase we need to put it back
                    }
                }
                
                //if this is a submit button, trigger the submit event on the form
                if (button.is(":submit")) {
                	var form = jQuery(this.form);
                	
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
        jQuery(document).on('keydown', 'form.f_preventSubmitViaEnterKey', function(e){
        	//allow override submission elements
        	if(jQuery(e.target).hasClass("f_allowSubmitViaEnterKey")){
        		return true;
        	}
        	if(e.keyCode == 13){
        		return false;
        	}
        });
        
        // close popup on submit event
        jQuery(document).on('click', 'button.f_closePopupOnSubmit', function(event){
            jQuery(event).closest('.popUpDiv').dialog('close');
        });

        jQuery(document).on('click', '.f_setHomePage', function(event) {
            event.preventDefault ? event.preventDefault() : event.returnValue = false;  // IE8 requires returnValue
            event.stopPropagation();

            var userId = jQuery("#changeHomepage_userId").val();
            var send = document.location.href;
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

        
        // resize it with the window
        
        // resize blocked elements w/ page resize
        
        $$("input.f_formatPhone").each(function(elem){
            elem.observe('blur', function(event){
                Yukon.ui.formatPhone(event.element());
            });
            
            Yukon.ui.formatPhone(elem);
        });
        jQuery(document).on('blur', 'input.f_formatPhone', function(event){
            Yukon.ui.formatPhone(event.target);
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
        
        /*
         * Focus the designated input element
         */
        Yukon.ui._autofocus();
    },
    
    
    _AUTOFOCUS_TRIES: 0,
    _autofocus: function(){
        var focusElement = jQuery("[autofocus], .f_focus:first")[0];
        
        if(focusElement) {
            try{ //Play nice with IE
                focusElement.focus();
            }catch(err){
                //give the autofocus element 3 seconds to show itself
                if(Yukon.ui._AUTOFOCUS_TRIES < 30){
                    //certain browsers will error if the element to be focused is not yet visible
                    setTimeout('Yukon.ui._autofocus()', 100);
                }
            }
        }
    },
    
    /*
     * Sets the focus to the first input/textarea found on the page having a class of "error"
     */
    focusFirstError: function(){
        Yukon.ui._setFocusFirstError();
        Yukon.ui._autofocus();
    },
    
    /*
     * Applies the "f_focus" class to the first input/textarea element having a class of "error"
     */
    _setFocusFirstError: function(){
        var error_field = jQuery("input.error, textarea.error").first();
        if (error_field.length === 1) {
            jQuery(".f_focus").removeClass("f_focus");
            error_field.addClass("f_focus");
        }
    },
    
    block: function(event){
       var blockElement = jQuery(event.target).closest(".f_block_this")[0];
       if(blockElement){
           Yukon.uiUtils.elementGlass.show(blockElement);
       }else{
           Yukon.uiUtils.pageGlass.show();
       }
    },
    
    unblock: function(element){
        var blockElement = jQuery(event.target).closest(".f_block_this")[0];
        if(blockElement){
            Yukon.uiUtils.elementGlass.hide(blockElement);
        }else{
            Yukon.uiUtils.pageGlass.hide();
        }
    },

    blockPage: function(args) {
        Yukon.uiUtils.pageGlass.show();
    },

    unblockPage: function() {
        Yukon.uiUtils.pageGlass.hide();
    },
    
    flashSuccess: function(markup){
        jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage CONFIRM"});
    },
    
    flashError: function(markup) {
        jQuery(".main-container").addMessage({message: markup, messageClass: "userMessage ERROR"});
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
                        Yukon.ui.wizard.nextPage(event.element().up(".f_page"));
                    });
                });
                
                elem.select(".f_prev").each(function(prevButton){
                    prevButton.observe('click', function(event){
                        Yukon.ui.wizard.prevPage(event.element().up(".f_page"));
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
        	page = jQuery(page);
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
        	wizard = jQuery(wizard);
            if(wizard.hasClass("f_wizard")){
                jQuery(".f_page", wizard).hide();
                jQuery(".f_page:first", wizard).show();
            }else{
            	jQuery(".f_wizard .f_page").hide();
            	jQuery(".f_wizard .f_page:first").show();
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
            jQuery(element).find('.glass:first').fadeOut(200, function(){jQuery(this).remove();});
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
};

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

jQuery.fn.toggleDisabled = function() {
    return this.each(function() {
    	if (jQuery(this).is(":input")) {
    		this.disabled = !this.disabled;
    	}
    });
};

jQuery.fn.flashYellow = function (duration) {
	return this.each(function(){
		if(typeof(duration) != 'number'){
			duration = 0.8;
		}
		jQuery(this).flashColor({color: "#FF0", duration: duration*1000});
	});
};

// initialize the lib
jQuery(document).ready(function(){
    Yukon.ui.init();

    //turn off ajax caching application-wide by default
    jQuery.ajaxSetup({cache: false});
});
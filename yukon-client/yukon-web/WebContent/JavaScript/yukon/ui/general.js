/*
*   General purpose ui functionality for Yukon.
*   
*     REQUIRES:
*     * Prototype 1.7.1+
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
    
    autoWire: function() {
        // register listeners
        $$("a.f_blocker").each(function(elem) {
            elem.observe('click', function(event) {
                Yukon.ui.blockPage({color:'#000', alpha: 0.25});
                return true;
            });
        });
        
        $$("button.f_blocker").each(function(elem) {
            elem.observe('click', function(event) {
                Yukon.ui.blockPage({color:'#000', alpha: 0.25});
                return true;
            });
        });
        
        $$(".f_clearBlocker").each(function(elem){
            elem.observe('click', function(event){
                Yukon.ui.unblockPage();
                return true;
            });
        });
        
        // close popup on submit event
        $$("button.f_closePopupOnSubmit").each(function(elem){
            elem.observe('click', function(event){
            	elem.up('.popUpDiv').hide();
                return true;
            });
        });
        
        // resize it with the window
        Event.observe(window, 'resize', function(event) {
            //get visible blocked element's glass and redraw them as well
            $$("div.glass").each(function(elem){
                Yukon.uiUtils.elementGlass.redraw(elem);
            });
        });
        
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
        
        Yukon.uiUtils.tabs.init();
        
        /*
         * Focus the designated input element
         */
        var focusElement = $$(".f_focus:first")[0];
        if(focusElement) {
            focusElement.focus();
        }
    },

    blockPage: function(args) {
        Yukon.uiUtils.pageGlass.show(args);
    },

    unblockPage: function() {
        Yukon.uiUtils.pageGlass.hide();
    },
    
    blockElement: function(args) {
        $$(args.selector).each(function(elem){
            args.element = elem;
            Yukon.uiUtils.elementGlass.show(args);            
        });
    },
    
    unblockElement: function(args) {
        $$(args.selector).each(function(elem){
            args.element = elem;
            Yukon.uiUtils.elementGlass.hide(args);            
        });
    },
    
    formatPhone: function(input){
        //strip the input down to just numbers, then format
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

    toggleInputs: function(input){
        //find matching inputs
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
         * Resets the page of the wizard to the first/initial page.  Does NOT do anything with the contents
         * 
         * wizard: can be any element in the DOM.  
         *              * If it is the f_wizard container itself, it will reset the page
         *              * If it is an arbitrary node, it will search for and reset ALL f_wizard containers within
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
        show: function(args) {
            if(args.element != null) {
                
                args.element.select('button', 'input[type=button]', 'input[type=submit]').each(function(item) {
                    item.disable();
                });
                
                var glass = args.element.next(".glass");
                if(glass == null) { //create the glass
                    glass = new Element('div', {'class': 'glass'});
                    //insert the element
                    args.element.insert({after:glass});
                }
                
                if(args.color != null) {
                    glass.setStyle("background-color:"+args.color);
                }
                
                if(args.alpha != null) {
                    glass.setOpacity(args.alpha);
                }
                
                this.redraw(glass);
                return glass;
            }
            return null;
        },
        
        hide: function(args) {
            if(args.element != null) {
                
                args.element.select('button', 'input[type=button]', 'input[type=submit]').each(function(item) {
                    item.enable();
                });
                
                var glass = args.element.next(".glass");
                if(glass) {
                    glass.remove();
                }
            }
        },
        
        redraw: function(elem) {
            
            //resize the glass
            elem.clonePosition(elem.previous());
            
            //show the glass
            elem.show();
        }
    }, 
        
    pageGlass : {
        show : function(args) {
            if (args == null) {
                args = {color:'#000', alpha: 0.25};
            }
            var glass = $("modal_glass");
            var tint = $$("#modal_glass > .tint")[0];
            if (glass == null) {
                glass = new Element('div', {
                    id : "modal_glass",
                    style : 'display:none;);' //hide it so we can fade it in
                });
                tint = new Element('div', {'class':'tint'});
                glass.insert(new Element('div', {'class':'loading'}));
                glass.insert(tint);
                document.body.insertBefore(glass, document.body.childNodes[0]);
            }
            
            if(args.color != null) {
                tint.setStyle("background-color:"+args.color);
            }
            
            if(args.alpha != null) {
                tint.setOpacity(args.alpha);
            }
            
            // size it appropriately
            tint.setStyle({
                height : Yukon.uiUtils.viewport.height() + 'px'
            });
            tint.setStyle({
                width : Yukon.uiUtils.viewport.width() + 'px'
            });

            //sugary gooey-ness
            Effect.Appear('modal_glass', {duration: 0.2});
        },

        hide : function() {
            Effect.Fade('modal_glass', {duration: 0.2});
            window.onresize = null;
        }
    },
    
    tabs: {
        init: function(elem){
            if(elem === undefined){
                $$(".f_tabs > li").each(function(elem){
                    elem.observe('click', function(event){
                        Yukon.uiUtils.tabs.showTab(event.element());
                    });
                });
                
                $$(".f_tabs").each(function(elem){
                    elem.select("li:first").each(function(li){
                        Yukon.uiUtils.tabs.showTab(li);
                    });
                });                
            } else {
                elem.select(".f_tabs > li").each(function(elem){
                    elem.observe('click', function(event){
                        Yukon.uiUtils.tabs.showTab(event.element());
                    });
                });
                
                elem.select(".f_tabs").each(function(elem){
                    elem.select("li:first").each(function(li){
                        Yukon.uiUtils.tabs.showTab(li);
                    });
                });
            }
        },
        showTab: function(tab){
            if(!tab.hasClassName("active")){
                var tabIndex = 0;
                var tabbed = null;
                var tabControls = null;
                
                tab.parentNode.childElements().each(function(elem, idx){ //no native support for indexing an element in prototype!
                    if (elem === tab) {
                        tabIndex = idx;
                    }
                });
                
                
                if(tab.hasClassName("f_tab")){
                    tabbed = tab.parentNode;
                    // IE doesn't extend the parentNode for some reason in Prototype 1.7
                    tabControls = $(tab.parentNode.parentNode).select(".f_tabs")[0];
                }else{
                    tabControls = tab.parentNode;
                 // IE doesn't extend the parentNode for some reason in Prototype 1.7
                    tabbed = $(tab.parentNode.parentNode).select(".f_tabbed")[0];
                }
                
                tabbed.childElements().each(function(elem){
                    elem.removeClassName("active");
                });
                
                tabControls.childElements().each(function(elem){
                    elem.removeClassName("active");
                });
                
                tabbed.select(".f_tab")[tabIndex].addClassName("active");
                tabControls.select("li")[tabIndex].addClassName("active");
                return true;
            }
            return false;
        }
    },

    viewport : {
        height : function() {
            return (window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight);
        },
        width : function() {
            return (document.body.offsetWidth || window.innerWidth || document.documentElement.clientWidth || 0);
        }
    }
};

//Really Prototype doesn't have this!
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

//initialize the lib
document.observe("dom:loaded", function() {
    Yukon.ui.init();
});


Event.observe(window, 'load', function() {
    $$('.focusableFieldHolder input').each(function(inputField) {
        var blurAndInitial = function() {
            var defaultField = inputField.up('span').next('input');
            if ($F(inputField) == $F(defaultField) || $F(inputField) == "") {
                inputField.removeClassName('usingNonDefaultValue');
                inputField.value = $F(defaultField);
            } else {
                inputField.addClassName('usingNonDefaultValue');
            }
            $('descriptionPopup').hide();
        };
        Event.observe(inputField, "focus", function(event) {
            showPointingPopup(event);
            var defaultField = inputField.up('span').next('input');
            if ($F(inputField) == $F(defaultField)) {
                inputField.value = "";
            }
            inputField.removeClassName('usingNonDefaultValue');
        });
        Event.observe(inputField, "blur", blurAndInitial);
        blurAndInitial();
    });

    // handling select elements is similar, but requires slightly different code 
    $$('.focusableFieldHolder select').each(function(inputField) {
        var blurAndInitial = function() {
            var defaultField = inputField.up('span').next('input');
            if ($F(inputField) == $F(defaultField)) {
                inputField.removeClassName('usingNonDefaultValue');
            } else {
                inputField.addClassName('usingNonDefaultValue');
            }
            $('descriptionPopup').hide();
        };
        Event.observe(inputField, "change", blurAndInitial);
        Event.observe(inputField, "focus", function(event) {
            showPointingPopup(event);
            inputField.removeClassName('usingNonDefaultValue');
        });
        Event.observe(inputField, "blur", function(event) {
            blurAndInitial();
        });
        blurAndInitial();
    });

});
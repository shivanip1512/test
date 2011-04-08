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
        }
    },
    
    autoWire: function() {
        // register listeners
        $$(".f_blocker").each(function(elem) {
            elem.observe('click', function(event) {
                Yukon.ui.blockPage({color:'#000', alpha: 0.25});
                return true;
            });
        });
        
        $$(".f_clearBlocker").each(function(elem){
            elem.observe('click', function(event){
                Yukon.ui.unblockPage();
                return true;
            })
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
        args.element = $$(args.selector).each(function(elem){
            args.elem = elem;
            Yukon.uiUtils.elementGlass.show(args);            
        });
    },
    
    unblockElement: function(args) {
        args.element = $$(args.selector).each(function(elem){
            args.elem = elem;
            Yukon.uiUtils.elementGlass.hide(args);            
        });
    },
    
    formatPhone: function(input){
        //strip the input down to just numbers, then format
        var stripped = input.value.replace(/[^\d]/g, "");
        for(var i=0; i<YG.PHONE.FORMATS.length; i++){
            var regex = YG.PHONE.FORMATS[i].regex;
            var format = YG.PHONE.FORMATS[i].format;
            if(regex.test(stripped)){
                input.value = stripped.replace(regex, format);
            }
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
    }    
};


Yukon.uiUtils = {
    elementGlass: {
        show: function(args) {
            if(args.element != null) {
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
                var glass = args.element.next(".glass");
                if(glass) {
                    glass.remove();
                }
            }
        },
        
        redraw: function(elem) {
            
            //resize the glass
            elem.clonePosition(elem.previous(), {offsetTop: 5}); /*need to figure out the 5px offset issue in all browsers*/
            
            //show the glass
            elem.show();
        }
    }, 
        
    pageGlass : {
        show : function(args) {
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

    viewport : {
        height : function() {
            return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
        },
        width : function() {
            return document.body.offsetWidth || window.innerWidth || document.documentElement.clientWidth || 0;
        }
    }
};

//initialize the lib
document.observe("dom:loaded", function() {
    Yukon.ui.init();
});
/*
*   General purpose ui functionality for Yukon.
*   
*     REQUIRES:
*     * Prototype 1.7.1+
*     
*     USAGE:
*     In day to day use, the Cooper.ui class will be your best bet at getting the functionality you
*     need on a page.  Cooper.uiUtils are more low level functions for use by other libraries.
*     
*/

if(typeof(Cooper) == 'undefined') {
    Cooper = {};
}

Cooper.ui = {
    _initialized : false,

    init : function(modules) {
        if (!this._initialized) {

            // register listeners
            $$(".blocker").each(function(elem) {
                elem.observe('click', function(event) {
                    Cooper.ui.blockPage({color:'#000', alpha: 0.05});
                    return true;
                });
            });
            
            $$(".clearBlocker").each(function(elem){
                elem.observe('click', function(event){
                    Cooper.ui.unblockPage();
                    return true;
                })
            });

            this._initialized = true;
        }
    },

    blockPage : function(args) {
        Cooper.uiUtils.pageGlass.show(args);
    },

    unblockPage : function() {
        Cooper.uiUtils.pageGlass.hide();
    }
};


Cooper.uiUtils = {
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
            tint.setStyle("background-color:"+args.color);
            tint.setOpacity(args.alpha);
            
            
            // size it appropriately
            tint.setStyle({
                height : Cooper.uiUtils.viewport.height() + 'px'
            });
            tint.setStyle({
                width : Cooper.uiUtils.viewport.width() + 'px'
            });

            //sugary gooey-ness
            Effect.Appear('modal_glass', {duration: 0.2});

            // resize it with the window
            window.onresize = function(event) {
                Cooper.uiUtils.pageGlass.show();
            };
        },

        hide : function() {
            debug('unload!!');
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
    Cooper.ui.init();
});
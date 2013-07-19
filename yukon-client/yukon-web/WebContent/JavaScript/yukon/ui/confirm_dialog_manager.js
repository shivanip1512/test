/*  confirm_dialog_manager.js
 * 
 * requirements:
 *      jQueryUI 1.8.16+
 *      
 * abstract:
 *      Manages the display and event handling of the confirmation dialog box in standard.jsp
 *      
 * usage:
 *      Simply including this file will initialize the singleton and register event listeners for
 *      the appropriate dialog events.  'Private' methods begin with '_' and should generally not
 *      be called.  Public methods are limited to 
 *          * getting the current (if any) confirmation object
 *          * registering a new confirmation object
 *          * closing the dialog
 */

Yukon = (function (mod) {
    return mod;
})(Yukon || {});

Yukon.Dialog = (function (mod) {
    return mod;
})(Yukon.Dialog || {});

Yukon.Dialog.ConfirmationManager = (function() {
    var dialogConfirmationManagerMod = {
        _initialized: false,
        _dialogs: [],
        _current_dialog: null,
        _INDEX_OF_ACTION_BUTTON: 1, // Action button is the right of 2 buttons = [1]
        
        /*---------------------*/
        /* 'PUBLIC' functions */
        /*---------------------*/
        
        /*
         * Add a confirmation dialog, does not show the dialog
         * 
         * args = object {}
         *      on:string               - jQuery selector to activate this dialog
         *      eventType:string        - the event type that will trigger this dialog to open
         *      strings:object          - contains all of the i18n'd strings for the dialog
         *      strings.title:string    - title message for the dialog
         *      strings.message:string  - message that is displayed
         *      strings.ok:string       - ok button string
         *      strings.cancel:string   - cancel button string
         */
        add: function(args) {
            //initialize the dialog if needed
            Yukon.Dialog.ConfirmationManager._init();
            
            //the default values are NOT i18n'd.  The intent is for the developer to resolve
            //these strings [title, message, ok, cancel] when calling this function
            var defaults = {on: null,
                            'eventType': 'click',
                            'strings': {
                                'title': "UNDEFINED TITLE",
                                'message': "UNDEFINED MESSAGE",
                                'ok': 'UNDEFINED OK TEXT',
                                'cancel': 'UNDEFINED CANCEL TEXT'}
                            };
            
            jQuery.extend(defaults, args);
                
            if (defaults.on) {
                //store the data on the element
                var element = jQuery(defaults.on);
                element.data("args", defaults);
                
                //remove the href redirect for buttons as applied in general.js for buttons with 
                //a data-href attribute since the binding is likely to be higher in the call
                //stack than the binding for this function.  Simply removing the data-href attribute
                //solves the problem as general.js delegates this attribute on the document.
                jQuery(defaults.on).each(function(idx, elem){
                    elem = jQuery(elem);
                    if(elem.attr("data-href") != ""){
                        elem.data("href", elem.attr("data-href"));
                        elem.removeAttr("data-href");
                    }
                    if(elem.attr("onclick") != ""){
                        elem.data("data-onclick", elem.attr("onclick"));
                        elem.removeAttr("onclick");
                    }
                });
                
                //register the event handler
                jQuery(document).on(defaults.eventType, defaults.on, this._show_window);
                return true;
            }
            return false;
        },

        /*
         * Close the current dialog and perform the cancel action.  By default this 
         * will trigger a 'yukonDialogConfirmCancel' event on the #yukon_dialog_confirm element
         */
        cancel: function(){
            return this._default._cancel_action; 
        },

        /*
         * Return the current dialog if exists, null otherwise
         */
        current_dialog: function(){
           return this._current_dialog; 
        },
            
        /*
         * Close the current dialog and perform the ok action.  By default this 
         * will trigger a 'yukonDialogConfirmOk' event on the #yukon_dialog_confirm element
         */
        ok: function(){
            return this._default._ok_action;
        },

        /*---------------------*/
        /* 'PRIVATE' functions */
        /*---------------------*/
        _init: function(){
            if(!this._initialized){
                jQuery('html').append('<div id="yukon_dialog_confirm" style="display:none;"><span class="message"></span></div>');
            }
            this._initialized = true;
        },

        _show_window: function(event){
            event.preventDefault();
            event.stopPropagation();
            
            var _self = Yukon.Dialog.ConfirmationManager;
            //cache the jQuery object
            var element = jQuery(this);
            
            //fetch the data from element
            var args = element.data("args");
            
            //determine the 'ok' and 'cancel' events
            var buttons = [];
            buttons.push({text: args.strings.cancel, click: _self._default._cancel_action});
            buttons.push({text: args.strings.ok, click: _self._default._ok_action, class:'primary action'});

            //is the intent to redirect on ok?
            var actionButton = buttons[Yukon.Dialog.ConfirmationManager._INDEX_OF_ACTION_BUTTON];
            if (element.attr("href")) {
                actionButton.click = function(){
                    _self._close_dialog();
                    window.location = element.attr("href");
                };
            } else if(element.attr("data-href")) {
                actionButton.click = function(){
                    _self._close_dialog();
                    window.location = element.attr("data-href");
                };
            } else if(element.data("data-onclick")) {
                actionButton.click = function(){
                    _self._close_dialog();
                    var scripts =  element.data("data-onclick");
                    eval(scripts);
                };
            } else if(element.data("href")) {
                actionButton.click = function(){
                    _self._close_dialog();
                    window.location = element.data("href");};
            }
            //is the intent to submit a form on ok?
            else if (element.attr("type") != undefined && element.attr("type").toLowerCase() == "submit") {
                actionButton.click = function() {
                	var form = element.closest("form")[0];
                	if(!(typeof(element.attr("value")) == "undefined") 
                		&& !(typeof(element.attr("name")) == "undefined")){
                		jQuery(form).prepend('<input type="hidden" name="'+ element.attr("name") +'" value="'+ element.attr("value") +'">');
                	}
                	form.submit();
                };
            }
            //is the intent to submit a specific form on ok?
            else if(element.attr("data-form")){
                actionButton.click = function(){jQuery(element.attr("data-form"))[0].submit();};
            }

            //dialog default options
            var defaults = {
                'position': 'center',
                'width': '500px',
                'height': 'auto',
                'buttons': buttons,
                'title': args.strings.title,
                'modal': true 
            };

            //inject the message into the dialog
            jQuery("#yukon_dialog_confirm .message").html(args.strings.message);
            //show the dialog
            _self._current_dialog = jQuery('#yukon_dialog_confirm').dialog(jQuery.extend(defaults, args));
            
            // kindof a hack to get buttons disabled when the action is slow
            if (args.disable_group) {
                jQuery('#yukon_dialog_confirm').closest('.ui-dialog').find('.ui-dialog-buttonset button').each(function(idx, button) {
                    var b = jQuery(button);
                    b.addClass('f-disableAfterClick');
                    b.attr('data-disable-group', args.disable_group);
                    
                });
            }
        },

        _close_dialog: function() {
            var _self = Yukon.Dialog.ConfirmationManager;
            if(_self._current_dialog){
                _self._current_dialog.dialog('destroy');
                _self._current_dialog = null;
            }
        },

        _default: {
            _ok_action: function(event){
                var _self = Yukon.Dialog.ConfirmationManager;
                if(_self._current_dialog){
                    _self._current_dialog.trigger("yukonDialogConfirmOk");
                }
                _self._close_dialog();
            },
            
            _cancel_action: function(event){
                var _self = Yukon.Dialog.ConfirmationManager;
                if(_self._current_dialog){
                    _self._current_dialog.trigger("yukonDialogConfirmCancel");
                }
                _self._close_dialog();
            }
        }
    };
    return dialogConfirmationManagerMod;
})();
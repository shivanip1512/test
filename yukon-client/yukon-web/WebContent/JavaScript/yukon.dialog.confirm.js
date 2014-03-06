/** 
 * Singleton to manage confirming an action with a special dialog
 * 
 * @requires jQueryUI 1.8.16+
 *      
 * abstract:
 *      Manages the display and event handling of the confirmation dialog box
 *      
 * usage:
 *      Simply including this file will initialize the singleton and register event listeners for
 *      the appropriate dialog events.  'Private' methods begin with '_' and should generally not
 *      be called.  Public methods are limited to 
 *          * getting the current (if any) confirmation object
 *          * registering a new confirmation object
 *          * closing the dialog
 */

yukon.namespace('yukon.modules.dialogConfirm');

yukon.modules.dialogConfirm = function (mod) {
    var _initialized = false,
        _current_dialog = null,
        _INDEX_OF_ACTION_BUTTON = 1, // Action button is the right of 2 buttons = [1]

        /*---------------------*/
        /* 'PRIVATE' functions */
        /*---------------------*/
        _init = function () {
            if (!_initialized) {
                jQuery('html').append('<div id="yukon_dialog_confirm" style="display:none;"><span class="confirm-message"></span></div>');
                _initialized = true;
            }
        },

        _show_window = function (event) {
            //cache the jQuery object
            var element = jQuery(this),
                //fetch the data from element
                args = element.data("args"),
                buttons = [],
                actionButton,
                //dialog default options
                defaults = {
                    'position': 'center',
                    'width': '500px',
                    'height': 'auto',
                    'buttons': buttons,
                    'title': args.strings.title,
                    'modal': true 
                };
            event.preventDefault();
            event.stopPropagation();
            //determine the 'ok' and 'cancel' events
            buttons.push({text: args.strings.cancel, click: _default._cancel_action});
            buttons.push({text: args.strings.ok, click: _default._ok_action, 'class':'primary action'});
            actionButton = buttons[_INDEX_OF_ACTION_BUTTON];

            //is the intent to redirect on ok?
            if (element.attr("href")) {
                actionButton.click = function () {
                    _close_dialog();
                    window.location = element.attr("href");
                };
            } else if (element.attr("data-href")) {
                actionButton.click = function () {
                    _close_dialog();
                    window.location = element.attr("data-href");
                };
            } else if (element.data("data-onclick")) {
                actionButton.click = function () {
                    _close_dialog();
                    var scripts =  element.data("data-onclick");
                    eval(scripts);
                };
            } else if (element.data("href")) {
                actionButton.click = function () {
                    _close_dialog();
                    window.location = element.data("href");
                };
            }
            //is the intent to submit a form on ok?
            else if (element.attr("type") != undefined && element.attr("type").toLowerCase() == "submit") {
                actionButton.click = function () {
                    var form = element.closest("form")[0],
                        value = element.val(),
                        name = element.attr('name');
                    if (!(typeof (value) == "undefined") && !(typeof (name) == "undefined")) {
                        jQuery(form).prepend('<input type="hidden" name="' + name + '" value="' + value + '">');
                    }
                    form.submit();
                };
            }
            //is the intent to submit a specific form on ok?
            else if (element.attr("data-form")) {
                actionButton.click = function () {
                    jQuery(element.attr("data-form"))[0].submit();
                };
            }

            //inject the message into the dialog
            jQuery("#yukon_dialog_confirm .confirm-message").html(args.strings.message);
            //show the dialog
            _current_dialog = jQuery('#yukon_dialog_confirm').dialog(jQuery.extend(defaults, args));
            
            // kindof a hack to get buttons disabled when the action is slow
            if (args.disable_group) {
                jQuery('#yukon_dialog_confirm').closest('.ui-dialog').find('.ui-dialog-buttonset button').each(function (idx, button) {
                    var b = jQuery(button);
                    b.addClass('f-disable-after-click');
                    b.attr('data-disable-group', args.disable_group);
                    
                });
            }
        },

        _close_dialog = function() {
            if (_current_dialog) {
                _current_dialog.dialog('destroy');
                _current_dialog = null;
            }
        },

        _default = {
            _ok_action: function(event) {
                if (_current_dialog) {
                    _current_dialog.trigger("yukonDialogConfirmOk");
                }
                _close_dialog();
            },
            
            _cancel_action: function(event) {
                if (_current_dialog) {
                    _current_dialog.trigger("yukonDialogConfirmCancel");
                }
                _close_dialog();
            }
        };
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
    mod.add = function (args) {
        //initialize the dialog if needed
        _init();

        //the default values are NOT i18n'd.  The intent is for the developer to resolve
        //these strings [title, message, ok, cancel] when calling this function
        var defaults = {
                on: null,
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

            //remove the href redirect for buttons as applied in yukon.js for buttons with 
            //a data-href attribute since the binding is likely to be higher in the call
            //stack than the binding for this function.  Simply removing the data-href attribute
            //solves the problem as yukon.js delegates this attribute on the document.
            jQuery(defaults.on).each(function (idx, elem) {
                elem = jQuery(elem);
                if (elem.attr("data-href") != "") {
                    elem.data("href", elem.attr("data-href"));
                    elem.removeAttr("data-href");
                }
                if (elem.attr("onclick") != "") {
                    elem.data("data-onclick", elem.attr("onclick"));
                    elem.removeAttr("onclick");
                }
            });

            //register the event handler
            jQuery(document).on(defaults.eventType, defaults.on, _show_window);
            return true;
        }
        return false;
    };

    /*
     * Close the current dialog and perform the cancel action.  By default this 
     * will trigger a 'yukonDialogConfirmCancel' event on the #yukon_dialog_confirm element
     */
    mod.cancel = function () {
        return _default._cancel_action;
    };

    /*
     * Return the current dialog if exists, null otherwise
     */
    mod.current_dialog = function () {
       return _current_dialog; 
    };
        
    /*
     * Close the current dialog and perform the ok action.  By default this 
     * will trigger a 'yukonDialogConfirmOk' event on the #yukon_dialog_confirm element
     */
    mod.ok = function () {
        return _default._ok_action;
    };
};

( function () {
    try {
        Sandbox ('dialogConfirm', function (mod) {
            yukon.dialogConfirm = mod;
        });
    } catch(dialogex) {
    }
})();

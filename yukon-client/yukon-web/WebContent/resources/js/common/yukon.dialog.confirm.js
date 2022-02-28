yukon.namespace('yukon.dialogConfirm');

/** 
 * Module to manage confirming an action with a special dialog
 * @module yukon.dialogConfirm
 * @requires JQUERY 
 * @requires JQUERY UI
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
yukon.dialogConfirm = (function () {
    
    var _initialized = false,
        
        /** @type {Object} - reference of current dialog */
        _current_dialog = null,
        
        /** @type {string} -  used for triggering ok event*/
        _ok_event = 'yukon.dialog.confirm.ok',
        
        /** @type {string} - used for triggering cancel event */
        _cancel_event = 'yukon.dialog.confirm.cancel',
        
        _init = function () {
            if (!_initialized) {
                $('html').append('<div id="yukon_dialog_confirm" style="display:none;"><span class="confirm-message"></span></div>');
                _initialized = true;
            }
        },
        
        /** 
         * Display the dialog and registering the event handler.
         * @param {Object} event - jquery event object.
         */
        _show_window = function (event) {
            //cache the jQuery object
            var element = $(this),
                //fetch the data from element
                args = element.data("args"),
                buttons = [],
                actionButton,
                //dialog default options
                defaults = {
                    'width': '500px',
                    'height': 'auto',
                    'buttons': buttons,
                    'title': args.strings.title,
                    'modal': true 
                };
            
            event.preventDefault();
            event.stopPropagation();
            
            buttons.push({text: args.strings.cancel, click: _default._cancel_action});
            
            if (args.strings.ok === yg.text.deleteButton) {
                args.strings.okClasses += ' delete';
            }
            
            actionButton = {text: args.strings.ok, click: _default._ok_action, 'class': 'primary action ' + args.strings.okClasses};
            buttons.push(actionButton);
            
            // Determine the 'ok' click action.
            if (element.data('okEvent')) {
                // fire an event
                actionButton.click = function () {
                    element.trigger(element.data('okEvent'));
                    _close_dialog();
                };
            } else if (element.attr("href")) {
                // go to a url
                actionButton.click = function () {
                    _close_dialog();
                    window.location = element.attr("href");
                };
            } else if (element.attr("data-href") || element.data("href")) {
                // go to a url
                actionButton.click = function () {
                    _close_dialog();
                    window.location = element.data("href");
                };
            } else if (element.data("onclick")) {
                // fire onclick function
                actionButton.click = function () {
                    _close_dialog();
                    var scripts =  element.data("onclick");
                    eval(scripts);
                };
            } else if (typeof element.attr("type") !== 'undefined' && element.attr("type").toLowerCase() == "submit") {
                // submit a form
                actionButton.click = function () {
                    var form = element.closest("form")[0],
                        value = element.val(),
                        name = element.attr('name');
                    if (!(typeof (value) == "undefined") && !(typeof (name) == "undefined")) {
                        $(form).prepend('<input type="hidden" name="' + name + '" value="' + value + '">');
                    }
                    form.submit();
                };
            } else if (element.attr("data-form")) {
                // submit a specific form
                actionButton.click = function () {
                    $(element.attr("data-form"))[0].submit();
                };
            }
            
            // Inject the message into the dialog
            $("#yukon_dialog_confirm .confirm-message").html(args.strings.message);
            // Show the dialog
            _current_dialog = $('#yukon_dialog_confirm').dialog($.extend(defaults, args));
            
            // Kindof a hack to get buttons disabled when the action is slow
            if (args.disable_group) {
                $('#yukon_dialog_confirm').closest('.ui-dialog').find('.ui-dialog-buttonset button').each(function (idx, button) {
                    var b = $(button);
                    b.addClass('js-disable-after-click');
                    b.attr('data-disable-group', args.disable_group);
                });
            }
            if (args.strings.userMessage) {
                _current_dialog.addMessage({message: args.strings.userMessage, messageClass: args.strings.userMessageClass});
            }
        },
        
        /** Close the dialog. */
        _close_dialog = function() {
            if (_current_dialog) {
                _current_dialog.dialog('destroy');
                _current_dialog = null;
            }
        },

        _default = {
            _ok_action: function(event) {
                if (_current_dialog) {
                    _current_dialog.trigger(_ok_event);
                }
                _close_dialog();
            },
            
            _cancel_action: function(event) {
                if (_current_dialog) {
                    _current_dialog.trigger(_cancel_event);
                }
                _close_dialog();
            }
        },
        mod = {};
        
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
    mod = {
        add : function (args) {
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
                    'cancel': 'UNDEFINED CANCEL TEXT',
                    'userMessageClass' : 'info'}
            };

        $.extend(defaults, args);

        if (defaults.on) {
            //store the data on the element
            var element = $(defaults.on);
            element.data('args', defaults);
            
            //remove the href redirect for buttons as applied in yukon.js for buttons with 
            //a data-href attribute since the binding is likely to be higher in the call
            //stack than the binding for this function.  Simply removing the data-href attribute
            //solves the problem as yukon.js binds that event to the document.
            if (element.is('[data-href]')) {
                element.data('href', element.attr('data-href'));
                element.removeAttr('data-href');
            }
            if (element.is('[onclick]')) {
                element.data('onclick', element.attr('onclick'));
                element.removeAttr('onclick');
            }

            //register the event handler
            $(document).on(defaults.eventType, defaults.on, _show_window);
            return true;
        }
        return false;
        },

        /**
         * Close the current dialog and perform the cancel action.  By default this 
         * will trigger a 'yukon.dialog.confirm.cancel' event on the #yukon_dialog_confirm element
         */
        cancel : function () {
        return _default._cancel_action;
        },

        /** Return the current dialog if exists, null otherwise. */
        current_dialog : function () {
       return _current_dialog; 
        },
        
        /**
         * Close the current dialog and perform the ok action.  By default this 
         * will trigger a 'yukon.dialog.confirm.ok' event on the #yukon_dialog_confirm element
         */
        ok : function () {
        return _default._ok_action;
        }
    };
    return mod;
})();

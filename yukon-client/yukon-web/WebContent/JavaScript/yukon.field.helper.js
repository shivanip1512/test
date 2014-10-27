yukon.namespace('yukon.ui.FieldHelper');

/**
 * Module for UI functionality editing focusableFieldHolders
 * @module   yukon.ui.FieldHelper
 * @requires JQUERY
 * @requires JQUERY UI
 */
yukon.ui.FieldHelper = function () {

    var _initialized = false,
    
        /** @type {number} - Id returned by set timeout */
        _timeout = null,
        
        /** @type {object} - jquery timeout event object */
        _timeoutArgs = null,
        
        mod = {};

    //Initializer
    mod = {
        init : function () {

            if (!_initialized) {
                //setup select elements
                $('.focusableFieldHolder select').on('change', this.focusSelect);
                $('.focusableFieldHolder select, .focusableFieldHolder a').on('focus active', this.focusSelect);
                $('.focusableFieldHolder select, .focusableFieldHolder a').on('blur mouseleave', this.blurSelect);
                $('.focusableFieldHolder select, .focusableFieldHolder a, .focusableFieldHolder input').on('mouseenter', this.showTooltip);

                //setup input elements
                $('.focusableFieldHolder input').on('blur change mouseleave', this.blurInput);
                $('.focusableFieldHolder input').on('focus', this.focusInput);

                //trigger a blur event on each element -> performs an initial render
                $('.focusableFieldHolder select, .focusableFieldHolder input').trigger('blur');
                _initialized = true;
            }
        },

        /** Handles the blur event on input fields.
          * @param {Object} event - jquery event object
        */
        blurInput : function (event) {
            var inputField,
                defaultField;
            clearTimeout(_timeout);
            inputField = $(event.currentTarget);
            defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
            $('#descriptionPopup').hide();
            if (!defaultField.length) {
                return;
            }
            if (inputField.val() == defaultField.val() || inputField.val() == "") {
                inputField.removeClass('usingNonDefaultValue');
                inputField.val(defaultField.val());
            } else {
                inputField.addClass('usingNonDefaultValue');
            }
        },

        /** Handles the blur event on select fields.
          * @param {Object} event - jquery event object
        */
        blurSelect : function (event) {
            var inputField,
                defaultField;
            clearTimeout(_timeout);
            inputField = $(event.currentTarget);
            defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
            $('#descriptionPopup').hide();
            if (!defaultField.length) {
                return;
            }
            if (inputField.val() === defaultField.val()) {
                inputField.removeClass('usingNonDefaultValue');
            } else {
                inputField.addClass('usingNonDefaultValue');
            }
        },

        /** Handles the focus event on input fields.
          * @param {Object} event - jquery event object
        */
        focusInput : function (event) {
            var inputField,
                defaultField;
            mod.showPointingPopup(event);
            inputField = $(event.currentTarget);
            defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
            inputField.removeClass('usingNonDefaultValue');
            if (!defaultField.length) {
                return;
            }
        },

        /** Displays tool tip
          * @param {Object} event - jquery event object
        */
        showTooltip : function (event) {
            _timeoutArgs = event;
            clearTimeout(_timeout);
            _timeout = setTimeout( function () {
                mod.showPointingPopup(_timeoutArgs);
            }, 400);
        },

        /** Handles the focus event on select fields. It shows a popup and removes the class name
          * @param {Object} event - jquery event object
        */
        focusSelect : function (event) {
            mod.showPointingPopup(event);
            $(event.currentTarget).removeClass('usingNonDefaultValue');
        },
        
        /** Displays a description pop up
          * @param {Object} event - jquery event object
        */
        showPointingPopup : function (event) {
            var popup = $("#descriptionPopup"),
                popupString,
                target,
                popupLeft,
                left,
                top,
                fieldDesc;
    
            if (!popup.length) {
                popupString = [];
                popupString.push('<div class="pointing-popup-container" id="descriptionPopup" style="display:none;">');
                    popupString.push('<div class="pointing-popup-chevron ov pr">');
                    popupString.push('</div>');
                    popupString.push('<div class="pointing-popup-content" id="descriptionPopup_content">');
                    popupString.push('</div>');
                popupString.push('</div>');
                $('body').append(popupString.join(''));
            }
            target = $(event.target);
            popupLeft = target.offset().left + target.width() + 12;
            left = popupLeft + 'px';
            top = (target.offset().top -2) + 'px';
    
            $('#descriptionPopup').css({left:left, top:top});
    
            fieldDesc = target.closest('.focusableFieldHolder').nextAll('span.focused-field-description');
            $('#descriptionPopup_content').html(fieldDesc.html());
            $('#descriptionPopup').show();
        }
    };
    return mod;
};

$(function () {
    var fieldHelper = new yukon.ui.FieldHelper();
    fieldHelper.init();
});
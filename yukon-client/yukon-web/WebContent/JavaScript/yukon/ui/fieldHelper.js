/*
 * Requirements: 
 *      - jQuery 1.6+
 *      - /JavaScript/yukon/ui/general.js [for the trigger()]
 * 
 * Singleton for UI functionality editing focusableFieldHolders
 */

Yukon.modules.FieldHelper = function (mod) {
    var _initialized = false,
        _timeout = null,
        _timeoutArgs = null;

    //Initializer
    mod.init = function () {
        if (!_initialized) {
            //setup select elements
            jQuery('.focusableFieldHolder select').bind('change', this.focusSelect);
            jQuery('.focusableFieldHolder select, .focusableFieldHolder a').bind('focus active', this.focusSelect);
            jQuery('.focusableFieldHolder select, .focusableFieldHolder a').bind('blur mouseleave', this.blurSelect);
            jQuery('.focusableFieldHolder select, .focusableFieldHolder a, .focusableFieldHolder input').bind('mouseenter', this.showTooltip);

            //setup input elements
            jQuery('.focusableFieldHolder input').bind('blur change mouseleave', this.blurInput);
            jQuery('.focusableFieldHolder input').bind('focus', this.focusInput);

            //trigger a blur event on each element -> performs an initial render
            jQuery('.focusableFieldHolder select, .focusableFieldHolder input').trigger('blur');
            _initialized = true;
        } else {
            return;
        }
    };

    mod.blurInput = function (event) {
        var inputField,
            defaultField;
        clearTimeout(_timeout);
        inputField = jQuery(event.currentTarget);
        defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
        jQuery('#descriptionPopup').hide();
        if (!defaultField.length) {
            return;
        }
        if (inputField.val() == defaultField.val() || inputField.val() == "") {
            inputField.removeClass('usingNonDefaultValue');
            inputField.val(defaultField.val());
        } else {
            inputField.addClass('usingNonDefaultValue');
        }
    };

    mod.blurSelect = function (event) {
        var inputField,
            defaultField;
        clearTimeout(_timeout);
        inputField = jQuery(event.currentTarget);
        defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
        jQuery('#descriptionPopup').hide();
        if (!defaultField.length) {
            return;
        }
        if (inputField.val() === defaultField.val()) {
            inputField.removeClass('usingNonDefaultValue');
        } else {
            inputField.addClass('usingNonDefaultValue');
        }
    };

    mod.focusInput = function (event) {
        var inputField,
            defaultField;
        mod.showPointingPopup(event);
        inputField = jQuery(event.currentTarget);
        defaultField = inputField.closest('span.focusableFieldHolder').next('input[type=hidden]');
        inputField.removeClass('usingNonDefaultValue');
        if (!defaultField.length) {
            return;
        }
        if (inputField.val() == defaultField.val()) {
            inputField.val("");
        }
    };

    mod.showTooltip = function (event) {
        _timeoutArgs = event;
        clearTimeout(_timeout);
        _timeout = setTimeout( function () {
            console.log('timeout fired!');
            mod.showPointingPopup(_timeoutArgs);
        }, 400);
    };

    //just show a popup and remove the class name
    mod.focusSelect = function (event) {
        mod.showPointingPopup(event);
        jQuery(event.currentTarget).removeClass('usingNonDefaultValue');
    };

    mod.showPointingPopup = function (event) {
        var popup = jQuery("#descriptionPopup"),
            popupString,
            target,
            popupLeft,
            left,
            top,
            fieldDesc;

        if (!popup.length) {
            popupString = [];
            popupString.push('<div class="pointingPopup_container" id="descriptionPopup" style="display:none;">');
                popupString.push('<div class="pointingPopup_chevron ov pr">');
                popupString.push('</div>');
                popupString.push('<div class="pointingPopup_content" id="descriptionPopup_content">');
                popupString.push('</div>');
            popupString.push('</div>');
            jQuery('body').append(popupString.join(''));
        }
        target = jQuery(event.target);
        popupLeft = target.offset().left + target.width() + 12;
        left = popupLeft + 'px';
        top = (target.offset().top -2) + 'px';

        jQuery('#descriptionPopup').css({left:left, top:top});

        fieldDesc = target.closest('.focusableFieldHolder').nextAll('span.focusedFieldDescription');
        jQuery('#descriptionPopup_content').html(fieldDesc.html());
        jQuery('#descriptionPopup').show();
    };
};

jQuery(document).ready(function() {
    Yukon.namespace('Yukon.modules.FieldHelper');
    try {
        Sandbox('FieldHelper', function (mod) {
            Yukon.FieldHelper = mod;
        });
    } catch(fieldhelperex) {
    }
    Yukon.FieldHelper.init();
});
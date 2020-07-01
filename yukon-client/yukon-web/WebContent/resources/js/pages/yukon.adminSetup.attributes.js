yukon.namespace('yukon.adminSetup.attributes');

/** .
 * Handles behavior on the Admin Setup Attributes page
 * @module yukon.adminSetup.attributes
 * @requires JQUERY
 * @requires yukon
 */
yukon.adminSetup.attributes = (function () {

    'use strict';
    
    var _initialized = false,
    
    _toggleEditViewAttribute = function (attributeId, editMode) {
        var editSpan = $('.js-edit-attribute-' + attributeId),
            viewSpan = $('.js-view-attribute-' + attributeId);
        editSpan.toggleClass('dn', !editMode);
        viewSpan.toggleClass('dn', editMode);
        if (!editMode) {
            var nameField = editSpan.find('[name="name"]');
            //switch name back
            nameField.val(editSpan.find('[name="savedName"]').val());
            //remove any errors
            nameField.removeClass('error');
            editSpan.find('.error').text("");
        }
    },
    
    mod = {
    
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $('.js-attributes-table').scrollTableBody();
            
            $(document).on('yukon:attribute:delete', function (ev) {
                var attributeId = $(ev.target).data('attributeId'),
                    form = $('#delete-attribute-form-' + attributeId);
                form.submit();
            });       
            
            $(document).on('click', '.js-save-edit-attribute', function () {
                var attributeId = $(this).data('attributeId'),
                    editSpan = $('.js-edit-attribute-' + attributeId),
                    nameField = editSpan.find('[name="name"]'),
                    params = {
                        id: attributeId,
                        name: nameField.val()
                    };
                $.post(yukon.url('/admin/config/attribute/save'), params, function (data) {
                    if (data.errorMessage != null) {
                        nameField.addClass('error');
                        editSpan.find('.error').text(data.errorMessage);
                    } else {
                        window.location.href = window.location.href;
                    }
                });
            });
            
            $(document).on('click', '.js-edit-attribute', function () {
                var attributeId = $(this).data('attributeId');
                _toggleEditViewAttribute(attributeId, true);
            });
            
            $(document).on('click', '.js-cancel-edit-attribute', function () {
                var attributeId = $(this).data('attributeId');
                _toggleEditViewAttribute(attributeId, false);
            });
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () {yukon.adminSetup.attributes.init(); });
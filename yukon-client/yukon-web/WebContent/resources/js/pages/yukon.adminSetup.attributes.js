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
        $('.js-edit-attribute-' + attributeId).toggleClass('dn', !editMode)
        $('.js-view-attribute-' + attributeId).toggleClass('dn', editMode);
    },
    
    mod = {
    
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $('.js-attributes-table').scrollTableBody({rowsToDisplay: 10});
            
            $(document).on('yukon:attribute:delete', function (ev) {
                var attributeId = $(ev.target).data('attributeId'),
                    form = $('#delete-attribute-form-' + attributeId);
                form.submit();
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
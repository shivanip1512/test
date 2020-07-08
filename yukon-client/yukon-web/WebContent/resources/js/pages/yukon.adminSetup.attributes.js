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
            viewSpan = $('.js-view-attribute-' + attributeId),
            nameField = editSpan.find('[name="name"]');
        //start out with all in view mode again
        $('[class*="js-edit-attribute-"]').addClass('dn');
        $('[class*="js-view-attribute-"]').removeClass('dn');
        //toggle the attribute that was selected
        editSpan.toggleClass('dn', !editMode);
        viewSpan.toggleClass('dn', editMode);
        //set name to saved name
        nameField.val(editSpan.find('[name="savedName"]').val());
        //remove any errors
        nameField.removeClass('error');
        editSpan.find('.error').text("");
    },
    
    mod = {
    
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $('.js-attributes-table').scrollTableBody();
            $('.js-selected-attributes').chosen({'width': '350px'});
            $('.js-selected-device-types').chosen({'width': '350px'});
            
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
            
            $(document).on('click', '.js-filter-assignments', function() {
                var tableContainer = $('#assignments-container'),
                    form = $('#filter-form');
                form.ajaxSubmit({
                    success: function(data) {
                        tableContainer.html(data);
                        tableContainer.data('url', yukon.url('/admin/config/attributeAssignments/filter?' + form.serialize()));
                    }
                });   
            });
            
            $(document).on("yukon:assignment:save", function (event) {
                var popup = $(event.target);
                $('#assignment-form').ajaxSubmit({
                    success: function () {
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr) {
                        popup.html(xhr.responseText);
                    }
                });
            });
            
            $(document).on('yukon:assignment:delete', function (ev) {
                var assignmentId = $(ev.target).data('assignmentId'),
                    form = $('#delete-assignment-form-' + assignmentId);
                form.submit();
            });   
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () {yukon.adminSetup.attributes.init(); });
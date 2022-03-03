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
    
    _refreshAssignmentsTable = function (successMessage, errorMessage) {
        var tableContainer = $('#assignments-container'),
            form = $('#filter-form');
        form.ajaxSubmit({
            success: function(data) {
                tableContainer.html(data);
                tableContainer.data('url', yukon.url('/admin/config/attributeAssignments/filter?' + form.serialize()));
                if (successMessage) {
                    $('.js-success-msg').append(yukon.escapeXml(successMessage)).removeClass('dn');
                }
                if (errorMessage) {
                    $('.js-error-msg').append(yukon.escapeXml(errorMessage)).removeClass('dn');
                }
            },
            error: function (xhr, status, error, $form) {
                tableContainer.html(xhr.responseText);
            },
        });  
    },
    
    mod = {
    
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
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
                _refreshAssignmentsTable();
            });
            
            $(document).on('yukon:assignment:load', function (ev) {
                var popup = $(ev.target);
                popup.find('.js-device-types').chosen({'width': '350px'});
                if (popup.find('.user-message').is(':visible')) {
                    $('.ui-dialog-buttonset').find('.js-primary-action').prop('disabled', true);
                }
            });  
            
            $(document).on('yukon:assignment:pointSelected', function (ev, items, picker) {
                var pointType = items[0].pointType,
                    pointOffset = items[0].pointOffset,
                    dialog = $(ev.target);
                dialog.find('.js-point-type').val(pointType);
                dialog.find('.js-point-offset').val(pointOffset);
                
            });
            
            $(document).on('click', '.js-edit-assignment', function () {
                var assignmentId = $(this).data('assignmentId'),
                    url = yukon.url('/admin/config/attributeAssignments/popup?id=' + assignmentId),
                    popup = $('.js-edit-assignment-popup'),
                    popupTitle = popup.data('title'),
                    dialogDivJson = {
                        "data-url" : url,
                        "data-dialog": '',
                        "data-load-event" : "yukon:assignment:load",
                        "data-event" : "yukon:assignment:save",
                        "data-title" : popupTitle,
                        "data-ok-text" : yg.text.save,
                        "data-destroy-dialog-on-close" : "",
                    };
                
                yukon.ui.dialog($("<div/>").attr(dialogDivJson));
            });
            
            $(document).on("yukon:assignment:save", function (event) {
                var popup = $(event.target),
                    attributeName = popup.find('#attributeId option:selected').text();
                popup.find('#attributeName').val(yukon.escapeXml(attributeName));
                popup.find('#assignment-form').ajaxSubmit({
                    success: function (data) {
                        popup.dialog('close');
                        //refresh assignments table
                        _refreshAssignmentsTable(data.successMessage, data.errorMessage);
                    },
                    error: function (xhr) {
                        popup.html(xhr.responseText);
                        popup.find('.js-device-types').chosen({'width': '350px'});
                    }
                });
            });
            
            $(document).on('yukon:assignment:delete', function (ev) {
                var assignmentId = $(ev.target).data('assignmentId'),
                    form = $('#delete-assignment-form-' + assignmentId);
                form.ajaxSubmit({
                    success: function (data) {
                        _refreshAssignmentsTable(data.successMessage, data.errorMessage);
                    },
                    error: function (xhr) {
                        _refreshAssignmentsTable(xhr.successMessage, xhr.errorMessage);
                    }
                })
            });   
            
            _initialized = true;
        }

    };
    
    return mod;
})();
 
$(function () {yukon.adminSetup.attributes.init(); });
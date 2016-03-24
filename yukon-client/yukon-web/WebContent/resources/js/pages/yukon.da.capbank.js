yukon.namespace('yukon.da.capbank');

/**
 * Module for the volt/var capbank page.
 * @module yukon.da.capbank
 * @requires JQUERY
 * @requires yukon
 */
yukon.da.capbank = (function () {

    'use strict';
    var initialized = false;
    
    var setCustomCommunicationMedium = function () {
        var toggle = $('#customMediumCheckbox'),
            commMedium = $('#commMedium'),
            customCommMedium = $('#customCommMedium'),
            active = toggle.prop('checked');
    
        if (active) {
            var commValue = commMedium.val();
            commMedium.val(null);
            commMedium.addClass("dn");
            customCommMedium.removeClass("dn");
            if (initialized) 
                customCommMedium.val(commValue);
        } else {
            commMedium.removeClass("dn");
            customCommMedium.val(null);
            customCommMedium.addClass("dn");
            if (initialized) 
                commMedium.val("None");
        }
    };

    mod = {

        /** Initialize this module. */
        init: function () {

            if (initialized) return;
            
            setCustomCommunicationMedium();
                        
            /** User confirmed intent to delete capbank. */
            $(document).on('yukon:da:capbank:delete', function () {
                $('#delete-capbank').submit();
            });
            
            /** User clicked Custom Communication Medium */
            $(document).on('click', '.js-custom-medium', function () {
                setCustomCommunicationMedium(true);
            });
            
            /** User clicked on Remove Point button */
            $(document).on('click', '.js-remove-point', function () {
                var button = $(this),
                    pointRow = button.closest("tr");
                
                pointRow.remove();

            });
            
            /** User clicked save on point assignment dialog. */
            $(document).on('yukon:vv:children:save', function (ev) {

                var container = $(ev.target),
                    parentId = container.data('parentId'),
                    children = [];

                container.find('.select-box-selected .select-box-item').each(function (idx, item) {
                    children.push($(item).data('id'));
                });

                $.ajax({
                    url: yukon.url('/capcontrol/capbanks/' + parentId + '/points'),
                    method: 'post',
                    data: { children: children}
                }).done(function () {
                    window.location.reload();
                });

            });
            
            initialized = true;

        }
    };

    return mod;
})();

$(function () { yukon.da.capbank.init(); });
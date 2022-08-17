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
    
    /**
     * @type Array.string
     *
     * Filled out in yukon.da.capbank.init().
     * Array of strings of paoTypes that represent 2 way cbcs and logical cbcs
     */
    var twoWayTypes = [],
        logicalTypes = [];

    var updatePaoTypeFields = function () {
         var isCreateCBC = $('.js-createCBC').prop('checked');
         //Resets new CBC field and also the errors if any.
         if(!isCreateCBC){
             $('.js-cbcControllerName').val("");
             $('.js-cbcControllerName').removeClass("error");
             $("[id='cbcControllerName.errors']").remove();
             $('.js-logical-cbc').addClass('dn');
         }
        $('#comm-port').attr("disabled", true); 
        var paoTypeField = $('#pao-type');
        
        if(paoTypeField.is(":visible")) {
            var paoType = paoTypeField.val();
            var isTwoWay = twoWayTypes.indexOf(paoType) !== -1,
                isLogical = logicalTypes.indexOf(paoType) !== -1;
            if (isTwoWay) {
               $('#comm-port').removeAttr("disabled"); 
            } 
            $('.js-logical-cbc').toggleClass('dn', !isLogical);
        }
    };
    
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
    
    var setCustomBankSize = function () {
        var toggle = $('#customSizeCheckbox'),
            bankSize = $('#bankSize'),
            customBankSize = $('#customBankSize'),
            active = toggle.prop('checked');
    
        if (active) {
            var commValue = bankSize.val();
            bankSize.val(600);
            bankSize.addClass("dn");
            customBankSize.removeClass("dn");
            if (initialized) 
                customBankSize.val(commValue);
        } else {
            bankSize.removeClass("dn");
            customBankSize.val(null);
            customBankSize.addClass("dn");
            if (initialized) 
                bankSize.val(600);
        }
    };

    /**
     * When disabling Max Daily Operation, make the value 0
     */
    var updateMaxDailyOperation = function (event) {
        var checkbox = $(event.currentTarget);
        var toggleGroup = checkbox.data('toggle');
        var input = $('[data-toggle-group="' + toggleGroup + '"]');
        
        if (!checkbox.is(':checked')) {
                input.val(0);
         }
    };
    
    mod = {

        /** Initialize this module. */
        init: function () {

            if (initialized) return;
            twoWayTypes = yukon.fromJson('#two-way-types');
            logicalTypes = yukon.fromJson('#logical-types');

            setCustomCommunicationMedium();
            setCustomBankSize();
            
            updatePaoTypeFields();
            $('.js-create-cbc').on('change', updatePaoTypeFields); 
            $('#pao-type').on('change', updatePaoTypeFields);
            $('.js-use-maxDailyOp').on('change', updateMaxDailyOperation);
            
            /** User confirmed intent to delete capbank. */
            $(document).on('yukon:da:capbank:delete', function () {
                $('#delete-capbank').submit();
            });
            /** User clicked Custom Communication Medium */
            $(document).on('click', '.js-custom-medium', function () {
                setCustomCommunicationMedium(true);
            });
            
            /** User clicked Custom Bank Size */
            $(document).on('click', '.js-custom-bankSize', function () {
                setCustomBankSize(true);
            });
            
            /** User clicked on Remove Point button */
            $(document).on('click', '.js-remove-capbank-point', function () {
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
            
            yukon.ui.highlightErrorTabs();
        }
    };

    return mod;
})();

$(function () { yukon.da.capbank.init(); });
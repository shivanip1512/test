yukon.namespace('yukon.dr.setup.loadGroup');

/**
 * Module that handles the behavior on the setup Load Group page.
 * @module yukon.dr.setup.loadGroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.loadGroup = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _initCheckboxes = function () {
        if ($('.loadaddressing').exists()) {
            $('.loadaddressing').find("input:checkbox").addClass("js-loadaddress");
        }
        if ($('.addressUsage').exists()) {
            $('.addressUsage').find("input:checkbox").addClass("js-addressUsage");
        }
        if ($('.feederChk').exists()) {
            $('.feederChk').find("input:checkbox").addClass("js-feederChk");
        }
        if ($('.verAddressUsage').exists()) {
            $('.verAddressUsage').find("input:checkbox").addClass("js-verAddressUsage");
        }
        if ($('.classAddress').exists()) {
            $('.classAddress').find("input:checkbox").addClass("js-classAddress");
        }
        if ($('.divisionAddress').exists()) {
            $('.divisionAddress').find("input:checkbox").addClass("js-divisionAddress");
        }
    },

    _loadGroup = function() {
        var type = $('#type').val(); 
        if (type !== '') {
            yukon.ui.block($('js-loadgroup-container'));
        
            var name = $('#name').val();
            $.ajax({
                url: yukon.url('/dr/setup/loadGroup/create/' + type),
                type: 'get',
               data: {name: name}
            }).done(function(data) {
                 $('#loadGroup').html(data);
                 _initCheckboxes();
                 yukon.ui.unblock($('js-loadgroup-container'));
           });
        } else {
            $('.noswitchtype').html('');
        }
    }, 
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _initCheckboxes();
             
            $(document).on('click', '#js-cancel-btn', function (event) {
                window.history.back();
            });
            
            $(document).on("yukon:loadGroup:delete", function () {
                yukon.ui.blockPage();
                $('#delete-loadGroup-form').submit();
            });
            $(document).on("yukon:loadGroup:copy", function () {
                yukon.ui.blockPage();
                $('#loadGroup-copy-form').ajaxSubmit({
                    success: function (data, status, xhr, $form) {
                        if (!$.isEmptyObject(data.redirectUrl))
                            window.location.href=yukon.url(data.redirectUrl);
                        else
                            window.location.href=yukon.url('/dr/setup/loadGroup/' + data.groupId);
                    },
                    error: function (xhr, status, error, $form) {
                        $('#copy-loadGroup-popup').html(xhr.responseText);
                        yukon.ui.initContent('#copy-loadGroup-popup');
                        yukon.ui.unblockPage();
                    }
                });
            });
            
            $(document).on('change', '#type', function (event) {
                _loadGroup();
            });
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.loadGroup.init(); });
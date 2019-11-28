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
    
    _buildAddressString = function (checkboxContainer) {
        var addressStr ='';
        checkboxContainer.find('input[type=checkbox]').each(function () {
            addressStr = addressStr + (this.checked ? "1" : "0");
        });
        return addressStr;
    },
    
    _setAddressCheckboxes = function (checkboxContainer, addressValue) {
        checkboxContainer.find('input[type=checkbox]').each(function (index, item) {
           if(addressValue.charAt(index) == '1') {
                $(item).prop('checked', true);
            }
        });
    },
    
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
    
    _retrievePointState = function() {
        var pointId = $("#js-control-point-selected").val();
        var container = $("#js-loadgroup-container");
        if (!$.isEmptyObject(pointId) && pointId.length !== 0){
            container.find("#js-start-state").removeClass("dn");
            $.ajax({
                url: yukon.url('/dr/setup/loadGroup/getStartState/' + pointId),
                type: 'get'
            }).done(function (data) {
                $("#js-loadgroup-container").find("#js-control-start-state").empty();
                data.startStates.forEach(function (field){
                    var option = $('<option value=' + field.id + '>' + field.name + '</option>');
                    container.find("#js-control-start-state").append(option);
                });
            });
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _initCheckboxes();
            
            if ($("#js-shed-time").exists()) {
                var controlAddress = $(".js-control-value").val(),
                       restoreAddress = $(".js-restore-value").val(),
                       controlAddressLength = controlAddress.length,
                       restoreAddressLength = restoreAddress.length;
                _setAddressCheckboxes($(".js-control-value_row1"), controlAddress.substring(0, controlAddressLength/2));
                _setAddressCheckboxes($(".js-control-value_row2"), controlAddress.substring((controlAddressLength / 2), controlAddressLength + 1));
                _setAddressCheckboxes($(".js-restore-value_row1"), restoreAddress.substring(0, restoreAddressLength / 2));
                _setAddressCheckboxes($(".js-restore-value_row2"), restoreAddress.substring((restoreAddressLength / 2), restoreAddressLength + 1));
             
            if ($('.js-create-mode').val() == 'true' && $('.js-group-type').val() == 'true' && $('.js-device-error').val() == 'false') {
                _retrievePointState();
            }
            
            if ($('.js-edit-mode').val() == 'true' && $('.js-group-type').val() == 'true') {
                var container = $("#js-loadgroup-container");
                container.find("#js-start-state").removeClass("dn");
            }
            
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
            
            $(document).on('change', '.js-address-level', function (event) {
                var container = $(this).closest(".js-mct-load-group-container"),
                       addressTextRow = container.find(".js-address"),
                       mctAddressRow = container.find(".js-mct-address"),
                       mctAdressEnumVal = container.find(".js-addr-level-mct-addr-val").val(),
                       isMctAddrSelected = $(this).val() === mctAdressEnumVal;

                addressTextRow.toggleClass("dn", isMctAddrSelected);
                mctAddressRow.toggleClass("dn", !isMctAddrSelected);
                
                if (isMctAddrSelected) {
                    container.find(".js-address-txt").val('');
                } else {
                    var uniqueId = container.find(".js-unique-value").val(),
                           mctPicker = yukon.pickers['mctMeterPicker_' + uniqueId];
                    mctPicker.clearSelected.call(mctPicker);
                }
            });
            
            $(document).on('change', '#type', function (event) {
                _loadGroup();
            });
            
            $(document).on('submit', '.js-load-group-form', function () {
                /* check if Ripple Load Group is selected */
                if ($("#js-shed-time").exists()) {
                    var controlAddress = _buildAddressString($(".js-control-value_row1"));
                    controlAddress = controlAddress + _buildAddressString($(".js-control-value_row2"));
                    $(".js-control-value").val(controlAddress);
                    var restoreAddress = _buildAddressString($(".js-restore-value_row1"));
                    restoreAddress = restoreAddress + _buildAddressString($(".js-restore-value_row2"));
                    $(".js-restore-value").val(restoreAddress);
                } 
            });
            
            $(document).on('yukon:pointGroup:point:selected', function (event, items, picker) {
                if (!$.isEmptyObject(items)){
                    $('#js-control-device-selected').val(items[0].paObjectId);
                    var pointId = items[0].pointId;
                    $("#js-control-point-selected").val(pointId);
                    if (!$.isEmptyObject(pointId)){
                        _retrievePointState();
                    }
                }
            });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.loadGroup.init(); });
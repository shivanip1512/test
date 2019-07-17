yukon.namespace('yukon.dr.setup.loadGroup.expresscom');

/**
 * Module that handles the behavior on the setup Expresscom Load Group page.
 * @module yukon.dr.setup.loadGroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.loadGroup.expresscom = (function() {
    
    'use strict';
    
    var
    _initialized = false,
    
    _handleAddressing = function () {
        if ($("input[id=GEO_chk]:checked").length !== 0) {
            $('#js-geoRow').show();	
        } else {
            $('#js-geoRow').hide();
        }
        if ($("input[id=SUBSTATION_chk]:checked").length !== 0) {
            $('#js-substationRow').show();	
        } else {
            $('#js-substationRow').hide();
        }
        if ($("input[id=FEEDER_chk]:checked").length !== 0) {
            $('#js-feederRow').show();	
        } else {
            $('#js-feederRow').hide();
        }
        if ($("input[id=ZIP_chk]:checked").length !== 0) {
            $('#js-zipRow').show();	
        } else {
            $('#js-zipRow').hide();
        }
        if ($("input[id=USER_chk]:checked").length !== 0) {
            $('#js-userRow').show();	
        } else {
            $('#js-userRow').hide();
        }
        if ($("input[id=SERIAL_chk]:checked").length !== 0) {
            $('#js-serialRow').show();
            $('#SERIAL_chk').prop("checked", true);
            
            // disable other buttons
            $('#GEO_chk').prop("disabled", true);
            $('#SUBSTATION_chk').prop("disabled", true);
            $('#FEEDER_chk').prop("disabled", true);
            $('#ZIP_chk').prop("disabled", true);
            $('#USER_chk').prop("disabled", true);
            
            // disable other textbox
            $('#js-geo').attr("disabled", "disabled");
            $('#js-substation').attr("disabled", "disabled");
            $('#js-zip').attr("disabled", "disabled");
            $('#js-user').attr("disabled", "disabled");
            $('#js-feeder').find('input[type=checkbox]').prop("disabled", true);
        } else {
             $('#js-serialRow').hide();
             // enable other checkbox
             $('#GEO_chk').prop("disabled", false);
             $('#SUBSTATION_chk').prop("disabled", false);
             $('#FEEDER_chk').prop("disabled", false);
             $('#ZIP_chk').prop("disabled", false);
             $('#USER_chk').prop("disabled", false);
             
             // enable textbox
             $('#js-geo').removeAttr("disabled");
             $('#js-substation').removeAttr("disabled");
             $('#js-feeder').removeAttr("disabled");
             $('#js-zip').removeAttr("disabled");
             $('#js-user').removeAttr("disabled");
             $('#js-feeder').find('input[type=checkbox]').prop("disabled", false);
        }
     },
    _handleLoads = function () {
        if ($("input[id=PROGRAM_chk]:checked").length !== 0) {
            $('#js-programRow').show();	
        } else {
            $('#js-programRow').hide();
        }
        if ($("input[id=SPLINTER_chk]:checked").length !== 0) {
            $('#js-splinterRow').show();	
        } else {
            $('#js-splinterRow').hide();
        }
        if ($("input[id=LOAD_chk]:checked").length !== 0) {
            $('#js-sendControlMessageYes').removeClass('dn');
            $('#js-sendControlMessageNo').addClass('dn');
        } else {
            $('#js-sendControlMessageYes').addClass('dn');
            $('#js-sendControlMessageNo').removeClass('dn');
        }
        if ($("input[id=LOAD_chk]:checked").length !== 0) {
            if (($("input[id=LOAD_chk]:checked").length !== 0) && 
            ($("input[id=PROGRAM_chk]:checked").length !== 0 || $("input[id=SPLINTER_chk]:checked").length !== 0)) {
            var popup = $('#addressing-popup'),
                title = popup.data('title'),
                okButtonText = popup.data('okText'),
                cancelButtonText = popup.data('cancelText'),
                width = popup.data('width');
            popup.dialog({
                title: title, 
                modal: true,
                width: width,
                buttons: yukon.ui.buttons({okText: okButtonText, event: 'yukon:uncheck:load',
                    cancelText: cancelButtonText})
               });
            }
        }
    },
    _buildFeederValue = function() {
        var feederValue ='';
        $('#js-feeder').find('input[type=checkbox]').each(function () {
            var thisFeederValue = (this.checked ? "1" : "0");
            feederValue = feederValue + thisFeederValue;
        });
        $("#feederValueString").val(feederValue);
    }, 
    _setFeederValue = function() {
        var feederValue = $("#feederValueString").val();
        var i=0;
        $('#js-feeder').find('input[type=checkbox]').each(function () {
           if(feederValue.charAt(i) == '1') {
                this.checked = true;
            }
            i++;
        });
        $("#feederValueString").val(feederValue);
    }, 
    _addressUsage = function() {
        var addressUsage = $(allAddressUsage).val();
        if(addressUsage.indexOf('GEO') > -1) {
            $('#GEO_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('SUBSTATION') > -1) {
            $('#SUBSTATION_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('FEEDER') > -1) {
            $('#FEEDER_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('ZIP') > -1) {
            $('#ZIP_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('USER') > -1) {
            $('#USER_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('SERIAL') > -1) {
            $('#SERIAL_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('LOAD') > -1) {
            $('#LOAD_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('PROGRAM') > -1) {
            $('#PROGRAM_chk').prop("checked", true)
        }
        if(addressUsage.indexOf('SPLINTER') > -1) {
            $('#SPLINTER_chk').prop("checked", true)
        }
    },
    _setValues = function() {
        if ($("input[id=GEO_chk]:checked").length === 0) {
            $('#js-geo').val();	
        }
        if ($("input[id=SUBSTATION_chk]:checked").length === 0) {
            $('#js-substation').val();
        }
        if ($("input[id=FEEDER_chk]:checked").length === 0) {
            $('#feederValueString').val();	
        }
        if ($("input[id=ZIP_chk]:checked").length === 0) {
            $('#js-zip').val();
        }
        if ($("input[id=USER_chk]:checked").length === 0) {
            $('#js-user').val();	
        }
        if ($("input[id=SERIAL_chk]:checked").length === 0) {
            $('#js-user').val();	
        }
        if ($("input[id=PROGRAM_chk]:checked").length === 0) {
            $('#js-program').val();	
        }
        if ($("input[id=SPLINTER_chk]:checked").length === 0) {
            $('#js-splinter').val();	
        }
    }
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
           var _mode = $('.js-page-mode').val();
           if(_mode !== 'VIEW' && ($(type).val() === 'LM_GROUP_EXPRESSCOMM' ||
                $(type).val() === 'LM_GROUP_RFN_EXPRESSCOMM')) {
                _addressUsage();
                _handleAddressing();
                _setFeederValue();
                _handleLoads();
           }
           
            $(document).on('click', '#js-addressUsage', function (event) {
                _handleAddressing();
            });
            $(document).on('click', '#js-loadAddressUsage', function (event) {
                _handleLoads();
            });
            $(document).on('click', '#js-feederRow', function (event) {
               _buildFeederValue();
            });
            $(document).on('click', '#save', function (event) {
                _setValues();
             });
            $(document).on('yukon:uncheck:load', function (ev) {
                $('#LOAD_chk').prop("checked", false);
                $('#js-sendControlMessageYes').addClass('dn');
                $('#js-sendControlMessageNo').removeClass('dn');
                $("#addressing-popup").dialog('close');
             });
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.loadGroup.expresscom.init(); });
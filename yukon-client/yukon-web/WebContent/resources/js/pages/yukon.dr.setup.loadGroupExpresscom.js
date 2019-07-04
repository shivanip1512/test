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
            $('#js-geo').val(0);
            $('#js-geoRow').hide();
        }
        if ($("input[id=SUBSTATION_chk]:checked").length !== 0) {
            $('#js-substationRow').show();	
        } else {
            $('#js-substation').val(0);
            $('#js-substationRow').hide();
        }
        if ($("input[id=FEEDER_chk]:checked").length !== 0) {
            $('#js-feederRow').show();	
        } else {
            $('#js-feeder').find('input[type=checkbox]:checked').prop("checked", false)
            $('#js-feederRow').hide();
        }
        if ($("input[id=ZIP_chk]:checked").length !== 0) {
            $('#js-zipRow').show();	
        } else {
            $('#js-zip').val(0);
            $('#js-zipRow').hide();
        }
        if ($("input[id=USER_chk]:checked").length !== 0) {
            $('#js-userRow').show();	
        } else {
            $('#js-user').val(0);
            $('#js-userRow').hide();
        }
        if ($("input[id=SERIAL_chk]:checked").length !== 0) {
            $('#js-serialRow').show();
            $('#js-geo').val(0);
            $('#js-geoRow').hide();
            $('#js-substation').val(0);
            $('#js-substationRow').hide();
            $('#js-feeder').find('input[type=checkbox]:checked').prop("checked", false)
            $('#js-feederRow').hide();
            $('#js-zip').val(0);
            $('#js-zipRow').hide();
            $('#js-user').val(0);
            $('#js-userRow').hide();
            
            // uncheck all checkbox except for serial
            $('#js-addressUsage').find('input[type=checkbox]:checked').prop("checked", false)
            $('#SERIAL_chk').prop("checked", true);
        } else {
            $('#js-serial').val(0);
            $('#js-serialRow').hide();
        }
     },
    _handleLoads = function () {
        if ($("input[id=PROGRAM_chk]:checked").length !== 0) {
            $('#js-programRow').show();	
        } else {
            $('#js-program').val(0);
            $('#js-programRow').hide();
        }
        if ($("input[id=SPLINTER_chk]:checked").length !== 0) {
            $('#js-splinterRow').show();	
        } else {
            $('#js-splinter').val(0);
            $('#js-splinterRow').hide();
        }
        if ($("input[id=LOAD_chk]:checked").length !== 0) {
            $('#js-sendControlMessageYes').removeClass('dn');
            $('#js-sendControlMessageNo').addClass('dn');
        } else {
            $('#js-sendControlMessageYes').addClass('dn');
            $('#js-sendControlMessageNo').removeClass('dn');
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
            
            _initialized = true;
        }
    };
    
    return mod;
})();

$(function () { yukon.dr.setup.loadGroup.expresscom.init(); });
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
            $('#js-geoRow').toggleClass('dn', !($('#GEO_chk').is(':checked')));
            $('#js-substationRow').toggleClass('dn', !($('#SUBSTATION_chk').is(':checked')));
            $('#js-feederRow').toggleClass('dn', !($('#FEEDER_chk').is(':checked')));
            $('#js-zipRow').toggleClass('dn', !($('#ZIP_chk').is(':checked')));
            $('#js-userRow').toggleClass('dn', !($('#USER_chk').is(':checked')));

           if($('#SERIAL_chk').is(':checked')) {
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
    _handleLoads = function (mode) {
        $('#js-programRow').toggleClass('dn', $('input[id=PROGRAM_chk]:checked').length == 0);
        $('#js-splinterRow').toggleClass('dn', $('input[id=SPLINTER_chk]:checked').length == 0);
        if($('#LOAD_chk').is(':checked')) {
            $('#js-sendControlMessageYes').removeClass('dn');
            $('#js-sendControlMessageNo').addClass('dn');
        } else {
            $('#js-sendControlMessageYes').addClass('dn');
            $('#js-sendControlMessageNo').removeClass('dn');
        }
    },
    _showConfirmation = function() {
        if($('#LOAD_chk').is(':checked')) {
            if(($('#PROGRAM_chk').is(':checked')) || $('#SPLINTER_chk').is(':checked')) {
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
        if (addressUsage.indexOf('GEO') > -1) {
            $('#GEO_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('SUBSTATION') > -1) {
            $('#SUBSTATION_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('FEEDER') > -1) {
            $('#FEEDER_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('ZIP') > -1) {
            $('#ZIP_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('USER') > -1) {
            $('#USER_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('SERIAL') > -1) {
            $('#SERIAL_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('LOAD') > -1) {
            $('#LOAD_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('PROGRAM') > -1) {
            $('#PROGRAM_chk').prop("checked", true)
        }
        if (addressUsage.indexOf('SPLINTER') > -1) {
            $('#SPLINTER_chk').prop("checked", true)
        }
    },
    _setValues = function() {
        if (!($('#GEO_chk').is(':checked'))) {
            $('#js-geo').val(0);
        }
        if (!($('#SUBSTATION_chk').is(':checked'))) {
            $('#js-substation').val(0);
        }
        if (!($('#FEEDER_chk').is(':checked'))) {
            $('#feederValueString').val(0);	
        }
        if (!($('#ZIP_chk').is(':checked'))) {
            $('#js-zip').val(0);
        }
        if (!($('#USER_chk').is(':checked'))) {
            $('#js-user').val(0);
        }
        if (!($('#SERIAL_chk').is(':checked'))) {
            $('#js-serial').val(0);
        }
        if (!($('#PROGRAM_chk').is(':checked'))) {
            $('#js-program').val(0);
        }
        if (!($('#SPLINTER_chk').is(':checked'))) {
            $('#js-splinter').val(0);
        }
    }
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
           if (_initialized) return;

           var _mode = $('.js-page-mode').val();
           if (_mode !== 'VIEW' && ($("#type").val() === 'LM_GROUP_EXPRESSCOMM' ||
               $("#type").val() === 'LM_GROUP_RFN_EXPRESSCOMM')) {
               _addressUsage();
               _handleAddressing();
               _setFeederValue();
               _handleLoads();
           }
           
            $(document).on('change', '.js-addressUsage', function (event) {
                _handleAddressing();
            });
            $(document).on('change', '.js-feederChk', function (event) {
               _buildFeederValue();
            });
            $(document).on('click', '#save', function (event) {
                _setValues();
             });
            $(document).on('change', '.js-loadaddress', function (event) {
                _handleLoads();
                if (this.checked) {
                    _showConfirmation();
               }
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
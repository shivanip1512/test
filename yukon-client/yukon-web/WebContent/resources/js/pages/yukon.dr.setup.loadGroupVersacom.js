yukon.namespace('yukon.dr.setup.loadGroup.versacom');

/**
 * Module that handles the behavior on the setup Versacom Load Group page.
 * @module yukon.dr.setup.loadGroup
 * @requires JQUERY
 * @requires yukon
 */
yukon.dr.setup.loadGroup.versacom = (function() {

'use strict';
var
_initialized = false,

_handleAddressUsage = function () {
    $('#js-sectionAddress-row').toggleClass('dn', (!$('#SECTION_chk').is(':checked')));
    $('#js-classAddress-row').toggleClass('dn', (!$('#CLASS_chk').is(':checked')));
    $('#js-divisionAddress-row').toggleClass('dn', (!$('#DIVISION_chk').is(':checked')));
    if ($('#SERIAL_chk').is(':checked')) {
        $('#js-serialAddress-row').show();
        $('#SERIAL_chk').prop("checked", true);
        // disable other buttons
        $('#SECTION_chk').prop("disabled", true);
        $('#CLASS_chk').prop("disabled", true);
        $('#DIVISION_chk').prop("disabled", true);
        // disable other textbox
        $('#js-sectionAddress').attr("disabled", "disabled");
        $('#js-classAddress').find('input[type=checkbox]').prop("disabled", true);
        $('#js-divisionAddress').find('input[type=checkbox]').prop("disabled", true);
    } else {
         $('#js-serialAddress-row').hide();
         // enable other checkbox
         $('#SECTION_chk').prop("disabled", false);
         $('#CLASS_chk').prop("disabled", false);
         $('#DIVISION_chk').prop("disabled", false);
         
         // enable textbox
         $('#js-sectionAddress').removeAttr("disabled");
         $('#js-sectionAddress').val('0');
         $('#js-classAddress').find('input[type=checkbox]').prop("disabled", false);
         $('#js-divisionAddress').find('input[type=checkbox]').prop("disabled", false);
    }
 },

_setValues = function() {
    if (!$('#SECTION_chk').is(':checked')) {
        $('#js-sectionAddress').val('0');
    }
    if (!$('#CLASS_chk').is(':checked')) {
        $('#classAddressString').val('');
    }
    if (!$('#DIVISION_chk').is(':checked')) {
        $('#divisionAddressString').val('');
    }
    if (!$('#SERIAL_chk').is(':checked')) {
        $('#js-serialAddress').val('0');
    }
},

_buildClassAddressValue = function() {
    var classAddressValue ='';
    $('#js-classAddress').find('input[type=checkbox]').each(function () {
        var thisClassAddress = (this.checked ? "1" : "0");
        classAddressValue = classAddressValue + thisClassAddress;
    });
    $("#classAddressString").val(classAddressValue);
},
_setClassAddressValue = function() {
    var classAddressValue = $("#classAddressString").val();
    var i=0;
    $('#js-classAddress').find('input[type=checkbox]').each(function () {
       if(classAddressValue.charAt(i) == '1') {
            this.checked = true;
        }
        i++;
    });
    $("#classAddressString").val(classAddressValue);
},

_buildDivisionAddressValue = function() {
    var divisionAddressValue ='';
    $('#js-divisionAddress').find('input[type=checkbox]').each(function () {
        var thisDivisionAddress = (this.checked ? "1" : "0");
        divisionAddressValue = divisionAddressValue + thisDivisionAddress;
    });
    $("#divisionAddressString").val(divisionAddressValue);
},

_setDivisionAddressValue = function() {
    var divisionAddressValue = $("#divisionAddressString").val();
    var i=0;
    $('#js-divisionAddress').find('input[type=checkbox]').each(function () {
       if(divisionAddressValue.charAt(i) == '1') {
            this.checked = true;
        }
        i++;
    });
    $("#divisionAddressValue").val(divisionAddressValue);
},

_setAddressUsage = function() {
    var addressUsage = $("#allVersaAddressUsage").val();
    $(".js-verAddressUsage").find("input:checkbox").each(function (index, item) {
        var itemValue = $(item).val();
        if(addressUsage.indexOf(itemValue) > -1) {
            $('#' + itemValue + '_chk').prop("checked", true)
        }
    });
}

mod = {
    
    /** Initialize this module. */
    init: function () {
       if (_initialized) return;
       var _mode = $('.js-page-mode').val();
       if(_mode !== 'VIEW' && ($("#type").val() === 'LM_GROUP_VERSACOM')) {
           _handleAddressUsage();
           _setClassAddressValue();
           _setDivisionAddressValue();
           _setAddressUsage();
       }

       // Select Address Usage options
       $(document).on('change', '.js-verAddressUsage', function (event) {
               _handleAddressUsage();
       });

        // Select class Address bit bus
        $(document).on('change', '.js-classAddress', function (event) {
            _buildClassAddressValue();
        });

        // Select Division Address bit bus
        $(document).on('click', '.js-divisionAddress', function (event) {
            _buildDivisionAddressValue();
        });

        $(document).on('click', '#save' , function (event) {
            _setValues();
        });

        _initialized = true;
    }
   };

return mod;
})();

$(function () { yukon.dr.setup.loadGroup.versacom.init(); });
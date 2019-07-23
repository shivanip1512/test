yukon.namespace('yukon.dr.setup.loadGroup.versacom');

yukon.dr.setup.loadGroup.versacom = (function() {

'use strict';
var
_initialized = false,

_handleAddressUsage = function () {
    if ($('#UTILITY_chk').is(':checked')) {
        $('#js-utilityAddress-row').show();
    } else {
        $('#js-utilityAddress-row').hide();
    }

    if ($('#SECTION_chk').is(':checked')) {
        $('#js-sectionAddress-row').show();
    } else {
        $('#js-sectionAddress-row').hide();
    }

    if ($('#CLASS_chk').is(':checked')) {
        $('#js-classAddress-row').show();
    } else {
        $('#js-classAddress-row').hide();
    }

    if ($('#DIVISION_chk').is(':checked')) {
        $('#js-divisionAddress-row').show();
    } else {
        $('#js-divisionAddress-row').hide();
    }

    if ($('#SERIAL_chk').is(':checked')) {
        $('#js-serialAddress-row').show();
    } else {
        $('#js-serialAddress-row').hide();
    }
 },

_setValues = function() {
    if (!$('#UTILITY_chk').is(':checked')) {
        $('#js-utilityAddress').val('1');
    }
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
    var addressUsage = $(allVersaAddressUsage).val();
    if(addressUsage.indexOf('UTILITY') > -1) {
        $('#UTILITY_chk').prop("checked", true)
    }
    if(addressUsage.indexOf('SECTION') > -1) {
        $('#SECTION_chk').prop("checked", true)
    }
    if(addressUsage.indexOf('CLASS') > -1) {
        $('#CLASS_chk').prop("checked", true)
    }
    if(addressUsage.indexOf('DIVISION') > -1) {
        $('#DIVISION_chk').prop("checked", true)
    }
    if(addressUsage.indexOf('SERIAL') > -1) {
        $('#SERIAL_chk').prop("checked", true)
    }
}
mod = {
    
    /** Initialize this module. */
    init: function () {
       var _mode = $('.js-page-mode').val();
       if(_mode !== 'VIEW' && ($(type).val() === 'LM_GROUP_VERSACOM')) {
           _handleAddressUsage();
           _setClassAddressValue();
           _setDivisionAddressValue();
           _setAddressUsage();
       }
       
       // Select Address Usage options
       $(document).on('click', '#js-versaAddressUsage', function (event) {
           if(_mode !== 'VIEW') {
               _handleAddressUsage();
           }
       });

        // Select class Address bit bus
        $(document).on('click', '#js-classAddress-row', function (event) {
            _buildClassAddressValue();
        });
        
        // Select Division Address bit bus
        $(document).on('click', '#js-divisionAddress-row', function (event) {
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
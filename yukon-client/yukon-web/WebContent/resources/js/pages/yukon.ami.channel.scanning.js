/**
 * Handles profile channel scanning page.
 * 
 * @module yukon.ami.channel.scanning
 */

yukon.namespace('yukon.ami.channel.scanning');

yukon.ami.channel.scanning = (function (){
    var newToggleVal = false,

    mod = {
            init: function() {
             $(document).on('yukon.toggle.click', '#toggle-state', function (ev) {
             $('#confirm-popup').dialog('close');
             doToggleScanning(0,newToggleVal);
            });

            $(document).on('click', ".js-toggle-profile-on", function(e){
            newToggleVal = true;
            yukon.ui.dialog('#confirm-popup');
            });

           $(document).on('click', ".js-toggle-profile-off", function(e){
           newToggleVal = false;
           doToggleScanning(0,newToggleVal);
           });	
         }
       };
    return mod;
})();

$(function() {
	yukon.ami.channel.scanning.init();
});


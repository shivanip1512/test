/**
 * Handles profile channel scanning page.
 * 
 * @module yukon.ami.channel.scanning
 */

yukon.namespace('yukon.ami.channel.scanning');

yukon.ami.channel.scanning = {
init: function() {
    $(document).on('yukon.toggle.click', '#toggle-state', function (ev) {
    $('#confirm-popup').dialog('close');
    doToggleScanning(0,newToggleVal);
});

$(document).on('click', ".js-toggle-profile-on", function(e){
    newToggleVal = false;
    yukon.ui.dialog('#confirm-popup');
});

$(document).on('click', ".js-toggle-profile-off", function(e){
    newToggleVal = true;
    doToggleScanning(0,newToggleVal);
});
}
};

$(function() {
	yukon.ami.channel.scanning.init();
});


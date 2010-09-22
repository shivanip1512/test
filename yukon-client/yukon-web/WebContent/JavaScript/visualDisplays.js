function setLastTransmission() {

	return function(data) {
        
		new Ajax.Updater('lastTransmission', 
			'/spring/multispeak/visualDisplays/loadManagement/currentDateTime', {
			'onSuccess': function(transport, json) {
			
				$('lastTransmitted').innerHTML = json['nowStr'];
				flashYellow($('lastTransmitted'), 3.5);
			}
		});
    };
}
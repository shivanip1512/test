function setLastTransmission() {

	return function(data) {
        
		new Ajax.Updater('lastTransmission', 
			'/multispeak/visualDisplays/loadManagement/currentDateTime', {
			'onSuccess': function(transport, json) {
			
				$('lastTransmitted').innerHTML = json['nowStr'];
				flashYellow($('lastTransmitted'), 3.5);
			}
		});
    };
}
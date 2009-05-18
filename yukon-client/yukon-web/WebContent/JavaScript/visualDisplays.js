function setLastTransmission() {

	return function(data) {
        
		new Ajax.Updater('lastTransmission', 
			'/spring/multispeak/visualDisplays/loadManagement/currentDateTime', {
			'onSuccess': function(transport, json) {
			
				$('lastTransmitted').innerHTML = json['nowStr'];
				new Effect.Highlight($('lastTransmitted'), {'duration': 3.5, 'startcolor': '#FFE900', 'restorecolor': '#FFFFFF'});
			}
		});
    };
}
function setLastTransmission() {

    return function(data) {
        jQuery.ajax({
            url: '/multispeak/visualDisplays/loadManagement/currentDateTime'
        }).done(function (data, textStatus, jqXHR) {
            var jsonData = yukon.ui.util.getHeaderJSON(jqXHR),
                lastTransmitData = jsonData['nowStr'];
            jQuery('#lastTransmitted').html(lastTransmitData);
            flashYellow(jQuery('#lastTransmitted')[0], 3.5);
        });
    };
};
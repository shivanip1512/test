function setLastTransmission() {

    return function(data) {
        $.ajax({
            url: '/multispeak/visualDisplays/loadManagement/currentDateTime'
        }).done(function (data, textStatus, jqXHR) {
            var jsonData = yukon.ui.util.getHeaderJSON(jqXHR),
                lastTransmitData = jsonData['nowStr'];
            $('#lastTransmitted').html(lastTransmitData);
            flashYellow($('#lastTransmitted')[0], 3.5);
        });
    };
};
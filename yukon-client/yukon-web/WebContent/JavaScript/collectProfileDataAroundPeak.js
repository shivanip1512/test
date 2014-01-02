// initiate profile collection
function peakDayProfile_start (divId, profileRequestOrigin) {
    var initiateComplete,
        args,
        url;

    jQuery('#' + divId + '_startButton').prop('disabled', true);

    initiateComplete = function (transport, json) {
        alert(json['returnMsg']);
        jQuery('#' + divId + '_startButton').prop('disabled', false);
    };

    args = {};
    args.deviceId = jQuery('#' + divId + '_deviceId').val();
    args.email = jQuery('#' + divId + '_email').val();
    args.peakDate = jQuery('#' + divId + '_selectedPeakDate').val();
    args.startDate = jQuery('#' + divId + '_startDate').val();
    args.stopDate = jQuery('#' + divId + '_stopDate').val();
    args.beforeDays = jQuery('#' + divId + '_beforeDays').val();
    args.afterDays = jQuery('#' + divId + '_afterDays').val();
    args.profileRequestOrigin = profileRequestOrigin;

    url = '/meter/highBill/initiateLoadProfile';
    //new Ajax.Request(url, {'method': 'post', 'evalScripts': true, 'onComplete': initiateComplete, 'onFailure': initiateComplete, 'parameters': args});
    jQuery.ajax({
        url: url,
        type: 'POST',
        data: args
    }).done(function (data, textStatus, jqXHR) {
        var jsonData = Yukon.ui.aux.getHeaderJSON(jqXHR);
        initiateComplete(jqXHR, jsonData);
    }).fail(function (jqXHR, textStatus, errorThrown) {
        var jsonData = {};
        jsonData.returnMsg = jqXHR.responseText;
        initiateComplete(jqXHR, jsonData);
    });
}


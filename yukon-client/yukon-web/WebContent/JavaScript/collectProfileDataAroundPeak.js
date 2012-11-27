// initiate profile collection
function peakDayProfile_start(divId, profileRequestOrigin) {
    
    $(divId + '_startButton').disable();
        
    var initiateComplete = function(transport, json) {
        alert(json['returnMsg']);
        $(divId + '_startButton').enable();
    };

    var args = {};
    args.deviceId = $F(divId + '_deviceId');
    args.email = $F(divId + '_email');
    args.peakDate = $F(divId + '_selectedPeakDate');
    args.startDate = $F(divId + '_startDate');
    args.stopDate = $F(divId + '_stopDate');
    args.beforeDays = $F(divId + '_beforeDays');
    args.afterDays = $F(divId + '_afterDays');
    args.profileRequestOrigin = profileRequestOrigin;
    
    var url = '/meter/highBill/initiateLoadProfile';
    new Ajax.Request(url, {'method': 'post', 'evalScripts': true, 'onComplete': initiateComplete, 'onFailure': initiateComplete, 'parameters': args});
  
}



// show/hide the profile popup display
function peakDayProfile_toggleDisplay(divId, profileRequestOrigin) {
    
  var holder = $(divId + '_holder');
  
  if (holder.visible()) {
    holder.hide();
    return;
  }
  
  holder.show();
  
  peakDayProfile_initRequestPopup(divId, profileRequestOrigin);
}
  
// get pending profile data
function peakDayProfile_initRequestPopup(divId, profileRequestOrigin) {

    var onDisplayComplete = function(transport, json) {
        $(divId + '_email').value = json.email;
        
        var listEl = $(divId + '_pendingList');
        listEl.descendants().each(function(el) {
	       $(el).remove();
        });

        if (json.pendingRequests.length > 0) {
            // fill in pending section
            json.pendingRequests.each(function(req) {
                var liEl = document.createElement('li');
                
                liEl.innerHTML = req.from + ' - ' + req.to + ' <br>Requested by: ' + req.email + ' <a href="javascript:void(0);' + " onclick=peakDayProfile_cancel('" + divId + "', " + req.requestId + ", '" + profileRequestOrigin + "');" + '">Cancel</a>';
                
                liEl.setAttribute('title', req.requestId + ": \"" + req.command + "\"");
                liEl.setAttribute('style', 'white-space:nowrap;');
                
                listEl.appendChild(liEl);
            });
            $(divId + '_pendingHolder').show();
            
        } else {
            $(divId + '_pendingHolder').hide();
        }
    };

    var onDisplayFailure = function(transport, json) {
        alert("Unable to get pending requests:\n" + transport.responseText);
    };

    var url = "/spring/loadProfile/defaults";
    var args = {};
    args.deviceId = $F(divId + '_deviceId');
    new Ajax.Request(url, {'method': 'post', 'onSuccess': onDisplayComplete, 'onFailure': onDisplayFailure, 'parameters': args});

}
 
// initiate profile collection
function peakDayProfile_start(divId, profileRequestOrigin) {

    var onStartFailure = function(transport, json) {
        //alert("There was an error starting the profile collection:\n" + transport.responseText);
        $(divId + '_startButton').enabled = true;
        $(divId + '_errors').innerHTML = transport.responseText;
    };

    var onStartComplete = function(transport, json) {
        if (json.success) {
            peakDayProfile_initRequestPopup(divId, profileRequestOrigin);
        } else {
            $(divId + '_errors').innerHTML = json.errString;
            $(divId + '_startButton').enabled = true;
        }
    };

    $(divId + '_errors').innerHTML = "";
    $(divId + '_startButton').enabled = false;
    
    var args = {};
    args.deviceId = $F(divId + '_deviceId');
    args.email = $F(divId + '_email');
    args.peakDate = $F(divId + '_peakDate');
    args.startDate = $F(divId + '_startDate');
    args.stopDate = $F(divId + '_stopDate');
    args.beforeDays = $F(divId + '_beforeDays');
    args.afterDays = $F(divId + '_afterDays');
    args.profileRequestOrigin = profileRequestOrigin;
    
    var url = '/spring/loadProfile/initiateLoadProfile';
    new Ajax.Request(url, {'method': 'post', 'onComplete': onStartComplete, 'onFailure': onStartFailure, 'parameters': args});
  
}

// cancel
function peakDayProfile_cancel(divId, requestId, profileRequestOrigin) {

    var onCancelFailure = function(transport, json) {
        alert("There was an error canceling the profile collection:\n" + transport.responseText);
    };

    var onCancelComplete = function(transport, json) {
    };
    
    var args = {};
    args.deviceId = $F(divId + '_deviceId');
    args.requestId = requestId;
      
    var url = '/spring/loadProfile/cancelLoadProfile';
    new Ajax.Request(url, {'method': 'post', 'onComplete': onCancelComplete, 'onFailure': onCancelFailure, 'parameters': args});
    
    peakDayProfile_initRequestPopup(divId, profileRequestOrigin);
}
  



function loadProfile_display(divId,profileRequestOrigin) {
  var holder = $(divId + '_holder');
  if (holder.visible()) {
    holder.hide();
    return;
  }
  $(divId + '_indicator').style.visibility = "visible";
  
  var onDisplayComplete = function(transport, json) {
    $(divId + '_indicator').style.visibility = "hidden";
    $(divId + '_holder').show();
    $(divId + '_email').value = json.email;
    $(divId + '_start').value = json.startDate;
    $(divId + '_startDisplay').innerHTML = json.startDate;
    $(divId + '_stop').value = json.stopDate;
    $(divId + '_stopDisplay').innerHTML = json.stopDate;
    $(divId + '_startButton').enabled = true;
    
    var listEl = $(divId + '_pendingList');
    listEl.descendants().each(function(el) {
      $(el).remove();
    });
    
    if (json.pendingRequests.length > 0) {
      // fill in pending section
      json.pendingRequests.each(function(req) {
        var liEl = document.createElement('li');
        liEl.innerHTML = req.from + ' - ' + req.to + ' <br>Requested by: ' + req.email + ' <a href="javascript:void(0);' + " onclick=loadProfile_cancel('" + divId + "'," + req.requestId + ",'" + profileRequestOrigin + "');" + '">Cancel</a>';
        liEl.setAttribute('title', req.requestId + ": \"" + req.command + "\"");
        listEl.appendChild(liEl);
      });
      $(divId + '_pendingHolder').show();
    } else {
      $(divId + '_pendingHolder').hide();
    }
  };
  
  var onDisplayFailure = function(transport, json) {
    $(divId + '_indicator').style.visibility = "hidden";
    alert("Unable to get defaults:\n" + transport.responseText);
  };
  
  var url = "/spring/loadProfile/defaults";
  var args = {};
  args.startOffset = $F(divId + '_startOffset');
  args.startDate = $F(divId + '_start');
  args.stopDate = $F(divId + '_stop');
  args.deviceId = $F(divId + '_deviceId');
  new Ajax.Request(url, {'method': 'post', 'onSuccess': onDisplayComplete, 'onFailure': onDisplayFailure, 'parameters': args});
}  

function loadProfile_cancel(divId,requestId,profileRequestOrigin) {

	var onCancelFailure = function(transport, json) {
    	alert("There was an error canceling the profile collection:\n" + transport.responseText);
  	};

 	var onCancelComplete = function(transport, json) {
 		
  	};
  	
	var args = {};
  	args.deviceId = $F(divId + '_deviceId');
  	args.requestId = requestId;
  
  	var holder = $(divId + '_holder');
  		if (holder.visible()) {
    	holder.hide();
   	}
  	
	var url = '/spring/loadProfile/cancelLoadProfile';
  	new Ajax.Request(url, {'method': 'post', 'onComplete': onCancelComplete, 'onFailure': onCancelFailure, 'parameters': args});
  	
  	loadProfile_display(divId,profileRequestOrigin)
}


function loadProfile_start(divId,profileRequestOrigin) {
  var onStartFailure = function(transport, json) {
    alert("There was an error starting the profile collection:\n" + transport.responseText);
    $(divId + '_startButton').enabled = true;
    $(divId + '_indicator').style.visibility = "hidden";
  };

  var onStartComplete = function(transport, json) {
    $(divId + '_indicator').style.visibility = "hidden";
    if (json.success) {
      $(divId + '_holder').hide();
    } else {
      $(divId + '_errors').innerHTML = json.errString;
      $(divId + '_startButton').enabled = true;
    }
  };

  $(divId + '_errors').innerHTML = "";
  $(divId + '_startButton').enabled = false;
  $(divId + '_indicator').style.visibility = "visible";
  var args = {};
  args.deviceId = $F(divId + '_deviceId');
  args.email = $F(divId + '_email');
  args.startDate = $F(divId + '_start');
  args.stopDate = $F(divId + '_stop');
  args.profileRequestOrigin = profileRequestOrigin;
  var url = '/spring/loadProfile/initiateLoadProfile';
  new Ajax.Request(url, {'method': 'post', 'onComplete': onStartComplete, 'onFailure': onStartFailure, 'parameters': args});
  // comment
}
/* comment */

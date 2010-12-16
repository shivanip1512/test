function createCookie(name,value) {
  var date = new Date();
  date.setTime(date.getTime()+(365*24*60*60*1000));
  var expires = "; expires="+date.toGMTString();
  document.cookie = name+"="+encodeURIComponent(value)+expires+"; path=/";
}

function readCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for(var i=0;i < ca.length;i++) {
    var c = ca[i];
    while (c.charAt(0)==' ') c = c.substring(1,c.length);
    if (c.indexOf(nameEQ) == 0) return decodeURIComponent(c.substring(nameEQ.length,c.length));
  }
  return null;
}

YukonClientPersistance = {}

// Method to persist state into a cookie
YukonClientPersistance.persistState = function(scope, persistId, value){
	    
	var clientPersistance = readCookie('yukonClientPersistance');
	
	if(clientPersistance){
		clientPersistance = clientPersistance.evalJSON();
	} else {
		clientPersistance = {};
	}
	
	clientPersistance[scope + persistId] = value;
	createCookie('yukonClientPersistance', Object.toJSON(clientPersistance));
}

// Method to get state from a cookie
YukonClientPersistance.getState = function(scope, persistId, defaultValue){
	    
	var clientPersistance = readCookie('yukonClientPersistance');
	if(clientPersistance){
		clientPersistance = clientPersistance.evalJSON();
	} else {
		return defaultValue;
	}
	var value = clientPersistance[scope + persistId];
  
    if (value) {
        return value;
    } else {
        return defaultValue;
    }
}
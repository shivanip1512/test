function boxContainerSetup(id, showInitially, persistId) {
  var doShow = function() {
    $(id + '_content').show();
    $(id + '_plusImg').hide();
    $(id + '_minusImg').show();
    
    if (persistId != '') {
	    YukonClientPersistance.persistState('', persistId, 'show');
	}
  };
  var doHide = function() {
    $(id + '_content').hide();
    $(id + '_minusImg').hide();
    $(id + '_plusImg').show();

    if (persistId != '') {
	    YukonClientPersistance.persistState('', persistId, 'hide');
	}
  };
  
  var lastState = null;
  if (persistId != '') {
    lastState = YukonClientPersistance.getState('', persistId);
  }
  
  if (lastState) {
    if (lastState == 'show') {
      doShow();
    } else {
      doHide();
    }
  } else if (showInitially) {
    doShow();
  } else {
    doHide();
  }

  $(id + '_plusImg').observe('click', function(event) {
    if ($(id + '_content').visible()) {
      doHide();
    } else {
      doShow();
    }
  });

  $(id + '_minusImg').observe('click', function(event) {
    if ($(id + '_content').visible()) {
      doHide();
    } else {
      doShow();
    }
  });
	
}


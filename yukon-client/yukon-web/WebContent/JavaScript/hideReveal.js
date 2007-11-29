function hideRevealSectionSetup(showElement, hideElement, clickableElement, section, showInitially, persistId) {
  var doShow = function() {
    $(section).show();
    $(showElement).hide();
    $(hideElement).show();
    if (persistId != '') {
      YukonClientPersistance.persistState('hideReveal', persistId, 'show');
    }
  };
  var doHide = function() {
    $(section).hide();
    $(hideElement).hide();
    $(showElement).show();
    if (persistId != '') {
      YukonClientPersistance.persistState('hideReveal', persistId, 'hide');
    }
  };
  
  var lastState = null;
  if (persistId != '') {
    lastState = YukonClientPersistance.getState('hideReveal', persistId);
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

  $(clickableElement).observe('click', function(event) {
    if ($(section).visible()) {
      doHide();
    } else {
      doShow();
    }
  });
}
function hideRevealSectionSetup(id, section, showInitially, persistId) {
  var doShow = function() {
    $(section).show();
    $(id + '_plusImg').hide();
    $(id + '_minusImg').show();
    if (persistId != '') {
      createCookie('hideReveal' + persistId,'show');
    }
  };
  var doHide = function() {
    $(section).hide();
    $(id + '_minusImg').hide();
    $(id + '_plusImg').show();
    if (persistId != '') {
      createCookie('hideReveal' + persistId,'hide');
    }
  };
  
  var lastState = null;
  if (persistId != '') {
    lastState = readCookie('hideReveal' + persistId);
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

  $(id + '_span').observe('click', function(event) {
    if ($(section).visible()) {
      doHide();
    } else {
      doShow();
    }
  });
}
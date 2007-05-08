function hideRevealSectionSetup(id, section, showInitially) {
  var doShow = function() {
    $(section).show();
    $(id + '_plusImg').hide();
    $(id + '_minusImg').show();
  };
  var doHide = function() {
    $(section).hide();
    $(id + '_minusImg').hide();
    $(id + '_plusImg').show();
  };
  
  if (showInitially) {
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
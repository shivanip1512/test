function hideRevealSectionSetup(showElement, hideElement, clickableElement, section, showInitially, persistId, slide) {
  var doShow = function(doSlide) {
    if (doSlide) {
        Effect.SlideDown(section, { duration : .4 } );
    } else {
        $(section).show();  
    }
    $(showElement).hide();
    $(hideElement).show();
    if (persistId != '') {
      YukonClientPersistance.persistState('hideReveal', persistId, 'show');
    }
  };
  var doHide = function(doSlide) {
      if (doSlide) {
          Effect.SlideUp(section, { duration : .4 } );
      } else {
          $(section).hide();
      }
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
        doShow(false);
    } else {
        doHide(false);
    }
  } else if (showInitially) {
      doShow(false);
  } else {
      doHide(false);
  }

  $(clickableElement).observe('click', function(event) {
    if ($(section).visible()) {
      doHide(slide);
    } else {
      doShow(slide);
    }
  });
}
function hideRevealSectionSetup(showElement, 
                                hideElement, 
                                clickableElement,
                                section, 
                                showInitially, 
                                persistId) {
    
    section = $(document.getElementById(section));
    showElement = $(document.getElementById(showElement));
    hideElement = $(document.getElementById(hideElement));
    
    var doShow = function(doSlide) {
        section.show();
        hideElement.show();
        showElement.hide();
        
        if (persistId != '') {
            yukon.cookie.set('hideReveal', persistId, 'show');
        }
    };
    
    var doHide = function() {
        section.hide();
        hideElement.hide();
        showElement.show();
        if (persistId != '') {
            yukon.cookie.set('hideReveal', persistId, 'hide');
        }
    };
    
    var lastState = null;
    if (persistId != '') {
        lastState = yukon.cookie.get('hideReveal', persistId);
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
    
    $(document.getElementById(clickableElement)).click(function (event) {
        if (section.is(":visible")) {
            doHide();
        } else {
            doShow();
        }
    });
}
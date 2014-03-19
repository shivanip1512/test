function hideRevealSectionSetup(showElement, 
                                hideElement, 
                                clickableElement,
                                section, 
                                showInitially, 
                                persistId, 
                                slide) {
    // cache the jQuery objects
    section = $(document.getElementById(section));
    showElement = $(document.getElementById(showElement));
    hideElement = $(document.getElementById(hideElement));

    var doShow = function(doSlide) {
        section.slideDown(doSlide, 'swing');
        showElement.hide();
        hideElement.show();

        if (persistId != '') {
            YukonClientPersistance
                    .persistState('hideReveal', persistId, 'show');
        }
    };
    
    var doHide = function(doSlide) {
        $(section).slideUp(doSlide, 'swing');
        hideElement.hide();
        showElement.show();
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
            doShow(0);
        } else {
            doHide(0);
        }
    } else if (showInitially) {
        doShow(0);
    } else {
        doHide(0);
    }

    $(document.getElementById(clickableElement)).click(function(event) {
        // convert from true/false (passed in) to a relevant numerical value
        slide = slide ? 400 : 0;
        if (section.is(":visible")) {
            doHide(slide);
        } else {
            doShow(slide);
        }
    });
}
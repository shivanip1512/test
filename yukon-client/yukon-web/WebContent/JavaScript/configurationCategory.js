jQuery(function() {
    determineDisplayItemAddButtonVisibility();
    showSandwichedSlotDisabledEntries();
    
    for (var num = 1; num < 5; num++) {
        determineScheduleAddButtonVisibility(num);
        showSandwichedMidnightEntries(num);
    }
    
    jQuery("#showNextFieldBtn").on("click", function() {
        // Show the next hidden display item.
        var hiddenElems = jQuery("div[id^='displayItem']").filter(function() { return this.classList.contains('dn'); });
        jQuery('#' + hiddenElems[0].id).removeClass('dn');
        determineDisplayItemAddButtonVisibility();
    });
    
    jQuery(".f-categories").click(function() {
        jQuery(".pipe").css('visibility', 'hidden');
        var devTypeClass = jQuery(this).attr('class').match(/f-devtype-\w*/)[0];
        jQuery('.' + devTypeClass + '.pipe').css('visibility', 'visible');
    });
    
    jQuery("#categoryPopup").on("click", function(event) {
        if (jQuery(event.target).closest('#showNextFieldBtn').length > 0) {
            // Show the next hidden display item.
            var hiddenElems = jQuery("div[id^='displayItem']").filter(function() { return this.classList.contains('dn'); });
            jQuery('#' + hiddenElems[0].id).removeClass('dn');
            var visibleElems = jQuery("div[id^='displayItem']").filter(function() { return !this.classList.contains('dn'); });
            if (visibleElems.length < 26) {
                jQuery('#showNextDiv').css('display', 'block');
            } else {
                jQuery('#showNextDiv').css('display', 'none');
            }
        }
    });

    // Find the first type and select his categories
    var pipe = jQuery(".pipe").get(0);
    if (pipe) {
        var devTypeClass = jQuery(pipe).attr('class').match(/f-devtype-\w*/)[0];
        jQuery('.' + devTypeClass + '.pipe').css('visibility', 'visible');
        jQuery(pipe).css('visibility', 'visible');
    }
});

function determineDisplayItemAddButtonVisibility() {
    var visibleElems = jQuery("div[id^='displayItem']").filter(function() { return !this.classList.contains('dn'); });
    if (visibleElems.length < 26) {
        jQuery('#showNextDiv').css('display', 'block');
    } else {
        jQuery('#showNextDiv').css('display', 'none');
    }
}

function showSandwichedMidnightEntries(num) {
    var scheduleVisibles = jQuery("td").filter(function() { 
        return this.id.match('^schedule' + num + '_time\\d+$') && this.parentElement.style.display != 'none'; 
    });
    scheduleVisibles.last().closest('tr').prevAll().show();
}

function showSandwichedSlotDisabledEntries() {
    var visibleElems = jQuery("div[id^='displayItem']").filter(function() { return !this.classList.contains('dn'); });
    visibleElems.last().closest('div').prevAll().show();
}

function determineScheduleAddButtonVisibility(num) {
    var regex = '^schedule' + num + '_time\\d+$';
    var total = jQuery("td").filter(function() { return this.id.match(regex); }).length;
    var schedule1visibles = jQuery("td").filter(function() { return this.id.match(regex) && this.parentElement.style.display != 'none'; });
    if (schedule1visibles.length < total) {
        var btn = jQuery('#showNextDivSchedule' + num);
        btn.css('display', 'block');
        jQuery('#' + schedule1visibles[schedule1visibles.length - 1].id).parent().append(btn);
    } else {
        jQuery('#showNextDivSchedule' + num).css('display', 'none');
    }
}

function showNextSchedule(num) {
    var regex = '^schedule' + num + '_time\\d+$';
    var hiddenElems = jQuery("td").filter(function() { return this.id.match(regex) && this.parentElement.style.display == 'none'; });
    jQuery('#' + hiddenElems[0].id).parent().css('display', 'table-row');
    determineScheduleAddButtonVisibility(num);
}

function makeAjaxCall(method, params, button) {
    jQuery('#' + button.attr('id')).mouseleave();
    var temp = '/deviceConfiguration/category/' + method;
    jQuery('#categoryPopup').load(temp, params, function() {
        handleVisibleElemsAndButtons();
        enableButton(button);
    });
}

function enableButton(button) {
    button.removeAttr('disabled');
    button.find('.icon-loading').css('display', 'none');
    button.find('.icon').first().css('display', 'block');
}

function changeOut(type) {
    var form = jQuery('#categoryChange_' + type);
    form.submit();
    return true;
}

function handleVisibleElemsAndButtons() {
    var hiddenElems = jQuery("div[id^='displayItem']").filter(function() { return this.classList.contains('dn'); });
    if (hiddenElems.length < 26) {
        jQuery('#showNextDiv').css('display', 'block');
    } else {
        jQuery('#showNextDiv').css('display', 'none');
    }
    
    for (var i = 1; i < 5; i++) {
        determineScheduleAddButtonVisibility(i);
    }
}
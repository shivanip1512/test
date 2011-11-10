function updateStateColorGenerator(id) {
  //assumes data is of type Hash
    return function(data) {
        var anchorTag = $(id);
        var state = data.get('value');
        var color;
        if (state.indexOf('Pending') != -1) {
            color = '#F09100';
        } else if (state.startsWith('ENABLED')) {
            color = '#3C8242';
        } else if (state.startsWith('DISABLED')) {
            color = '#FF0000';
        }
        anchorTag.style.color = color;
    };
}

function updateWarningImage(id) {
  //assumes data is of type Hash
    return function(data) {
        var alertSpan = $(id + '_alert');
        var okSpan = $(id + '_ok');
        
        var isWarning = eval(data.get('value'));
        if (isWarning) {
            okSpan.hide();
            alertSpan.show();
        } else {
            alertSpan.hide();
            okSpan.show();
        }
    }
}

function updateDualBusImage(id) {
  //assumes data is of type Hash
    return function(data) {
        var primarySpan = $(id + '_primary');
        var alternateSpan = $(id + '_alternate');
        
        var icon = data.get('value');
        
        if (icon == 'Primary') {
            primarySpan.show();
            alternateSpan.hide();
        } else if (icon == 'Alternate') {
            primarySpan.hide();
            alternateSpan.show();
        } else {
            primarySpan.hide();
            alternateSpan.hide();           
        }
    }
}

function updateCapBankWarningImage(id) {
  //assumes data is of type Hash
    return function(data) {
        var yellowSpan = $(id + '_yellow');
        var greenSpan = $(id + '_green');
        var yellowLocalSpan = $(id + '_yellow_local');
        var greenLocalSpan = $(id + '_green_local');
        
        var icon = data.get('value');
        
        if (icon == 'GreenRemote') {
            yellowSpan.hide();
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenSpan.show();
        } else if (icon == 'GreenLocal') {
            yellowSpan.hide();
            yellowLocalSpan.hide();
            greenLocalSpan.show();
            greenSpan.hide();
        } else if (icon == 'YellowRemote') {
            yellowSpan.show();
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenSpan.hide();
        } else if (icon == 'YellowLocal') {
            yellowSpan.hide();
            yellowLocalSpan.show();
            greenLocalSpan.hide();
            greenSpan.hide();
        }
    }
}

function updateRegulatorModeIndicator(id) {
  //assumes data is of type Hash
    return function(data) {
        var yellowLocalSpan = $(id + '_local_warning');
        var greenLocalSpan = $(id + '_local_normal');
        var greenNormalSpan = $(id + '_normal');
        
        var icon = data.get('value');
        
        if (icon == 'none') {
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenNormalSpan.show();
        } else if (icon == 'NormalLocal') {
            yellowLocalSpan.hide();
            greenLocalSpan.show();
            greenNormalSpan.hide();
        } else if (icon == 'WarningLocal') {
            yellowLocalSpan.show();
            greenLocalSpan.hide();
            greenNormalSpan.hide();
        }
    }
}

function updateRegulatorThreePhaseTapIndicator(zoneId, zoneType, phase) {

    var tapContainer = $('tapContainer_' + zoneId);
    var divPhaseA = tapContainer.down('.phaseA');
    var divPhaseB = tapContainer.down('.phaseB');
    var divPhaseC = tapContainer.down('.phaseC');
    
    if (zoneType == 'THREE_PHASE') {
        // assumes data is of type Hash
        return function(data) {
            
            hideAll();

            var modeA = data.get('modeA');
            var modeB = data.get('modeB');
            var modeC = data.get('modeC');
            var tapIconA = data.get('tapA');
            var tapIconB = data.get('tapB');
            var tapIconC = data.get('tapC');
            var tapTooltipA = data.get('tapTooltipA');
            var tapTooltipB = data.get('tapTooltipB');
            var tapTooltipC = data.get('tapTooltipC');
            
            setMode(modeA, divPhaseA);
            setMode(modeB, divPhaseB);
            setMode(modeC, divPhaseC);
            
            setTapIcon(tapIconA, modeA, divPhaseA, tapTooltipA);
            setTapIcon(tapIconB, modeB, divPhaseB, tapTooltipB);
            setTapIcon(tapIconC, modeC, divPhaseC, tapTooltipC);
        }
    } else if (zoneType == 'GANG_OPERATED') {
        // assumes data is of type Hash
        return function(data) {
            
            hideAll();
            
            var mode = data.get('mode');
            var icon = data.get('value');
            var tapTooltip = data.get('tapTooltip');

            setMode(mode, divPhaseA);
            setMode(mode, divPhaseB);
            setMode(mode, divPhaseC);
            
            setTapIcon(icon, mode, divPhaseA, tapTooltip);
            setTapIcon(icon, mode, divPhaseB, tapTooltip);
            setTapIcon(icon, mode, divPhaseC, tapTooltip);
        }
    } else if (zoneType == 'SINGLE_PHASE') {
        // assumes data is of type Hash
        return function(data) {
            
            hideAll();

            var mode = data.get('mode');
            var icon = data.get('value');
            var tapTooltip = data.get('tapTooltip');
            var divPhase;

            if (phase == 'A') {
                divPhase = divPhaseA;
            } else if (phase == 'B') {
                divPhase = divPhaseB;
            } else {
                divPhase = divPhaseC;
            }

            setMode(mode, divPhase);
            setTapIcon(icon, mode, divPhase, tapTooltip);
        }
    }

    function setMode(mode, div) {
        div = div.down('.lastOpRight');
        if (mode == 'NormalLocal') {
            setModeNormalLocal(div);
        } else if (mode == 'WarningLocal') {
            setModeWarningLocal(div);
        } else {
            setModeNormalRemote(div);
        }
    }
    
    function setTapIcon(tapIcon, mode, div, tapTooltip) {
        div = div.down('.lastOpLeft');
        if (tapIcon == 'RAISE_TAP') {
            if (mode == 'WarningLocal') {
                showTapRaiseWarning(div, tapTooltip);
            } else {
                showTapRaise(div, tapTooltip);
            }
        } else if (tapIcon == 'LOWER_TAP') {
            if (mode == 'WarningLocal') {
                showTapLowerWarning(div, tapTooltip);
            } else {
                showTapLower(div, tapTooltip);
            }
        } else if (tapIcon == 'LOWER_TAP_RECENT') {
            if (mode == 'WarningLocal') {
                showTapLowerRecentWarning(div, tapTooltip);
            } else {
                showTapLowerRecent(div, tapTooltip);
            }
        } else if (tapIcon == 'RAISE_TAP_RECENT') {
            if (mode == 'WarningLocal') {
                showTapRaiseRecentWarning(div, tapTooltip);
            } else {
                showTapRaiseRecent(div, tapTooltip);
            }
        } else {
            showTapDefault(div, tapTooltip);
        }
    }
    
    function showTapDefault(phase, tapTooltip) {
        var tapDiv = phase.down('.tapDefault');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaise(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaise');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaiseWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaiseWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLower(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLower');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLowerWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLowerWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLowerRecent(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLowerRecent');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapLowerRecentWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapLowerRecentWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaiseRecent(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaiseRecent');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }
    function showTapRaiseRecentWarning(phase, tapTooltip) {
        var tapDiv = phase.down('.tapRaiseRecentWarning');
        tapDiv.title = tapTooltip;
        tapDiv.show();
    }

    function setModeNormalRemote(divPhase) {
        divPhase.down('.regulatorModeRemote').show();
    }
    function setModeNormalLocal(divPhase) {
        divPhase.down('.regulatorModeLocal').show();
    }
    function setModeWarningLocal(divPhase) {
        divPhase.down('.regulatorModeLocalWarning').show();
    }

    function hideAll() {
        tapContainer.select(".lastOpLeft span, .lastOpRight span").invoke('hide');
    }
}

function updateVerificationImage(spanId) {
  //assumes data is of type Hash
    return function(data) {
        var isVerification = eval(data.get('value'));
        if (isVerification) {
            $(spanId).show();
        } else {
            $(spanId).hide();
        }
    }
}

function updateStateColorGenerator(id) {
    return function (data) {
        var anchorTag = $('#' + id)[0],
            state = data.value,
            color = '#fff';
        if (-1 !== state.indexOf('Pending')) {
            color = '#F09100';
        } else if (0 === state.indexOf('ENABLED', 0)) {
            color = '#009933';
        } else if (0 === state.indexOf('DISABLED', 0)) {
            color = '#D14836';
        }
        anchorTag.style.color = color;
        anchorTag.style.backgroundColor = color;
    };
}

function updateWarningImage(id) {
    return function (data) {
        var alertSpan = $('#' + id + '_alert'),
            okSpan = $('#' + id + '_ok'),
            isWarning = 'false' === data.value ? false : true;
        if (isWarning) {
            okSpan.hide();
            alertSpan.show();
        } else {
            alertSpan.hide();
            okSpan.show();
        }
    };
}

function updateDualBusImage(id) {
    return function (data) {
        var primarySpan = $('#' + id + '_primary'),
            alternateSpan = $('#' + id + '_alternate'),
            icon;

        icon = data.value;
        if (icon === 'Primary') {
            primarySpan.show();
            alternateSpan.hide();
        } else if (icon === 'Alternate') {
            primarySpan.hide();
            alternateSpan.show();
        } else {
            primarySpan.hide();
            alternateSpan.hide();
        }
    };
}

function feederDualBus() {
    return function (data) {
        var feederInfo = JSON.parse(data.value),
            icon = jQuery('[data-dual-bus-feeder='+ feederInfo.paoId + ']');
        if (feederInfo.dualBus) {
            icon.show();
        } else {
            icon.hide();
        }
    };
}

function updateCapBankWarningImage(id) {
    return function (data) {
        var yellowSpan = $('#' + id + '_yellow'),
            greenSpan = $('#' + id + '_green'),
            icon = data.value;

        if (icon == 'Green') {
            yellowSpan.hide();
            greenSpan.show();
        } else if (icon == 'Yellow') {
            yellowSpan.show();
            greenSpan.hide();
        }
    };
}

function updateRegulatorModeIndicator(id) {
    return function (data) {
        var yellowLocalSpan = $('#' + id + '_local_warning'),
            greenLocalSpan = $('#' + id + '_local_normal'),
            greenNormalSpan = $('#' + id + '_normal'),
            icon;

        icon = data.value;
        if (icon === 'none') {
            yellowLocalSpan.hide();
            greenLocalSpan.hide();
            greenNormalSpan.show();
        } else if (icon === 'NormalLocal') {
            yellowLocalSpan.hide();
            greenLocalSpan.show();
            greenNormalSpan.hide();
        } else if (icon === 'WarningLocal') {
            yellowLocalSpan.show();
            greenLocalSpan.hide();
            greenNormalSpan.hide();
        }
    };
}

function updateRegulatorThreePhaseTapIndicator(zoneId, zoneType, phase) {

    var tapContainer = $('#' + 'tapContainer_' + zoneId),
        divPhaseA = tapContainer.find('.phaseA')[0],
        divPhaseB = tapContainer.find('.phaseB')[0],
        divPhaseC = tapContainer.find('.phaseC')[0];
    
    if (zoneType === 'THREE_PHASE') {
        return function (data) {

            var modeA = data.modeA,
                modeB = data.modeB,
                modeC = data.modeC,
                tapIconA = data.tapA,
                tapIconB = data.tapB,
                tapIconC = data.tapC,
                tapTooltipA = data.tapTooltipA,
                tapTooltipB = data.tapTooltipB,
                tapTooltipC = data.tapTooltipC;

            hideAll();
            setMode(modeA, divPhaseA);
            setMode(modeB, divPhaseB);
            setMode(modeC, divPhaseC);

            setTapIcon(tapIconA, modeA, divPhaseA, tapTooltipA);
            setTapIcon(tapIconB, modeB, divPhaseB, tapTooltipB);
            setTapIcon(tapIconC, modeC, divPhaseC, tapTooltipC);
        };
    } else if (zoneType === 'GANG_OPERATED') {
        return function (data) {
            
            var mode = data.mode,
               icon = data.value,
               tapTooltip = data.tapTooltip;

            hideAll();
            setMode(mode, divPhaseA);
            setMode(mode, divPhaseB);
            setMode(mode, divPhaseC);
            
            setTapIcon(icon, mode, divPhaseA, tapTooltip);
            setTapIcon(icon, mode, divPhaseB, tapTooltip);
            setTapIcon(icon, mode, divPhaseC, tapTooltip);
        };
    } else if (zoneType === 'SINGLE_PHASE') {
        return function (data) {

            var mode = data.mode,
                icon = data.value,
                tapTooltip = data.tapTooltip,
                divPhase;

            hideAll();
            if (phase === 'A') {
                divPhase = divPhaseA;
            } else if (phase === 'B') {
                divPhase = divPhaseB;
            } else {
                divPhase = divPhaseC;
            }

            setMode(mode, divPhase);
            setTapIcon(icon, mode, divPhase, tapTooltip);
        };
    }

    function setMode (mode, div) {
        div = $(div).find('.lastOpRight');
        if (mode === 'NormalLocal') {
            setModeNormalLocal(div);
        } else if (mode === 'WarningLocal') {
            setModeWarningLocal(div);
        } else {
            setModeNormalRemote(div);
        }
    }

    function setTapIcon (tapIcon, mode, div, tapTooltip) {
        div = $(div).find('.lastOpLeft');
        if (tapIcon === 'RAISE_TAP') {
            if (mode === 'WarningLocal') {
                showTapRaiseWarning(div, tapTooltip);
            } else {
                showTapRaise(div, tapTooltip);
            }
        } else if (tapIcon === 'LOWER_TAP') {
            if (mode === 'WarningLocal') {
                showTapLowerWarning(div, tapTooltip);
            } else {
                showTapLower(div, tapTooltip);
            }
        } else if (tapIcon === 'LOWER_TAP_RECENT') {
            if (mode === 'WarningLocal') {
                showTapLowerRecentWarning(div, tapTooltip);
            } else {
                showTapLowerRecent(div, tapTooltip);
            }
        } else if (tapIcon === 'RAISE_TAP_RECENT') {
            if (mode === 'WarningLocal') {
                showTapRaiseRecentWarning(div, tapTooltip);
            } else {
                showTapRaiseRecent(div, tapTooltip);
            }
        } else {
            showTapDefault(div, tapTooltip);
        }
    }
    
    function modifyTap (tapDiv, tapTooltip) {
        if (0 < tapDiv.length) {
            tapDiv.attr('title', tapTooltip);
            tapDiv.show();
        }
    }
    function showTapDefault(phase, tapTooltip) {
        modifyTap(phase.find('.tapDefault'), tapTooltip);
    }
    function showTapRaise(phase, tapTooltip) {
        modifyTap(phase.find('.tapRaise'), tapTooltip);
    }
    function showTapRaiseWarning(phase, tapTooltip) {
        modifyTap(phase.find('.tapRaiseWarning'), tapTooltip);
    }
    function showTapLower(phase, tapTooltip) {
        modifyTap(phase.find('.tapLower'), tapTooltip);
    }
    function showTapLowerWarning(phase, tapTooltip) {
        modifyTap(phase.find('.tapLowerWarning'), tapTooltip);
    }
    function showTapLowerRecent(phase, tapTooltip) {
        modifyTap(phase.find('.tapLowerRecent'), tapTooltip);
    }
    function showTapLowerRecentWarning(phase, tapTooltip) {
        modifyTap(phase.find('.tapLowerRecentWarning'), tapTooltip);
    }
    function showTapRaiseRecent(phase, tapTooltip) {
        modifyTap(phase.find('.tapRaiseRecent'), tapTooltip);
    }
    function showTapRaiseRecentWarning(phase, tapTooltip) {
        modifyTap(phase.find('.tapRaiseRecentWarning'), tapTooltip);
    }

    function setModeNormalRemote(divPhase) {
        divPhase.find('.regulatorModeRemote').show();
    }
    function setModeNormalLocal(divPhase) {
        divPhase.find('.regulatorModeLocal').show();
    }
    function setModeWarningLocal(divPhase) {
        divPhase.find('.regulatorModeLocalWarning').show();
    }

    function hideAll() {
        var jtap = $(tapContainer, '.lastOpLeft span, .lastOpRight span');
        jtap.hide();
    }
}

function updateVerificationImage(paoId) {
    return function (data) {
        var isVerification = 'false' === data.value ? false : true,
            icon = $('[data-verification][data-pao-id=' + paoId + ']');
            
        if (isVerification) {
            icon.show();
        } else {
            icon.hide();
        }
    };
}
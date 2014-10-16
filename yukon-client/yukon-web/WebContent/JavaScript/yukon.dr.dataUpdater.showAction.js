yukon.namespace('yukon.dr.dataUpdater.showAction');

yukon.dr.dataUpdater.showAction = (function () {
    var 
    _updateMenu = function(paoId, menu) {
        var actionsEl = $('[data-pao-id='+paoId+']');
        for (var action in menu) {
            actionsEl.filter('[data-' + action + '-action]').hide();
            actionsEl.filter('[data-' + action + '-action="' + menu[action].state + '"]')
                .show().attr('title', menu[action].title);
        }
    },

    // creates and returns a 'menu' defaulting every action to 'on'
    _getDefaultMenuForActions = function(actions) {
        var menu = {};
        var numActions = actions.length;
        for (var i = 0; i < numActions; i++) {
            menu[actions[i]] = {state : 'on'};
        }
        return menu;
    },
    // Updates and returns a control area 'menu' for enableDisable actions
    _updateMenuForEnableDisableActions = function (menu,controlAreaState) {
    	menu['enable-control-area'].state = 'off';
        menu['enable-control-area'].title = controlAreaState.noEnableDisableMsg;
        menu['disable-control-area'].state = 'off';
        menu['disable-control-area'].title = controlAreaState.noEnableDisableMsg;
        menu['enable-programs'].state = 'off';
        menu['enable-programs'].title = controlAreaState.noEnableDisableMsg;
        menu['disable-programs'].state = 'off';
        menu['disable-programs'].title = controlAreaState.noEnableDisableMsg;
        return menu;
    }, 
    mod = {
        // handles enabling and disabling control area menu items
        updateControlAreaMenu: function (paoId,isGearChangeEnabled,enableDisableProgramsAllowed) {
            return function(data) {
                var controlAreaState = JSON.parse(data.state);
                var menu = _getDefaultMenuForActions(['start', 'stop', 'change-triggers',
                    'change-time', 'change-triggers', 'enable-control-area','disable-control-area','change-gears', 
                    'enable-programs', 'disable-programs', 'reset-peak']);

                if (controlAreaState.unknown) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = controlAreaState.unknownMsg;
                    }
                } else if (controlAreaState.noAssignedPrograms) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = controlAreaState.noAssignedProgramsMsg;
                    }
                } else {
                    // if the control area is partially active we leave both 'start' and 'stop' actions enabled
                    if (controlAreaState.fullyActive) {
                    	if (!isGearChangeEnabled) {
                    		menu['change-gears'].state = 'off';
                            menu['change-gears'].title = controlAreaState.noChangeGearsMsg;
                          
                    	}
                    	if (!enableDisableProgramsAllowed) {
                    		menu=_updateMenuForEnableDisableActions(menu,controlAreaState);
                    	}   
                        menu['start'].state = 'off';
                        menu['start'].title = controlAreaState.fullyActiveMsg;
                    } else if(controlAreaState.inactive) {
                        menu['stop'].state = 'off';
                        menu['stop'].title = controlAreaState.inactiveMsg;
                        menu['change-gears'].state = 'off';
                        menu['change-gears'].title = controlAreaState.inactiveMsg;
                        if (!isGearChangeEnabled) {
                    		menu['change-gears'].state = 'off';
                            menu['change-gears'].title = controlAreaState.noChangeGearsMsg;
                          
                    	}
                        if (!enableDisableProgramsAllowed) {
                        	menu=_updateMenuForEnableDisableActions(menu,controlAreaState);
                    	}   
                    } else {
                    	if (!isGearChangeEnabled) {
                    		menu['change-gears'].state = 'off';
                            menu['change-gears'].title = controlAreaState.noChangeGearsMsg;
                    	}
                    	if (!enableDisableProgramsAllowed) {
                    		menu=_updateMenuForEnableDisableActions(menu,controlAreaState);
                    	}   
                    }
                    if (controlAreaState.disabled) {
                        menu['disable-control-area'].state = 'disable-off';
                    } else {
                        menu['enable-control-area'].state = 'disable-on';
                    }
                }
                
                _updateMenu(paoId, menu);
            };
        },

        // DONE
         updateProgramMenu: function (paoId,isGearChangeEnabled,enableDisableProgramsAllowed) {
            return function(data) {
                var programState = JSON.parse(data.state);
                
                var menu = _getDefaultMenuForActions(['start', 'stop', 'change-gears', 
                                                                'enable-program','disable-program']);

                if (programState.unknown) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = programState.unknownMsg;
                    }
                } else {
                    if (programState.running) {
                    	if (!isGearChangeEnabled) {
                    		menu['change-gears'].state = 'off';
                            menu['change-gears'].title = programState.noChangeGearsMsg;
                    	} 
                    	if (!enableDisableProgramsAllowed) {
                       	 menu['enable-program'].state = 'off';
                         menu['enable-program'].title = programState.noEnableDisableMsg;
                         menu['disable-program'].state = 'off';
                         menu['disable-program'].title = programState.noEnableDisableMsg;
                       	} 
                        menu['start'].state = 'off';
                        menu['start'].title = programState.alreadyRunningMsg;
                    } else {
                        menu['change-gears'].state = 'off';
                        menu['change-gears'].title = programState.changeGearsDisabledMsg;
                        if (!programState.scheduled) {
                            // Not running but only turn off 'stop' action if we arn't scheduled
                            menu['stop'].state = 'off';
                            menu['stop'].title = programState.notRunningMsg;
                        }
                        if (!enableDisableProgramsAllowed) {
                        	menu['enable-program'].state = 'off';
                            menu['enable-program'].title = programState.noEnableDisableMsg;
                            menu['disable-program'].state = 'off';
                            menu['disable-program'].title = programState.noEnableDisableMsg;
                       	}
                    }
                    if (programState.disabled) {
                    	 menu['disable-program'].state = 'disable-off';
                    } else {
                    	 menu['enable-program'].state = 'enable-off';
                    }
                }
                _updateMenu(paoId, menu);
            };
         },
    };
    return mod;
}());

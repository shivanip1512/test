yukon.namespace('yukon.dr.dataUpdater.showAction');

yukon.dr.dataUpdater.showAction = (function () {
    var 
    _updateMenu = function(paoId, menu) {
        var actionsEl = jQuery('[data-pao-id='+paoId+']');
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
    
    mod = {
        // handles enabling and disabling control area menu items
        updateControlAreaMenu: function(paoId) {
            return function(data) {
                var controlAreaState = JSON.parse(data.state);

                var menu = _getDefaultMenuForActions(['start', 'stop', 'change-triggers',
                    'change-time', 'change-triggers', 'enable-control-area', 'change-gears', 
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
                        menu['start'].state = 'off';
                        menu['start'].title = controlAreaState.fullyActiveMsg;
                    } else if(controlAreaState.inactive) {
                        menu['stop'].state = 'off';
                        menu['stop'].title = controlAreaState.inactiveMsg;
                        menu['change-gears'].state = 'off';
                        menu['change-gears'].title = controlAreaState.inactiveMsg;
                    }
                    if(controlAreaState.disabled) {
                        menu['enable-control-area'].state = 'enable-on';
                    } else {
                        menu['enable-control-area'].state = 'disable-on';
                    }
                }
                
                _updateMenu(paoId, menu);
            };
        },

        // DONE
         updateProgramMenu: function (paoId) {
            return function(data) {
                var programState = JSON.parse(data.state);
                var menu = _getDefaultMenuForActions(['start', 'stop', 'change-gears', 
                                                                'enable-program']);

                if (programState.unknown) {
                    for (var action in menu) {
                        menu[action].state = 'off';
                        menu[action].title = programState.unknownMsg;
                    }
                } else {
                    if (programState.running) {
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
                    }
                    if(programState.disabled) {
                        menu['enable-program'].state = 'enable-on';
                    } else {
                        menu['enable-program'].state = 'disable-on';
                    }
                }
                _updateMenu(paoId, menu);
            };
         },
    };
    return mod;
}());

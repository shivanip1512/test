yukon.namespace('yukon.da.updaters');

/**
 * Functions for cti:dataUpdaterCallback in the Volt/Var module
 *
 * @requires JQUERY
 */

yukon.da.updaters = (function () {

    var _updateOperations = function (opsContainer, lastOp, mode, tooltip) {
            var tapIcons = opsContainer.find('[data-last-tap]');
            mode = JSON.parse(mode);

            opsContainer.children().addClass('dn');

            if (mode.warning) {
                opsContainer.find('[data-regulator-mode="warning"]').removeClass('dn');
                tapIcons = tapIcons.filter('[data-tap-warning]');
            } else {
                if (mode.local) {
                    opsContainer.find('[data-regulator-mode="local"]').removeClass('dn');
                } else {
                    opsContainer.find('[data-regulator-mode="normal"]').removeClass('dn');
                }
                tapIcons = tapIcons.filter(':not([data-tap-warning])');
            }

            tapIcons.filter('[data-last-tap="' + lastOp + '"]').attr('title', tooltip).removeClass('dn');
        },

        mod = {
            stateColor: function (data) {
                var result = JSON.parse(data.value),
                    box = $('.js-cc-state-updater[data-pao-id="' + result.paoId + '"]'),
                    color = '#d14836';

                if (result.value.pending) {
                    color = '#f09100';
                } else if (result.value.enabled) {
                    color = '#009933';
                }

                box.css({
                    'color': color,
                    'background-color': color
                });
            },

            warningIcon: function (data) {
                var result = JSON.parse(data.value),
                    icon = $('.js-warning-image[data-pao-id="' + result.paoId + '"]');
                icon.toggleClass('dn', !result.value);
            },

            dualBus: function (data) {
                var result = JSON.parse(data.value),
                    icon = $('.js-dual-bus[data-pao-id="' + result.paoId  + '"]');
                icon.toggleClass('dn', !result.value);
            },

            feederDualBus: function (data) {
                var result = JSON.parse(data.value),
                    icon = $('[data-dual-bus-feeder=' + result.paoId + ']');
                icon.toggleClass('dn', !result.value);
            },

            verification: function (data) {
                var result = JSON.parse(data.value),
                    icon = $('.js-verification[data-pao-id="' + result.paoId  + '"]');
                icon.toggleClass('dn', !result.value);
            },

            regulatorMode: function (data) {
                var result = JSON.parse(data.value),
                    icons = $('[data-regulator-mode][data-pao-id="' + result.paoId  + '"]');

                icons.addClass('dn');
                if (result.local) {
                    icons.filter('[data-regulator-mode="local"]').removeClass('dn');
                } else if (result.warning) {
                    icons.filter('[data-regulator-mode="warning"]').removeClass('dn');
                } else {
                    icons.filter('[data-regulator-mode="normal"]').removeClass('dn');
                }
            },

            regulatorTap: function (zoneId, zoneType, phase) {

                var tapContainer = $('.js-tap-container[data-zone-id="' + zoneId  + '"]');

                if (zoneType === 'THREE_PHASE') {
                    return function (data) {

                        _updateOperations(tapContainer.find('.js-phase-A'), data.tapA, data.modeA, data.tapTooltipA);
                        _updateOperations(tapContainer.find('.js-phase-B'), data.tapB, data.modeB, data.tapTooltipB);
                        _updateOperations(tapContainer.find('.js-phase-C'), data.tapC, data.modeC, data.tapTooltipC);
                    };
                }
                if (zoneType === 'GANG_OPERATED') {
                    return function (data) {

                        _updateOperations(tapContainer, data.tap, data.mode, data.tapTooltip);
                    };
                }
                if (zoneType === 'SINGLE_PHASE') {
                    return function (data) {

                        _updateOperations(tapContainer.find('.js-phase-' + phase), data.tap, data.mode, data.tapTooltip);
                    };
                }
            },
            
            dmvTestRunning: function (data) {
                var result = JSON.parse(data.value),
                    icon = $('.js-dmvTestRunning[data-pao-id="' + result.paoId  + '"]');
                icon.toggleClass('dn', !result.value);
            }
        };

    return mod;
}());
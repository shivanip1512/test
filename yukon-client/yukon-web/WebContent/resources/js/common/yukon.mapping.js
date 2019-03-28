yukon.namespace('yukon.mapping');
 
/**
 * Module to handle common mapping functionality
 * 
 * @module yukon.mapping
 * @requires yukon
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.mapping = (function () {
    
    'use strict';
 
    var
    _initialized = false,
    
    //Line Color depends on ETX Band 1 - #006622(GREEN), 2 - #669900(LIGHT GREEN), 3 - #CCA300(YELLOW), 4 - #FF6600(ORANGE), 5 and up - #FF0000(RED)
    _neighborColors = ['#006622', '#669900', '#CCA300', '#FF6600', '#FF0000'],  
    
    /** @type {Object.<string, {ol.style.Style}>} - A cache of styles to avoid creating lots of objects using lots of memory. */
    _styles = { 
        'METER_ELECTRIC': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-elec-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_PLC_ELECTRIC': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-plc-elec-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_WATER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-water-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_GAS': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-gas-grey.png'), anchor: [0.5, 1.0] }) }),
        'TRANSMITTER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter-grey.png'), anchor: [0.5, 1.0] }) }),
        'RELAY': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-relay-grey.png'), anchor: [0.5, 1.0] }) }),
        'LCR' : new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-lcr-grey.png'), anchor: [0.5, 1.0] }) }),
        'PLC_LCR' : new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-plc-lcr-grey.png'), anchor: [0.5, 1.0] }) }),
        'THERMOSTAT' : new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-thermostat-grey.png'), anchor: [0.5, 1.0] }) }),
        'GENERIC_GREY': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-generic.png'), anchor: [0.5, 1.0] }) }),
    },
    
    /** @type {Array.<{ol.Layer.Tile|ol.layer.Group}>} - Array of tile layers for our map. */
    _tiles = [ 
          new ol.layer.Tile({ name: 'mqosm',
              source: new ol.source.XYZ({ name: 'mqosm',
                  url: yg.map_devices_street_url,
                  attributions: [new ol.Attribution({
                      html: "© <a href='https://www.mapbox.com/about/maps/'>Mapbox</a> © <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a>"
                    })]
              })
          }),
          new ol.layer.Tile({ name: 'mqsat', visible: false,
              source: new ol.source.XYZ({ name: 'mqsat', 
                url: yg.map_devices_satellite_url,
                attributions: [new ol.Attribution({
                    html: "© <a href='https://www.mapbox.com/about/maps/'>Mapbox</a> © <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a>"
                  })]
              })
          }),
          new ol.layer.Tile({ name: 'hybrid', visible: false,
              source: new ol.source.XYZ({ name: 'hybrid', 
                url: yg.map_devices_hybrid_url,
                attributions: [new ol.Attribution({
                    html: "© <a href='https://www.mapbox.com/about/maps/'>Mapbox</a> © <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a>"
                  })]
              })
          })
    ],
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            _initialized = true;
        },
        
        getStyles: function() {
            return _styles;
        },
        
        getTiles: function() {
            return _tiles;
        },
        
        displayCommonPopupProperties: function(pao) {
            $('.js-device-display').toggleClass('dn', pao.device.name === null);
            if (pao.deviceDetailUrl != null) {
                var nameSpan = $('<span />'),
                    deviceLink = '<a href="' + yukon.url(pao.deviceDetailUrl) + '" target=_blank>' + nameSpan.text(pao.device.name).html() + '</a>',
                    actionsDiv = $('#actionsDiv').clone().removeClass('dn');
                actionsDiv.find('.js-device-neighbors, .js-device-route, .js-device-map').attr('data-device-id', pao.device.paoIdentifier.paoId);
                actionsDiv.find('.js-view-all-notes').attr('data-pao-id', pao.device.paoIdentifier.paoId);
                yukon.tools.paonotespopup.hideShowNotesIcons(pao.device.paoIdentifier.paoId);
                if (pao.device.paoIdentifier.paoType === 'RFN_GATEWAY' || pao.device.paoIdentifier.paoType === 'GWY800') {
                    actionsDiv.find('.js-device-route').addClass('dn');
                }
                $('.js-device').html(deviceLink + actionsDiv[0].outerHTML);
            } else {
                $('.js-device').text(pao.device.name);
            }
            $('.js-meter-number-display').toggleClass('dn', pao.meterNumber === null);
            $('.js-meter-number').text(pao.meterNumber);
            $('.js-type-display').toggleClass('dn', pao.device.paoIdentifier.paoType === null);
            $('.js-type').text(pao.device.paoIdentifier.paoType);
            $('.js-status-display').toggleClass('dn', pao.statusDisplay === null);
            $('.js-status').text(pao.statusDisplay);
            mod.updateDeviceStatusClass(pao.statusDisplay);
            $('.js-primary-gateway-display').toggleClass('dn', pao.primaryGateway === null);
            if (pao.primaryGatewayUrl != null) {
                var nameSpan = $('<span />');
                nameSpan.text(pao.primaryGateway);
                $('.js-primary-gateway').html('<a href="' + yukon.url(pao.primaryGatewayUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
            } else {
                $('.js-primary-gateway').text(pao.primaryGateway);
            }
            //display NM error if applicable
            $('.js-nm-error-text').text(pao.errorMsg);
            $('.js-nm-error').toggleClass('dn', pao.errorMsg === null);
        },
        
        displayNeighborPopupProperties: function(neighbor) {
            var neighborData = neighbor.data;
            $('.js-node-sn-display').toggleClass('dn', (neighborData.serialNumber === null || neighbor.gatewayType));
            $('.js-node-sn').text(neighborData.serialNumber);
            $('.js-serial-number-display').toggleClass('dn', (neighborData.rfnIdentifier.sensorSerialNumber === null || neighbor.gatewayType));
            $('.js-serial-number').text(neighborData.rfnIdentifier.sensorSerialNumber);
            $('.js-gateway-serial-number-display').toggleClass('dn', (neighborData.rfnIdentifier.sensorSerialNumber === null || !neighbor.gatewayType));
            $('.js-gateway-serial-number').text(neighborData.rfnIdentifier.sensorSerialNumber);
            $('.js-ip-address-display').toggleClass('dn', neighbor.ipAddress === null);
            $('.js-ip-address').text(neighbor.ipAddress);
            $('.js-address-display').toggleClass('dn', neighborData.neighborAddress === null);
            $('.js-address').text(neighborData.neighborAddress);
            $('.js-flags-display').toggleClass('dn', neighbor.commaDelimitedNeighborFlags === null);
            $('.js-flags').text(neighbor.commaDelimitedNeighborFlags);
            $('.js-link-cost-display').toggleClass('dn', neighborData.neighborLinkCost === null);
            $('.js-link-cost').text(neighborData.neighborLinkCost);
            $('.js-num-samples-display').toggleClass('dn', neighborData.numSamples === null);
            $('.js-num-samples').text(neighborData.numSamples);
            $('.js-etx-band-display').toggleClass('dn', neighborData.etxBand === null);
            $('.js-etx-band').text(neighborData.etxBand);
            $('.js-distance-display').toggleClass('dn', neighbor.distanceDisplay === null);
            $('.js-distance').text(neighbor.distanceDisplay);
            $('#parent-info').hide();
            $('#device-info').hide();
            $('#route-info').hide();
            $('#neighbor-info').show();
            $('#marker-info').show();
        },
        
        displayPrimaryRoutePopupProperties: function(routeInfo) {
            $('.js-node-sn-display').toggleClass('dn', (routeInfo.route.serialNumber === null || routeInfo.gatewayType));
            $('.js-node-sn').text(routeInfo.route.serialNumber);
            $('.js-serial-number-display').toggleClass('dn', (routeInfo.route.rfnIdentifier.sensorSerialNumber === null || routeInfo.gatewayType));
            $('.js-serial-number').text(routeInfo.route.rfnIdentifier.sensorSerialNumber);
            $('.js-gateway-serial-number-display').toggleClass('dn', (routeInfo.route.rfnIdentifier.sensorSerialNumber === null || !routeInfo.gatewayType));
            $('.js-gateway-serial-number').text(routeInfo.route.rfnIdentifier.sensorSerialNumber);
            $('.js-ip-address-display').toggleClass('dn', routeInfo.ipAddress === null);
            $('.js-ip-address').text(routeInfo.ipAddress);
            $('.js-address-display').toggleClass('dn', routeInfo.macAddress === null);
            $('.js-address').text(routeInfo.macAddress);
            $('.js-destination-address-display').toggleClass('dn', routeInfo.route.destinationAddress === null);
            $('.js-destination-address').text(routeInfo.route.destinationAddress);
            $('.js-next-hop-address-display').toggleClass('dn', routeInfo.route.nextHopAddress === null);
            $('.js-next-hop-address').text(routeInfo.route.nextHopAddress);
            $('.js-total-cost-display').toggleClass('dn', routeInfo.route.totalCost === null);
            $('.js-total-cost').text(routeInfo.route.totalCost);
            $('.js-hop-count-display').toggleClass('dn', routeInfo.route.hopCount === null);
            $('.js-hop-count').text(routeInfo.route.hopCount);
            $('.js-route-flag-display').toggleClass('dn', routeInfo.commaDelimitedRouteFlags === null);
            $('.js-route-flag').text(routeInfo.commaDelimitedRouteFlags);
            $('.js-distance-display').toggleClass('dn', routeInfo.distanceInMiles === 0);
            $('.js-distance').text(routeInfo.distanceDisplay);
            $('#parent-info').hide();
            $('#neighbor-info').hide();
            $('#device-info').hide();
            $('#route-info').show();
            $('#marker-info').show();
        },
        
        getNeighborLineColor: function(etxBand) {
            //Line Color is based on ETX Band - see colors above
            var lineColor = _neighborColors[4];
            switch(etxBand) {
                case 1:
                    lineColor = _neighborColors[0];
                    break;
                case 2:
                    lineColor = _neighborColors[1];
                    break;
                case 3:
                    lineColor = _neighborColors[2];
                    break;
                case 4:
                    lineColor = _neighborColors[3];
                    break;
                default:
                    lineColor = _neighborColors[4];
            }
            return lineColor;
        },
        
        getNeighborLineThickness: function(numberSamples) {
            //Line thickness depends on Number of Samples 0-50 - 1px, 51-500 - 2px, 500 and up - 3px
            var lineThickness = 1;
            if (numberSamples > 50 && numberSamples <= 500) {
                lineThickness = 2;
            } else if (numberSamples > 500) {
                lineThickness = 3;
            }
            return lineThickness;
        },
        
        displayNeighborsLegend: function() {
            var borderDetails = "2px solid ";
            $('.js-etx-1').css({backgroundColor: _neighborColors[0], borderTop: borderDetails + _neighborColors[0]});
            $('.js-etx-2').css({backgroundColor: _neighborColors[1], borderTop: borderDetails + _neighborColors[1]});
            $('.js-etx-3').css({backgroundColor: _neighborColors[2], borderTop: borderDetails + _neighborColors[2]});
            $('.js-etx-4').css({backgroundColor: _neighborColors[3], borderTop: borderDetails + _neighborColors[3]});
            $('.js-etx-5').css({backgroundColor: _neighborColors[4], borderTop: borderDetails + _neighborColors[4]});
            $('.js-legend-neighbors').show();
        },
        
        hideNeighborsLegend: function() {
            $('.js-legend-neighbors').hide();
        },
        
        updateDeviceStatusClass: function(deviceStatus) {
            var statusClass;
            $('.js-status').removeClass("success");
            $('.js-status').removeClass("error");
            $('.js-status').removeClass("warning");
            switch(deviceStatus) {
                case 'Ready':
                    statusClass = "success";
                    break;
                case 'Not Ready':
                    statusClass = "error";
                    break;
                case 'Unknown':
                    statusClass = "warning";
                    break;
                default :
                    break;
            }
            $('.js-status').addClass(statusClass);
        }

    };
 
    return mod;
})();
 
$(function () { yukon.mapping.init(); });
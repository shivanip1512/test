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
    _updateNetworkTreeInterval,
    
    //Line Color depends on ETX Band 1 - #006622(GREEN), 2 - #669900(LIGHT GREEN), 3 - #CCA300(YELLOW), 4 - #FF6600(ORANGE), 5 and up - #FF0000(RED)
    _neighborColors = ['#006622', '#669900', '#CCA300', '#FF6600', '#FF0000'],  
    //dark blue
    _routeColor = "#0000CC",
    
    //lines should go beneath icons
    _lineLayerIndex = 3,
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    _srcProjection = 'EPSG:4326',
    
    //Gateways should always display on top, followed by relays and finally meters
    _iconZIndex = 1,
    _relayZIndex = 2,
    _gatewayZIndex = 3,
    
    //gateways should be the largest icons, then relays, then meters
    _deviceScale = 0.8,
    _relayScale = 0.9,
    _gatewayScale = 1,
    _largerScale = 1.1,
    
    _anchor = [0.5, 1.0],

    _elevationLayer,
    _allGatewayIcons = [],
    _allRelayIcons = [],
    _allRoutesIcons = [],
    _allRoutesLines = [],
    _allRoutesLineFeatures = [],
    _allRoutesDashedLineFeatures = [],
    
    _setRouteLastUpdatedDateTime = function (dateTimeInstant) {
        if (dateTimeInstant == null) {
            $("#js-route-details-container").find(".js-last-update-date-time").text($(".js-loading-text").val());
        } else {
            $("#js-route-details-container").find(".js-last-update-date-time").text(" " + moment(dateTimeInstant).tz(yg.timezone).format(yg.formats.date.both_with_ampm));
        }
    },

    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    /** @type {Object.<string, {ol.style.Style}>} - A cache of styles to avoid creating lots of objects using lots of memory. */
    _styles = { 
        'METER_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-elec-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_PLC_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-plc-elec-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_WIFI_ELECTRIC': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-wifi-grey.png'), 
                scale: _relayScale, 
                anchor: _anchor }), 
            zIndex: _relayZIndex }),
        'METER_WATER': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-water-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'METER_GAS': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-meter-gas-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'TRANSMITTER': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter-grey.png'), 
                scale: _gatewayScale, 
                anchor: _anchor }), 
            zIndex: _gatewayZIndex }),
        'RELAY': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-relay-grey.png'), 
                scale: _relayScale, 
                anchor: _anchor }), 
            zIndex: _relayZIndex }),
        'LCR' : new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-lcr-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'PLC_LCR' : new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-plc-lcr-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'THERMOSTAT' : new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-thermostat-grey.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
        'GENERIC_GREY': new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-generic.png'), 
                scale: _deviceScale, 
                anchor: _anchor }), 
            zIndex: _iconZIndex }),
    },
    
    _attributionText = "© <a href='https://www.mapbox.com/about/maps/'>Mapbox</a> © <a href='http://www.openstreetmap.org/copyright'>OpenStreetMap</a>",
    
    /** @type {Array.<{ol.Layer.Tile|ol.layer.Group}>} - Array of tile layers for our map. */
    _tiles = [ 
          new ol.layer.Tile({ name: 'mqosm',
              source: new ol.source.XYZ({ name: 'mqosm',
                  url: yg.map_devices_street_url,
                  attributions: _attributionText,
                  tileSize: 512
              })
          }),
          new ol.layer.Tile({ name: 'mqsat', visible: false,
              source: new ol.source.XYZ({ name: 'mqsat', 
                url: yg.map_devices_satellite_url,
                attributions: _attributionText,
                tileSize: 512
              })
          }),
          new ol.layer.Tile({ name: 'hybrid', visible: false,
              source: new ol.source.XYZ({ name: 'hybrid', 
                url: yg.map_devices_hybrid_url,
                attributions: _attributionText,
                tileSize: 512
              })
          })
    ],
    
    mod = {
        
        /** Initialize this module. */
        init : function () {
            
            if (_initialized) return;
            
            $(document).on("click", ".js-request-route-update", function () {
                $(".js-request-route-update").attr('disabled', true);
                $.post(yukon.url("/stars/comprehensiveMap/requestNetworkTreeUpdate"), function (response) {
                    if (response.isUpdateRequestSent) {
                        yukon.ui.alertSuccess(response.msgText);
                    } else {
                        yukon.ui.alertError(response.msgText);
                    }
                    $(window).scrollTop($('#user-message').offset().top);
                });
            });

            /** Redirects to new device map network page **/
            $(document).on('click', '.js-device-map', function() {
                var deviceId = $(this).data('deviceId');
                window.location.href = yukon.url('/stars/mapNetwork/home') + '?deviceId=' + deviceId;
            });
            
            $(document).on('change', '.js-all-gateways', function () {
                yukon.mapping.showHideAllGateways();
            });
            
            $(document).on('change', '.js-all-relays', function () {
                yukon.mapping.showHideAllRelays();
            });        
            
            $('#map-tiles button').click(function (ev) {
                $(this).siblings().removeClass('on');
                $(this).addClass('on');
                for (var i in _tiles) {
                    var layer = $(this).data('layer');
                    _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                }
                
            });
            
            _initialized = true;
        },
        
        initializeMap: function(map) {
            _map = map;
        },
        
        getStyles: function() {
            return _styles;
        },
        
        getTiles: function() {
            return _tiles;
        },
        
        getAnchor: function() {
            return _anchor;
        },
        
        getDestProjection: function() {
            return _destProjection;
        },
        
        getSrcProjection: function() {
            return _srcProjection;
        },
        
        getLineLayerIndex: function() {
            return _lineLayerIndex;
        },
        
        getRouteColor: function() {
            return _routeColor;
        },
        
        getLargerScale: function() {
            return _largerScale;
        },
        
        getIconLayer: function() {
            var iconLayer;
            _map.getLayers().forEach(function (layer) {
                if (layer.get('name') === 'icons') {
                    iconLayer = layer;
                }
            });
            return iconLayer;
        },
        
        getIconLayerSource: function() {
            return yukon.mapping.getIconLayer().getSource();
        },
        
        findFocusDevice: function(deviceId, makeLarger) {
            var source = yukon.mapping.getIconLayerSource(),
                feature = source.getFeatureById(deviceId);
            if (feature && makeLarger) {
                yukon.mapping.makeDeviceIconLarger(feature);
            }
            return feature;
        },
        
        makeDeviceIconLarger: function(icon) {
            var largerStyle = icon.getStyle().clone();
            largerStyle.getImage().setScale(_largerScale);
            icon.setStyle(largerStyle);
        },
        
        setScaleForDevice: function(feature) {
            var currentStyle = feature.getStyle(),
                scale = _deviceScale,
                gatewayTypes = $('#gatewayTypes').val(),
                relayTypes = $('#relayTypes').val(),
                wifiTypes = $('#wifiTypes').val(),
                pao = feature.get('pao');
            if (relayTypes.indexOf(pao.paoType) > -1 || wifiTypes.indexOf(pao.paoType) > -1) {
                scale = _relayScale;
            } else if (gatewayTypes.indexOf(pao.paoType) > -1) {
                scale = _gatewayScale;
            }
            currentStyle.getImage().setScale(scale);
            feature.setStyle(currentStyle);
        },
        
        displayCommonPopupProperties: function(pao) {
            $('.js-device-display').toggleClass('dn', pao.device.name === null);
            if (pao.deviceDetailUrl != null) {
                var deviceLink = '<a href="' + yukon.url(pao.deviceDetailUrl) + '" target=_blank>' + yukon.escapeXml(pao.device.name) + '</a>',
                    actionsDiv = $('#actionsDiv').clone().removeClass('dn');
                actionsDiv.find('.js-device-neighbors, .js-device-route, .js-device-map').attr('data-device-id', pao.device.paoIdentifier.paoId);
                actionsDiv.find('.js-view-all-notes').attr('data-pao-id', pao.device.paoIdentifier.paoId);
                yukon.tools.paonotespopup.hideShowNotesIcons(pao.device.paoIdentifier.paoId);
                var gatewayTypes = $('#gatewayTypes').val();
                if (gatewayTypes.indexOf(pao.device.paoIdentifier.paoType) > -1) {
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
                $('.js-primary-gateway').html('<a href="' + yukon.url(pao.primaryGatewayUrl) + '" target=_blank>' + yukon.escapeXml(pao.primaryGateway) + '</a>');
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
            $('.js-descendant-count-display').toggleClass('dn', routeInfo.descendantCount === null);
            $('.js-hop-count').text(routeInfo.route.hopCount);
            $('.js-descendant-count').text(routeInfo.descendantCount);
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
        
        displayParentNodePopupProperties: function(parent) {
            var parentData = parent.data;
            $('.js-node-sn-display').toggleClass('dn', (parentData.nodeSN === null || parent.gatewayType));
            $('.js-node-sn').text(parentData.nodeSN);
            $('.js-serial-number-display').toggleClass('dn', (parentData.rfnIdentifier.sensorSerialNumber === null || parent.gatewayType));
            $('.js-serial-number').text(parentData.rfnIdentifier.sensorSerialNumber);
            $('.js-gateway-serial-number-display').toggleClass('dn', (parentData.rfnIdentifier.sensorSerialNumber === null || !parent.gatewayType));
            $('.js-gateway-serial-number').text(parentData.rfnIdentifier.sensorSerialNumber);
            $('.js-ip-address-display').toggleClass('dn', parent.ipAddress === null);
            $('.js-ip-address').text(parent.ipAddress);
            $('.js-mac-address-display').toggleClass('dn', parentData.nodeMacAddress === null);
            $('.js-mac-address').text(parentData.nodeMacAddress);
            $('.js-distance-display').toggleClass('dn', parent.distanceDisplay === null);
            $('.js-distance').text(parent.distanceDisplay);
            $('#neighbor-info').hide();
            $('#device-info').hide();
            $('#route-info').hide();
            $('#parent-info').show();
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
        },
        
        displayMappingPopup: function(feature, overlay) {
            var geometry = feature.getGeometry(),
                coord = geometry.getCoordinates(),
                properties = feature.getProperties(),
                parent = properties.parent,
                neighbor = properties.neighbor,
                routeInfo = properties.routeInfo,
                nearby = properties.nearby,
                primaryRoutesExists = $('.js-all-routes').exists(),
                fromComprehensiveMap = $('.js-all-routes-comprehensive').exists(),
                includeRouteData = false;
                if (primaryRoutesExists) {
                    var allRoutesChecked = $('.js-all-routes').find(':checkbox').prop('checked');
                    //always display route data for comprehensive map
                    includeRouteData = fromComprehensiveMap ? true : allRoutesChecked;
                }
            if (parent != null) {
                mod.displayCommonPopupProperties(parent);
                mod.displayParentNodePopupProperties(parent);
                overlay.setPosition(coord);
            } else if (routeInfo != null) {
                mod.displayCommonPopupProperties(routeInfo);
                mod.displayPrimaryRoutePopupProperties(routeInfo);
                overlay.setPosition(coord);
            } else if (neighbor != null) {
                mod.displayCommonPopupProperties(neighbor);
                mod.displayNeighborPopupProperties(neighbor);
                overlay.setPosition(coord);
            } else {
                $('#parent-info').hide();
                $('#neighbor-info').hide();
                $('#route-info').hide();
                var url = yukon.url('/tools/map/device/' + feature.get('pao').paoId + '/info?includePrimaryRoute=' + includeRouteData);
                $('#device-info').load(url, function() {
                    if (nearby != null) {
                        $('.js-distance').text(nearby.distance.distance.toFixed(4) + " ");
                        $('.js-distance-display').show();
                    }
                    $('#device-info').show();
                    $('#marker-info').show();
                    overlay.setPosition(coord);
                    var deviceStatus = $('.js-status').text();
                    mod.updateDeviceStatusClass(deviceStatus);
                });
                //close any lingering delete dialogs to simplify handling
                var deleteDialog = $('#confirm-delete');
                if (deleteDialog.hasClass('ui-dialog-content')) {
                    deleteDialog.dialog('destroy');
                }
            }
        },
        
        updateZoom: function(map) {
            var source = yukon.mapping.getIconLayerSource(),
                features = source.getFeatures();
            if (features != null && features.length > 0) {
                if (features.length > 1) {
                    map.getView().fit(source.getExtent(), map.getSize());
                    //zoom out just a little to make sure pins display fully
                    var currentZoom = map.getView().getZoom();
                    map.getView().setZoom(currentZoom - 0.2);
                    if (currentZoom > 16){
                        map.getView().setZoom(16);
                    }
                } else {
                    map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
                    map.getView().setZoom(13);
                }
            }
        },

        showHideElevationLayer: function(map, button) {
            var checked = button.hasClass('on');
            if (checked) {
                button.removeClass('on');
                map.removeLayer(_elevationLayer);
            } else {
                _elevationLayer = new ol.layer.VectorTile({
                    declutter: true,
                    opacity: 0.6,
                    source: new ol.source.VectorTile({
                        format: new ol.format.MVT(),
                        url: yg.map_devices_elevation_url
                    }),
                })
                button.addClass('on');
                map.addLayer(_elevationLayer);
            }
        },
        
        addFeatureToMapAndArray: function(feature, iconArray) {
            var source = yukon.mapping.getIconLayerSource(),
                pao = feature.properties.paoIdentifier,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ pao: pao });
            icon.setId(feature.id);
        
            //check if device already exists on map
            var deviceFound = yukon.mapping.findFocusDevice(pao.paoId, false);
            if (deviceFound) {
                icon = deviceFound;
            } else {
                icon.setStyle(style);
                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
                
                iconArray.push(icon);
                source.addFeature(icon);
            }
            return icon;
        },
        
        showHideAllGateways: function() {
            var checked = $('.js-all-gateways').find(':checkbox').prop('checked');
            if (!checked) {
                yukon.mapping.removeAllGatewayIcons();
            } else {
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/allGateways'),
                    type: 'get'
                }).done( function(data) {
                    for (var x in data.features) {
                        var feature = data.features[x];
                        yukon.mapping.addFeatureToMapAndArray(feature, _allGatewayIcons);
                    }
                });
            }
        },
        
        showHideAllRelays: function() {
            var checked = $('.js-all-relays').find(':checkbox').prop('checked');
            if (!checked) {
                yukon.mapping.removeAllRelayIcons();
            } else {
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/allRelays'),
                    type: 'get'
                }).done( function(data) {
                    for (var x in data.features) {
                        var feature = data.features[x];
                        yukon.mapping.addFeatureToMapAndArray(feature, _allRelayIcons);
                    }
                });
            }
        },
        
        drawChildren: function(currentNode, primaryRoutePreviousPoints, atRoot, gatewayPoints) {
            var currentNodePoints = primaryRoutePreviousPoints,
                parentFeature = yukon.mapping.getFeatureFromData(currentNode),
                dashedLine = parentFeature == null;
            
            for (var x in currentNode.children) {
                if (atRoot) {
                    dashedLine = false;
                    currentNodePoints = gatewayPoints;
                    primaryRoutePreviousPoints = gatewayPoints;
                }
                var child = currentNode.children[x];
      
                if (yukon.mapping.shouldLineBeDrawn(child)) {
                    var feature = yukon.mapping.getFeatureFromData(child);
                    if (feature != null) {
                        var icon = yukon.mapping.addFeatureToMapAndArray(feature, _allRoutesIcons);
                        if (currentNodePoints != null) {
                            var points = [];
                            points.push(icon.getGeometry().getCoordinates());
                            points.push(currentNodePoints);
                            
                            var lineFeature = new ol.Feature({
                                geometry: new ol.geom.LineString(points),
                                name: 'Line'
                            })
                            if (dashedLine) {
                                _allRoutesDashedLineFeatures.push(lineFeature);
                            } else {
                                _allRoutesLineFeatures.push(lineFeature);
                            }
                        }
                        primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
                    } 
                } 
                yukon.mapping.drawChildren(child, primaryRoutePreviousPoints, false, gatewayPoints);
            }
        },
        
        shouldLineBeDrawn: function(currentNode) {
            //check node and all children to see if any of the devices are on map
            var source = yukon.mapping.getIconLayerSource(),
                drawLine = false,
                paoId = yukon.mapping.getPaoIdFromData(currentNode);
            if (paoId != null) {
                //first check self
                if (source.getFeatureById(paoId)) {
                    return true;
                }
            }

            for (var x in currentNode.children) {
                var child = currentNode.children[x],
                    paoId = yukon.mapping.getPaoIdFromData(child);
                if (paoId != null) {
                    //check any of the children
                    if (source.getFeatureById(paoId) != null || yukon.mapping.shouldLineBeDrawn(child)) {
                        return true;
                    }
                }
            }
            return drawLine;
        },
        
        getPaoIdFromData: function(node) {
          if (node.data != null) {
              return Object.keys(node.data)[0];
          }  
        },
        
        getFeatureFromData: function(node) {
            if (node.data != null) {
                var nodeData = Object.keys(node.data).map(function (key) {
                    return node.data[key];
                });
                if (nodeData[0] != null) {
                    return nodeData[0].features[0];
                }
            }
        },
                
        showHideAllRoutes: function(gatewayIds) {
            $('.js-no-location-message').addClass('dn');
            var checked = $('.js-all-routes').find(':checkbox').prop('checked');
            // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
            if ($("#js-route-details-container").exists()) {
                $("#js-route-details-container").toggleClass("vh", !checked);
            }
            if (!checked) {
                yukon.mapping.removeAllRoutesLayers();
                // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
                if ($("#js-route-details-container").exists()) {
                    clearInterval(_updateNetworkTreeInterval);
                }
            } else {
                // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
                if ($("#js-route-details-container").exists()) {
                    _setRouteLastUpdatedDateTime(null); // It may take few seconds to fetch the RouteLastUpdatedDateTime value. Meanwhile display test "Loading..."
                    _updateNetworkTreeInterval = setInterval(function () {
                        $.getJSON(yukon.url('/stars/comprehensiveMap/getRouteDetails'), function (response) {
                            _setRouteLastUpdatedDateTime(response.routeLastUpdatedDateTime);
                            $("#js-route-details-container").find(".js-request-route-update").attr("disabled", !response.isUpdatePossible);
                            if (response.updateRoutes) {
                                yukon.mapping.showHideAllRoutes(gatewayIds);
                            }
                        });
                    }, yg.rp.updater_delay);
                }

                var mapContainer = $('#map-container'),
                       primaryRoutePreviousPoints;
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/comprehensiveMap/allPrimaryRoutes?gatewayIds=' + gatewayIds))
                .done(function (json) {
                    //check for error
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                        // TODO: Remove this if condition after adding this functionality support to collection actions and map devices page.
                        if ($("#js-route-details-container").exists()) {
                            clearInterval(_updateNetworkTreeInterval);
                        }
                    } else if (json.tree) {
                        yukon.mapping.removeAllRoutesLayers();
                        _setRouteLastUpdatedDateTime(json.routeLastUpdatedDateTime);
                        $("#js-route-details-container").find(".js-request-route-update").attr("disabled", !json.isUpdatePossible)

                        //gateway is top node
                        for (var x in json.tree) {
                            var currentNode = json.tree[x],
                                feature = yukon.mapping.getFeatureFromData(currentNode);
                            if (feature != null) {
                                var icon = yukon.mapping.addFeatureToMapAndArray(feature, _allRoutesIcons);
                                primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
                                yukon.mapping.drawChildren(currentNode, primaryRoutePreviousPoints, true, primaryRoutePreviousPoints);
                            } else {
                                //this is a virtual gateway so draw children instead
                                for (var i in currentNode.children) {
                                    var childNode = currentNode.children[i],
                                        feature = yukon.mapping.getFeatureFromData(childNode);
                                    if (feature != null) {
                                        var icon = yukon.mapping.addFeatureToMapAndArray(feature, _allRoutesIcons);
                                        primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
                                        yukon.mapping.drawChildren(childNode, primaryRoutePreviousPoints, true, primaryRoutePreviousPoints);
                                    }
                                }
                            }

                            if (_allRoutesLineFeatures.length > 0) {
                                //draw lines
                                var layerLines = new ol.layer.Vector({
                                    source: new ol.source.Vector({
                                        features: _allRoutesLineFeatures
                                    }),
                                    style: new ol.style.Style({
                                        stroke: new ol.style.Stroke({ color: _routeColor, width: 2.5 })
                                    })
                                });
                                
                                _allRoutesLines.push(layerLines);
                                _map.getLayers().insertAt(_lineLayerIndex, layerLines);
                                _allRoutesLineFeatures = [];
                            }
                            
                            if (_allRoutesDashedLineFeatures.length > 0) {
                                
                                $('.js-no-location-message').removeClass('dn');
                                //draw dashed lines
                                var dashedLines = new ol.layer.Vector({
                                    source: new ol.source.Vector({
                                        features: _allRoutesDashedLineFeatures
                                    }),
                                    style: new ol.style.Style({
                                        stroke: new ol.style.Stroke({ color: _routeColor, width: 2.5, lineDash: [10,10] })
                                    })
                                });
                                
                                _allRoutesLines.push(dashedLines);
                                _map.getLayers().insertAt(_lineLayerIndex, dashedLines);
                                _allRoutesDashedLineFeatures = [];
                            }
                            
                        }
                    }
                }).always(function () {
                    yukon.ui.unblock(mapContainer);
                });
            }
        },
        
        removeAllGatewayIcons: function() {
            var source = yukon.mapping.getIconLayerSource();
            _allGatewayIcons.forEach(function (icon) {
                source.removeFeature(icon);
            });
            _allGatewayIcons = [];
        },
        
        removeAllRelayIcons: function() {
            var source = yukon.mapping.getIconLayerSource();
            _allRelayIcons.forEach(function (icon) {
                source.removeFeature(icon);
            });
            _allRelayIcons = [];
        },
        
        removeAllRoutesLayers: function() {
            var source = yukon.mapping.getIconLayerSource();
            _allRoutesIcons.forEach(function (icon) {
                source.removeFeature(icon);
            });
            _allRoutesLines.forEach(function (line) {
                _map.removeLayer(line);
            });
            _allRoutesIcons = [];
            _allRoutesLines = [];
        },
        
        adjustMapForFullScreenModeChange: function(mapContainer, paddingTop) {
            // we if are doing an exit from the full screen, close any open pop-ups
            if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                $(".ui-dialog-content").dialog("close");
                if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                    $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                }
                //move all dropdowns back to the body
                var menus = $('div.ol-viewport').find('.dropdown-menu');
                $('body').prepend(menus);
                //adjust height back
                mapContainer.css('padding-top', '0px');
            } else {
                //adjust height for mapping buttons
                mapContainer.css('padding-top', paddingTop);
                mapContainer.css('padding-bottom', '0px');
                
                //move any dropdowns from body to viewport
                var menus = $('body').children('.dropdown-menu');
                $('div.ol-viewport').prepend(menus);
            }
            //close any popups
            $('#marker-info').hide();
            mod.updateZoom(_map);
            _map.updateSize();
        }

    };
 
    return mod;
})();
 
$(function () { yukon.mapping.init(); });
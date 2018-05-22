yukon.namespace('yukon.map.network');
 
/**
 * Module to handle the map network page
 * 
 * @module yukon.map.network
 * @requires yukon
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.map.network = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    //Line Color depends on ETX Band 1 - #006622(GREEN), 2 - #669900(LIGHT GREEN), 3 - #CCA300(YELLOW), 4 - #FF6600(ORANGE), 5 and up - #FF0000(RED)
    _neighborColors = ['#006622', '#669900', '#CCA300', '#FF6600', '#FF0000'],    
    //grey
    _parentColor = "#808080",
    //dark blue
    _routeColor = "#0000CC",
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _neighborsLayerIndex = 0,
    _primaryRouteLayerIndex = 1,
    _parentLayerIndex = 2,
    _iconLayerIndex = 3,
    _deviceIconLayerIndex = 4,
    
    _devicePoints = [],
    _deviceIcon,
    _deviceStandsOut = false,
    _deviceOriginalStyle,
    _parentIcon,
    _parentLine,
    _parentIconLayer,
    _neighborIcons = [],
    _neighborLines = [],
    _neighborIconLayer,
    _primaryRouteIcons = [],
    _primaryRouteLines = [],
    _primaryRouteIconLayer,
    _primaryRoutePreviousPoints,
    _deviceDragInteraction,
    _nearbyIcons = [],
    _nearbyIconLayer,
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    /** @type {Object.<string, {ol.style.Style}>} - A cache of styles to avoid creating lots of objects using lots of memory. */
    _styles = { 
        'METER_ELECTRIC': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-elec-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_PLC_ELECTRIC': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-plc-elec-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_WATER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-water-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_GAS': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-gas-grey.png'), anchor: [0.5, 1.0] }) }),
        'TRANSMITTER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter-grey.png'), anchor: [0.5, 1.0] }) }),
        'RELAY': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-relay-grey.png'), anchor: [0.5, 1.0] }) }),
        'LCR' : new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-lcr-grey.png'), anchor: [0.5, 1.0] }) }),
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
    
    /** 
     * Gets pao location as geojson format and adds an icon feature to the vector layer for the map.
     */
    _loadIcon = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource(),
            fc = yukon.fromJson('#geojson'),
            feature = fc.features[0],
            src_projection = fc.crs.properties.name,
            pao = feature.properties.paoIdentifier,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ pao: pao });
            
        icon.setStyle(style);
        _deviceOriginalStyle = style;
            
        if (src_projection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        // Drag and drop feature
        _deviceDragInteraction = new ol.interaction.Modify({
            features: new ol.Collection([icon]),
            pixelTolerance: 40
        });
        
        // Add the event to the drag and drop feature
        _deviceDragInteraction.on('modifyend', function(e) {
            yukon.map.location.changeCoordinatesPopup(e, _destProjection, src_projection);
        }, icon);
        
        _devicePoints = icon.getGeometry().getCoordinates();
        
        source.addFeature(icon);
        _deviceIcon = icon;
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: [_deviceIcon]})});
        iconsLayer.setZIndex(_deviceIconLayerIndex);
        _map.addLayer(iconsLayer);
        
        _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
        _map.getView().setZoom(13);
    },
    
    _loadParentData = function(parent) {
        var fc = yukon.fromJson('#geojson'),
            source = _map.getLayers().getArray()[_tiles.length].getSource(),
            feature = parent.location.features[0],
            src_projection = fc.crs.properties.name,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ parent: parent });
        
        icon.setStyle(style);
    
        if (src_projection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        _parentIcon = icon;
        source.addFeature(icon);
        
        //draw line
        var points = [];
        points.push(icon.getGeometry().getCoordinates());
        points.push(_devicePoints);
        
        var layerLines = new ol.layer.Vector({
            source: new ol.source.Vector({
                features: [new ol.Feature({
                    geometry: new ol.geom.LineString(points),
                    name: 'Line'
                })]
            }),
            style: new ol.style.Style({
                stroke: new ol.style.Stroke({ color: _parentColor, width: 2, lineDash: [10,10] })
            })
        });
        
        layerLines.setZIndex(_parentLayerIndex);
        _parentLine = layerLines;
        _map.addLayer(layerLines);
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: [_parentIcon]})});
        iconsLayer.setZIndex(_iconLayerIndex);
        _parentIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
        _makeCurrentDeviceStandOut();
    },
    
    _loadNeighborData = function(neighbors) {
        var fc = yukon.fromJson('#geojson');
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        for (x in neighbors) {
            var neighbor = neighbors[x],
            feature = neighbor.location.features[0],
            src_projection = fc.crs.properties.name,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
            icon = new ol.Feature({ neighbor: neighbor });
            
            icon.setStyle(style);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }
            
            _neighborIcons.push(icon);
            source.addFeature(icon);
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            points.push(_devicePoints);

            //Line Color is based on ETX Band - see colors above
            var etxBand = neighbor.data.etxBand;
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

            //Line thickness depends on Number of Samples 0-50 - 1px, 51-500 - 2px, 500 and up - 3px
            var numberSamples = neighbor.data.numSamples;
            var lineThickness = 1;
            if (numberSamples > 50 && numberSamples <= 500) {
                lineThickness = 2;
            } else if (numberSamples > 500) {
                lineThickness = 3;
            }
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: [new ol.Feature({
                        geometry: new ol.geom.LineString(points),
                        name: 'Line'
                    })]
                }),

                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: lineColor, width: lineThickness })
                })
            });
            
            layerLines.setZIndex(_neighborsLayerIndex);
            _neighborLines.push(layerLines);
            _map.addLayer(layerLines);
        }
        
        var allIcons = [];
        allIcons.push.apply(allIcons, _neighborIcons);
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(_iconLayerIndex);
        _neighborIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
        _makeCurrentDeviceStandOut();
    },
    
    _loadPrimaryRouteData = function(routeInfo) {
        var fc = yukon.fromJson('#geojson');
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        for (x in routeInfo) {
            var route = routeInfo[x],
            feature = route.location.features[0],
            src_projection = fc.crs.properties.name,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
            icon = new ol.Feature({ routeInfo: route });
            
            icon.setStyle(style);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }

            _primaryRouteIcons.push(icon);
            source.addFeature(icon);
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            if (_primaryRoutePreviousPoints != null) {
                points.push(_primaryRoutePreviousPoints);
            } else {
                points.push(_devicePoints);
            }
            _primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();
            
            var layerLines = new ol.layer.Vector({
                source: new ol.source.Vector({
                    features: [new ol.Feature({
                        geometry: new ol.geom.LineString(points),
                        name: 'Line'
                    })]
                }),
                style: new ol.style.Style({
                    stroke: new ol.style.Stroke({ color: _routeColor, width: 3.5 })
                })
            });
            
            layerLines.setZIndex(_primaryRouteLayerIndex);
            _primaryRouteLines.push(layerLines);
            _map.addLayer(layerLines);
        }
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: _primaryRouteIcons})});
        iconsLayer.setZIndex(_iconLayerIndex);
        _primaryRouteIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
        _makeCurrentDeviceStandOut();
    },
    
    _loadNearbyDevices = function(nearbyDevices) {
        var fc = yukon.fromJson('#geojson');
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        for (x in nearbyDevices) {
            var nearby = nearbyDevices[x],
            feature = nearby.location.features[0],
            pao = feature.properties.paoIdentifier,
            src_projection = fc.crs.properties.name,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
            icon = new ol.Feature({ nearby: nearby, pao: pao });
            
            icon.setStyle(style);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }
        
            _nearbyIcons.push(icon);
            source.addFeature(icon);
        }
        
        var allIcons = [];
        allIcons.push.apply(allIcons, _nearbyIcons);
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(_iconLayerIndex);
        _nearbyIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
        _makeCurrentDeviceStandOut();
    },
    
    _makeCurrentDeviceStandOut = function() {
        /*Change device Icon to be larger and have colored circle underneath*/
        if (!_deviceStandsOut) {
            var largerStyle = _deviceIcon.getStyle().clone(),
                circleStyle = new ol.style.Style({
                image: new ol.style.Circle({
                    radius: 8,
                    fill: new ol.style.Fill({color: _routeColor}),
                    stroke: new ol.style.Stroke({color: 'black', width: 2}) 
                })
            });
            largerStyle.getImage().setScale(1.3);
            _deviceIcon.setStyle([circleStyle, largerStyle]);
            _deviceStandsOut = true;
        }
    },
    
    _checkToGoBackToDeviceOriginalStyle = function() {
        //map originally has 5 layers (Map, Satellite, Hybrid, Device Feature, Icon Layer)
        if (_map.getLayers().getArray().length < 6) {
            _deviceIcon.setStyle(_deviceOriginalStyle);
            _deviceStandsOut = false;
        }
    }
    
    _getNearbyDevices = function() {
        var fc = yukon.fromJson('#geojson'),
        feature = fc.features[0],
        paoId = feature.id,
        miles = $('#miles').val();
        yukon.ui.removeAlerts();
        yukon.ui.busy('.js-nearby');
        $.getJSON('nearby?' + $.param({ deviceId: paoId, miles: miles }))
        .done(function (json) {
            if (json.nearby) {
                _loadNearbyDevices(json.nearby);
            }
            if (json.errorMsg) {
                yukon.ui.alertError(json.errorMsg);
            }
            yukon.ui.unbusy('.js-nearby');
        });
    },
    
    _removeNearbyDevices = function() {
        yukon.ui.removeAlerts();
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        for (x in _nearbyIcons) {
            var nearby = _nearbyIcons[x];
            var source = _map.getLayers().getArray()[_tiles.length].getSource();
            source.removeFeature(nearby);
        }
        _map.removeLayer(_nearbyIconLayer);
        _nearbyIcons = [];
        _updateZoom();
        _checkToGoBackToDeviceOriginalStyle();
    },
    
    _updateZoom = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        var features = source.getFeatures();
        if (features != null && features.length > 1) {
            _map.getView().fit(source.getExtent(), _map.getSize());
            if (_map.getView().getZoom() > 16){
                _map.getView().setZoom(16);
            }
        } else {
            _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
            _map.getView().setZoom(13);
        }
    },
    
    _displayNeighborsLegend = function() {
        var borderDetails = "2px solid ";
        $('.js-etx-1').css({backgroundColor: _neighborColors[0], borderTop: borderDetails + _neighborColors[0]});
        $('.js-etx-2').css({backgroundColor: _neighborColors[1], borderTop: borderDetails + _neighborColors[1]});
        $('.js-etx-3').css({backgroundColor: _neighborColors[2], borderTop: borderDetails + _neighborColors[2]});
        $('.js-etx-4').css({backgroundColor: _neighborColors[3], borderTop: borderDetails + _neighborColors[3]});
        $('.js-etx-5').css({backgroundColor: _neighborColors[4], borderTop: borderDetails + _neighborColors[4]});
        $('.js-legend-neighbors').show();
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
                        
            /** Initialize map if we have a location. */
            if ($('#device-location').data('hasLocation') === true) {
                
                /** Setup the openlayers map. */
                _map = new ol.Map({
                    controls: [
                        new ol.control.Attribution(),
                        new ol.control.FullScreen({source: 'map-network-container'}),
                        new ol.control.Zoom() 
                    ],
                    layers: _tiles,
                    target: 'device-location',
                    view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
                });
                _destProjection = _map.getView().getProjection().getCode();
                _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
                
                /** Load icon for location */
                _loadIcon();
                
                /** Display marker info popup on marker clicks. */
                var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
                _map.addOverlay(_overlay);
                _map.on('click', function(ev) {

                    var feature = _map.forEachFeatureAtPixel(ev.pixel, function(feature, layer) { 
                        var featureProperties = feature.getProperties();
                        if (featureProperties.name != 'Line') {
                            return feature; 
                        }
                    });
                    if (feature) {
                        var 
                        geometry = feature.getGeometry(),
                        coord = geometry.getCoordinates(),
                        properties = feature.getProperties(),
                        parent = properties.parent;
                        neighbor = properties.neighbor;
                        routeInfo = properties.routeInfo;
                        nearby = properties.nearby;

                        if (parent != null) {
                            var parentData = parent.data;
                            $('.js-device-display').toggleClass('dn', parent.device.name == null);
                            if (parent.deviceDetailUrl != null) {
                                var nameSpan = $('<span />');
                                nameSpan.text(parent.device.name);
                                $('.js-device').html('<a href="' + yukon.url(parent.deviceDetailUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
                            } else {
                                $('.js-device').text(parent.device.name);
                            }
                            $('.js-meter-number-display').toggleClass('dn', parent.meterNumber == null);
                            $('.js-meter-number').text(parent.meterNumber);
                            $('.js-type-display').toggleClass('dn', parent.device.paoIdentifier.paoType == null);
                            $('.js-type').text(parent.device.paoIdentifier.paoType);
                            $('.js-status-display').toggleClass('dn', parent.statusDisplay == null);
                            $('.js-status').text(parent.statusDisplay);
                            $('.js-node-sn-display').toggleClass('dn', (parentData.nodeSN == null || parent.gatewayType));
                            $('.js-node-sn').text(parentData.nodeSN);
                            $('.js-serial-number-display').toggleClass('dn', (parentData.rfnIdentifier.sensorSerialNumber == null || parent.gatewayType));
                            $('.js-serial-number').text(parentData.rfnIdentifier.sensorSerialNumber);
                            $('.js-gateway-serial-number-display').toggleClass('dn', (parentData.rfnIdentifier.sensorSerialNumber == null || !parent.gatewayType));
                            $('.js-gateway-serial-number').text(parentData.rfnIdentifier.sensorSerialNumber);
                            $('.js-ip-address-display').toggleClass('dn', parent.ipAddress == null);
                            $('.js-ip-address').text(parent.ipAddress);
                            $('.js-mac-address-display').toggleClass('dn', parentData.nodeMacAddress == null);
                            $('.js-mac-address').text(parentData.nodeMacAddress);
                            $('.js-primary-gateway-display').toggleClass('dn', parent.primaryGateway == null);
                            if (parent.primaryGatewayUrl != null) {
                                var nameSpan = $('<span />');
                                nameSpan.text(parent.primaryGateway);
                                $('.js-primary-gateway').html('<a href="' + yukon.url(parent.primaryGatewayUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
                            } else {
                                $('.js-primary-gateway').text(parent.primaryGateway);
                            }
                            $('.js-distance-display').toggleClass('dn', parent.distanceDisplay == null);
                            $('.js-distance').text(parent.distanceDisplay);
                            $('#neighbor-info').hide();
                            $('#device-info').hide();
                            $('#route-info').hide();
                            $('#parent-info').show();
                            $('#marker-info').show();
                            _overlay.setPosition(coord);
                        } else if (neighbor != null) {
                            var neighborData = neighbor.data;
                            $('.js-device-display').toggleClass('dn', neighbor.device.name == null);
                            if (neighbor.deviceDetailUrl != null) {
                                var nameSpan = $('<span />');
                                nameSpan.text(neighbor.device.name);
                                $('.js-device').html('<a href="' + yukon.url(neighbor.deviceDetailUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
                            } else {
                                $('.js-device').text(neighbor.device.name);
                            }
                            $('.js-meter-number-display').toggleClass('dn', neighbor.meterNumber == null);
                            $('.js-meter-number').text(neighbor.meterNumber);
                            $('.js-type-display').toggleClass('dn', neighbor.device.paoIdentifier.paoType == null);
                            $('.js-type').text(neighbor.device.paoIdentifier.paoType);
                            $('.js-status-display').toggleClass('dn', neighbor.statusDisplay == null);
                            $('.js-status').text(neighbor.statusDisplay);
                            $('.js-node-sn-display').toggleClass('dn', (neighborData.serialNumber == null || neighbor.gatewayType));
                            $('.js-node-sn').text(neighborData.serialNumber);
                            $('.js-serial-number-display').toggleClass('dn', (neighborData.rfnIdentifier.sensorSerialNumber == null || neighbor.gatewayType));
                            $('.js-serial-number').text(neighborData.rfnIdentifier.sensorSerialNumber);
                            $('.js-gateway-serial-number-display').toggleClass('dn', (neighborData.rfnIdentifier.sensorSerialNumber == null || !neighbor.gatewayType));
                            $('.js-gateway-serial-number').text(neighborData.rfnIdentifier.sensorSerialNumber);
                            $('.js-ip-address-display').toggleClass('dn', neighbor.ipAddress == null);
                            $('.js-ip-address').text(neighbor.ipAddress);
                            $('.js-address-display').toggleClass('dn', neighborData.neighborAddress == null);
                            $('.js-address').text(neighborData.neighborAddress);
                            $('.js-primary-gateway-display').toggleClass('dn', neighbor.primaryGateway == null);
                            if (neighbor.primaryGatewayUrl != null) {
                                var nameSpan = $('<span />');
                                nameSpan.text(neighbor.primaryGateway);
                                $('.js-primary-gateway').html('<a href="' + yukon.url(neighbor.primaryGatewayUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
                            } else {
                                $('.js-primary-gateway').text(neighbor.primaryGateway);
                            }
                            $('.js-flags-display').toggleClass('dn', neighbor.commaDelimitedNeighborFlags == null);
                            $('.js-flags').text(neighbor.commaDelimitedNeighborFlags);
                            $('.js-link-cost-display').toggleClass('dn', neighborData.neighborLinkCost == null);
                            $('.js-link-cost').text(neighborData.neighborLinkCost);
                            $('.js-num-samples-display').toggleClass('dn', neighborData.numSamples == null);
                            $('.js-num-samples').text(neighborData.numSamples);
                            $('.js-etx-band-display').toggleClass('dn', neighborData.etxBand == null);
                            $('.js-etx-band').text(neighborData.etxBand);
                            $('.js-distance-display').toggleClass('dn', neighbor.distanceDisplay == null);
                            $('.js-distance').text(neighbor.distanceDisplay);
                            $('#parent-info').hide();
                            $('#device-info').hide();
                            $('#route-info').hide();
                            $('#neighbor-info').show();
                            $('#marker-info').show();
                            _overlay.setPosition(coord);
                        } else if (routeInfo != null) {
                            $('.js-device-display').toggleClass('dn', routeInfo.device.name == null);
                            if (routeInfo.deviceDetailUrl != null) {
                                var nameSpan = $('<span />');
                                nameSpan.text(routeInfo.device.name);
                                $('.js-device').html('<a href="' + yukon.url(routeInfo.deviceDetailUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
                            } else {
                                $('.js-device').text(routeInfo.device.name);
                            }
                            $('.js-meter-number-display').toggleClass('dn', routeInfo.meterNumber == null);
                            $('.js-meter-number').text(routeInfo.meterNumber);
                            $('.js-type-display').toggleClass('dn', routeInfo.device.paoIdentifier.paoType == null);
                            $('.js-type').text(routeInfo.device.paoIdentifier.paoType);
                            $('.js-status-display').toggleClass('dn', routeInfo.statusDisplay == null);
                            $('.js-status').text(routeInfo.statusDisplay);
                            $('.js-node-sn-display').toggleClass('dn', (routeInfo.route.serialNumber == null || routeInfo.gatewayType));
                            $('.js-node-sn').text(routeInfo.route.serialNumber);
                            $('.js-serial-number-display').toggleClass('dn', (routeInfo.route.rfnIdentifier.sensorSerialNumber == null || routeInfo.gatewayType));
                            $('.js-serial-number').text(routeInfo.route.rfnIdentifier.sensorSerialNumber);
                            $('.js-gateway-serial-number-display').toggleClass('dn', (routeInfo.route.rfnIdentifier.sensorSerialNumber == null || !routeInfo.gatewayType));
                            $('.js-gateway-serial-number').text(routeInfo.route.rfnIdentifier.sensorSerialNumber);
                            $('.js-ip-address-display').toggleClass('dn', routeInfo.ipAddress == null);
                            $('.js-ip-address').text(routeInfo.ipAddress);
                            $('.js-primary-gateway-display').toggleClass('dn', routeInfo.primaryGateway == null);
                            if (routeInfo.primaryGatewayUrl != null) {
                                var nameSpan = $('<span />');
                                nameSpan.text(routeInfo.primaryGateway);
                                $('.js-primary-gateway').html('<a href="' + yukon.url(routeInfo.primaryGatewayUrl) + '" target=_blank>' + nameSpan.html() + '</a>');
                            } else {
                                $('.js-primary-gateway').text(routeInfo.primaryGateway);
                            }
                            $('.js-address-display').toggleClass('dn', routeInfo.macAddress == null);
                            $('.js-address').text(routeInfo.macAddress);
                            $('.js-destination-address-display').toggleClass('dn', routeInfo.route.destinationAddress == null);
                            $('.js-destination-address').text(routeInfo.route.destinationAddress);
                            $('.js-next-hop-address-display').toggleClass('dn', routeInfo.route.nextHopAddress == null);
                            $('.js-next-hop-address').text(routeInfo.route.nextHopAddress);
                            $('.js-total-cost-display').toggleClass('dn', routeInfo.route.totalCost == null);
                            $('.js-total-cost').text(routeInfo.route.totalCost);
                            $('.js-hop-count-display').toggleClass('dn', routeInfo.route.hopCount == null);
                            $('.js-hop-count').text(routeInfo.route.hopCount);
                            $('.js-route-flag-display').toggleClass('dn', routeInfo.commaDelimitedRouteFlags == null);
                            $('.js-route-flag').text(routeInfo.commaDelimitedRouteFlags);
                            $('.js-distance-display').toggleClass('dn', routeInfo.distanceInMiles == 0);
                            $('.js-distance').text(routeInfo.distanceDisplay);
                            $('#parent-info').hide();
                            $('#neighbor-info').hide();
                            $('#device-info').hide();
                            $('#route-info').show();
                            $('#marker-info').show();
                            _overlay.setPosition(coord);
                        } else {
                            $('#parent-info').hide();
                            $('#neighbor-info').hide();
                            $('#route-info').hide();
                            if (feature.get('pao') != null) {
                                url = yukon.url('/tools/map/device/' + feature.get('pao').paoId + '/info');
                                $('#device-info').load(url, function() {
                                    var deviceStatus = $('.js-device-status').val();
                                    if (deviceStatus) {
                                        $('.js-status').text(deviceStatus);
                                        $('.js-status-display').show();
                                    }
                                    if (nearby != null) {
                                        $('.js-distance').text(nearby.distance.distance.toFixed(4) + " ");
                                        $('.js-distance-display').show();
                                    }
                                    $('#device-info').show();
                                    $('#marker-info').show();
                                    _overlay.setPosition(coord);
                                });
                            }
                        }
                    } else {
                        $('#marker-info').hide();
                    }
                });
                
                /** Change map tiles layer on tile button group clicks. */
                $('#map-tiles button').click(function (ev) {
                    $(this).siblings().removeClass('on');
                    $(this).addClass('on');
                    for (var i in _tiles) {
                        var layer = $(this).data('layer');
                        _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                    }
                    
                });
                
                /** Change mouse cursor when over marker.  There HAS to be a css way to do this! */
                $(_map.getViewport()).on('mousemove', function(e) {
                    var pixel = _map.getEventPixel(e.originalEvent),
                        hit = _map.forEachFeatureAtPixel(pixel, function(feature, layer) { 
                            var featureProperties = feature.getProperties();
                            if (featureProperties.name != 'Line') {
                                return true; 
                            }
                        });
                    $('#' + _map.getTarget()).css('cursor', hit ? 'pointer' : 'default');
                });
                
                /** Remove the coordinates for the device when the user clicks OK on the confirmation popup. **/
                $(document).on('yukon:tools:map:delete-coordinates', function(event) {
                    var paoId = $('#remove-pin').data("pao"),
                        removeUrl = yukon.url('/tools/map/device/' + paoId);
                    
                    $.ajax({
                        url: removeUrl,
                        type: 'POST',
                        data: {_method: 'DELETE'},
                        success: function(results) {
                            $('#confirm-delete').dialog('destroy');
                            yukon.ui.removeAlerts();
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            var features = source.getFeatures();
                            if (features != null && features.length > 0) {
                                for (x in features) {
                                   var properties = features[x].getProperties();
                                   var id = properties.pao.paoId;
                                   if (id == paoId) {
                                     source.removeFeature(features[x]);
                                      break;
                                   }
                                 }
                               }
                            source.removeFeature(feature);
                        },
                        error: function(xhr, status, error) {
                            var errorMsg = xhr.responseJSON.message;
                            yukon.ui.alertError(errorMsg);
                        }
                    });
                    
                });
                
                /** Gets the neighbor data from Network Manager **/
                $(document).on('click', '.js-neighbor-data', function() {
                    var neighborsRow = $(this).closest('.switch-btn'),
                    wasChecked = neighborsRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        _displayNeighborsLegend();
                        if (_neighborIcons.length > 0) {
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            for (x in _neighborLines) {
                                var neighborLine = _neighborLines[x];
                                _map.addLayer(neighborLine);
                            }
                            for (x in _neighborIcons) {
                                var neighbor = _neighborIcons[x];
                                source.addFeature(neighbor);
                            }
                            _map.addLayer(_neighborIconLayer);
                            _updateZoom();
                            _makeCurrentDeviceStandOut();
                        } else {
                            var fc = yukon.fromJson('#geojson'),
                            feature = fc.features[0],
                            paoId = feature.id;
                            yukon.ui.busy('.js-neighbor-data');
                            $.getJSON('neighbors?' + $.param({ deviceId: paoId }))
                            .done(function (json) {
                                if (json.neighbors) {
                                    _loadNeighborData(json.neighbors);
                                }
                                if (json.errorMsg) {
                                    yukon.ui.alertError(json.errorMsg);
                                }
                                yukon.ui.unbusy('.js-neighbor-data');
                            });
                        }
                    } else {
                        $('.js-legend-neighbors').hide();
                        for (x in _neighborIcons) {
                            var neighbor = _neighborIcons[x];
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            source.removeFeature(neighbor);
                        }
                        for (x in _neighborLines) {
                            var neighborLine = _neighborLines[x];
                            _map.removeLayer(neighborLine);
                        }
                        _map.removeLayer(_neighborIconLayer);
                        _updateZoom();
                        _checkToGoBackToDeviceOriginalStyle();
                    }
                });
                
                /** Gets the primary route from Network Manager **/
                $(document).on('click', '.js-primary-route', function() {
                    var primaryRouteRow = $(this).closest('.switch-btn'),
                    wasChecked = primaryRouteRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        if (_primaryRouteIcons.length > 0) {
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            for (x in _primaryRouteLines) {
                                var routeLine = _primaryRouteLines[x];
                                _map.addLayer(routeLine);
                            }
                            for (x in _primaryRouteIcons) {
                                var route = _primaryRouteIcons[x];
                                source.addFeature(route);
                            }
                            _map.addLayer(_primaryRouteIconLayer);
                            _updateZoom();
                            _makeCurrentDeviceStandOut();
                        } else {
                            var fc = yukon.fromJson('#geojson'),
                            feature = fc.features[0],
                            paoId = feature.id;
                            yukon.ui.busy('.js-primary-route');
                            $.getJSON('primaryRoute?' + $.param({ deviceId: paoId }))
                            .done(function (json) {
                                if (json.routeInfo) {
                                    _loadPrimaryRouteData(json.routeInfo);
                                }
                                if (json.errorMsg) {
                                    yukon.ui.alertError(json.errorMsg);
                                }
                                yukon.ui.unbusy('.js-primary-route');
                            });
                        }
                    } else {
                        for (x in _primaryRouteIcons) {
                            var route = _primaryRouteIcons[x];
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            source.removeFeature(route);
                        }
                        for (x in _primaryRouteLines) {
                            var primaryRouteLine = _primaryRouteLines[x];
                            _map.removeLayer(primaryRouteLine);
                        }
                        _map.removeLayer(_primaryRouteIconLayer);
                        _updateZoom();
                        _checkToGoBackToDeviceOriginalStyle();
                    }
                });
                
                /** Gets the parent node from Network Manager **/
                $(document).on('click', '.js-parent-node', function() {
                    var parentNodeRow = $(this).closest('.switch-btn'),
                    wasChecked = parentNodeRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        if (_parentIcon != null) {
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            source.addFeature(_parentIcon);
                            _map.addLayer(_parentLine);
                            _map.addLayer(_parentIconLayer);
                            _updateZoom();
                            _makeCurrentDeviceStandOut();
                        } else {
                            var fc = yukon.fromJson('#geojson'),
                            feature = fc.features[0],
                            paoId = feature.id;
                            yukon.ui.busy('.js-parent-node');
                            $.getJSON('parentNode?' + $.param({ deviceId: paoId }))
                            .done(function (json) {
                                if (json.parent) {
                                    _loadParentData(json.parent);
                                }
                                if (json.errorMsg) {
                                    yukon.ui.alertError(json.errorMsg);
                                }
                                yukon.ui.unbusy('.js-parent-node');
                            });
                        }
                    } else {
                        //Remove parent and line
                        var source = _map.getLayers().getArray()[_tiles.length].getSource();
                        source.removeFeature(_parentIcon);
                        _map.removeLayer(_parentLine);
                        _map.removeLayer(_parentIconLayer);
                        _updateZoom();
                        _checkToGoBackToDeviceOriginalStyle();
                    }

                });
                
                /** Gets the nearby devices **/
                $(document).on('click', '.js-nearby', function() {
                    var nearbyRow = $(this).closest('.switch-btn'),
                    wasChecked = nearbyRow.find('.switch-btn-checkbox').prop('checked');
                    if (!wasChecked) {
                        _getNearbyDevices();
                    } else {
                        _removeNearbyDevices();
                    }
                });
                
                /** Gets the nearby devices **/
                $(document).on('change', '.js-miles', function() {
                    var nearbyRow = $('.js-nearby').closest('.switch-btn'),
                    wasChecked = nearbyRow.find('.switch-btn-checkbox').prop('checked');
                    if (wasChecked) {
                        _removeNearbyDevices();
                        _getNearbyDevices();
                    }
                });

                
                $(document).on('click', '.js-edit-coordinates', function() {
                    _map.addInteraction(_deviceDragInteraction);
                });
                
                $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {
                    _updateZoom();
                    // we if are doing an exit from the full screen, close any open pop-ups
                    if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                        $(".ui-dialog-content").dialog("close");
                        if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                            $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                        }
                    }
                });
                
                $("body").on("dialogopen", function (event, ui) {
                    if (document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement) {
                        $(event.target).closest('.ui-dialog').appendTo("div.ol-viewport");
                        $(event.target).closest('.ui-dialog').find('.ui-dialog-content').scroll( function (event) {
                            if ($(this).hasClass('menu-open')) {
                                $(this).removeClass('menu-open');
                            }
                            $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                        });
                        
                        // If a dialog box is opened while viewing the map in full screen map, disable double click and
                        // mouse wheel zoom interactions since they perform a zoom operation on map.
                        if(!_interactionsBlocked) {
                            _doubleClickZoomInteraction.setActive(false);
                            _mouseWheelZoomInteraction.setActive(false);
                            _interactionsBlocked = true;
                        }
                    }
                });
                
                $("body").on("dialogclose", function(event, ui) {
                    // If Double click and mouse wheel zoom interactions are disabled AND there is no dialog box open,
                    // enable those operations again.
                    if(($("body").find(".ui-dialog:visible").length === 0) && _interactionsBlocked) {
                        _doubleClickZoomInteraction.setActive(true);
                        _mouseWheelZoomInteraction.setActive(true);
                        _interactionsBlocked = false;
                    }
                });
                
                var interactions = _map.getInteractions();
                for(var i=0; i < interactions.getLength(); i++) {
                    var interaction = interactions.item(i);
                    if (interaction instanceof ol.interaction.DoubleClickZoom) {
                        _doubleClickZoomInteraction = interaction;
                    } else if (interaction instanceof ol.interaction.MouseWheelZoom) {
                        _mouseWheelZoomInteraction = interaction;
                    }
                }
                
            }
            
            _initialized = true;
        },
        
        getMap: function () {
            return _map;
        }

    };
 
    return mod;
})();
 
$(function () { yukon.map.network.init(); });
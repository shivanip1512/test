yukon.namespace('yukon.map.network');
 
/**
 * Module to handle the map network page
 * 
 * @module yukon.map.network
 * @requires yukon
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.map.network = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    //grey
    _parentColor = "#808080",
    //dark blue
    _routeColor = "#0000CC",
    //grey
    _focusRouteColor = "#808080",
    _largerScale = 1.3,
    
    //order layers should display, Icons > Parent > Primary Route > Neighbors
    _neighborsLayerIndex = 0,
    _primaryRouteLayerIndex = 1,
    _parentLayerIndex = 2,
    _iconLayerIndex = 3,
    
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
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    _deviceFocusIconLayer,
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
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
        iconsLayer.setZIndex(_iconLayerIndex);
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
    
    _removeDeviceFocusLayers = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        _deviceFocusIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _deviceFocusLines.forEach(function (line) {
            _map.removeLayer(line);
        });
        _map.removeLayer(_deviceFocusIconLayer);
        _deviceFocusIcons = [];
        _deviceFocusLines = [];
        _deviceFocusIconLayer = null;
        //set focus device back to normal style
        if (_deviceFocusCurrentIcon != null) {
            var normalStyle = _deviceFocusCurrentIcon.getStyle();
            normalStyle.getImage().setScale(1);
            _deviceFocusCurrentIcon.setStyle(normalStyle);
        }
    },
    
    _findFocusDevice = function(deviceId) {
        //check neighbor devices first
        var exists = _neighborIcons.filter(function (neighbor) { 
            return neighbor.getProperties().neighbor.device.paoIdentifier.paoId === deviceId;
        });
        //check primary route next
        if (exists.length === 0) {
            exists = _primaryRouteIcons.filter(function (route) { 
                return route.getProperties().routeInfo.device.paoIdentifier.paoId === deviceId;
            });
        }
        //check parent node
        if (exists.length === 0 && _parentIcon != null) {
            if (_parentIcon.getProperties().parent.device.paoIdentifier.paoId === deviceId) {
                exists.push(_parentIcon);
            }
        }
        //check nearby devices
        if (exists.length === 0) {
            exists = _nearbyIcons.filter(function (nearby) {
                return nearby.getProperties().pao.paoId === deviceId;
            });
        }
        //check focus devices
        if (exists.length === 0) {
            exists = _deviceFocusIcons.filter(function (device) {
                if (device.getProperties().routeInfo != null) {
                    return device.getProperties().routeInfo.device.paoIdentifier.paoId === deviceId;
                } else if (device.getProperties().neighbor != null) {
                    return device.getProperties().neighbor.device.paoIdentifier.paoId === deviceId;
                }
            });
        }
        //last just use original device
        if (exists.length === 0) {
            exists.push(_deviceIcon);
        }
        if (exists.length > 0) {
            var focusDevice = exists[0],
                largerStyle = focusDevice.getStyle().clone();
            largerStyle.getImage().setScale(_largerScale);
            focusDevice.setStyle(largerStyle);
            return focusDevice;
        }
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var fc = yukon.fromJson('#geojson'),
            source = _map.getLayers().getArray()[_tiles.length].getSource();
        if (deviceId != null) {
            var isFocusDevice = true,
                focusDevice = _findFocusDevice(deviceId),
                focusPoints = focusDevice.getGeometry().getCoordinates();
        }
            
       if (isFocusDevice) {
           var clonedFocusDevice = focusDevice.clone();
           _removeDeviceFocusLayers();
           _deviceFocusCurrentIcon = clonedFocusDevice;
           _deviceFocusIcons.push(clonedFocusDevice);
           source.addFeature(clonedFocusDevice);
       }
        
        for (x in neighbors) {
            var neighbor = neighbors[x],
            feature = neighbor.location.features[0],
            src_projection = fc.crs.properties.name,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ neighbor: neighbor });
            
            icon.setStyle(style);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }
            
            if (isFocusDevice) {
                _deviceFocusIcons.push(icon);
            } else {
                _neighborIcons.push(icon);
            }
            source.addFeature(icon);
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            if (isFocusDevice) {
                points.push(focusPoints);
            } else {
                points.push(_devicePoints);
            }

            var lineColor = yukon.mapping.getNeighborLineColor(neighbor.data.etxBand),
                lineThickness = yukon.mapping.getNeighborLineThickness(neighbor.data.numSamples);
            
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
            if (isFocusDevice) {
                _deviceFocusLines.push(layerLines);
            } else {
                _neighborLines.push(layerLines);
            }
            _map.addLayer(layerLines);
        }
        
        var allIcons = [];
        if (isFocusDevice) {
            allIcons.push.apply(allIcons, _deviceFocusIcons);
        } else {
            allIcons.push.apply(allIcons, _neighborIcons);
        }
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        iconsLayer.setZIndex(_iconLayerIndex);
        if (isFocusDevice) {
            _deviceFocusIconLayer = iconsLayer;
        } else {
            _neighborIconLayer = iconsLayer;
        }
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
        if (!isFocusDevice) {
            _makeCurrentDeviceStandOut();
        }
    },
    
    _loadNeighborData = function(neighbors) {
        _addNeighborDataToMap(null, neighbors);
    },
    
    _loadDeviceNeighbors = function(deviceId, neighbors) {
        _addNeighborDataToMap(deviceId, neighbors);
    },

    _addPrimaryRouteToMap = function(deviceId, routeInfo) {
        var fc = yukon.fromJson('#geojson'),
            source = _map.getLayers().getArray()[_tiles.length].getSource(),
            routeColor = _routeColor,
            routeLineWidth = 3.5;
        if (deviceId != null) {
            var isFocusDevice = true,
                focusDevice = _findFocusDevice(deviceId),
                focusPoints = focusDevice.getGeometry().getCoordinates(),
                routeColor = _focusRouteColor,
                routeLineWidth = 2.5;
        }
        _primaryRoutePreviousPoints = null;
        if (isFocusDevice) {
            _removeDeviceFocusLayers();
            _deviceFocusCurrentIcon = focusDevice;
        }

        for (x in routeInfo) {
            var route = routeInfo[x],
                feature = route.location.features[0],
                src_projection = fc.crs.properties.name,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ routeInfo: route });
            
            icon.setStyle(style);
            
            //the first device in the route will be the focus device
            if (x == 0) {
                var largerStyle = icon.getStyle().clone();
                largerStyle.getImage().setScale(_largerScale);
                icon.setStyle(largerStyle);
            }
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }

            if (isFocusDevice) {
                _deviceFocusIcons.push(icon);
            } else {
                _primaryRouteIcons.push(icon);
            }
            source.addFeature(icon);
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            if (_primaryRoutePreviousPoints != null) {
                points.push(_primaryRoutePreviousPoints);
            } else {
                if (isFocusDevice) {
                    points.push(focusPoints);
                } else {
                    points.push(_devicePoints);
                }
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
                    stroke: new ol.style.Stroke({ color: routeColor, width: routeLineWidth })
                })
            });
            
            layerLines.setZIndex(_primaryRouteLayerIndex);
            if (isFocusDevice) {
                _deviceFocusLines.push(layerLines);
            } else {
                _primaryRouteLines.push(layerLines);
            }
            _map.addLayer(layerLines);
        }
        
        var allIcons = isFocusDevice ? _deviceFocusIcons : _primaryRouteIcons,
                iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons})});
        iconsLayer.setZIndex(_iconLayerIndex);
        if (isFocusDevice) {
            _deviceFocusIconLayer = iconsLayer;
        } else {
            _primaryRouteIconLayer = iconsLayer;
        }
        _map.addLayer(iconsLayer);
        
        _updateZoom();
        
        if (!isFocusDevice) {
            _makeCurrentDeviceStandOut();
        }
    }
    
    _loadPrimaryRouteData = function(routeInfo) {
        _addPrimaryRouteToMap(null, routeInfo);
    },
    
    _loadDeviceRoute = function(deviceId, routeInfo) {
        _addPrimaryRouteToMap(deviceId, routeInfo);
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
            largerStyle.getImage().setScale(_largerScale);
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
        $.getJSON(yukon.url('/stars/mapNetwork/nearby?') + $.param({ deviceId: paoId, miles: miles }))
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
                /** Hide any cog dropdowns on zoom or map move **/
                _map.getView().on('change:resolution', function(ev) {
                    $('.dropdown-menu').css('display', 'none');
                 });
                 _map.on('movestart', function(ev) {
                     $('.dropdown-menu').css('display', 'none');
                  });
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
                        var geometry = feature.getGeometry(),
                            coord = geometry.getCoordinates(),
                            properties = feature.getProperties(),
                            parent = properties.parent,
                            neighbor = properties.neighbor,
                            routeInfo = properties.routeInfo,
                            nearby = properties.nearby;

                        if (parent != null) {
                            var parentData = parent.data;
                            yukon.mapping.displayCommonPopupProperties(parent);
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
                            _overlay.setPosition(coord);
                        } else if (neighbor != null) {
                            yukon.mapping.displayCommonPopupProperties(neighbor);
                            yukon.mapping.displayNeighborPopupProperties(neighbor);
                            _overlay.setPosition(coord);
                        } else if (routeInfo != null) {
                            yukon.mapping.displayCommonPopupProperties(routeInfo);
                            yukon.mapping.displayPrimaryRoutePopupProperties(routeInfo);
                            _overlay.setPosition(coord);
                        } else {
                            $('#parent-info').hide();
                            $('#neighbor-info').hide();
                            $('#route-info').hide();
                            if (feature.get('pao') != null) {
                                url = yukon.url('/tools/map/device/' + feature.get('pao').paoId + '/info');
                                $('#device-info').load(url, function() {
                                    if (nearby != null) {
                                        $('.js-distance').text(nearby.distance.distance.toFixed(4) + " ");
                                        $('.js-distance-display').show();
                                    }
                                    $('#device-info').show();
                                    $('#marker-info').show();
                                    _overlay.setPosition(coord);
                                    var deviceStatus = $('.js-status').text();
                                    yukon.mapping.updateDeviceStatusClass(deviceStatus);
                                });
                            }
                        }
                    } else {
                        var target = ev.originalEvent.target;
                        //check if user clicked on the cog, the error hide-reveal, or notes icon
                        var cog = $(target).closest('.js-cog-menu'),
                            error = $(target).closest('.hide-reveal-container'),
                            notesIcon = $(target).closest('.js-view-all-notes');
                        if (!cog.exists() && !error.exists() && !notesIcon.exists()) {
                            $('#marker-info').hide();
                        }
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
                    var paoId = $('#remove-pin').data("device-id"),
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
                        yukon.mapping.displayNeighborsLegend();
                        if (_neighborIcons.length > 0) {
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            _neighborLines.forEach(function (line) {
                                _map.addLayer(line);
                            });
                            _neighborIcons.forEach(function (icon) {
                                source.addFeature(icon);
                            });
                            _map.addLayer(_neighborIconLayer);
                            _updateZoom();
                            _makeCurrentDeviceStandOut();
                        } else {
                            var fc = yukon.fromJson('#geojson'),
                                feature = fc.features[0],
                                paoId = feature.id;
                            yukon.ui.busy('.js-neighbor-data');
                            $.getJSON(yukon.url('/stars/mapNetwork/neighbors?') + $.param({ deviceId: paoId }))
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
                        yukon.mapping.hideNeighborsLegend();
                        var source = _map.getLayers().getArray()[_tiles.length].getSource();
                        _neighborIcons.forEach(function (icon) {
                            source.removeFeature(icon);
                        });
                        _neighborLines.forEach(function (line) {
                            _map.removeLayer(line);
                        });
                        _map.removeLayer(_neighborIconLayer);
                        _removeDeviceFocusLayers();
                        _updateZoom();
                        _checkToGoBackToDeviceOriginalStyle();
                    }
                });
                
                /** Gets the neighbor data from Network Manager **/
                $(document).on('click', '.js-device-neighbors', function() {
                    var deviceId = $(this).data('deviceId'),
                        mapContainer = $('#map-network-container');
                    yukon.ui.block(mapContainer);
                    $.getJSON(yukon.url('/stars/mapNetwork/neighbors?') + $.param({ deviceId: deviceId }))
                    .done(function (json) {
                        if (json.neighbors) {
                            yukon.mapping.displayNeighborsLegend();
                            _loadDeviceNeighbors(deviceId, json.neighbors);
                        }
                        if (json.errorMsg) {
                            yukon.ui.alertError(json.errorMsg);
                        }
                        yukon.ui.unblock(mapContainer);
                        $('#marker-info').hide();
                    });
                });
                
                /** Gets the primary route from Network Manager **/
                $(document).on('click', '.js-primary-route', function() {
                    var primaryRouteRow = $(this).closest('.switch-btn'),
                    wasChecked = primaryRouteRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        if (_primaryRouteIcons.length > 0) {
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            _primaryRouteLines.forEach(function (line) {
                                _map.addLayer(line);
                            });
                            _primaryRouteIcons.forEach(function (icon) {
                                source.addFeature(icon);
                            });
                            _map.addLayer(_primaryRouteIconLayer);
                            _updateZoom();
                            _makeCurrentDeviceStandOut();
                        } else {
                            var fc = yukon.fromJson('#geojson'),
                                feature = fc.features[0],
                                paoId = feature.id;
                            yukon.ui.busy('.js-primary-route');
                            $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute?') + $.param({ deviceId: paoId }))
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
                        var source = _map.getLayers().getArray()[_tiles.length].getSource();
                        _primaryRouteIcons.forEach(function (icon) {
                            source.removeFeature(icon);
                        });
                        _primaryRouteLines.forEach(function (line) {
                            _map.removeLayer(line);
                        });
                        _map.removeLayer(_primaryRouteIconLayer);
                        _removeDeviceFocusLayers();
                        _updateZoom();
                        _checkToGoBackToDeviceOriginalStyle();
                    }
                });
                
                /** Gets the neighbor data from Network Manager **/
                $(document).on('click', '.js-device-route', function() {
                    var deviceId = $(this).data('deviceId'),
                        mapContainer = $('#map-network-container');
                    yukon.ui.block(mapContainer);
                    $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute?') + $.param({ deviceId: deviceId }))
                    .done(function (json) {
                        if (json.routeInfo) {
                            _loadDeviceRoute(deviceId, json.routeInfo);
                        }
                        if (json.errorMsg) {
                            yukon.ui.alertError(json.errorMsg);
                        }
                        yukon.ui.unblock(mapContainer);
                        $('#marker-info').hide();
                    });
                });
                
                /** Redirects to new device map network page **/
                $(document).on('click', '.js-device-map', function() {
                    var deviceId = $(this).data('deviceId');
                    window.location.href = yukon.url('/stars/mapNetwork/home?deviceId=' + deviceId);
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
                            $.getJSON(yukon.url('/stars/mapNetwork/parentNode?') + $.param({ deviceId: paoId }))
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
                        _removeDeviceFocusLayers();
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
                    // we if are doing an exit from the full screen, close any open pop-ups
                    if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                        $(".ui-dialog-content").dialog("close");
                        if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                            $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                        }
                        //adjust height back
                        $('#map-network-container').css('padding-bottom', '0px');
                    } else {
                        //adjust height for mapping buttons
                        $('#map-network-container').css('padding-bottom', '50px');
                    }
                    //close any popups
                    $('#marker-info').hide();
                    _updateZoom();
                    _map.updateSize();
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
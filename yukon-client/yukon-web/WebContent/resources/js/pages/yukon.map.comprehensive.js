yukon.namespace('yukon.map.comprehensive');
 
/**
 * Module to handle the Comprehensive Map page
 * 
 * @module yukon.map.comprehensive
 * @requires yukon
 * @requires yukon.mapping.js
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.map.comprehensive = (function () {
 
    var
    _initialized = false,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    //dark blue
    _routeColor = "#0000CC",
    
    //lines should go beneath icons
    _lineLayerIndex = 3,

    //Gateways should always display on top, followed by relays and finally meters
    _iconZIndex = 1,
    _relayZIndex = 2,
    _gatewayZIndex = 3,
    
    _primaryRoutePreviousPoints,
    _deviceFocusCurrentIcon,
    _deviceFocusIcons = [],
    _deviceFocusLines = [],
    _deviceFocusIconLayer,
    _allRoutesIcons = [],
    _allRoutesLines = [],
    _allGatewayIcons = [],
    _allRelayIcons = [],
    _largerScale = 1.3,
    _deviceScale = 0.8,
    _relayScale = 0.9,
    _gatewayScale = 1,
    
    _highlightedDevices = [],
        
    /** @type {Object.<number, {ol.Feature}>} - Map of pao id to feature for all device icons. */
    _icons = [], 
    
    _destProjection = yukon.mapping.getDestProjection(),
    _srcProjection = yukon.mapping.getSrcProjection(),
    
    /** @type {ol.interaction.DoubleClickZoom} - The openlayers interaction object for zoom on double click. */
    _doubleClickZoomInteraction,
    
    /** @type {ol.interaction.MouseWheelZoom} - The openlayers interaction object for zoom on scrolling mouse wheel. */
    _mouseWheelZoomInteraction,
    
    /** @type {boolean} - This is a boolean variable indicating if the _doubleClickZoomInteraction and _doubleClickZoomInteraction interactions are blocked */
    _interactionsBlocked = false,
    
    _styles = yukon.mapping.getStyles(),
    _tiles = yukon.mapping.getTiles(),
    
    _loadDevices = function(color, devices) {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
        for (x in devices.features) {
            var feature = devices.features[x],
                pao = feature.properties.paoIdentifier,
                src_projection = devices.crs.properties.name,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
                icon = new ol.Feature({ device: feature, pao: pao });
            
            icon.setId(feature.id);
            icon.setStyle(style);

            var currentStyle = icon.getStyle().clone(),
                image = currentStyle.getImage(),
                src = image.getSrc();
            
            currentStyle.setImage(new ol.style.Icon({ src: src, color: color, anchor:  [0.5, 1.0] }));
            
            icon.setStyle(currentStyle);
            
            if (src_projection === _destProjection) {
                icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
            } else {
                var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
                icon.setGeometry(new ol.geom.Point(coord));
            }
            
            _setScaleForDevice(icon);
        
            _icons.push(icon);
            source.addFeature(icon);
        }
        
        yukon.mapping.updateZoom(_map);
        
    },
    
    _findFocusDevice = function(deviceId, makeLarger) {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
            feature = source.getFeatureById(deviceId);
            if (feature && makeLarger) {
                _makeDeviceIconLarger(feature);
            }
            return feature;
    },
    
    _removeDeviceFocusLayers = function() {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
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
        yukon.mapping.hideNeighborsLegend();
    },
    
    _removeAllRoutesLayers = function() {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
        _allRoutesIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _allRoutesLines.forEach(function (line) {
            _map.removeLayer(line);
        });
        _allRoutesIcons = [];
        _allRoutesLines = [];
    },
    
    _removeAllGatewayIcons = function() {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
        _allGatewayIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _allGatewayIcons = [];
    },
    
    _removeAllRelayIcons = function() {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
        _allRelayIcons.forEach(function (icon) {
            source.removeFeature(icon);
        });
        _allRelayIcons = [];
    },
    
    _setScaleForDevice = function(feature) {
        var currentStyle = feature.getStyle().clone(),
            scale = _deviceScale,
            pao = feature.get('pao');
        currentStyle.setZIndex(_iconZIndex);
        //make larger and put on top layer if relay or gateway
        if (pao.paoType === 'RFN_RELAY') {
            scale = _relayScale;
            currentStyle.setZIndex(_relayZIndex);
        } else if (pao.paoType === 'RFN_GATEWAY' || pao.paoType === 'GWY800' || pao.paoType === 'VIRTUAL_GATEWAY') {
            scale = _gatewayScale;
            currentStyle.setZIndex(_gatewayZIndex);
        }
        currentStyle.getImage().setScale(scale);
        feature.setStyle(currentStyle);
    }
    
    //remove neighbors and route information from all icons and set back to initial scale
    _setIconsBack = function() {
        for (var i in _icons) {
            var icon = _icons[i];
            icon.unset("neighbor");
            icon.unset("routeInfo");
            _setScaleForDevice(icon);
        }
    },
    
    _makeDeviceIconLarger = function(icon) {
        var largerStyle = icon.getStyle().clone();
        largerStyle.getImage().setScale(_largerScale);
        icon.setStyle(largerStyle);
    },
    
    _addPrimaryRouteToMap = function(deviceId, routeInfo) {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
            focusDevice = _findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            routeLineWidth = 2.5;

        _primaryRoutePreviousPoints = null;
        _removeDeviceFocusLayers();
        _setIconsBack();
        _deviceFocusCurrentIcon = focusDevice;

        for (var x in routeInfo) {
            var route = routeInfo[x],
                feature = route.location.features[0],
                pao = feature.properties.paoIdentifier,
                style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
                icon = new ol.Feature({ routeInfo: route, pao: pao });
            icon.setId(feature.id);
            
            //check if device already exists on map...the first device will always be the original device so make the icon larger
            var deviceFound = _findFocusDevice(pao.paoId, x == 0);
            if (deviceFound) {
                icon = deviceFound;
                icon.set("routeInfo", route);
                icon.unset("neighbor");
            } else {
                icon.setStyle(style);
                _setScaleForDevice(icon);
                if (x == 0) {
                    _makeDeviceIconLarger(icon);
                }
                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
                
                _deviceFocusIcons.push(icon);
                source.addFeature(icon);
            }
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            if (_primaryRoutePreviousPoints != null) {
                points.push(_primaryRoutePreviousPoints);
            } else {
                points.push(focusPoints);
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
                    stroke: new ol.style.Stroke({ color: _routeColor, width: routeLineWidth })
                })
            });
            
            _deviceFocusLines.push(layerLines);
            _map.getLayers().insertAt(_lineLayerIndex, layerLines);
        }
        
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: _deviceFocusIcons})});
        _deviceFocusIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
    },
    
    _addNeighborDataToMap = function(deviceId, neighbors) {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource();
            focusDevice = _findFocusDevice(deviceId, true),
            focusPoints = focusDevice.getGeometry().getCoordinates(),
            clonedFocusDevice = focusDevice.clone();
            
        clonedFocusDevice.setStyle(focusDevice.getStyle().clone());
        clonedFocusDevice.unset("routeInfo");
        clonedFocusDevice.unset("neighbor");
            
        _removeDeviceFocusLayers();
        _setIconsBack();
        _deviceFocusCurrentIcon = clonedFocusDevice;
        _deviceFocusIcons.push(clonedFocusDevice);
        source.addFeature(clonedFocusDevice);

        for (var x in neighbors) {
            var neighbor = neighbors[x],
            feature = neighbor.location.features[0],
            pao = feature.properties.paoIdentifier;
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ neighbor: neighbor, pao: pao });
            icon.setId(feature.id);

            //check if neighbor already exists on map
            var neighborFound = _findFocusDevice(pao.paoId, false);
            if (neighborFound) {
                icon = neighborFound;
                icon.set("neighbor", neighbor);
                icon.unset("routeInfo");
            } else {
                icon.setStyle(style);
                _setScaleForDevice(icon);

                if (_srcProjection === _destProjection) {
                    icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
                } else {
                    var coord = ol.proj.transform(feature.geometry.coordinates, _srcProjection, _destProjection);
                    icon.setGeometry(new ol.geom.Point(coord));
                }
                
                _deviceFocusIcons.push(icon);
                source.addFeature(icon);
            }
            
            //draw line
            var points = [];
            points.push(icon.getGeometry().getCoordinates());
            points.push(focusPoints);

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
            
            _deviceFocusLines.push(layerLines);
            _map.getLayers().insertAt(_lineLayerIndex, layerLines);
        }
        
        var allIcons = [];
        allIcons.push.apply(allIcons, _deviceFocusIcons);
        var iconsLayer = new ol.layer.Vector({style: style, source: new ol.source.Vector({features: allIcons}), rendererOptions: {zIndexing: true, yOrdering: true}});
        _deviceFocusIconLayer = iconsLayer;
        _map.addLayer(iconsLayer);
    },
    
    _getIconLayer = function() {
        var iconLayer;
        _map.getLayers().forEach(function (layer) {
            if (layer.get('name') === 'icons') {
                iconLayer = layer;
            }
        });
        return iconLayer;
    },
    
    _addFeatureToMapAndArray = function(feature, iconArray) {
        var iconLayer = _getIconLayer(),
            source = iconLayer.getSource(),
            pao = feature.properties.paoIdentifier,
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'],
            icon = new ol.Feature({ pao: pao });
        icon.setId(feature.id);
    
        //check if device already exists on map
        var deviceFound = _findFocusDevice(pao.paoId, false);
        if (deviceFound) {
            icon = deviceFound;
        } else {
            icon.setStyle(style);
            _setScaleForDevice(icon);
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
    
    _showHideAllGateways = function() {
        var checked = $('.js-all-gateways').find(':checkbox').prop('checked');
        if (!checked) {
            _removeAllGatewayIcons();
        } else {
            $.ajax({
                url: yukon.url('/stars/comprehensiveMap/allGateways'),
                type: 'get'
            }).done( function(data) {
                for (var x in data.features) {
                    var feature = data.features[x];
                    _addFeatureToMapAndArray(feature, _allGatewayIcons);
                }
            });
        }
    },
    
    _showHideAllRelays = function() {
        var checked = $('.js-all-relays').find(':checkbox').prop('checked');
        if (!checked) {
            _removeAllRelayIcons();
        } else {
            $.ajax({
                url: yukon.url('/stars/comprehensiveMap/allRelays'),
                type: 'get'
            }).done( function(data) {
                for (var x in data.features) {
                    var feature = data.features[x];
                    _addFeatureToMapAndArray(feature, _allRelayIcons);
                }
            });
        }
    },
    
    _showHideAllRoutes = function() {
        var checked = $('.js-all-routes').find(':checkbox').prop('checked');
        if (!checked) {
            _removeAllRoutesLayers();
        } else {
            var mapContainer = $('#map-container'),
                collectionGroup = $('#collection-group').val();

            yukon.ui.block(mapContainer);
            $.getJSON(yukon.url('/stars/comprehensiveMap/allPrimaryRoutes?groupName=' + collectionGroup))
            .done(function (json) {
                for (var x in json) {
                    var route = json[x],
                        routeLineWidth = 2.5;

                    for (var x in route.features) {
                        var feature = route.features[x];
                        icon = _addFeatureToMapAndArray(feature, _allRoutesIcons);
                        
                        //draw line
                        if (_primaryRoutePreviousPoints != null) {
                            var points = [];
                            points.push(icon.getGeometry().getCoordinates());
                            points.push(_primaryRoutePreviousPoints)
                            
                            var layerLines = new ol.layer.Vector({
                                source: new ol.source.Vector({
                                    features: [new ol.Feature({
                                        geometry: new ol.geom.LineString(points),
                                        name: 'Line'
                                    })]
                                }),
                                style: new ol.style.Style({
                                    stroke: new ol.style.Stroke({ color: _routeColor, width: routeLineWidth })
                                })
                            });
                            
                            _allRoutesLines.push(layerLines);
                            _map.getLayers().insertAt(_lineLayerIndex, layerLines);
                        }
                        _primaryRoutePreviousPoints = icon.getGeometry().getCoordinates();

                    }

                }
                yukon.ui.unblock(mapContainer);
            });
        }
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            $(".js-selected-gateways").chosen({width: "400px", max_selected_options: 5});
            $(".js-selected-gateways").bind("chosen:maxselected", function () {
                var gatewayError = $('#tooManyGatewaysError').val();
                yukon.ui.alertError(gatewayError);
            }); 
            $(".js-selected-link-qualities").chosen({width: "200px"});
            
            /** Setup the openlayers map. */
            _map = new ol.Map({
                controls: [
                    new ol.control.Attribution(),
                    new ol.control.FullScreen({source: 'comprehensive-map-container'}),
                    new ol.control.Zoom() 
                ],
                layers: _tiles,
                target: 'comprehensive-map',
                view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], _srcProjection, _destProjection), zoom: 4 })
            });
            _destProjection = _map.getView().getProjection().getCode();
            _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
            
            /** Display marker info popup on marker clicks. */
            var _overlay = new ol.Overlay({ element: document.getElementById('marker-info'), positioning: 'bottom-center', stopEvent: false });
            _map.addOverlay(_overlay);
            _map.on('click', function(ev) {
                var paoFeature = _map.forEachFeatureAtPixel(ev.pixel, function(feature) {
                    if (feature && feature.get('pao') != null) {
                        return feature;
                    }
                });
                if (paoFeature) {
                    yukon.mapping.displayMappingPopup(paoFeature, _overlay);
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
            
            /** Change mouse cursor when over marker.  There HAS to be a css way to do this! */
            $(_map.getViewport()).on('mousemove', function(e) {
                var pixel = _map.getEventPixel(e.originalEvent),
                paoFeature = _map.forEachFeatureAtPixel(pixel, function(feature) {
                    if (feature && feature.get('pao') != null) {
                        return feature;
                    }
                });
                $('#' + _map.getTarget()).css('cursor', paoFeature ? 'pointer' : 'default');
            });
            
            $('.js-selected-gateways').on('change', function(evt, params) {
                var values = $(".js-selected-gateways").chosen().val();
                $('.js-filter-map').prop('disabled', values.length == 0);
            });
            
            $(document).on('click', '.js-filter-map', function (ev) {
                var form = $('#filter-form');
                $.ajax({
                    url: yukon.url('/stars/comprehensiveMap/filter'),
                    type: 'get',
                    data: form.serialize()
                }).done( function(data) {
                    if (data.errorMsg) {
                        yukon.ui.alertError(data.errorMsg);
                    } else {
                        //clear any existing features
                        var iconLayer = _getIconLayer(),
                            source = iconLayer.getSource();
                        if (_icons.length > 0) {
                            _icons.forEach(function(icon) {
                                if (source.getFeatureById(icon.get('pao').paoId)) {
                                    source.removeFeature(icon); 
                                }
                            });
                            _icons = [];
                        }
                        _removeDeviceFocusLayers();
                        _removeAllRoutesLayers();
                        _removeAllGatewayIcons();
                        _removeAllRelayIcons();
                        var map = data.map.mappedDevices;
                        Object.keys(map).forEach(function(key) {
                            var value = map[key];
                            _loadDevices(key, value);
                        });
                        $('#legend').empty();
                        for (var i = 0; i < data.map.legend.length; i++) {
                            var legendItem = data.map.legend[i];
                            $('#legend').append('<span class=small-circle style=margin-left:5px;margin-bottom:5px;background:' + legendItem.hexColor + '></span><span style=margin-left:5px;>' + legendItem.text + '</span>');
                        }
                        $('#legend').removeClass('dn');
                        //display devices cog
                        $('#filtered-devices').removeClass('dn');
                        $('.js-number-devices').html(data.map.totalDevices);
                        $('#collectionActionLink').attr('href', yukon.url(data.collectionActionRedirect));
                        $('#collection-group').val(data.collectionGroup);
                        _showHideAllGateways();
                        _showHideAllRelays();
                        _showHideAllRoutes();
                    }
                }).always(function () {
                    yukon.ui.unbusy('.js-filter-map');
                });
            });
            
            $(document).on('click', '.js-download', function () {
                var collectionGroup = $('#collection-group').val();
                window.location = yukon.url('/stars/comprehensiveMap/download?groupName=' + collectionGroup);
            });
            
            $("#findDevice").keyup(function(event) {
                if (event.keyCode === 13) {
                    var searchText = $('#findDevice').val();
                    //remove last highlighted devices
                    if (_highlightedDevices.length > 0) {
                        var iconLayer = _getIconLayer(),
                            source = iconLayer.getSource();
                        _highlightedDevices.forEach(function(device) {
                           source.removeFeature(device);
                        });
                        _highlightedDevices = [];
                    }
                    var searchError = $('.js-no-results-found');
                    searchError.addClass('dn');
                    if (searchText) {
                        $.ajax({
                            url: yukon.url('/stars/comprehensiveMap/search?searchText=' + searchText),
                            type: 'get',
                        }).done( function(data) {
                            if (data.paoIds) {
                                var foundADevice = false,
                                    iconLayer = _getIconLayer(),
                                    source = iconLayer.getSource();
                                data.paoIds.forEach(function(paoId) {
                                    var feature = source.getFeatureById(paoId);
                                    if (feature) {
                                        foundADevice = true;
                                        var clonedFeature = feature.clone(),
                                            largerStyle = clonedFeature.getStyle().clone(),
                                            circleStyle = new ol.style.Style({
                                            image: new ol.style.Circle({
                                                radius: 8,
                                                fill: new ol.style.Fill({color: _routeColor}),
                                                stroke: new ol.style.Stroke({color: 'black', width: 2}) 
                                            })
                                        });
                                        largerStyle.getImage().setScale(_largerScale);
                                        clonedFeature.setStyle([circleStyle, largerStyle]);
                                        _highlightedDevices.push(clonedFeature);
                                        source.addFeature(clonedFeature);
                                    }
                                });
                                if (foundADevice) { 
                                    var allCoordinates = [];
                                    _highlightedDevices.forEach(function(device) {
                                        var coordinates = device.getGeometry().getCoordinates();
                                        allCoordinates.push(coordinates);
                                    });
                                    _map.getView().fit(ol.extent.boundingExtent(allCoordinates), {'maxZoom': 16});
                                } else {
                                    searchError.removeClass('dn');
                                }
                            } else {
                                searchError.removeClass('dn');
                            }
                        });
                    }
                }
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-neighbors', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/neighbors') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    if (json.neighbors) {
                        _addNeighborDataToMap(deviceId, json.neighbors);
                        yukon.mapping.displayNeighborsLegend();
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                });
            });
            
            /** Gets the neighbor data from Network Manager **/
            $(document).on('click', '.js-device-route', function() {
                var deviceId = $(this).data('deviceId'),
                    mapContainer = $('#map-container');
                yukon.ui.block(mapContainer);
                $.getJSON(yukon.url('/stars/mapNetwork/primaryRoute') + '?' + $.param({ deviceId: deviceId }))
                .done(function (json) {
                    if (json.routeInfo) {
                        _addPrimaryRouteToMap(deviceId, json.routeInfo);
                    }
                    if (json.errorMsg) {
                        yukon.ui.alertError(json.errorMsg);
                    }
                    yukon.ui.unblock(mapContainer);
                    $('#marker-info').hide();
                });
            });
            
            /** Add an elevation layer to the map **/
            $(document).on('click', '.js-elevation-layer', function() {
                yukon.mapping.showHideElevationLayer(_map, $(this));
            });
            
            
            $(document).on('click', '.js-all-gateways', function () {
                _showHideAllGateways();
            });
            
            $(document).on('click', '.js-all-relays', function () {
                _showHideAllRelays();
            });

            /** Add all primary routes to the map **/
            $(document).on('change', '.js-all-routes', function() {
                _showHideAllRoutes();
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
                        var iconLayer = _getIconLayer(),
                            source = iconLayer.getSource();
                            feature = source.getFeatureById(paoId);
                        if (feature) {
                            source.removeFeature(feature);
                        }
                        var successMsg = $('#coordinatesDeletedMsg').val();
                        yukon.ui.alertSuccess(successMsg);
                        
                        $('#marker-info').hide();
                    },
                    error: function(xhr, status, error) {
                        var errorMsg = xhr.responseJSON.message;
                        yukon.ui.alertError(errorMsg);
                    }
                });
                
            });
                        
            $(document).on('webkitfullscreenchange mozfullscreenchange fullscreenchange MSFullscreenChange', function() {
                // we if are doing an exit from the full screen, close any open pop-ups
                if (!(document.fullScreen || document.mozFullScreen || document.webkitIsFullScreen || document.msFullscreenElement)) {
                    $(".ui-dialog-content").dialog("close");
                    if($("div.ol-viewport").find("ul.dropdown-menu:visible")) {
                        $("div.ol-viewport").find("ul.dropdown-menu:visible").hide();
                    }
                    //adjust height back
                    $('#comprehensive-map-container').css('padding-bottom', '0px');
                    $('#comprehensive-map-container').css('padding-top', '0px');
                } else {
                    //adjust height for mapping buttons
                    $('#comprehensive-map-container').css('padding-bottom', '220px');
                    $('#comprehensive-map-container').css('padding-top', '0px');
                }
                //close any popups
                $('#marker-info').hide();
                yukon.mapping.updateZoom(_map);
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
            
            _initialized = true;
        },
    };
 
    return mod;
})();
 
$(function () { yukon.map.comprehensive.init(); });
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
    _routeColor = "#1E66CC",
    
    _devicePoints = [],
    _parentIcon,
    _parentLine,
    _neighborIcons = [],
    _neighborLines = [],
    _primaryRouteIcons = [],
    _primaryRouteLines = [],
    _primaryRoutePreviousPoints,
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    /** @type {Object.<string, {ol.style.Style}>} - A cache of styles to avoid creating lots of objects using lots of memory. */
    _styles = { 
        'METER_ELECTRIC': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-elec-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_WATER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-water-grey.png'), anchor: [0.5, 1.0] }) }),
        'METER_GAS': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-meter-gas-grey.png'), anchor: [0.5, 1.0] }) }),
        'TRANSMITTER': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter-grey.png'), anchor: [0.5, 1.0] }) }),
        'RELAY': new ol.style.Style({ image: new ol.style.Icon({ src: yukon.url('/WebConfig/yukon/Icons/marker-relay-grey.png'), anchor: [0.5, 1.0] }) }),
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
            style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
            icon = new ol.Feature({ pao: pao });
            
            icon.setStyle(style);
            
        if (src_projection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        _devicePoints = icon.getGeometry().getCoordinates();
        
        source.addFeature(icon);
        
        _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
        _map.getView().setZoom(13);
    },
    
    _loadParentData = function(parent) {
        var fc = yukon.fromJson('#geojson');
        source = _map.getLayers().getArray()[_tiles.length].getSource(),
        feature = parent.location.features[0],
        src_projection = fc.crs.properties.name,
        style = _styles[feature.properties.icon] || _styles['GENERIC_GREY'];
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
                fill: new ol.style.Fill({ color: _parentColor, weight: 4 }),
                stroke: new ol.style.Stroke({ color: _parentColor, width: 2, lineDash: [10,10] })
            })
        });
        
        _parentLine = layerLines;
        _map.addLayer(layerLines);
        _map.getView().fitExtent(source.getExtent(), _map.getSize());
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
                    fill: new ol.style.Fill({ color: lineColor, weight: lineThickness }),
                    stroke: new ol.style.Stroke({ color: lineColor, width: lineThickness })
                })
            });
            
            _neighborLines.push(layerLines);
            _map.addLayer(layerLines);
        }
        _map.getView().fitExtent(source.getExtent(), _map.getSize());
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
                    fill: new ol.style.Fill({ color: _routeColor, weight: 4 }),
                    stroke: new ol.style.Stroke({ color: _routeColor, width: 2 })
                })
            });
            
            _primaryRouteLines.push(layerLines);
            _map.addLayer(layerLines);
        }
        _map.getView().fitExtent(source.getExtent(), _map.getSize());
    },
    
    _updateZoom = function() {
        var source = _map.getLayers().getArray()[_tiles.length].getSource();
        var features = source.getFeatures();
        if (features != null && features.length > 1) {
            _map.getView().fitExtent(source.getExtent(), _map.getSize());
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
                        new ol.control.FullScreen(),
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

                        if (parent != null) {
                            var parentData = parent.data;
                            $('.js-device').text(parent.device.name);
                            $('.js-type').text(parent.device.paoIdentifier.paoType);
                            $('.js-status').text(parent.statusDisplay);
                            $('.js-manufacturer').text(parentData.rfnIdentifier.sensorManufacturer);
                            $('.js-model').text(parentData.rfnIdentifier.sensorModel);
                            $('.js-serial-number').text(parentData.rfnIdentifier.sensorSerialNumber);
                            $('.js-node-sn').text(parentData.nodeSN);
                            $('.js-mac-address').text(parentData.nodeMacAddress);
                            $('.js-distance').text(parent.distanceDisplay);
                            $('#neighbor-info').hide();
                            $('#device-info').hide();
                            $('#route-info').hide();
                            $('#parent-info').show();
                        } else if (neighbor != null) {
                            var neighborData = neighbor.data;
                            $('.js-device').text(neighbor.device.name);
                            $('.js-type').text(neighbor.device.paoIdentifier.paoType);
                            $('.js-status').text(neighbor.statusDisplay);
                            $('.js-serial-number').text(neighborData.rfnIdentifier.sensorSerialNumber);
                            $('.js-neighbor-serialNumber').text(neighborData.serialNumber);
                            $('.js-address').text(neighborData.neighborAddress);
                            if (neighbor.commaDelimitedNeighborFlags != null) {
                                $('.js-flags').text(neighbor.commaDelimitedNeighborFlags);
                            }
                            $('.js-link-cost').text(neighborData.neighborLinkCost);
                            $('.js-num-samples').text(neighborData.numSamples);
                            $('.js-etx-band').text(neighborData.etxBand);
                            $('.js-distance').text(neighbor.distanceDisplay);
                            $('#parent-info').hide();
                            $('#device-info').hide();
                            $('#route-info').hide();
                            $('#neighbor-info').show();
                        } else if (routeInfo != null) {
                            $('.js-device').text(routeInfo.device.name);
                            $('.js-type').text(routeInfo.device.paoIdentifier.paoType);
                            $('.js-status').text(routeInfo.statusDisplay);
                            $('.js-node-sn').text(routeInfo.route.serialNumber);
                            $('.js-serial-number').text(routeInfo.route.rfnIdentifier.sensorSerialNumber);
                            //check if it's the last in the route then don't display the rest
                            var lastItem = _primaryRouteIcons[_primaryRouteIcons.length - 1];
                            var lastProperties = lastItem.getProperties();
                            var lastRoute = lastProperties.routeInfo;
                            if (lastRoute.device.name != routeInfo.device.name) {
                                $('.js-route-display').show();
                                $('.js-destination-address').text(routeInfo.route.destinationAddress);
                                $('.js-next-hop-address').text(routeInfo.route.nextHopAddress);
                                $('.js-total-cost').text(routeInfo.route.totalCost);
                                $('.js-hop-count').text(routeInfo.route.hopCount);
                                $('.js-route-flag').text(routeInfo.commaDelimitedRouteFlags);
                                $('.js-distance').text(routeInfo.distanceDisplay);
                            } else {
                                $('.js-route-display').hide();
                            }
                            $('#parent-info').hide();
                            $('#neighbor-info').hide();
                            $('#device-info').hide();
                            $('#route-info').show();
                        } else {
                            $('#parent-info').hide();
                            $('#neighbor-info').hide();
                            $('#route-info').hide();
                            url = yukon.url('/tools/map/device/' + feature.get('pao').paoId + '/info');
                            $('#device-info').load(url, function() {
                                var deviceStatus = $('.js-device-status').val();
                                $('.js-status').text(deviceStatus);
                                $('.js-status-display').show();
                                $('#device-info').show();
                            });
                        }
                        $('#marker-info').show();
                        _overlay.setPosition(coord);

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
                            for (x in _neighborIcons) {
                                var neighbor = _neighborIcons[x];
                                source.addFeature(neighbor);
                            }
                            for (x in _neighborLines) {
                                var neighborLine = _neighborLines[x];
                                _map.addLayer(neighborLine);
                            }
                            _map.getView().fitExtent(source.getExtent(), _map.getSize());
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
                        _updateZoom();
                    }
                });
                
                /** Gets the primary route from Network Manager **/
                $(document).on('click', '.js-primary-route', function() {
                    var primaryRouteRow = $(this).closest('.switch-btn'),
                    wasChecked = primaryRouteRow.find('.switch-btn-checkbox').prop('checked');
                    
                    if (!wasChecked) {
                        if (_primaryRouteIcons.length > 0) {
                            var source = _map.getLayers().getArray()[_tiles.length].getSource();
                            for (x in _primaryRouteIcons) {
                                var route = _primaryRouteIcons[x];
                                source.addFeature(route);
                            }
                            for (x in _primaryRouteLines) {
                                var routeLine = _primaryRouteLines[x];
                                _map.addLayer(routeLine);
                            }
                            _map.getView().fitExtent(source.getExtent(), _map.getSize());
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
                        _updateZoom();
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
                            _map.getView().fitExtent(source.getExtent(), _map.getSize());
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
                        _updateZoom();
                    }

                });
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
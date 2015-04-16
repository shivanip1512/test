yukon.namespace('yukon.assets.gateway.details');
 
/**
 * Module to handle the gateway details page.
 * 
 * @module yukon.assets.gateway.details
 * @requires yukon
 * @requires JQUERY
 * @requires OPEN_LAYERS
 */
yukon.assets.gateway.details = (function () {
 
    var
    _initialized = false,
    
    _text,
    
    /** {String} - The IANA timezone name. */
    _tz = jstz.determine().name(),
    
    /** @type {Number} - The gateway pao id. */
    _gateway,
    
    /** @type {ol.Map} - The openlayers map object. */
    _map = {},
    
    /** @type {string} - The default projection code of our map tiles. */
    _destProjection = 'EPSG:3857',
    
    /** @type {Array.<{ol.Layer.Tile|ol.layer.Group}>} - Array of tile layers for our map. */
    _tiles = [ 
        new ol.layer.Tile({ name: 'mqosm', source: new ol.source.MapQuest({ layer: 'osm' }) }),
        new ol.layer.Tile({ name: 'mqsat', source: new ol.source.MapQuest({ layer: 'sat' }), visible: false }),
        new ol.layer.Group({
            name: 'hybrid',
            layers: [
                new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'sat'}) }),
                new ol.layer.Tile({ source: new ol.source.MapQuest({layer: 'hyb'}) })
            ],
            visible: false
        })
    ],
    
    /** 
     * Gets pao location as geojson format and adds an icon feature to the vector layer for the map.
     */
    _loadIcon = function() {
        var source = _map.getLayers().getArray()[3].getSource(),
            fc = yukon.fromJson('#gateway-geojson'),
            feature = fc.features[0],
            src_projection = fc.crs.properties.name,
            pao = feature.properties.paoIdentifier,
            icon = new ol.Feature({ pao: pao });
        
        icon.setStyle(new ol.style.Style({ 
            image: new ol.style.Icon({ 
                src: yukon.url('/WebConfig/yukon/Icons/marker-transmitter.png'), 
                anchor: [0.5, 1.0] 
            }) 
        }));
        
        if (src_projection === _destProjection) {
            icon.setGeometry(new ol.geom.Point(feature.geometry.coordinates));
        } else {
            var coord = ol.proj.transform(feature.geometry.coordinates, src_projection, _destProjection);
            icon.setGeometry(new ol.geom.Point(coord));
        }
        
        source.addFeature(icon);
        
        _map.getView().setCenter(source.getFeatures()[0].getGeometry().getCoordinates());
        _map.getView().setZoom(13);
    },
    
    _updateSequences = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/' + _gateway + '/sequences')
        }).done(function (table) {
            $('#gw-sequence-table').html(table);
        }).always(function () {
            setTimeout(_updateSequences, 4000);
        });
    },
    
    _update = function () {
        $.ajax({
            url: yukon.url('/stars/gateways/' + _gateway + '/data')
        }).done(function (gw) {
            
            var info = $('#gw-info'), comm = $('#gw-comm'), collection = $('#gw-data-collection'), 
                data = gw.data, radios = gw.data.radios, percent = data.collectionPercent.toFixed(2);
            
            info.find('.js-gw-name').text(gw.name);
            info.find('.js-gw-sn').text(gw.rfnId.sensorSerialNumber);
            info.find('.js-gw-hw-version').text(data.hwVersion);
            info.find('.js-gw-sw-version').text(data.swVersion);
            info.find('.js-gw-us-version').text(data.usVersion);
            info.find('.js-gw-radio-version').text(data.radioVersion);
            info.find('.js-gw-release-version').text(data.releaseVersion);
            info.find('.js-gw-version-conflicts').text(data.versionConflicts)
            .toggleClass('empty-list', !data.versionConflict).toggleClass('error', data.versionConflict);
            info.find('.js-gw-app-mode').text(data.appMode)
            .toggleClass('error', !data.appModeNormal).toggleClass('success', data.appModeNormal);
            comm.find('.js-gw-admin').text(data.admin);
            comm.find('.js-gw-super-admin').text(data.superAdmin);
            comm.find('.js-gw-conn-type').text(data.connType);
            comm.find('.js-gw-ip').text(data.ip);
            comm.find('.js-gw-port').text(_text['port'].replace('{0}', data.port));
            comm.find('.js-gw-radios').empty();
            radios.forEach(function (item, idx, arr) {
                var radio = radios[idx],
                    timestamp = moment(radio.timestamp).tz(_tz).format(yg.formats.date.full_hm),
                    div = $('<div>').addClass('stacked').attr('title', timestamp);
                div.append('<div>' + radio.type + '</div>');
                div.append('<div>' + radio.mac + '</div>');
                $('.js-gw-radios').append(div);
            });
            comm.find('.js-gw-conn-state').toggleClass('green', data.connected).toggleClass('red', !data.connected);
            comm.find('.js-gw-conn-state-text').text(data.connectionStatusText);
            comm.find('.js-gw-last-comm').text(data.lastCommText)
            .toggleClass('green', data.lastComm == 'SUCCESSFUL')
            .toggleClass('red', data.lastComm == 'FAILED')
            .toggleClass('orange', data.lastComm == 'MISSED')
            .toggleClass('subtle', data.lastComm == 'UNKNOWN');
            comm.find('.js-gw-last-comm-time').text(moment(data.lastCommTimestamp).tz(_tz).format(yg.formats.date.full_hm));
            collection.find('.js-gw-data-completeness .progress-bar').css({ width: data.collectionPercent + '%' })
            .toggleClass('progress-bar-success', !data.collectionDanger && !data.collectionWarning)
            .toggleClass('progress-bar-warning', data.collectionWarning)
            .toggleClass('progress-bar-danger', data.collectionDanger);
            if (percent == 100) percent = 100;
            collection.find('.js-gw-data-completeness-percent').text(percent + '%');
            collection.find('.js-gw-schedule').text(data.schedule);
        }).always(function () {
            setTimeout(_update, 4000);
        });
    },
    
    mod = {
        
        /** Initialize this module. */
        init: function () {
            
            if (_initialized) return;
            
            _text = yukon.fromJson('#gateway-text');
            _gateway = $('#gateway-edit-popup').data('id');
            
            /** Initialize map if we have a location. */
            if ($('#gateway-location').data('hasLocation') === true) {
                
                /** Setup the openlayers map. */
                _map = new ol.Map({
                    controls: [
                        new ol.control.Attribution(),
                        new ol.control.FullScreen(),
                        new ol.control.Zoom() 
                    ],
                    layers: _tiles,
                    target: 'gateway-location',
                    view: new ol.View({ center: ol.proj.transform([-97.734375, 40.529458], 'EPSG:4326', 'EPSG:3857'), zoom: 4 })
                });
                _destProjection = _map.getView().getProjection().getCode();
                _map.addLayer(new ol.layer.Vector({ name: 'icons', source: new ol.source.Vector({ projection: _destProjection }) }));
                
                /** Load icon for location */
                _loadIcon();
                
                /** Change map tiles layer on tile button group clicks. */
                $('#map-tiles button').click(function (ev) {
                    $(this).siblings().removeClass('on');
                    $(this).addClass('on');
                    for (var i in _tiles) {
                        var layer = $(this).data('layer');
                        _tiles[i].set('visible', (_tiles[i].get('name') === layer));
                    }
                    
                });
            }
            
            /** Delete this gateway. */
            $(document).on('yukon:assets:gateways:delete', function (ev) {
                $('#delete-gw-form').submit();
            });
            
            
            /** Edit popup was opened, adjust test connection buttons. */
            $(document).on('yukon:assets:gateway:edit:load', function (ev) {
                yukon.assets.gateway.shared.adjustTestConnectionButtons();
            });
            
            /** Save button clicked on edit popup. */
            $(document).on('yukon:assets:gateway:save', function (ev) {
                
                var popup = $('#gateway-edit-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
                
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-settings-form').ajaxSubmit({
                    type: 'post',
                    success: function (result, status, xhr, $form) {
                        
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                        yukon.assets.gateway.shared.adjustTestConnectionButtons();
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
            });
            
            /** Test a connection for username and password. */
            $(document).on('click', '.js-conn-test-btn', function (ev) {
                
                var btn = $(this),
                    row = btn.closest('tr'),
                    ip = $('#gateway-settings-form .js-gateway-edit-ip').val(),
                    username = row.find('.js-gateway-edit-username').val(),
                    password = row.find('.js-gateway-edit-password').val();
                
                yukon.ui.busy(btn);
                $('.js-test-results').removeClass('success error').text('');
                
                $.ajax({
                    url: yukon.url('/stars/gateways/test-connection'),
                    data: {
                        ip: ip,
                        username: username,
                        password: password,
                        id: $('#gateway-edit-popup').data('id')
                    }
                }).done(function (result) {
                    if (result.success) {
                        $('.js-test-results').addClass('success').text(_text['login.successful']);
                    } else {
                        if (result.message) {
                            $('.js-test-results').addClass('error').text(result.message);
                        } else {
                            $('.js-test-results').addClass('error').text(_text['login.failed']);
                        }
                    }
                }).always(function () {
                    yukon.ui.unbusy(btn);
                });
                
            });
            
            /** Save button clicked on location popup, save the location. */
            $(document).on('yukon:assets:gateway:location:save', function (ev) {
                
                var popup = $('#gateway-location-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
            
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-location-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        popup.dialog('close');
                        window.location.href = window.location.href;
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
                
            });

            /** Save button clicked on schedule popup, save the schedule. */
            $(document).on('yukon:assets:gateway:schedule:save', function (ev) {
                
                var popup = $('#gateway-schedule-popup'),
                    btns = popup.closest('.ui-dialog').find('.ui-dialog-buttonset'),
                    primary = btns.find('.js-primary-action'),
                    secondary = btns.find('.js-secondary-action');
            
                yukon.ui.busy(primary);
                secondary.prop('disabled', true);
                
                popup.find('.user-message').remove();
                
                $('#gateway-schedule-form').ajaxSubmit({
                    success: function (result, status, xhr, $form) {
                        popup.dialog('close');
                    },
                    error: function (xhr, status, error, $form) {
                        popup.html(xhr.responseText);
                    },
                    complete: function () {
                        yukon.ui.unbusy(primary);
                        secondary.prop('disabled', false);
                    }
                });
                
            });
            
            _update();
            _updateSequences();
            
            _initialized = true;
        },
        
        getMap: function () {
            return _map;
        }

    };
 
    return mod;
})();
 
$(function () { yukon.assets.gateway.details.init(); });
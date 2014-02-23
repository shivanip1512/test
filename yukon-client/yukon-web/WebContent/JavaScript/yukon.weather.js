
yukon.namespace('yukon.weather');

yukon.weather = (function() {
    var
    _initialized = false, 

   _searchWeatherStationsAgainBtnClick = function() {
        jQuery("#weatherLocationSearchResults").fadeOut(50, function() {
            jQuery("#weatherLocationSearch").fadeIn(50);
        });
    },

    _newWeatherLocationBtnClick = function() {
        jQuery("#weatherLocationSearch").show();
        jQuery("#weatherLocationSearchResults").hide();
        var title = jQuery("#weatherStationSearchTitle").html();
        jQuery("#weatherStationDialog").dialog({minWidth:500, title:title, modal:true});
    },

    _searchWeatherStationsBtnClick = function() {
        jQuery("#findCloseStationsForm").ajaxSubmit({success: function(data) {
            jQuery("#weatherStationDialog").fadeOut(50, function () {
                jQuery(this).html(data).fadeIn(50);
                yukon.ui.elementGlass.hide(jQuery("#weatherStationDialog"));
            });
        }});
    },

    _saveWeatherStationBtnClick = function() {
        jQuery("#saveWeatherLocationForm").ajaxSubmit({success: function(data) {
            jQuery("#weatherStationDialog").fadeOut(50, function () {
                jQuery(this).html(data);
                if (jQuery("#dialogState").val() == 'done') {
                    jQuery("#weatherStationDialog").dialog('close');
                    _reloadWeatherStations();
                } else {
                    jQuery(this).fadeIn(50);
                }
            });
        }});
    },

    _reloadWeatherStations = function() {
        jQuery("#weatherLocationsLoading").fadeIn(150);
        jQuery("#weatherLocations").load("weatherLocationsTable", function() {
            jQuery("#weatherLocationsLoading").hide();
            jQuery("#weatherLocations").hide().fadeIn(150);
        });
    },

    mod = {
        init: function () {
            if (_initialized) {
                return;
            }

            jQuery("#weatherStationDialog")
                .on("click","#searchWeatherStationsAgain", _searchWeatherStationsAgainBtnClick)
                .on("click","#searchWeatherStations", _searchWeatherStationsBtnClick)
                .on("click","#saveWeatherStationBtn", _saveWeatherStationBtnClick);
            jQuery("#newWeatherLocationBtn")
                .click(_newWeatherLocationBtnClick);
            jQuery(document).on("submit", "#saveWeatherLocationForm", function (){return false;});

            _reloadWeatherStations();

            _initialized = true;
        },

        updateWeatherInputFields : function(metaData) {
            var metaDataObj = JSON.parse(metaData.value);
            var paoId = metaDataObj.paoId;

            if (metaDataObj.dispatchError) {
                jQuery("#dispatchError").show();
                jQuery(".f-drFormula-temperature-field").removeClass("error").addClass("disabled");
                jQuery(".f-drFormula-humidity-field").removeClass("error").addClass("disabled");
                jQuery(".f-drFormula-timestamp-field").removeClass("error").addClass("disabled");
            } else {
                jQuery("#dispatchError").hide();
            }

            if (metaDataObj.invalidPaoError) {
                // A weather location was probably deleted, reload table
                _reloadWeatherStations();
            }

            if (metaDataObj.humidity !== 'valid') {
                jQuery("#humidityField_"+paoId).addClass("disabled");
            } else {
                jQuery("#humidityField_"+paoId).removeClass("disabled");
            }

            if (metaDataObj.temperature !== 'valid') {
                jQuery("#temperatureField_"+paoId).addClass("disabled");
            } else {
                jQuery("#temperatureField_"+paoId).removeClass("disabled");
            }

            if (metaDataObj.timestamp !== 'valid') {
                jQuery("#timestampField_"+paoId).addClass("error").removeClass("disabled");
            } else {
                jQuery("#timestampField_"+paoId).removeClass("error disabled");
            }
        }
    };
    return mod;
}());

jQuery(function() {
    yukon.weather.init();
});
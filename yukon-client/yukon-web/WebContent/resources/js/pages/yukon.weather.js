yukon.namespace('yukon.weather');

/**
 * Module that manages the weather data
 * @module   yukon.weather
 * @requires JQUERY 
 * @requires JQUERY UI
 */
yukon.weather = (function() {
    var
    _initialized = false, 

    /** Handles search weather stations button clicked again event */
   _searchWeatherStationsAgainBtnClick = function() {
        $("#weatherLocationSearchResults").fadeOut(50, function() {
            $("#weatherLocationSearch").fadeIn(50);
        });
    },

    /** Handles new weather station button click event. */
    _newWeatherLocationBtnClick = function() {
        $("#weatherLocationSearch").show();
        $("#weatherLocationSearchResults").hide();
        var title = $("#weatherStationSearchTitle").html();
        $("#weatherStationDialog").dialog({minWidth:500, title:title, modal:true});
    },

    /** Handles search weather station button click event. */
    _searchWeatherStationsBtnClick = function() {
        $("#findCloseStationsForm").ajaxSubmit({success: function(data) {
            $("#weatherStationDialog").fadeOut(50, function () {
                $(this).html(data).fadeIn(50);
                yukon.ui.unblock($("#weatherStationDialog"));
            });
        }});
    },

    /** Saves a weather station */
    _saveWeatherStationBtnClick = function() {
        $("#dispatchError-popup").hide();
        $("#saveWeatherStationBtn").prop('disabled', true);
        $("#saveWeatherLocationForm").ajaxSubmit({
            success: function(data) {
                $("#weatherStationDialog").fadeOut(50, function () {
                    $(this).html(data);
                    if ($("#dialogState").val() == 'done') {
                        $("#weatherStationDialog").dialog('close');
                        _reloadWeatherStations();
                    } else {
                        $(this).fadeIn(50);
                    }
                });
                $("#saveWeatherStationBtn").prop('disabled', false);
            },
            error: function(data, xhr){
                $("#dispatchError-popup").show();
                $("#saveWeatherStationBtn").prop('disabled', false);
            }
        });
    },

    /** Reloads the weather stations */
    _reloadWeatherStations = function() {
        $("#weatherLocationsLoading").fadeIn(150);
        $("#weatherLocations").load("weatherLocationsTable", function() {
            $("#weatherLocationsLoading").hide();
            $("#weatherLocations").hide().fadeIn(150);
        });
    },

    /** Update the primary weather location */
    _updatePrimaryWeatherLocation = function() {
        var paoId = $(this).val();
        $.ajax({
            type: "POST",
            url: yukon.url('/admin/config/updatePrimaryWeatherLocation'),
            data: {
                paoId: paoId
            }
        });
       },

    mod = {
        init: function () {
            if (_initialized) {
                return;
            }

            $("#weatherStationDialog")
                .on("click","#searchWeatherStationsAgain", _searchWeatherStationsAgainBtnClick)
                .on("click","#searchWeatherStations", _searchWeatherStationsBtnClick)
                .on("click","#saveWeatherStationBtn", _saveWeatherStationBtnClick);
            $("#newWeatherLocationBtn")
                .click(_newWeatherLocationBtnClick);
            $(document).on("submit", "#saveWeatherLocationForm", function (){return false;});

            _reloadWeatherStations();

            $(document).on('change', '[name="primaryWeatherLocation"]', _updatePrimaryWeatherLocation);

            _initialized = true;
        },

        /** Updates the various weather parameter details
         *  @param {object} metaData - Json containing various weather parameter details.
         */
        updateWeatherInputFields : function(metaData) {
            var metaDataObj = JSON.parse(metaData.value);
            var paoId = metaDataObj.paoId;

            if (metaDataObj.dispatchError) {
                $("#dispatchError").show();
                $(".js-formula-temperature-field").removeClass("error").addClass("disabled");
                $(".js-formula-humidity-field").removeClass("error").addClass("disabled");
                $(".js-formula-timestamp-field").removeClass("error").addClass("disabled");
            } else {
                $("#dispatchError").hide();
            }

            if (metaDataObj.invalidPaoError) {
                // A weather location was probably deleted, reload table
                _reloadWeatherStations();
            }

            if (metaDataObj.humidity !== 'valid') {
                $("#humidityField_"+paoId).addClass("disabled");
            } else {
                $("#humidityField_"+paoId).removeClass("disabled");
            }

            if (metaDataObj.temperature !== 'valid') {
                $("#temperatureField_"+paoId).addClass("disabled");
            } else {
                $("#temperatureField_"+paoId).removeClass("disabled");
            }

            if (metaDataObj.timestamp !== 'valid') {
                $("#timestampField_"+paoId).addClass("error").removeClass("disabled");
            } else {
                $("#timestampField_"+paoId).removeClass("error disabled");
            }
        }
    };
    return mod;
}());

$(function() {
    yukon.weather.init();
});
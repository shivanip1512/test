    function resetLabel(label) {
        $('thermostatLabel').value = label;
    }
    
    // Static temps in fahrenheit
    var tempDefault = 72;
    var tempLowerLimit = 45;
    var tempUpperLimit = 88;
    
    function convertTemp(temp, currentUnit) {
        if (currentUnit == 'F') {
            // convert to celsius
            return Math.round((temp - 32) / 1.8);
        } else {
            // convert to fahrenheit
            return Math.round((temp * 1.8) + 32);
        }
    }
    
    function changeTemp(amount) {
    
        var tempField = $('temperature');
        
        var currentTemp = $F(tempField)
        
        if(currentTemp == ''){
            // No current temp value
            if(!tempField.disabled) {
                // field is enabled - set to default
            
                var unit = $F('temperatureUnit');
                if(amount > 0) {
                    // User clicked the up button - set to low temp
                    tempField.value = getConvertedTemp(tempLowerLimit, unit);
                } else {
                    // User clicked the down button - set to high temp
                    tempField.value = getConvertedTemp(tempUpperLimit, unit);
                }
                validateTemp();
            }
        
        } else {
            // There is a current temp value, increment or decrement it
            
            currentTemp = parseInt(currentTemp);
            
            var newTemp = currentTemp + amount;
            
            tempField.value = newTemp;
            
            validateTemp();
        }
    }
    
    function validateTemp(){
        
        var tempField = $('temperature');
        var currentTemp = $F(tempField);
        var currentTempUnit = $F('temperatureUnit');
        
        if(isNaN(currentTemp)){
            currentTemp = tempDefault;
        }
        
        // Convert current temp to fahrenheit if needed for validation
        var fTemp = getFahrenheitTemp(currentTemp, currentTempUnit);
        
        if(fTemp < tempLowerLimit) {
            fTemp = tempLowerLimit; 
        } else if(fTemp > tempUpperLimit) {
            fTemp = tempUpperLimit; 
        }
        
        // Convert current temp to celsius if needed
        fTemp = getConvertedTemp(fTemp, currentTempUnit);
        
        tempField.value = fTemp;
    }
    
    function setTempUnits(units){
    
        var tempUnitField = $('temperatureUnit');
        var currentTempUnit = $F(tempUnitField);

        var tempField = $('temperature');
        var currentTemp = $F(tempField);
        
        if(units == currentTempUnit){
            return;
        }
    
        if(!isNaN(currentTemp) && currentTemp != '') {
            var temp = convertTemp(currentTemp, currentTempUnit);
            tempField.value = temp;
        }

        tempUnitField.value = units;
        
        if('C' == units) {
            $('celsiusLink').hide();
            $('celsiusSpan').show();
            $('fahrenheitSpan').hide();
            $('fahrenheitLink').show();
            
        } else {
            $('celsiusLink').show();
            $('celsiusSpan').hide();
            $('fahrenheitSpan').show();
            $('fahrenheitLink').hide();
        }
    }
    
    function setMode(mode){
        $('mode').value = mode;
        
        $('coolArrow').hide();
        $('heatArrow').hide();
        $('emHeatArrow').hide();
        $('offArrow').hide();

        if('COOL' == mode) {
            $('coolArrow').show();
            $('temperature').setStyle({color: 'blue'});
            $('temperature').enable();
        } else if('HEAT' == mode) {
            $('heatArrow').show();
            $('temperature').setStyle({color: 'red'});
            $('temperature').enable();
        } else if('EMERGENCY_HEAT' == mode) {
            $('emHeatArrow').show();
            $('temperature').setStyle({color: 'red'});
            $('temperature').enable();
        } else {
            $('offArrow').show();
            $('temperature').setStyle({color: 'black'});
            $('temperature').disable();
        } 
    }
    
    function setFan(arrow, fan){
        $('fan').value = fan;

        $('autoArrow').hide();
        $('onArrow').hide();
        $(arrow).show();
    }
    
    function editName(){
        $('editName').toggle();
        $('thermostatName').toggle();
    }
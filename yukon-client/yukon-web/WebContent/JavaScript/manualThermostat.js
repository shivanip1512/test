    function resetLabel(label) {
        $('thermostatLabel').value = label;
    }
    
    // Static temps in fahrenheit
    var tempDefault = 72;
    var tempLowerLimit = 45;
    var tempUpperLimit = 88;
    
    var actualCurrentTemp = 72;
    
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
        var fTemp = getRawFahrenheitTemp(currentTemp, currentTempUnit);
        
        if(fTemp < tempLowerLimit) {
            fTemp = tempLowerLimit; 
        } else if(fTemp > tempUpperLimit) {
            fTemp = tempUpperLimit; 
        }
        
        // Convert current temp to celsius if needed
        fTemp = getRawConvertedTemp(fTemp, currentTempUnit);
        
        actualCurrentTemp = fTemp;
        tempField.value = Math.round(fTemp);
    }
    
    function setTempUnits(newUnit){
    
        var tempUnitField = $('temperatureUnit');
        var currentTempUnit = $F(tempUnitField);

        if(newUnit == currentTempUnit){
            return;
        }
    
        tempUnitField.value = newUnit;
        tempUnit = newUnit;
    
        convertFieldTemp('temperature', currentTempUnit, newUnit);
        
        if('C' == newUnit) {
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
    
    function convertFieldTemp(fieldId, currentUnit, newUnit){
        
        var field = $(fieldId);
        var currentTemp = actualCurrentTemp;
        
        if(!isNaN(currentTemp) && currentTemp != '') {
            currentTemp = getRawFahrenheitTemp(currentTemp, currentUnit);
            currentTemp = getRawConvertedTemp(currentTemp, newUnit);
            
            actualCurrentTemp = currentTemp;
            field.value = Math.round(currentTemp);
        }
        
    }
    
    function setMode(mode){
        $('mode').value = mode;
        
        $('autoArrowMode').hide();
        $('coolArrow').hide();
        $('heatArrow').hide();
        $('emHeatArrow').hide();
        $('offArrow').hide();

        if('AUTO' == mode) {
            $('autoArrowMode').show();
            $('temperature').setStyle({color: 'black'});
            $('temperature').enable();
        } else if('COOL' == mode) {
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
        $('circulateArrow').hide();
        $('onArrow').hide();
        $(arrow).show();
    }
    
    function editName(){
        $('editName').toggle();
        $('thermostatName').toggle();
    }
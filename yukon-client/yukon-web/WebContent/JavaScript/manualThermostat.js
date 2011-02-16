    function resetLabel(label) {
        $('thermostatLabel').value = label;
    }
    
    // Static temps in fahrenheit
    var tempDefault = 72;
    var tempLowerLimit = 50;
    var tempUpperLimit = 99;
    
    var actualCurrentTemp = 72;
    
    function changeTemp(amount) {
    
        if('OFF' == $('mode').value) {
            return;
        }
    
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
            currentTemp = getRawConvertedTemp(tempDefault, currentTempUnit);
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
        var tempUnitFieldRun = $('temperatureUnitRun');
        var currentTempUnit = $F(tempUnitField);

        if(newUnit == currentTempUnit){
            return;
        }
    
        tempUnitField.value = newUnit;
        tempUnitFieldRun.value = newUnit;
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
        var currentTemp = field.value;
        
        if(!isNaN(currentTemp) && currentTemp != '') {
            currentTemp = getRawFahrenheitTemp(currentTemp, currentUnit);
            currentTemp = getRawConvertedTemp(currentTemp, newUnit);
            
            actualCurrentTemp = currentTemp;
            field.value = Math.round(currentTemp);
        }
        
    }
    
    function setMode(mode, element){
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
        $('modeConfirm').innerHTML = element.innerHTML;
    }
    
    function setFan(arrow, fan, element){
        $('fan').value = fan;
        $('autoArrow').hide();
        $('circulateArrow').hide();
        $('onArrow').hide();
        $(arrow).show();
        $('fanConfirm').innerHTML = element.innerHTML;
    }
    
    function editName(){
        $('editName').toggle();
        $('thermostatName').toggle();
    }
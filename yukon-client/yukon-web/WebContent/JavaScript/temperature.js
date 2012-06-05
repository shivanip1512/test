Temperature = function(args){
    this.init = function(args){
        for(var key in args){
            //don't extend this class or overwrite any of our functions
            if(typeof(this[key]) != 'undefined' && typeof(this[key]) != 'function'){
                this[key] = args[key];
            }
        }
    };
    
    this.unit = 'F';
    this.degrees = 72.0;
    this.resolution = {'F': 1.0, 'C': 0.5};
    this.decimalPlaces = {'F': 0, 'C': 1};
    
    this.init(args);
    
    this.getF = function(){
        switch(this.unit){
        case 'F':
            return this.degrees;
        case 'C':
            var degreesCelsius = this.degrees;
            return (degreesCelsius / 5 * 9) + 32;
        default:
            return NaN;
        }
    };
    
    this.getC = function() {
        switch(this.unit){
        case 'F':
            var degreesFahrenheit = this.degrees;
            return (degreesFahrenheit - 32) / 9.0 * 5.0;
        case 'C':
            return this.degrees;
        default:
            return NaN;
        }
    };
    
    this.getResolution = function() {
        return this.resolution[this.unit];
    };
    
    this.setF = function(degreesFahrenheit){
        switch(this.unit){
        case 'F':
            this.degrees = degreesFahrenheit;
            return;
        case 'C':
            this.degrees = (degreesFahrenheit - 32) / 9.0 * 5.0;
            return;
        default:
            throw "Unsupported unit: " + this.unit;
        }
    };
    
    this.setC = function(degreesCelsius){
        switch(this.unit){
        case 'F':
            this.degrees = (degreesCelsius / 5 * 9) + 32;
            return;
        case 'C':
            this.degrees = degreesCelsius;
            return;
        default:
            throw "Unsupported unit: " + this.unit;
        }
    };
    
    this._getResolvedFahrenheitTemp = function() {
        var raw = this.getF();
        var alias = 1.0 / this.resolution['F'];
        
        return Math.round(raw * alias) / alias;
    };
    
    this._getResolvedCelsiusTemp = function() {
        var raw = this.getC();
        var alias = 1.0 / this.resolution['C'];
        
        return Math.round(raw * alias) / alias;
    };
    
    this.getResolvedTemp = function() {
        switch(this.unit) {
        case 'F':
            return this._getResolvedFahrenheitTemp();
        case 'C':
            return this._getResolvedCelsiusTemp();
        default:
            return NaN;
        }
    };
    
    this.sanitizedValue = function(toUnit) {
        switch(toUnit){
        case 'F':
            return parseFloat(this._getResolvedFahrenheitTemp().toFixed(this.decimalPlaces['F']));
        case 'C':
            return parseFloat(this._getResolvedCelsiusTemp().toFixed(this.decimalPlaces['C']));
        default:
            return parseFloat(this.getResolvedTemp().toFixed(this.decimalPlaces[this.unit]));
        }
    };
    
    this.sanitizedString = function(toUnit) {
        if(typeof(toUnit) == 'undefined'){
            toUnit = this.unit;
        }
        return this.sanitizedValue(toUnit).toFixed(this.decimalPlaces[toUnit]);
    };
    
    this.toFahrenheit = function(args) {
        if(typeof(args) == 'undefined'){
            args = {};
        }
        
        var opts = {
                degrees: this.getF(),
                unit: 'F',
                resolution: typeof(args.resolution) == 'undefined' ? this.resolution : args.resolution,
                decimalPlaces: typeof(args.decimalPlaces) == 'undefined' ? this.decimalPlaces : args.decimalPlaces
        };
        
        this.init(opts);
    };
    
    this.toCelsius = function(args) {
        if(typeof(args) == 'undefined'){
            args = {};
        }
        
        var opts = {
                degrees: this.getC(),
                unit: 'C',
                resolution: typeof(args.resolution) == 'undefined' ? this.resolution : args.resolution,
                decimalPlaces: typeof(args.decimalPlaces) == 'undefined' ? this.decimalPlaces : args.decimalPlaces
        };
        
        this.init(opts);
    };
    
    this.toUnit = function(unit){
        switch(unit){
        case 'F':
            this.toFahrenheit();
            break;
        case 'C':
            this.toCelsius();
            break;
        default:
            throw "Unsupported unit: [" + unit + "]";
        }
    };
    
    this.print = function(){
    	return this.sanitizedString() + " " + this.unit;
    };
    
    this.stepUp = function() {
        this.degrees += this.resolution[this.unit];
    };
    
    this.stepDown = function() {
        this.degrees -= this.resolution[this.unit];
    };
};
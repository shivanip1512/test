var CURRENT_TIME_INPUT = null;
var CURRENT_TEMP_INPUT = null;
var KEYUP = 38;
var KEYDOWN = 40;

Yukon.ThermostatScheduleEditor = {
        
    //These values should be set by the Initialization function
    _private: {
        upperHeatF: 0,
        lowerHeatF: 0,
        upperCoolF: 0,
        lowerCoolF: 0,
        secondsResolution: 0, //seconds
        secondsBetweenPeriods: 0, //minimum time between periods in seconds
        currentUnit: 'F',
        
        
        heatColor: {
            r: {start: 242, end: 242},
            g: {start: 150, end: 0},
            b: {start: 29, end: 29},
            a: {start: 1, end: 1}
        },
        
        coolColor: {
            r: {start: 64, end: 189},
            g: {start: 153, end: 220},
            b: {start: 255, end: 255},
            a: {start: 1, end: 1}
        }
    },
    
    //Initializes the object
    //args requires the following values:
    // upperHeatF           - floating point fahrenheit value
    // lowerHeatF           - floating point fahrenheit value
    // upperCoolF           - floating point fahrenheit value
    // lowerCoolF           - floating point fahrenheit value
    // secondsResolution    - integer seconds to snap time values to
    // secondsBetweenPeriods- integer minimum time between periods in seconds
    
    //optional args are:
    // currentUnit          - string - Current units to display the schedules in
    // heatColor            - object - {r:{start:[0..255], end:[0..255]}, g:{start:[0..255], end:[0..255]}, b:{start:[0..255], end:[0..255]}, a:{start:[0..255], end:[0..255]}}
    // coolColor            - object - {r:{start:[0..255], end:[0..255]}, g:{start:[0..255], end:[0..255]}, b:{start:[0..255], end:[0..255]}, a:{start:[0..255], end:[0..255]}}
    init: function(args) {
        Yukon.ThermostatScheduleEditor.initArgs(args);
        Yukon.ThermostatScheduleEditor.renderTime();
        Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor._private.currentUnit]();
       
        $$(".time input:text").each(function(input){
            input.observe('focus', Yukon.ThermostatScheduleEditor.showTimeSlider);
            input.observe('blur', Yukon.ThermostatScheduleEditor.blurTimeInput);
            input.observe('keydown', Yukon.ThermostatScheduleEditor.timeKeydown);
        });
        
        $$(".temp input:text").each(function(input){
            input.observe('focus', Yukon.ThermostatScheduleEditor.showTempSlider);
            input.observe('blur', Yukon.ThermostatScheduleEditor.blurTempInput);
            input.observe('keydown', Yukon.ThermostatScheduleEditor.tempKeydown);
        });
        
        TIME_SLIDER = new Control.Slider($("timeSlider").down('.handle'), $("timeSlider").down('.track'), {
            range: $R(0, 24*60),
            sliderValue: 0,
            onSlide: function(value, e) {
                CURRENT_TIME_INPUT.value = timeFormatter.formatTime(value, parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60));
            },
            onChange: function(value, e) {
                Yukon.ThermostatScheduleEditor.commitTimeValue(timeFormatter.formatTime(value, parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60)), CURRENT_TIME_INPUT);
                CURRENT_TIME_INPUT.select();    //Good ol' IE needs this.
            }
            
        });
        
        TEMP_SLIDER = new Control.Slider($("tempSlider").down('.handle'), $("tempSlider").down('.track'), {
            range: $R(this._private.lowerHeatF, this._private.upperHeatF),
            sliderValue: 72,
            onSlide: function(value) {
                if(Yukon.ThermostatScheduleEditor._private.currentUnit == 'C'){
                    CURRENT_TEMP_INPUT.value = Yukon.Thermostat.celsiusToSafeCelsius(value).toFixed(1);
                }else {
                    CURRENT_TEMP_INPUT.value = parseInt(value);
                }
            },
            onChange: function(value, e) {
                Yukon.ThermostatScheduleEditor.commitTempValue(value, CURRENT_TEMP_INPUT);
                CURRENT_TEMP_INPUT.select();    //Good ol' IE needs this.
            }
        });

        $$(".schedules input:text, .createSchedule input:text").each(function(input){
            input.observe('focus', function(event){
                this.writeAttribute("previousValue", this.value);
            });
        });
        
        $$(".tempControls input:radio").each(function(radio){
            radio.observe('click', function(e){
                //ajax preference
                var form = e.target.up('form');
                if(form){
                    var params = {temperatureUnit: this.value};
                    var accountIdInput = form.down('input[name=accountId]');
                    
                    if(accountIdInput){
                        params.accountId = accountIdInput.value;
                    }
                    
                    new Ajax.Request(form.readAttribute("action"), {
                                     method:"POST",
                                     parameters: params,
                                     onComplete: Yukon.ThermostatScheduleEditor[this.value]});
                }
            });
        });
        
        $$(".tempControlsNoUpdate input:radio").each(function(radio){
            radio.observe('click', function(e){
                Yukon.ThermostatScheduleEditor[this.value]();
            });
        });
        
        YEvent.observeSelectorClick(".send", function(e){
            //confirmation popup already shown at this point
            $$("form[name=sendSchedule] input[name=scheduleId]")[0].value = e.target.up("form").down("input[name=scheduleId]").value;
        });
        
        YEvent.observeSelectorClick(".delete", function(e){
            //confirmation popup already shown at this point, so hide it
            e.target.up(".popUpDiv").hide();
            $$("form[name=deleteSchedule] input[name=scheduleId]")[0].value = e.target.up(".popUpDiv").down("form input[name=scheduleId]").value;
        });
        
        YEvent.observeSelectorClick(".cancel", function(e){
            e.target.up(".popUpDiv").hide();
            e.target.up(".popUpDiv").down('form').select("input[initialValue]").each(function(input){
                input.value = input.readAttribute('initialValue');
            });
            Yukon.ThermostatScheduleEditor.renderTime();
            Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor._private.currentUnit]();
        });
        
        YEvent.observeSelectorClick(".copy", function(e){
            var form = $("editSchedule_"+e.target.up("form").down("input[name=scheduleId]").value);
             form.down("input[name=scheduleId]").value = -1;
             form.down("input[name=scheduleName]").value = form.down('input[name=copyName]').value;
             form.down("button.delete").hide();
             
             //change title
             form.down('.titleBar .title').innerHTML = form.down('input[name=copyTitle]').value;
        });
        
        YEvent.observeSelectorClick(".edit", function(e){
            var form = $("editSchedule_"+e.target.up("form").down("input[name=scheduleId]").value);
            form.down("input[name=scheduleId]").value = e.target.up("form").down("input[name=scheduleId]").value;
            form.down("input[name=scheduleName]").value = e.target.up("form").down("input[name=scheduleName]").value;
            if(form.down("button.delete")){
                form.down("button.delete").show();
            }
            
            //change title
            form.down('.titleBar .title').innerHTML = form.down('input[name=editTitle]').value;
        });
        
        //This is a very specific editing mode for default schedules on energy companies.
        //There is only ever 1 schedule per page, but we need to provide capacity for changing modes
        //since you cannot delete one of these schedules.
        YEvent.observeSelectorClick(".editDefaultSchedule", function(e){
            //show the second page of the wizard, select the 'current' mode
            var mode = e.target.up("form").down("input[name=thermostatScheduleMode]").value;
            var page = $$(".page_0")[0]; 
            page.down("input[value="+ mode +"]").checked = true;
            page.down("button.f_next").enable();
            
            //select the second page page.
            page.up(1).select(".schedule_editor").invoke('hide');
            page.up(1).down("."+ mode).show();
            Yukon.ui.wizard.nextPage(page);
            
            
            var id = e.target.up("form").down("input[name=scheduleId]").value;
            var name = e.target.up("form").down("input[name=scheduleName]").value;
            var form = $("form_"+e.target.up("form").down("input[name=scheduleId]").value);
            form.down("input[name=scheduleId]").value = id;
            form.down("input[name=scheduleName]").value = name;
            
            if(form.down("button.delete")){
                form.down("button.delete").hide();
            }
        });
        
        YEvent.observeSelectorClick(".save", function(e){
            e.stop();
            var form = null;
            if(e.target.up("#createSchedule")){
                var mode = $("createSchedule_body").down("input[name=defaultScheduleMode]:checked").value;
                form = e.target.up('.popUpDiv').down('.'+ mode +' form');
            }else{
                form = e.target.up('.popUpDiv').down('form');
            }
            Yukon.ThermostatScheduleEditor.prepForm(form);
            form.submit();
            return true;
        });
        
        $$(".page_0 input:radio").each(function(input){
            input.observe('click', function(e){
                e.target.up('.f_page').down('.f_next').enable();
            });
        });
        
        YEvent.observeSelectorClick(".create", function(e){
            //show type picker
            Yukon.ui.wizard.reset($("createSchedule_body"));
            return false;
        });
        
        YEvent.observeSelectorClick(".page_0 .f_next", function(e){
            var input = $("createSchedule_body").down("input[name=defaultScheduleMode]:checked")
            $("createSchedule_body").select(".schedule_editor").each(function(elem){
                if(!elem.hasClassName(input.value)){
                    elem.hide();
                    elem.removeClassName("active");
                }else{
                    elem.show();
                    elem.addClassName("active");
                }
            });
        });
        
        YEvent.observeSelectorClick(".default", function(e){
            //find 'recommended schedule in the create popup
            var ourForm = e.target.up(".popUpDiv").down('form');
            var mode = ourForm.down("input[name=thermostatScheduleMode]").value;
            var recForm = $("createSchedule").down('.'+ mode +' form');
            
            Yukon.ThermostatScheduleEditor.resetDefaults({ourForm:ourForm, recForm:recForm});
            
        });
        
        YEvent.observeSelectorClick(".createDefault", function(e){
            //find 'recommended schedule in the create popup
            var ourForm = $("createSchedule").down(".schedule_editor.active");
            var mode = ourForm.down("input[name=thermostatScheduleMode]").value;
            var recForm = $("createSchedule").down('.'+ mode +' form');
            
            Yukon.ThermostatScheduleEditor.resetDefaults({ourForm:ourForm, recForm:recForm});
            
        });
        
        $(document).observe('click', function(e) {
            //hide the sliders if we did NOT click on the slider or the inputs that spawn them
            if(e.target.up('.slider') == null && e.target.up('.time') == null && e.target.up('.temp') == null){
                Yukon.ThermostatScheduleEditor.hideTimeSlider();
                Yukon.ThermostatScheduleEditor.hideTempSlider();
            }
        });
        
        //show the schedules
        $$(".schedule").invoke('removeClassName', "vh");
        $$(".schedule_editor").invoke('removeClassName', "vh");
    },
    
    initArgs: function(args) {
        for(var key in args){
            this._private[key] = args[key];
         }
        Yukon.Thermostat.init(args);
    },
    
    resetDefaults: function(args){
        if(args.recForm != null){
            var days = args.ourForm.select(".day");
            var defaultDays = args.recForm.down(".day");
            for(var i=0; i<days.length; i++){
                var timeOfWeek = args.recForm.down("." + days[i].down("input[name=timeOfWeek]").value);
                
                //copy values over
                var times = days[i].select("input[name=secondsFromMidnight]");
                var defaultTimes = defaultDays.select("input[name=secondsFromMidnight]");
                for(var j=0; j<times.length; j++){
                    times[j].value = defaultTimes[j].getAttribute("defaultValue");
                }
                
                var heatTemps = days[i].select("input[name=heat_F]");
                var defaultHeatTemps = defaultDays.select("input[name=heat_F]");
                for(var j=0; j<heatTemps.length; j++){
                    heatTemps[j].value = defaultHeatTemps[j].getAttribute("defaultValue");
                }
                
                var coolTemps = days[i].select("input[name=cool_F]");
                var defaultCoolTemps = defaultDays.select("input[name=cool_F]");
                for(var j=0; j<coolTemps.length; j++){
                    coolTemps[j].value = defaultCoolTemps[j].getAttribute("defaultValue");
                }
            }
            
            //render times and temps
            Yukon.ThermostatScheduleEditor.renderTime();
            Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor._private.currentUnit]();
        }
    },
    
    prepForm: function(form) {
       //reconstruct the data object to submit
        var periodsArr = [];
       var periods = form.select(".period");
       
       for(var i=0; i<periods.length; i++) {
           var inputs = periods[i].select("input[name]");
           if(inputs.length > 0){
               periodData = {};
               for(var j=0; j<inputs.length; j++) {
                   periodData[inputs[j].readAttribute('name')] = inputs[j].value;
               }
               periodsArr.push(periodData);
           }
       }
       
       //var schedule = JSON.parse(form.down("input[name=schedules]").value);
       
       var schedule = {};
       var inputs = form.select("input[name]");
       for(var i=0; i<inputs.length; i++){
           schedule[inputs[i].readAttribute('name')] = inputs[i].value;
       }
       
       schedule.periods = periodsArr;
       schedule.scheduleName = form.down("input[name=scheduleName]").value;
       schedule.scheduleId = form.down("input[name=scheduleId]").value;
       form.down("input[name=schedules]").value = JSON.stringify(schedule);
    },
    
    tempKeydown: function(event){
        //don't bother processing
        if(event.keyCode != KEYDOWN && event.keyCode != KEYUP){
            return;
        }
        
        //handle up/down nudge
        var value = parseFloat(CURRENT_TEMP_INPUT.value);
        var start = 0;
        var end = 0;
        
        if(this.hasClassName("heat_F")) {
            start = Yukon.ThermostatScheduleEditor._private.lowerHeatF;
            end = Yukon.ThermostatScheduleEditor._private.upperHeatF;
        } else if(this.hasClassName("cool_F")) {
            start = Yukon.ThermostatScheduleEditor._private.lowerCoolF;
            end = Yukon.ThermostatScheduleEditor._private.upperCoolF;
        }
        
        if(Yukon.ThermostatScheduleEditor._private.currentUnit == 'C'){
            var c = Yukon.Thermostat.fahrenheitToCelsius(start);
            start = Yukon.Thermostat.halfIntegerAccuracy(c);
            c = Yukon.Thermostat.fahrenheitToCelsius(end);
            end = Yukon.Thermostat.halfIntegerAccuracy(c);
        }
        
        switch(event.keyCode){
        case KEYUP:
            if(Yukon.ThermostatScheduleEditor._private.currentUnit == 'C'){
                value = Yukon.Thermostat.halfIntegerAccuracy(value + 0.5);
            }else {
                value = value+1;
            }
            break;
        case KEYDOWN:
            if(Yukon.ThermostatScheduleEditor._private.currentUnit == 'C'){
                value = Yukon.Thermostat.halfIntegerAccuracy(value - 0.5);
            }else {
                value = value-1;
            }
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        
        if(value >= start && value <= end){
            if(Yukon.ThermostatScheduleEditor._private.currentUnit == 'C'){
                CURRENT_TEMP_INPUT.value = value.toFixed(1);
            }else{
                CURRENT_TEMP_INPUT.value = parseInt(value);
            }
            TEMP_SLIDER.setValue(value);
        }
        event.stop();
    },
    
    showTempSlider: function(event){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.hideTimeSlider();
        
        var startingValue = parseFloat(this.value);
        
        //determine range
        var start = 0;
        var end = 0;
        
        if(this.hasClassName("heat_F")) {
            start = Yukon.ThermostatScheduleEditor._private.lowerHeatF;
            end = Yukon.ThermostatScheduleEditor._private.upperHeatF;
        } else if(this.hasClassName("cool_F")) {
            start = Yukon.ThermostatScheduleEditor._private.lowerCoolF;
            end = Yukon.ThermostatScheduleEditor._private.upperCoolF;
        }
        
        var startLabel = "";
        var endLabel = "";
        if(_self._private.currentUnit == 'C'){
            var c = Yukon.Thermostat.fahrenheitToCelsius(start);
            start = Yukon.Thermostat.halfIntegerAccuracy(c);
            if(start < c){
                start += 0.5;
            }
            
            c = Yukon.Thermostat.fahrenheitToCelsius(end);
            end = Yukon.Thermostat.halfIntegerAccuracy(c);
            if(end > c){
                end -= 0.5;
            }
            TEMP_SLIDER.increment = 0.5;
            startLabel = start.toFixed(1);
            endLabel = end.toFixed(1);
        }else {
            TEMP_SLIDER.increment = 1;
            startLabel = Math.round(start);
            endLabel = Math.round(end);
        }
        
        CURRENT_TEMP_INPUT = this;
        TEMP_SLIDER.options.range.start = start;
        TEMP_SLIDER.options.range.end = end;
        TEMP_SLIDER.setValue(startingValue);
        
        $("tempSlider").down(".startLabel").innerHTML = startLabel;
        $("tempSlider").down(".endLabel").innerHTML = endLabel;
        $("tempSlider").show();
        
        var inputDimensions = this.getDimensions();
        var sliderDimensions = $("tempSlider").getDimensions();
        // 1/2 width of the targetted item + 1/2 width of slider
        var offsetLeft = -1 * ((sliderDimensions.width/2) - (inputDimensions.width/2));
        //height of the input plus the height of the chevron
        var offsetTop = inputDimensions.height + 16;
        
        $("tempSlider").clonePosition(this, {setWidth: false, setHeight: false, offsetLeft: offsetLeft, offsetTop: offsetTop});
    },
    
    hideTempSlider: function(event){
        $("tempSlider").hide();
    },
    
    blurTempInput: function(event) {
        Yukon.ThermostatScheduleEditor.commitTempValue(this.value, this);
    },
    
    commitTempValue: function(value, input){
        var _self = Yukon.ThermostatScheduleEditor;
        value = parseFloat(value);
        var value_C = null;
        var value_F = value;
        
        //determine range
        var start = 0;
        var end = 0;
                
        if(input.hasClassName("heat_F")) {
            start = Yukon.ThermostatScheduleEditor._private.lowerHeatF;
            end = Yukon.ThermostatScheduleEditor._private.upperHeatF;
        } else if(input.hasClassName("cool_F")) {
            start = Yukon.ThermostatScheduleEditor._private.lowerCoolF;
            end = Yukon.ThermostatScheduleEditor._private.upperCoolF;
        }
        
        //convert the value to fahrenheit scale
        if(_self._private.currentUnit == 'C'){
            //only .5 increments are allowed
            value_F = Yukon.Thermostat.celsiusToFahrenheit(value);
            input.value = Yukon.Thermostat.celsiusToSafeCelsius(value).toFixed(1);
        }else {
            input.value = parseInt(value_F);
        }
        
        if(value_F >= start && value_F <= end){
            //put value into REAL input
            input.adjacent("input:hidden")[0].value = value_F;
            if(input.up('.temp').hasClassName('heat')){
                input.up('.temp').setStyle({backgroundColor:Yukon.Thermostat.calcHeatColor(value_F)});
            }else if(input.up('.temp').hasClassName('cool')){
                input.up('.temp').setStyle({backgroundColor:Yukon.Thermostat.calcCoolColor(value_F)});
            }
            return true;
        }
        
        //revert to previousValue
        input.value = input.readAttribute('previousValue');
    },
    
    timeKeydown: function(event){
        //don't bother processing iff not the UP or DOWN key
        if(event.keyCode != KEYDOWN && event.keyCode != KEYUP){
            return;
        }
        
        //determine range
        var parent = this.up('.period');
        var start = 0;
        var end = ((24*60)-parseInt(Yukon.ThermostatScheduleEditor._private.secondsBetweenPeriods/60));  //11:45pm is the default
        var value = timeFormatter.parseTime(this.value);
                
        if(parent.previous('.period')) {
            start = parseInt(parent.previous('.period').down('.time input[name=secondsFromMidnight]').value);
            
            //round to the closest quarter hour (900 seconds = 15minutes)
            start = Math.ceil((start+Yukon.ThermostatScheduleEditor._private.secondsResolution)/60);
        }
        if(parent.next('.period')){
            end = parseInt(parent.next('.period').down('.time input[name=secondsFromMidnight]').value);
            
            //round to the closest quarter hour (900 seconds = 15minutes)
            end = Math.floor((end-Yukon.ThermostatScheduleEditor._private.secondsResolution)/60);
        }
        
        switch(event.keyCode){
        //Up arrow key
        case KEYUP:
            value += parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60);
            break;
        //Down arrow key
        case KEYDOWN:
            value -= parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60);
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        if(value >= start && value <= end){
            CURRENT_TIME_INPUT.value = timeFormatter.formatTime(value, parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60));
            
            //round to nearest 15
            TIME_SLIDER.setValue(value);
        }
        event.stop();
    },
    
    showTimeSlider: function(event){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.hideTempSlider();
        
        var startingValueSeconds = parseInt(this.adjacent('input[name=secondsFromMidnight]')[0].value);
        var input = event.currentTarget;
        
        //determine range
        var parent = this.up('.period');
        var startSeconds = 0;
        var endSeconds = 24*60*60;
        
        var previous = parent.previous('.period');
        if(previous) {
            var prevInput = previous.down('.time input[name=secondsFromMidnight]');
            if(prevInput){
                startSeconds = parseInt(prevInput.value);
            }
        }
        
        var next = parent.next('.period');
        if(next){
            var nextInput = next.down('.time input[name=secondsFromMidnight]');
            if(nextInput){
                endSeconds = parseInt(nextInput.value);
            }
        }
        
        if(startSeconds != 0){
            startSeconds = Math.ceil((startSeconds+_self._private.secondsBetweenPeriods)/60)*60;    //round to the nearest interval
        }
        endSeconds = Math.floor((endSeconds+1-_self._private.secondsBetweenPeriods)/60)*60;

        CURRENT_TIME_INPUT = this;
        TIME_SLIDER.options.range.start = startSeconds/60;
        TIME_SLIDER.options.range.end = endSeconds/60;
        
        TIME_SLIDER.setValue(startingValueSeconds/60);
        
        $("timeSlider").down(".startLabel").innerHTML = timeFormatter.formatTime(TIME_SLIDER.options.range.start, parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60));
        $("timeSlider").down(".endLabel").innerHTML = timeFormatter.formatTime(TIME_SLIDER.options.range.end, parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60));
        $("timeSlider").show();
        
        var inputDimensions = this.getDimensions();
        var sliderDimensions = $("timeSlider").getDimensions();
        // 1/2 width of the targetted item + 1/2 width of slider
        var offsetLeft = -1 * ((sliderDimensions.width/2) - (inputDimensions.width/2));
        //height of the input plus the height of the chevron
        var offsetTop = inputDimensions.height + 16;
        $("timeSlider").clonePosition(this, {setWidth: false, setHeight: false, offsetLeft: offsetLeft, offsetTop: offsetTop});
    },
    
    hideTimeSlider: function(event){
        $("timeSlider").hide();
    },
    
    blurTimeInput: function(event) {
        Yukon.ThermostatScheduleEditor.commitTimeValue(this.value, this);
    },
    
    commitTimeValue: function(value, input){
        var curr = timeFormatter.parseTime(value)*60;
        if(curr != -1){
            //check bounds
            var parent = input.up('.period');
            var start = -1;
            var end = ((24*60)+1-parseInt(Yukon.ThermostatScheduleEditor._private.secondsBetweenPeriods/60))*60;
                    
            var previous = parent.previous('.period');
            if(previous) {
                var previousInput = previous.down('.time input[name=secondsFromMidnight]');
                if(previousInput){
                    start = parseInt(previousInput.value);
                }
            }
            
            var next = parent.next('.period');
            if(next){
                var nextInput = next.down('.time input[name=secondsFromMidnight]');
                if(nextInput){
                    end = parseInt(nextInput.value);
                }
            }
            
            //allow the user to set the start time to 12:00am

            if((start < curr) && (curr < end)){
                //set seconds from midnight value
                input.adjacent('input[name=secondsFromMidnight]')[0].value = timeFormatter.parseTime(input.value)*60;
                
                //get a nicely formatted time in case the user inputs some shorthand value such as '4pm'
                input.value = timeFormatter.formatTime(timeFormatter.parseTime(input.value), parseInt(Yukon.ThermostatScheduleEditor._private.secondsResolution/60));
                input.writeAttribute('previousValue', input.value);
                return true;
            }
        }
        //revert to previousValue
        input.value = input.readAttribute('previousValue');
    },
    
    celsius: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self._private.currentUnit = 'C';
        $$(".temp .value").each(function(elem){
            var f = parseFloat(elem.next("input:hidden").value);
            var c = Yukon.Thermostat.fahrenheitToCelsius(f);
            var rounded = Yukon.Thermostat.halfIntegerAccuracy(c);
            elem.innerHTML = rounded.toFixed(1);
        });
        $$(".temp input:text").each(function(elem){
            var f = parseFloat(elem.next("input:hidden").value);
            var c = Yukon.Thermostat.fahrenheitToCelsius(f);
            var rounded = Yukon.Thermostat.halfIntegerAccuracy(c);
            elem.value = rounded.toFixed(1);
        });
        $$(".temp, .tempLabel").each(function(elem){
            elem.removeClassName('F');
            elem.addClassName('C');
        });
        $("tempSlider").select(".tempHolder").each(function(elem){
            elem.removeClassName('F');
            elem.addClassName('C');
        });
        $$("input[name=temperatureUnit]").each(function(elem){
            elem.value = "C";
        });
        _self.calcTempColor();
    },
    
    C: function(){
        Yukon.ThermostatScheduleEditor.celsius();
    },
    
    fahrenheit: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self._private.currentUnit = 'F';
        $$(".temp .value").each(function(elem){
            elem.innerHTML = Math.round(elem.next("input:hidden").value);
        });
        $$(".temp input:text").each(function(elem){
            elem.value = Math.round(elem.next("input:hidden").value);
        });
        $$(".temp").each(function(elem){
            elem.removeClassName('C');
            elem.addClassName('F');
        });
        $("tempSlider").select(".tempHolder").each(function(elem){
            elem.removeClassName('C');
            elem.addClassName('F');
        });
        $$("input[name=temperatureUnit]").each(function(elem){
            elem.value = "F";
        });
        _self.calcTempColor();
    },
    
    F: function(){
        Yukon.ThermostatScheduleEditor.fahrenheit();
    },
    
    renderTime: function(){
        $$("input[name=secondsFromMidnight]").each(function(elem){
            elem.adjacent("span.time").each(function(span){
                span.innerHTML =timeFormatter.formatTime(elem.value/60);
            });
            
            elem.adjacent("input:text").each(function(input){
                input.value =timeFormatter.formatTime(elem.value/60);
            });
        });
    },
    
    calcTempColor: function(){
        
        $$(".temp input[type=hidden]").each(function(elem){
            if(elem.up('.temp').hasClassName('heat')){
                var temp = parseInt(elem.value);
                elem.up(".temp").setStyle({backgroundColor:Yukon.Thermostat.calcHeatColor(temp)});
            } else {
                var temp = parseInt(elem.value);
                elem.up(".temp").setStyle({backgroundColor:Yukon.Thermostat.calcCoolColor(temp)});
            }
        });
    },
    
    _tests: {
        conversionTest: function(){
            var fail = [];
            var c = 0;
            while(c<50) {
                //convert to fahrenheit
                var f = Yukon.Thermostat.celsiusToFahrenheit(c);
                
                //convert back to celsius and round like we do on display and validate
                
                //This is what we will store in the DB
                var d = Math.round(f);
                
                //This is what will come back out from the DB when converted to Celsius
                var c_prime = Yukon.Thermostat.fahrenheitToCelsius(d);
                var rounded = Yukon.Thermostat.halfIntegerAccuracy(c_prime);
                c_final = parseFloat(rounded.toFixed(1));
                
                if (c_final != c) {
                    debug("Fail! input["+ c +"] db value["+ d +"] -> back to Celsius -> ["+ c_final +"]     (f actual ["+ f +"] c_prime ["+ c_prime + "] rounded["+ rounded + "]");
                }else{
                    debug("pass");
                }
                
                c += 0.5;
            }
        }
    }
}

Yukon.ThermostatManualEditor = {
    _private: {
        currentUnit: 'F',
        increment: {C: 0.5, F:1}
    },
    
    init: function(args){
        _self = Yukon.ThermostatManualEditor;
        
        for(var key in args){
            this._private[key] = args[key];
        }

        //prep schedule editor data
        Yukon.Thermostat.init(args);
        
        $$(".manualThermostat .state").invoke('observe', 'click', _self.changeFanState);
        $$(".manualThermostat .mode").invoke('observe', 'click', _self.changeThermostatMode);
        $$(".temperatureAdjust .up").invoke('observe', 'click', _self.temperatureUp);
        $$(".temperatureAdjust .down").invoke('observe', 'click', _self.temperatureDown);
        $$(".temperatureUnit .unit").invoke('observe', 'click', _self.changeUnit);
        $$("input[name=temperature_display]").invoke('observe', 'blur', _self.onBlurTemperatureDisplay);
        $$("input[name=temperature_display]").invoke('observe', 'focus', _self.onFocusTemperatureDisplay);
        $$("#sendSettingsSubmit").invoke('observe', 'click', _self.prepForm);
        $$(".closePopup").invoke('observe', 'click', function(event){this.up(".popUpDiv").hide();});
        $$(".editLabel, .cancelLabelEdit").invoke('observe', 'click', _self.toggleLabelEditor);
        
        _self.renderTemperature();
        _self.renderOtherTemperatures(_self._private.currentUnit);
    },
    
    toggleLabelEditor: function(){
        $('editName').toggle();
        $('thermostatName').toggle();
    },
    
    changeUnit: function(event) {
        Yukon.ui.exclusiveSelect(event.target);
        var unit = event.target.readAttribute('unit');
        Yukon.ThermostatManualEditor._private.currentUnit = unit;
        Yukon.ThermostatManualEditor.renderTemperature();
        Yukon.ThermostatManualEditor.renderOtherTemperatures(unit);
    },
    
    changeFanState: function(event) {
        Yukon.ui.exclusiveSelect(event.target);
        event.target.up(".manualThermostat").down("input[name=fan]").value = event.target.readAttribute("state");
    },
    
    changeThermostatMode: function(event) {
        Yukon.ui.exclusiveSelect(event.target);
        Yukon.ThermostatManualEditor.sanitizeInputTemperature(event.target.up(".manualThermostat"));
        event.target.up(".manualThermostat").down("input[name=mode]").value = event.target.readAttribute("mode");
        Yukon.ThermostatManualEditor.renderTemperature();
    },
    
    onBlurTemperatureDisplay: function(event) {
        var widget = event.target.up(".manualThermostat");
        var mode = widget.down(".thermostatModes .selected").readAttribute("mode");
        if(Yukon.Thermostat.isValidTemperatureF(parseFloat(this.value), Yukon.ThermostatManualEditor._private.currentUnit, mode)){
            Yukon.ThermostatManualEditor.adjustInputTemperature({
                widget: widget,
                adjustment: 0
            });
            Yukon.ThermostatManualEditor.renderTemperature();
        }else{
            this.value = this.readAttribute("previousValue");
        }
    },
    
    onFocusTemperatureDisplay: function(event) {
        this.writeAttribute("previousValue", this.value);
    },
    
    prepForm: function(event){
        var popup = $(event.target.readAttribute("popup_id"));
        var widget = event.target.up(".manualThermostat");
        
        popup.select(".unit").invoke('hide');
        popup.select("." + Yukon.ThermostatManualEditor._private.currentUnit).invoke('show');
        
        popup.select("input:hidden").each(function(input){
            var name = input.readAttribute("name");
            var source = widget.down("input[name="+ name +"]");
            
            if(source){
                //not really true for the checkbox, but we overwrite it down below
                input.value = source.value;
            
                if(widget.down("."+ name +".selected")){
                    popup.down("."+ name +"Confirm").innerHTML = widget.down("."+ name +".selected").innerHTML; 
                }else if(widget.down("input[type=text]."+ name +"")){
                    popup.down("."+ name  +"Confirm").innerHTML = widget.down("."+ name).value;
                }else if(widget.down("input[type=checkbox]."+ name +"")){
                    popup.down("."+name).down("." + widget.down("."+ name).checked).show();
                    popup.down("."+name).down("." + !widget.down("."+ name).checked).hide();
                    input.value = source.checked;
                }
            }
        });
        
        //send the actual requested value for logging
        popup.down("input[name=temperature]").value = widget.down("input[name=temperature_display]").value;
        popup.down("input[name=temperatureUnit]").value = Yukon.ThermostatManualEditor._private.currentUnit;
        
        popup.show();
    },

    temperatureUp: function(event) {
        Yukon.ThermostatManualEditor.adjustInputTemperature({
            widget: event.target.up(".manualThermostat"),
            adjustment: _self._private.increment[Yukon.ThermostatManualEditor._private.currentUnit]
        });
        Yukon.ThermostatManualEditor.renderTemperature();
    },
    
    temperatureDown: function(event) {
        Yukon.ThermostatManualEditor.adjustInputTemperature({
            widget: event.target.up(".manualThermostat"),
            adjustment: -1 * (_self._private.increment[Yukon.ThermostatManualEditor._private.currentUnit])
        });
        Yukon.ThermostatManualEditor.renderTemperature();
    },
    
    sanitizeInputTemperature: function(widget) {
        //snap to limits - most thermostats have different limits based on the HEAT/COOL mode
        var actualTemperature = widget.down("input[name=temperature]");
        var mode = widget.down(".thermostatModes .selected").readAttribute("mode");
        actualTemperature.value = Yukon.Thermostat.toValidTemperatureF(parseFloat(actualTemperature.value), 'F', mode);
    },
    
    adjustInputTemperature: function(args){
        var _self = Yukon.ThermostatManualEditor;
        var temperatureDisplay = args.widget.down("input[name=temperature_display]");
        var actualTemperature = args.widget.down("input[name=temperature]");
        var mode = args.widget.down(".thermostatModes .selected").readAttribute("mode");

        if(Yukon.Thermostat.isValidTemperatureF(parseFloat(temperatureDisplay.value)+args.adjustment, _self._private.currentUnit, mode)){
            actualTemperature.value = Yukon.Thermostat.toValidTemperatureF(parseFloat(temperatureDisplay.value)+args.adjustment, _self._private.currentUnit, mode);
        }
    },
    
    renderTemperature: function(){
        $$(".manualThermostat input[name=temperature]").each(function(temperatureInput){
            var exactTemperature = parseFloat(temperatureInput.value);
            var displayInput = temperatureInput.up().down("input[name=temperature_display]");
            var mode = displayInput.up(".manualThermostat").down(".thermostatModes .selected").readAttribute("mode");

            //bounds check the temperature
            exactTemperature = Yukon.Thermostat.toValidTemperatureF(exactTemperature, 'F', mode);
            //sanitize the value for our current temperature unit
            displayInput.value = Yukon.Thermostat.convertFTemperatureToDisplaySafeTemperature(exactTemperature, Yukon.ThermostatManualEditor._private.currentUnit);
                
            //show the color
            switch(mode){
            case 'EMERGENCY_HEAT':
            case 'HEAT':
                displayInput.setStyle({color: Yukon.Thermostat.calcHeatColor(exactTemperature)});
                displayInput.enable();
                break;
            case 'COOL':
                displayInput.setStyle({color: Yukon.Thermostat.calcCoolColor(exactTemperature)});
                displayInput.enable();
                break;
            default:
                displayInput.setStyle({color: "#CCC"});
                displayInput.disable();
            }
        });
    },
    
    renderOtherTemperatures: function(unit){
        $$(".unit_label").invoke('hide');
        $$("."+ unit +"_label").invoke('show');
        
        $$(".raw_temperature_F").each(function(elem){
            var value = parseFloat(elem.readAttribute('raw_temperature_F'));
            switch(unit){
            case 'F':
                value = Math.round(value);
                break;
            case 'C':
                value = Yukon.Thermostat.celsiusToSafeCelsius(Yukon.Thermostat.fahrenheitToCelsius(value));
                break;
            default:
                break;
            }
            elem.innerHTML = value
        });
    }
}

Yukon.Thermostat = {
    _private: {
        upperHeatF: 0,
        lowerHeatF: 0,
        upperCoolF: 0,
        lowerCoolF: 0,
        secondsResolution: 0, //seconds
        secondsBetweenPeriods: 0, //minimum time between periods in seconds
        
        heatColor: {
            r: {start: 242, end: 242},
            g: {start: 150, end: 0},
            b: {start: 29, end: 29},
            a: {start: 1, end: 1}
        },
        
        coolColor: {
            r: {start: 64, end: 189},
            g: {start: 153, end: 220},
            b: {start: 255, end: 255},
            a: {start: 1, end: 1}
        }  
    },
    
    init: function(args){
        for(var key in args){
            this._private[key] = args[key];
        }
    },
    
    calcHeatColor: function(temp){
        var _self = Yukon.Thermostat;
        var r = _self._private.heatColor.r.start;
        var g = Math.round(_self._private.heatColor.g.start - Math.abs((temp - _self._private.lowerHeatF) * ((_self._private.heatColor.g.end - _self._private.heatColor.g.start)/(_self._private.upperHeatF - _self._private.lowerHeatF))));
        var b = _self._private.heatColor.b.start; 
        return "rgb("+ r +", "+ g +", "+ b +")";
    },
    
    calcCoolColor: function(temp){
        var _self = Yukon.Thermostat;
        var tempRange = _self._private.upperCoolF - _self._private.lowerCoolF;
        var RRange = _self._private.coolColor.r.end - _self._private.coolColor.r.start;
        var GRange = _self._private.coolColor.g.end - _self._private.coolColor.g.start;
        var r = Math.round(_self._private.coolColor.r.start + ((temp - _self._private.lowerCoolF) * (RRange/tempRange)));
        var g = Math.round(_self._private.coolColor.g.start + ((temp - _self._private.lowerCoolF) * (GRange/tempRange)));
        var b = _self._private.coolColor.b.start;
        return "rgb("+ r +","+ g +","+ b +")";
    },
    
    halfIntegerAccuracy: function(value){
        return Math.round(value * 2) / 2.0;
    },
    
    fahrenheitToCelsius: function(fahrenheit){
        return ((fahrenheit - 32) * 5) / 9.0;  
    },
    
    celsiusToFahrenheit: function(celsius){
        return ((celsius * 9) / 5.0) + 32;
    },
    
    celsiusToSafeCelsius: function(celsius) {
        //ensure we have 0.5 accurracy
        return parseFloat(Yukon.Thermostat.halfIntegerAccuracy(celsius).toFixed(1)).toFixed(1);
    },
    
    convertFTemperatureToDisplaySafeTemperature: function(temperature, desiredUnit){
        var _self = Yukon.Thermostat;
        
        switch(desiredUnit){
        case 'F':
            return Math.round(temperature);
            break;
        case 'C':
            return _self.celsiusToSafeCelsius(_self.fahrenheitToCelsius(temperature));
            break;
        default:
            return temperature;
            break;
        }
    },
    
    toValidTemperatureF: function(temperature, incomingUnit, mode){
        var _self = Yukon.Thermostat;
        var temperature_f = -1;
        
        switch(mode){
        case 'EMERGENCY_HEAT':
        case 'HEAT':
            temperature_f = _self._toValidHeatTemperatureF(temperature, incomingUnit);
            break;
        case 'COOL':
            temperature_f = _self._toValidCoolTemperatureF(temperature, incomingUnit);
            break;
        default:
            return temperature;
            break;
        }
        
        return temperature_f;
    },
    
    isValidTemperatureF: function(temperature, incomingUnit, mode){
        var _self = Yukon.Thermostat;
        var temperature_f = temperature;
        
        switch(incomingUnit){
        case 'F':            
            break;
        case 'C':
            temperature_f = _self.celsiusToFahrenheit(temperature);
            break;
        default:
            return false;
            break;
        }
        
        switch(mode){
        case 'EMERGENCY_HEAT':
        case 'HEAT':
            if((temperature_f >= _self._private.lowerHeatF) && (temperature_f <= _self._private.upperHeatF)){
                return true;
            }
            break;
        case 'COOL':
            if((temperature_f >= _self._private.lowerCoolF) && (temperature_f <= _self._private.upperCoolF)){
                return true;
            }
            break;
        default:
            return false;
        }
        
        return false; 
    },
    
    _toValidHeatTemperatureF: function(temperature, incomingUnit){
        var _self = Yukon.Thermostat;
        var temperature_f = temperature;
        
        switch(incomingUnit){
        case 'F':            
            break;
        case 'C':
            temperature_f = _self.celsiusToFahrenheit(temperature);
            break;
        default:
            return -1;
            break;
        }
        
        if(temperature_f < _self._private.lowerHeatF){
            temperature_f = _self._private.lowerHeatF;
        }else if(temperature_f > _self._private.upperHeatF){
            temperature_f = _self._private.upperHeatF;
        }
        
        return temperature_f; 
    },
    
    _toValidCoolTemperatureF: function(temperature, incomingUnit){
        var _self = Yukon.Thermostat;
        var temperature_f = temperature;
        
        switch(incomingUnit){
        case 'F':            
            break;
        case 'C':
            temperature_f = _self.celsiusToFahrenheit(temperature);
            break;
        default:
            return -1;
            break;
        }
        
        if(temperature_f < _self._private.lowerCoolF){
            temperature_f = _self._private.lowerCoolF;
        }else if(temperature_f > _self._private.upperCoolF){
            temperature_f = _self._private.upperCoolF;
        }
        
        return temperature_f;
    }
}


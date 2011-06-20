var CURRENT_TIME_INPUT = null;
var CURRENT_TEMP_INPUT = null;

Yukon.ThermostatScheduleEditor = {
        
    upperHeatF: 99,
    lowerHeatF: 39,
    upperCoolF: 99,
    lowerCoolF: 40,
    currentUnit: 'fahrenheit',
    
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
    },
    
    initPrototype: function() {
        Yukon.ThermostatScheduleEditor.renderTime();
        Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor.currentUnit]();
       
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
                CURRENT_TIME_INPUT.value = timeFormatter.formatTime(value, true);
            },
            onChange: function(value, e) {
                Yukon.ThermostatScheduleEditor.commitTimeValue(timeFormatter.formatTime(value), CURRENT_TIME_INPUT);
            }
            
        });
        
        TEMP_SLIDER = new Control.Slider($("tempSlider").down('.handle'), $("tempSlider").down('.track'), {
            range: $R(40, 99),
            sliderValue: 72,
            onSlide: function(value) {
                if(Yukon.ThermostatScheduleEditor.currentUnit == 'celcius'){
                    CURRENT_TEMP_INPUT.value = (Math.round(value*2) / 2).toFixed(1);
                }else {
                    CURRENT_TEMP_INPUT.value = parseInt(value);
                }
            },
            onChange: function(value, e) {
                Yukon.ThermostatScheduleEditor.commitTempValue(value, CURRENT_TEMP_INPUT);
            }
        });

        $$(".schedules input:text").each(function(input){
            input.observe('focus', function(event){
                this.writeAttribute("previousValue", this.value);
                this.select();
            });
        });
        
        $$(".tempControls input:radio").each(function(radio){
            radio.observe('click', function(e){
                Yukon.ThermostatScheduleEditor[this.value]();
            });
        });
        
        YEvent.observeSelectorClick(".send", function(e){
            //confirmation popup already shown at this point
             $$("form[name=sendSchedule] input[name=scheduleId]")[0].value = e.target.up("form").down("input[name=scheduleId]").value;
        });
        
        YEvent.observeSelectorClick(".delete", function(e){
            //confirmation popup already shown at this point
             $$("form[name=deleteSchedule] input[name=scheduleId]")[0].value = e.target.up(".popUpDiv").down("form input[name=scheduleId]").value;
        });
        
        YEvent.observeSelectorClick(".cancel", function(e){
            e.target.up(".popUpDiv").down('form').select("input[initialValue]").each(function(input){
                input.value = input.readAttribute('initialValue');
            });
            Yukon.ThermostatScheduleEditor.renderTime();
            Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor.currentUnit]();
            e.target.up(".popUpDiv").hide();
        });
        
        YEvent.observeSelectorClick(".copy", function(e){
            var form = $("editSchedule_"+e.target.up("form").down("input[name=scheduleId]").value);
             form.down("input[name=scheduleId]").value = -1;
             form.down("input[name=scheduleName]").value += "*";
             form.down("button.delete").hide();
        });
        
        YEvent.observeSelectorClick(".edit", function(e){
            var id = e.target.up("form").down("input[name=scheduleId]").value;
            var name = e.target.up("form").down("input[name=scheduleName]").value;
            var form = $("editSchedule_"+e.target.up("form").down("input[name=scheduleId]").value);
            form.down("input[name=scheduleId]").value = id;
            form.down("input[name=scheduleName]").value = name;
            form.down("button.delete").show();
        });
        
        YEvent.observeSelectorClick(".save", function(e){
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
        
        YEvent.observeSelectorClick(".create", function(e){
            //show type picker
            $$(".page_0 input:radio").each(function(input){
                input.checked = false;
                input.observe('click', function(e){
                    e.target.up('.f_page').down('.f_next').enable();
                });
            });
            Yukon.ui.wizard.reset($("createSchedule_body"));
            return false;
        });
        
        YEvent.observeSelectorClick(".page_0 .f_next", function(e){
            var value = $("createSchedule_body").down("input[name=defaultScheduleMode]:checked").value;
            $("createSchedule_body").select(".schedule_editor").each(function(elem){
                if(!elem.hasClassName(value)){
                    elem.hide();
                }else{
                    elem.show();
                }
            });
        });
        
        YEvent.observeSelectorClick(".default", function(e){
            //find 'recommended schedule in the create popup
            var ourForm = e.target.up(".popUpDiv").down('form');
            var mode = ourForm.down("input[name=thermostatScheduleMode]").value;
            var recForm = $("createSchedule").down('.'+ mode +' form');
            
            if(recForm != null){
                var days = ourForm.select(".day");
                for(var i=0; i<days.length; i++){
                    var timeOfWeek = days[i].down("input[name=timeOfWeek]").value;
                    
                    //copy values over
                    var times = days[i].select("input[name=secondsFromMidnight]");
                    for(var j=0; j<times.length; j++){
                        times[j].value = recForm.down("."+timeOfWeek).down('input[name=secondsFromMidnight]', j).readAttribute("defaultValue");
                    }
                    
                    var heatTemps = days[i].select("input[name=heat_F]");
                    for(var j=0; j<heatTemps.length; j++){
                        heatTemps[j].value = recForm.down("."+timeOfWeek).down('input[name=heat_F]', j).readAttribute("defaultValue");
                    }
                    
                    var coolTemps = days[i].select("input[name=cool_F]");
                    for(var j=0; j<coolTemps.length; j++){
                        coolTemps[j].value = recForm.down("."+timeOfWeek).down('input[name=cool_F]', j).readAttribute("defaultValue");
                    }
                }
                
                //render times and temps
                Yukon.ThermostatScheduleEditor.renderTime();
                Yukon.ThermostatScheduleEditor[Yukon.ThermostatScheduleEditor.currentUnit]();
            }
            
        });
        
        $(document).observe('click', function(e) {
            //hide the sliders if we did NOT click on the slider or the inputs that spawn them
            if(e.target.up('.slider') == null && e.target.up('.time') == null && e.target.up('.temp') == null){
                Yukon.ThermostatScheduleEditor.hideTimeSlider();
                Yukon.ThermostatScheduleEditor.hideTempSlider();
            }
        });
        
        
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
       
       form.submit();
    },
    
    tempKeydown: function(event){
        //don't bother processing
        if(event.keyCode != 40 && event.keyCode != 38){
            return;
        }
        
        //handle up/down nudge
        var value = parseFloat(CURRENT_TEMP_INPUT.value);
        var start = 0;
        var end = 0;
        
        if(this.hasClassName("heat_F")) {
            start = Yukon.ThermostatScheduleEditor.lowerHeatF;
            end = Yukon.ThermostatScheduleEditor.upperHeatF;
        } else if(this.hasClassName("cool_F")) {
            start = Yukon.ThermostatScheduleEditor.lowerCoolF;
            end = Yukon.ThermostatScheduleEditor.upperCoolF;
        }
        
        if(Yukon.ThermostatScheduleEditor.currentUnit == 'celcius'){
            var c = (((start - 32)*5)/9);
            start = Math.round(c*2) / 2;
            c = (((end - 32)*5)/9);
            end = Math.round(c*2) / 2;
        }
        
        switch(event.keyCode){
        case 38:
            if(Yukon.ThermostatScheduleEditor.currentUnit == 'celcius'){
                value = Math.round((value+0.5)*2) / 2;
            }else {
                value = value+1;
            }
            break;
        case 40:
            if(Yukon.ThermostatScheduleEditor.currentUnit == 'celcius'){
                value = Math.round((value-0.5)*2) / 2;
            }else {
                value = value-1;
            }
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        
        if(value >= start && value <= end){
            if(Yukon.ThermostatScheduleEditor.currentUnit == 'celcius'){
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
            start = Yukon.ThermostatScheduleEditor.lowerHeatF;
            end = Yukon.ThermostatScheduleEditor.upperHeatF;
        } else if(this.hasClassName("cool_F")) {
            start = Yukon.ThermostatScheduleEditor.lowerCoolF;
            end = Yukon.ThermostatScheduleEditor.upperCoolF;
        }
        
        var startLabel = "";
        var endLabel = "";
        if(_self.currentUnit == 'celcius'){
            var c = (((start - 32)*5)/9);
            start = Math.round(c*2) / 2;
            
            c = (((end - 32)*5)/9);
            end = Math.round(c*2) / 2;
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
            start = Yukon.ThermostatScheduleEditor.lowerHeatF;
            end = Yukon.ThermostatScheduleEditor.upperHeatF;
        } else if(input.hasClassName("cool_F")) {
            start = Yukon.ThermostatScheduleEditor.lowerCoolF;
            end = Yukon.ThermostatScheduleEditor.upperCoolF;
        }
        
        //convert the value to fahrenheit scale
        if(_self.currentUnit == 'celcius'){
            //only .5 increments are allowed
            value = Math.round(value*2)/2;
            value_C = value;
            value_F = ((value*9)/5)+32;
            input.value = value_C.toFixed(1);
        }else {
            input.value = parseInt(value_F);
        }
        
        if(value_F >= start && value_F <= end){
            //put value into REAL input
            input.adjacent("input:hidden")[0].value = value_F;
            if(input.up('.temp').hasClassName('heat')){
                input.up('.temp').setStyle({backgroundColor:_self.calcHeatColor(value_F)});
            }else if(input.up('.temp').hasClassName('cool')){
                input.up('.temp').setStyle({backgroundColor:_self.calcCoolColor(value_F)});
            }
            return true;
        }
        
        //revert to previousValue
        input.value = input.readAttribute('previousValue');
    },
    
    timeKeydown: function(event){
        //don't bother processing
        if(event.keyCode != 40 && event.keyCode != 38){
            return;
        }
        
        //determine range
        var parent = this.up('.period');
        var start = 0;
        var end = ((24*60)-1);  //11:59pm is the default
        var value = timeFormatter.parseTime(this.value);
                
        if(parent.previous('.period')) {
            start = parseInt(parent.previous('.period').down('.time input[name=secondsFromMidnight]').value);
            start = Math.ceil((start+600)/60);
        }
        if(parent.next('.period')){
            end = parseInt(parent.next('.period').down('.time input[name=secondsFromMidnight]').value);
            end = Math.floor((end-600)/60);
        }
        
        switch(event.keyCode){
        case 38:
            value += 1;
            break;
        case 40:
            value -= 1;
            break;
        default:
            //really this is redundant given the first check in this function
            return;
        }
        
        if(value > start && value <= end){
            CURRENT_TIME_INPUT.value = timeFormatter.formatTime(value);
            TIME_SLIDER.setValue(value);
        }
        event.stop();
    },
    
    showTimeSlider: function(event){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.hideTempSlider();
        
        var startingValue = parseInt(this.adjacent('input[name=secondsFromMidnight]')[0].value);
        var input = event.currentTarget;
        
        //determine range
        var parent = this.up('.period');
        var start = 0;
        var end = 24*60*60;  //11:50pm is the default 
                
        if(parent.previous('.period')) {
            start = parseInt(parent.previous('.period').down('.time input[name=secondsFromMidnight]').value);
        }
        if(parent.next('.period')){
            end = parseInt(parent.next('.period').down('.time input[name=secondsFromMidnight]').value);
        }
        
        if(start != 0){
            start = Math.ceil((start+600)/60);    //round to the nearest 10 minutes
        }
        end = Math.floor((end-600)/60);

        CURRENT_TIME_INPUT = this;
        TIME_SLIDER.options.range.start = start;
        TIME_SLIDER.options.range.end = end;
        TIME_SLIDER.setValue(startingValue/60);
        
        $("timeSlider").down(".startLabel").innerHTML = timeFormatter.formatTime(TIME_SLIDER.options.range.start, true);
        $("timeSlider").down(".endLabel").innerHTML = timeFormatter.formatTime(TIME_SLIDER.options.range.end, true);
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
            var prev = 0;
            var next = ((24*60)-1)*60;  //11:59pm is the default 
                    
            if(parent.previous('.period')) {
                prev = parseInt(parent.previous('.period').down('.time input[name=secondsFromMidnight]').value);
            }
            if(parent.next('.period')){
                next = parseInt(parent.next('.period').down('.time input[name=secondsFromMidnight]').value);
            }

            if((prev < curr) && (curr < next)){
                //set seconds from midnight value
                input.adjacent('input[name=secondsFromMidnight]')[0].value = timeFormatter.parseTime(input.value)*60;
                
                //get a nicely formatted time in case the user inputs some shorthand value such as '4pm'
                input.value = timeFormatter.formatTime(timeFormatter.parseTime(input.value));
                input.writeAttribute('previousValue', input.value);
                return true;
            }
        }
        //revert to previousValue
        input.value = input.readAttribute('previousValue');
    },
    
    celcius: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.currentUnit = 'celcius';
        $$(".temp .value").each(function(elem){
            var f = parseFloat(elem.next("input:hidden").value);
            f -= 32;
            var c = ((f*5)/9)
            var rounded = Math.round(c*2) / 2;
            elem.innerHTML = rounded.toFixed(1);
        });
        $$(".temp input:text").each(function(elem){
            var f = parseFloat(elem.next("input:hidden").value);
            f -= 32;
            var c = ((f*5)/9)
            var rounded = Math.round(c*2) / 2;
            elem.value = rounded.toFixed(1);
        });
        $$(".temp").each(function(elem){
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
    
    fahrenheit: function(){
        var _self = Yukon.ThermostatScheduleEditor;
        _self.currentUnit = 'fahrenheit';
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
                elem.up(".temp").setStyle({backgroundColor:Yukon.ThermostatScheduleEditor.calcHeatColor(temp)});
            } else {
                var temp = parseInt(elem.value);
                elem.up(".temp").setStyle({backgroundColor:Yukon.ThermostatScheduleEditor.calcCoolColor(temp)});
            }
        });
    },
    
    calcHeatColor: function(temp){
        var _self = Yukon.ThermostatScheduleEditor;
        var r = _self.heatColor.r.start;
        var g = Math.round(_self.heatColor.g.start - Math.abs((temp - _self.lowerHeatF) * ((_self.heatColor.g.end - _self.heatColor.g.start)/(_self.upperHeatF - _self.lowerHeatF))));
        var b = _self.heatColor.b.start; 
        return "rgb("+ r +", "+ g +", "+ b +")";
    },
    
    calcCoolColor: function(temp){
        var _self = Yukon.ThermostatScheduleEditor;
        var tempRange = _self.upperCoolF - _self.lowerCoolF;
        var RRange = _self.coolColor.r.end - _self.coolColor.r.start;
        var GRange = _self.coolColor.g.end - _self.coolColor.g.start;
        var r = Math.round(_self.coolColor.r.start + ((temp - _self.lowerCoolF) * (RRange/tempRange)));
        var g = Math.round(_self.coolColor.g.start + ((temp - _self.lowerCoolF) * (GRange/tempRange)));
        var b = _self.coolColor.b.start;
        return "rgb("+ r +","+ g +","+ b +")";
    },
    
    conversionTest: function(){
        var fail = [];
        var c = 0;
        while(c<40) {
            //convert to fahrenheit
            var f = ((c*9)/5)+32;
            
            //convert back to celcius and round like we do on display and validate
            var d = Math.round(((f-32)*5/9)*2)/2;
            if (d != c) {
                fail.push(c + " FAIL!!!!!!! " + d + " " + f);
            }
            
            c += 0.5;
        }
        
        if(fail.length > 0){
            alert("FAIL\n"+fail.join("\n"));
        }else{
            alert("PASS")
        }
    }
}
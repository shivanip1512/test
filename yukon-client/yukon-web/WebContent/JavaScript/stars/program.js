Program = Class.create();

Program.prototype = { 

    initialize: function() {
        this.programId = '';
        this.deviceId = '';
        this.programName = '';
        this.displayName = '';
        this.shortName = '';
        this.description = '';
        this.descFile = '';
        this.ctrlOdds = '';
        this.iconNameSavings = '';
        this.iconNameControl = '';
        this.iconNameEnvrn = '';
        this.hasUnsavedChanges = false;
    },
    
    setProgramId: function(programId) {
        this.programId = checkNullString(programId);
    },
    
    getProgramId: function() {
        return this.programId;
    },
    
    setDeviceId: function(deviceId) {
        this.deviceId = checkNullString(deviceId);
    },
    
    getDeviceId: function() {
        return this.deviceId;
    },
    
    setProgramName: function(programName) {
        this.programName = checkNullString(programName);
    },
    
    getProgramName: function() {
        return this.programName;
    },
    
    setDisplayName: function(displayName) {
        this.displayName = checkNullString(displayName);
    },
    
    getDisplayName: function() {
        return this.displayName;
    },
    
    setShortName: function(shortName) {
        this.shortName = checkNullString(shortName);
    },    
    
    getShortName: function() {
        return this.shortName;
    },
    
    setDescription: function(description) {
        this.description = checkNullString(description);
    },
    
    getDescription: function() {
        return this.description;
    },
    
    setDescFile: function(descFile) {
        this.descFile = checkNullString(descFile);
    },
    
    getDescFile: function() {
        return this.descFile;
    },

    setCtrlOdds: function(ctrlOdds) {
        this.ctrlOdds = checkNullString(ctrlOdds);
    },
    
    getCtrlOdds: function() {
        return this.ctrlOdds;
    },
    
    setIconNameSavings: function(iconNameSavings) {
        this.iconNameSavings = checkNullString(iconNameSavings);
    },
    
    getIconNameSavings: function() {
        return this.iconNameSavings;
    },
    
    setIconNameControl: function(iconNameControl) {
        this.iconNameControl = checkNullString(iconNameControl);
    },
    
    getIconNameControl: function() {
        return this.iconNameControl;
    },

    setIconNameEnvrn: function(iconNameEnvrn) {
        this.iconNameEnvrn = checkNullString(iconNameEnvrn);
    },
    
    getIconNameEnvrn: function() {
        return this.iconNameEnvrn;
    },
    
    setHasUnsavedChanges: function(hasUnsavedChanges) {
        this.hasUnsavedChanges = hasUnsavedChanges;
    },
    
    getHasUnsavedChanges: function() {
        return this.hasUnsavedChanges;
    },
    
    toString: function() {
        return this.programName;
    }
        
}

function checkNullString(value) {
    if (value == 'null') return '';
    return value;
}
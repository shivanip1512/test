/**
 * Object to create and operate a widget
 */
function YukonWidget(shortName, parameters) {
    
    this.shortName = shortName;
    this.parameters = parameters;
    this.container = "widget-container-" + this.parameters.widgetId;
    this.linkInfo = {};

    /**
     * Render the widget
     */
    this.render = function () {
        var url = yukon.url('/widget/' + this.shortName + '/render'),
           params = this.getWidgetParameters(),
           _self = this;

        $.ajax({
            url : url,
            data : params
        }).done (function (data, status, xhr) {
            _self.onSuccess(xhr);
            $(document.getElementById(_self.container)).html(data);
        });
    };

    this.onSuccess = function (xhr) {
        this.parameters = yukon.ui.util.getHeaderJSON(xhr);
        // this helps prevent this from growing like crazy each time the widget is refreshed
        this.linkInfo = {};
    };

    this.onSuccessPeriodic = function (transport, json) {
        this.parameters = json;
        // this helps prevent this from growing like crazy each time the widget is refreshed
        this.linkInfo = {};
    };

    /**
     * Private function to start periodic reloading of the widget.
     * 
     * opts - json representing the options 
     * {
     *    url - the url of the ajax request
     *    params - the parameters of the ajax request
     *    container - the container to load the returned html into
     *    frequency - the number of seconds to wait between reloads
     * }
     * 
     * context - the widget object making the call
     */
    this.periodicRefresh = function (opts, context) {
        var timeoutId = 0,
            doRefresh = function () {
                $.ajax({
                    url : opts.url,
                    type : 'GET',
                    data : opts.params
                }).done(function (data, status, xhr) {
                    if ('undefined' !== typeof data && null !== data) {
                        $('#' + opts.container).html(data);
                    }
                    context.parameters = yukon.ui.util.getHeaderJSON(xhr);
                    context.linkInfo = {};
                }).always(function (data, status, xhr) {
                    timeoutId = setTimeout(doRefresh, opts.frequency * 1000);
                });
            };
        this.stop = function () {
            clearTimeout(timeoutId);
            return this;
        };
        this.start = function () {
            // paranoid
            clearTimeout(timeoutId);
            doRefresh();
            return this;
        };
        doRefresh();
        return this;
    };

    /**
     * Reloads the widget using the given command.
     */
    this.doDirectActionRefresh = function (cmd) {

        var url = yukon.url('/widget/' + this.shortName + '/' + cmd),
            params = this.getWidgetParameters(),
            _self = this;

        $.ajax({
            url : url,
            data : params
        }).done( function (data, status, xhr) {
            _self.onSuccess(xhr);
            $(document.getElementById(_self.container)).html(data);
        });
    };

    this.doActionRefresh = function (args) {
        
        var container = this.container,
            _self = this,
            localSuccess = function (xhr) {
                if (args.disableInputs == null || args.disableInputs == true) {
                    $('#' + container + ' input').prop('disabled', false);
                    $('#' + container + ' button').prop('disabled', false);
                }
                _self.onSuccess(xhr);
            },
            oldParams,
            url;
            
        if (args.disableInputs == null || args.disableInputs == true) {
            $('#' + 'container' + ' input').prop('disabled', true);
            $('#' + 'container' + ' button').prop('disabled', true);
        }

        oldParams = $.extend(true, this.getWidgetParameters(), this.linkInfo[args.key]);

        url = yukon.url('/widget/' + this.shortName + '/' + args.command);

        $.ajax({
            url : url,
            data : oldParams
        }).done(function (data, status, xhr) {
            localSuccess(xhr);
            $(document.getElementById(_self.container)).html(data);
        });
    };

    /**
     * Performs the action for the given command and loads the resulting 
     * html into the provided container.
     */
    this.doActionUpdate = function (args) {
        
        var oldParams = $.extend(true, this.getWidgetParameters(), this.linkInfo[args.key], args.extraParameters),
            url = yukon.url('/widget/' + this.shortName + '/' + args.command),
            buttonId = args.key; // key happeneds to be the button's id as well so lets just use it.

        $.ajax({
            url : url,
            data : oldParams
        }).done(function (data) {
            $(document.getElementById(args.containerID)).html(data);
        }).always(function() {
            var button = $('#' + buttonId);
            if (button.length > 0) { // if the cotainer included the button, it won't be there anymore
                yukon.ui.unbusy(button);
            }
        });
    };

    this.setParameter = function (name, value) {
        this.parameters[name] = value;
    };

    this.setupLink = function (key, jsonData) {
        this.linkInfo[key] = jsonData;
    };

    this.doPeriodicRefresh = function (cmd, newParams, period, container) {
        var containerToUse = container,
            oldParams,
            url;
        if (typeof container === 'undefined' || container === '') {
            containerToUse = this.container;
        }
        oldParams = $.extend(true, this.getWidgetParameters(), newParams);

        url = yukon.url('/widget/' + this.shortName + '/' + cmd);
        return new this.periodicRefresh({'container' : containerToUse, 'url' : url, 'params' : oldParams, 'frequency' : period}, this);
    };

    /**
     * @returns {Hash}
     */
    this.getWidgetParameters = function () {
        var container = $('#' + this.container)[0],
            theseParameters = {},
            widgetInputs,
            i,
            el,
            mergedParameters;

        // now find all inputs under the container
        widgetInputs = container.getElementsByTagName('input');
        for (i = 0; i < widgetInputs.length; i += 1) {
            el = widgetInputs[i];
            if (el.name) {
                if (el.type != 'radio') {
                    theseParameters[el.name] = $(el).val();
                } else {
                    if (el.checked) {
                        theseParameters[el.name] = $(el).val();
                    }
                }
            }
        }
        widgetInputs = container.getElementsByTagName('select');
        for (i = 0; i < widgetInputs.length; i += 1) {
            el = widgetInputs[i];
            if (el.name) {
                theseParameters[el.name] = $(el).val();
            }
        }
        widgetInputs = container.getElementsByTagName('textarea');
        for (i = 0; i < widgetInputs.length; i += 1) {
            el = widgetInputs[i];
            if (el.name) {
                theseParameters[el.name] = $(el).val();
            }
        }

        mergedParameters = $.extend(true, this.parameters, theseParameters);
        return mergedParameters;
    };
    
}
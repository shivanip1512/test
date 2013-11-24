// TODO: Update the rest of this file to use jQuery
// Currently the only jQuery in here are the .ajax calls (except for doPeriodicRefresh)

function JsWidgetObject (shortName, parameters) {
    // Don't use $() up here because this is called before page is fully loaded
    this.shortName = shortName;
    this.parameters = parameters;
    this.container = "widget-container-" + this.parameters.widgetId;
    this.linkInfo = {};

    this.render = function () {
        var url = "/widget/" + this.shortName + "/render",
           params = this.getWidgetParameters(),
           _self = this;

        jQuery.ajax({
            url : url,
            data : params
        }).done (function (data, status, xhr) {
            _self.onSuccess(xhr);
            jQuery(document.getElementById(_self.container)).html(data);
        });
    };

    this.onSuccess = function (xhr) {
        this.parameters = Yukon.ui.aux.getHeaderJSON(xhr);
        // this helps prevent this from growing like crazy each time the widget is refreshed
        this.linkInfo = {};
    };

    this.onSuccessPeriodic = function (transport, json) {
        this.parameters = json;
        // this helps prevent this from growing like crazy each time the widget is refreshed
        this.linkInfo = {};
    };

    this.periodicRefresh = function (opts, context) {
        var timeoutId = 0,
            doRefresh = function () {
                jQuery.ajax({
                    url : opts.url,
                    type : 'GET',
                    data : opts.params
                }).done(function (data, status, xhr) {
                    if ('undefined' !== typeof data && null !== data) {
                        jQuery('#' + opts.container).html(data);
                    }
                    context.parameters = Yukon.ui.aux.getHeaderJSON(xhr);
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
        doRefresh ();
        return this;
    };

    this.doDirectActionRefresh = function (cmd) {
        jQuery('#' + this.container + ' input').prop('disabled', true);

        var url = "/widget/" + this.shortName + "/" + cmd,
            params = this.getWidgetParameters(),
            _self = this;

        jQuery.ajax({
            url : url,
            data : params
        }).done( function (data, status, xhr) {
            _self.onSuccess(xhr);
            jQuery(document.getElementById(_self.container)).html(data);
        });
    };

  /**
   * args = {
   *    command = string final path for the url
   *    buttonID = string containing the ID of the button
   *    waitingText = text that will be placed on the button while the ajax call is in progress
   *    key = 
   * }
   */
    this.doActionRefresh = function (args) {
        var defaultButtonText = jQuery(jQuery('#' + args.buttonID + ' span')[0]).html(),
            container = this.container,
            _self = this,
            localSuccess = function (xhr) {
                jQuery(jQuery('#' + args.buttonID + ' span')[0]).html(defaultButtonText);
                jQuery('#' + args.buttonID + ' .widgetAction_waiting').hide();
                if (args.disableInputs == null || args.disableInputs == true) {
                    jQuery('#' + container + ' input').prop('disabled', false);
                    jQuery('#' + container + ' button').prop('disabled', false);
                }
                _self.onSuccess(xhr);
            },
            oldParams,
            url;
        jQuery(jQuery('#' + args.buttonID + ' span')[0]).html(args.waitingText);
        jQuery('#' + args.buttonID + ' .widgetAction_waiting').show();
        if (args.disableInputs == null || args.disableInputs == true) {
            jQuery('#' + 'container' + ' input').prop('disabled', true);
            jQuery('#' + 'container' + ' button').prop('disabled', true);
        }

        oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[args.key]);

        url = "/widget/" + this.shortName + "/" + args.command;

        jQuery.ajax({
            url : url,
            data : oldParams
        }).done( function (data, status, xhr) {
            localSuccess(xhr);
            jQuery(document.getElementById(_self.container)).html(data);
        });
    };

  /**
   * args = {
   *    command = string final path for the url
   *    containerID = string containing the ID of the container
   *    buttonID = string containing the ID of the button
   *    waitingText = text that will be placed on the button while the ajax call is in progress
   *    key = 
   * }
   */
    this.doActionUpdate = function (args) {
        var waitingTextLocationSpecified = typeof args.waitingTextLocation !== 'undefined',
            defaultButtonText,
            container = this.container,
            updateButton = function () {
                if (waitingTextLocationSpecified) {
                    jQuery(args.waitingTextLocation).hide();
                } else {
                    jQuery('#' + args.buttonID).find('span').text(defaultButtonText);
                }
                jQuery('#' + args.buttonID).find('.widgetAction_waiting').hide();
                jQuery('#' + container).find('button').prop('disabled', true);
            },
            oldParams,
            url;
        if (args.buttonID) {
            if (waitingTextLocationSpecified) {
                jQuery(args.waitingTextLocation).text(args.waitingText);
                jQuery(args.waitingTextLocation).show();
            } else {
                defaultButtonText = jQuery('#' + args.buttonID).find('span').text();
                jQuery('#' + args.buttonID).find('span').text(args.waitingText);
            }
            jQuery('#' + args.buttonID).find('.widgetAction_waiting').show();
            jQuery('#' + container).find('button').prop('disabled', true);
        }

        oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[args.key], args.extraParameters);

        url = "/widget/" + this.shortName + "/" + args.command;

        jQuery.ajax({
            url : url,
            data : oldParams,
            success : function (data) {
                if (args.buttonID) {
                    updateButton();
                }
                jQuery(document.getElementById(args.containerID)).html(data);
            }
        });
    };

    this.setParameter = function (name, value) {
        this.parameters[name] = value;
    };

    this.setupLink = function (key, jsonData) {
        this.linkInfo[key] = jsonData;
    };

    this.doActionLinkRefresh = function (cmd, actionSpan, waitingLabel, key, container) {
        var oldParams,
            url,
            useContainer,
            _self = this;
        jQuery('#' + actionSpan + ' .actionLinkAnchor').hide();
        jQuery('#' + actionSpan + ' .widgetAction_waiting').show();
        jQuery('#' + actionSpan + ' span').html(waitingLabel);
        jQuery('#' + this.container + ' input').prop('disabled', true);

        oldParams = jQuery.extend(true, this.getWidgetParameters(), this.linkInfo[key]);

        url = "/widget/" + this.shortName + "/" + cmd;

        useContainer = container;
        if (typeof container === 'undefined' || container === '') {
            useContainer = this.container;
        }

        jQuery.ajax({
            url : url,
            data : oldParams
        }).done( function (data, status, xhr) {
            _self.onSuccess(xhr);
            jQuery(document.getElementById(useContainer)).html(data);
        }).always( function () {
                jQuery("#" + actionSpan + " .actionLinkAnchor").show();
        });
        jQuery('#' + this.container + ' input').prop('disabled', false);
    };

    this.doPeriodicRefresh = function (cmd, newParams, period, container) {
        var containerToUse = container,
            oldParams,
            url;
        if (typeof container === 'undefined' || container === '') {
            containerToUse = this.container;
        }
        oldParams = jQuery.extend(true, this.getWidgetParameters(), newParams);

        url = "/widget/" + this.shortName + "/" + cmd;
        return new this.periodicRefresh({'container' : containerToUse, 'url' : url, 'params' : oldParams, 'frequency' : period}, this);
    };

    /**
     * @returns {Hash}
     */
    this.getWidgetParameters = function () {
        var container = jQuery('#' + this.container)[0],
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
                    theseParameters[el.name] = jQuery(el).val();
                } else {
                    if (el.checked) {
                        theseParameters[el.name] = jQuery(el).val();
                    }
                }
            }
        }
        widgetInputs = container.getElementsByTagName('select');
        for (i = 0; i < widgetInputs.length; i += 1) {
            el = widgetInputs[i];
            if (el.name) {
                theseParameters[el.name] = jQuery(el).val();
            }
        }
        widgetInputs = container.getElementsByTagName('textarea');
        for (i = 0; i < widgetInputs.length; i += 1) {
            el = widgetInputs[i];
            if (el.name) {
                theseParameters[el.name] = jQuery(el).val();
            }
        }

        mergedParameters = jQuery.extend(true, this.parameters, theseParameters);
        return mergedParameters;
    };
}

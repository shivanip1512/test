import React from "react";

const CURRENT_URL = new URL(window.location.href);

function getYukonApiUrl() {
    debugger;
    var apiUrl = CURRENT_URL.origin;
    if (window.configs != null && window.configs.YUKON_API_URL != null) {
        apiUrl = window.configs.YUKON_API_URL;
    }
    console.log("Api URL: " + apiUrl);
    return apiUrl;
}

function getYukonReactUrl() {
    debugger;
    var reactUrl = CURRENT_URL.origin;
    if (window.configs != null && window.configs.YUKON_REACT_URL != null) {
        reactUrl = window.configs.YUKON_REACT_URL;
    }
    console.log("React URL: " + reactUrl);
    return reactUrl;
}

export const urlHelper = {
    getYukonApiUrl,
    getYukonReactUrl,
};

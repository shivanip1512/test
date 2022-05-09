const CURRENT_URL = new URL(window.location.href);
const API_AND_REACT_URL = CURRENT_URL.origin;

export function getYukonApiUrl() {
    var apiUrl = API_AND_REACT_URL;
    if (window.configs != null && window.configs.YUKON_API_URL != null) {
        apiUrl = window.configs.YUKON_API_URL;
    }
    return apiUrl;
}

export function getYukonReactUrl() {
    var reactUrl = API_AND_REACT_URL;
    if (window.configs != null && window.configs.YUKON_REACT_URL != null) {
        reactUrl = window.configs.YUKON_REACT_URL;
    }
    return reactUrl;
}

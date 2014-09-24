/**
 * Singleton that manages the cookies.
 * @requires yukon.hide.reveal.js
 */

/** 
 * Creates a cookie with the given name and value,this method also sets it expiry time.
 * @param {string} name - Name of cookie.
 * @param {string} value - Value of that cookie.
 */
function createCookie (name, value) {
    var date = new Date(),
        expires;
    date.setTime(date.getTime() + (365 * 24 * 60 * 60 * 1000));
    expires = "; expires=" + date.toGMTString();
    document.cookie = name + "=" + encodeURIComponent(value) + expires + "; path=/";
}

/** 
 * Read a cookie with the given name.
 * @param {string} name - Name of cookie.
 * @returns {string} value - Value of that cookie.
 */
function readCookie (name) {
    var nameEQ = name + "=",
        ca = document.cookie.split(';'),
        i,
        c;
    for (i = 0; i < ca.length; i++) {
        c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1, c.length);
        }
        if (c.indexOf(nameEQ) === 0) {
            return decodeURIComponent(c.substring(nameEQ.length, c.length));
        }
    }
    return null;
}

YukonClientPersistance = {};

/** 
 * Method to persist state into a cookie.
 * @param {string} scope - Scope of the cookie.
 * @param {string} persistId - Id of cookie.
 * @param {string} value - Value of that cookie.
 */
YukonClientPersistance.persistState = function (scope, persistId, value) {
    var clientPersistance = readCookie('yukonClientPersistance');

    if (clientPersistance) {
        clientPersistance = JSON.parse(clientPersistance);
    } else {
        clientPersistance = {};
    }

    clientPersistance[scope + persistId] = value;
    createCookie('yukonClientPersistance', JSON.stringify(clientPersistance));
};

/** 
 * Method to get state from a cookie.
 * @param {string} scope - Scope of the cookie.
 * @param {string} persistId - Id of cookie.
 * @param {string} defaultValue - Default value of that cookie.
 * @returns {string} state - Previous state of the cookie.
 */
YukonClientPersistance.getState = function (scope, persistId, defaultValue) {
    var clientPersistance = readCookie('yukonClientPersistance'),
        value;
    if (clientPersistance) {
        clientPersistance = JSON.parse(clientPersistance);
    } else {
        return defaultValue;
    }
    value = clientPersistance[scope + persistId];

    if (value) {
        return value;
    } else {
        return defaultValue;
    }
};
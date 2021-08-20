import * as actionTypes from "./actionTypes";

export const openDrawer = () => {
    return {
      type: actionTypes.OPEN_DRAWER
    };
  };

  export const closeDrawer = () => {
    return {
      type: actionTypes.CLOSE_DRAWER
    };
  };

  export const toggleDrawer = () => {
    return {
      type: actionTypes.TOGGLE_DRAWER
    };
  };

  export const renderDrawer = () => {
    return {
      type: actionTypes.RENDER_DRAWER
    };
  };

  export const setToken = (token) => {
    return {
      type: actionTypes.SET_TOKEN,
      token: token
    };
  };

export const setFlashErrors = (flashErrors) => {
    return {
      type: actionTypes.SET_FLASH_ERRORS,
      flashErrors: flashErrors
    };
  };

  export const clearFlashErrors = () => {
    return {
      type: actionTypes.CLEAR_FLASH_ERRORS
    };
  };

  export const setFlashSuccess = (flashSuccess) => {
    return {
      type: actionTypes.SET_FLASH_SUCCESS,
      flashSuccess: flashSuccess
    };
  };

  export const clearFlashSuccess = () => {
    return {
      type: actionTypes.CLEAR_FLASH_SUCCESS
    };
  };

  export const setValidationErrors = (validationErrors) => {
    return {
      type: actionTypes.SET_VALIDATION_ERRORS,
      validationErrors: validationErrors
    };
  };

  export const clearValidationErrors = () => {
    return {
      type: actionTypes.CLEAR_VALIDATION_ERRORS
    };
  };

  export const clearAllAlerts = () => {
    return {
      type: actionTypes.CLEAR_ALL_ALERTS
    };
  };

  export const addI18nKeyValue = (key, value) => {
    return {
      type: actionTypes.ADD_I18N_KEY_VALUE,
      key: key,
      value: value
    }
  };

  export const getI18nKeyValue = (key, args) => {
    return {
      type: actionTypes.GET_I18N_KEY_VALUE,
      key: key,
      args: args
    }
  }

  export const setTheme = (theme) => {
    return {
      type: actionTypes.SET_THEME,
      theme: theme
    }
  }
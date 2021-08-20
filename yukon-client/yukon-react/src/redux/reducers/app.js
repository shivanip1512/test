import {
    OPEN_DRAWER,
    CLOSE_DRAWER,
    TOGGLE_DRAWER,
    RENDER_DRAWER,
    SET_TOKEN,
    SET_FLASH_ERRORS,
    CLEAR_FLASH_ERRORS,
    SET_FLASH_SUCCESS,
    CLEAR_FLASH_SUCCESS,
    SET_VALIDATION_ERRORS,
    CLEAR_VALIDATION_ERRORS,
    CLEAR_ALL_ALERTS,
    ADD_I18N_KEY_VALUE,
    SET_THEME
} from '../actions/actionTypes';

const initialAppState = {
    drawerOpen: false,
    renderDrawer: false,
    token: null,
    username: 'yukon',
    password: 'yukon',
    flashErrors: null,
    flashSuccess: null,
    validationErrors: null,
    i18nKeyValues: [],
    theme: null
};

export const AppReducer = (state = initialAppState, action) => {
    switch (action.type) {
        case OPEN_DRAWER:
            return {...state, drawerOpen: true};
        case CLOSE_DRAWER:
            return {...state, drawerOpen: false};
        case TOGGLE_DRAWER:
            return {...state, drawerOpen: !state.drawerOpen};
        case RENDER_DRAWER:
            return {...state, renderDrawer: true};
        case SET_TOKEN:
            return {...state, token: action.token};
        case SET_FLASH_ERRORS:
            return {...state, flashErrors: action.flashErrors};
        case CLEAR_FLASH_ERRORS:
            return {...state, flashErrors: null};
        case SET_FLASH_SUCCESS:
            return {...state, flashSuccess: action.flashSuccess};
        case CLEAR_FLASH_SUCCESS:
            return {...state, flashSuccess: null};
        case SET_VALIDATION_ERRORS:
            return {...state, validationErrors: action.validationErrors};
        case CLEAR_VALIDATION_ERRORS:
            return {...state, validationErrors: null};
        case CLEAR_ALL_ALERTS:
            return {...state, validationErrors: null, flashSuccess: null, flashErrors: null};
        case ADD_I18N_KEY_VALUE:
            let updatedI18nKeyValues = {...state.i18nKeyValues};
            if (updatedI18nKeyValues[action.key] != null) {
                updatedI18nKeyValues[action.key] = action.value;
            } else {
                updatedI18nKeyValues = {...state.i18nKeyValues, [action.key]: [action.value]}
            }
            return {...state, i18nKeyValues: updatedI18nKeyValues};
        case SET_THEME:
            return {...state, theme: action.theme};
        default:
            return state;
    }
}
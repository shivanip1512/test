import I18n from '../components/I18n/I18n';

import axios from '../axiosConfig';
import * as actions from '../redux/actions/index';
import { store } from '../redux/store';

export const checkForApiValidationError = (fieldName, apiValidationErrors) => {
    if (apiValidationErrors != null) {
        const fieldError = apiValidationErrors.filter((error) => {
            return error.field === fieldName
        });
        if (fieldError.length > 0) {
            return true;
        }
    }
    return false;
};

export const displayApiValidationError = (fieldName, apiValidationErrors) => {
    if (apiValidationErrors != null) {
        const fieldErrors = apiValidationErrors.filter((error) => {
            return error.field === fieldName
        });
        if (fieldErrors[0] != null) {
            return fieldErrors[0].detail;
        }
    }
};

export const i18n = (key, args) => {
    return <I18n i18nKey={key} args={args}/>;
};

export const getPagei18nValues = (keysArgsMap, setPageKeysReceived) => {
    const {dispatch} = store;
    let state = store.getState();

    const i18nKeyValues = state.app.i18nKeyValues;
    let neededKeys = [];

    keysArgsMap.forEach((keyArg) => {
        if (!i18nKeyValues[keyArg.nameKey]) {
            neededKeys.push(keyArg);
        }
    })

    if (neededKeys.length > 0) {
        axios({
            url: '/api/i18n/keys',
            method: 'post',
            data: neededKeys
        }).then(response => {
            response.data.forEach((i18nKeyValue) => {
                dispatch(actions.addI18nKeyValue(i18nKeyValue.nameKey, i18nKeyValue.i18nValue));
            });
            setPageKeysReceived(true);
        });
    } else {
        setPageKeysReceived(true);
    }
};

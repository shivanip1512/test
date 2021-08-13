import React, { useEffect} from 'react';
import { useSelector, useDispatch } from 'react-redux';

import axios from '../../axiosConfig';
import * as actions from '../../redux/actions/index';

const I18n = (props) => {

    const dispatch = useDispatch();

    const i18nKeyValues = useSelector(store => store.app.i18nKeyValues);
    let i18nKeyValue = "";
    if (i18nKeyValues[props.i18nKey]) {
        i18nKeyValue = i18nKeyValues[props.i18nKey][0];
    } 

    useEffect(() => {
        if (i18nKeyValues[props.i18nKey] && props.args == null) {
            i18nKeyValue = i18nKeyValues[props.i18nKey][0];
        } else {
            axios.get('/api/i18n/key?key=' + props.i18nKey + '&args=' + props.args)
            .then(i18nValue => {
                //if (props.args == null) {
                    //don't cache those that need arguments
                    dispatch(actions.addI18nKeyValue(props.i18nKey, i18nValue.data));
                //}
                i18nKeyValue = i18nValue.data;
            });
        }    
    }, [props.i18nKey, props.args]);

    return (
        i18nKeyValue
    )

}

export default I18n;
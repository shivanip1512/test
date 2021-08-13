import * as yup from 'yup';

import { i18n } from './utils/Helpers';


yup.setLocale({
    mixed: {
        required: i18n('yukon.web.error.required')
    },
    number: {
        min: ({min}) => i18n('yukon.web.error.101110', min)
    }
});

export default yup;
import * as yup from 'yup';
import I18n from './components/I18n/I18n';

yup.setLocale({
    mixed: {
        required: <I18n i18nKey='validation.required'/>
    },
    number: {
        min: ({min}) => <I18n i18nKey='validation.min' args={{min: min}}/>
    }
});

export default yup;
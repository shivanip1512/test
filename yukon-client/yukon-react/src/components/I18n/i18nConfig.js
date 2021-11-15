import i18n from 'i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import { initReactI18next } from 'react-i18next';
import Backend from 'i18next-http-backend';

const yukoni18n = i18n.createInstance();

yukoni18n
    .use(Backend)
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        fallbackLng: 'en',
        ns: ['translation', 'common', 'validation', 'custom'],
        defaultNS: 'custom',
        fallbackNS: ['translation', 'common', 'validation'],
        backend: {
            loadPath: '/yukon-ui/locales/{{lng}}/{{ns}}.json',
        },
        react: {
            wait: true,
            useSuspense: true
        },
        interpolation: {
            escapeValue: false, // not needed for react as it escapes by default
        },
        debug: true
    });

export default yukoni18n;


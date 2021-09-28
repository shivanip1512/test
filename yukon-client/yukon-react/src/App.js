import React, { useEffect } from 'react';
import { Route, Redirect, Switch, useLocation } from 'react-router-dom'
import { I18nextProvider } from 'react-i18next';
import LanguageDetector from 'i18next-browser-languagedetector';
import { initReactI18next } from 'react-i18next';
import Backend from 'i18next-http-backend';

import { DrawerLayout} from '@pxblue/react-components';

import {
    SecurityContextProvider,
    AuthNavigationContainer,
    AuthUIContextProvider,
    useSecurityActions,
} from '@pxblue/react-auth-workflow';
import { ProjectAuthUIActions } from './actions/AuthUIActions';
import { ProjectRegistrationUIActions } from './actions/RegistrationUIActions';
import { routes } from './constants/routing';

import productLogo from './assets/images/eaton_yukon_logo.png';

import NavigationMenu from './components/Navigation/NavigationMenu';
import NavigationDrawer from './components/Navigation/NavigationDrawer';
import DRSetupFilterPage from './components/YukonPage/DemandResponse/DRSetupFilter';
import DRTestPage from './components/YukonPage/DemandResponse/DRTestPage';
import CommChannelCreatePage from './components/YukonPage/Assets/CommChannelCreate';
import DashboardPage from './components/YukonPage/Dashboards/Dashboard';

import i18n from 'i18next';

const ScrollToTop = () => {
    const { pathname } = useLocation();

    useEffect(() => {
        window.scrollTo(0, 0);
    }, [pathname]);

    return null;
};

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
      loadPath: '/yukon-ui/locales/{{lng}}/{{ns}}.json'
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

export const AuthUIConfiguration = (props) => {
    const securityContextActions = useSecurityActions();

    const backgroundProps = {
        backgroundImage: 'url(/yukon-ui/yukon_background.png)',
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover'
    }

    return (
        <AuthUIContextProvider
            authActions={ProjectAuthUIActions(securityContextActions)}
            registrationActions={ProjectRegistrationUIActions}
            showSelfRegistration={false}
            showContactSupport={false}
            htmlEula={false}
            loginType="username"
            background={backgroundProps}
            contactEmail={'something@email.com'}
            contactPhone={'1-800-123-4567'}
            projectImage={productLogo}
        >
            {props.children}
        </AuthUIContextProvider>
    );
};


export const App = () => {
    return (
        <SecurityContextProvider>
            <AuthUIConfiguration>
                <AuthNavigationContainer routeConfig={routes}>
                    <I18nextProvider i18n={yukoni18n}>
                        <ScrollToTop/>
                        <DrawerLayout drawer={<NavigationDrawer yukonPath={window.configs.YUKON_API_URL} reactPath={window.configs.YUKON_REACT_URL} />}>
                            <NavigationMenu yukonPath={window.configs.YUKON_API_URL} reactPath={window.configs.YUKON_REACT_URL}/>
                            <Switch>
                                <Route exact path="/yukon-ui/dashboard" component={DashboardPage}/>
                                <Route exact path="/yukon-ui/dr/setup/list" component={DRSetupFilterPage}/>
                                <Route exact path="/yukon-ui/dr/setup/test" component={DRTestPage}/>
                                <Route exact path="/yukon-ui/stars/device/commChannel/create" component={CommChannelCreatePage}/>
                                <Route path="/yukon-ui">
                                    <Redirect to="/yukon-ui/dashboard"/>
                                </Route>
                            </Switch>
                        </DrawerLayout>
                    </I18nextProvider>
                </AuthNavigationContainer>
            </AuthUIConfiguration>
        </SecurityContextProvider>
    );
    
};

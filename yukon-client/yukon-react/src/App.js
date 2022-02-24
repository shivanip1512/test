import React, { useEffect } from 'react';
import { Route, Redirect, Switch, useLocation } from 'react-router-dom'
import { I18nextProvider } from 'react-i18next';

import yukoni18n from './components/I18n/i18nConfig';

import { DrawerLayout} from '@brightlayer-ui/react-components';

import { SecurityContextProvider, AuthNavigationContainer } from '@brightlayer-ui/react-auth-workflow';
import AuthUIConfiguration from './components/security/AuthUIConfiguration';
import { routes } from './constants/routing';

import NavigationMenu from './components/Navigation/NavigationMenu';
import NavigationDrawer from './components/Navigation/NavigationDrawer';
import DRSetupFilterPage from './components/YukonPage/DemandResponse/DRSetupFilter';
import DRTestPage from './components/YukonPage/DemandResponse/DRTestPage';
import CommChannelCreatePage from './components/YukonPage/Assets/CommChannelCreate';
import DashboardPage from './components/YukonPage/Dashboards/Dashboard';

const ScrollToTop = () => {
    const { pathname } = useLocation();

    useEffect(() => {
        window.scrollTo(0, 0);
    }, [pathname]);

    return null;
};

export const App = () => {
    return (
        <SecurityContextProvider>
            <AuthUIConfiguration>
                <AuthNavigationContainer routeConfig={routes} loginErrorDisplayConfig={{err:"custom error"}}>
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

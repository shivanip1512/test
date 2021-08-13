import React, { useEffect } from 'react';
import { Route, BrowserRouter, Switch, useLocation } from 'react-router-dom'

import { DrawerLayout} from '@pxblue/react-components';

import NavigationMenu from './components/Navigation/NavigationMenu';
import NavigationDrawer from './components/Navigation/NavigationDrawer';
import DRSetupFilterPage from './components/YukonPage/DemandResponse/DRSetupFilter';
import DRTestPage from './components/YukonPage/DemandResponse/DRTestPage';
import CommChannelCreatePage from './components/YukonPage/Assets/CommChannelCreate';

const ScrollToTop = () => {
    const { pathname } = useLocation();

    useEffect(() => {
        window.scrollTo(0, 0);
    }, [pathname]);

    return null;
};


export function App() {
    return (
        <BrowserRouter>
            <ScrollToTop/>
            <DrawerLayout drawer={<NavigationDrawer yukonPath={window.configs.YUKON_API_URL} reactPath={window.configs.YUKON_REACT_URL} />}>
                <NavigationMenu yukonPath={window.configs.YUKON_API_URL} reactPath={window.configs.YUKON_REACT_URL}/>
                <Switch>
                    <Route path="/yukon-ui/dr/setup/list" component={DRSetupFilterPage}/>
                    <Route path="/yukon-ui/dr/setup/test" component={DRTestPage}/>
                    <Route path="/yukon-ui/stars/device/commChannel/create" component={CommChannelCreatePage}/>
                </Switch>
            </DrawerLayout>
        </BrowserRouter>
    );
    
}

export default App;
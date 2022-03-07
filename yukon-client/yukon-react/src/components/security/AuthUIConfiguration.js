import React from 'react';

import {
    AuthUIContextProvider,
    useSecurityActions,
} from '@brightlayer-ui/react-auth-workflow';

import { ProjectAuthUIActions } from '../../actions/AuthUIActions';
import { ProjectRegistrationUIActions } from '../../actions/RegistrationUIActions';

import productLogo from '../../assets/images/eaton_yukon_logo.png';


const AuthUIConfiguration = (props) => {
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
            loginErrorDisplayConfig={{mode:"message-box"}}
        >
            {props.children}
        </AuthUIContextProvider>
    );
};

export default AuthUIConfiguration;
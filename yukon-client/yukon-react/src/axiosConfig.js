import axios from 'axios';

import { store } from './redux/store';

import * as actions from './redux/actions/index';

let axiosInstance = axios.create();

//Apply interceptor to request to attach JWT token
axiosInstance.interceptors.request.use(
    config => {
        let state = store.getState();

        // Add authorization header to all requests
        //commented below line to check wheather httpCookie is working
        //config.headers['Authorization'] = 'Bearer ' + state.app.token;
        return config;
    }, 
    error => {
        return Promise.reject(error);
    }
);

// apply interceptor on response for error handling
axiosInstance.interceptors.response.use(
   response => {
        const {dispatch} = store;
        dispatch(actions.clearAllAlerts());
        return response;
   },
   error => {
        const {dispatch} = store;
        let state = store.getState();
        dispatch(actions.clearAllAlerts());
        if (error.response) {
            if (error.response.status === 401) {
                //refresh token
                return axiosInstance.post('/api/token', {
                    username: state.app.username,
                    password: state.app.password
                }).then(response => {
                    dispatch(actions.setToken(response.data.accessToken));
                    error.config.headers['Authorization'] = 'Bearer ' + response.data.accessToken;
                    return axiosInstance.request(error.config);
                });
            } else {
                if (error.response.data.code === 101000) {
                    dispatch(actions.setValidationErrors(error.response.data.errors));
                } else {
                    dispatch(actions.setFlashErrors(error.response.data.detail));
                }
            }
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
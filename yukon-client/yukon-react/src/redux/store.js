import { createStore, applyMiddleware, compose } from 'redux';
import thunk from 'redux-thunk';
import { rootReducer } from './reducers';

export const store = createStore(rootReducer(), applyMiddleware(thunk));


//Use only if you have Redux Dev Tools Chrome extension installed
/* const composeEnhancers = window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

export const store = createStore(
    rootReducer(), 
    composeEnhancers(applyMiddleware(thunk))
); */



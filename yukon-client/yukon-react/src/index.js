import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';

import { ThemeProvider, createTheme } from '@material-ui/core/styles';
import CssBaseline from '@material-ui/core/CSSBaseline';
import * as PXBThemes from '@pxblue/react-themes';
import '@pxblue/react-themes/open-sans';

import './index.css';

import { Provider } from 'react-redux';
import { store } from './redux/store';

import App from './App';

// import i18n (needs to be bundled ;))
import './i18n';

const app = (
  <Provider store={store}>
      <ThemeProvider theme={createTheme(PXBThemes.blue)}>
      <CssBaseline />
      <Suspense fallback={<div></div>}>
          <App />
      </Suspense>
    </ThemeProvider>
  </Provider>
)

ReactDOM.render(app, document.getElementById('root'));

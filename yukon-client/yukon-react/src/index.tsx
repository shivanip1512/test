import React, { Suspense } from 'react';
import ReactDOM from 'react-dom';
import { I18nextProvider } from 'react-i18next';

import { CssBaseline, ThemeProvider, createTheme } from '@material-ui/core';

import * as PXBThemes from '@brightlayer-ui/react-themes';
import '@brightlayer-ui/react-themes/open-sans';

import './index.css';

import { Provider } from 'react-redux';
import { store } from './redux/store';

import { App } from './App';

import i18n from 'i18next';

ReactDOM.render(
  <Provider store={store}>
    <I18nextProvider i18n={i18n}>
      <ThemeProvider theme={createTheme(PXBThemes.blue)}>
        <CssBaseline />
        <Suspense fallback={<div></div>}>
          <App />
        </Suspense>
      </ThemeProvider>
    </I18nextProvider>
   </Provider>,
  document.getElementById('root')
);
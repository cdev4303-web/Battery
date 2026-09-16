import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { ErrorBoundary } from './components/ErrorBoundary';
import './index.css';

// Manage Service Worker
if ('serviceWorker' in navigator && !window.location.protocol.startsWith('file')) {
  if (import.meta.env.DEV) {
    // In development mode, ensure any existing service workers are unregistered so they don't break HMR or proxy requests
    navigator.serviceWorker.getRegistrations().then((registrations) => {
      for (const registration of registrations) {
        registration.unregister().catch(() => {});
      }
    }).catch(() => {});
  } else {
    // In production mode, register PWA service worker after window load
    window.addEventListener('load', () => {
      navigator.serviceWorker.register('./sw.js')
        .then((reg) => {
          console.log('ServiceWorker registered with scope: ', reg.scope);
        })
        .catch((err) => {
          console.warn('ServiceWorker registration failed: ', err);
        });
    });
  }
}

const rootElement = document.getElementById('root');
if (rootElement) {
  ReactDOM.createRoot(rootElement).render(
    <React.StrictMode>
      <ErrorBoundary>
        <App />
      </ErrorBoundary>
    </React.StrictMode>
  );
}


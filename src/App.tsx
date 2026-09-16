import React, { useState, useEffect } from 'react';
import { Header } from './components/Header';
import { HomeView } from './components/HomeView';
import { ChargingView } from './components/ChargingView';
import { AlertsView } from './components/AlertsView';
import { DiagnosticModal } from './components/DiagnosticModal';
import { BottomNav, TabType } from './components/BottomNav';
import { ToastBanner } from './components/ToastBanner';
import { useBatteryManager } from './hooks/useBatteryManager';
import { usePWAInstall } from './hooks/usePWAInstall';
import { loadSettings, saveSettings, loadSessions } from './utils/storage';
import { processBatteryAlerts } from './utils/alertEngine';
import { isNativeAndroid, isForegroundServiceRunning, requestBatteryOptimization } from './services/backgroundService';
import { AlertSettings } from './types';

export const App: React.FC = () => {
  const [activeTab, setActiveTab] = useState<TabType>('home');
  const [settings, setSettings] = useState<AlertSettings>(loadSettings);
  const [sessions, setSessions] = useState(loadSessions);
  const [isDiagnosticOpen, setIsDiagnosticOpen] = useState(false);
  const [serviceActive, setServiceActive] = useState<boolean>(false);
  const [showInstallBanner, setShowInstallBanner] = useState(true);

  const { battery, isNative } = useBatteryManager();
  const { isInstallable, installApp } = usePWAInstall();

  // Check initial foreground service status
  useEffect(() => {
    if (isNativeAndroid()) {
      setServiceActive(isForegroundServiceRunning());
    } else {
      setServiceActive(true); // Active in browser tab
    }
  }, []);

  // Process alerts whenever battery status or settings change
  useEffect(() => {
    processBatteryAlerts(battery, settings);
    // Refresh sessions
    setSessions(loadSessions());
  }, [battery, settings]);

  const handleUpdateSettings = (newSettings: AlertSettings) => {
    setSettings(newSettings);
    saveSettings(newSettings);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex flex-col font-sans max-w-md mx-auto relative shadow-2xl border-x border-slate-900">
      {/* Top Header Bar */}
      <Header
        serviceActive={serviceActive}
        onOpenOptimization={requestBatteryOptimization}
      />

      {/* PWA Install Banner */}
      {!isNative && isInstallable && showInstallBanner && (
        <ToastBanner
          isInstallable={isInstallable}
          onInstall={installApp}
          onDismiss={() => setShowInstallBanner(false)}
        />
      )}

      {/* Main Content Area */}
      <main className="flex-1 p-4 overflow-y-auto">
        {activeTab === 'home' && (
          <HomeView
            battery={battery}
            settings={settings}
            isNative={isNative}
            serviceActive={serviceActive}
            onToggleService={setServiceActive}
          />
        )}

        {activeTab === 'charging' && (
          <ChargingView
            battery={battery}
            sessions={sessions}
          />
        )}

        {activeTab === 'alerts' && (
          <AlertsView
            settings={settings}
            onUpdateSettings={handleUpdateSettings}
          />
        )}

        {activeTab === 'diagnostics' && (
          <div className="space-y-4 pb-24">
            <h2 className="text-lg font-bold text-white px-1">ডায়াগনস্টিক ও সিস্টেম স্থিতি</h2>
            <button
              onClick={() => setIsDiagnosticOpen(true)}
              className="w-full py-3 rounded-2xl bg-slate-900 border border-slate-800 text-emerald-400 font-semibold text-sm hover:bg-slate-800 transition"
            >
              সম্পূর্ণ সিস্টেম রিপোর্ট দেখুন
            </button>
            <div className="p-4 rounded-2xl bg-slate-900/60 border border-slate-800 text-xs text-slate-400 space-y-2 leading-relaxed">
              <p className="font-semibold text-slate-200">কেন চার্জিং সহকারী সবচেয়ে নির্ভরযোগ্য?</p>
              <p>১. ব্যাকগ্রাউন্ডে অ্যান্ড্রয়েড অফিসিয়াল Foreground Service ব্যবহার করা হয়।</p>
              <p>২. স্ক্রিন অফ থাকলেও ব্যাটারি লেভেল এবং তাপমাত্রা মনিটর করা হয়।</p>
              <p>৩. ৮০%, ৯০% বা আপনার পছন্দের টার্গেটে পৌঁছালে বাংলায় স্পষ্ট ভয়েস অ্যালার্ট দেয়।</p>
            </div>
          </div>
        )}
      </main>

      {/* System Diagnostic Modal */}
      <DiagnosticModal
        isOpen={isDiagnosticOpen || activeTab === 'diagnostics'}
        onClose={() => {
          setIsDiagnosticOpen(false);
          if (activeTab === 'diagnostics') setActiveTab('home');
        }}
        serviceActive={serviceActive}
      />

      {/* Fixed Bottom Navigation */}
      <BottomNav
        activeTab={activeTab}
        onChangeTab={setActiveTab}
      />
    </div>
  );
};

export default App;

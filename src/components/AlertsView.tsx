import React from 'react';
import {
  Bell,
  Volume2,
  Sliders,
  Thermometer,
  RotateCcw,
  CheckCircle2,
  Plug,
  Zap,
} from 'lucide-react';
import { AlertSettings } from '../types';
import { toBn } from '../utils/bnUtils';
import { useBengaliVoice } from '../hooks/useBengaliVoice';

interface AlertsViewProps {
  settings: AlertSettings;
  onUpdateSettings: (newSettings: AlertSettings) => void;
}

export const AlertsView: React.FC<AlertsViewProps> = ({ settings, onUpdateSettings }) => {
  const { speak } = useBengaliVoice();

  const handleToggle = (key: keyof AlertSettings) => {
    onUpdateSettings({
      ...settings,
      [key]: !settings[key],
    });
  };

  const handleNumberChange = (key: keyof AlertSettings, val: number) => {
    onUpdateSettings({
      ...settings,
      [key]: val,
    });
  };

  return (
    <div className="space-y-6 pb-24">
      {/* Header Banner */}
      <div className="bg-gradient-to-r from-emerald-900/40 via-slate-900 to-slate-900 border border-emerald-500/20 rounded-2xl p-4">
        <div className="flex items-center gap-3">
          <div className="p-2.5 rounded-xl bg-emerald-500/20 text-emerald-400">
            <Sliders className="w-5 h-5" />
          </div>
          <div>
            <h2 className="font-bold text-base text-white">অ্যালার্ট ও সতর্কবার্তা কনফিগারেশন</h2>
            <p className="text-xs text-slate-400">আপনার প্রয়োজন অনুযায়ী ব্যাটারি স্তর ও ভয়েস নির্ধারণ করুন</p>
          </div>
        </div>
      </div>

      {/* Target Battery Percentage Slider */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 space-y-3">
        <div className="flex items-center justify-between">
          <label className="text-sm font-semibold text-white flex items-center gap-2">
            <Zap className="w-4 h-4 text-emerald-400" />
            টার্গেট চার্জিং লেভেল (Target Level)
          </label>
          <span className="text-base font-bold text-emerald-400 px-3 py-0.5 rounded-lg bg-emerald-500/10 border border-emerald-500/30">
            {toBn(settings.targetPercentage)}%
          </span>
        </div>
        <p className="text-xs text-slate-400">
          ব্যাটারির আয়ু দীর্ঘ রাখতে বিশেষজ্ঞরা সাধারণত ৮০% বা ৯০% এ চার্জ থামানোর পরামর্শ দেন।
        </p>
        <input
          type="range"
          min="50"
          max="100"
          step="5"
          value={settings.targetPercentage}
          onChange={(e) => handleNumberChange('targetPercentage', parseInt(e.target.value, 10))}
          className="w-full h-2 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-emerald-500"
        />
        <div className="flex justify-between text-[11px] text-slate-500 font-medium">
          <span>৫০%</span>
          <span>৮০% (প্রস্তাবিত)</span>
          <span>৯০%</span>
          <span>১০০%</span>
        </div>
      </div>

      {/* Low Battery Percentage Slider */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 space-y-3">
        <div className="flex items-center justify-between">
          <label className="text-sm font-semibold text-white flex items-center gap-2">
            <Plug className="w-4 h-4 text-rose-400" />
            লো ব্যাটারি লেভেল (Low Battery)
          </label>
          <span className="text-base font-bold text-rose-400 px-3 py-0.5 rounded-lg bg-rose-500/10 border border-rose-500/30">
            {toBn(settings.lowBatteryPercentage)}%
          </span>
        </div>
        <input
          type="range"
          min="10"
          max="35"
          step="5"
          value={settings.lowBatteryPercentage}
          onChange={(e) => handleNumberChange('lowBatteryPercentage', parseInt(e.target.value, 10))}
          className="w-full h-2 bg-slate-700 rounded-lg appearance-none cursor-pointer accent-rose-500"
        />
        <div className="flex justify-between text-[11px] text-slate-500 font-medium">
          <span>১০%</span>
          <span>২০% (প্রস্তাবিত)</span>
          <span>৩৫%</span>
        </div>
      </div>

      {/* Master Audio & Notification Toggles */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 space-y-4">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">প্রধান মাধ্যম</h3>

        {/* Bengali Voice Toggle */}
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-amber-500/10 text-amber-400">
              <Volume2 className="w-5 h-5" />
            </div>
            <div>
              <div className="text-sm font-semibold text-white">বাংলা ভয়েস অ্যালার্ট (Bengali Voice)</div>
              <div className="text-xs text-slate-400">অ্যান্ড্রয়েড নেটিভ টিটিএস অথবা ব্রাউজার স্পিচ</div>
            </div>
          </div>
          <button
            onClick={() => handleToggle('voiceAlerts')}
            className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
              settings.voiceAlerts ? 'bg-emerald-500' : 'bg-slate-700'
            }`}
          >
            <span className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
              settings.voiceAlerts ? 'translate-x-6' : 'translate-x-1'
            }`} />
          </button>
        </div>

        {/* System Notification Alerts Toggle */}
        <div className="flex items-center justify-between pt-2 border-t border-slate-800/80">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-blue-500/10 text-blue-400">
              <Bell className="w-5 h-5" />
            </div>
            <div>
              <div className="text-sm font-semibold text-white">সিস্টেম নোটিফিকেশন (Notifications)</div>
              <div className="text-xs text-slate-400">পপ-আপ ও ফোরগ্রাউন্ড নোটিফিকেশন বার্তার মাধ্যমে</div>
            </div>
          </div>
          <button
            onClick={() => handleToggle('notificationAlerts')}
            className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
              settings.notificationAlerts ? 'bg-emerald-500' : 'bg-slate-700'
            }`}
          >
            <span className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
              settings.notificationAlerts ? 'translate-x-6' : 'translate-x-1'
            }`} />
          </button>
        </div>
      </div>

      {/* Individual Alert Triggers (Section 6) */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 space-y-3.5">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-400">সতর্কতার কারণসমূহ</h3>

        {/* Charger Connected Alert */}
        <div className="flex items-center justify-between py-1">
          <div>
            <div className="text-sm text-slate-200">চার্জার সংযুক্ত সতর্কতা</div>
            <div className="text-xs text-slate-500">"চার্জার সংযুক্ত হয়েছে"</div>
          </div>
          <input
            type="checkbox"
            checked={settings.alertConnected}
            onChange={() => handleToggle('alertConnected')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>

        {/* Charger Disconnected Alert */}
        <div className="flex items-center justify-between py-1 border-t border-slate-800/60">
          <div>
            <div className="text-sm text-slate-200">চার্জার খুলে নেওয়ার সতর্কতা</div>
            <div className="text-xs text-slate-500">"চার্জার খুলে নেওয়া হয়েছে"</div>
          </div>
          <input
            type="checkbox"
            checked={settings.alertDisconnected}
            onChange={() => handleToggle('alertDisconnected')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>

        {/* 80% Alert */}
        <div className="flex items-center justify-between py-1 border-t border-slate-800/60">
          <div>
            <div className="text-sm text-slate-200">৮০% ব্যাটারি সতর্কতা</div>
            <div className="text-xs text-slate-500">"ব্যাটারি ৮০ শতাংশ হয়েছে"</div>
          </div>
          <input
            type="checkbox"
            checked={settings.alert80}
            onChange={() => handleToggle('alert80')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>

        {/* 90% Alert */}
        <div className="flex items-center justify-between py-1 border-t border-slate-800/60">
          <div>
            <div className="text-sm text-slate-200">৯০% ব্যাটারি সতর্কতা</div>
            <div className="text-xs text-slate-500">"ব্যাটারি ৯০ শতাংশ হয়েছে"</div>
          </div>
          <input
            type="checkbox"
            checked={settings.alert90}
            onChange={() => handleToggle('alert90')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>

        {/* 100% Charging Complete Alert */}
        <div className="flex items-center justify-between py-1 border-t border-slate-800/60">
          <div>
            <div className="text-sm text-slate-200">১০০% সম্পূর্ণ চার্জ সতর্কতা</div>
            <div className="text-xs text-slate-500">"চার্জ সম্পূর্ণ হয়েছে"</div>
          </div>
          <input
            type="checkbox"
            checked={settings.alert100}
            onChange={() => handleToggle('alert100')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>

        {/* High Temperature Warning */}
        <div className="flex items-center justify-between py-1 border-t border-slate-800/60">
          <div>
            <div className="text-sm text-slate-200 flex items-center gap-1.5">
              <Thermometer className="w-3.5 h-3.5 text-rose-400" />
              উচ্চ তাপমাত্রা সতর্কতা ({toBn(settings.highTempThreshold)}°সে+)
            </div>
            <div className="text-xs text-slate-500">"ব্যাটারির তাপমাত্রা বেশি"</div>
          </div>
          <input
            type="checkbox"
            checked={settings.alertHighTemp}
            onChange={() => handleToggle('alertHighTemp')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>

        {/* Repeated Reminder */}
        <div className="flex items-center justify-between py-1 border-t border-slate-800/60">
          <div>
            <div className="text-sm text-slate-200 flex items-center gap-1.5">
              <RotateCcw className="w-3.5 h-3.5 text-amber-400" />
              পুনরাবৃত্তিমূলক অনুস্মারক (প্রতি {toBn(settings.repeatIntervalMinutes)} মিনিটে)
            </div>
            <div className="text-xs text-slate-500">টার্গেট পূরণের পরেও প্লাগ লাগানো থাকলে বারবার স্মরণ করাবে</div>
          </div>
          <input
            type="checkbox"
            checked={settings.repeatedReminder}
            onChange={() => handleToggle('repeatedReminder')}
            className="w-5 h-5 accent-emerald-500 rounded cursor-pointer"
          />
        </div>
      </div>

      {/* Test Playback Button */}
      <button
        onClick={() => speak("চার্জিং সহকারী সতর্কতা ব্যবস্থা সফলভাবে সক্রিয় আছে")}
        className="w-full py-3 px-4 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-sm flex items-center justify-center gap-2 shadow-lg shadow-emerald-600/20 transition active:scale-98"
      >
        <Volume2 className="w-4 h-4" />
        বর্তমান সেটিংস দিয়ে ভয়েস টেস্ট করুন
      </button>
    </div>
  );
};

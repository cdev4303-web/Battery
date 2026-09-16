import React from 'react';
import {
  Zap,
  Gauge,
  Thermometer,
  Activity,
  Cpu,
  Shield,
  Volume2,
  Clock,
  BatteryCharging,
  Power,
  RefreshCw,
} from 'lucide-react';
import { BatteryInfo, AlertSettings } from '../types';
import { BatteryIndicator } from './BatteryIndicator';
import {
  toBn,
  formatTemperatureBn,
  formatVoltageBn,
  formatCurrentBn,
  formatPowerBn,
  formatCapacityBn,
  formatDurationBn,
  getPluggedTypeBn,
  NOT_AVAILABLE_BN,
} from '../utils/bnUtils';
import { useBengaliVoice } from '../hooks/useBengaliVoice';
import {
  startForegroundService,
  stopForegroundService,
  requestBatteryOptimization,
} from '../services/backgroundService';

interface HomeViewProps {
  battery: BatteryInfo;
  settings: AlertSettings;
  isNative: boolean;
  serviceActive: boolean;
  onToggleService: (active: boolean) => void;
}

export const HomeView: React.FC<HomeViewProps> = ({
  battery,
  settings,
  isNative,
  serviceActive,
  onToggleService,
}) => {
  const { speak, isSpeaking } = useBengaliVoice();

  const handleToggleService = () => {
    if (!isNative) {
      onToggleService(!serviceActive);
      return;
    }

    if (serviceActive) {
      const stopped = stopForegroundService();
      if (stopped) onToggleService(false);
    } else {
      const started = startForegroundService();
      if (started) onToggleService(true);
    }
  };

  return (
    <div className="space-y-6 pb-24">
      {/* 3D Visual Battery Indicator */}
      <BatteryIndicator
        level={battery.level}
        isCharging={battery.isCharging}
        targetLevel={settings.targetPercentage}
        temperature={battery.temperature}
        status={battery.status}
      />

      {/* Android Native Foreground Monitoring Control Card */}
      <div className="bg-slate-900/90 border border-slate-800 rounded-2xl p-4 shadow-lg backdrop-blur-md">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className={`p-2.5 rounded-xl ${serviceActive ? 'bg-emerald-500/20 text-emerald-400' : 'bg-slate-800 text-slate-400'}`}>
              <Shield className="w-5 h-5" />
            </div>
            <div>
              <h2 className="font-semibold text-sm text-white">
                {isNative ? 'অ্যান্ড্রয়েড ফোরগ্রাউন্ড সার্ভিস' : 'ওয়েব ব্যাকগ্রাউন্ড গার্ড'}
              </h2>
              <p className="text-xs text-slate-400 mt-0.5">
                {isNative
                  ? 'অ্যাপ মিনিমাইজ বা স্ক্রিন অফ থাকলেও অফিশিয়াল ব্যাকগ্রাউন্ড মনিটরিং'
                  : 'ব্রাউজার ট্যাবে সার্বক্ষণিক লাইভ ব্যাটারি অ্যালার্ট'}
              </p>
            </div>
          </div>

          <button
            onClick={handleToggleService}
            className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none ${
              serviceActive ? 'bg-emerald-500' : 'bg-slate-700'
            }`}
            title="সার্ভিস অন বা অফ করুন"
          >
            <span
              className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                serviceActive ? 'translate-x-6' : 'translate-x-1'
              }`}
            />
          </button>
        </div>

        {isNative && (
          <div className="mt-3.5 pt-3 border-t border-slate-800/80 flex items-center justify-between">
            <span className="text-xs text-slate-400 flex items-center gap-1.5">
              <span className="w-2 h-2 rounded-full bg-emerald-400" />
              নেটিভ পারসিস্টেন্ট নোটিফিকেশন প্রস্তুত
            </span>
            <button
              onClick={requestBatteryOptimization}
              className="text-xs text-emerald-400 hover:text-emerald-300 font-medium underline underline-offset-2 flex items-center gap-1"
            >
              অপটিমাইজেশন অনুমতি
            </button>
          </div>
        )}
      </div>

      {/* Real-time Technical Metrics Grid (Section 4) */}
      <div className="space-y-3">
        <div className="flex items-center justify-between px-1">
          <h2 className="text-sm font-semibold text-slate-300 flex items-center gap-2">
            <Gauge className="w-4 h-4 text-emerald-400" />
            ব্যাটারি ও চার্জিং বিস্তারিত
          </h2>
          <span className="text-[11px] text-slate-500 flex items-center gap-1">
            <RefreshCw className="w-3 h-3 animate-spin-slow" />
            রিয়েল-টাইম আপডেট
          </span>
        </div>

        <div className="grid grid-cols-2 gap-3">
          {/* Temperature */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Thermometer className="w-4 h-4 text-rose-400" />
              <span>তাপমাত্রা</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.temperature !== null ? formatTemperatureBn(battery.temperature) : NOT_AVAILABLE_BN}
            </div>
          </div>

          {/* Voltage */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Activity className="w-4 h-4 text-cyan-400" />
              <span>ভোল্টেজ</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.voltage !== null ? formatVoltageBn(battery.voltage) : NOT_AVAILABLE_BN}
            </div>
          </div>

          {/* Current */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Zap className="w-4 h-4 text-amber-400" />
              <span>কারেন্ট (Current)</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.current !== null ? formatCurrentBn(battery.current) : NOT_AVAILABLE_BN}
            </div>
          </div>

          {/* Power */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Power className="w-4 h-4 text-violet-400" />
              <span>পাওয়ার (Power)</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.power !== null ? formatPowerBn(battery.power) : NOT_AVAILABLE_BN}
            </div>
          </div>

          {/* Capacity */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <BatteryCharging className="w-4 h-4 text-emerald-400" />
              <span>ব্যাটারি ক্যাপাসিটি</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.capacity !== null ? formatCapacityBn(battery.capacity) : NOT_AVAILABLE_BN}
            </div>
          </div>

          {/* Technology */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Cpu className="w-4 h-4 text-blue-400" />
              <span>প্রযুক্তি (Tech)</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.technology ? battery.technology : NOT_AVAILABLE_BN}
            </div>
          </div>

          {/* Plugged Source */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Zap className="w-4 h-4 text-emerald-400" />
              <span>চার্জারের ধরন</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {getPluggedTypeBn(battery.pluggedType)}
            </div>
          </div>

          {/* Remaining Time / Full Time */}
          <div className="bg-slate-900/60 border border-slate-800 rounded-xl p-3.5 flex flex-col justify-between">
            <div className="flex items-center gap-2 text-slate-400 text-xs">
              <Clock className="w-4 h-4 text-amber-400" />
              <span>{battery.isCharging ? 'সম্পূর্ণ হতে সময়' : 'অবশিষ্ট সময়'}</span>
            </div>
            <div className="mt-2 font-semibold text-sm text-white">
              {battery.isCharging
                ? formatDurationBn(battery.chargingTime)
                : formatDurationBn(battery.dischargingTime)}
            </div>
          </div>
        </div>
      </div>

      {/* Bengali Voice Quick Test Bar */}
      <div className="bg-slate-900/80 border border-slate-800 rounded-2xl p-4 shadow-sm">
        <div className="flex items-center justify-between mb-3">
          <h2 className="text-sm font-semibold text-white flex items-center gap-2">
            <Volume2 className="w-4 h-4 text-amber-400" />
            বাংলা ভয়েস অ্যালার্ট পরীক্ষা
          </h2>
          <span className="text-xs text-slate-400">
            {isSpeaking ? 'কথা বলছে...' : 'পরীক্ষা করতে ট্যাপ করুন'}
          </span>
        </div>

        <div className="grid grid-cols-2 gap-2">
          <button
            onClick={() => speak("চার্জার সংযুক্ত হয়েছে")}
            className="text-left text-xs bg-slate-800/70 hover:bg-slate-800 border border-slate-700/60 text-slate-200 p-2.5 rounded-xl transition active:scale-95"
          >
            "চার্জার সংযুক্ত হয়েছে"
          </button>
          <button
            onClick={() => speak(`ব্যাটারি ${toBn(settings.targetPercentage)} শতাংশ হয়েছে`)}
            className="text-left text-xs bg-slate-800/70 hover:bg-slate-800 border border-slate-700/60 text-slate-200 p-2.5 rounded-xl transition active:scale-95"
          >
            "ব্যাটারি {toBn(settings.targetPercentage)}% হয়েছে"
          </button>
          <button
            onClick={() => speak("চার্জ সম্পূর্ণ হয়েছে")}
            className="text-left text-xs bg-slate-800/70 hover:bg-slate-800 border border-slate-700/60 text-slate-200 p-2.5 rounded-xl transition active:scale-95"
          >
            "চার্জ সম্পূর্ণ হয়েছে"
          </button>
          <button
            onClick={() => speak("চার্জার খুলে নেওয়া হয়েছে")}
            className="text-left text-xs bg-slate-800/70 hover:bg-slate-800 border border-slate-700/60 text-slate-200 p-2.5 rounded-xl transition active:scale-95"
          >
            "চার্জার খুলে নেওয়া হয়েছে"
          </button>
        </div>
      </div>
    </div>
  );
};

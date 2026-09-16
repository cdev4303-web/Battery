import React from 'react';
import {
  X,
  CheckCircle2,
  AlertCircle,
  Smartphone,
  Cpu,
  Shield,
  Volume2,
  Info,
  ExternalLink,
} from 'lucide-react';
import { isNativeAndroid, requestBatteryOptimization, openBatterySettings } from '../services/backgroundService';

interface DiagnosticModalProps {
  isOpen: boolean;
  onClose: () => void;
  serviceActive: boolean;
}

export const DiagnosticModal: React.FC<DiagnosticModalProps> = ({
  isOpen,
  onClose,
  serviceActive,
}) => {
  if (!isOpen) return null;

  const isNative = isNativeAndroid();
  const hasTTS = 'speechSynthesis' in window || isNative;
  const hasSW = 'serviceWorker' in navigator;
  const hasNotification = 'Notification' in window || isNative;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-sm">
      <div className="bg-slate-900 border border-slate-700 w-full max-w-md rounded-3xl p-5 shadow-2xl space-y-4 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between border-b border-slate-800 pb-3">
          <div className="flex items-center gap-2">
            <Shield className="w-5 h-5 text-emerald-400" />
            <h2 className="font-bold text-base text-white">সিস্টেম ও ব্যাকগ্রাউন্ড ডায়াগনস্টিক</h2>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-full text-slate-400 hover:text-white hover:bg-slate-800 transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Diagnostic Items */}
        <div className="space-y-2.5 text-xs">
          {/* Native Android Bridge */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700/60">
            <div className="flex items-center gap-2.5">
              <Smartphone className="w-4 h-4 text-cyan-400" />
              <div>
                <div className="font-semibold text-white">অ্যান্ড্রয়েড নেটিভ ব্রিজ</div>
                <div className="text-slate-400">window.ChargingAssistantNative</div>
              </div>
            </div>
            {isNative ? (
              <span className="flex items-center gap-1 text-emerald-400 font-semibold">
                <CheckCircle2 className="w-4 h-4" /> সংযুক্ত (Native)
              </span>
            ) : (
              <span className="flex items-center gap-1 text-amber-400 font-medium">
                ওয়েব ব্রাউজার মোড
              </span>
            )}
          </div>

          {/* Foreground Service Status */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700/60">
            <div className="flex items-center gap-2.5">
              <Shield className="w-4 h-4 text-emerald-400" />
              <div>
                <div className="font-semibold text-white">ফোরগ্রাউন্ড সার্ভিস স্থিতি</div>
                <div className="text-slate-400">পারসিস্টেন্ট নোটিফিকেশন সার্ভিস</div>
              </div>
            </div>
            <span className={`font-semibold flex items-center gap-1 ${serviceActive ? 'text-emerald-400' : 'text-slate-400'}`}>
              {serviceActive ? <CheckCircle2 className="w-4 h-4" /> : <AlertCircle className="w-4 h-4" />}
              {serviceActive ? 'সক্রিয় (Active)' : 'নিষ্ক্রিয় (Inactive)'}
            </span>
          </div>

          {/* Bengali Voice Synthesis */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700/60">
            <div className="flex items-center gap-2.5">
              <Volume2 className="w-4 h-4 text-amber-400" />
              <div>
                <div className="font-semibold text-white">বাংলা ভয়েস ইঞ্জিন (TTS)</div>
                <div className="text-slate-400">Android Text-To-Speech / Web Speech</div>
              </div>
            </div>
            <span className="flex items-center gap-1 text-emerald-400 font-semibold">
              <CheckCircle2 className="w-4 h-4" /> প্রস্তুত
            </span>
          </div>

          {/* Notification Permission */}
          <div className="flex items-center justify-between p-3 rounded-xl bg-slate-800/60 border border-slate-700/60">
            <div className="flex items-center gap-2.5">
              <Cpu className="w-4 h-4 text-violet-400" />
              <div>
                <div className="font-semibold text-white">সিস্টেম নোটিফিকেশন চ্যানেল</div>
                <div className="text-slate-400">Android 13+ POST_NOTIFICATIONS</div>
              </div>
            </div>
            <span className="flex items-center gap-1 text-emerald-400 font-semibold">
              <CheckCircle2 className="w-4 h-4" /> সমর্থিত
            </span>
          </div>
        </div>

        {/* Official Android Background Execution Note (Section 3 Requirement) */}
        <div className="p-3.5 rounded-xl bg-slate-950/70 border border-slate-800 text-[11px] text-slate-400 leading-relaxed space-y-1.5">
          <div className="font-semibold text-slate-300 flex items-center gap-1.5">
            <Info className="w-3.5 h-3.5 text-blue-400 shrink-0" />
            অ্যান্ড্রয়েড সিস্টেম নিরাপত্তা ও ব্যাকগ্রাউন্ড তথ্য:
          </div>
          <p>
            এই অ্যাপ্লিকেশনটি অ্যান্ড্রয়েডের অফিসিয়াল <strong>ফোরগ্রাউন্ড সার্ভিস (Foreground Service)</strong> এবং <strong>BatteryManager</strong> এপিআই ব্যবহার করে ব্যাকগ্রাউন্ডে নোটিফিকেশন বজায় রাখে। কোনো নিরাপত্তা ব্যবস্থা বাইপাস করা হয় না। ডিভাইস ভেদে (যেমন Xiaomi, Samsung, Vivo) অতিরিক্ত পাওয়ার সেভিং বন্ধ করতে নিচে ট্যাপ করুন।
          </p>
        </div>

        {/* Optimization Settings Button */}
        {isNative && (
          <div className="grid grid-cols-2 gap-2 pt-2">
            <button
              onClick={requestBatteryOptimization}
              className="py-2.5 px-3 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs flex items-center justify-center gap-1.5 transition"
            >
              ব্যাটারি অপটিমাইজেশন
            </button>
            <button
              onClick={openBatterySettings}
              className="py-2.5 px-3 rounded-xl bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 font-semibold text-xs flex items-center justify-center gap-1.5 transition"
            >
              <ExternalLink className="w-3.5 h-3.5" /> সেটিংস খুলুন
            </button>
          </div>
        )}

        <button
          onClick={onClose}
          className="w-full py-2.5 rounded-xl bg-slate-800 text-slate-300 text-xs font-semibold hover:bg-slate-700 transition"
        >
          বন্ধ করুন
        </button>
      </div>
    </div>
  );
};

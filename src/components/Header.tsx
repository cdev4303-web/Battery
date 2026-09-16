import React from 'react';
import { Zap, ShieldCheck, ShieldAlert, Smartphone } from 'lucide-react';
import { isNativeAndroid } from '../services/backgroundService';

interface HeaderProps {
  serviceActive: boolean;
  onOpenOptimization: () => void;
}

export const Header: React.FC<HeaderProps> = ({ serviceActive, onOpenOptimization }) => {
  const isNative = isNativeAndroid();

  return (
    <header className="sticky top-0 z-30 bg-slate-900/80 backdrop-blur-md border-b border-slate-800/80 px-4 py-3 flex items-center justify-between">
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 rounded-xl bg-gradient-to-br from-emerald-500 to-teal-700 flex items-center justify-center shadow-lg shadow-emerald-500/20 border border-emerald-400/30">
          <Zap className="w-5 h-5 text-amber-300 fill-amber-300" />
        </div>
        <div>
          <h1 className="font-bold text-lg leading-tight text-white flex items-center gap-1.5">
            চার্জিং সহকারী
            <span className="text-[10px] font-semibold tracking-wider uppercase px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              PRO
            </span>
          </h1>
          <p className="text-xs text-slate-400 font-medium">অ্যান্ড্রয়েড ব্যাটারি হেলথ ও ভয়েস গার্ড</p>
        </div>
      </div>

      <div className="flex items-center gap-2">
        {isNative ? (
          <button
            onClick={onOpenOptimization}
            title="ব্যাটারি অপটিমাইজেশন সেটিংস"
            className="flex items-center gap-1.5 text-xs px-2.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 border border-slate-700 text-slate-200 transition-colors"
          >
            <Smartphone className="w-3.5 h-3.5 text-emerald-400" />
            <span className="hidden sm:inline">অপটিমাইজেশন</span>
          </button>
        ) : null}

        <div
          className={`flex items-center gap-1.5 text-xs px-2.5 py-1.5 rounded-lg border font-medium ${
            serviceActive
              ? 'bg-emerald-950/60 border-emerald-500/40 text-emerald-300'
              : 'bg-slate-800/60 border-slate-700 text-slate-400'
          }`}
        >
          {serviceActive ? (
            <>
              <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
              <ShieldCheck className="w-3.5 h-3.5" />
              <span>মনিটরিং অন</span>
            </>
          ) : (
            <>
              <ShieldAlert className="w-3.5 h-3.5 text-slate-500" />
              <span>মনিটরিং অফ</span>
            </>
          )}
        </div>
      </div>
    </header>
  );
};

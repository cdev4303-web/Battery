import React from 'react';
import { Download, X } from 'lucide-react';

interface ToastBannerProps {
  isInstallable: boolean;
  onInstall: () => void;
  onDismiss: () => void;
}

export const ToastBanner: React.FC<ToastBannerProps> = ({
  isInstallable,
  onInstall,
  onDismiss,
}) => {
  if (!isInstallable) return null;

  return (
    <div className="fixed top-16 left-4 right-4 z-50 max-w-md mx-auto animate-in fade-in slide-in-from-top-4 duration-300">
      <div className="bg-slate-900 border border-emerald-500/40 rounded-2xl p-3 shadow-2xl backdrop-blur-md flex items-center justify-between gap-3">
        <div className="flex items-center gap-2.5">
          <div className="p-2 rounded-xl bg-emerald-500/20 text-emerald-400">
            <Download className="w-4 h-4" />
          </div>
          <div>
            <div className="text-xs font-bold text-white">অ্যাপ ইনস্টল করুন (PWA)</div>
            <div className="text-[11px] text-slate-400">অফলাইন ও দ্রুত ব্যবহারের জন্য হোম স্ক্রিনে যোগ করুন</div>
          </div>
        </div>
        <div className="flex items-center gap-1.5 shrink-0">
          <button
            onClick={onInstall}
            className="px-2.5 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white font-semibold text-xs transition"
          >
            ইনস্টল
          </button>
          <button
            onClick={onDismiss}
            className="p-1 rounded-lg text-slate-400 hover:text-white"
          >
            <X className="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  );
};

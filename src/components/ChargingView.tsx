import React from 'react';
import {
  BatteryCharging,
  Clock,
  History,
  CheckCircle,
  Lightbulb,
  ShieldCheck,
  TrendingUp,
} from 'lucide-react';
import { BatteryInfo, ChargingSession } from '../types';
import { toBn, formatDurationBn, getPluggedTypeBn } from '../utils/bnUtils';

interface ChargingViewProps {
  battery: BatteryInfo;
  sessions: ChargingSession[];
}

export const ChargingView: React.FC<ChargingViewProps> = ({ battery, sessions }) => {
  return (
    <div className="space-y-6 pb-24">
      {/* Current Active Charging State Card */}
      <div className="bg-gradient-to-br from-slate-900 to-slate-950 border border-slate-800 rounded-2xl p-5 shadow-xl">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className={`p-3 rounded-2xl ${battery.isCharging ? 'bg-emerald-500/20 text-emerald-400 animate-pulse' : 'bg-slate-800 text-slate-400'}`}>
              <BatteryCharging className="w-6 h-6" />
            </div>
            <div>
              <h2 className="text-base font-bold text-white">
                {battery.isCharging ? 'চার্জিং সেশন সক্রিয়' : 'বর্তমানে ডিসচার্জিং মোড'}
              </h2>
              <p className="text-xs text-slate-400 mt-0.5">
                {battery.isCharging
                  ? `${getPluggedTypeBn(battery.pluggedType)} এর মাধ্যমে চার্জ হচ্ছে`
                  : 'ডিভাইসটি চার্জারে সংযুক্ত নেই'}
              </p>
            </div>
          </div>
          <div className="text-right">
            <span className="text-2xl font-black text-white">{toBn(battery.level)}%</span>
          </div>
        </div>

        {battery.isCharging && (
          <div className="mt-4 pt-4 border-t border-slate-800/80 grid grid-cols-2 gap-3 text-xs">
            <div className="bg-slate-800/40 p-2.5 rounded-xl border border-slate-800">
              <span className="text-slate-400">চার্জিং গতি:</span>
              <span className="block font-semibold text-emerald-400 mt-0.5">
                {battery.current ? `${toBn(Math.abs(battery.current))} mA` : 'স্ট্যান্ডার্ড চার্জিং'}
              </span>
            </div>
            <div className="bg-slate-800/40 p-2.5 rounded-xl border border-slate-800">
              <span className="text-slate-400">আনুমানিক সময়:</span>
              <span className="block font-semibold text-white mt-0.5">
                {formatDurationBn(battery.chargingTime)}
              </span>
            </div>
          </div>
        )}
      </div>

      {/* Charging Advice & Battery Preservation Tips */}
      <div className="bg-slate-900/70 border border-slate-800 rounded-2xl p-4 space-y-3">
        <h3 className="text-sm font-semibold text-amber-400 flex items-center gap-2">
          <Lightbulb className="w-4 h-4" />
          লিথিয়াম-আয়ন ব্যাটারি সুরক্ষার পরামর্শ
        </h3>
        <ul className="text-xs text-slate-300 space-y-2 leading-relaxed">
          <li className="flex items-start gap-2">
            <CheckCircle className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
            <span><strong>২০%-৮০% নিয়ম:</strong> ব্যাটারি ২০% এর নিচে নামার আগে চার্জে দিন এবং ৮০% থেকে ৯০% হলে খুলে ফেলুন। এতে সাইকেল লাইফ দ্বিগুণ হয়।</span>
          </li>
          <li className="flex items-start gap-2">
            <CheckCircle className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
            <span><strong>তাপমাত্রা নিয়ন্ত্রণ:</strong> চার্জ করার সময় ভারী গেম খেলবেন না বা ফোন গরম স্থানে রাখবেন না। ৪২°সে এর উপরে ব্যাটারির ক্ষতি হয়।</span>
          </li>
          <li className="flex items-start gap-2">
            <CheckCircle className="w-3.5 h-3.5 text-emerald-400 shrink-0 mt-0.5" />
            <span><strong>সারারাত প্লাগ ইন পরিহার:</strong> ফুল চার্জ হওয়ার পর প্লাগ ইন রাখা এড়িয়ে চলুন, আমাদের ভয়েস অ্যালার্ট আপনাকে স্মরণ করিয়ে দেবে।</span>
          </li>
        </ul>
      </div>

      {/* Recent Sessions History */}
      <div className="space-y-3">
        <h3 className="text-sm font-semibold text-slate-300 flex items-center gap-2 px-1">
          <History className="w-4 h-4 text-emerald-400" />
          পূর্ববর্তী চার্জিং সেশনসমূহ ({toBn(sessions.length)})
        </h3>

        {sessions.length === 0 ? (
          <div className="bg-slate-900/50 border border-slate-800/80 rounded-2xl p-8 text-center text-slate-400 text-xs">
            <Clock className="w-8 h-8 mx-auto text-slate-600 mb-2" />
            এখনও কোনো সেশন রেকর্ড হয়নি। ফোন চার্জে দিলে এবং খুললে স্বয়ংক্রিয়ভাবে সেশন হিস্ট্রি যুক্ত হবে।
          </div>
        ) : (
          <div className="space-y-2">
            {sessions.map((s) => (
              <div
                key={s.id}
                className="bg-slate-900/80 border border-slate-800 rounded-xl p-3 flex items-center justify-between text-xs"
              >
                <div>
                  <div className="font-semibold text-white flex items-center gap-1.5">
                    <TrendingUp className="w-3.5 h-3.5 text-emerald-400" />
                    {toBn(s.startLevel)}% হতে {s.endLevel !== undefined ? `${toBn(s.endLevel)}%` : 'চলমান'}
                    {s.percentageGained !== undefined && (
                      <span className="text-[10px] text-emerald-400 font-bold bg-emerald-500/10 px-1.5 py-0.2 rounded">
                        +{toBn(s.percentageGained)}%
                      </span>
                    )}
                  </div>
                  <div className="text-[11px] text-slate-400 mt-0.5">
                    {new Date(s.startTime).toLocaleTimeString('bn-BD', { hour: '2-digit', minute: '2-digit' })} • {getPluggedTypeBn(s.pluggedType)}
                  </div>
                </div>
                <div className="text-right text-slate-400">
                  <span className="font-medium text-slate-200">
                    {s.durationMinutes ? `${toBn(s.durationMinutes)} মিনিট` : 'চলছে'}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

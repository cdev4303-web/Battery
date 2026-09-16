import React from 'react';
import { Zap, AlertTriangle } from 'lucide-react';
import { toBn, getChargingStatusBn } from '../utils/bnUtils';

interface BatteryIndicatorProps {
  level: number;
  isCharging: boolean;
  targetLevel: number;
  temperature: number | null;
  status: string;
}

export const BatteryIndicator: React.FC<BatteryIndicatorProps> = ({
  level,
  isCharging,
  targetLevel,
  temperature,
  status,
}) => {
  // Clamped percentage with complete null/NaN safety
  const safeLevel = (typeof level === 'number' && !isNaN(level)) ? Math.min(100, Math.max(0, level)) : 75;
  const pct = safeLevel;
  const safeTarget = (typeof targetLevel === 'number' && !isNaN(targetLevel)) ? targetLevel : 80;

  // Determine dynamic gradient color based on battery state
  const getColorClasses = () => {
    if (isCharging) {
      return {
        glow: 'from-emerald-500/20 via-teal-500/10 to-transparent',
        liquid: 'from-emerald-400 via-teal-500 to-emerald-600',
        text: 'text-emerald-400',
        badge: 'bg-emerald-500/10 text-emerald-400 border-emerald-500/30',
      };
    }
    if (pct <= 20) {
      return {
        glow: 'from-rose-500/20 via-red-500/10 to-transparent',
        liquid: 'from-rose-400 via-red-500 to-rose-600',
        text: 'text-rose-400',
        badge: 'bg-rose-500/10 text-rose-400 border-rose-500/30',
      };
    }
    if (pct <= 45) {
      return {
        glow: 'from-amber-500/20 via-yellow-500/10 to-transparent',
        liquid: 'from-amber-400 via-yellow-500 to-amber-600',
        text: 'text-amber-400',
        badge: 'bg-amber-500/10 text-amber-400 border-amber-500/30',
      };
    }
    return {
      glow: 'from-blue-500/20 via-teal-500/10 to-transparent',
      liquid: 'from-teal-400 via-emerald-500 to-teal-600',
      text: 'text-teal-400',
      badge: 'bg-teal-500/10 text-teal-400 border-teal-500/30',
    };
  };

  const colors = getColorClasses();

  return (
    <div className="relative flex flex-col items-center justify-center py-6">
      {/* Background Ambient Radial Glow */}
      <div
        className={`absolute w-72 h-72 rounded-full bg-gradient-to-br ${colors.glow} blur-3xl -z-10 pointer-events-none transition-all duration-700`}
      />

      {/* 3D Glass Battery Container */}
      <div className="relative flex flex-col items-center">
        {/* Metal Top Anode Terminal */}
        <div className="w-16 h-4 rounded-t-lg bg-gradient-to-r from-slate-400 via-slate-200 to-slate-500 shadow-md border-t border-slate-300/40" />

        {/* Battery Outer Chamber */}
        <div className="relative w-44 h-72 rounded-3xl p-2.5 bg-slate-900/90 border-2 border-slate-700/80 shadow-2xl backdrop-blur-md flex flex-col justify-end overflow-hidden">
          {/* Subtle 3D Glass Inner Rim */}
          <div className="absolute inset-1 rounded-[22px] border border-white/10 pointer-events-none z-20" />

          {/* Target Indicator Line Marker */}
          {safeTarget > 0 && (
            <div
              className="absolute left-0 right-0 z-20 border-b-2 border-dashed border-amber-400/70 flex items-center justify-end pr-2 transition-all duration-300"
              style={{ bottom: `${safeTarget}%` }}
              title={`টার্গেট: ${toBn(safeTarget)}%`}
            >
              <span className="text-[10px] font-bold bg-amber-500/90 text-slate-950 px-1.5 py-0.5 rounded shadow">
                লক্ষ্য {toBn(safeTarget)}%
              </span>
            </div>
          )}

          {/* Liquid Battery Energy Fill */}
          <div
            className={`w-full rounded-2xl bg-gradient-to-t ${colors.liquid} relative transition-all duration-1000 ease-out shadow-inner`}
            style={{ height: `${pct}%` }}
          >
            {/* Wave / Liquid Shimmer Light Effect */}
            <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/20 to-transparent opacity-60 animate-pulse" />

            {/* Segment Divider Lines */}
            <div className="absolute inset-0 flex flex-col justify-evenly opacity-30 pointer-events-none">
              <div className="border-b border-white" />
              <div className="border-b border-white" />
              <div className="border-b border-white" />
            </div>
          </div>

          {/* Center Glass Reflection Highlight */}
          <div className="absolute top-4 left-4 bottom-4 w-1.5 rounded-full bg-white/20 z-20 pointer-events-none" />

          {/* Charging Bolt Animation in Center */}
          {isCharging && (
            <div className="absolute inset-0 flex items-center justify-center z-20">
              <div className="p-3 rounded-full bg-slate-950/60 backdrop-blur-md border border-amber-400/40 shadow-xl shadow-amber-500/20 animate-bounce-soft">
                <Zap className="w-8 h-8 text-amber-300 fill-amber-300 animate-pulse" />
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Numerical Percentage Display in Bengali */}
      <div className="mt-5 text-center">
        <div className="flex items-center justify-center gap-1">
          <span className="text-5xl font-black tracking-tight text-white font-sans drop-shadow-sm">
            {toBn(pct)}
          </span>
          <span className={`text-3xl font-bold ${colors.text}`}>%</span>
        </div>

        {/* Status Badge */}
        <div className="mt-2 flex items-center justify-center gap-2">
          <span className={`inline-flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-semibold border ${colors.badge}`}>
            {isCharging ? (
              <Zap className="w-3.5 h-3.5 fill-current animate-pulse" />
            ) : pct <= 20 ? (
              <AlertTriangle className="w-3.5 h-3.5" />
            ) : null}
            {getChargingStatusBn(status, isCharging)}
          </span>

          {temperature !== null && (
            <span className={`px-2.5 py-1 rounded-full text-xs font-medium border ${
              temperature >= 40
                ? 'bg-rose-500/10 text-rose-400 border-rose-500/30 font-bold'
                : 'bg-slate-800/80 text-slate-300 border-slate-700'
            }`}>
              {toBn(temperature.toFixed(1))}°সে
            </span>
          )}
        </div>
      </div>
    </div>
  );
};

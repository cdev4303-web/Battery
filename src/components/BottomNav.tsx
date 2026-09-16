import React from 'react';
import { Home, BatteryCharging, Bell, ShieldCheck } from 'lucide-react';

export type TabType = 'home' | 'charging' | 'alerts' | 'diagnostics';

interface BottomNavProps {
  activeTab: TabType;
  onChangeTab: (tab: TabType) => void;
}

export const BottomNav: React.FC<BottomNavProps> = ({ activeTab, onChangeTab }) => {
  const tabs = [
    { id: 'home' as TabType, label: 'হোম', icon: Home },
    { id: 'charging' as TabType, label: 'চার্জিং', icon: BatteryCharging },
    { id: 'alerts' as TabType, label: 'অ্যালার্ট', icon: Bell },
    { id: 'diagnostics' as TabType, label: 'ডায়াগনস্টিক', icon: ShieldCheck },
  ];

  return (
    <nav className="fixed bottom-0 left-0 right-0 z-40 bg-slate-900/90 backdrop-blur-lg border-t border-slate-800/80 px-2 py-2">
      <div className="max-w-md mx-auto grid grid-cols-4 gap-1">
        {tabs.map((tab) => {
          const Icon = tab.icon;
          const isActive = activeTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => onChangeTab(tab.id)}
              className={`flex flex-col items-center justify-center py-1.5 px-2 rounded-xl transition-all ${
                isActive
                  ? 'text-emerald-400 font-bold bg-emerald-500/10'
                  : 'text-slate-400 font-medium hover:text-slate-200'
              }`}
            >
              <Icon className={`w-5 h-5 mb-1 ${isActive ? 'stroke-[2.5]' : 'stroke-2'}`} />
              <span className="text-[11px] leading-none">{tab.label}</span>
            </button>
          );
        })}
      </div>
    </nav>
  );
};

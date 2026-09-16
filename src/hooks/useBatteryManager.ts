import { useState, useEffect, useRef } from 'react';
import { BatteryInfo, ChargingSession } from '../types';
import { isNativeAndroid } from '../services/backgroundService';
import { loadSessions, saveSessions } from '../utils/storage';

interface BatteryManagerAPI extends EventTarget {
  charging: boolean;
  chargingTime: number;
  dischargingTime: number;
  level: number;
  addEventListener(type: string, listener: EventListenerOrEventListenerObject): void;
  removeEventListener(type: string, listener: EventListenerOrEventListenerObject): void;
}

export function useBatteryManager() {
  const [battery, setBattery] = useState<BatteryInfo>({
    level: 75,
    isCharging: false,
    status: 'discharging',
    chargingTime: null,
    dischargingTime: null,
    temperature: null,
    voltage: null,
    current: null,
    power: null,
    technology: null,
    health: null,
    capacity: null,
    pluggedType: 'none',
    timestamp: Date.now(),
  });

  const [isSupported, setIsSupported] = useState<boolean>(true);
  const previousChargingRef = useRef<boolean>(false);
  const activeSessionRef = useRef<ChargingSession | null>(null);

  useEffect(() => {
    let timerId: number | null = null;
    let batteryApiInstance: BatteryManagerAPI | null = null;

    const pollNativeBattery = () => {
      if (window.ChargingAssistantNative && typeof window.ChargingAssistantNative.getBatteryInfo === 'function') {
        try {
          const jsonStr = window.ChargingAssistantNative.getBatteryInfo();
          if (jsonStr) {
            const data = JSON.parse(jsonStr);
            const level = typeof data.level === 'number' ? data.level : 0;
            const isCharging = Boolean(data.isCharging);
            const temp = typeof data.temperature === 'number' ? data.temperature : null;
            const voltage = typeof data.voltage === 'number' ? data.voltage : null;
            const current = typeof data.current === 'number' ? data.current : null;
            const capacity = typeof data.capacity === 'number' ? data.capacity : null;
            
            // Calculate power = voltage * current if available
            let power: number | null = null;
            if (voltage && current) {
              const v = voltage > 50 ? voltage / 1000 : voltage;
              const a = Math.abs(current) / 1000;
              power = +(v * a).toFixed(2);
            }

            const newInfo: BatteryInfo = {
              level,
              isCharging,
              status: data.status || (isCharging ? 'charging' : 'discharging'),
              chargingTime: data.chargingTime ?? null,
              dischargingTime: data.dischargingTime ?? null,
              temperature: temp,
              voltage,
              current,
              power,
              technology: data.technology || 'Li-ion',
              health: data.health || 'Good',
              capacity,
              pluggedType: data.pluggedType || (isCharging ? 'ac' : 'none'),
              timestamp: Date.now(),
            };

            setBattery(newInfo);
            handleSessionTransitions(isCharging, level, newInfo.pluggedType);
            return;
          }
        } catch (e) {
          console.error('Failed to parse native battery info', e);
        }
      }
    };

    const handleSessionTransitions = (isCharging: boolean, level: number, pluggedType: string) => {
      if (isCharging && !previousChargingRef.current) {
        // Charging started
        activeSessionRef.current = {
          id: Date.now().toString(),
          startTime: Date.now(),
          startLevel: level,
          pluggedType,
        };
      } else if (!isCharging && previousChargingRef.current && activeSessionRef.current) {
        // Charging stopped -> save session
        const currentSession = activeSessionRef.current;
        const endTime = Date.now();
        const durationMinutes = Math.max(1, Math.round((endTime - currentSession.startTime) / 60000));
        const percentageGained = Math.max(0, level - currentSession.startLevel);

        const completedSession: ChargingSession = {
          ...currentSession,
          endTime,
          endLevel: level,
          durationMinutes,
          percentageGained,
        };

        const existing = loadSessions();
        saveSessions([completedSession, ...existing]);
        activeSessionRef.current = null;
      }
      previousChargingRef.current = isCharging;
    };

    if (isNativeAndroid()) {
      // Android Native Bridge Mode: poll every 1.5 seconds
      pollNativeBattery();
      timerId = window.setInterval(pollNativeBattery, 1500);
    } else {
      // Standard Web Battery API Mode
      try {
        const nav = navigator as Navigator & { getBattery?: () => Promise<BatteryManagerAPI> };
        if (typeof nav.getBattery === 'function') {
          nav.getBattery().then((batteryManager) => {
            batteryApiInstance = batteryManager;
            const updateWebBattery = () => {
              const level = Math.round((batteryManager.level ?? 0.75) * 100);
              const isCharging = Boolean(batteryManager.charging);
              const newInfo: BatteryInfo = {
                level,
                isCharging,
                status: isCharging ? (level >= 100 ? 'full' : 'charging') : 'discharging',
                chargingTime: isFinite(batteryManager.chargingTime) ? batteryManager.chargingTime : null,
                dischargingTime: isFinite(batteryManager.dischargingTime) ? batteryManager.dischargingTime : null,
                temperature: null, // Web API does not provide temperature
                voltage: null, // Web API does not provide voltage
                current: null,
                power: null,
                technology: null,
                health: null,
                capacity: null,
                pluggedType: isCharging ? 'ac' : 'none',
                timestamp: Date.now(),
              };
              setBattery(newInfo);
              handleSessionTransitions(isCharging, level, newInfo.pluggedType);
            };

            updateWebBattery();
            batteryManager.addEventListener('levelchange', updateWebBattery);
            batteryManager.addEventListener('chargingchange', updateWebBattery);
            batteryManager.addEventListener('chargingtimechange', updateWebBattery);
            batteryManager.addEventListener('dischargingtimechange', updateWebBattery);
          }).catch((err) => {
            console.warn('Navigator getBattery error', err);
            setIsSupported(false);
          });
        } else {
          setIsSupported(false);
        }
      } catch (err) {
        console.warn('Navigator getBattery synchronous error', err);
        setIsSupported(false);
      }
    }

    return () => {
      if (timerId !== null) {
        clearInterval(timerId);
      }
      if (batteryApiInstance) {
        // Event listeners cleaned up when unmounted
      }
    };
  }, []);

  return { battery, isSupported, isNative: isNativeAndroid() };
}

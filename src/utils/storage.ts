import { AlertSettings, ChargingSession } from '../types';

const SETTINGS_KEY = 'charging_assistant_settings';
const SESSIONS_KEY = 'charging_assistant_sessions';

export const DEFAULT_SETTINGS: AlertSettings = {
  targetPercentage: 80,
  lowBatteryPercentage: 20,
  alert80: true,
  alert90: true,
  alert100: true,
  alertConnected: true,
  alertDisconnected: true,
  alertChargingComplete: true,
  alertHighTemp: true,
  highTempThreshold: 42,
  repeatedReminder: false,
  repeatIntervalMinutes: 3,
  voiceAlerts: true,
  notificationAlerts: true,
  keepScreenAwake: false,
  autoStartOnBoot: true,
};

export function loadSettings(): AlertSettings {
  try {
    const saved = localStorage.getItem(SETTINGS_KEY);
    if (saved) {
      return { ...DEFAULT_SETTINGS, ...JSON.parse(saved) };
    }
  } catch (e) {
    console.error('Failed to load settings from localStorage', e);
  }
  return DEFAULT_SETTINGS;
}

export function saveSettings(settings: AlertSettings): void {
  try {
    localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
    // Also sync to Android native bridge if available
    if (window.ChargingAssistantNative && typeof window.ChargingAssistantNative.syncSettings === 'function') {
      window.ChargingAssistantNative.syncSettings(JSON.stringify(settings));
    }
  } catch (e) {
    console.error('Failed to save settings', e);
  }
}

export function loadSessions(): ChargingSession[] {
  try {
    const saved = localStorage.getItem(SESSIONS_KEY);
    if (saved) {
      return JSON.parse(saved);
    }
  } catch (e) {
    console.error('Failed to load sessions', e);
  }
  return [];
}

export function saveSessions(sessions: ChargingSession[]): void {
  try {
    // Keep last 30 sessions
    const trimmed = sessions.slice(0, 30);
    localStorage.setItem(SESSIONS_KEY, JSON.stringify(trimmed));
  } catch (e) {
    console.error('Failed to save sessions', e);
  }
}

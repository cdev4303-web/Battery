export interface BatteryInfo {
  level: number; // 0 - 100
  isCharging: boolean;
  status: 'charging' | 'discharging' | 'full' | 'not_charging' | 'unknown';
  chargingTime: number | null; // seconds until full
  dischargingTime: number | null; // seconds until empty
  temperature: number | null; // Celsius
  voltage: number | null; // Volts or mV
  current: number | null; // mA (positive or negative)
  power: number | null; // Watts
  technology: string | null; // e.g. "Li-ion"
  health: string | null; // e.g. "Good", "Overheat"
  capacity: number | null; // mAh (if available)
  pluggedType: 'ac' | 'usb' | 'wireless' | 'none' | 'unknown';
  timestamp: number;
}

export interface AlertSettings {
  targetPercentage: number; // default 80
  lowBatteryPercentage: number; // default 20
  alert80: boolean;
  alert90: boolean;
  alert100: boolean;
  alertConnected: boolean;
  alertDisconnected: boolean;
  alertChargingComplete: boolean;
  alertHighTemp: boolean;
  highTempThreshold: number; // default 42°C
  repeatedReminder: boolean;
  repeatIntervalMinutes: number; // default 3
  voiceAlerts: boolean;
  notificationAlerts: boolean;
  keepScreenAwake: boolean;
  autoStartOnBoot: boolean;
}

export interface ChargingSession {
  id: string;
  startTime: number;
  endTime?: number;
  startLevel: number;
  endLevel?: number;
  pluggedType: string;
  durationMinutes?: number;
  percentageGained?: number;
}

export interface NativeBridge {
  isNativeAndroid: () => boolean;
  startForegroundService: () => void;
  stopForegroundService: () => void;
  getBatteryInfo: () => string; // JSON
  requestBatteryOptimization: () => void;
  openBatterySettings: () => void;
  speak: (text: string) => void;
  showNotification: (title: string, message: string) => void;
  syncSettings: (settingsJson: string) => void;
  isServiceRunning: () => boolean;
}

declare global {
  interface Window {
    ChargingAssistantNative?: NativeBridge;
  }
}

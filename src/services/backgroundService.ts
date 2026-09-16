/**
 * Background Service Controller & Native Bridge Coordinator
 */

export const isNativeAndroid = (): boolean => {
  return typeof window !== 'undefined' &&
    window.ChargingAssistantNative !== undefined &&
    typeof window.ChargingAssistantNative.isNativeAndroid === 'function' &&
    window.ChargingAssistantNative.isNativeAndroid();
};

export const isForegroundServiceRunning = (): boolean => {
  if (isNativeAndroid() && window.ChargingAssistantNative?.isServiceRunning) {
    try {
      return window.ChargingAssistantNative.isServiceRunning();
    } catch (e) {
      console.warn('Error checking service status', e);
    }
  }
  return false;
};

export const startForegroundService = (): boolean => {
  if (isNativeAndroid() && window.ChargingAssistantNative?.startForegroundService) {
    try {
      window.ChargingAssistantNative.startForegroundService();
      return true;
    } catch (e) {
      console.error('Failed to start Android Foreground Service', e);
    }
  }
  return false;
};

export const stopForegroundService = (): boolean => {
  if (isNativeAndroid() && window.ChargingAssistantNative?.stopForegroundService) {
    try {
      window.ChargingAssistantNative.stopForegroundService();
      return true;
    } catch (e) {
      console.error('Failed to stop Android Foreground Service', e);
    }
  }
  return false;
};

export const requestBatteryOptimization = (): void => {
  if (isNativeAndroid() && window.ChargingAssistantNative?.requestBatteryOptimization) {
    try {
      window.ChargingAssistantNative.requestBatteryOptimization();
    } catch (e) {
      console.error('Failed to request battery optimization', e);
    }
  } else {
    alert("ব্যাটারি অপটিমাইজেশন সেটিংস শুধুমাত্র অ্যান্ড্রয়েড অ্যাপে প্রযোজ্য।");
  }
};

export const openBatterySettings = (): void => {
  if (isNativeAndroid() && window.ChargingAssistantNative?.openBatterySettings) {
    try {
      window.ChargingAssistantNative.openBatterySettings();
    } catch (e) {
      console.error('Failed to open battery settings', e);
    }
  }
};

export const sendNotification = (title: string, message: string): void => {
  if (isNativeAndroid() && window.ChargingAssistantNative?.showNotification) {
    try {
      window.ChargingAssistantNative.showNotification(title, message);
      return;
    } catch (e) {
      console.error('Native notification failed', e);
    }
  }

  // Web Notification fallback
  if ('Notification' in window && Notification.permission === 'granted') {
    try {
      new Notification(title, {
        body: message,
        icon: './pwa-192x192.png',
        badge: './icon.svg',
      });
    } catch (e) {
      console.warn('Web notification failed', e);
    }
  }
};

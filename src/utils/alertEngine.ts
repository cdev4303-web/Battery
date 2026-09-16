import { BatteryInfo, AlertSettings } from '../types';
import { bengaliVoiceService } from '../services/bengaliVoiceService';
import { sendNotification } from '../services/backgroundService';
import { toBn } from './bnUtils';

interface AlertState {
  lastSpokenLevel: number | null;
  lastChargingState: boolean | null;
  lastReminderTime: number;
  highTempAlerted: boolean;
  completeAlerted: boolean;
}

const state: AlertState = {
  lastSpokenLevel: null,
  lastChargingState: null,
  lastReminderTime: 0,
  highTempAlerted: false,
  completeAlerted: false,
};

export function processBatteryAlerts(battery: BatteryInfo, settings: AlertSettings): void {
  const now = Date.now();

  // 1. Charger connected / disconnected transition
  if (state.lastChargingState !== null && state.lastChargingState !== battery.isCharging) {
    if (battery.isCharging && settings.alertConnected) {
      const msg = "চার্জার সংযুক্ত হয়েছে";
      if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
      if (settings.notificationAlerts) sendNotification("চার্জিং সহকারী", msg);
      state.completeAlerted = false;
    } else if (!battery.isCharging && settings.alertDisconnected) {
      const msg = "চার্জার খুলে নেওয়া হয়েছে";
      if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
      if (settings.notificationAlerts) sendNotification("চার্জিং সহকারী", msg);
      state.completeAlerted = false;
    }
  }
  state.lastChargingState = battery.isCharging;

  // 2. High Temperature Alert (> settings.highTempThreshold or > 42°C)
  if (battery.temperature !== null && settings.alertHighTemp) {
    if (battery.temperature >= settings.highTempThreshold && !state.highTempAlerted) {
      const msg = `ব্যাটারির তাপমাত্রা বেশি, ${toBn(battery.temperature.toFixed(0))} ডিগ্রি সেলসিয়াস`;
      if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
      if (settings.notificationAlerts) sendNotification("সতর্কতা: উচ্চ তাপমাত্রা", msg);
      state.highTempAlerted = true;
    } else if (battery.temperature < settings.highTempThreshold - 2) {
      state.highTempAlerted = false; // Reset when cooled down
    }
  }

  // 3. Charging Complete (100%)
  if (battery.isCharging && battery.level >= 100) {
    if (settings.alert100 && !state.completeAlerted) {
      const msg = "চার্জ সম্পূর্ণ হয়েছে, অনুগ্রহ করে চার্জারটি আনপ্লাগ করুন";
      if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
      if (settings.notificationAlerts) sendNotification("চার্জিং সম্পন্ন", msg);
      state.completeAlerted = true;
    }
  } else if (battery.level < 99) {
    state.completeAlerted = false;
  }

  // 4. Target Level reached (e.g. 80%, 90% or custom target)
  if (battery.isCharging) {
    const checkTarget = (targetVal: number, alertEnabled: boolean, msg: string) => {
      if (alertEnabled && battery.level >= targetVal && state.lastSpokenLevel !== targetVal) {
        if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
        if (settings.notificationAlerts) sendNotification("চার্জিং লক্ষ্য পূরণ", msg);
        state.lastSpokenLevel = targetVal;
        state.lastReminderTime = now;
      }
    };

    if (battery.level === 80) {
      checkTarget(80, settings.alert80, "ব্যাটারি ৮০ শতাংশ হয়েছে");
    } else if (battery.level === 90) {
      checkTarget(90, settings.alert90, "ব্যাটারি ৯০ শতাংশ হয়েছে");
    } else if (battery.level === settings.targetPercentage && settings.targetPercentage !== 80 && settings.targetPercentage !== 90 && settings.targetPercentage !== 100) {
      checkTarget(settings.targetPercentage, true, `ব্যাটারি ${toBn(settings.targetPercentage)} শতাংশ হয়েছে`);
    }

    // Repeated reminder if still plugged in and target reached
    if (settings.repeatedReminder && battery.level >= settings.targetPercentage) {
      const intervalMs = settings.repeatIntervalMinutes * 60 * 1000;
      if (now - state.lastReminderTime >= intervalMs) {
        const msg = `স্মারক: ব্যাটারি ${toBn(battery.level)} শতাংশে পৌঁছেছে, চার্জার সংযোগ বিচ্ছিন্ন করতে পারেন`;
        if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
        if (settings.notificationAlerts) sendNotification("চার্জিং অনুস্মারক", msg);
        state.lastReminderTime = now;
      }
    }
  }

  // 5. Low Battery alert when discharging
  if (!battery.isCharging && battery.level <= settings.lowBatteryPercentage && state.lastSpokenLevel !== -1) {
    const msg = `ব্যাটারি কম, ${toBn(battery.level)} শতাংশ অবশিষ্ট আছে, অনুগ্রহ করে চার্জার সংযুক্ত করুন`;
    if (settings.voiceAlerts) bengaliVoiceService.speak(msg);
    if (settings.notificationAlerts) sendNotification("ব্যাটারি কম!", msg);
    state.lastSpokenLevel = -1;
  } else if (battery.level > settings.lowBatteryPercentage + 5 && state.lastSpokenLevel === -1) {
    state.lastSpokenLevel = null;
  }
}

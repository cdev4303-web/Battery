// Bengali numerals and localization utilities

const BN_DIGITS = ['০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'];

export const NOT_AVAILABLE_BN = "এই তথ্যটি আপনার ডিভাইসে উপলব্ধ নয়";

/**
 * Converts any number or numeric string to Bengali numerals
 */
export function toBn(num: number | string | null | undefined): string {
  if (num === null || num === undefined || num === '') return '';
  return num.toString().replace(/\d/g, (digit) => BN_DIGITS[parseInt(digit, 10)] || digit);
}

/**
 * Formats battery percentage in Bengali
 */
export function formatPercentage(level: number | null | undefined): string {
  if (level === null || level === undefined || isNaN(level)) return NOT_AVAILABLE_BN;
  return `${toBn(Math.round(level))}%`;
}

/**
 * Formats time in minutes/hours in Bengali
 */
export function formatDurationBn(seconds: number | null | undefined): string {
  if (seconds === null || seconds === undefined || !isFinite(seconds) || seconds <= 0) {
    return NOT_AVAILABLE_BN;
  }
  const mins = Math.round(seconds / 60);
  if (mins < 60) {
    return `${toBn(mins)} মিনিট`;
  }
  const hours = Math.floor(mins / 60);
  const remainingMins = mins % 60;
  if (remainingMins === 0) {
    return `${toBn(hours)} ঘণ্টা`;
  }
  return `${toBn(hours)} ঘণ্টা ${toBn(remainingMins)} মিনিট`;
}

/**
 * Formats temperature in Bengali
 */
export function formatTemperatureBn(temp: number | null | undefined): string {
  if (temp === null || temp === undefined || isNaN(temp)) return NOT_AVAILABLE_BN;
  return `${toBn(temp.toFixed(1))}°সে`;
}

/**
 * Formats voltage in Bengali
 */
export function formatVoltageBn(voltage: number | null | undefined): string {
  if (voltage === null || voltage === undefined || isNaN(voltage)) return NOT_AVAILABLE_BN;
  // If voltage is in mV (e.g. 4200), convert to V
  const v = voltage > 50 ? voltage / 1000 : voltage;
  return `${toBn(v.toFixed(2))} ভোল্ট`;
}

/**
 * Formats electric current in Bengali
 */
export function formatCurrentBn(current: number | null | undefined): string {
  if (current === null || current === undefined || isNaN(current)) return NOT_AVAILABLE_BN;
  return `${toBn(Math.round(current))} মিলিঅ্যাম্পিয়ার (mA)`;
}

/**
 * Formats power in Bengali
 */
export function formatPowerBn(power: number | null | undefined): string {
  if (power === null || power === undefined || isNaN(power)) return NOT_AVAILABLE_BN;
  return `${toBn(power.toFixed(2))} ওয়াট (W)`;
}

/**
 * Formats battery capacity in Bengali
 */
export function formatCapacityBn(cap: number | null | undefined): string {
  if (cap === null || cap === undefined || isNaN(cap) || cap <= 0) return NOT_AVAILABLE_BN;
  return `${toBn(Math.round(cap))} এমএএইচ (mAh)`;
}

/**
 * Translate charging status to Bengali
 */
export function getChargingStatusBn(status: string, isCharging: boolean): string {
  if (status === 'full') return 'সম্পূর্ণ চার্জড (Full)';
  if (isCharging || status === 'charging') return 'চার্জ হচ্ছে (Charging)';
  if (status === 'discharging') return 'চার্জ হচ্ছে না (Discharging)';
  if (status === 'not_charging') return 'চার্জ বন্ধ (Not Charging)';
  return 'অজানা অবস্থা';
}

/**
 * Translate plugged type to Bengali
 */
export function getPluggedTypeBn(type: string): string {
  switch (type) {
    case 'ac':
      return 'এসি চার্জার (AC Adapter)';
    case 'usb':
      return 'ইউএসবি পোর্ট (USB)';
    case 'wireless':
      return 'ওয়্যারলেস চার্জার (Wireless)';
    case 'none':
      return 'সংযুক্ত নয় (Unplugged)';
    default:
      return NOT_AVAILABLE_BN;
  }
}

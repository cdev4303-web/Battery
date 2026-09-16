/**
 * Bengali Voice Alert Service
 * Handles native Android TTS, Web Speech API (bn-BD / bn-IN), and Web Audio chime fallback.
 */

class BengaliVoiceService {
  private synth: SpeechSynthesis | null = null;
  private audioCtx: AudioContext | null = null;
  private isInitialized = false;

  constructor() {
    if (typeof window !== 'undefined' && 'speechSynthesis' in window) {
      this.synth = window.speechSynthesis;
    }
  }

  private initAudioContext(): AudioContext | null {
    if (this.audioCtx) return this.audioCtx;
    try {
      const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (AudioContextClass) {
        this.audioCtx = new AudioContextClass();
      }
    } catch (e) {
      console.warn('Web Audio API not supported', e);
    }
    return this.audioCtx;
  }

  /**
   * Plays a pleasant chime sound as an auditory alert fallback
   */
  public playChime(frequency = 587.33, duration = 0.3): void {
    const ctx = this.initAudioContext();
    if (!ctx) return;

    try {
      if (ctx.state === 'suspended') {
        ctx.resume();
      }

      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      osc.type = 'sine';
      osc.frequency.setValueAtTime(frequency, ctx.currentTime);
      osc.frequency.exponentialRampToValueAtTime(frequency * 1.5, ctx.currentTime + duration * 0.5);

      gain.gain.setValueAtTime(0.3, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.01, ctx.currentTime + duration);

      osc.connect(gain);
      gain.connect(ctx.destination);

      osc.start(ctx.currentTime);
      osc.stop(ctx.currentTime + duration);
    } catch (e) {
      console.warn('Error playing chime', e);
    }
  }

  /**
   * Speaks the provided Bengali text.
   * Prioritizes Android native bridge, then Web Speech API, then audio tone.
   */
  public speak(text: string): Promise<boolean> {
    return new Promise((resolve) => {
      // 1. Android Native Bridge
      if (window.ChargingAssistantNative && typeof window.ChargingAssistantNative.speak === 'function') {
        try {
          window.ChargingAssistantNative.speak(text);
          resolve(true);
          return;
        } catch (e) {
          console.error('Error in Native Android speak()', e);
        }
      }

      // 2. Web Speech API Fallback
      if (this.synth) {
        try {
          this.synth.cancel(); // Cancel any ongoing speech

          const utterance = new SpeechSynthesisUtterance(text);
          utterance.lang = 'bn-BD';
          utterance.rate = 0.95; // Slightly slower for clear Bengali pronunciation
          utterance.pitch = 1.0;

          // Try to select a Bengali voice if present
          const voices = this.synth.getVoices();
          const bnVoice = voices.find(v => v.lang.startsWith('bn') || v.name.toLowerCase().includes('bengali') || v.name.toLowerCase().includes('bangla'));
          if (bnVoice) {
            utterance.voice = bnVoice;
          }

          utterance.onend = () => resolve(true);
          utterance.onerror = () => {
            // Fallback to chime on speech error
            this.playChime();
            resolve(false);
          };

          this.synth.speak(utterance);
          return;
        } catch (e) {
          console.warn('Web Speech API failed', e);
        }
      }

      // 3. Audio Chime Fallback
      this.playChime();
      resolve(true);
    });
  }
}

export const bengaliVoiceService = new BengaliVoiceService();

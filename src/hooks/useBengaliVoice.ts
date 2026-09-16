import { useState, useCallback } from 'react';
import { bengaliVoiceService } from '../services/bengaliVoiceService';

export function useBengaliVoice() {
  const [isSpeaking, setIsSpeaking] = useState(false);

  const speak = useCallback(async (text: string) => {
    setIsSpeaking(true);
    try {
      await bengaliVoiceService.speak(text);
    } finally {
      setIsSpeaking(false);
    }
  }, []);

  const playChime = useCallback(() => {
    bengaliVoiceService.playChime();
  }, []);

  return { speak, playChime, isSpeaking };
}

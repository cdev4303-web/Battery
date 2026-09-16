/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eefcf4',
          100: '#d6f7e4',
          200: '#b0eece',
          300: '#7be1b1',
          400: '#41cc8f',
          500: '#18b172',
          600: '#0d8e5a',
          700: '#0d714a',
          800: '#0e593c',
          900: '#0d4a33',
        },
        charging: {
          blue: '#3b82f6',
          green: '#10b981',
          amber: '#f59e0b',
          red: '#ef4444',
          purple: '#8b5cf6',
        }
      },
      fontFamily: {
        sans: ['Hind Siliguri', 'Noto Sans Bengali', 'sans-serif'],
      },
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'bounce-soft': 'bounce 2s infinite',
        'spin-slow': 'spin 8s linear infinite',
      }
    },
  },
  plugins: [],
}

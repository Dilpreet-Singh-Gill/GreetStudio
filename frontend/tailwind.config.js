/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        slate: {
          850: '#151e2e',
          950: '#0b1121'
        },
        primary: {
          500: '#6366f1',
          600: '#4f46e5'
        }
      }
    },
  },
  plugins: [],
}

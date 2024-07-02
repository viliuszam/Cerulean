/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        cerulean: {
          DEFAULT: '#007BA7',
          light: '#89CFF0',
        },
      },
    },
  },
  plugins: [],
}


import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/features/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/hooks/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/services/**/*.{js,ts,jsx,tsx,mdx}"
  ],
  theme: {
    extend: {
      colors: {
        background: "hsl(var(--background) / <alpha-value>)",
        foreground: "hsl(var(--foreground) / <alpha-value>)",
        surface: "hsl(var(--surface) / <alpha-value>)",
        "surface-muted": "hsl(var(--surface-muted) / <alpha-value>)",
        border: "hsl(var(--border) / <alpha-value>)",
        ring: "hsl(var(--ring) / <alpha-value>)",
        brand: "hsl(var(--brand) / <alpha-value>)",
        "brand-600": "hsl(var(--brand-600) / <alpha-value>)",
        "brand-700": "hsl(var(--brand-700) / <alpha-value>)",
        "brand-soft": "hsl(var(--brand-soft) / <alpha-value>)",
        success: "hsl(var(--success) / <alpha-value>)",
        warning: "hsl(var(--warning) / <alpha-value>)",
        danger: "hsl(var(--danger) / <alpha-value>)",
        "muted-foreground": "hsl(var(--muted-foreground) / <alpha-value>)"
      },
      borderRadius: {
        sm: "4px",
        md: "var(--radius)",
        lg: "var(--radius)"
      },
      boxShadow: {
        card: "0 8px 22px -14px rgba(15, 23, 42, 0.22)",
        promo: "0 12px 30px -16px rgba(251, 146, 60, 0.48)"
      },
      maxWidth: {
        page: "1200px"
      },
      fontSize: {
        "display-sm": ["1.875rem", { lineHeight: "2.3rem", fontWeight: "700" }],
        "display-md": ["2.25rem", { lineHeight: "2.8rem", fontWeight: "700" }],
        "heading-lg": ["1.5rem", { lineHeight: "2rem", fontWeight: "700" }],
        "heading-md": ["1.25rem", { lineHeight: "1.75rem", fontWeight: "700" }],
        "body-md": ["1rem", { lineHeight: "1.625rem", fontWeight: "400" }],
        "body-sm": ["0.875rem", { lineHeight: "1.4rem", fontWeight: "400" }]
      },
      backgroundImage: {
        "promo-soft": "linear-gradient(135deg, hsl(var(--brand-soft)) 0%, hsl(var(--surface)) 72%)",
        "promo-strong": "linear-gradient(135deg, hsl(var(--brand)) 0%, hsl(var(--brand-700)) 100%)"
      }
    }
  },
  plugins: []
};

export default config;

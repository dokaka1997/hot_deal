import type { ButtonHTMLAttributes } from "react";

import { cn } from "@/lib/utils";

type ButtonVariant = "primary" | "secondary" | "ghost" | "danger" | "cta";
type ButtonSize = "sm" | "md" | "lg";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
}

const variantClassMap: Record<ButtonVariant, string> = {
  primary: "border border-[#ff6a00] bg-[#ff6a00] text-white hover:bg-[#f15f00] hover:border-[#f15f00] shadow-[0_12px_20px_-14px_rgba(255,106,0,0.95)]",
  secondary: "border border-brand/25 bg-brand-soft text-brand-700 hover:bg-brand/15",
  ghost: "border border-transparent bg-transparent text-foreground hover:bg-surface-muted",
  danger: "border border-danger bg-danger text-white hover:bg-danger/90",
  cta: "border border-[#ff6a00] bg-[#ff6a00] text-white hover:bg-[#f15f00] shadow-[0_12px_20px_-14px_rgba(255,106,0,0.95)]"
};

const sizeClassMap: Record<ButtonSize, string> = {
  sm: "h-8 px-3 text-sm",
  md: "h-10 px-4 text-sm font-semibold",
  lg: "h-11 px-5 text-base"
};

export const Button = ({
  className,
  variant = "primary",
  size = "md",
  type = "button",
  ...props
}: ButtonProps) => {
  return (
    <button
      className={cn(
        "inline-flex items-center justify-center gap-1.5 rounded-md transition-all duration-150",
        "focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2",
        "focus-visible:ring-offset-background",
        "disabled:pointer-events-none disabled:opacity-60",
        variantClassMap[variant],
        sizeClassMap[size],
        className
      )}
      type={type}
      {...props}
    />
  );
};

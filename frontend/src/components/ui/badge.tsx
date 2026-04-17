import type { HTMLAttributes } from "react";

import { cn } from "@/lib/utils";

type BadgeVariant = "default" | "brand" | "success" | "danger" | "source";

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
}

const variantClassMap: Record<BadgeVariant, string> = {
  default: "border border-border bg-surface-muted text-muted-foreground",
  brand: "border border-brand/30 bg-brand-soft text-brand-700",
  success: "border border-success/30 bg-success/10 text-success",
  danger: "border border-danger/35 bg-danger/10 text-danger",
  source: "border border-foreground/12 bg-surface text-foreground"
};

export const Badge = ({ variant = "default", className, ...props }: BadgeProps) => {
  return (
    <span
      className={cn(
        "inline-flex h-7 items-center rounded-md px-2.5 text-xs font-semibold uppercase tracking-wide",
        variantClassMap[variant],
        className
      )}
      {...props}
    />
  );
};

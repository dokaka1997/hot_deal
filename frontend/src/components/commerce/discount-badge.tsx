import { Badge } from "@/components/ui";
import { cn } from "@/lib/utils";

interface DiscountBadgeProps {
  percent: number | null | undefined;
  className?: string;
  compact?: boolean;
}

export const DiscountBadge = ({ percent, className, compact = false }: DiscountBadgeProps) => {
  if (percent === null || percent === undefined || Number.isNaN(percent)) {
    return null;
  }

  const roundedPercent = Math.max(0, Math.round(percent));

  return (
    <Badge
      className={cn(
        compact ? "h-6 px-2 text-[11px]" : "h-8 px-2.5 text-sm",
        "border-transparent bg-gradient-to-r from-[#ff3b30] to-[#ff6a00] font-extrabold text-white shadow-[0_8px_16px_-10px_rgba(255,106,0,0.85)]",
        className
      )}
      variant="danger"
    >
      -{roundedPercent}%
    </Badge>
  );
};

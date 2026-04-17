import { Badge } from "@/components/ui";
import { cn } from "@/lib/utils";

interface SourceBadgeProps {
  sourceName?: string | null;
  sourceCode?: string | null;
  className?: string;
}

export const SourceBadge = ({ sourceName, sourceCode, className }: SourceBadgeProps) => {
  const label = sourceName || sourceCode || "Nguon khac";

  return (
    <Badge className={cn("h-6 rounded-sm px-2 normal-case tracking-normal text-[11px]", className)} variant="source">
      {label}
    </Badge>
  );
};

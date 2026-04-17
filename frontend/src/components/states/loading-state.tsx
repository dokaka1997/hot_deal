import { Card, CardDescription, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface LoadingStateProps {
  title?: string;
  description?: string;
  className?: string;
}

export const LoadingState = ({
  title = "Loading",
  description = "Please wait while data is being prepared.",
  className
}: LoadingStateProps) => {
  return (
    <Card className={cn("flex items-center gap-4 bg-promo-soft", className)}>
      <span className="inline-block h-5 w-5 animate-spin rounded-full border-2 border-brand/25 border-t-brand-700" />
      <div>
        <CardTitle className="text-base">{title}</CardTitle>
        <CardDescription className="mt-1">{description}</CardDescription>
      </div>
    </Card>
  );
};

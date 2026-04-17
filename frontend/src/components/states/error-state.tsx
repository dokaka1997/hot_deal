import { Button } from "@/components/ui/button";
import { Card, CardDescription, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import { getReadableErrorMessage } from "@/services/api";

interface ErrorStateProps {
  error: unknown;
  title?: string;
  retryLabel?: string;
  onRetry?: () => void;
  className?: string;
}

export const ErrorState = ({
  error,
  title = "Unable to load data",
  retryLabel = "Thu lai",
  onRetry,
  className
}: ErrorStateProps) => {
  return (
    <Card className={cn("space-y-4 border-danger/40 bg-danger/5", className)}>
      <div>
        <CardTitle>{title}</CardTitle>
        <CardDescription>{getReadableErrorMessage(error)}</CardDescription>
      </div>

      {onRetry ? (
        <div>
          <Button onClick={onRetry} variant="danger">
            {retryLabel}
          </Button>
        </div>
      ) : null}
    </Card>
  );
};

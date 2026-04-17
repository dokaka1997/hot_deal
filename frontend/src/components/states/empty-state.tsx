import type { ReactNode } from "react";

import { Card, CardDescription, CardTitle } from "@/components/ui/card";
import { cn } from "@/lib/utils";

interface EmptyStateProps {
  title?: string;
  description?: string;
  action?: ReactNode;
  className?: string;
}

export const EmptyState = ({
  title = "Khong co ket qua",
  description = "Hay dieu chinh bo loc hoac quay lai sau.",
  action,
  className
}: EmptyStateProps) => {
  return (
    <Card className={cn("border-brand/25 bg-brand-soft/50", className)}>
      <CardTitle>{title}</CardTitle>
      <CardDescription>{description}</CardDescription>
      {action ? <div className="mt-4">{action}</div> : null}
    </Card>
  );
};

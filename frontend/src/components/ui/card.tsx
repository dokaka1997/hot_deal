import type { HTMLAttributes, PropsWithChildren } from "react";

import { cn } from "@/lib/utils";

interface CardProps extends HTMLAttributes<HTMLElement> {
  as?: "section" | "article" | "div";
}

export const Card = ({ as = "section", className, ...props }: CardProps) => {
  const Element = as;
  return (
    <Element
      className={cn("rounded-[14px] border border-border bg-surface p-5 shadow-[0_8px_20px_-14px_rgba(15,23,42,0.22)]", className)}
      {...props}
    />
  );
};

interface CardTitleProps extends PropsWithChildren {
  className?: string;
}

export const CardTitle = ({ className, children }: CardTitleProps) => {
  return <h2 className={cn("text-heading-md text-foreground", className)}>{children}</h2>;
};

export const CardDescription = ({ className, children }: CardTitleProps) => {
  return <p className={cn("mt-1.5 text-body-sm text-muted-foreground", className)}>{children}</p>;
};

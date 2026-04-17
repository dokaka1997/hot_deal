import type { HTMLAttributes, PropsWithChildren, ReactNode } from "react";

import { cn } from "@/lib/utils";

interface PageContainerProps extends PropsWithChildren {
  className?: string;
}

export const PageContainer = ({ children, className }: PageContainerProps) => {
  return <div className={cn("page-shell", className)}>{children}</div>;
};

interface PageStackProps extends PropsWithChildren {
  className?: string;
}

export const PageStack = ({ children, className }: PageStackProps) => {
  return <div className={cn("page-stack", className)}>{children}</div>;
};

interface SectionContainerProps extends HTMLAttributes<HTMLElement> {
  as?: "section" | "article" | "div";
  title?: string;
  description?: string;
  kicker?: string;
  action?: ReactNode;
}

export const SectionContainer = ({
  as = "section",
  title,
  description,
  kicker,
  action,
  className,
  children,
  ...props
}: SectionContainerProps) => {
  const Element = as;

  return (
    <Element className={cn("section-surface", className)} {...props}>
      {title || description || kicker || action ? (
        <div className="section-head mb-[14px]">
          <div className="space-y-1">
            {kicker ? <p className="section-kicker">{kicker}</p> : null}
            {title ? <h2 className="text-[22px] font-extrabold leading-[1.1] text-[#1f1f1f]">{title}</h2> : null}
            {description ? <p className="mt-1 text-[12px] text-[#8a8a8a]">{description}</p> : null}
          </div>
          {action}
        </div>
      ) : null}
      {children}
    </Element>
  );
};

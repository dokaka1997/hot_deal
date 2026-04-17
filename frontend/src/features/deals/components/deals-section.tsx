import type { PropsWithChildren, ReactNode } from "react";

import { SectionContainer } from "@/components/layout/page-container";

interface DealsSectionProps extends PropsWithChildren {
  title: string;
  description: string;
  kicker?: string;
  action?: ReactNode;
}

export const DealsSection = ({ title, description, kicker, action, children }: DealsSectionProps) => {
  return (
    <SectionContainer action={action} description={description} kicker={kicker} title={title}>
      {children}
    </SectionContainer>
  );
};

import type { PropsWithChildren } from "react";

import { AppHeader } from "@/components/layout/app-header";
import { PageContainer } from "@/components/layout/page-container";

export default function AdminLayout({ children }: PropsWithChildren) {
  return (
    <div className="min-h-screen bg-background">
      <AppHeader />
      <PageContainer>{children}</PageContainer>
    </div>
  );
}

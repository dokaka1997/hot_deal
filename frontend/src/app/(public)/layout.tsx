"use client";

import type { PropsWithChildren } from "react";
import { usePathname } from "next/navigation";

import { AppFooter } from "@/components/layout/app-footer";
import { AppHeader } from "@/components/layout/app-header";

export default function PublicLayout({ children }: PropsWithChildren) {
  const pathname = usePathname();
  const showHeader = pathname !== "/";
  const showFooter = pathname !== "/";

  return (
    <div className="min-h-screen bg-[#f7f7f7]">
      {showHeader ? <AppHeader /> : null}
      {children}
      {showFooter ? <AppFooter /> : null}
    </div>
  );
}

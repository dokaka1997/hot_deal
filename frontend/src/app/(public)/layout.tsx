"use client";

import type { PropsWithChildren } from "react";
import { usePathname } from "next/navigation";

import { AppHeader } from "@/components/layout/app-header";

export default function PublicLayout({ children }: PropsWithChildren) {
  const pathname = usePathname();
  const showHeader = pathname !== "/";

  return (
    <div className="min-h-screen bg-[#f7f7f7]">
      {showHeader ? <AppHeader /> : null}
      {children}
    </div>
  );
}

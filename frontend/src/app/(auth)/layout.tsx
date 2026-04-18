import type { PropsWithChildren } from "react";

export default function AuthLayout({ children }: PropsWithChildren) {
  return <div className="min-h-screen bg-[#f9f9fe]">{children}</div>;
}

import type { PropsWithChildren, ReactNode } from "react";

interface HomeLayoutShellProps extends PropsWithChildren {
  sidebar?: ReactNode;
}

const SidebarScaffold = () => {
  return (
    <div className="sticky top-[86px] space-y-4">
      <section className="rounded-[14px] border border-[#ffd9c2] bg-white p-4 shadow-[0_10px_22px_-18px_rgba(255,106,0,0.8)]">
        <div className="h-5 w-32 rounded-sm bg-[#ffe7d8]" />
        <div className="mt-3 h-28 rounded-[12px] bg-[#fff5ee]" />
      </section>
      <section className="rounded-[14px] border border-[#ffd9c2] bg-white p-4 shadow-[0_10px_22px_-18px_rgba(255,106,0,0.8)]">
        <div className="h-5 w-24 rounded-sm bg-[#ffe7d8]" />
        <div className="mt-3 h-44 rounded-[12px] bg-[#fff5ee]" />
      </section>
      <section className="rounded-[14px] border border-[#ffd9c2] bg-white p-4 shadow-[0_10px_22px_-18px_rgba(255,106,0,0.8)]">
        <div className="h-5 w-28 rounded-sm bg-[#ffe7d8]" />
        <div className="mt-3 h-36 rounded-[12px] bg-[#fff5ee]" />
      </section>
    </div>
  );
};

export const HomeLayoutShell = ({ children, sidebar }: HomeLayoutShellProps) => {
  return (
    <main className="mx-auto w-full max-w-[1440px] px-6 py-4">
      <div className="grid grid-cols-1 gap-6 xl:grid-cols-[minmax(0,1fr)_352px]">
        <section className="min-w-0">{children}</section>
        <aside className="hidden xl:block">{sidebar ?? <SidebarScaffold />}</aside>
      </div>
    </main>
  );
};

import Link from "next/link";

import { APP_CONFIG } from "@/config/app";
import { APP_ROUTES } from "@/config/routes";

const navItems = [
  { label: "Deal cua toi", href: APP_ROUTES.public.deals },
  { label: "Thong bao", href: APP_ROUTES.public.deals },
  { label: "Dang nhap", href: APP_ROUTES.public.deals }
] as const;

export const AppHeader = () => {
  return (
    <header className="sticky top-0 z-40 border-b border-[#e9e9e9] bg-white/95 backdrop-blur-[6px]">
      <div className="mx-auto flex h-[52px] w-full max-w-[1440px] items-center gap-5 px-5">
        <Link className="inline-flex items-center gap-2" href={APP_ROUTES.public.home}>
          <span className="inline-flex h-6 w-6 items-center justify-center rounded-[6px] bg-[#ff6a00] text-[10px] font-bold text-white">
            HD
          </span>
          <span className="flex flex-col leading-none">
            <span className="text-[24px] font-bold text-[#ff6a00]">{APP_CONFIG.name}</span>
            <span className="text-[11px] font-medium text-[#8d8d8d]">San deal moi ngay</span>
          </span>
        </Link>

        <form className="hidden min-w-0 flex-1 items-center md:flex">
          <div className="flex h-[34px] w-[380px] items-center overflow-hidden rounded-full border border-[#ededed] bg-[#f5f5f5] shadow-[inset_0_1px_2px_rgba(0,0,0,0.04)]">
            <input
              className="h-full flex-1 bg-transparent px-3.5 text-[12px] text-[#222] outline-none placeholder:text-[#9a9a9a]"
              placeholder="Tim kiem deal, san pham, thuong hieu..."
              type="text"
            />
            <button
              className="mr-0.5 inline-flex h-[34px] w-[72px] items-center justify-center rounded-full bg-[#ff6a00] text-[12px] font-bold text-white transition-colors hover:bg-[#f26500]"
              type="button"
            >
              Tim kiem
            </button>
          </div>
        </form>

        <nav aria-label="Top Actions" className="ml-auto hidden items-center gap-6 lg:flex">
          {navItems.map((item) => (
            <Link
              className="inline-flex items-center text-[12px] font-medium text-[#444] transition-colors hover:text-[#ff6a00]"
              href={item.href}
              key={item.label}
            >
              {item.label}
            </Link>
          ))}
        </nav>
      </div>
    </header>
  );
};

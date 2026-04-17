import Link from "next/link";

interface CategoryItem {
  key: string;
  label: string;
  href: string;
  icon: "all" | "phone" | "laptop" | "audio" | "watch" | "fashion" | "shoes" | "home" | "beauty" | "more";
  active?: boolean;
}

interface CategoryIconRowProps {
  items: CategoryItem[];
}

const CategoryIcon = ({ type, active = false }: { type: CategoryItem["icon"]; active?: boolean }) => {
  const stroke = active ? "#ffffff" : "#8b8b8b";

  switch (type) {
    case "phone":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <rect height="16" rx="2.5" stroke={stroke} strokeWidth="1.8" width="10" x="7" y="4" />
          <circle cx="12" cy="17.5" fill={stroke} r="0.9" />
        </svg>
      );
    case "laptop":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <rect height="9" rx="1.5" stroke={stroke} strokeWidth="1.8" width="12" x="6" y="5" />
          <path d="M4 17H20" stroke={stroke} strokeLinecap="round" strokeWidth="1.8" />
        </svg>
      );
    case "audio":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <path d="M4.5 12a7.5 7.5 0 0 1 15 0" stroke={stroke} strokeLinecap="round" strokeWidth="1.8" />
          <rect height="5" rx="1" stroke={stroke} strokeWidth="1.8" width="2.5" x="4" y="12" />
          <rect height="5" rx="1" stroke={stroke} strokeWidth="1.8" width="2.5" x="17.5" y="12" />
        </svg>
      );
    case "watch":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <rect height="10" rx="2" stroke={stroke} strokeWidth="1.8" width="10" x="7" y="7" />
          <path d="M10 7V4h4v3M10 17v3h4v-3" stroke={stroke} strokeLinecap="round" strokeWidth="1.8" />
        </svg>
      );
    case "fashion":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <path d="M7.5 7.5 9 4h6l1.5 3.5L12 10 7.5 7.5ZM8.3 8.2V20h7.4V8.2" stroke={stroke} strokeWidth="1.8" />
        </svg>
      );
    case "shoes":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <path d="M4 14h5l3-3 3 3h5v4H4z" stroke={stroke} strokeLinejoin="round" strokeWidth="1.8" />
        </svg>
      );
    case "home":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <path d="m4.5 10.5 7.5-6 7.5 6V19H4.5z" stroke={stroke} strokeLinejoin="round" strokeWidth="1.8" />
          <path d="M10 19v-4h4v4" stroke={stroke} strokeLinecap="round" strokeWidth="1.8" />
        </svg>
      );
    case "beauty":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <rect height="10" rx="2" stroke={stroke} strokeWidth="1.8" width="7" x="8.5" y="9" />
          <path d="M10 9V6h4v3" stroke={stroke} strokeLinecap="round" strokeWidth="1.8" />
        </svg>
      );
    case "more":
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <path d="M6 10l6 6 6-6" stroke={stroke} strokeLinecap="round" strokeLinejoin="round" strokeWidth="1.8" />
        </svg>
      );
    default:
      return (
        <svg fill="none" height="16" viewBox="0 0 24 24" width="16">
          <rect height="5" rx="1.2" stroke={stroke} strokeWidth="1.8" width="5" x="4.5" y="4.5" />
          <rect height="5" rx="1.2" stroke={stroke} strokeWidth="1.8" width="5" x="14.5" y="4.5" />
          <rect height="5" rx="1.2" stroke={stroke} strokeWidth="1.8" width="5" x="4.5" y="14.5" />
          <rect height="5" rx="1.2" stroke={stroke} strokeWidth="1.8" width="5" x="14.5" y="14.5" />
        </svg>
      );
  }
};

export const CategoryIconRow = ({ items }: CategoryIconRowProps) => {
  return (
    <section className="h-[74px] rounded-[14px] border border-[#f1f1f1] bg-white px-[14px] py-[11px] shadow-[0_8px_20px_rgba(0,0,0,0.06)]">
      <div className="flex h-full items-start gap-2 overflow-x-auto [scrollbar-width:none] [-ms-overflow-style:none] [&::-webkit-scrollbar]:hidden xl:justify-between xl:overflow-visible">
        {items.map((item) => (
          <Link
            className="group flex w-[72px] shrink-0 flex-col items-center"
            href={item.href}
            key={item.key}
          >
            <span
              className={
                item.active
                  ? "inline-flex h-10 w-10 items-center justify-center rounded-full bg-[#ff6a00] shadow-[0_8px_16px_-10px_rgba(255,106,0,0.95)]"
                  : "inline-flex h-10 w-10 items-center justify-center rounded-full border border-[#e8e8e8] bg-[#fafafa] transition-all group-hover:border-[#ffc39a] group-hover:bg-[#fff8f4]"
              }
            >
              <CategoryIcon active={item.active} type={item.icon} />
            </span>
            <span className={item.active ? "mt-2 text-[10px] font-bold text-[#ff6a00]" : "mt-2 text-[10px] font-medium text-[#777] transition-colors group-hover:text-[#ff6a00]"}>
              {item.label}
            </span>
          </Link>
        ))}
      </div>
    </section>
  );
};

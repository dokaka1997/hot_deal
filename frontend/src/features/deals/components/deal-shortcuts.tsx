import Link from "next/link";

interface ShortcutItem {
  label: string;
  count: number;
  href: string;
}

interface DealShortcutsProps {
  title: string;
  items: ShortcutItem[];
}

export const DealShortcuts = ({ title, items }: DealShortcutsProps) => {
  if (!items.length) {
    return null;
  }

  return (
    <section className="section-surface space-y-3">
      <div className="section-head">
        <h2 className="text-xl font-bold text-[#222]">{title}</h2>
      </div>
      <div className="flex flex-wrap gap-2.5">
        {items.map((item) => (
          <Link
            className="inline-flex items-center gap-2 rounded-[12px] border border-[#ffd8bf] bg-white px-3 py-2 text-sm font-semibold text-[#ff6a00] shadow-[0_8px_18px_-16px_rgba(255,106,0,0.85)] hover:bg-[#fff5ef]"
            href={item.href}
            key={`${title}-${item.label}`}
          >
            <span>{item.label}</span>
            <span className="rounded-[8px] bg-[#fff1e7] px-2 py-0.5 text-xs font-bold text-[#ff6a00]">{item.count}</span>
          </Link>
        ))}
      </div>
    </section>
  );
};

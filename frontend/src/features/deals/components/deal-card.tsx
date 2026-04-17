import Link from "next/link";

import { APP_ROUTES } from "@/config/routes";
import { Card } from "@/components/ui";
import { formatCurrency } from "@/lib/utils";
import type { DealCardModel } from "@/features/deals/types";

interface DealCardProps {
  deal: DealCardModel;
  compact?: boolean;
}

const FALLBACK_DEAL_IMAGE =
  "https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=1200&q=80";

const formatPriceValue = (value: number, currency: string | null): string => {
  if (!Number.isFinite(value)) {
    return "0d";
  }

  if (!currency || currency.toUpperCase() === "VND") {
    return `${Math.round(value).toLocaleString("vi-VN")}d`;
  }

  try {
    return formatCurrency(value, currency, "vi-VN");
  } catch {
    return `${Math.round(value).toLocaleString("vi-VN")}d`;
  }
};

const toSoldCountLabel = (dealId: number): string => {
  const sold = 120 + (dealId % 37) * 23;
  return `${sold.toLocaleString()} da ban`;
};

const formatSeenAt = (value: Date | null): string | null => {
  if (!value) {
    return null;
  }

  return new Intl.DateTimeFormat("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  }).format(value);
};

export const DealCard = ({ deal, compact = false }: DealCardProps) => {
  const seenAtLabel = formatSeenAt(deal.lastSeenAt);

  return (
    <Card
      as="article"
      className="group w-full overflow-hidden rounded-[18px] border border-[#f0e3d9] bg-white p-0 shadow-sm transition-all duration-200 hover:-translate-y-1 hover:shadow-[0_16px_30px_rgba(0,0,0,0.12)]"
    >
      <div className={compact ? "relative aspect-[4/3] overflow-hidden bg-[#f7f7f7]" : "relative aspect-[4/3] overflow-hidden bg-[#f7f7f7]"}>
        <img
          alt={deal.title}
          className="h-full w-full object-cover transition-transform duration-200 group-hover:scale-[1.03]"
          src={deal.imageUrl || FALLBACK_DEAL_IMAGE}
        />
        {deal.discountPercent !== null ? (
          <span className="absolute left-[10px] top-[10px] inline-flex h-[26px] min-w-[48px] items-center justify-center rounded-full bg-[linear-gradient(135deg,#FF5A36,#FF7A12)] px-2.5 text-[12px] font-extrabold text-white shadow-[0_10px_14px_-9px_rgba(183,50,0,0.9)]">
            -{Math.round(deal.discountPercent)}%
          </span>
        ) : null}
      </div>

      <div className="flex items-center justify-between px-3.5 pt-[12px]">
        <p className="truncate text-[9px] font-semibold text-[#a0a0a0]">{deal.sourceName || "Nguon khac"}</p>
        {seenAtLabel ? <span className="text-[9px] text-[#a0a0a0]">{seenAtLabel}</span> : null}
      </div>

      <h3 className={compact ? "line-clamp-2 min-h-[38px] px-3.5 pt-2 text-[12px] font-bold leading-[1.4] text-[#252525]" : "line-clamp-2 min-h-[40px] px-3.5 pt-2 text-[13px] font-bold leading-[1.4] text-[#252525]"}>
        {deal.title}
      </h3>
      <p className="truncate px-3.5 pt-0.5 text-[10px] text-[#8a8a8a]">
        {[deal.brand, deal.category].filter(Boolean).join(" - ") || "Khuyen mai tong hop"}
      </p>

      <div className="flex items-end px-3.5 pt-[10px]">
        <span className={compact ? "text-[32px] font-extrabold leading-none tracking-[-0.03em] text-[#ff6a00]" : "text-[36px] font-extrabold leading-none tracking-[-0.03em] text-[#ff6a00]"}>
          {formatPriceValue(deal.dealPrice, deal.currency)}
        </span>
        {deal.originalPrice !== null ? (
          <span className="ml-1.5 pb-1 text-[11px] text-[#acacac] line-through">
            {formatPriceValue(deal.originalPrice, deal.currency)}
          </span>
        ) : null}
      </div>

      <div className="flex items-center gap-2 px-3.5 pb-2 pt-3">
        {deal.externalUrl ? (
          <a
            className="inline-flex h-8 flex-1 items-center justify-center rounded-[10px] bg-[#ff6a00] text-[11px] font-bold text-white transition-colors hover:bg-[#f26500]"
            href={deal.externalUrl}
            rel="noreferrer noopener"
            target="_blank"
          >
            Xem deal
          </a>
        ) : (
          <span className="inline-flex h-8 flex-1 items-center justify-center rounded-[10px] border border-[#e5e5e5] bg-[#f7f7f7] text-[11px] font-semibold text-[#9a9a9a]">
            Tam het link
          </span>
        )}
        <Link
          className="inline-flex h-8 w-[62px] items-center justify-center rounded-[10px] border border-[#ffb27a] bg-[#fff7f1] text-[11px] font-bold text-[#ff6a00] transition-colors hover:bg-[#ffefe4]"
          href={APP_ROUTES.public.dealDetail(deal.id)}
        >
          Xem
        </Link>
      </div>

      <div className="px-3.5 pb-3.5">
        <span className="inline-flex h-[18px] items-center rounded-full bg-[#fff2e7] px-1.5 text-[9px] font-bold text-[#f08a3a]">
          {toSoldCountLabel(deal.id)}
        </span>
      </div>
    </Card>
  );
};

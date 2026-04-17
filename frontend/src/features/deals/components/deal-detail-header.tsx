import Link from "next/link";

import { PriceDisplay, SourceBadge } from "@/components/commerce";
import { Badge, Card } from "@/components/ui";
import { APP_ROUTES } from "@/config/routes";
import { formatDateTime } from "@/lib/utils";
import type { DealDetail } from "@/types/domain";

const FALLBACK_DEAL_IMAGE =
  "https://images.unsplash.com/photo-1607082350899-7e105aa886ae?auto=format&fit=crop&w=1200&q=80";

const statusClassMap: Record<DealDetail["status"], string> = {
  ACTIVE: "border-success/35 bg-success/10 text-success",
  EXPIRED: "border-border bg-surface-muted text-muted-foreground",
  INACTIVE: "border-warning/35 bg-warning/10 text-foreground"
};

export const DealDetailHeader = ({ deal }: { deal: DealDetail }) => {
  return (
    <section className="grid grid-cols-1 gap-4 lg:grid-cols-[1.2fr_1fr]">
      <Card as="article" className="overflow-hidden p-0">
        <img
          alt={deal.title}
          className="h-64 w-full object-cover sm:h-72"
          src={deal.imageUrl || FALLBACK_DEAL_IMAGE}
        />
      </Card>

      <Card as="article" className="space-y-4">
        <div className="flex flex-wrap items-center gap-2">
          <Badge className={statusClassMap[deal.status]} variant="default">
            {deal.status}
          </Badge>
          <SourceBadge sourceCode={deal.source.code} sourceName={deal.source.name} />
          {deal.category ? (
            <Badge variant="brand">{deal.category}</Badge>
          ) : null}
        </div>

        <div className="space-y-1">
          <h1 className="text-display-sm text-foreground">{deal.title}</h1>
          <p className="text-body-sm text-muted-foreground">
            {[deal.brand, deal.source.name || deal.source.code].filter(Boolean).join(" - ") || "Nguon khong xac dinh"}
          </p>
        </div>

        <PriceDisplay
          className="gap-3"
          currency={deal.currency}
          dealPrice={deal.dealPrice}
          discountPercent={deal.discountPercent}
          originalPrice={deal.originalPrice}
          priceClassName="text-3xl"
        />

        <div className="space-y-1 text-sm text-muted-foreground">
          {deal.lastSeenAt ? <p>Cap nhat: {formatDateTime(deal.lastSeenAt)}</p> : null}
          {deal.validUntil ? <p>Han deal: {formatDateTime(deal.validUntil)}</p> : null}
          {deal.dealScore !== null ? <p>Diem deal: {deal.dealScore.toFixed(2)}</p> : null}
        </div>

        <div className="flex flex-wrap items-center gap-2 pt-1">
          <Link
            className="inline-flex h-10 items-center rounded-sm border border-brand/25 bg-brand-soft px-3 text-sm font-semibold text-brand-700 hover:bg-brand/15"
            href={APP_ROUTES.public.deals}
          >
            Quay lai listing
          </Link>
          {deal.externalUrl ? (
            <a
              className="inline-flex h-10 items-center rounded-sm border border-brand bg-gradient-to-r from-brand-600 to-brand-700 px-3 text-sm font-semibold text-white shadow-promo hover:from-brand-700 hover:to-brand-700"
              href={deal.externalUrl}
              rel="noreferrer noopener"
              target="_blank"
            >
              Mo uu dai goc
            </a>
          ) : (
            <span className="inline-flex h-10 items-center rounded-sm border border-border bg-surface-muted px-3 text-sm text-muted-foreground">
              Nguon tam het link
            </span>
          )}
        </div>
      </Card>
    </section>
  );
};

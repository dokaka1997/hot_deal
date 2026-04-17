import { Card } from "@/components/ui";
import { formatDateTime } from "@/lib/utils";
import type { DealDetail } from "@/types/domain";

const valueOrFallback = (value: string | null | undefined): string => {
  if (!value || !value.trim()) {
    return "-";
  }

  return value;
};

const dateOrFallback = (value: Date | null): string => {
  if (!value) {
    return "-";
  }

  return formatDateTime(value);
};

interface MetadataItemProps {
  label: string;
  value: string;
}

const MetadataItem = ({ label, value }: MetadataItemProps) => {
  return (
    <div className="space-y-1 rounded-sm border border-border bg-surface-muted p-2.5">
      <p className="text-[11px] font-semibold uppercase tracking-wide text-muted-foreground">{label}</p>
      <p className="text-sm font-medium text-foreground">{value}</p>
    </div>
  );
};

export const DealDetailMetadata = ({ deal }: { deal: DealDetail }) => {
  return (
    <section className="grid grid-cols-1 gap-4 lg:grid-cols-2">
      <Card as="article" className="space-y-4">
        <div className="space-y-1">
          <p className="section-kicker">Thong tin deal</p>
          <h2 className="text-heading-lg text-foreground">Chi tiet metadata</h2>
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <MetadataItem label="Deal ID" value={String(deal.id)} />
          <MetadataItem label="Source Code" value={valueOrFallback(deal.source.code)} />
          <MetadataItem label="Source Name" value={valueOrFallback(deal.source.name)} />
          <MetadataItem label="Coupon Code" value={valueOrFallback(deal.couponCode)} />
          <MetadataItem label="Valid From" value={dateOrFallback(deal.validFrom)} />
          <MetadataItem label="Valid Until" value={dateOrFallback(deal.validUntil)} />
          <MetadataItem label="First Seen" value={dateOrFallback(deal.firstSeenAt)} />
          <MetadataItem label="Last Seen" value={dateOrFallback(deal.lastSeenAt)} />
        </div>
      </Card>

      <Card as="article" className="space-y-4">
        <div className="space-y-1">
          <p className="section-kicker">Thong tin san pham</p>
          <h2 className="text-heading-lg text-foreground">Tom tat lien quan</h2>
        </div>
        <div className="space-y-2 text-sm text-muted-foreground">
          <p>{deal.description?.trim() || "Chua co mo ta chi tiet."}</p>
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <MetadataItem label="Product Name" value={valueOrFallback(deal.product?.name)} />
          <MetadataItem label="Product Brand" value={valueOrFallback(deal.product?.brand)} />
          <MetadataItem label="Product Category" value={valueOrFallback(deal.product?.category)} />
          <MetadataItem label="Normalized Title" value={valueOrFallback(deal.normalizedTitle)} />
        </div>
      </Card>
    </section>
  );
};

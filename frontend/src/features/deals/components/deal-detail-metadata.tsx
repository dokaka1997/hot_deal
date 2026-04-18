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
          <p className="section-kicker">Thông tin ưu đãi</p>
          <h2 className="text-heading-lg text-foreground">Chi tiết metadata</h2>
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <MetadataItem label="Mã ưu đãi" value={String(deal.id)} />
          <MetadataItem label="Mã nguồn" value={valueOrFallback(deal.source.code)} />
          <MetadataItem label="Tên nguồn" value={valueOrFallback(deal.source.name)} />
          <MetadataItem label="Mã giảm giá" value={valueOrFallback(deal.couponCode)} />
          <MetadataItem label="Bắt đầu hiệu lực" value={dateOrFallback(deal.validFrom)} />
          <MetadataItem label="Kết thúc hiệu lực" value={dateOrFallback(deal.validUntil)} />
          <MetadataItem label="Lần đầu ghi nhận" value={dateOrFallback(deal.firstSeenAt)} />
          <MetadataItem label="Lần cập nhật gần nhất" value={dateOrFallback(deal.lastSeenAt)} />
        </div>
      </Card>

      <Card as="article" className="space-y-4">
        <div className="space-y-1">
          <p className="section-kicker">Thông tin sản phẩm</p>
          <h2 className="text-heading-lg text-foreground">Tóm tắt liên quan</h2>
        </div>
        <div className="space-y-2 text-sm text-muted-foreground">
          <p>{deal.description?.trim() || "Chưa có mô tả chi tiết."}</p>
        </div>
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <MetadataItem label="Tên sản phẩm" value={valueOrFallback(deal.product?.name)} />
          <MetadataItem label="Thương hiệu" value={valueOrFallback(deal.product?.brand)} />
          <MetadataItem label="Danh mục" value={valueOrFallback(deal.product?.category)} />
          <MetadataItem label="Tiêu đề chuẩn hóa" value={valueOrFallback(deal.normalizedTitle)} />
        </div>
      </Card>
    </section>
  );
};

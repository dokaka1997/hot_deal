import { EmptyState, ErrorState, LoadingState } from "@/components/states";
import { Card } from "@/components/ui";
import { formatCurrency, formatDateTime } from "@/lib/utils";
import type { PriceHistoryPoint } from "@/types/domain";
import { DealPriceHistoryChart } from "@/features/deals/components/deal-price-history-chart";

interface DealPriceHistorySectionProps {
  points: PriceHistoryPoint[];
  currency: string;
  isPending: boolean;
  isError: boolean;
  error: unknown;
  onRetry: () => void;
  isUpdating?: boolean;
}

const formatPriceValue = (value: number, currency: string): string => {
  try {
    return formatCurrency(value, currency);
  } catch {
    return value.toFixed(2);
  }
};

const getPriceStats = (points: PriceHistoryPoint[]) => {
  const latestPoint = points.reduce((latest, candidate) => {
    return candidate.capturedAt > latest.capturedAt ? candidate : latest;
  }, points[0]);

  const lowestDealPrice = points.reduce((min, candidate) => Math.min(min, candidate.dealPrice), points[0].dealPrice);
  const highestDealPrice = points.reduce((max, candidate) => Math.max(max, candidate.dealPrice), points[0].dealPrice);

  return {
    latestPoint,
    lowestDealPrice,
    highestDealPrice
  };
};

export const DealPriceHistorySection = ({
  points,
  currency,
  isPending,
  isError,
  error,
  onRetry,
  isUpdating = false
}: DealPriceHistorySectionProps) => {
  return (
    <section className="section-surface space-y-4">
      <div className="section-head">
        <div>
          <p className="section-kicker">Theo dõi giá</p>
          <h2 className="text-heading-lg text-foreground">Lịch sử giá</h2>
          <p className="text-body-sm text-muted-foreground">Diễn biến giá gần đây của ưu đãi này.</p>
        </div>
        {isUpdating ? <p className="text-sm text-muted-foreground">Đang làm mới dữ liệu...</p> : null}
      </div>

      {isPending ? <LoadingState description="Đang tải lịch sử giá..." /> : null}

      {isError ? <ErrorState error={error} onRetry={onRetry} /> : null}

      {!isPending && !isError && !points.length ? (
        <EmptyState
          description="Ưu đãi này chưa có dữ liệu lịch sử giá."
          title="Chưa có lịch sử giá"
        />
      ) : null}

      {!isPending && !isError && points.length ? (
        <>
          <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
            {(() => {
              const stats = getPriceStats(points);
              return (
                <>
                  <Card as="article" className="p-4">
                    <p className="text-xs font-semibold uppercase text-muted-foreground">Giá mới nhất</p>
                    <p className="mt-1 text-lg font-semibold text-foreground">
                      {formatPriceValue(stats.latestPoint.dealPrice, currency)}
                    </p>
                    <p className="mt-1 text-xs text-muted-foreground">
                      {formatDateTime(stats.latestPoint.capturedAt)}
                    </p>
                  </Card>
                  <Card as="article" className="p-4">
                    <p className="text-xs font-semibold uppercase text-muted-foreground">Giá thấp nhất</p>
                    <p className="mt-1 text-lg font-semibold text-brand-700">
                      {formatPriceValue(stats.lowestDealPrice, currency)}
                    </p>
                  </Card>
                  <Card as="article" className="p-4">
                    <p className="text-xs font-semibold uppercase text-muted-foreground">Giá cao nhất</p>
                    <p className="mt-1 text-lg font-semibold text-foreground">
                      {formatPriceValue(stats.highestDealPrice, currency)}
                    </p>
                  </Card>
                </>
              );
            })()}
          </div>

          <Card as="article" className="p-4">
            <DealPriceHistoryChart currency={currency} points={points} />
          </Card>
        </>
      ) : null}
    </section>
  );
};

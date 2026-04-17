"use client";

import Link from "next/link";
import { useMemo } from "react";

import { EmptyState, ErrorState, LoadingState } from "@/components/states";
import { APP_ROUTES } from "@/config/routes";
import {
  DealDetailHeader,
  DealDetailMetadata,
  DealPriceHistorySection
} from "@/features/deals/components";
import { usePublicDealDetail, usePublicDealPriceHistory } from "@/features/deals/hooks";
import type { DealPriceHistoryRequest } from "@/types/requests";

interface DealDetailContainerProps {
  dealId: number;
}

export const DealDetailContainer = ({ dealId }: DealDetailContainerProps) => {
  const detailQuery = usePublicDealDetail(dealId);

  const historyRequest = useMemo<DealPriceHistoryRequest>(() => {
    return {
      dealId,
      page: 0,
      size: 100,
      sortBy: "capturedAt",
      direction: "DESC"
    };
  }, [dealId]);

  const priceHistoryQuery = usePublicDealPriceHistory(historyRequest, {
    enabled: detailQuery.isSuccess
  });

  if (detailQuery.isPending) {
    return <LoadingState description="Dang tai chi tiet deal..." />;
  }

  if (detailQuery.isError) {
    return <ErrorState error={detailQuery.error} onRetry={() => detailQuery.refetch()} />;
  }

  if (!detailQuery.data) {
    return (
      <EmptyState
        action={
          <Link
            className="inline-flex h-10 items-center rounded-sm border border-brand/25 bg-brand-soft px-3 text-sm font-semibold text-brand-700 hover:bg-brand/15"
            href={APP_ROUTES.public.deals}
          >
            Quay lai danh sach
          </Link>
        }
        description="Khong tim thay deal ban vua yeu cau."
        title="Deal khong ton tai"
      />
    );
  }

  return (
    <div className="page-stack">
      <DealDetailHeader deal={detailQuery.data} />
      <DealDetailMetadata deal={detailQuery.data} />
      <DealPriceHistorySection
        currency={detailQuery.data.currency}
        error={priceHistoryQuery.error}
        isError={priceHistoryQuery.isError}
        isPending={priceHistoryQuery.isPending}
        isUpdating={priceHistoryQuery.isFetching && !priceHistoryQuery.isPending}
        onRetry={() => priceHistoryQuery.refetch()}
        points={priceHistoryQuery.data?.points ?? []}
      />
    </div>
  );
};

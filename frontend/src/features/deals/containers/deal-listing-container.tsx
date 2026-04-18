"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";

import { EmptyState, ErrorState, LoadingState } from "@/components/states";
import { Button } from "@/components/ui";
import { usePublicAnalyticsSummary } from "@/features/analytics/hooks";
import {
  DealAppliedFilters,
  DealGrid,
  DealListFilters,
  type DealFilterOption,
  DealsPagination
} from "@/features/deals/components";
import { usePublicDeals } from "@/features/deals/hooks";
import { toDealCardFromListItem } from "@/features/deals/mappers";
import type { DealFilterState } from "@/features/deals/types";
import {
  buildDealsListingHref,
  createDefaultDealFilterState,
  DEFAULT_DEALS_PAGE,
  getAppliedDealFilterChips,
  isSameDealFilterState,
  isSameDealSearchRequest,
  parseDealSearchParams,
  toDealFilterState,
  toDealSearchRequest,
  type AppliedDealFilterKey
} from "@/features/deals/utils";

const CATEGORY_LABEL_MAP: Record<string, string> = {
  phone: "Điện thoại",
  laptop: "Laptop",
  audio: "Tai nghe",
  watch: "Đồng hồ",
  fashion: "Thời trang",
  home: "Gia dụng",
  beauty: "Làm đẹp",
  voucher: "Voucher"
};

const toTitleLabel = (value: string): string => {
  return value
    .replace(/[_-]+/g, " ")
    .trim()
    .replace(/\s+/g, " ")
    .split(" ")
    .filter(Boolean)
    .map((word) => `${word.charAt(0).toUpperCase()}${word.slice(1)}`)
    .join(" ");
};

const toCategoryLabel = (value: string): string => {
  const normalized = value.trim().toLowerCase();
  return CATEGORY_LABEL_MAP[normalized] ?? toTitleLabel(normalized);
};

const toSourceLabel = (value: string): string => {
  const normalized = value.trim();
  return normalized.toUpperCase();
};

const uniqOptions = (options: DealFilterOption[]): DealFilterOption[] => {
  const map = new Map<string, DealFilterOption>();

  options.forEach((option) => {
    const normalizedKey = option.value.trim().toLowerCase();
    if (!normalizedKey || map.has(normalizedKey)) {
      return;
    }
    map.set(normalizedKey, option);
  });

  return Array.from(map.values());
};

const withSelectedOption = (
  options: DealFilterOption[],
  selectedValue: string,
  toLabel: (value: string) => string
): DealFilterOption[] => {
  const normalized = selectedValue.trim();
  if (!normalized) {
    return options;
  }

  const exists = options.some((option) => option.value.trim().toLowerCase() === normalized.toLowerCase());
  if (exists) {
    return options;
  }

  return [{ value: normalized, label: toLabel(normalized) }, ...options];
};

const removeFilterByKey = (
  key: AppliedDealFilterKey,
  updateFilters: (nextValues: Partial<DealFilterState>) => void
) => {
  switch (key) {
    case "keyword":
      updateFilters({ keyword: "" });
      break;
    case "category":
      updateFilters({ category: "" });
      break;
    case "source":
      updateFilters({ source: "" });
      break;
    case "minPrice":
      updateFilters({ minPrice: "" });
      break;
    case "maxPrice":
      updateFilters({ maxPrice: "" });
      break;
    case "activeOnly":
      updateFilters({ activeOnly: true });
      break;
    case "hasCoupon":
      updateFilters({ hasCoupon: "all" });
      break;
  }
};

export const DealListingContainer = () => {
  const router = useRouter();
  const searchParams = useSearchParams();
  const searchParamKey = searchParams.toString();

  const requestFromUrl = useMemo(() => {
    return parseDealSearchParams(new URLSearchParams(searchParamKey));
  }, [searchParamKey]);

  const filtersFromUrl = useMemo<DealFilterState>(() => {
    return toDealFilterState(requestFromUrl);
  }, [requestFromUrl]);

  const [filters, setFilters] = useState<DealFilterState>(filtersFromUrl);

  useEffect(() => {
    setFilters((currentFilters) => {
      if (isSameDealFilterState(currentFilters, filtersFromUrl)) {
        return currentFilters;
      }

      return filtersFromUrl;
    });
  }, [filtersFromUrl]);

  const currentRequest = useMemo(() => {
    return toDealSearchRequest(filters);
  }, [filters]);

  useEffect(() => {
    if (isSameDealSearchRequest(requestFromUrl, currentRequest)) {
      return;
    }

    router.replace(buildDealsListingHref(currentRequest), { scroll: false });
  }, [currentRequest, requestFromUrl, router]);

  const dealsQuery = usePublicDeals(currentRequest);
  const analyticsQuery = usePublicAnalyticsSummary({
    activeOnly: false,
    sourceLimit: 20,
    categoryLimit: 20
  });

  const cards = dealsQuery.data?.items.map(toDealCardFromListItem) ?? [];
  const appliedFilterChips = getAppliedDealFilterChips(currentRequest);
  const hasAppliedFilters = appliedFilterChips.length > 0;
  const isUpdating = dealsQuery.isFetching && !dealsQuery.isPending;

  const categoryOptions = useMemo<DealFilterOption[]>(() => {
    const fromSummary = (analyticsQuery.data?.dealCountByCategory ?? [])
      .filter((item) => Boolean(item.category))
      .map((item) => ({
        value: String(item.category).trim().toLowerCase(),
        label: toCategoryLabel(String(item.category)),
        count: item.dealCount
      }))
      .sort((left, right) => right.count - left.count);

    const uniqueOptions = uniqOptions(fromSummary);
    return withSelectedOption(uniqueOptions, filters.category, toCategoryLabel);
  }, [analyticsQuery.data?.dealCountByCategory, filters.category]);

  const sourceOptions = useMemo<DealFilterOption[]>(() => {
    const fromSummary = (analyticsQuery.data?.dealCountBySource ?? [])
      .filter((item) => Boolean(item.sourceCode))
      .map((item) => {
        const sourceCode = String(item.sourceCode).trim();
        return {
          value: sourceCode.toLowerCase(),
          label: item.sourceName || toSourceLabel(sourceCode),
          count: item.dealCount
        };
      })
      .sort((left, right) => right.count - left.count);

    const uniqueOptions = uniqOptions(fromSummary);
    return withSelectedOption(uniqueOptions, filters.source, toSourceLabel);
  }, [analyticsQuery.data?.dealCountBySource, filters.source]);

  const resetFilters = () => {
    setFilters(createDefaultDealFilterState());
  };

  const updateFilters = (nextValues: Partial<DealFilterState>) => {
    setFilters((currentFilters) => ({
      ...currentFilters,
      ...nextValues,
      page: DEFAULT_DEALS_PAGE
    }));
  };

  return (
    <div className="page-stack">
      <DealListFilters
        categoryOptions={categoryOptions}
        isUpdating={isUpdating}
        onChange={updateFilters}
        onReset={resetFilters}
        sourceOptions={sourceOptions}
        values={filters}
      />

      <DealAppliedFilters
        chips={appliedFilterChips}
        onClearAll={resetFilters}
        onRemove={(key) => removeFilterByKey(key, updateFilters)}
      />

      {dealsQuery.isPending ? (
        <LoadingState description="Đang tải dữ liệu ưu đãi..." title="Đang tải" />
      ) : null}

      {dealsQuery.isError ? (
        <ErrorState
          error={dealsQuery.error}
          onRetry={() => dealsQuery.refetch()}
          retryLabel="Thử lại"
          title="Không thể tải dữ liệu"
        />
      ) : null}

      {!dealsQuery.isPending && !dealsQuery.isError && !cards.length ? (
        <EmptyState
          action={
            hasAppliedFilters ? (
              <Button onClick={resetFilters} type="button" variant="secondary">
                Xóa bộ lọc
              </Button>
            ) : null
          }
          description={
            hasAppliedFilters
              ? `Không tìm thấy ưu đãi phù hợp với ${appliedFilterChips.map((chip) => chip.label).join(", ")}.`
              : "Tạm thời chưa có ưu đãi. Vui lòng quay lại sau."
          }
          title={hasAppliedFilters ? "Không có ưu đãi phù hợp bộ lọc" : "Chưa có ưu đãi"}
        />
      ) : null}

      {!dealsQuery.isPending && !dealsQuery.isError && cards.length ? <DealGrid deals={cards} /> : null}

      {!dealsQuery.isPending && !dealsQuery.isError && dealsQuery.data ? (
        <DealsPagination
          isFirst={dealsQuery.data.first}
          isLast={dealsQuery.data.last}
          page={dealsQuery.data.page}
          totalPages={dealsQuery.data.totalPages}
          onPageChange={(nextPage) => {
            if (nextPage < 0) {
              return;
            }

            setFilters((currentFilters) => ({
              ...currentFilters,
              page: nextPage
            }));
          }}
        />
      ) : null}
    </div>
  );
};

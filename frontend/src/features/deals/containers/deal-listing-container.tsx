"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";

import { EmptyState, ErrorState, LoadingState } from "@/components/states";
import { Button } from "@/components/ui";
import { DealGrid, DealListFilters, DealsPagination } from "@/features/deals/components";
import { toDealCardFromListItem } from "@/features/deals/mappers";
import type { DealFilterState } from "@/features/deals/types";
import { usePublicDeals } from "@/features/deals/hooks";
import { useDebouncedValue } from "@/hooks";
import {
  buildDealsListingHref,
  createDefaultDealFilterState,
  DEFAULT_DEALS_PAGE,
  getAppliedDealFilterLabels,
  isSameDealFilterState,
  isSameDealSearchRequest,
  parseDealSearchParams,
  toDealFilterState,
  toDealSearchRequest
} from "@/features/deals/utils";

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

  const debouncedKeyword = useDebouncedValue(filters.keyword, 350);

  const currentRequest = useMemo(() => {
    return toDealSearchRequest({
      ...filters,
      keyword: filters.keyword.trim().length ? debouncedKeyword : filters.keyword
    });
  }, [debouncedKeyword, filters]);

  useEffect(() => {
    if (isSameDealSearchRequest(requestFromUrl, currentRequest)) {
      return;
    }

    router.replace(buildDealsListingHref(currentRequest), { scroll: false });
  }, [currentRequest, requestFromUrl, router]);

  const dealsQuery = usePublicDeals(currentRequest);
  const cards = dealsQuery.data?.items.map(toDealCardFromListItem) ?? [];
  const appliedFilters = getAppliedDealFilterLabels(currentRequest);
  const hasAppliedFilters = appliedFilters.length > 0;
  const isUpdating = dealsQuery.isFetching && !dealsQuery.isPending;

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
        isUpdating={isUpdating}
        onChange={updateFilters}
        onReset={() => {
          setFilters(createDefaultDealFilterState());
        }}
        values={filters}
      />

      {dealsQuery.isPending ? <LoadingState description="Dang tai du lieu deal..." /> : null}

      {dealsQuery.isError ? (
        <ErrorState error={dealsQuery.error} onRetry={() => dealsQuery.refetch()} />
      ) : null}

      {!dealsQuery.isPending && !dealsQuery.isError && !cards.length ? (
        <EmptyState
          action={
            hasAppliedFilters ? (
                <Button onClick={() => setFilters(createDefaultDealFilterState())} type="button" variant="secondary">
                Xoa bo loc
                </Button>
            ) : null
          }
          description={
            hasAppliedFilters
              ? `Khong tim thay deal phu hop voi ${appliedFilters.join(", ")}. Thu mo rong bo loc.`
              : "Tam thoi chua co deal. Vui long quay lai sau."
          }
          title={hasAppliedFilters ? "Khong co deal phu hop bo loc" : "Chua co deal"}
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

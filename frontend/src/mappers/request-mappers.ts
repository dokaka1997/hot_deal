import type { QueryParams } from "@/lib/utils/query-string";
import type {
  AdminCollectorJobHistoryRequest,
  AdminFailedRawDealRequest,
  AnalyticsSummaryRequest,
  DealPriceHistoryRequest,
  DealSearchRequest,
  PaginationRequest
} from "@/types/requests";

const normalizeOptionalText = (value?: string): string | undefined => {
  if (!value) {
    return undefined;
  }

  const trimmed = value.trim();
  return trimmed.length ? trimmed : undefined;
};

const normalizeOptionalNonNegativeNumber = (value?: number): number | undefined => {
  if (value === undefined || !Number.isFinite(value) || value < 0) {
    return undefined;
  }

  return value;
};

const normalizePage = (value?: number): number | undefined => {
  if (value === undefined || !Number.isFinite(value)) {
    return undefined;
  }

  return Math.max(Math.trunc(value), 0);
};

const normalizePageSize = (value?: number): number | undefined => {
  if (value === undefined || !Number.isFinite(value)) {
    return undefined;
  }

  const truncated = Math.trunc(value);
  return Math.min(Math.max(truncated, 1), 100);
};

export const toPaginationQuery = (request: PaginationRequest): QueryParams => {
  return {
    page: request.page,
    size: request.size,
    sortBy: request.sortBy,
    direction: request.direction
  };
};

export const toDealSearchQuery = (request: DealSearchRequest): QueryParams => {
  const minPrice = normalizeOptionalNonNegativeNumber(request.minPrice);
  const maxPrice = normalizeOptionalNonNegativeNumber(request.maxPrice);
  const hasValidPriceRange =
    minPrice === undefined || maxPrice === undefined || maxPrice >= minPrice;

  return {
    keyword: normalizeOptionalText(request.keyword),
    category: normalizeOptionalText(request.category),
    source: normalizeOptionalText(request.source),
    minPrice,
    maxPrice: hasValidPriceRange ? maxPrice : undefined,
    activeOnly: request.activeOnly,
    page: normalizePage(request.page),
    size: normalizePageSize(request.size),
    sortBy: request.sortBy,
    direction: request.direction
  };
};

export const toAnalyticsSummaryQuery = (request: AnalyticsSummaryRequest): QueryParams => {
  return {
    activeOnly: request.activeOnly,
    hottestLimit: request.hottestLimit,
    sourceLimit: request.sourceLimit,
    categoryLimit: request.categoryLimit
  };
};

export const toAdminCollectorJobHistoryQuery = (
  request: AdminCollectorJobHistoryRequest
): QueryParams => {
  return {
    sourceCode: request.sourceCode,
    status: request.status,
    page: request.page,
    size: request.size,
    sortBy: request.sortBy,
    direction: request.direction
  };
};

export const toAdminFailedRawDealQuery = (request: AdminFailedRawDealRequest): QueryParams => {
  return {
    sourceCode: request.sourceCode,
    status: request.status,
    page: request.page,
    size: request.size,
    sortBy: request.sortBy,
    direction: request.direction
  };
};

export const toDealPriceHistoryQuery = (request: DealPriceHistoryRequest): QueryParams => {
  return {
    page: request.page,
    size: request.size,
    sortBy: request.sortBy,
    direction: request.direction
  };
};

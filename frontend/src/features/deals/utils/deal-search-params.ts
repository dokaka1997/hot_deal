import { z } from "zod";

import { APP_ROUTES } from "@/config/routes";
import type { DealFilterState } from "@/features/deals/types/deal-filter-state";
import { buildQueryString, type QueryParams } from "@/lib/utils";
import { SORT_DIRECTIONS } from "@/types/contracts/backend";
import { DEAL_SORT_FIELDS, type DealSearchRequest } from "@/types/requests";

export const DEFAULT_DEALS_PAGE = 0;
export const DEFAULT_DEALS_PAGE_SIZE = 12;
export const DEFAULT_DEALS_SORT = DEAL_SORT_FIELDS[0];
export const DEFAULT_DEALS_DIRECTION = SORT_DIRECTIONS[1];
export const DEFAULT_DEALS_ACTIVE_ONLY = true;

const optionalQueryString = (maxLength: number) =>
  z.preprocess((value) => {
    if (typeof value !== "string") {
      return undefined;
    }

    const trimmed = value.trim();
    return trimmed.length ? trimmed : undefined;
  }, z.string().max(maxLength).optional());

const optionalNonNegativeNumber = z.preprocess((value) => {
  if (value === undefined || value === null || value === "") {
    return undefined;
  }

  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : undefined;
}, z.number().nonnegative().optional());

const pageParamSchema = z.coerce.number().int().min(0);
const sizeParamSchema = z.coerce.number().int().min(1).max(100);
const sortParamSchema = z.enum(DEAL_SORT_FIELDS);
const directionParamSchema = z.enum(SORT_DIRECTIONS);
const activeOnlyParamSchema = z.enum(["true", "false"]);

const keywordParamSchema = optionalQueryString(120);
const categoryParamSchema = optionalQueryString(120);
const sourceParamSchema = optionalQueryString(64);

type SearchParamsLike = {
  entries(): IterableIterator<[string, string]>;
};

const toParamObject = (params: SearchParamsLike): Record<string, string> => {
  return Object.fromEntries(params.entries());
};

const parseWithFallback = <T>(schema: z.ZodType<T>, input: unknown, fallback: T): T => {
  const parsed = schema.safeParse(input);
  return parsed.success ? parsed.data : fallback;
};

const normalizeOptionalText = (value?: string, maxLength = 120): string | undefined => {
  if (!value) {
    return undefined;
  }

  const trimmed = value.trim();
  if (!trimmed) {
    return undefined;
  }

  return trimmed.slice(0, maxLength);
};

const clampPage = (value?: number): number => {
  if (value === undefined || !Number.isFinite(value)) {
    return DEFAULT_DEALS_PAGE;
  }

  return Math.max(Math.trunc(value), 0);
};

const clampPageSize = (value?: number): number => {
  if (value === undefined || !Number.isFinite(value)) {
    return DEFAULT_DEALS_PAGE_SIZE;
  }

  const truncated = Math.trunc(value);
  return Math.min(Math.max(truncated, 1), 100);
};

const normalizePriceRange = (minPrice?: number, maxPrice?: number): { minPrice?: number; maxPrice?: number } => {
  if (
    minPrice !== undefined &&
    maxPrice !== undefined &&
    maxPrice < minPrice
  ) {
    return {
      minPrice,
      maxPrice: undefined
    };
  }

  return {
    minPrice,
    maxPrice
  };
};

const toPriceInputValue = (value?: number): string => {
  if (value === undefined) {
    return "";
  }

  return String(value);
};

export const createDefaultDealFilterState = (): DealFilterState => {
  return {
    keyword: "",
    category: "",
    source: "",
    minPrice: "",
    maxPrice: "",
    activeOnly: DEFAULT_DEALS_ACTIVE_ONLY,
    sortBy: DEFAULT_DEALS_SORT,
    direction: DEFAULT_DEALS_DIRECTION,
    size: DEFAULT_DEALS_PAGE_SIZE,
    page: DEFAULT_DEALS_PAGE
  };
};

export const parseDealSearchParams = (params: SearchParamsLike): DealSearchRequest => {
  const paramObject = toParamObject(params);
  const parsedActiveOnly = activeOnlyParamSchema.safeParse(paramObject.activeOnly);

  const minPrice = parseWithFallback(optionalNonNegativeNumber, paramObject.minPrice, undefined);
  const maxPrice = parseWithFallback(optionalNonNegativeNumber, paramObject.maxPrice, undefined);
  const normalizedPriceRange = normalizePriceRange(minPrice, maxPrice);

  return {
    keyword: parseWithFallback(keywordParamSchema, paramObject.keyword, undefined),
    category: parseWithFallback(categoryParamSchema, paramObject.category, undefined),
    source: parseWithFallback(sourceParamSchema, paramObject.source, undefined),
    minPrice: normalizedPriceRange.minPrice,
    maxPrice: normalizedPriceRange.maxPrice,
    activeOnly: parsedActiveOnly.success
      ? parsedActiveOnly.data === "true"
      : DEFAULT_DEALS_ACTIVE_ONLY,
    page: parseWithFallback(pageParamSchema, paramObject.page, DEFAULT_DEALS_PAGE),
    size: parseWithFallback(sizeParamSchema, paramObject.size, DEFAULT_DEALS_PAGE_SIZE),
    sortBy: parseWithFallback(sortParamSchema, paramObject.sortBy, DEFAULT_DEALS_SORT),
    direction: parseWithFallback(directionParamSchema, paramObject.direction, DEFAULT_DEALS_DIRECTION)
  };
};

export const toDealFilterState = (request: DealSearchRequest): DealFilterState => {
  const defaults = createDefaultDealFilterState();

  return {
    keyword: normalizeOptionalText(request.keyword, 120) ?? "",
    category: normalizeOptionalText(request.category, 120) ?? "",
    source: normalizeOptionalText(request.source, 64) ?? "",
    minPrice: toPriceInputValue(request.minPrice),
    maxPrice: toPriceInputValue(request.maxPrice),
    activeOnly: request.activeOnly ?? defaults.activeOnly,
    sortBy: request.sortBy ?? defaults.sortBy,
    direction: request.direction ?? defaults.direction,
    size: clampPageSize(request.size),
    page: clampPage(request.page)
  };
};

export const toDealSearchRequest = (filters: DealFilterState): DealSearchRequest => {
  const defaults = createDefaultDealFilterState();
  const parsedMinPrice = parseWithFallback(optionalNonNegativeNumber, filters.minPrice, undefined);
  const parsedMaxPrice = parseWithFallback(optionalNonNegativeNumber, filters.maxPrice, undefined);
  const normalizedPriceRange = normalizePriceRange(parsedMinPrice, parsedMaxPrice);

  return {
    keyword: normalizeOptionalText(filters.keyword, 120),
    category: normalizeOptionalText(filters.category, 120),
    source: normalizeOptionalText(filters.source, 64),
    minPrice: normalizedPriceRange.minPrice,
    maxPrice: normalizedPriceRange.maxPrice,
    activeOnly: filters.activeOnly ?? defaults.activeOnly,
    sortBy: filters.sortBy ?? defaults.sortBy,
    direction: filters.direction ?? defaults.direction,
    size: clampPageSize(filters.size),
    page: clampPage(filters.page)
  };
};

export const isSameDealFilterState = (left: DealFilterState, right: DealFilterState): boolean => {
  return (
    left.keyword === right.keyword &&
    left.category === right.category &&
    left.source === right.source &&
    left.minPrice === right.minPrice &&
    left.maxPrice === right.maxPrice &&
    left.activeOnly === right.activeOnly &&
    left.sortBy === right.sortBy &&
    left.direction === right.direction &&
    left.size === right.size &&
    left.page === right.page
  );
};

export const isSameDealSearchRequest = (left: DealSearchRequest, right: DealSearchRequest): boolean => {
  return buildQueryString(toDealSearchQueryParams(left)) === buildQueryString(toDealSearchQueryParams(right));
};

export const getAppliedDealFilterLabels = (request: DealSearchRequest): string[] => {
  const labels: string[] = [];

  if (request.keyword) {
    labels.push(`keyword: "${request.keyword}"`);
  }

  if (request.category) {
    labels.push(`category: "${request.category}"`);
  }

  if (request.source) {
    labels.push(`source: "${request.source}"`);
  }

  if (request.minPrice !== undefined) {
    labels.push(`min price: ${request.minPrice}`);
  }

  if (request.maxPrice !== undefined) {
    labels.push(`max price: ${request.maxPrice}`);
  }

  if (request.activeOnly === false) {
    labels.push("including inactive deals");
  }

  return labels;
};

export const toDealSearchQueryParams = (request: DealSearchRequest): QueryParams => {
  const normalized = toDealSearchRequest(toDealFilterState(request));

  return {
    keyword: normalized.keyword,
    category: normalized.category,
    source: normalized.source,
    minPrice: normalized.minPrice,
    maxPrice: normalized.maxPrice,
    activeOnly:
      normalized.activeOnly === undefined || normalized.activeOnly === DEFAULT_DEALS_ACTIVE_ONLY
        ? undefined
        : normalized.activeOnly,
    page:
      normalized.page === undefined || normalized.page === DEFAULT_DEALS_PAGE ? undefined : normalized.page,
    size:
      normalized.size === undefined || normalized.size === DEFAULT_DEALS_PAGE_SIZE ? undefined : normalized.size,
    sortBy:
      normalized.sortBy === undefined || normalized.sortBy === DEFAULT_DEALS_SORT ? undefined : normalized.sortBy,
    direction:
      normalized.direction === undefined || normalized.direction === DEFAULT_DEALS_DIRECTION
        ? undefined
        : normalized.direction
  };
};

export const buildDealsListingHref = (request: DealSearchRequest): string => {
  return `${APP_ROUTES.public.deals}${buildQueryString(toDealSearchQueryParams(request))}`;
};

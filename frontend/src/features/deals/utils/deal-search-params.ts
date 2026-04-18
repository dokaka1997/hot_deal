import { z } from "zod";

import { APP_ROUTES } from "@/config/routes";
import type { DealCouponFilter, DealFilterState } from "@/features/deals/types/deal-filter-state";
import { buildQueryString, type QueryParams } from "@/lib/utils";
import { SORT_DIRECTIONS, type SortDirection } from "@/types/contracts/backend";
import { DEAL_SORT_FIELDS, type DealSearchRequest, type DealSortField } from "@/types/requests";

export const DEFAULT_DEALS_PAGE = 0;
export const DEFAULT_DEALS_PAGE_SIZE = 12;
export const DEFAULT_DEALS_SORT = DEAL_SORT_FIELDS[0];
export const DEFAULT_DEALS_DIRECTION = SORT_DIRECTIONS[1];
export const DEFAULT_DEALS_ACTIVE_ONLY = true;
export const DEFAULT_DEALS_COUPON_FILTER: DealCouponFilter = "all";

export interface DealSortPreset {
  key: "newest" | "highestDiscount" | "lowestPrice" | "highestPrice" | "hottest";
  label: string;
  sortBy: DealSortField;
  direction: SortDirection;
}

export const DEAL_SORT_PRESETS: readonly DealSortPreset[] = [
  { key: "newest", label: "Mới nhất", sortBy: "lastSeenAt", direction: "DESC" },
  { key: "highestDiscount", label: "Giảm giá cao nhất", sortBy: "discountPercent", direction: "DESC" },
  { key: "lowestPrice", label: "Giá thấp nhất", sortBy: "dealPrice", direction: "ASC" },
  { key: "highestPrice", label: "Giá cao nhất", sortBy: "dealPrice", direction: "DESC" },
  { key: "hottest", label: "Nổi bật", sortBy: "dealScore", direction: "DESC" }
] as const;

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
const hasCouponParamSchema = z.enum(["true", "false"]);

const keywordParamSchema = optionalQueryString(120);
const categoryParamSchema = optionalQueryString(120);
const sourceParamSchema = optionalQueryString(64);

type SearchParamsLike = {
  entries(): IterableIterator<[string, string]>;
};

const toParamObject = (params: SearchParamsLike): Record<string, string> => {
  return Object.fromEntries(params.entries());
};

const parseWithFallback = <TSchema extends z.ZodTypeAny>(
  schema: TSchema,
  input: unknown,
  fallback: z.output<TSchema>
): z.output<TSchema> => {
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

const normalizeOptionalToken = (value?: string, maxLength = 120): string | undefined => {
  const normalized = normalizeOptionalText(value, maxLength);
  return normalized ? normalized.toLowerCase() : undefined;
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

const toCouponFilterValue = (hasCoupon?: boolean): DealCouponFilter => {
  if (hasCoupon === true) {
    return "withCoupon";
  }

  if (hasCoupon === false) {
    return "withoutCoupon";
  }

  return DEFAULT_DEALS_COUPON_FILTER;
};

const toHasCouponValue = (value: DealCouponFilter): boolean | undefined => {
  if (value === "withCoupon") {
    return true;
  }

  if (value === "withoutCoupon") {
    return false;
  }

  return undefined;
};

export const resolveDealSortPreset = (
  sortBy?: DealSortField,
  direction?: SortDirection
): DealSortPreset["key"] => {
  const matchedPreset = DEAL_SORT_PRESETS.find((preset) => {
    return preset.sortBy === sortBy && preset.direction === direction;
  });

  return matchedPreset?.key ?? DEAL_SORT_PRESETS[0].key;
};

export const getDealSortPreset = (presetKey: DealSortPreset["key"]): DealSortPreset => {
  const matchedPreset = DEAL_SORT_PRESETS.find((preset) => preset.key === presetKey);
  return matchedPreset ?? DEAL_SORT_PRESETS[0];
};

export const createDefaultDealFilterState = (): DealFilterState => {
  return {
    keyword: "",
    category: "",
    source: "",
    minPrice: "",
    maxPrice: "",
    activeOnly: DEFAULT_DEALS_ACTIVE_ONLY,
    hasCoupon: DEFAULT_DEALS_COUPON_FILTER,
    sortBy: DEFAULT_DEALS_SORT,
    direction: DEFAULT_DEALS_DIRECTION,
    size: DEFAULT_DEALS_PAGE_SIZE,
    page: DEFAULT_DEALS_PAGE
  };
};

export const parseDealSearchParams = (params: SearchParamsLike): DealSearchRequest => {
  const paramObject = toParamObject(params);
  const parsedActiveOnly = activeOnlyParamSchema.safeParse(paramObject.activeOnly);
  const parsedHasCoupon = hasCouponParamSchema.safeParse(paramObject.hasCoupon);

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
    hasCoupon: parsedHasCoupon.success ? parsedHasCoupon.data === "true" : undefined,
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
    category: normalizeOptionalToken(request.category, 120) ?? "",
    source: normalizeOptionalToken(request.source, 64) ?? "",
    minPrice: toPriceInputValue(request.minPrice),
    maxPrice: toPriceInputValue(request.maxPrice),
    activeOnly: request.activeOnly ?? defaults.activeOnly,
    hasCoupon: toCouponFilterValue(request.hasCoupon),
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
    category: normalizeOptionalToken(filters.category, 120),
    source: normalizeOptionalToken(filters.source, 64),
    minPrice: normalizedPriceRange.minPrice,
    maxPrice: normalizedPriceRange.maxPrice,
    activeOnly: filters.activeOnly ?? defaults.activeOnly,
    hasCoupon: toHasCouponValue(filters.hasCoupon),
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
    left.hasCoupon === right.hasCoupon &&
    left.sortBy === right.sortBy &&
    left.direction === right.direction &&
    left.size === right.size &&
    left.page === right.page
  );
};

export const isSameDealSearchRequest = (left: DealSearchRequest, right: DealSearchRequest): boolean => {
  return buildQueryString(toDealSearchQueryParams(left)) === buildQueryString(toDealSearchQueryParams(right));
};

export type AppliedDealFilterKey =
  | "keyword"
  | "category"
  | "source"
  | "minPrice"
  | "maxPrice"
  | "activeOnly"
  | "hasCoupon";

export interface AppliedDealFilterChip {
  key: AppliedDealFilterKey;
  label: string;
}

const formatAppliedPrice = (value: number): string => {
  return Number(value).toLocaleString("vi-VN");
};

const CATEGORY_CHIP_LABEL_MAP: Record<string, string> = {
  phone: "Điện thoại",
  laptop: "Laptop",
  audio: "Tai nghe",
  watch: "Đồng hồ",
  fashion: "Thời trang",
  home: "Gia dụng",
  beauty: "Làm đẹp",
  voucher: "Voucher"
};

const toAppliedCategoryLabel = (value: string): string => {
  const normalized = value.trim().toLowerCase();
  return CATEGORY_CHIP_LABEL_MAP[normalized] ?? value;
};

export const getAppliedDealFilterChips = (request: DealSearchRequest): AppliedDealFilterChip[] => {
  const chips: AppliedDealFilterChip[] = [];

  if (request.keyword) {
    chips.push({ key: "keyword", label: `Từ khóa: "${request.keyword}"` });
  }

  if (request.category) {
    chips.push({ key: "category", label: `Danh mục: ${toAppliedCategoryLabel(request.category)}` });
  }

  if (request.source) {
    chips.push({ key: "source", label: `Nguồn: ${request.source.toUpperCase()}` });
  }

  if (request.minPrice !== undefined) {
    chips.push({ key: "minPrice", label: `Giá từ ${formatAppliedPrice(request.minPrice)}` });
  }

  if (request.maxPrice !== undefined) {
    chips.push({ key: "maxPrice", label: `Giá đến ${formatAppliedPrice(request.maxPrice)}` });
  }

  if (request.activeOnly === false) {
    chips.push({ key: "activeOnly", label: "Bao gồm ưu đãi hết hạn" });
  }

  if (request.hasCoupon === true) {
    chips.push({ key: "hasCoupon", label: "Chỉ có mã giảm giá" });
  } else if (request.hasCoupon === false) {
    chips.push({ key: "hasCoupon", label: "Không có mã giảm giá" });
  }

  return chips;
};

export const getAppliedDealFilterLabels = (request: DealSearchRequest): string[] => {
  return getAppliedDealFilterChips(request).map((chip) => chip.label);
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
    hasCoupon: normalized.hasCoupon === undefined ? undefined : normalized.hasCoupon,
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

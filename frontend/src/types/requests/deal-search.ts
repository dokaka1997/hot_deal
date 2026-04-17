import type { PaginationRequest } from "@/types/requests/pagination";

export const DEAL_SORT_FIELDS = [
  "lastSeenAt",
  "dealPrice",
  "discountPercent",
  "dealScore",
  "createdAt"
] as const;

export type DealSortField = (typeof DEAL_SORT_FIELDS)[number];

export interface DealSearchRequest extends Omit<PaginationRequest, "sortBy"> {
  keyword?: string;
  category?: string;
  source?: string;
  minPrice?: number;
  maxPrice?: number;
  activeOnly?: boolean;
  sortBy?: DealSortField;
}

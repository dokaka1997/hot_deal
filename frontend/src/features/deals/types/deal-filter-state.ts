import type { SortDirection } from "@/types/contracts/backend";
import type { DealSortField } from "@/types/requests";

export type DealCouponFilter = "all" | "withCoupon" | "withoutCoupon";

export interface DealFilterState {
  keyword: string;
  category: string;
  source: string;
  minPrice: string;
  maxPrice: string;
  activeOnly: boolean;
  hasCoupon: DealCouponFilter;
  sortBy: DealSortField;
  direction: SortDirection;
  size: number;
  page: number;
}

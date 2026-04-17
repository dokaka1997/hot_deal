import type { SortDirection } from "@/types/contracts/backend";
import type { DealSortField } from "@/types/requests";

export interface DealFilterState {
  keyword: string;
  category: string;
  source: string;
  minPrice: string;
  maxPrice: string;
  activeOnly: boolean;
  sortBy: DealSortField;
  direction: SortDirection;
  size: number;
  page: number;
}

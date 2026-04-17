import type { SortDirection } from "@/types/contracts/backend";

export interface PaginatedResult<TItem> {
  items: TItem[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  numberOfElements: number;
  first: boolean;
  last: boolean;
  sortBy: string;
  direction: SortDirection;
}

import type { SortDirection } from "@/types/contracts/backend";

export interface PaginationRequest {
  page?: number;
  size?: number;
  sortBy?: string;
  direction?: SortDirection;
}

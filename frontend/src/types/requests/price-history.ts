import type { PaginationRequest } from "@/types/requests/pagination";

export interface DealPriceHistoryRequest extends PaginationRequest {
  dealId: number;
}

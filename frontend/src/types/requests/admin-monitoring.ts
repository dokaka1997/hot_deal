import type { JobExecutionStatus, RawDealStatus } from "@/types/contracts/backend";
import type { PaginationRequest } from "@/types/requests/pagination";

export interface AdminCollectorJobHistoryRequest extends PaginationRequest {
  sourceCode?: string;
  status?: JobExecutionStatus;
}

export interface AdminFailedRawDealRequest extends PaginationRequest {
  sourceCode?: string;
  status?: RawDealStatus;
}

export interface AdminRawDealReprocessRequest {
  rawDealId: number;
}

import type { SortDirection } from "@/types/contracts/backend/enums";

export type DecimalValue = number | string;
export type IsoDateTimeString = string;

export interface ApiFieldErrorContract {
  field: string;
  message: string;
  rejectedValue: string | null;
}

export interface ApiErrorContract {
  code: string;
  message: string;
  fieldErrors: ApiFieldErrorContract[];
}

export interface ApiResponseContract<TData> {
  timestamp: IsoDateTimeString;
  success: boolean;
  path: string;
  traceId: string;
  data: TData | null;
  error: ApiErrorContract | null;
}

export interface PageResponseContract<TItem> {
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

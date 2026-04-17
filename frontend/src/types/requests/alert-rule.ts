import type { NotificationChannel } from "@/types/contracts/backend";
import type { PaginationRequest } from "@/types/requests/pagination";

export interface CreateAlertRuleRequest {
  subscriberKey: string;
  name?: string;
  keyword?: string;
  category?: string;
  sourceCode?: string;
  maxPrice?: number;
  minDiscountPercent?: number;
  notificationChannel?: NotificationChannel;
  notificationTarget?: string;
}

export interface AlertRuleListRequest extends PaginationRequest {
  subscriberKey?: string;
}

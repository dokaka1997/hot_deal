import type { DecimalValue, IsoDateTimeString } from "@/types/contracts/backend/common";
import type { AlertRuleStatus, NotificationChannel } from "@/types/contracts/backend/enums";

export interface AlertRuleApi {
  id: number;
  subscriberKey: string;
  name: string | null;
  keyword: string | null;
  category: string | null;
  sourceCode: string | null;
  maxPrice: DecimalValue | null;
  minDiscountPercent: DecimalValue | null;
  notificationChannel: NotificationChannel;
  notificationTarget: string | null;
  status: AlertRuleStatus;
  lastTriggeredAt: IsoDateTimeString | null;
  createdAt: IsoDateTimeString;
  updatedAt: IsoDateTimeString;
}

export interface AlertRuleOperationApi {
  alertRuleId: number;
  operation: string;
  message: string;
}

export interface CreateAlertRulePayloadApi {
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

import type { AlertRuleStatus, NotificationChannel } from "@/types/contracts/backend";

export interface AlertRule {
  id: number;
  subscriberKey: string;
  name: string | null;
  keyword: string | null;
  category: string | null;
  sourceCode: string | null;
  maxPrice: number | null;
  minDiscountPercent: number | null;
  notificationChannel: NotificationChannel;
  notificationTarget: string | null;
  status: AlertRuleStatus;
  lastTriggeredAt: Date | null;
  createdAt: Date;
  updatedAt: Date;
}

export interface AlertRuleOperationResult {
  alertRuleId: number;
  operation: string;
  message: string;
}

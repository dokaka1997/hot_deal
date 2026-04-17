import { mapPageResponse, toDateOrNull, toNumberOrNull, toRequiredDate } from "@/mappers/common-mappers";
import type {
  AlertRuleApi,
  AlertRuleOperationApi,
  CreateAlertRulePayloadApi,
  PageResponseContract
} from "@/types/contracts/backend";
import type { QueryParams } from "@/lib/utils/query-string";
import type { AlertRule, AlertRuleOperationResult, PaginatedResult } from "@/types/domain";
import type { AlertRuleListRequest, CreateAlertRuleRequest as CreateAlertRuleInput } from "@/types/requests";

export const mapAlertRule = (api: AlertRuleApi): AlertRule => {
  return {
    id: api.id,
    subscriberKey: api.subscriberKey,
    name: api.name,
    keyword: api.keyword,
    category: api.category,
    sourceCode: api.sourceCode,
    maxPrice: toNumberOrNull(api.maxPrice),
    minDiscountPercent: toNumberOrNull(api.minDiscountPercent),
    notificationChannel: api.notificationChannel,
    notificationTarget: api.notificationTarget,
    status: api.status,
    lastTriggeredAt: toDateOrNull(api.lastTriggeredAt),
    createdAt: toRequiredDate(api.createdAt),
    updatedAt: toRequiredDate(api.updatedAt)
  };
};

export const mapAlertRulePage = (page: PageResponseContract<AlertRuleApi>): PaginatedResult<AlertRule> =>
  mapPageResponse(page, mapAlertRule);

export const mapAlertRuleOperation = (api: AlertRuleOperationApi): AlertRuleOperationResult => {
  return {
    alertRuleId: api.alertRuleId,
    operation: api.operation,
    message: api.message
  };
};

export const toCreateAlertRuleApiPayload = (
  input: CreateAlertRuleInput
): CreateAlertRulePayloadApi => {
  return {
    subscriberKey: input.subscriberKey,
    name: input.name,
    keyword: input.keyword,
    category: input.category,
    sourceCode: input.sourceCode,
    maxPrice: input.maxPrice,
    minDiscountPercent: input.minDiscountPercent,
    notificationChannel: input.notificationChannel,
    notificationTarget: input.notificationTarget
  };
};

export const toAlertRuleListQuery = (input: AlertRuleListRequest): QueryParams => {
  return {
    subscriberKey: input.subscriberKey,
    page: input.page,
    size: input.size,
    sortBy: input.sortBy,
    direction: input.direction
  };
};

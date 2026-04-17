import { mapPageResponse, toDateOrNull, toRequiredDate } from "@/mappers/common-mappers";
import type {
  AdminCollectorJobExecutionApi,
  AdminFailedRawDealApi,
  AdminRawDealReprocessResultApi,
  AdminSourceHealthApi,
  PageResponseContract
} from "@/types/contracts/backend";
import type {
  AdminCollectorJobExecution,
  AdminFailedRawDeal,
  AdminRawDealReprocessResult,
  AdminSourceHealth,
  PaginatedResult
} from "@/types/domain";

export const mapAdminCollectorJobExecution = (
  api: AdminCollectorJobExecutionApi
): AdminCollectorJobExecution => {
  return {
    id: api.id,
    sourceCode: api.sourceCode,
    sourceName: api.sourceName,
    jobName: api.jobName,
    triggerType: api.triggerType,
    status: api.status,
    runKey: api.runKey,
    startedAt: toRequiredDate(api.startedAt),
    finishedAt: toDateOrNull(api.finishedAt),
    durationMs: api.durationMs,
    totalFetched: api.totalFetched,
    totalPersisted: api.totalPersisted,
    totalFailed: api.totalFailed,
    errorMessage: api.errorMessage
  };
};

export const mapAdminCollectorJobExecutionPage = (
  page: PageResponseContract<AdminCollectorJobExecutionApi>
): PaginatedResult<AdminCollectorJobExecution> => mapPageResponse(page, mapAdminCollectorJobExecution);

export const mapAdminSourceHealth = (api: AdminSourceHealthApi): AdminSourceHealth => {
  return {
    sourceId: api.sourceId,
    sourceCode: api.sourceCode,
    sourceName: api.sourceName,
    sourceType: api.sourceType,
    sourceStatus: api.sourceStatus,
    lastSuccessAt: toDateOrNull(api.lastSuccessAt),
    lastFailureAt: toDateOrNull(api.lastFailureAt),
    lastErrorMessage: api.lastErrorMessage,
    lastJobStatus: api.lastJobStatus,
    lastJobStartedAt: toDateOrNull(api.lastJobStartedAt),
    lastJobFinishedAt: toDateOrNull(api.lastJobFinishedAt),
    jobsLast24Hours: api.jobsLast24Hours,
    failuresLast24Hours: api.failuresLast24Hours
  };
};

export const mapAdminFailedRawDeal = (api: AdminFailedRawDealApi): AdminFailedRawDeal => {
  return {
    rawDealId: api.rawDealId,
    sourceId: api.sourceId,
    sourceCode: api.sourceCode,
    sourceName: api.sourceName,
    jobExecutionId: api.jobExecutionId,
    sourceDealId: api.sourceDealId,
    sourceRecordKey: api.sourceRecordKey,
    sourceRecordHash: api.sourceRecordHash,
    status: api.status,
    ingestedAt: toRequiredDate(api.ingestedAt),
    normalizedAt: toDateOrNull(api.normalizedAt),
    parseError: api.parseError
  };
};

export const mapAdminFailedRawDealPage = (
  page: PageResponseContract<AdminFailedRawDealApi>
): PaginatedResult<AdminFailedRawDeal> => mapPageResponse(page, mapAdminFailedRawDeal);

export const mapAdminRawDealReprocessResult = (
  api: AdminRawDealReprocessResultApi
): AdminRawDealReprocessResult => {
  return {
    rawDealId: api.rawDealId,
    status: api.status,
    message: api.message
  };
};

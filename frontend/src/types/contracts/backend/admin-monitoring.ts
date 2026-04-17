import type { IsoDateTimeString } from "@/types/contracts/backend/common";
import type {
  JobExecutionStatus,
  JobTriggerType,
  RawDealStatus,
  SourceStatus,
  SourceType
} from "@/types/contracts/backend/enums";

export interface AdminCollectorJobExecutionApi {
  id: number;
  sourceCode: string | null;
  sourceName: string | null;
  jobName: string | null;
  triggerType: JobTriggerType;
  status: JobExecutionStatus;
  runKey: string;
  startedAt: IsoDateTimeString;
  finishedAt: IsoDateTimeString | null;
  durationMs: number | null;
  totalFetched: number | null;
  totalPersisted: number | null;
  totalFailed: number | null;
  errorMessage: string | null;
}

export interface AdminSourceHealthApi {
  sourceId: number;
  sourceCode: string;
  sourceName: string;
  sourceType: SourceType;
  sourceStatus: SourceStatus;
  lastSuccessAt: IsoDateTimeString | null;
  lastFailureAt: IsoDateTimeString | null;
  lastErrorMessage: string | null;
  lastJobStatus: string | null;
  lastJobStartedAt: IsoDateTimeString | null;
  lastJobFinishedAt: IsoDateTimeString | null;
  jobsLast24Hours: number;
  failuresLast24Hours: number;
}

export interface AdminFailedRawDealApi {
  rawDealId: number;
  sourceId: number;
  sourceCode: string | null;
  sourceName: string | null;
  jobExecutionId: number | null;
  sourceDealId: string | null;
  sourceRecordKey: string | null;
  sourceRecordHash: string | null;
  status: RawDealStatus;
  ingestedAt: IsoDateTimeString;
  normalizedAt: IsoDateTimeString | null;
  parseError: string | null;
}

export interface AdminRawDealReprocessResultApi {
  rawDealId: number;
  status: RawDealStatus;
  message: string;
}

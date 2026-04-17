import type {
  JobExecutionStatus,
  JobTriggerType,
  RawDealStatus,
  SourceStatus,
  SourceType
} from "@/types/contracts/backend";

export interface AdminCollectorJobExecution {
  id: number;
  sourceCode: string | null;
  sourceName: string | null;
  jobName: string | null;
  triggerType: JobTriggerType;
  status: JobExecutionStatus;
  runKey: string;
  startedAt: Date;
  finishedAt: Date | null;
  durationMs: number | null;
  totalFetched: number | null;
  totalPersisted: number | null;
  totalFailed: number | null;
  errorMessage: string | null;
}

export interface AdminSourceHealth {
  sourceId: number;
  sourceCode: string;
  sourceName: string;
  sourceType: SourceType;
  sourceStatus: SourceStatus;
  lastSuccessAt: Date | null;
  lastFailureAt: Date | null;
  lastErrorMessage: string | null;
  lastJobStatus: string | null;
  lastJobStartedAt: Date | null;
  lastJobFinishedAt: Date | null;
  jobsLast24Hours: number;
  failuresLast24Hours: number;
}

export interface AdminFailedRawDeal {
  rawDealId: number;
  sourceId: number;
  sourceCode: string | null;
  sourceName: string | null;
  jobExecutionId: number | null;
  sourceDealId: string | null;
  sourceRecordKey: string | null;
  sourceRecordHash: string | null;
  status: RawDealStatus;
  ingestedAt: Date;
  normalizedAt: Date | null;
  parseError: string | null;
}

export interface AdminRawDealReprocessResult {
  rawDealId: number;
  status: RawDealStatus;
  message: string;
}

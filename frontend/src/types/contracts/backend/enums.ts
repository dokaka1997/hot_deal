export const SORT_DIRECTIONS = ["ASC", "DESC"] as const;
export type SortDirection = (typeof SORT_DIRECTIONS)[number];

export const DEAL_STATUSES = ["ACTIVE", "EXPIRED", "INACTIVE"] as const;
export type DealStatus = (typeof DEAL_STATUSES)[number];

export const AVAILABILITY_STATUSES = ["IN_STOCK", "OUT_OF_STOCK", "UNKNOWN"] as const;
export type AvailabilityStatus = (typeof AVAILABILITY_STATUSES)[number];

export const ALERT_RULE_STATUSES = ["ACTIVE", "DISABLED"] as const;
export type AlertRuleStatus = (typeof ALERT_RULE_STATUSES)[number];

export const NOTIFICATION_CHANNELS = ["INTERNAL_LOG", "EMAIL"] as const;
export type NotificationChannel = (typeof NOTIFICATION_CHANNELS)[number];

export const JOB_EXECUTION_STATUSES = ["RUNNING", "SUCCESS", "FAILED", "PARTIAL", "CANCELLED"] as const;
export type JobExecutionStatus = (typeof JOB_EXECUTION_STATUSES)[number];

export const JOB_TRIGGER_TYPES = ["SCHEDULED", "MANUAL", "RETRY"] as const;
export type JobTriggerType = (typeof JOB_TRIGGER_TYPES)[number];

export const RAW_DEAL_STATUSES = ["NEW", "NORMALIZED", "REJECTED", "DUPLICATE", "ERROR"] as const;
export type RawDealStatus = (typeof RAW_DEAL_STATUSES)[number];

export const SOURCE_STATUSES = ["ACTIVE", "PAUSED", "DISABLED"] as const;
export type SourceStatus = (typeof SOURCE_STATUSES)[number];

export const SOURCE_TYPES = ["API", "SCRAPER", "FEED", "MANUAL"] as const;
export type SourceType = (typeof SOURCE_TYPES)[number];

import type { ApiFieldError } from "@/types/api";

export type ApiClientErrorKind =
  | "NETWORK_ERROR"
  | "HTTP_ERROR"
  | "API_ERROR"
  | "INVALID_RESPONSE"
  | "UNKNOWN_ERROR";

interface ApiClientErrorParams {
  kind: ApiClientErrorKind;
  message: string;
  code?: string;
  status?: number;
  path?: string;
  traceId?: string;
  fieldErrors?: ApiFieldError[];
  cause?: unknown;
}

export class ApiClientError extends Error {
  readonly kind: ApiClientErrorKind;
  readonly code?: string;
  readonly status?: number;
  readonly path?: string;
  readonly traceId?: string;
  readonly fieldErrors: ApiFieldError[];
  override readonly cause?: unknown;

  constructor(params: ApiClientErrorParams) {
    super(params.message);
    this.name = "ApiClientError";
    this.kind = params.kind;
    this.code = params.code;
    this.status = params.status;
    this.path = params.path;
    this.traceId = params.traceId;
    this.fieldErrors = params.fieldErrors ?? [];
    this.cause = params.cause;
  }
}

export const isApiClientError = (error: unknown): error is ApiClientError => {
  return error instanceof ApiClientError;
};

import { ENV } from "@/config/env";
import { buildQueryString, createCorrelationId } from "@/lib/utils";
import { ApiClientError } from "@/services/api/errors";
import type { ApiRequestOptions } from "@/services/api/types";
import type { ApiError, ApiResponse } from "@/types/api";

const API_CLIENT_CONFIG = {
  baseUrl: ENV.publicApiBaseUrl,
  correlationHeader: "X-Correlation-Id"
} as const;

const isApiResponse = <T>(value: unknown): value is ApiResponse<T> => {
  if (!value || typeof value !== "object") {
    return false;
  }

  const candidate = value as Partial<ApiResponse<T>>;
  return (
    typeof candidate.success === "boolean" &&
    typeof candidate.path === "string" &&
    typeof candidate.traceId === "string" &&
    "data" in candidate &&
    "error" in candidate
  );
};

const createAuthHeader = (auth?: ApiRequestOptions["auth"]): string | null => {
  if (!auth) {
    return null;
  }

  if (auth.type === "bearer") {
    return `Bearer ${auth.token}`;
  }

  if (typeof btoa !== "undefined") {
    return `Basic ${btoa(`${auth.username}:${auth.password}`)}`;
  }

  if (typeof Buffer !== "undefined") {
    return `Basic ${Buffer.from(`${auth.username}:${auth.password}`).toString("base64")}`;
  }

  return null;
};

const buildHeaders = <TBody>(options: ApiRequestOptions<TBody>): Headers => {
  const headers = new Headers(options.headers);

  headers.set("Accept", "application/json");
  headers.set(API_CLIENT_CONFIG.correlationHeader, createCorrelationId());

  if (options.body !== undefined && !(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }

  const authHeader = createAuthHeader(options.auth);
  if (authHeader) {
    headers.set("Authorization", authHeader);
  }

  return headers;
};

const buildUrl = (path: string, query?: ApiRequestOptions["query"]): string => {
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  return `${API_CLIENT_CONFIG.baseUrl}${normalizedPath}${buildQueryString(query)}`;
};

const parseBody = async (response: Response): Promise<unknown> => {
  if (response.status === 204) {
    return null;
  }

  const responseText = await response.text();
  if (!responseText) {
    return null;
  }

  try {
    return JSON.parse(responseText);
  } catch (error) {
    throw new ApiClientError({
      kind: "INVALID_RESPONSE",
      message: "The API returned an invalid JSON response.",
      status: response.status,
      cause: error
    });
  }
};

const toApiError = (error: ApiError, status: number, traceId?: string, path?: string): ApiClientError => {
  return new ApiClientError({
    kind: "API_ERROR",
    message: error.message,
    code: error.code,
    status,
    traceId,
    path,
    fieldErrors: error.fieldErrors
  });
};

const toHttpError = (status: number, path?: string): ApiClientError => {
  return new ApiClientError({
    kind: "HTTP_ERROR",
    message: `Request failed with status ${status}.`,
    status,
    path
  });
};

export const apiClient = {
  async request<TData, TBody = unknown>(
    path: string,
    options: ApiRequestOptions<TBody> = {}
  ): Promise<TData> {
    const requestUrl = buildUrl(path, options.query);

    const requestInit: RequestInit = {
      method: options.method ?? "GET",
      headers: buildHeaders(options),
      signal: options.signal,
      credentials: options.credentials
    };

    if (options.body !== undefined) {
      requestInit.body = options.body instanceof FormData ? options.body : JSON.stringify(options.body);
    }

    let response: Response;
    try {
      response = await fetch(requestUrl, requestInit);
    } catch (error) {
      if (error instanceof DOMException && error.name === "AbortError") {
        throw error;
      }

      throw new ApiClientError({
        kind: "NETWORK_ERROR",
        message: "Unable to reach the API server.",
        path,
        cause: error
      });
    }

    const payload = await parseBody(response);
    if (!isApiResponse<TData>(payload)) {
      throw new ApiClientError({
        kind: "INVALID_RESPONSE",
        message: "The API response format is invalid.",
        status: response.status,
        path
      });
    }

    if (!response.ok) {
      if (payload.error) {
        throw toApiError(payload.error, response.status, payload.traceId, payload.path);
      }
      throw toHttpError(response.status, payload.path);
    }

    if (!payload.success || payload.error) {
      if (payload.error) {
        throw toApiError(payload.error, response.status, payload.traceId, payload.path);
      }
      throw new ApiClientError({
        kind: "INVALID_RESPONSE",
        message: "The API marked this response as unsuccessful.",
        status: response.status,
        traceId: payload.traceId,
        path: payload.path
      });
    }

    return payload.data as TData;
  },

  get<TData>(path: string, options: Omit<ApiRequestOptions, "method" | "body"> = {}) {
    return this.request<TData>(path, {
      ...options,
      method: "GET"
    });
  },

  post<TData, TBody = unknown>(path: string, body?: TBody, options: Omit<ApiRequestOptions, "method" | "body"> = {}) {
    return this.request<TData, TBody>(path, {
      ...options,
      method: "POST",
      body
    });
  },

  put<TData, TBody = unknown>(path: string, body?: TBody, options: Omit<ApiRequestOptions, "method" | "body"> = {}) {
    return this.request<TData, TBody>(path, {
      ...options,
      method: "PUT",
      body
    });
  },

  patch<TData, TBody = unknown>(
    path: string,
    body?: TBody,
    options: Omit<ApiRequestOptions, "method" | "body"> = {}
  ) {
    return this.request<TData, TBody>(path, {
      ...options,
      method: "PATCH",
      body
    });
  },

  delete<TData>(path: string, options: Omit<ApiRequestOptions, "method" | "body"> = {}) {
    return this.request<TData>(path, {
      ...options,
      method: "DELETE"
    });
  }
};

export { API_CLIENT_CONFIG };

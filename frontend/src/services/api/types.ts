import type { QueryParams } from "@/lib/utils/query-string";

export type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

export type ApiAuth =
  | {
      type: "basic";
      username: string;
      password: string;
    }
  | {
      type: "bearer";
      token: string;
    };

export interface ApiRequestOptions<TBody = unknown> {
  method?: HttpMethod;
  query?: QueryParams;
  body?: TBody;
  headers?: HeadersInit;
  signal?: AbortSignal;
  credentials?: RequestCredentials;
  auth?: ApiAuth;
}

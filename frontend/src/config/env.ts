import { z } from "zod";

const DEFAULT_API_BASE_URL = "http://localhost:8080/api/v1";

const trimTrailingSlash = (value: string): string => value.replace(/\/+$/, "");

const envSchema = z.object({
  NODE_ENV: z.enum(["development", "test", "production"]).default("development"),
  NEXT_PUBLIC_API_BASE_URL: z.string().trim().url().default(DEFAULT_API_BASE_URL),
  NEXT_PUBLIC_ENABLE_QUERY_DEVTOOLS: z.enum(["true", "false"]).default("false")
});

const parsedEnv = envSchema.parse({
  NODE_ENV: process.env.NODE_ENV,
  NEXT_PUBLIC_API_BASE_URL: process.env.NEXT_PUBLIC_API_BASE_URL ?? DEFAULT_API_BASE_URL,
  NEXT_PUBLIC_ENABLE_QUERY_DEVTOOLS: process.env.NEXT_PUBLIC_ENABLE_QUERY_DEVTOOLS
});

export const ENV = {
  nodeEnv: parsedEnv.NODE_ENV,
  publicApiBaseUrl: trimTrailingSlash(parsedEnv.NEXT_PUBLIC_API_BASE_URL),
  enableQueryDevtools: parsedEnv.NEXT_PUBLIC_ENABLE_QUERY_DEVTOOLS === "true"
} as const;

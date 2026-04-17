import { ENV } from "@/config/env";

export const APP_CONFIG = {
  name: "Hot Deal",
  description: "Frontend foundation for deal discovery and operations.",
  publicApiBaseUrl: ENV.publicApiBaseUrl
} as const;

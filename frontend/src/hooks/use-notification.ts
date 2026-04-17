"use client";

import { toast } from "sonner";

interface NotifyOptions {
  description?: string;
}

export const useNotification = () => {
  return {
    success: (message: string, options?: NotifyOptions) =>
      toast.success(message, { description: options?.description }),
    error: (message: string, options?: NotifyOptions) =>
      toast.error(message, { description: options?.description }),
    info: (message: string, options?: NotifyOptions) =>
      toast.info(message, { description: options?.description })
  };
};

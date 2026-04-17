"use client";

import { MutationCache, QueryCache, QueryClient } from "@tanstack/react-query";
import { toast } from "sonner";

import { getReadableErrorMessage, shouldHandleErrorGlobally } from "@/services/api";

const shouldSkipGlobalHandler = (meta: unknown): boolean => {
  if (!meta || typeof meta !== "object") {
    return false;
  }

  return Boolean((meta as { skipGlobalErrorHandler?: boolean }).skipGlobalErrorHandler);
};

const notifyError = (error: unknown): void => {
  if (!shouldHandleErrorGlobally(error)) {
    return;
  }

  toast.error(getReadableErrorMessage(error));
};

export const createQueryClient = (): QueryClient => {
  return new QueryClient({
    queryCache: new QueryCache({
      onError: (error, query) => {
        if (shouldSkipGlobalHandler(query.meta)) {
          return;
        }

        notifyError(error);
      }
    }),
    mutationCache: new MutationCache({
      onError: (error, _variables, _context, mutation) => {
        if (shouldSkipGlobalHandler(mutation.meta)) {
          return;
        }

        notifyError(error);
      }
    }),
    defaultOptions: {
      queries: {
        retry: 1,
        staleTime: 30_000,
        gcTime: 5 * 60_000,
        refetchOnWindowFocus: false
      },
      mutations: {
        retry: 0
      }
    }
  });
};

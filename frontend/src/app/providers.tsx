"use client";

import { QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import type { PropsWithChildren } from "react";
import { useState } from "react";

import { AppToaster } from "@/components/ui/toaster";
import { ENV } from "@/config/env";
import { createQueryClient } from "@/lib/query/query-client";

export const AppProviders = ({ children }: PropsWithChildren) => {
  const [queryClient] = useState(() => createQueryClient());

  return (
    <QueryClientProvider client={queryClient}>
      {children}
      <AppToaster />
      {ENV.enableQueryDevtools ? <ReactQueryDevtools initialIsOpen={false} /> : null}
    </QueryClientProvider>
  );
};

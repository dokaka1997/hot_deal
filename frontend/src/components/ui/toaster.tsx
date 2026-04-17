"use client";

import { Toaster } from "sonner";

export const AppToaster = () => {
  return (
    <Toaster
      closeButton
      position="top-right"
      richColors
      toastOptions={{
        duration: 4000
      }}
    />
  );
};

import { ApiClientError, isApiClientError } from "@/services/api/errors";

const FALLBACK_MESSAGE = "Something went wrong. Please try again.";

export const isAbortRequestError = (error: unknown): boolean => {
  return error instanceof DOMException && error.name === "AbortError";
};

export const toApiClientError = (error: unknown): ApiClientError => {
  if (isApiClientError(error)) {
    return error;
  }

  if (error instanceof Error) {
    return new ApiClientError({
      kind: "UNKNOWN_ERROR",
      message: error.message || FALLBACK_MESSAGE,
      cause: error
    });
  }

  return new ApiClientError({
    kind: "UNKNOWN_ERROR",
    message: FALLBACK_MESSAGE
  });
};

export const getReadableErrorMessage = (error: unknown): string => {
  return toApiClientError(error).message;
};

export const shouldHandleErrorGlobally = (error: unknown): boolean => {
  return !isAbortRequestError(error);
};

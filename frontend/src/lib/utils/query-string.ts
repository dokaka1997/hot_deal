type QueryPrimitive = string | number | boolean;
type QueryValue = QueryPrimitive | null | undefined | QueryPrimitive[];

export type QueryParams = Record<string, QueryValue>;

export const buildQueryString = (query?: QueryParams): string => {
  if (!query) {
    return "";
  }

  const searchParams = new URLSearchParams();

  for (const [key, value] of Object.entries(query)) {
    if (value === null || value === undefined) {
      continue;
    }

    if (Array.isArray(value)) {
      value.forEach((item) => {
        searchParams.append(key, String(item));
      });
      continue;
    }

    searchParams.append(key, String(value));
  }

  const encoded = searchParams.toString();
  return encoded ? `?${encoded}` : "";
};

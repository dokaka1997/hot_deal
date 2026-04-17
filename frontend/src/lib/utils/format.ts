export const formatCurrency = (
  value: number,
  currency = "USD",
  locale = "en-US"
): string => {
  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
    maximumFractionDigits: 2
  }).format(value);
};

export const formatDateTime = (
  value: string | number | Date,
  locale = "en-US",
  options?: Intl.DateTimeFormatOptions
): string => {
  return new Intl.DateTimeFormat(locale, {
    year: "numeric",
    month: "short",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
    ...options
  }).format(new Date(value));
};

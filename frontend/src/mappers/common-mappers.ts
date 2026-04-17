import type { DecimalValue, IsoDateTimeString, PageResponseContract } from "@/types/contracts/backend";
import type { PaginatedResult } from "@/types/domain";

export const toNumberOrNull = (value: DecimalValue | null | undefined): number | null => {
  if (value === null || value === undefined) {
    return null;
  }

  if (typeof value === "number") {
    return Number.isFinite(value) ? value : null;
  }

  const parsed = Number(value);
  return Number.isFinite(parsed) ? parsed : null;
};

export const toRequiredNumber = (value: DecimalValue): number => {
  if (typeof value === "number") {
    return value;
  }

  const parsed = Number(value);
  if (!Number.isFinite(parsed)) {
    throw new Error(`Unable to parse decimal value: ${value}`);
  }

  return parsed;
};

export const toDateOrNull = (value: IsoDateTimeString | null | undefined): Date | null => {
  if (!value) {
    return null;
  }

  const parsed = new Date(value);
  return Number.isNaN(parsed.getTime()) ? null : parsed;
};

export const toRequiredDate = (value: IsoDateTimeString): Date => {
  const parsed = toDateOrNull(value);
  if (!parsed) {
    throw new Error(`Unable to parse date value: ${value}`);
  }
  return parsed;
};

export const mapPageResponse = <TApi, TDomain>(
  page: PageResponseContract<TApi>,
  mapItem: (item: TApi) => TDomain
): PaginatedResult<TDomain> => {
  return {
    items: page.items.map(mapItem),
    page: page.page,
    size: page.size,
    totalPages: page.totalPages,
    totalElements: page.totalElements,
    numberOfElements: page.numberOfElements,
    first: page.first,
    last: page.last,
    sortBy: page.sortBy,
    direction: page.direction
  };
};

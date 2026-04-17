import type { DecimalValue, IsoDateTimeString } from "@/types/contracts/backend/common";
import type { AvailabilityStatus } from "@/types/contracts/backend/enums";

export interface PriceHistoryPointApi {
  id: number;
  dealId: number;
  productId: number | null;
  sourceCode: string | null;
  sourceName: string | null;
  capturedAt: IsoDateTimeString;
  currency: string;
  originalPrice: DecimalValue | null;
  dealPrice: DecimalValue;
  discountPercent: DecimalValue | null;
  availabilityStatus: AvailabilityStatus;
}

export interface DealPriceHistoryApi {
  dealId: number;
  points: PriceHistoryPointApi[];
}

import type { AvailabilityStatus } from "@/types/contracts/backend";

export interface PriceHistoryPoint {
  id: number;
  dealId: number;
  productId: number | null;
  sourceCode: string | null;
  sourceName: string | null;
  capturedAt: Date;
  currency: string;
  originalPrice: number | null;
  dealPrice: number;
  discountPercent: number | null;
  availabilityStatus: AvailabilityStatus;
}

export interface DealPriceHistory {
  dealId: number;
  points: PriceHistoryPoint[];
}

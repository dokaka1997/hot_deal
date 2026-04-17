import type { IsoDateTimeString } from "@/types/contracts/backend/common";

export interface ProductSummaryApi {
  id: number;
  name: string;
  brand: string | null;
  category: string | null;
}

export interface ProductDetailApi extends ProductSummaryApi {
  canonicalSku: string | null;
  normalizedName: string | null;
  imageUrl: string | null;
  fingerprint: string | null;
  attributes: Record<string, unknown> | null;
  active: boolean;
  createdAt: IsoDateTimeString;
  updatedAt: IsoDateTimeString;
}

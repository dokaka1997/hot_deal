export interface ProductSummary {
  id: number;
  name: string;
  brand: string | null;
  category: string | null;
}

export interface ProductDetail extends ProductSummary {
  canonicalSku: string | null;
  normalizedName: string | null;
  imageUrl: string | null;
  fingerprint: string | null;
  attributes: Record<string, unknown> | null;
  active: boolean;
  createdAt: Date;
  updatedAt: Date;
}

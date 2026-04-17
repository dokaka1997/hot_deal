export interface DealCardModel {
  id: number;
  title: string;
  brand: string | null;
  category: string | null;
  imageUrl: string | null;
  externalUrl: string | null;
  sourceName: string | null;
  currency: string | null;
  dealPrice: number;
  originalPrice: number | null;
  discountPercent: number | null;
  dealScore: number | null;
  lastSeenAt: Date | null;
}

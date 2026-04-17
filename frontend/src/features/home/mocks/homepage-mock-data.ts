import type { DealCardModel } from "@/features/deals/types";

const PLACEHOLDER_IMAGES = {
  phone: "https://images.unsplash.com/photo-1583391733956-6c78276477e1?auto=format&fit=crop&w=1200&q=80",
  earbuds: "https://images.unsplash.com/photo-1606220838315-056192d5e927?auto=format&fit=crop&w=1200&q=80",
  laptop: "https://images.unsplash.com/photo-1517336714739-489689fd1ca8?auto=format&fit=crop&w=1200&q=80",
  shoes: "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=1200&q=80",
  watch: "https://images.unsplash.com/photo-1524592094714-0f0654e20314?auto=format&fit=crop&w=1200&q=80",
  appliance: "https://images.unsplash.com/photo-1584269600464-37b1b58a9fe7?auto=format&fit=crop&w=1200&q=80",
  fashion: "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=1200&q=80",
  beauty: "https://images.unsplash.com/photo-1522335789203-aabd1fc54bc9?auto=format&fit=crop&w=1200&q=80"
} as const;

const now = Date.now();

const mkDate = (minutesAgo: number) => new Date(now - minutesAgo * 60 * 1000);

const BASE_DEALS: DealCardModel[] = [
  {
    id: 9001,
    title: "iPhone 15 Pro Max 256GB - uu dai giam gia hot",
    brand: "Apple",
    category: "Dien thoai",
    imageUrl: PLACEHOLDER_IMAGES.phone,
    externalUrl: "https://example.com/deal/iphone-15-promax",
    sourceName: "FPT Shop",
    currency: "VND",
    dealPrice: 29990000,
    originalPrice: 34990000,
    discountPercent: 14.3,
    dealScore: 98,
    lastSeenAt: mkDate(7)
  },
  {
    id: 9002,
    title: "Tai nghe AirPods Pro 2 USB-C chinh hang",
    brand: "Apple",
    category: "Tai nghe",
    imageUrl: PLACEHOLDER_IMAGES.earbuds,
    externalUrl: "https://example.com/deal/airpods-pro2",
    sourceName: "Shopee Mall",
    currency: "VND",
    dealPrice: 4290000,
    originalPrice: 5990000,
    discountPercent: 28.4,
    dealScore: 96,
    lastSeenAt: mkDate(12)
  },
  {
    id: 9003,
    title: "Laptop ASUS TUF Gaming A15 Ryzen 7 RTX 4050",
    brand: "ASUS",
    category: "Laptop",
    imageUrl: PLACEHOLDER_IMAGES.laptop,
    externalUrl: "https://example.com/deal/asus-tuf-a15",
    sourceName: "Lazada",
    currency: "VND",
    dealPrice: 18990000,
    originalPrice: 23990000,
    discountPercent: 20.8,
    dealScore: 94,
    lastSeenAt: mkDate(18)
  },
  {
    id: 9004,
    title: "Giay Nike Air Force 1 '07 full box",
    brand: "Nike",
    category: "Giay dep",
    imageUrl: PLACEHOLDER_IMAGES.shoes,
    externalUrl: "https://example.com/deal/nike-af1",
    sourceName: "Shopee",
    currency: "VND",
    dealPrice: 1399000,
    originalPrice: 1999000,
    discountPercent: 30,
    dealScore: 92,
    lastSeenAt: mkDate(20)
  },
  {
    id: 9005,
    title: "Dong ho Casio G-Shock GA-2100 chong nuoc",
    brand: "Casio",
    category: "Dong ho",
    imageUrl: PLACEHOLDER_IMAGES.watch,
    externalUrl: "https://example.com/deal/gshock-ga2100",
    sourceName: "Tiki",
    currency: "VND",
    dealPrice: 1990000,
    originalPrice: 2990000,
    discountPercent: 33.4,
    dealScore: 90,
    lastSeenAt: mkDate(24)
  },
  {
    id: 9006,
    title: "Noi chien khong dau 6L cong suat 1700W",
    brand: "Philips",
    category: "Gia dung",
    imageUrl: PLACEHOLDER_IMAGES.appliance,
    externalUrl: "https://example.com/deal/air-fryer",
    sourceName: "Lazada Mall",
    currency: "VND",
    dealPrice: 1290000,
    originalPrice: 1890000,
    discountPercent: 31.8,
    dealScore: 89,
    lastSeenAt: mkDate(29)
  },
  {
    id: 9007,
    title: "Ao khoac ni unisex form rong limited edition",
    brand: "Local Brand",
    category: "Thoi trang",
    imageUrl: PLACEHOLDER_IMAGES.fashion,
    externalUrl: "https://example.com/deal/hoodie",
    sourceName: "Shopee Mall",
    currency: "VND",
    dealPrice: 359000,
    originalPrice: 599000,
    discountPercent: 40.1,
    dealScore: 86,
    lastSeenAt: mkDate(36)
  },
  {
    id: 9008,
    title: "Serum Vitamin C 15% lam sang da 30ml",
    brand: "SkinLab",
    category: "Lam dep",
    imageUrl: PLACEHOLDER_IMAGES.beauty,
    externalUrl: "https://example.com/deal/vitamin-c-serum",
    sourceName: "Tiki Trading",
    currency: "VND",
    dealPrice: 219000,
    originalPrice: 349000,
    discountPercent: 37.2,
    dealScore: 84,
    lastSeenAt: mkDate(45)
  }
];

export const MOCK_FEATURED_DEALS: DealCardModel[] = BASE_DEALS.slice(0, 6);

export const MOCK_LATEST_DEALS: DealCardModel[] = BASE_DEALS;

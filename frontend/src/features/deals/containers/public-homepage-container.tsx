"use client";

import Link from "next/link";
import { useRouter } from "next/navigation";

import { EmptyState } from "@/components/states";
import { APP_ROUTES } from "@/config/routes";
import { usePublicAnalyticsSummary } from "@/features/analytics/hooks";
import {
  DealGrid,
  DealsSection,
  HomeHeroSearch
} from "@/features/deals/components";
import { toDealCardFromHottestDeal, toDealCardFromListItem } from "@/features/deals/mappers";
import { buildDealsListingHref, DEFAULT_DEALS_PAGE_SIZE, DEFAULT_DEALS_SORT, DEFAULT_DEALS_DIRECTION } from "@/features/deals/utils";
import { usePublicDeals } from "@/features/deals/hooks";
import { CategoryIconRow, CouponSection, FlashSaleCountdown } from "@/features/home/components";
import { MOCK_FEATURED_DEALS, MOCK_LATEST_DEALS } from "@/features/home/mocks/homepage-mock-data";
import type { DealSearchRequest } from "@/types/requests";

const FEATURED_LIMIT = 6;
const LATEST_LIMIT = 8;

export const PublicHomepageContainer = () => {
  const router = useRouter();

  const featuredRequest = {
    activeOnly: true,
    hottestLimit: FEATURED_LIMIT,
    sourceLimit: 8,
    categoryLimit: 8
  } as const;

  const latestRequest: DealSearchRequest = {
    activeOnly: true,
    page: 0,
    size: LATEST_LIMIT,
    sortBy: DEFAULT_DEALS_SORT,
    direction: DEFAULT_DEALS_DIRECTION
  };

  const featuredQuery = usePublicAnalyticsSummary(featuredRequest);
  const latestQuery = usePublicDeals(latestRequest);

  const featuredDealsFromApi = featuredQuery.data?.hottestDeals.map(toDealCardFromHottestDeal) ?? [];
  const latestDealsFromApi = latestQuery.data?.items.map(toDealCardFromListItem) ?? [];

  const shouldUseFeaturedMock = !featuredDealsFromApi.length && (featuredQuery.isError || !featuredQuery.data);
  const shouldUseLatestMock = !latestDealsFromApi.length && (latestQuery.isError || !latestQuery.data);

  const featuredDeals = shouldUseFeaturedMock ? MOCK_FEATURED_DEALS : featuredDealsFromApi;
  const latestDeals = shouldUseLatestMock ? MOCK_LATEST_DEALS : latestDealsFromApi;

  const categoryItems = [
    { key: "all", label: "Tat ca", icon: "all", href: buildDealsListingHref({ activeOnly: true }), active: true },
    { key: "phone", label: "Dien thoai", icon: "phone", href: buildDealsListingHref({ category: "phone", activeOnly: true }) },
    { key: "laptop", label: "Laptop", icon: "laptop", href: buildDealsListingHref({ category: "laptop", activeOnly: true }) },
    { key: "audio", label: "Tai nghe", icon: "audio", href: buildDealsListingHref({ category: "audio", activeOnly: true }) },
    { key: "watch", label: "Dong ho", icon: "watch", href: buildDealsListingHref({ category: "watch", activeOnly: true }) },
    { key: "fashion", label: "Thoi trang", icon: "fashion", href: buildDealsListingHref({ category: "fashion", activeOnly: true }) },
    { key: "shoes", label: "Giay dep", icon: "shoes", href: buildDealsListingHref({ category: "shoes", activeOnly: true }) },
    { key: "home", label: "Gia dung", icon: "home", href: buildDealsListingHref({ category: "home", activeOnly: true }) },
    { key: "beauty", label: "Lam dep", icon: "beauty", href: buildDealsListingHref({ category: "beauty", activeOnly: true }) },
    { key: "more", label: "Them", icon: "more", href: buildDealsListingHref({ activeOnly: true }) }
  ] as const;

  return (
    <div className="pb-8">
      <HomeHeroSearch
        onSearch={({ keyword }) => {
          router.push(
            buildDealsListingHref({
              keyword,
              activeOnly: true,
              page: 0,
              size: DEFAULT_DEALS_PAGE_SIZE
            })
          );
        }}
      />

      <div className="mt-[18px]">
        <CategoryIconRow items={[...categoryItems]} />
      </div>

      <div className="mt-[28px]">
        <DealsSection
          description="Nhung deal duoc san nhieu nhat hom nay."
          kicker="HOT"
          title="Deal Hot Hom Nay"
          action={
            <Link
              className="inline-flex h-8 items-center rounded-[10px] bg-[#ff6a00] px-3.5 text-[12px] font-bold text-white shadow-[0_6px_14px_rgba(255,106,0,0.18)] transition-colors hover:bg-[#f26500]"
              href={APP_ROUTES.public.deals}
            >
              Xem tat ca deal
            </Link>
          }
        >
          {featuredQuery.isError ? (
            <p className="mb-3 rounded-[10px] border border-[#ffd6bf] bg-[#fff4ec] px-3 py-2 text-[11px] font-semibold text-[#cf4d02]">
              API tam thoi chua san sang, dang hien thi du lieu mau.
            </p>
          ) : null}
          {!featuredDeals.length ? (
            <EmptyState
              description="Tam thoi chua co deal noi bat phu hop."
              title="Chua co deal noi bat"
            />
          ) : null}
          {featuredDeals.length ? (
            <DealGrid deals={featuredDeals} />
          ) : null}
        </DealsSection>
      </div>

      <div className="mt-[28px]">
        <DealsSection
          description="San pham khuyen mai co so luong gioi han."
          kicker="FLASH SALE"
          title="Flash Sale Dang Dien Ra"
          action={
            <div className="flex items-center gap-2">
              <FlashSaleCountdown />
              <Link
                className="inline-flex h-8 items-center rounded-[10px] bg-[#ff6a00] px-3.5 text-[12px] font-bold text-white shadow-[0_6px_14px_rgba(255,106,0,0.18)] transition-colors hover:bg-[#f26500]"
                href={APP_ROUTES.public.deals}
              >
                Mo trang listing
              </Link>
            </div>
          }
        >
          {latestQuery.isError ? (
            <p className="mb-3 rounded-[10px] border border-[#ffd6bf] bg-[#fff4ec] px-3 py-2 text-[11px] font-semibold text-[#cf4d02]">
              Flash sale dang dung du lieu mau do backend chua ket noi.
            </p>
          ) : null}
          {!latestDeals.length ? (
            <EmptyState description="Chua co deal moi trong luc nay." title="Chua co deal moi" />
          ) : null}
          {latestDeals.length ? (
            <DealGrid compact deals={latestDeals} />
          ) : null}
        </DealsSection>
      </div>

      <div className="mt-[28px]">
        <CouponSection />
      </div>
    </div>
  );
};

import { Button } from "@/components/ui";

interface CouponItem {
  id: string;
  brand: string;
  logoText: string;
  logoColor: string;
  discount: string;
  condition: string;
  expiry: string;
}

const COUPONS: CouponItem[] = [
  {
    id: "shopee-50k",
    brand: "Shopee",
    logoText: "S",
    logoColor: "#ff5a2c",
    discount: "50K",
    condition: "Don tu 299K",
    expiry: "30/04/2026"
  },
  {
    id: "lzd-12",
    brand: "Lazada",
    logoText: "L",
    logoColor: "#5c3dff",
    discount: "12%",
    condition: "Toi da 180K",
    expiry: "02/05/2026"
  },
  {
    id: "tiki-30",
    brand: "Tiki",
    logoText: "T",
    logoColor: "#22a2ff",
    discount: "30K",
    condition: "Don tu 199K",
    expiry: "05/05/2026"
  },
  {
    id: "freeship",
    brand: "Freeship",
    logoText: "F",
    logoColor: "#12b76a",
    discount: "FS",
    condition: "Toi da 40K",
    expiry: "30/04/2026"
  }
];

export const CouponSection = () => {
  return (
    <section className="section-surface">
      <div className="section-head mb-[14px]">
        <div className="space-y-1">
          <p className="section-kicker">Voucher moi</p>
          <h2 className="text-[22px] font-extrabold leading-[1.1] text-[#1f1f1f]">Ma Giam Gia Moi Nhat</h2>
          <p className="mt-1 text-[12px] text-[#8a8a8a]">Thu thap ma va ap dung khi thanh toan.</p>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3 md:grid-cols-3 xl:grid-cols-4 xl:[&>*]:max-w-[196px] xl:[&>*]:w-full">
        {COUPONS.map((coupon) => (
          <article
            className="flex min-h-[118px] flex-col rounded-[12px] border border-[#f0e3d9] bg-white p-[14px] shadow-[0_6px_18px_rgba(0,0,0,0.04)]"
            key={coupon.id}
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <span
                  className="inline-flex h-6 w-6 items-center justify-center rounded-[6px] text-[11px] font-extrabold text-white"
                  style={{ background: coupon.logoColor }}
                >
                  {coupon.logoText}
                </span>
                <span className="text-[11px] font-bold text-[#3a3a3a]">{coupon.brand}</span>
              </div>
            </div>

            <p className="mt-[10px] text-[24px] font-extrabold leading-none text-[#ff6a00]">{coupon.discount}</p>
            <p className="mt-1 text-[10px] text-[#8a8a8a]">{coupon.condition}</p>
            <p className="mt-0.5 text-[9px] text-[#b0b0b0]">HSD: {coupon.expiry}</p>
            <Button className="mt-auto h-7 w-full rounded-[8px] px-3 text-[11px] font-bold" size="sm" variant="cta">
              Sao chep
            </Button>
          </article>
        ))}
      </div>
    </section>
  );
};

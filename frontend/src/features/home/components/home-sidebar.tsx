import { Button, Input, Select } from "@/components/ui";

const FEATURED_IMAGE = "https://images.unsplash.com/photo-1695048133142-1a20484d2569?auto=format&fit=crop&w=700&q=80";

export const HomeSidebar = () => {
  return (
    <div className="sticky top-5 w-full space-y-[14px]">
      <section className="rounded-[14px] border border-[#f2e8df] bg-white p-3 shadow-[0_8px_24px_rgba(0,0,0,0.07)]">
        <div className="overflow-hidden rounded-[10px] bg-[#f3f3f3]">
          <img alt="Featured product" className="h-[130px] w-full object-cover" src={FEATURED_IMAGE} />
        </div>
        <h3 className="mt-3 text-[18px] font-bold text-[#242424]">iPhone 15 Pro Max 256GB</h3>
        <p className="mt-1 text-[11px] text-[#8a8a8a]">FPT Shop | 4.8 | 12.5K danh gia</p>
        <div className="mt-2 flex items-end gap-2">
          <span className="text-[38px] font-extrabold leading-none text-[#ff6a00]">29.990.000d</span>
          <span className="pb-1 text-[12px] text-[#a0a0a0] line-through">34.990.000d</span>
        </div>
        <span className="mt-2 inline-flex h-6 items-center rounded-full bg-[#fff2e7] px-2.5 text-[10px] font-bold text-[#f08a3a]">
          Tiet kiem 5.000.000d
        </span>
        <Button className="mt-3 h-[38px] w-full rounded-[10px] text-[13px] font-bold" variant="cta">
          Xem tai FPT Shop
        </Button>
      </section>

      <section className="rounded-[14px] border border-[#f2e8df] bg-white p-[14px] shadow-[0_8px_24px_rgba(0,0,0,0.07)]">
        <h3 className="text-[13px] font-bold text-[#333]">Bieu do gia</h3>
        <p className="mt-1 text-[10px] text-[#9a9a9a]">Lich su thay doi gia trong 30 ngay</p>
        <div className="mt-3 h-[130px] rounded-[10px] border border-[#f0d5c1] bg-[#fff9f5] p-2.5">
          <div className="relative h-full w-full">
            <div className="absolute inset-0 flex flex-col justify-between py-1.5">
              <span className="border-b border-[#f1e2d8]" />
              <span className="border-b border-[#f1e2d8]" />
              <span className="border-b border-[#f1e2d8]" />
              <span className="border-b border-[#f1e2d8]" />
            </div>
            <svg className="absolute inset-0 h-full w-full" fill="none" viewBox="0 0 300 130">
              <path d="M10 98 C46 88, 74 62, 108 66 C140 70, 164 42, 196 48 C224 54, 246 78, 274 70 C286 66, 294 52, 296 40" stroke="#ff6a00" strokeLinecap="round" strokeWidth="3" />
              <circle cx="10" cy="98" fill="#ff6a00" r="3.5" />
              <circle cx="108" cy="66" fill="#ff6a00" r="3.5" />
              <circle cx="196" cy="48" fill="#ff6a00" r="3.5" />
              <circle cx="274" cy="70" fill="#ff6a00" r="3.5" />
              <circle cx="296" cy="40" fill="#ff3b30" r="4" />
            </svg>
            <span className="absolute right-2 top-2 rounded-full bg-[#e9f8ee] px-2 py-0.5 text-[9px] font-bold text-[#1f8c4b]">
              Gia thap nhat
            </span>
          </div>
        </div>
      </section>

      <section className="rounded-[14px] border border-[#f2e8df] bg-white p-[14px] shadow-[0_8px_24px_rgba(0,0,0,0.07)]">
        <h3 className="text-[14px] font-bold text-[#222]">Tao alert san deal</h3>
        <div className="mt-3 space-y-3">
          <label className="block">
            <span className="mb-1.5 block text-[10px] font-semibold text-[#6f6f6f]">Tu khoa</span>
            <Input className="h-[34px] rounded-[8px] border-[#e8e2dc] bg-white px-2.5 text-[11px]" placeholder="Vi du: iPhone, AirPods" />
          </label>
          <label className="block">
            <span className="mb-1.5 block text-[10px] font-semibold text-[#6f6f6f]">Danh muc</span>
            <Select className="h-[34px] rounded-[8px] border-[#e8e2dc] bg-white px-2.5 text-[11px]">
              <option>Chon danh muc</option>
              <option>Dien thoai</option>
              <option>Do gia dung</option>
              <option>Thoi trang</option>
            </Select>
          </label>
          <div>
            <span className="mb-1.5 block text-[10px] font-semibold text-[#6f6f6f]">Khoang gia</span>
            <div className="grid grid-cols-2 gap-2">
              <Input className="h-[34px] rounded-[8px] border-[#e8e2dc] bg-white px-2.5 text-[11px]" placeholder="Tu gia" />
              <Input className="h-[34px] rounded-[8px] border-[#e8e2dc] bg-white px-2.5 text-[11px]" placeholder="Den gia" />
            </div>
          </div>
          <label className="block">
            <span className="mb-1.5 block text-[10px] font-semibold text-[#6f6f6f]">Nguon</span>
            <Select className="h-[34px] rounded-[8px] border-[#e8e2dc] bg-white px-2.5 text-[11px]">
              <option>Tat ca nguon</option>
              <option>Shopee</option>
              <option>Lazada</option>
              <option>Tiki</option>
            </Select>
          </label>
          <label className="mt-[10px] inline-flex items-center gap-2 text-[10px] text-[#666]">
            <input className="h-3.5 w-3.5 rounded border-[#d9d9d9]" type="checkbox" />
            Nhan thong bao qua email
          </label>
          <Button className="mt-3 h-[34px] w-full rounded-[8px] text-[12px] font-bold shadow-[0_10px_18px_-12px_rgba(255,106,0,0.9)]" variant="cta">
            Tao alert
          </Button>
        </div>
      </section>
    </div>
  );
};

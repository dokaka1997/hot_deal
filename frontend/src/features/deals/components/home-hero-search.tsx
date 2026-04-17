"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

const heroSearchSchema = z.object({
  keyword: z.string().trim().max(120)
});

type HeroSearchValues = z.infer<typeof heroSearchSchema>;

interface HomeHeroSearchProps {
  onSearch: (values: { keyword?: string }) => void;
}

const HERO_IMAGE = "https://images.unsplash.com/photo-1583391733956-6c78276477e1?auto=format&fit=crop&w=1100&q=80";
const KEYWORD_CHIPS = ["iPhone", "Tai nghe", "Shopee", "Lazada", "Dien tu", "Thoi trang"] as const;

export const HomeHeroSearch = ({ onSearch }: HomeHeroSearchProps) => {
  const form = useForm<HeroSearchValues>({
    resolver: zodResolver(heroSearchSchema),
    defaultValues: {
      keyword: ""
    }
  });

  const submit = (values: HeroSearchValues) => {
    onSearch({
      keyword: values.keyword?.trim() || undefined
    });
  };

  return (
    <section
      className="relative isolate min-h-[270px] overflow-hidden rounded-[16px] bg-[radial-gradient(circle_at_80%_22%,rgba(255,255,255,0.24),transparent_38%),radial-gradient(circle_at_16%_82%,rgba(255,255,255,0.14),transparent_48%),linear-gradient(135deg,#ff5a00_0%,#ff6a00_38%,#ff8214_72%,#ffa336_100%)] px-5 py-6 shadow-[0_16px_34px_rgba(255,106,0,0.24)] lg:h-[270px] lg:px-[30px] lg:py-[28px]"
    >
      <span className="pointer-events-none absolute -left-7 top-9 h-[128px] w-[128px] rounded-full bg-white/10 blur-[1px]" />
      <span className="pointer-events-none absolute left-[230px] top-[20px] h-[82px] w-[82px] rounded-full bg-white/[0.1]" />
      <span className="pointer-events-none absolute right-[246px] bottom-[16px] hidden h-[96px] w-[96px] rounded-full bg-white/[0.11] lg:block" />
      <span className="pointer-events-none absolute right-[282px] top-[-20px] hidden rotate-[-16deg] text-[176px] font-black leading-none text-white/[0.08] lg:block">
        %
      </span>
      <span className="pointer-events-none absolute right-[304px] top-[26px] hidden rounded-full bg-[#ff4728] px-3 py-1 text-[11px] font-extrabold uppercase text-white shadow-[0_12px_18px_-10px_rgba(171,36,0,0.9)] lg:block">
        Sale
      </span>
      <span className="pointer-events-none absolute right-[374px] top-[108px] hidden rounded-full bg-white/20 px-2.5 py-1 text-[11px] font-extrabold text-white lg:block">
        %
      </span>
      <span className="pointer-events-none absolute right-[328px] bottom-[24px] hidden rounded-full bg-[#ff3f25] px-3 py-1 text-[11px] font-extrabold uppercase text-white shadow-[0_10px_16px_-10px_rgba(117,20,1,0.9)] lg:block">
        HOT
      </span>
      <div className="relative z-10 max-w-[360px]">
        <div className="flex flex-wrap gap-2">
          <span className="inline-flex h-6 items-center rounded-full border border-white/20 bg-white/[0.16] px-3 text-[11px] font-bold text-white">
            Deal moi
          </span>
          <span className="inline-flex h-6 items-center rounded-full border border-white/20 bg-white/[0.16] px-3 text-[11px] font-bold text-white">
            Gia soc
          </span>
        </div>
        <h1 className="mt-[14px] text-[36px] font-extrabold uppercase leading-[0.94] tracking-[-0.02em] text-white sm:text-[44px] lg:text-[56px]">
          San Deal Hoi
          <br />
          Moi Ngay
        </h1>
        <p className="mt-[14px] max-w-[320px] text-[14px] font-medium leading-[1.6] text-white/85">
          Tong hop khuyen mai chon loc, so sanh nhanh va chot don voi gia tot hon moi ngay.
        </p>

        <div className="mt-[18px] h-[46px] w-full max-w-[335px] rounded-[12px] border border-white/25 bg-white/[0.17] p-1.5 backdrop-blur-[10px]">
          <form className="flex h-full items-center gap-1.5" onSubmit={form.handleSubmit(submit)}>
            <input
              className="h-[34px] flex-1 rounded-[10px] border border-white/30 bg-white px-3 text-[12px] text-[#222] outline-none placeholder:text-[#9a9a9a]"
              placeholder="Tim san pham, thuong hieu..."
              {...form.register("keyword")}
            />
            <button
              className="h-[34px] w-[90px] rounded-[10px] bg-[#ff7a12] text-[12px] font-bold text-white transition-colors hover:bg-[#f26500]"
              type="submit"
            >
              Tim kiem
            </button>
          </form>
        </div>

        <div className="mt-3 flex flex-wrap gap-2">
          {KEYWORD_CHIPS.map((chip) => (
            <button
              className="inline-flex h-6 items-center rounded-full border border-white/20 bg-white/[0.12] px-2.5 text-[11px] font-semibold text-white"
              key={chip}
              onClick={() => onSearch({ keyword: chip })}
              type="button"
            >
              {chip}
            </button>
          ))}
        </div>
      </div>

      <div className="absolute right-[26px] top-[32px] hidden h-[160px] w-[272px] overflow-hidden rounded-[16px] border border-white/25 bg-[linear-gradient(180deg,rgba(255,255,255,0.2),rgba(255,255,255,0.07))] shadow-[0_20px_34px_rgba(0,0,0,0.16)] backdrop-blur-[10px] lg:block">
        <div className="absolute inset-0 bg-[linear-gradient(160deg,rgba(255,255,255,0.24),rgba(255,255,255,0.02))]" />
        <img alt="Promo visual" className="absolute inset-0 h-full w-full object-cover opacity-65" src={HERO_IMAGE} />
        <div className="relative h-full p-4">
          <span className="inline-flex h-6 items-center rounded-full border border-white/35 bg-white/20 px-2.5 text-[11px] font-bold text-white">
            Promo 45% OFF
          </span>
          <span className="absolute bottom-4 left-4 inline-flex h-[30px] items-center rounded-[10px] bg-white px-3 text-[12px] font-extrabold text-[#ff6a00] shadow-[0_10px_18px_-14px_rgba(15,23,42,0.7)]">
            Tu 1.290.000d
          </span>
          <span className="absolute bottom-4 right-4 inline-flex h-[24px] items-center rounded-full bg-[#ff4529] px-2.5 text-[11px] font-extrabold uppercase text-white">
            SALE
          </span>
        </div>
      </div>

      <div className="pointer-events-none absolute right-[48px] top-[82px] hidden h-[176px] w-[220px] overflow-hidden rounded-[16px] border border-white/20 bg-white/15 shadow-[0_20px_32px_rgba(0,0,0,0.14)] lg:block">
        <img alt="Deal preview" className="h-full w-full object-cover opacity-80" src={HERO_IMAGE} />
      </div>
    </section>
  );
};

import Link from "next/link";

import { APP_ROUTES } from "@/config/routes";

const policyLinks = [
  { label: "Chính sách bảo mật", href: "#" },
  { label: "Điều khoản dịch vụ", href: "#" },
  { label: "Quy chế hoạt động", href: "#" }
] as const;

export const AppFooter = () => {
  return (
    <footer className="border-t border-[#ece2d9] bg-white">
      <div className="mx-auto w-full max-w-[1440px] px-6 py-8">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div>
            <p className="text-base font-extrabold text-[#ff6a00]">Hot Deal</p>
            <p className="text-sm text-[#7b7b7b]">
              Nền tảng tổng hợp ưu đãi, mã giảm giá và flash sale từ các sàn thương mại điện tử.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <Link className="text-sm font-semibold text-[#555] hover:text-[#ff6a00]" href={APP_ROUTES.public.home}>
              Trang chủ
            </Link>
            <Link className="text-sm font-semibold text-[#555] hover:text-[#ff6a00]" href={APP_ROUTES.public.deals}>
              Danh sách ưu đãi
            </Link>
            <Link className="text-sm font-semibold text-[#555] hover:text-[#ff6a00]" href={APP_ROUTES.public.login}>
              Đăng nhập
            </Link>
            <Link className="text-sm font-semibold text-[#555] hover:text-[#ff6a00]" href={APP_ROUTES.public.register}>
              Đăng ký
            </Link>
          </div>
        </div>

        <div className="mt-5 flex flex-col gap-2 border-t border-[#f2e7de] pt-4 text-xs text-[#8a8a8a] md:flex-row md:items-center md:justify-between">
          <p>© 2026 Hot Deal. Tất cả quyền được bảo lưu.</p>
          <div className="flex flex-wrap items-center gap-3">
            {policyLinks.map((link) => (
              <a className="hover:text-[#ff6a00]" href={link.href} key={link.label}>
                {link.label}
              </a>
            ))}
          </div>
        </div>
      </div>
    </footer>
  );
};

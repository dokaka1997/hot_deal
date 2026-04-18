"use client";

import { Button, Input, Select } from "@/components/ui";
import type { DealFilterState } from "@/features/deals/types";
import { DEAL_SORT_PRESETS, getDealSortPreset, resolveDealSortPreset } from "@/features/deals/utils";

export interface DealFilterOption {
  value: string;
  label: string;
  count?: number;
}

interface DealListFiltersProps {
  values: DealFilterState;
  categoryOptions: DealFilterOption[];
  sourceOptions: DealFilterOption[];
  onChange: (nextValues: Partial<DealFilterState>) => void;
  onReset: () => void;
  isUpdating?: boolean;
}

const parsePriceValue = (value: string): number | undefined => {
  if (!value.trim()) {
    return undefined;
  }

  const parsed = Number(value);
  if (!Number.isFinite(parsed) || parsed < 0) {
    return undefined;
  }

  return parsed;
};

const toOptionLabel = (option: DealFilterOption): string => {
  if (option.count === undefined) {
    return option.label;
  }

  return `${option.label} (${option.count})`;
};

export const DealListFilters = ({
  values,
  categoryOptions,
  sourceOptions,
  onChange,
  onReset,
  isUpdating = false
}: DealListFiltersProps) => {
  const minPrice = parsePriceValue(values.minPrice);
  const maxPrice = parsePriceValue(values.maxPrice);
  const hasPriceRangeError = minPrice !== undefined && maxPrice !== undefined && maxPrice < minPrice;
  const activeSortPreset = resolveDealSortPreset(values.sortBy, values.direction);

  return (
    <section className="tool-surface space-y-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="space-y-1">
          <p className="section-kicker">Tìm và lọc</p>
          <h1 className="text-heading-lg text-foreground">Danh sách ưu đãi công khai</h1>
          <p className="text-body-sm text-muted-foreground">
            Tìm theo tên sản phẩm, lọc theo danh mục/nguồn/giá và sắp xếp để chọn ưu đãi nhanh.
          </p>
        </div>

        <Button onClick={onReset} type="button" variant="secondary">
          Đặt lại bộ lọc
        </Button>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-5">
        <label className="space-y-1 xl:col-span-2">
          <span className="text-sm font-semibold text-foreground">Từ khóa</span>
          <Input
            onChange={(event) => onChange({ keyword: event.target.value })}
            onKeyDown={(event) => {
              if (event.key === "Enter") {
                event.preventDefault();
                onChange({ keyword: event.currentTarget.value });
              }
            }}
            placeholder="Ví dụ: iPhone, AirPods, Nike..."
            value={values.keyword}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Sắp xếp</span>
          <Select
            onChange={(event) => {
              const preset = getDealSortPreset(event.target.value as typeof activeSortPreset);
              onChange({ sortBy: preset.sortBy, direction: preset.direction });
            }}
            value={activeSortPreset}
          >
            {DEAL_SORT_PRESETS.map((preset) => (
              <option key={preset.key} value={preset.key}>
                {preset.label}
              </option>
            ))}
          </Select>
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Danh mục</span>
          <Select onChange={(event) => onChange({ category: event.target.value })} value={values.category}>
            <option value="">Tất cả danh mục</option>
            {categoryOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {toOptionLabel(option)}
              </option>
            ))}
          </Select>
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Nguồn / Shop</span>
          <Select onChange={(event) => onChange({ source: event.target.value })} value={values.source}>
            <option value="">Tất cả nguồn</option>
            {sourceOptions.map((option) => (
              <option key={option.value} value={option.value}>
                {toOptionLabel(option)}
              </option>
            ))}
          </Select>
        </label>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-2 xl:grid-cols-5">
        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Giá từ</span>
          <Input
            min={0}
            onChange={(event) => onChange({ minPrice: event.target.value })}
            placeholder="0"
            step="0.01"
            type="number"
            value={values.minPrice}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Giá đến</span>
          <Input
            min={0}
            onChange={(event) => onChange({ maxPrice: event.target.value })}
            placeholder="50000000"
            step="0.01"
            type="number"
            value={values.maxPrice}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Mã giảm giá</span>
          <Select
            onChange={(event) => onChange({ hasCoupon: event.target.value as DealFilterState["hasCoupon"] })}
            value={values.hasCoupon}
          >
            <option value="all">Tất cả ưu đãi</option>
            <option value="withCoupon">Chỉ ưu đãi có mã</option>
            <option value="withoutCoupon">Không có mã</option>
          </Select>
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Số lượng/trang</span>
          <Select className="w-full" onChange={(event) => onChange({ size: Number(event.target.value) })} value={String(values.size)}>
            {[12, 20, 40].map((sizeOption) => (
              <option key={sizeOption} value={sizeOption}>
                {sizeOption}
              </option>
            ))}
          </Select>
        </label>

        <div className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Trạng thái</span>
          <label className="flex h-10 items-center gap-2 rounded-md border border-border bg-surface px-3">
            <input
              checked={values.activeOnly}
              className="h-4 w-4 rounded-sm border-border text-brand focus:ring-brand"
              onChange={(event) => onChange({ activeOnly: event.target.checked })}
              type="checkbox"
            />
            <span className="text-sm text-foreground">Chỉ ưu đãi đang hoạt động</span>
          </label>
        </div>
      </div>

      <div className="flex flex-wrap items-center justify-between gap-3">
        {hasPriceRangeError ? (
          <p className="text-sm text-danger">Giá đến phải lớn hơn hoặc bằng giá từ.</p>
        ) : (
          <p className="text-sm text-muted-foreground">Thay đổi bộ lọc sẽ tự động về trang 1 và cập nhật URL.</p>
        )}

        {isUpdating ? <p className="text-sm text-muted-foreground">Đang cập nhật kết quả...</p> : null}
      </div>
    </section>
  );
};

"use client";

import { Button, Input, Select } from "@/components/ui";
import type { DealFilterState } from "@/features/deals/types";
import { DEAL_SORT_FIELDS } from "@/types/requests";
import { SORT_DIRECTIONS } from "@/types/contracts/backend";

interface DealListFiltersProps {
  values: DealFilterState;
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

export const DealListFilters = ({ values, onChange, onReset, isUpdating = false }: DealListFiltersProps) => {
  const minPrice = parsePriceValue(values.minPrice);
  const maxPrice = parsePriceValue(values.maxPrice);
  const hasPriceRangeError =
    minPrice !== undefined && maxPrice !== undefined && maxPrice < minPrice;

  return (
    <section className="tool-surface space-y-4">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div className="space-y-1">
          <p className="section-kicker">Bo loc deal</p>
          <h1 className="text-heading-lg text-foreground">Tim deal theo nhu cau</h1>
          <p className="text-body-sm text-muted-foreground">
            Loc theo tu khoa, danh muc, nguon va khoang gia de chot deal nhanh hon.
          </p>
        </div>

        <Button onClick={onReset} type="button" variant="secondary">
          Dat lai bo loc
        </Button>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-3">
        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Tu khoa</span>
          <Input
            onChange={(event) => onChange({ keyword: event.target.value })}
            placeholder="Tim theo ten, thuong hieu, danh muc"
            value={values.keyword}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Danh muc</span>
          <Input
            onChange={(event) => onChange({ category: event.target.value })}
            placeholder="Vi du: electronics"
            value={values.category}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Nguon</span>
          <Input
            onChange={(event) => onChange({ source: event.target.value })}
            placeholder="Vi du: mock_deals"
            value={values.source}
          />
        </label>
      </div>

      <div className="grid grid-cols-1 gap-3 md:grid-cols-4">
        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Gia tu</span>
          <Input
            min={0}
            onChange={(event) => onChange({ minPrice: event.target.value })}
            placeholder="0.00"
            step="0.01"
            type="number"
            value={values.minPrice}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Gia den</span>
          <Input
            min={0}
            onChange={(event) => onChange({ maxPrice: event.target.value })}
            placeholder="1000.00"
            step="0.01"
            type="number"
            value={values.maxPrice}
          />
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Sap xep theo</span>
          <Select onChange={(event) => onChange({ sortBy: event.target.value as DealFilterState["sortBy"] })} value={values.sortBy}>
            {DEAL_SORT_FIELDS.map((field) => (
              <option key={field} value={field}>
                {field}
              </option>
            ))}
          </Select>
        </label>

        <label className="space-y-1">
          <span className="text-sm font-semibold text-foreground">Thu tu</span>
          <Select
            onChange={(event) => onChange({ direction: event.target.value as DealFilterState["direction"] })}
            value={values.direction}
          >
            {SORT_DIRECTIONS.map((direction) => (
              <option key={direction} value={direction}>
                {direction}
              </option>
            ))}
          </Select>
        </label>
      </div>

      <div className="flex flex-wrap items-center gap-4">
        <label className="inline-flex items-center gap-2 text-sm text-foreground">
          <input
            checked={values.activeOnly}
            className="h-4 w-4 rounded-sm border-border text-brand focus:ring-brand"
            onChange={(event) => onChange({ activeOnly: event.target.checked })}
            type="checkbox"
          />
          Chi hien deal dang hoat dong
        </label>

        <label className="inline-flex items-center gap-2">
          <span className="text-sm font-semibold text-foreground">So luong/trang</span>
          <Select className="w-28" onChange={(event) => onChange({ size: Number(event.target.value) })} value={String(values.size)}>
            {[12, 20, 40].map((sizeOption) => (
              <option key={sizeOption} value={sizeOption}>
                {sizeOption}
              </option>
            ))}
          </Select>
        </label>

        {isUpdating ? <p className="text-sm text-muted-foreground">Dang cap nhat ket qua...</p> : null}
      </div>

      {hasPriceRangeError ? (
        <p className="text-sm text-danger">Gia den phai lon hon hoac bang gia tu.</p>
      ) : null}
    </section>
  );
};

import { Button } from "@/components/ui";
import type { AppliedDealFilterChip, AppliedDealFilterKey } from "@/features/deals/utils";

interface DealAppliedFiltersProps {
  chips: AppliedDealFilterChip[];
  onRemove: (key: AppliedDealFilterKey) => void;
  onClearAll: () => void;
}

export const DealAppliedFilters = ({
  chips,
  onRemove,
  onClearAll
}: DealAppliedFiltersProps) => {
  if (!chips.length) {
    return null;
  }

  return (
    <div className="flex flex-wrap items-center gap-2">
      <p className="text-xs font-semibold text-[#8a8a8a]">Đang áp dụng:</p>

      {chips.map((chip) => (
        <button
          className="inline-flex h-8 items-center gap-2 rounded-md border border-[#f0d8c5] bg-[#fff7f1] px-2.5 text-xs font-semibold text-[#9a4a18] transition-colors hover:bg-[#ffefe2]"
          key={`${chip.key}-${chip.label}`}
          onClick={() => onRemove(chip.key)}
          type="button"
        >
          <span>{chip.label}</span>
          <span className="text-[11px] leading-none">x</span>
        </button>
      ))}

      <Button className="h-8" onClick={onClearAll} size="sm" variant="ghost">
        Xóa tất cả
      </Button>
    </div>
  );
};

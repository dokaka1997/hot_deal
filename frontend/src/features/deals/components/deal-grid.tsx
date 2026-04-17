import { DealCard } from "@/features/deals/components/deal-card";
import type { DealCardModel } from "@/features/deals/types";

interface DealGridProps {
  deals: DealCardModel[];
  compact?: boolean;
}

export const DealGrid = ({ deals, compact = false }: DealGridProps) => {
  return (
    <div
      className={
        compact
          ? "grid grid-cols-2 gap-3 md:grid-cols-3 xl:grid-cols-4"
          : "grid grid-cols-2 gap-4 md:grid-cols-3 xl:grid-cols-4"
      }
    >
      {deals.map((deal) => (
        <DealCard compact={compact} deal={deal} key={deal.id} />
      ))}
    </div>
  );
};

import { DiscountBadge } from "@/components/commerce/discount-badge";
import { cn, formatCurrency } from "@/lib/utils";

interface PriceDisplayProps {
  dealPrice: number;
  originalPrice?: number | null;
  discountPercent?: number | null;
  currency?: string | null;
  className?: string;
  priceClassName?: string;
}

const formatPriceValue = (value: number, currency?: string | null): string => {
  if (!currency) {
    return value.toFixed(2);
  }

  try {
    return formatCurrency(value, currency);
  } catch {
    return value.toFixed(2);
  }
};

export const PriceDisplay = ({
  dealPrice,
  originalPrice = null,
  discountPercent = null,
  currency = null,
  className,
  priceClassName
}: PriceDisplayProps) => {
  return (
    <div className={cn("flex flex-wrap items-center gap-2.5", className)}>
      <span className={cn("text-[1.75rem] font-extrabold leading-none text-[#ff4d00]", priceClassName)}>
        {formatPriceValue(dealPrice, currency)}
      </span>
      {originalPrice !== null ? (
        <span className="text-[13px] font-medium text-muted-foreground line-through">
          {formatPriceValue(originalPrice, currency)}
        </span>
      ) : null}
      <DiscountBadge compact percent={discountPercent} />
    </div>
  );
};

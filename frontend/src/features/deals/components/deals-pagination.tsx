import { Button } from "@/components/ui";

interface DealsPaginationProps {
  page: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
  onPageChange: (nextPage: number) => void;
}

export const DealsPagination = ({
  page,
  totalPages,
  isFirst,
  isLast,
  onPageChange
}: DealsPaginationProps) => {
  const safeTotalPages = Math.max(totalPages, 1);

  return (
    <div className="tool-surface flex flex-wrap items-center justify-between gap-3">
      <p className="text-sm font-medium text-muted-foreground">
        Trang {page + 1} / {safeTotalPages}
      </p>

      <div className="flex items-center gap-2">
        <Button disabled={isFirst} onClick={() => onPageChange(page - 1)} variant="secondary">
          Truoc
        </Button>
        <Button disabled={isLast} onClick={() => onPageChange(page + 1)} variant="cta">
          Tiep
        </Button>
      </div>
    </div>
  );
};

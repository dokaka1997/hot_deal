import { Button } from "@/components/ui";

interface DealsPaginationProps {
  page: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
  onPageChange: (nextPage: number) => void;
}

const getVisiblePages = (page: number, totalPages: number): number[] => {
  if (totalPages <= 0) {
    return [];
  }

  const pages = new Set<number>([0, totalPages - 1]);
  const start = Math.max(0, page - 2);
  const end = Math.min(totalPages - 1, page + 2);

  for (let current = start; current <= end; current += 1) {
    pages.add(current);
  }

  return Array.from(pages).sort((left, right) => left - right);
};

export const DealsPagination = ({
  page,
  totalPages,
  isFirst,
  isLast,
  onPageChange
}: DealsPaginationProps) => {
  const safeTotalPages = Math.max(totalPages, 1);
  const visiblePages = getVisiblePages(page, safeTotalPages);

  return (
    <div className="tool-surface flex flex-wrap items-center justify-between gap-3">
      <p className="text-sm font-medium text-muted-foreground">
        Trang {Math.min(page + 1, safeTotalPages)} / {safeTotalPages}
      </p>

      <div className="flex flex-wrap items-center gap-2">
        <Button disabled={isFirst} onClick={() => onPageChange(Math.max(page - 1, 0))} size="sm" variant="secondary">
          Trước
        </Button>

        {visiblePages.map((pageIndex, index) => {
          const previousPage = visiblePages[index - 1];
          const showEllipsis = previousPage !== undefined && pageIndex - previousPage > 1;

          return (
            <div className="flex items-center gap-2" key={`pagination-item-${pageIndex}`}>
              {showEllipsis ? (
                <span className="px-1 text-sm font-semibold text-muted-foreground">...</span>
              ) : null}
              <button
                className={
                  pageIndex === page
                    ? "inline-flex h-8 min-w-8 items-center justify-center rounded-md border border-[#ff6a00] bg-[#ff6a00] px-2 text-xs font-bold text-white shadow-[0_8px_16px_-12px_rgba(255,106,0,0.95)]"
                    : "inline-flex h-8 min-w-8 items-center justify-center rounded-md border border-border bg-surface px-2 text-xs font-semibold text-foreground transition-colors hover:border-[#ff6a00] hover:text-[#ff6a00]"
                }
                onClick={() => onPageChange(pageIndex)}
                type="button"
              >
                {pageIndex + 1}
              </button>
            </div>
          );
        })}

        <Button
          disabled={isLast}
          onClick={() => onPageChange(Math.min(page + 1, safeTotalPages - 1))}
          size="sm"
          variant="cta"
        >
          Tiếp
        </Button>
      </div>
    </div>
  );
};

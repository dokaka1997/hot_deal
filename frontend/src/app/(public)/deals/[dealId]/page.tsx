import { notFound } from "next/navigation";

import { PageContainer } from "@/components/layout/page-container";
import { DealDetailContainer } from "@/features/deals/containers";
import { parseDealIdFromParam } from "@/features/deals/utils";

interface DealDetailPageProps {
  params:
    | {
        dealId: string;
      }
    | Promise<{
        dealId: string;
      }>;
}

export default async function DealDetailPage({ params }: DealDetailPageProps) {
  const resolvedParams = await Promise.resolve(params);
  const dealId = parseDealIdFromParam(resolvedParams.dealId);
  if (!dealId) {
    notFound();
  }

  return (
    <PageContainer>
      <DealDetailContainer dealId={dealId} />
    </PageContainer>
  );
}

import { Card, CardDescription, CardTitle } from "@/components/ui";

interface PagePlaceholderProps {
  title: string;
  description: string;
}

export const PagePlaceholder = ({ title, description }: PagePlaceholderProps) => {
  return (
    <Card className="border-brand/25 bg-brand-soft/50">
      <CardTitle className="text-xl">{title}</CardTitle>
      <CardDescription>{description}</CardDescription>
    </Card>
  );
};

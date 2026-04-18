"use client";

import { useMemo } from "react";
import {
  CartesianGrid,
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";

import { formatCurrency } from "@/lib/utils";
import type { PriceHistoryPoint } from "@/types/domain";

interface DealPriceHistoryChartProps {
  points: PriceHistoryPoint[];
  currency: string;
}

interface ChartPoint {
  capturedAt: number;
  dealPrice: number;
  originalPrice: number | null;
}

const axisDateFormatter = new Intl.DateTimeFormat("vi-VN", {
  month: "short",
  day: "2-digit"
});

const tooltipDateFormatter = new Intl.DateTimeFormat("vi-VN", {
  year: "numeric",
  month: "short",
  day: "2-digit",
  hour: "2-digit",
  minute: "2-digit"
});

const formatPriceValue = (value: number, currency: string): string => {
  try {
    return formatCurrency(value, currency);
  } catch {
    return value.toFixed(2);
  }
};

const toSafeNumber = (value: unknown): number => {
  const parsed = typeof value === "number" ? value : Number(value);
  return Number.isFinite(parsed) ? parsed : 0;
};

export const DealPriceHistoryChart = ({ points, currency }: DealPriceHistoryChartProps) => {
  const chartData = useMemo<ChartPoint[]>(() => {
    return [...points]
      .sort((left, right) => left.capturedAt.getTime() - right.capturedAt.getTime())
      .map((point) => ({
        capturedAt: point.capturedAt.getTime(),
        dealPrice: point.dealPrice,
        originalPrice: point.originalPrice
      }));
  }, [points]);

  return (
    <div className="h-80 w-full">
      <ResponsiveContainer height="100%" width="100%">
        <LineChart data={chartData} margin={{ top: 8, right: 8, bottom: 8, left: 8 }}>
          <CartesianGrid stroke="#f1d3bc" strokeDasharray="3 3" />
          <XAxis
            dataKey="capturedAt"
            minTickGap={24}
            tick={{ fill: "#6b7280", fontSize: 12 }}
            tickFormatter={(value) => axisDateFormatter.format(new Date(toSafeNumber(value)))}
            type="number"
          />
          <YAxis
            tick={{ fill: "#6b7280", fontSize: 12 }}
            tickFormatter={(value) => formatPriceValue(toSafeNumber(value), currency)}
            width={90}
          />
          <Tooltip
            contentStyle={{
              border: "1px solid #f1d3bc",
              backgroundColor: "#fffaf5",
              borderRadius: "6px"
            }}
            formatter={(value, key) => [
              formatPriceValue(toSafeNumber(value), currency),
              String(key) === "dealPrice" ? "Giá ưu đãi" : "Giá gốc"
            ]}
            labelFormatter={(value) => tooltipDateFormatter.format(new Date(toSafeNumber(value)))}
          />
          <Line
            dataKey="dealPrice"
            dot={false}
            name="Giá ưu đãi"
            stroke="#eb6e1a"
            strokeWidth={2}
            type="monotone"
          />
          <Line
            connectNulls
            dataKey="originalPrice"
            dot={false}
            name="Giá gốc"
            stroke="#7f8793"
            strokeDasharray="5 5"
            strokeWidth={2}
            type="monotone"
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};

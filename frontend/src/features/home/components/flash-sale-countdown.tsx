"use client";

import { useEffect, useMemo, useState } from "react";

const pad = (value: number): string => value.toString().padStart(2, "0");

const toParts = (timeMs: number) => {
  const totalSeconds = Math.max(0, Math.floor(timeMs / 1000));
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const seconds = totalSeconds % 60;

  return {
    hours: pad(hours),
    minutes: pad(minutes),
    seconds: pad(seconds)
  };
};

export const FlashSaleCountdown = () => {
  const targetTimestamp = useMemo(() => Date.now() + 1000 * 60 * 60 * 4 + 1000 * 60 * 17 + 1000 * 32, []);
  const [remainingMs, setRemainingMs] = useState(() => Math.max(0, targetTimestamp - Date.now()));

  useEffect(() => {
    const timer = setInterval(() => {
      setRemainingMs(Math.max(0, targetTimestamp - Date.now()));
    }, 1000);

    return () => clearInterval(timer);
  }, [targetTimestamp]);

  const parts = toParts(remainingMs);

  return (
    <div className="flex items-center gap-1.5">
      <span className="inline-flex h-[18px] items-center rounded-full bg-[#fff2e7] px-2 text-[10px] font-bold text-[#ff6a00]">
        Ket thuc sau
      </span>
      <span className="inline-flex h-[22px] w-6 items-center justify-center rounded-[6px] bg-[#ff6a00] text-[11px] font-extrabold text-white">
        {parts.hours}
      </span>
      <span className="text-[11px] font-extrabold text-[#ff6a00]">:</span>
      <span className="inline-flex h-[22px] w-6 items-center justify-center rounded-[6px] bg-[#ff6a00] text-[11px] font-extrabold text-white">
        {parts.minutes}
      </span>
      <span className="text-[11px] font-extrabold text-[#ff6a00]">:</span>
      <span className="inline-flex h-[22px] w-6 items-center justify-center rounded-[6px] bg-[#ff6a00] text-[11px] font-extrabold text-white">
        {parts.seconds}
      </span>
    </div>
  );
};

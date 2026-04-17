export const parseDealIdFromParam = (value: string): number | null => {
  const normalized = value.trim();
  if (!normalized) {
    return null;
  }

  const exactNumeric = Number(normalized);
  if (Number.isInteger(exactNumeric) && exactNumeric > 0) {
    return exactNumeric;
  }

  const match = normalized.match(/^(\d+)(?:-|$)/);
  if (!match || !match[1]) {
    return null;
  }

  const parsed = Number(match[1]);
  if (!Number.isInteger(parsed) || parsed <= 0) {
    return null;
  }

  return parsed;
};

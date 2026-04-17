export const APP_ROUTES = {
  public: {
    home: "/",
    deals: "/deals",
    dealDetail: (dealId: string | number) => `/deals/${dealId}`
  },
  admin: {
    dashboard: "/admin"
  }
} as const;

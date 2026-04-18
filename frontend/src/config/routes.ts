export const APP_ROUTES = {
  public: {
    home: "/",
    deals: "/deals",
    login: "/login",
    register: "/register",
    dealDetail: (dealId: string | number) => `/deals/${dealId}`
  },
  admin: {
    dashboard: "/admin"
  }
} as const;

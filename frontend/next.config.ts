import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  reactStrictMode: true,
  async rewrites() {
    return [
      { source: "/login", destination: "/stitch-login.html" },
      { source: "/register", destination: "/stitch-register.html" }
    ];
  }
};

export default nextConfig;

import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      // TODO: Why did we add this? It's not used anywhere and it's messing up auth requests (/api/auth uses port 3000 not 8080)
      // {
      //   source: "/api/:path((?!auth).*?)",
      //   destination: "http://localhost:8080/api/:path*",
      // },
    ];
  },
};

export default nextConfig;

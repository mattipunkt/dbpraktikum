import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("/products", "routes/products.tsx"),
  route("/product/:productId", "routes/product-detail.tsx"),

  // route("/trolls", "routes/trolls.tsx"),
] satisfies RouteConfig;

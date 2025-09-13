import type { Route } from "./+types/home";
import ProductList from "~/components/productlist/productlist_top100";
import { useState, useEffect } from "react";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Top-Produkte" },
    {
      name: "description",
      content: "product lists shows all your favorite products!",
    },
  ];
}

export default function Produkte() {
  return <ProductList categoryPath={null} />;
}

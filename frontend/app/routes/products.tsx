import type { Route } from "./+types/home";
import ProductList from "~/components/productlist/productlist";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "Produktliste" },
    { name: "description", content: "product lists shows all your favorite products!" },
  ];
}

export default function Produkte() {
  return <div>
    <h1 className="text-3xl font-bold mb-4">Produkte</h1>
    <ProductList/>
  </div>
}
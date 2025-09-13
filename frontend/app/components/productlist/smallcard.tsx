import type { Product } from "./productlist";

export default function SmallProductCard({ product }: { product: Product }) {
  return (
    <a href={`/product/${product.id}`} className="w-full">
      <div className="border-2 rounded-2xl p-4 shadow-md hover:shadow-xl transition-shadow duration-300">
        <img
          src={product.bild}
          alt={product.titel}
          width={100}
          height={100}
          className="mx-auto mb-3 rounded-2xl shadow-lg "
        />
        <div className="text-center font-semibold">{product.titel}</div>
        <div className="text-center text-sm text-gray-500">{product.typ}</div>
      </div>
    </a>
  );
}
